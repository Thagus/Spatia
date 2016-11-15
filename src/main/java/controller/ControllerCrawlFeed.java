package controller;

import crawling.CrawlerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.ModelDatabase;

import java.util.ArrayList;

/**
 * Created by Thagus on 03/09/16.
 */
public class ControllerCrawlFeed implements EventHandler<ActionEvent> {
    private final ModelDatabase db;
    private final CrawlerController crawlerController;

    public ControllerCrawlFeed(){
        this.db = ModelDatabase.instance();
        this.crawlerController = new CrawlerController();
    }

    /**
     * Handle the Crawl option from the menu by requesting the start of the crawling process
     */
    @Override
    public void handle(ActionEvent event) {
        ArrayList<String> seeds = new ArrayList<>();

        seeds.add("http://udlap.mx/inicio.aspx");

        try {
            crawlerController.startCrawling(seeds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Requests the calculation of IDFs and weights
        db.opModel.recalculateWeights();

        //A message to alert the user about the number of read documents
        Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + db.opDocuments.countDocuments() + " documents");
        countInfo.setTitle("Successful crawl!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();
    }
}
