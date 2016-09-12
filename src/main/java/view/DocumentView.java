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

        //,classification,citations
        Label idLabel = new Label("ID: " + document.getIdDoc());
        idLabel.setFont(headerFont);

        Separator idSeparator = new Separator();

        Label titleLabel = new Label("Title");
        titleLabel.setFont(headerFont);
        Text titleText = new Text();
        titleText.setFont(textFont);
        titleText.setText(document.getTitle());
        titleText.setWrappingWidth(400);

        Separator titleSeparator = new Separator();

        Label journalLabel = new Label("Journal");
        journalLabel.setFont(headerFont);
        Text journalText = new Text();
        journalText.setFont(textFont);
        journalText.setText(document.getJournal());

        Separator journalSeparator = new Separator();

        Label libraryNotesLabel = new Label("Library Notes");
        libraryNotesLabel.setFont(headerFont);
        Text libraryNotesText = new Text();
        libraryNotesText.setFont(textFont);
        libraryNotesText.setText(document.getLibraryNotes());

        Separator librarySeparator = new Separator();

        Label authorsLabel = new Label("Authors");
        authorsLabel.setFont(headerFont);
        Text authorsText = new Text();
        authorsText.setFont(textFont);
        authorsText.setText(document.getAuthors());
        authorsText.setWrappingWidth(400);

        Separator authorsSeparator = new Separator();

        Label abstractLabel = new Label("Abstract");
        abstractLabel.setFont(headerFont);
        Text abstractText = new Text();
        abstractText.setFont(textFont);
        abstractText.setText(document.getAbstractText());
        abstractText.setWrappingWidth(400);
        abstractText.setTextAlignment(TextAlignment.JUSTIFY);

        Separator abstractSeparator = new Separator();

        Label keywordsLabel = new Label("Keywords");
        keywordsLabel.setFont(headerFont);
        Text keywordsText = new Text();
        keywordsText.setFont(textFont);
        keywordsText.setText(document.getKeywords());
        keywordsText.setWrappingWidth(400);

        Separator keywordsSeparator = new Separator();

        Label classificationLabel = new Label("Classification");
        classificationLabel.setFont(headerFont);
        Text classificationText = new Text();
        classificationText.setFont(textFont);
        classificationText.setText(document.getClassification());

        layout.getChildren().addAll(
                idLabel, idSeparator,
                titleLabel, titleText, titleSeparator,
                journalLabel, journalText, journalSeparator,
                libraryNotesLabel, libraryNotesText, librarySeparator,
                authorsLabel, authorsText, authorsSeparator,
                abstractLabel, abstractText, abstractSeparator,
                keywordsLabel, keywordsText, keywordsSeparator,
                classificationLabel, classificationText
        );

        stage.setScene(scene);
        stage.showAndWait();
    }
}
