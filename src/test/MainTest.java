package test;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Created by Thagus on 11/09/16.
 */
public class MainTest extends Application{
    private Stage window;
    private TestsDatabase db;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        db = TestsDatabase.instance();

        //Consume the close request in order to handle it properly
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        ViewTest view = new ViewTest();
        view.start(window);
    }

    /**
     * Ask the user permission to close the program
     */
    private void closeProgram(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Exit");
        alert.setHeaderText(null);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES){
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            window.close();
            System.exit(0);
        }
    }
}
