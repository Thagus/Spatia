package view;

import dataObjects.Document;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        layout.setSpacing(10);
        Scene scene = new Scene(layout);

        //,classification,citations
        Label idLabel = new Label("ID");
        Text idText = new Text();
        idText.setText(String.valueOf(document.getIdDoc()));

        Label titleLabel = new Label("Title");
        Text titleText = new Text();
        titleText.setText(document.getTitle());

        Label journalLabel = new Label("Journal");
        Text journalText = new Text();
        journalText.setText(document.getJournal());

        Label libraryNotesLabel = new Label("Library Notes");
        Text libraryNotesText = new Text();
        libraryNotesText.setText(document.getLibraryNotes());

        Label authorsLabel = new Label("Authors");
        Text authorsText = new Text();
        authorsText.setText(document.getAuthors());

        Label abstractLabel = new Label("Abstract");
        Text abstractText = new Text();
        abstractText.setText(document.getAbstractText());
        abstractText.setWrappingWidth(400);
        abstractText.setTextAlignment(TextAlignment.JUSTIFY);

        Label keywordsLabel = new Label("Keywords");
        Text keywordsText = new Text();
        keywordsText.setText(document.getKeywords());

        Label classificationLabel = new Label("Classification");
        Text classificationText = new Text();
        classificationText.setText(document.getClassification());

        layout.getChildren().addAll(
                idLabel, idText,
                titleLabel, titleText,
                journalLabel, journalText,
                libraryNotesLabel, libraryNotesText,
                authorsLabel, authorsText,
                abstractLabel, abstractText,
                keywordsLabel, keywordsText,
                classificationLabel, classificationText
        );

        stage.setScene(scene);
        stage.showAndWait();
    }
}
