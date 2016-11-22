package view;

import dataObjects.Document;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.*;
import javafx.scene.control.*;

/**
 * Created by Thagus on 05/09/16.
 */
public class DocumentView {
    public DocumentView(Document document){
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);

        VBox layout = new VBox();
        layout.setSpacing(5);
        Scene scene = new Scene(layout, 1200, 720);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        Label timeLabel = new Label();
        timeLabel.setVisible(false);

        final ProgressBar progress = new ProgressBar();
        final LongProperty startTime = new SimpleLongProperty();
        final LongProperty endTime = new SimpleLongProperty();

        // updating progress bar using binding
        progress.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(newValue == Worker.State.RUNNING){
                        startTime.setValue(System.currentTimeMillis());
                    }
                    else if (newValue == Worker.State.SUCCEEDED) {
                        // hide progress bar then page is ready
                        progress.setVisible(false);
                        endTime.setValue(System.currentTimeMillis());
                        timeLabel.setText("Load time: " + (endTime.getValue()-startTime.getValue()) + " ms");
                        timeLabel.setVisible(true);
                    }
                }
        );

        webEngine.load(document.getUrl());

        layout.getChildren().addAll(
                webView, progress, timeLabel
        );

        stage.setScene(scene);
        stage.showAndWait();
    }
}
