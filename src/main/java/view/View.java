package view;

import controller.ControllerImportDocument;
import controller.ControllerSceneSwitcher;
import controller.ControllerSearch;
import dataObjects.Document;
import javafx.geometry.Insets;
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
    private ControllerSearch controllerSearch;
    private ControllerSceneSwitcher controllerSceneSwitcher;

    /**
     * Creates the scene for the search
     * @param window The window where the scene will run
     * @param controllerSceneSwitcher The controller that handles scene switching
     */
    public Scene createScene(Stage window, ControllerSceneSwitcher controllerSceneSwitcher) {
        VBox layout = new VBox();
        layout.setSpacing(5);
        //layout.setPadding(new Insets(10, 7, 5, 7));

        //Create controllers
        this.controllerSceneSwitcher = controllerSceneSwitcher;
        controllerImportDocument = new ControllerImportDocument(window);
        controllerSearch = new ControllerSearch();

        //Create menu bar
        createMenus(layout);
        //Create search
        createSearch(layout);

        return new Scene(layout, 1200, 720);
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


        //Tests menu
        Menu testsMenu = new Menu("_Tests");
        MenuItem openTests = new MenuItem("Open tests");
        openTests.setUserData("openTests");
        openTests.setOnAction(controllerSceneSwitcher);
        testsMenu.getItems().add(openTests);

        menuBar.getMenus().addAll(menuFile, testsMenu);
        layout.getChildren().add(menuBar);

    }

    /**
     * Create the search box, search button, and table for search results
     * @param layout main layout to place the created containers
     */
    private void createSearch(VBox layout){
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

        Button searchButton = new Button("Search");
        searchButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        searchButton.setMinSize(10, 45);

        Separator separator = new Separator();
        separator.setPadding(new Insets(0, 10, 0, 10));
        separator.setVisible(false);


        //TableView for displaying document results
        TableView<Document> tableDocuments = new TableView<>();
        tableDocuments.setEditable(false);
        VBox.setMargin(tableDocuments, new Insets(0, 5, 5, 5));
        VBox.setVgrow(tableDocuments, Priority.ALWAYS);         //Resize table to fit window

        TableColumn<Document, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idDoc"));
        idCol.setMinWidth(45);
        idCol.setPrefWidth(45);
        idCol.setStyle("-fx-alignment: CENTER;");       //Center values from column

        TableColumn<Document, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setMinWidth(500);

        TableColumn<Document, String> journalCol = new TableColumn<>("Journal");
        journalCol.setCellValueFactory(new PropertyValueFactory<>("journal"));
        journalCol.setMinWidth(150);

        TableColumn<Document, Integer> simCol = new TableColumn<>("Similarity");
        simCol.setCellValueFactory(new PropertyValueFactory<>("similarity"));
        simCol.setMinWidth(125);

        tableDocuments.getColumns().addAll(idCol, titleCol, journalCol, simCol);


        /***********************************************************
         *       Set on action                                     *
         ***********************************************************/
        //Handle Enter on searchBox to start search, through ControllerTermSearch
        searchBox.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                controllerSearch.handleSearch(tableDocuments, searchBox);
            }
        });

        //Handle button press to start search, through ControllerTermSearch
        searchButton.setOnAction(event -> controllerSearch.handleSearch(tableDocuments, searchBox));

        //Handle click on a row to open Document view, through ControllerSearchTerm
        tableDocuments.setOnMouseClicked(controllerSearch::handleTableClick);

        /***********************************************************
         *       Add to layouts                                    *
         ***********************************************************/
        //Add elements to search bar layout
        searchLayout.getChildren().addAll(searchBox, searchButton);
        //Add elements to global layout
        layout.getChildren().addAll(searchLayout, tableDocuments);
    }
}
