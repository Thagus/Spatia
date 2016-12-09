package view;

import controller.ControllerHarvest;
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

/**
 * Created by Thagus on 02/09/16.
 */
public class View {
    private ControllerHarvest controllerHarvest;
    private ControllerSearch controllerSearch;

    /**
     * Creates the scene for the search
     */
    public Scene createScene() {
        VBox layout = new VBox();
        layout.setSpacing(5);

        //Create controllers
        this.controllerHarvest = new ControllerHarvest();
        this.controllerSearch = new ControllerSearch();

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
        MenuItem harvest = new MenuItem("Harvest");
        harvest.setOnAction(controllerHarvest);
        menuFile.getItems().add(harvest);

        menuBar.getMenus().addAll(menuFile);
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

        TableColumn<Document, Integer> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setMinWidth(500);
        titleCol.setPrefWidth(500);

        TableColumn<Document, Integer> urlCol = new TableColumn<>("URL");
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlCol.setMinWidth(500);
        urlCol.setPrefWidth(500);

        TableColumn<Document, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(new PropertyValueFactory<>("language"));
        langCol.setMaxWidth(25);

        TableColumn<Document, Integer> simCol = new TableColumn<>("Similarity");
        simCol.setCellValueFactory(new PropertyValueFactory<>("similarity"));
        simCol.setMinWidth(125);
        simCol.setStyle("-fx-alignment: CENTER;");       //Center values from column

        tableDocuments.getColumns().addAll(titleCol, urlCol, langCol, simCol);


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
