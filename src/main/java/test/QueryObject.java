package test;

import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.util.ArrayList;

/**
 * A class to create objects that store query data and execution results
 * Created by Thagus on 11/09/16.
 */
public class QueryObject implements Comparable<QueryObject>{
    private int qid;
    private String query;
    private ArrayList<Integer> relevantDocuments;
    private ArrayList<Integer> documentRetrieved;
    private LineChart<Number, Number> lineChart;

    private float recall, precision;

    /**
     * Constructor for objects that store fresh data from input file
     * @param qid The query id
     */
    public QueryObject(int qid){
        this.qid = qid;
        //this.relevantDocuments = new ArrayList<>();
    }

    /**
     * Constructor to instantiate objects that are read from database
     * @param qid Query id
     * @param query The query text
     * @param relevantDocuments The ids of relevant documents to the query
     */
    public QueryObject(int qid, String query, ArrayList<Integer> relevantDocuments) {
        this.qid = qid;
        this.query = query;
        this.relevantDocuments = relevantDocuments;
        this.documentRetrieved = new ArrayList<>();

        //Creating chart and axis
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Recovered documents");
        lineChart = new LineChart<Number, Number>(xAxis,yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setCursor(Cursor.CROSSHAIR);

        lineChart.getYAxis().setAnimated(true);
        lineChart.getXAxis().setAnimated(true);
    }

    /**
     * Getters
     */

    public int getQid() {
        return qid;
    }

    public String getQuery() {
        return query;
    }

    public ArrayList<Integer> getRelevantDocuments() {
        return relevantDocuments;
    }

    public ArrayList<Integer> getDocumentRetrieved() {
        return documentRetrieved;
    }

    public float getRecall() {
        return recall;
    }

    public float getPrecision() {
        return precision;
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    /**
     * Setters
     */

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public void setRecall(float recall) {
        this.recall = recall;
    }

    /**
     * A method to append query content, useful when reading a query that has multiple lines from file
     * @param line The line of text to add
     */
    public void appendQuery(String line){
        if(line==null)  //Handle nulls
            return;
        if(query==null) //We dont have a query String yet
            query = line;
        else            //We already have a query
            query += " " + line;
    }

    /**
     * Compare two QueryObjects based on their query id
     * @param o the object to compare to
     */
    @Override
    public int compareTo(QueryObject o) {
        if(this.qid < o.qid)
            return -1;
        else if(this.qid > o.qid)
            return 1;
        else
            return 0;
    }
}
