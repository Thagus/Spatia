package view;

import dataObjects.Document;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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


        Label idLabel = new Label("ID: " + document.getIdDoc());
        idLabel.setFont(headerFont);

        Separator idSeparator = new Separator();

        Text text = new Text();
        text.setFont(textFont);
        text.setText(document.getText());
        text.setWrappingWidth(400);



        layout.getChildren().addAll(
                idLabel, idSeparator,
                text
        );

        stage.setScene(scene);
        stage.showAndWait();
    }
}
