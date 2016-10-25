package test;

import controller.ControllerImportDocument;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import model.ModelDatabase;
import utilities.TermExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Thagus on 11/09/16.
 */
public class ControllerTests implements EventHandler<ActionEvent>, ChangeListener<TreeItem<String>>{
    private final ModelDatabase db;
    private HashMap<String, QueryObject> tagQuery;

    private HashMap<String, HashMap<Integer, ArrayList<Float>>> precisionMap;
    private HashMap<String, Float> averages, precisions;
    private LineChart<Number, Number> lineChart;

    private ViewTest view;

    public ControllerTests(ViewTest viewTest){
        db = ModelDatabase.instance();
        this.view = viewTest;
    }

    /**
     * Handles a menu item click from the view
     * @param event The fired event
     */
    @Override
    public void handle(ActionEvent event) {
        MenuItem node = (MenuItem) event.getSource();
        String userData = (String) node.getUserData();

        if(userData.equals("begin")){
            beginTests();
        }
    }

    /**
     * Obtains the queries to execute and starts the tests
     */
    public void beginTests(){
        long startTest = System.nanoTime();
        //Load queries
        ArrayList<QueryObject> queries = db.opTests.getQueries();

        //Sort them according the id (ascendant)
        Collections.sort(queries);

        //Instantiate arrays to contain average series
        precisionMap = new HashMap<>();
        precisions = new HashMap<>();
        averages = new HashMap<>();

        //Create average chart and axis
        final NumberAxis xAxis = new NumberAxis(0, 100, 10);
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Recall %");
        lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setCursor(Cursor.CROSSHAIR);
        lineChart.getYAxis().setAnimated(true);
        lineChart.getXAxis().setAnimated(true);
        lineChart.setMinHeight(640);


        tagQuery = new HashMap<>();

        int numOfQueries = db.opTests.countQueries();

        String[] testNames = {"Base", "Clustering"};

        for(String name : testNames){
            System.out.println("Starting test: " + name);

            HashMap<Integer, ArrayList<Float>> precisionSpecificMap = new HashMap<>();

            for (int i = 1; i <= 100; i++) {
                precisionSpecificMap.put(i, new ArrayList<>());
            }

            precisionMap.put(name, precisionSpecificMap);
            precisions.put(name, 0f);
            averages.put(name, 0f);

            if(name.contains("Clustering")){
                db.opModel.setClusteringActivated(true);
            }
            else {
                db.opModel.setClusteringActivated(false);
            }

            float recall = 0f, precision = 0f;


            //Execute the tests for each query
            for(QueryObject query : queries){
                ArrayList<Integer> retrieved = query.getDocumentRetrieved();    //Obtain the reference to the ArrayList of the query

                //Reset the retrieved documents
                retrieved.clear();

                //Retrieve result documents from evaluating query
                db.opModel.evaluateQuery(query.getQuery()).forEach(i ->
                        //For each retrieved document, extract its id and store it within the query
                        retrieved.add(i.getIdDoc())
                );

                //Evaluate results and create charts
                evaluateResults(query, name);

                //Evaluate averages
                recall += query.getRecall()/numOfQueries;
                precision += query.getPrecision()/numOfQueries;
            }

            precisions.put(name, precision);
            averages.put(name, recall);

            fillAverageChart(name);
        }

        //Update view
        updateTreeView(queries);
        long endTest = System.nanoTime();


        System.out.println("\nTests duration: " + (endTest-startTest)/1000000L + " ms");
    }

    /**
     * Evaluates the results of a query and creates the corresponding charts
     * @param query The query to be evaluated
     */
    private void evaluateResults(QueryObject query, String seriesName){
        //Obtain the relevant document ids from the queryID
        ArrayList<Integer> relevantDocuments = query.getRelevantDocuments();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        int totalRecall = relevantDocuments.size();      //The total number of relevant documents for the query
        int currentRelevant = 0;//The relevant documents counted so far
        int totalCounted = 0;   //The total documents counted so far

        float precision = 0, recall = 0;

        float lastRecall = -1;

        boolean check = false;

        if(totalRecall>0) {
            //Calculate precision and recall at each point of the retrieved list
            for (Integer documentID : query.getDocumentRetrieved()) {
                totalCounted++;

                if (relevantDocuments.contains(documentID)) {
                    currentRelevant++;
                }

                recall = (currentRelevant * 100.0f) / totalRecall;

                if(recall!=lastRecall && recall!=0){
                    check = true;
                    lastRecall = recall;

                    precision = (currentRelevant * 100.0f) / totalCounted;

                    //Add values to the chart
                    series.getData().add(new XYChart.Data<>(recall, precision));
                }
            }
        }
        else{
            System.out.println("Query " + query.getQid() + " has no relevant documents");
            return;     //Just in case there is a query with no relevant documents
        }

        query.setRecall(recall);
        query.setPrecision(precision);
        query.getLineChart().getData().add(series);

        if(check)
            feedAverages(series, seriesName);
    }

