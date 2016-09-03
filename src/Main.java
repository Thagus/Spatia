import javafx.application.Application;
import javafx.stage.Stage;

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
    }
}
