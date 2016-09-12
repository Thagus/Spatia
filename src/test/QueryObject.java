package test;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.util.ArrayList;

/**
 * Created by Thagus on 11/09/16.
 */
public class QueryObject {
    private int qid;
    private String query;
    private ArrayList<Integer> relevantDocuments;
    private ArrayList<Integer> documentRetrieved;
    private LineChart<Number, Number> lineChart;

    public QueryObject(int qid){
        this.qid = qid;
        //this.relevantDocuments = new ArrayList<>();
    }

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
    }

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

    public void appendQuery(String line){
        if(line==null)  //Handle nulls
            return;
        if(query==null) //We dont have a query String yet
            query = line;
        else            //We already have a query
            query += " " + line;
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }
}
