package view;

import dataObjects.Document;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

        webEngine.load(document.getUrl());

        layout.getChildren().addAll(
                webView
        );

        stage.setScene(scene);
        stage.showAndWait();
    }
}
