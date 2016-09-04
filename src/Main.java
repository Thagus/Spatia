import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.ModelDatabase;
import view.View;

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

        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        View view = new View();
        view.start(window);
    }

    private void closeProgram(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Exit");
        alert.setHeaderText(null);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES){
            db.close();
            window.close();
        }
    }
}
