package view;

import dataObjects.Document;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
        Scene scene = new Scene(layout);

        Font headerFont = Font.font("Arial", FontWeight.BOLD, 14);
        Font textFont = Font.font("Arial", FontWeight.NORMAL, 12);


        Label idLabel = new Label("ID: " + document.getUrl());
        idLabel.setFont(headerFont);

        Separator idSeparator = new Separator();

        Label titleLabel = new Label("ID: " + document.getTitle());
        titleLabel.setFont(headerFont);

        Separator titleSeparator = new Separator();

        Text text = new Text();
        text.setFont(textFont);
        text.setText(document.getText());
        text.setWrappingWidth(400);



        layout.getChildren().addAll(
                idLabel, idSeparator,
                titleLabel, titleSeparator,
                text
        );

        stage.setScene(scene);
        stage.showAndWait();
    }
}
