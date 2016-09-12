package test;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.ArrayList;

/**
 * Created by Thagus on 11/09/16.
 */
public class QueryObject implements Comparable<QueryObject>{
    private int qid;
    private String query;
    private ArrayList<Integer> relevantDocuments;
    private ArrayList<Integer> documentRetrieved;
    private LineChart<Number, Number> lineChart;

    private float recall, precision;

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
        lineChart.setCreateSymbols(false);
        lineChart.setCursor(Cursor.CROSSHAIR);

        final double SCALE_DELTA = 1.1;

        lineChart.setOnScroll(new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

                lineChart.setScaleX(lineChart.getScaleX() * scaleFactor);
                lineChart.setScaleY(lineChart.getScaleY() * scaleFactor);
            }
        });

        lineChart.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    lineChart.setScaleX(1.0);
                    lineChart.setScaleY(1.0);
                }
            }
        });
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

    public float getRecall() {
        return recall;
    }

    public void setRecall(float recall) {
        this.recall = recall;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
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
