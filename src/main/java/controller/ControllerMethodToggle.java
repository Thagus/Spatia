package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import model.ModelDatabase;
import model.ModelOperations;

/**
 * Created by Thagus on 25/09/16.
 */
public class ControllerMethodToggle implements ChangeListener<Toggle> {
    private ModelOperations modOps;

    public ControllerMethodToggle(){
        this.modOps = ModelDatabase.instance().opModel;
    }
    @Override
    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if(newValue!=null) {
            String userData = newValue.getUserData().toString();
            switch (userData) {
                //Weight methods
                case "IDF":
                    modOps.setWeightMethod(userData);
                    break;
                //Similarity methods
                case "Dot product":
                case "Cosine":
                    modOps.setSimilarityMethod(userData);
                    break;
                default:
            }
        }
    }
}
