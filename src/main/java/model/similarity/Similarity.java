package model.similarity;

import dataObjects.Document;
import javafx.collections.ObservableList;
import model.ModelDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Similarity {
    private String similarityMethodName;
    PreparedStatement addQuery, clearQuery;

    public Similarity(String similarityMethodName, Connection connection) throws SQLException {
        this.similarityMethodName = similarityMethodName;

        addQuery = connection.prepareStatement("INSERT INTO QUERY(term,tf) VALUES(?,?)");
        clearQuery = connection.prepareStatement("TRUNCATE TABLE QUERY");
    }

    public String getSimilarityMethodName() {
        return similarityMethodName;
    }

    public abstract ObservableList<Document> calculateSimilarity(HashMap<String, Integer> wordCount);

    public void calculateQueryWeight() {
        ModelDatabase.instance().opModel.calculateQueryWeights();
    }
}
