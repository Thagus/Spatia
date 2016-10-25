package model.similarity;

import dataObjects.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import model.ModelDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Similarity {
    private String similarityMethodName;
    private PreparedStatement addQuery, clearQuery, mergeToQuery;
    private PreparedStatement getMostRelevantTerms;
    protected PreparedStatement stCalculateSimilarity;

    private int relevantTermsLimit, relevantDocumentsLimit;

    public Similarity(String similarityMethodName, Connection connection) throws SQLException {
        this.similarityMethodName = similarityMethodName;
        this.relevantTermsLimit = 10;
        this.relevantDocumentsLimit = 10;

        //The SQL query to add a term to the QUERY table
        addQuery = connection.prepareStatement("INSERT INTO QUERY(term,tf) VALUES(?,?)");
        //A query to clear the QUERY table, so we don't have more than a query at a time
        clearQuery = connection.prepareStatement("TRUNCATE TABLE QUERY");
        //Get the N most relevant terms of a document
        getMostRelevantTerms = connection.prepareStatement("SELECT term, weight FROM SPATIA.INVERTEDINDEX WHERE idDoc=? ORDER BY weight DESC LIMIT ?");

        mergeToQuery = connection.prepareStatement("MERGE INTO QUERY(term,weight) VALUES(?,IFNULL(SELECT weight+? FROM QUERY WHERE term=?,?))");
    }

    /**
     * @return the name of the similarity method
     */
    public String getSimilarityMethodName() {
        return similarityMethodName;
    }

    /**
     * Setters
     */
    public void setRelevantTermsLimit(int limit){
        this.relevantTermsLimit = limit;
    }

    public void setRelevantDocumentsLimit(int limit) {
        this.relevantDocumentsLimit = limit;
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
                Document document = db.opDocuments.getDocument(rs.getInt(1));
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

    /**
     * Get the ID of the most relevant document for the given query
     * @param wordCount a hash table containing the terms of the query and their frequencies
     * @return the ID of the most relevant document
     */
    public int getMostRelevantDocumentID(HashMap<String, Integer> wordCount){
        int result = -1;

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

            //Get just the first document from the query
            stCalculateSimilarity.setMaxRows(1);

            //Execute calculation of similarity
            ResultSet rs = stCalculateSimilarity.executeQuery();

            //Obtain every resulting document
            while (rs.next()){
                result = rs.getInt(1);
            }

            //Clear Query table
            clearQuery.executeUpdate();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
