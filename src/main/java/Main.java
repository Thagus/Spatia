import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.ModelDatabase;
import utilities.LanguageDetector;
import utilities.TermExtractor;
import view.View;

import java.sql.SQLException;

/**
 * Created by Thagus on 01/09/16.
 */
public class Main extends Application{
    private Stage window;
    private ModelDatabase db;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        db = ModelDatabase.instance();

        //Consume the close request in order to handle it properly
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        View view = new View();
        Scene searchScene = view.createScene();

        //Initialize language detector
        LanguageDetector.initialize();
        //Initialize term extractor dictionary and stopwords
        TermExtractor.initialize();

        //Start application in search mode
        window.setScene(searchScene);
        window.show();
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
