package test;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Thagus on 11/09/16.
 */
public class ViewTest {
    private ControllerTests controllerTests;

    public void start(Stage window){
        window.setTitle("Spatia tests");
        VBox layout = new VBox();
        layout.setSpacing(5);

        controllerTests = new ControllerTests(window);

        createMenus(layout);

        Scene scene = new Scene(layout, 1200, 720);
        window.setScene(scene);
        window.show();
    }

    public void createMenus(VBox layout){
        MenuBar menuBar = new MenuBar();

        //File menu
        Menu menuFile = new Menu("_File");

        //Import document
        MenuItem importTests = new MenuItem("Import tests...");
        importTests.setOnAction(controllerTests);
        importTests.setUserData("import");

        //Separator
        SeparatorMenuItem separator = new SeparatorMenuItem();

        //Begin tests
        MenuItem beginTests = new MenuItem("Begin tests");
        beginTests.setOnAction(controllerTests);
        beginTests.setUserData("begin");

        //Add menu items to the menu
        menuFile.getItems().addAll(importTests, separator, beginTests);


        menuBar.getMenus().addAll(menuFile);
        layout.getChildren().add(menuBar);
    }

    public void createSideList(){

    }


    public void createCharts(){
        //Create chart

        //Create Axis
    }

}
