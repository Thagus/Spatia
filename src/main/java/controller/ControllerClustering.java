package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import model.ModelDatabase;
import model.clustering.HierarchicalClustering;

import java.sql.SQLException;

/**
 * Created by Thagus on 24/10/16.
 */
public class ControllerClustering implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        MenuItem node = (MenuItem) event.getSource();
        String userData = (String) node.getUserData();

        if(userData.equals("create")){
            try {
                HierarchicalClustering hierarchicalClustering = new HierarchicalClustering(ModelDatabase.instance().getCon());
                hierarchicalClustering.beginClustering();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(userData.equals("toggle")){
            ModelDatabase.instance().opModel.toggleClustering();
        }
    }
}
