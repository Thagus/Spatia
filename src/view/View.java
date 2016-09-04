package view;

import controller.ControllerImportDocument;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Created by Thagus on 02/09/16.
 */
public class View {
    private ControllerImportDocument controllerImportDocument;

    public void start(Stage window) {
        window.setTitle("Spatia");

        BorderPane layout = new BorderPane();
        Scene scene = new Scene(layout, 1280, 720);

        //Create controllers
        controllerImportDocument = new ControllerImportDocument(window);

        createMenus(layout);
        createSearchBox(layout);

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

    private void createSearchBox(BorderPane layout){
        TextField searchBox = new TextField();
        searchBox.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        searchBox.setMaxSize(450, 45);


        layout.setCenter(searchBox);
    }
}
