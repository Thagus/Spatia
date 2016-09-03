import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by Thagus on 02/09/16.
 */
public class View {
    public void start(Stage window) {
        window.setTitle("Spatia");

        BorderPane layout = new BorderPane();
        Scene scene = new Scene(layout, 1280, 720);

        createMenus(layout);

        window.setScene(scene);
        window.show();
    }

    private void createMenus(BorderPane layout){
        MenuBar menuBar = new MenuBar();

        //File menu
        Menu menuFile = new Menu("_File");

        MenuItem importDoc = new MenuItem("Import document...");
        //importDoc.setOnAction(controllerImportDocument);
        menuFile.getItems().add(importDoc);


        menuBar.getMenus().addAll(menuFile);
        layout.setTop(menuBar);

    }
}
