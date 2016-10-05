package test;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.ModelDatabase;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Thagus on 11/09/16.
 */
public class ControllerTests implements EventHandler<ActionEvent>, ChangeListener<TreeItem<String>>{
    private final Stage window;
    private final TestsDatabase db;
    private HashMap<String, QueryObject> tagQuery;

    private HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Float>>>> precisionMap;
    private HashMap<String, HashMap<String, Float>> testAverages, testPrecisions;
    private HashMap<String, LineChart<Number, Number>> lineCharts;

    private ViewTest view;

    public ControllerTests(Stage window, ViewTest viewTest){
        db = TestsDatabase.instance();
        this.window = window;
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

        //A message to alert the user about the number of read documents
        Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + queriesRead.size() + " queries");
        countInfo.setTitle("Successful reading!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();

        //Delete queries with no relevant documents
        db.deleteEmptyQueries();

        //A message to alert the user about the number of read documents
        countInfo = new Alert(Alert.AlertType.INFORMATION, "Cleared empty queries!, remained " + db.countQueries() + " queries");
        countInfo.setTitle("Successful cleaning!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();
    }

    /**
     * Obstans the queries to execute and start the tests
     */
    public void beginTests(){
        long startTest = System.nanoTime();

        //Get the main database instance
        ModelDatabase mainDatabase = ModelDatabase.instance();


        Set<String> weightNames = mainDatabase.opModel.getWeightMethods();
        Set<String> similarityNames = mainDatabase.opModel.getSimilarityMethods();


        //Instantiate hashmaps for averages
        precisionMap = new HashMap<>();
        testAverages = new HashMap<>();
        testPrecisions = new HashMap<>();

        //Tree view root
        TreeItem<String> root = new TreeItem<>("Tests");
        root.setExpanded(true);

        lineCharts = new HashMap<>();

        tagQuery = new HashMap<>();

        int numOfQueries = db.countQueries();

        for(int i=1; i<=1; i++){    //Iterate through document limits
            for(int j=1; j<=1; j++){   //Iterate through term limits
                String testName = "Test - " + i + ", " + j;
                //Create average chart and axis
                NumberAxis xAxis = new NumberAxis(0, 100, 10);
                NumberAxis yAxis = new NumberAxis(0, 100, 10);
                xAxis.setLabel("Recall %");
                LineChart<Number, Number> lineChart = new LineChart<>(xAxis,yAxis);
                lineChart.setCreateSymbols(false);
                lineChart.setCursor(Cursor.CROSSHAIR);
                lineChart.getYAxis().setAnimated(true);
                lineChart.getXAxis().setAnimated(true);
                lineChart.setMinHeight(640);

                lineCharts.put(testName, lineChart);

                //Load queries
                ArrayList<QueryObject> queries = db.getQueries();
                //Sort them according the id (ascendant)
                Collections.sort(queries);


                //Begin tests for each similarity and weight combination
                for(String w : weightNames) {
                    //Set weight method
                    mainDatabase.opModel.setWeightMethod(w);
                    mainDatabase.opModel.calculateWeights();    //Calculate weights

                    for (String s : similarityNames) {
                        //Set similarity method
                        mainDatabase.opModel.setSimilarityMethod(s);
                        //Define test name
                        String name = w + " - " + s;

                        HashMap<String, HashMap<Integer, ArrayList<Float>>> testNameMap = precisionMap.get(testName);

                        if(testNameMap==null){
                            testNameMap = new HashMap<>();
                        }

                        HashMap<Integer, ArrayList<Float>> precisionSpecificMap = new HashMap<>();
                        for (int p = 1; p<=100; p++) {
                            precisionSpecificMap.put(p, new ArrayList<>());
                        }

                        testNameMap.put(name, precisionSpecificMap);
                        precisionMap.put(testName, testNameMap);

                        HashMap<String, Float> precisions = testPrecisions.get(testName);
                        if(precisions==null){
                            precisions = new HashMap<>();
                        }
                        precisions.put(name, 0f);
                        testPrecisions.put(testName, precisions);


                        HashMap<String, Float> averages = testAverages.get(testName);
                        if(averages==null){
                            averages = new HashMap<>();
                        }
                        averages.put(name, 0f);
                        testAverages.put(testName, averages);


                        System.out.println("Starting test: " + name + ". With: " + testName);

                        float recall = 0f, precision = 0f;


                        //Execute the tests for each query
                        for(QueryObject query : queries){
                            ArrayList<Integer> retrieved = query.getDocumentRetrieved();    //Obtain the reference to the ArrayList of the query

                            //Reset the retrieved documents
                            retrieved.clear();

                            //Retrieve result documents from evaluating query
                            mainDatabase.opModel.evaluateQuery(query.getQuery(), j, i).forEach(q ->
                                    //For each retrieved document, extract its id and store it within the query
                                    retrieved.add(q.getIdDoc())
                            );

                            //Evaluate results and create charts
                            evaluateResults(query, name, testName);

                            //Evaluate averages
                            recall += query.getRecall()/numOfQueries;
                            precision += query.getPrecision()/numOfQueries;
                        }

                        precisions.put(name, precision);
                        averages.put(name, recall);

                        fillAverageChart(name, testName);
                    }
                }
                //Add the tests to the view
                updateTreeView(queries, root, testName);
            }
        }

        //Update view
        view.setRootTreeView(root);

        long endTest = System.nanoTime();


        System.out.println("\nTests duration: " + (endTest-startTest)/1000000L + " ms");
    }

    /**
     * Evaluates the results of a query and creates the corresponding charts
     * @param query The query to be evaluated
     */
    private void evaluateResults(QueryObject query, String seriesName, String testName){
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
            feedAverages(series, seriesName, testName);
    }

    /**
     * A method to calculate all the recall (from 1 to 100%) values in a series that doesn't contain all recall points
     *
     * @param series the series containing all the known points
     * @param seriesName the name of the series evaluated
     */
    private void feedAverages(XYChart.Series<Number, Number> series, String seriesName, String testName){
        ObservableList<XYChart.Data<Number, Number>> data = series.getData();
        int dataIndex =0;

        HashMap<Integer, ArrayList<Float>> precisionSpecificMap = precisionMap.get(testName).get(seriesName);

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
    private void fillAverageChart(String seriesName, String testName) {
        //Create series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        HashMap<Integer, ArrayList<Float>> precisionSpecificMap = precisionMap.get(testName).get(seriesName);
        float maxRecall = testAverages.get(testName).get(seriesName);
        float maxPrecision = testPrecisions.get(testName).get(seriesName);

        System.out.println("Test: " + seriesName + ". Max recall: " + maxRecall + ", end precision: " + maxPrecision);

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
        }

        lineCharts.get(testName).getData().add(series);
    }

    /**
     * Updates the values of the TreeView
     * and adds the queries to a HashMap
     * @param queries An array containing all the queries
     */
    private void updateTreeView(ArrayList<QueryObject> queries, TreeItem<String> root, String testName){
        //Update lateral tree view, with format "Query #", where # is the number of query from the array
        TreeItem<String> rootItem = new TreeItem<>(testName);
        rootItem.setExpanded(true);
        System.out.println("--------------------------------");

        for(QueryObject query : queries){
            String tag = "Query " + query.getQid() + " (" + testName + ")";

            //Add query to hashmap
            tagQuery.put(tag, query);

            TreeItem<String> item = new TreeItem<>(tag);
            rootItem.getChildren().add(item);
        }

        root.getChildren().add(rootItem);
    }

    /**
     * Handle the selection of items from the TreeView
     * @param observable
     * @param oldValue Previous selected value
     * @param newValue Selected value
     */
    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
        if(newValue!=null || !newValue.getValue().equals("Tests")) {
            if(newValue.getValue().startsWith("Test - ")){
                //View average
                view.setViewAverage(lineCharts.get(newValue.getValue()));
            }
            else{
                view.setViewedChart(tagQuery.get(newValue.getValue()));
            }
        }
    }
}
