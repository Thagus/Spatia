package view;

import controller.ControllerImportDocument;
import controller.ControllerTermSearch;
import dataObjects.DocumentTerm;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Created by Thagus on 02/09/16.
 */
public class View {
    private ControllerImportDocument controllerImportDocument;

    public void start(Stage window) {
        window.setTitle("Spatia v0.00");

        VBox layout = new VBox();
        layout.setSpacing(5);
        //layout.setPadding(new Insets(10, 7, 5, 7));

        Scene scene = new Scene(layout, 1200, 720);

        //Create controllers
        controllerImportDocument = new ControllerImportDocument(window);

        //Create containers v0.00
        createMenus(layout);
        createTermSearch(layout);

        window.setScene(scene);
        window.show();
    }

    /**
     * Create the menu bar
     * @param layout The layout that will contain the menu
     */
    private void createMenus(VBox layout){
        MenuBar menuBar = new MenuBar();

        //File menu
        Menu menuFile = new Menu("_File");

        //Import document
        MenuItem importDoc = new MenuItem("Import document...");
        importDoc.setOnAction(controllerImportDocument);
        menuFile.getItems().add(importDoc);


        menuBar.getMenus().addAll(menuFile);
        layout.getChildren().add(menuBar);

    }

    private void createSearchBox(){

    }

    private void createSearchResultsContainer(){

    }

    //Spatia v0.00 fuctionality

    /**
     * Functionality for Spatia v0.00
     * Create the search bar, search button, results table, and IDF label
     *
     * @param layout The layout that will contain the search components
     */
    private void createTermSearch(VBox layout){
        HBox searchLayout = new HBox();
        searchLayout.setSpacing(10);
        VBox.setMargin(searchLayout, new Insets(10, 5, 10, 5));

        /***********************************************************
         *       Define components                                 *
         ***********************************************************/
        TextField searchBox = new TextField();
        searchBox.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        searchBox.setMinSize(720, 40);
        searchBox.setMaxSize(720, 45);

        Button searchButton = new Button("Search term");
        searchButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        searchButton.setMinSize(10, 45);

        Separator separator = new Separator();
        separator.setPadding(new Insets(0, 10, 0, 10));
        separator.setVisible(false);

        Label tfidfLabel = new Label();
        tfidfLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        tfidfLabel.setAlignment(Pos.CENTER_RIGHT);


        //TableView for displaying document results
        TableView<DocumentTerm> tableTerms = new TableView<>();
        tableTerms.setEditable(false);
        VBox.setMargin(tableTerms, new Insets(0, 5, 5, 5));
        VBox.setVgrow(tableTerms, Priority.ALWAYS);         //Resize table to fit window

        TableColumn<DocumentTerm, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idDoc"));
        idCol.setMinWidth(45);
        idCol.setPrefWidth(45);
        idCol.setStyle("-fx-alignment: CENTER;");       //Center values from column

        TableColumn<DocumentTerm, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setMinWidth(500);

        TableColumn<DocumentTerm, String> journalCol = new TableColumn<>("Journal");
        journalCol.setCellValueFactory(new PropertyValueFactory<>("journal"));
        journalCol.setMinWidth(150);

        TableColumn<DocumentTerm, Integer> tfCol = new TableColumn<>("TF");
        tfCol.setCellValueFactory(new PropertyValueFactory<>("tf"));
        tfCol.setMinWidth(45);
        tfCol.setPrefWidth(45);
        tfCol.setStyle("-fx-alignment: CENTER;");
        tfCol.setSortType(TableColumn.SortType.DESCENDING);

        TableColumn<DocumentTerm, Double> tfidfCol = new TableColumn<>("TFIDF");
        tfidfCol.setCellValueFactory(new PropertyValueFactory<>("tfidf"));
        tfidfCol.setMinWidth(125);
        tfidfCol.setStyle("-fx-alignment: CENTER;");

        tableTerms.getColumns().addAll(idCol, titleCol, journalCol, tfCol, tfidfCol);


        /***********************************************************
         *       Set on action                                     *
         ***********************************************************/
        ControllerTermSearch controllerTermSearch = new ControllerTermSearch();

        //Handle Enter on searchBox to start search, through ControllerTermSearch
        searchBox.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                controllerTermSearch.handleSearch(tableTerms, tfidfLabel, tfCol, searchBox);
            }
        });

        //Handle button press to start search, through ControllerTermSearch
        searchButton.setOnAction(event -> controllerTermSearch.handleSearch(tableTerms, tfidfLabel, tfCol, searchBox));

        //Handle click on a row to open Document view, through ControllerSearchTerm
        tableTerms.setOnMouseClicked(controllerTermSearch::handleTableClick);

        /***********************************************************
         *       Add to layouts                                    *
         ***********************************************************/
        //Add elements to search bar layout
        searchLayout.getChildren().addAll(searchBox, searchButton, separator, tfidfLabel);
        //Add elements to global layout
        layout.getChildren().addAll(searchLayout, tableTerms);
    }
}
