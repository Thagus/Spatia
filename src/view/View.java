package view;

import controller.ControllerImportDocument;
import dataObjects.Document;
import dataObjects.DocumentTerm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.ModelDatabase;
import utilities.Tokenizer;

import javax.swing.*;
import java.sql.Date;

/**
 * Created by Thagus on 02/09/16.
 */
public class View {
    private ControllerImportDocument controllerImportDocument;

    public void start(Stage window) {
        window.setTitle("Spatia v0.00");

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10, 7, 5, 7));

        Scene scene = new Scene(layout, 1280, 720);

        //Create controllers
        controllerImportDocument = new ControllerImportDocument(window);

        //Create containers v0.00
        createMenus(layout);
        createTermSearch(layout);

        window.setScene(scene);
        window.show();
    }

    private void createMenus(BorderPane layout){
        MenuBar menuBar = new MenuBar();

        //File menu
        Menu menuFile = new Menu("_File");

        MenuItem importDoc = new MenuItem("Import document...");
        importDoc.setOnAction(controllerImportDocument);
        menuFile.getItems().add(importDoc);


        menuBar.getMenus().addAll(menuFile);
        layout.setTop(menuBar);

    }

    private void createSearchBox(){

    }

    private void createSearchResultsContainer(){

    }

    //Spatia v0.00
    private void createTermSearch(BorderPane layout){
        HBox topMenu = new HBox();
        topMenu.setSpacing(10);
        BorderPane.setMargin(topMenu, new Insets(10, 5, 10, 5));


        //BorderStrokeStyle boderStyle = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, null);
        //BorderStroke borderStroke = new BorderStroke(Color.LIGHTGRAY, boderStyle, CornerRadii.EMPTY, new BorderWidths(2), new Insets(2));
        //topMenu.setBorder(new Border(borderStroke));
        //topMenu.setPadding(new Insets(10, 0, 10, 0));

        //Define components
        TextField searchBox = new TextField();
        searchBox.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        searchBox.setMinSize(720, 40);
        searchBox.setMaxSize(720, 45);

        Button searchButton = new Button("Search term");
        searchButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        searchButton.setMinSize(10, 45);

        Separator separator = new Separator();
        separator.setPadding(new Insets(0, 10, 0, 10));

        Label tfidfLabel = new Label();
        tfidfLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        tfidfLabel.setAlignment(Pos.CENTER_RIGHT);


        TableView<DocumentTerm> tableTerms = new TableView<>();
        tableTerms.setEditable(false);

        TableColumn<DocumentTerm, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idDoc"));
        idCol.setMinWidth(45);
        idCol.setPrefWidth(45);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<DocumentTerm, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setMinWidth(400);

        TableColumn<DocumentTerm, String> journalCol = new TableColumn<>("Journal");
        journalCol.setCellValueFactory(new PropertyValueFactory<>("journal"));
        journalCol.setMinWidth(150);

        TableColumn<DocumentTerm, Date> tfCol = new TableColumn<>("TF");
        tfCol.setCellValueFactory(new PropertyValueFactory<>("tf"));
        tfCol.setMinWidth(45);
        tfCol.setPrefWidth(45);
        tfCol.setStyle("-fx-alignment: CENTER;");
        tfCol.setSortType(TableColumn.SortType.DESCENDING);

        TableColumn<DocumentTerm, Date> tfidfCol = new TableColumn<>("TFIDF");
        tfidfCol.setCellValueFactory(new PropertyValueFactory<>("tfidf"));
        tfidfCol.setMinWidth(125);
        tfidfCol.setStyle("-fx-alignment: CENTER;");

        tableTerms.getColumns().addAll(idCol, titleCol, journalCol, tfCol, tfidfCol);



        //Set on action
        searchBox.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                //Tokenize string, and get first word
                String[] terms = Tokenizer.tokenizeString(searchBox.getText());
                String term = "";

                for(String t : terms){
                    if(t.length()>=3){  //Filter terms
                        term = t;
                        break;
                    }
                }

                if(term.length()>2){
                    tableTerms.setItems( ModelDatabase.instance().opModel.termSearch(term));
                    tableTerms.getSortOrder().setAll(tfCol);
                    tableTerms.sort();
                    tfidfLabel.setText("IDF: " + ModelDatabase.instance().opIDF.getTermIDF(term));
                }
                else {
                    JOptionPane.showMessageDialog(null, "The given term is too short!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchButton.setOnAction(event -> {
            //Tokenize string, and get first word
            String[] terms = Tokenizer.tokenizeString(searchBox.getText());
            String term = "";

            for(String t : terms){
                if(t.length()>=3){  //Filter terms
                    term = t;
                    break;
                }
            }

            if(term.length()>2){
                tableTerms.setItems( ModelDatabase.instance().opModel.termSearch(term));
                tableTerms.getSortOrder().setAll(tfCol);
                tableTerms.sort();
                tfidfLabel.setText("IDF: " + ModelDatabase.instance().opIDF.getTermIDF(term));
            }
            else {
                JOptionPane.showMessageDialog(null, "The given term is too short!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        /*tableTerms.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    Node node = ((Node) event.getTarget()).getParent();
                    TableRow row;
                    if (node instanceof TableRow) {
                        row = (TableRow) node;
                    } else {
                        // clicking on text part
                        row = (TableRow) node.getParent();
                    }
                    DocumentTerm documentTerm = (DocumentTerm) row.getItem();
                    System.out.println(documentTerm.getTitle());
                }
            }
        });*/

        tableTerms.setOnMouseClicked(event -> {
            //Single click on row with primary button
            if(event.getClickCount() == 1 && event.getButton()== MouseButton.PRIMARY){
                Node node = ((Node) event.getTarget()).getParent();
                TableRow row;
                if (node instanceof TableRow) {
                    row = (TableRow) node;
                } else {
                    //clicking on text part
                    row = (TableRow) node.getParent();
                }
                DocumentTerm documentTerm = (DocumentTerm) row.getItem();

                //Get document
                Document document = ModelDatabase.instance().opDocuments.getDocument(documentTerm.getIdDoc());

                //Create document view
                new DocumentView(document);
            }
        });


        //Add to layouts
        topMenu.getChildren().addAll(searchBox, searchButton, separator, tfidfLabel);

        layout.setTop(topMenu);
        layout.setCenter(tableTerms);
    }
}