    /**
     * A method to calculate all the recall (from 1 to 100%) values in a series that doesn't contain all recall points
     *
     * @param series the series containing all the known points
     * @param seriesName the name of the series evaluated
     */
    private void feedAverages(XYChart.Series<Number, Number> series, String seriesName){
        ObservableList<XYChart.Data<Number, Number>> data = series.getData();
        int dataIndex =0;

        HashMap<Integer, ArrayList<Float>> precisionSpecificMap = precisionMap.get(seriesName);

        for(int recall=1; recall<=100; recall++){
            ArrayList<Float> precisions = precisionSpecificMap.get(recall);

            //If the recall we want is in the current position, add it and increase dataIndex if possible
            if(data.get(dataIndex).getXValue().floatValue()==recall){
                precisions.add(data.get(dataIndex).getYValue().floatValue());
                if(dataIndex+1 < data.size())
                    dataIndex++;
            }
            else{
                //dataIndex is 0 so there's no previous value, or we reached the last position so there is no next value
                if(dataIndex==0 || dataIndex+1>=data.size()){
                    precisions.add(data.get(dataIndex).getYValue().floatValue());
                }
                //The point we want is between the current dataIndex and the previous (because the current dataIndex recall is greater than the recall we want),
                // so we obtain the value based in the current and previous values
                else if(data.get(dataIndex).getXValue().floatValue()>recall){
                    precisions.add(xPoint(
                            data.get(dataIndex-1).getXValue().floatValue(),
                            data.get(dataIndex-1).getYValue().floatValue(),
                            data.get(dataIndex).getXValue().floatValue(),
                            data.get(dataIndex).getYValue().floatValue(),
                            recall
                    ));
                }
                //The point we want is between the current dataIndex and the next (because the current dataIndex recall is smaller than the recall we want),
                // so we obtain the value based in the current and next values
                else if(data.get(dataIndex).getXValue().floatValue()<recall){
                    precisions.add(xPoint(
                            data.get(dataIndex).getXValue().floatValue(),
                            data.get(dataIndex).getYValue().floatValue(),
                            data.get(dataIndex+1).getXValue().floatValue(),
                            data.get(dataIndex+1).getYValue().floatValue(),
                            recall
                    ));
                }
            }

            //If we wont get an IndexOutOfBounds exception and
            // the recall for the current dataIndex is greater to the recall being evaluated, increase dataIndex
            if(dataIndex+1 < data.size() && data.get(dataIndex).getXValue().floatValue()>recall){
                dataIndex++;
            }

            precisions.add(data.get(dataIndex).getYValue().floatValue());
        }
    }

    private float xPoint(float x1, float y1, float x2, float y2, float x){
        return y1 + ((y2 - y1)/(x2 - x1))*(x - x1);
    }

    /**
     * A method to create a chart of the precision average for every recall (from 1 to 100%)
     */
    private void fillAverageChart(String seriesName) {
        //Create series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        HashMap<Integer, ArrayList<Float>> precisionSpecificMap = precisionMap.get(seriesName);
        float maxRecall = averages.get(seriesName);
        float maxPrecision = precisions.get(seriesName);

        System.out.println("Test: " + seriesName + ". Max recall: " + maxRecall + ", max precision: " + maxPrecision);
        float first3=0, first5=0, first10=0, first15=0, first20=0, first30=0;

        //From 1% to 100% recall
        for(int recall=1; recall<=maxRecall; recall++){
            //Obtain the values array
            ArrayList<Float> precisions = precisionSpecificMap.get(recall);

            float averagePrecision = (float) precisions.stream()
                    .mapToDouble(v -> v)
                    .average()
                    .orElse(0);

            //Add values to the chart
            series.getData().add(new XYChart.Data<>(recall, averagePrecision));

            if(recall<=3)
                first3+=averagePrecision/3;
            if(recall<=5)
                first5+=averagePrecision/5;
            if(recall<=10)
                first10+=averagePrecision/10;
            if(recall<=15)
                first15+=averagePrecision/15;
            if(recall<=20)
                first20+=averagePrecision/20;
            if(recall<=30)
                first30+=averagePrecision/30;
        }

        //Average precision at 3%   5%   10%   15%   20%   30%
        System.out.println( seriesName + " ( " + seriesName + ")\t" + first3 + "\t" + first5 + "\t" + first10 + "\t" + first15 + "\t" + first20 + "\t" + first30);


        lineChart.getData().add(series);
    }

    /**
     * Updates the values of the TreeView
     * and adds the queries to a HashMap
     * @param queries An array containing all the queries
     */
    private void updateTreeView(ArrayList<QueryObject> queries){
        //Update lateral tree view, with format "Query #", where # is the number of query from the array
        TreeItem<String> rootItem = new TreeItem<>("Tests");
        rootItem.setExpanded(true);
        System.out.println("--------------------------------");

        for(QueryObject query : queries){
            String tag = "Query " + query.getQid();

            //Add query to hashmap
            tagQuery.put(tag, query);

            TreeItem<String> item = new TreeItem<>(tag);
            rootItem.getChildren().add(item);
        }

        view.setRootTreeView(rootItem);
    }

    /**
     * Handle the selection of items from the TreeView
     * @param observable
     * @param oldValue Previous selected value
     * @param newValue Selected value
     */
    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
        if(newValue!=null) {
            if(newValue.getValue().equals("Tests")){
                //View average
                view.setViewAverage(lineChart);
            }
            else{
                view.setViewedChart(tagQuery.get(newValue.getValue()));
            }
        }
    }
}