package test;

import dataObjects.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.ModelDatabase;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Thagus on 11/09/16.
 */
public class ControllerTests implements EventHandler<ActionEvent>{
    private final Stage window;
    private final TestsDatabase db;
    private ArrayList<QueryObject> queries;

    public ControllerTests(Stage window){
        db = TestsDatabase.instance();
        this.window = window;
    }

    /**
     * Handles a menu item click from teh view
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        MenuItem node = (MenuItem) event.getSource();
        String userData = (String) node.getUserData();

        if(userData.equals("import")) {

            DirectoryChooser fileChooser = new DirectoryChooser();
            File file = fileChooser.showDialog(window);

            if (file != null) {
                importTests(file);
            }
        }
        else if(userData.equals("begin")){
            beginTests();
        }
    }

    /**
     * Imports the tests files (query and qrels)
     * @param folder The folder containing the files
     */
    public void importTests(File folder){
        String folderPath = folder.getAbsolutePath();
        //Query documents paths
        String queryTextPath = folderPath + "\\query.text";
        String qrelsTextPath = folderPath + "\\qrels.text";

        File queryTextFile = new File(queryTextPath);
        File qrelsTextFile = new File(qrelsTextPath);

        final ArrayList<QueryObject> queriesRead = new ArrayList<>();

        //Read query.text file
        try (Stream<String> stream = Files.lines(queryTextFile.toPath())) {
            final int[] currentType = new int[1];

            stream.forEach(line -> {
                if(line.startsWith(".I")){
                    //The format is "I. " + id
                    int id = Integer.parseInt(line.substring(3));
                    queriesRead.add(new QueryObject(id));
                } else if(line.startsWith(".W")){
                    currentType[0] = 0;
                } else if(line.startsWith(".")) {   //Ignore all other tags
                    currentType[0] = 1;
                } else {    //When the line is not a header
                    switch (currentType[0]) {
                        case 0:     //Case for .W - query
                            queriesRead.get(queriesRead.size()-1).appendQuery(line.trim());
                            currentType[0] = -1;
                            break;
                        case 1:     //Case for ignored tags
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "The file doesn't follow the expected structure!");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write to database the queries
        for(QueryObject queryObject : queriesRead){
            db.addQuery(queryObject.getQid(), queryObject.getQuery());
        }

        Pattern numberPattern = Pattern.compile("\\s+");
        //Read qrels.text file and write to database its contents
        try (Stream<String> stream = Files.lines(qrelsTextFile.toPath())) {
            stream.forEach(line -> {
                String[] numbers = numberPattern.split(line);
                db.addRelevant(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obstans the queries to execute and start the tests
     */
    public void beginTests(){
        //Load queries
        queries = db.getQueries();

        ModelDatabase mainDatabase = ModelDatabase.instance();
        //Execute them
        for(QueryObject query : queries){
            ArrayList<Integer> retrieved = query.getDocumentRetrieved();
            mainDatabase.opModel.evaluateQuery(query.getQuery()).forEach(i ->
                    //For each retrieved document, extract its id and store it within the query
                    retrieved.add(i.getIdDoc())
            );

            //Evaluate results and create charts
            evaluateResults(query);
        }
    }

    /**
     * Evaluates the results of a query and creates the corresponding charts
     * @param query The query to be evaluated
     */
    private void evaluateResults(QueryObject query){
        //Obtain the relevant document ids from the queryID
        ArrayList<Integer> relevantDocuments = query.getRelevantDocuments();
        XYChart.Series<Number, Number> precisionSeries = new XYChart.Series<>();
        precisionSeries.setName("Precision");
        XYChart.Series<Number, Number> recallSeries = new XYChart.Series<>();
        recallSeries.setName("Recall");

        int totalRecall = relevantDocuments.size();      //The total number of relevant documents for the query
        int currentRelevant = 0;//The relevant documents counted so far
        int totalCounted = 0;   //The total documents counted so far

        //Calculate precision and recall at each point of the retrieved list
        for(Integer documentID : query.getDocumentRetrieved()){
            totalCounted++;

            if(relevantDocuments.contains(documentID)){
                currentRelevant++;
            }

            float precision = (currentRelevant * 100.0f) / totalCounted;
            float recall = (currentRelevant * 100.0f) / totalRecall;

            //Add values to the chart
            precisionSeries.getData().add(new XYChart.Data<>(totalCounted, precision));
            recallSeries.getData().add(new XYChart.Data<>(totalCounted, recall));
        }

        query.getLineChart().getData().addAll(recallSeries, precisionSeries);
    }
}
