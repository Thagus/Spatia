package controller;

import harvester.DCHarvester;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.ModelDatabase;

import java.util.ArrayList;

/**
 * Created by Thagus on 03/09/16.
 */
public class ControllerHarvest implements EventHandler<ActionEvent> {
    private final ModelDatabase db;
    private final DCHarvester dcHarvester;

    public ControllerHarvest(){
        this.db = ModelDatabase.instance();
        this.dcHarvester = new DCHarvester();
    }

    /**
     * Handle the Crawl option from the menu by requesting the start of the crawling process
     */
    @Override
    public void handle(ActionEvent event) {
        ArrayList<String> seeds = new ArrayList<>();

        seeds.add("http://red.mnstate.edu/do/oai/");
        seeds.add("http://www.la-press.com/oai.php");
        seeds.add("http://ricabib.cab.cnea.gov.ar/cgi/oai2");

        try {
            for(String site : seeds)
                dcHarvester.harvest(site);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Requests the calculation of IDFs and weights
        db.opModel.recalculateWeights();

        //A message to alert the user about the number of read documents
        Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + db.opDocuments.countDocuments() + " documents");
        countInfo.setTitle("Successful harvest!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();
    }
}
