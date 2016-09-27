package model.similarity;

import dataObjects.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ModelDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Similarity {
    private String similarityMethodName;
    private PreparedStatement addQuery, clearQuery;
    protected PreparedStatement stCalculateSimilarity;

    public Similarity(String similarityMethodName, Connection connection) throws SQLException {
        this.similarityMethodName = similarityMethodName;

        addQuery = connection.prepareStatement("INSERT INTO QUERY(term,tf) VALUES(?,?)");
        clearQuery = connection.prepareStatement("TRUNCATE TABLE QUERY");
    }

    public String getSimilarityMethodName() {
        return similarityMethodName;
    }

    public ObservableList<Document> calculateSimilarity(HashMap<String, Integer> wordCount){
        try{
            //Insert query terms to memory table
            for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
                addQuery.clearParameters();
                addQuery.setString(1, entry.getKey());
                addQuery.setInt(2, entry.getValue());

                addQuery.executeUpdate();
            }

            //Calculate the query weight
            ModelDatabase.instance().opModel.calculateQueryWeights();

            //Execute calculation of similarity
            ResultSet rs = stCalculateSimilarity.executeQuery();

            //Create Document objects
            ObservableList<Document> searchResult = FXCollections.observableArrayList();
            ModelDatabase db = ModelDatabase.instance();

            while (rs.next()){
                Document document = db.opDocuments.getDocument(rs.getInt(1));
                document.setSimilarity(rs.getDouble(2));

                searchResult.add(document);
            }

            //Clear Query database
            clearQuery.executeUpdate();

            return  searchResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
