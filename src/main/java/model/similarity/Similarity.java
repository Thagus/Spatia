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

        //The SQL query to add a term to the QUERY table
        addQuery = connection.prepareStatement("INSERT INTO QUERY(term,tf) VALUES(?,?)");
        //A query to clear the QUERY table, so we don't have more than a query at a time
        clearQuery = connection.prepareStatement("TRUNCATE TABLE QUERY");
    }

    /**
     * @return the name of the similarity method
     */
    public String getSimilarityMethodName() {
        return similarityMethodName;
    }

    /**
     * Saves the query on the QUERY table and executes the calculation of the similarity (that is implemented on the concrete subclases)
     * @param wordCount a hash table containing the terms of the query and their frequencies
     * @return The list of documents that are the result of the similarity method execution
     */
    public ObservableList<Document> calculateSimilarity(HashMap<String, Integer> wordCount) {
        try{
            //Insert query terms to memory table
            for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
                addQuery.clearParameters();
                addQuery.setString(1, entry.getKey());
                addQuery.setInt(2, entry.getValue());

                addQuery.executeUpdate();
            }

            //Calculate the query term weights
            ModelDatabase.instance().opModel.calculateQueryWeights();

            //Get all the documents from the query
            stCalculateSimilarity.setMaxRows(0);

            //Execute calculation of similarity
            ResultSet rs = stCalculateSimilarity.executeQuery();

            //Create Document objects
            ObservableList<Document> searchResult = FXCollections.observableArrayList();
            ModelDatabase db = ModelDatabase.instance();

            //Obtain every resulting document
            while (rs.next()){
                Document document = db.opDocuments.getDocument(rs.getString(1));
                document.setSimilarity(rs.getDouble(2));

                searchResult.add(document);
            }

            //Clear Query table
            clearQuery.executeUpdate();

            return searchResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
