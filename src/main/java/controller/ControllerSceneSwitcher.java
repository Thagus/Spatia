package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Created by Thagus on 15/09/16.
 */
public class ControllerSceneSwitcher implements EventHandler<ActionEvent> {
    private Stage window;
    private Scene searchScene, testScene;

    public ControllerSceneSwitcher(Stage window){
        this.window = window;
    }

    public void setSearchScene(Scene searchScene) {
        this.searchScene = searchScene;
    }

    public void setTestScene(Scene testScene) {
        this.testScene = testScene;
    }

    @Override
    public void handle(ActionEvent event) {
        MenuItem node = (MenuItem) event.getSource();
        String userData = (String) node.getUserData();

        if(userData.equals("openTests")){
            switchToTests();
        }
        else if(userData.equals("openSearch")){
            switchToSearch();
        }
    }

    private void switchToSearch(){
        window.setTitle("Spatia v0.02");

        window.setScene(searchScene);
        window.show();
    }

    private void switchToTests(){
        window.setTitle("Spatia tests");

        window.setScene(testScene);
        window.show();
    }

    public void start() {
        switchToSearch();
    }
}
