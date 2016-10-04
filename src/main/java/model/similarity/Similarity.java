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
        getMostRelevantTerms = connection.prepareStatement("SELECT term, weight FROM SPATIA.INVERTEDINDEX WHERE idDoc=? ORDER BY weight LIMIT ?");

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

            return  searchResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ObservableList<Document> similarityFeedback(HashMap<String, Integer> wordCount, int termLimit, int documentLimit, int iterations){
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

            //Set row count limit
            if(documentLimit<=0)
                stCalculateSimilarity.setMaxRows(relevantDocumentsLimit);
            else
                stCalculateSimilarity.setMaxRows(documentLimit);

            //Feedback iterations
            for(int i=0; i<iterations; i++){
                //Execute calculation of similarity
                ResultSet rs = stCalculateSimilarity.executeQuery();

                //Obtain every resulting document
                while (rs.next()){
                    //Obtain most relevant terms of document
                    getMostRelevantTerms.setInt(1, rs.getInt(1));
                    if(termLimit<=0)
                        getMostRelevantTerms.setInt(2, relevantTermsLimit);
                    else
                        getMostRelevantTerms.setInt(2, termLimit);

                    ResultSet termRS = getMostRelevantTerms.executeQuery();

                    //Add the most relevant terms to the query
                    while(termRS.next()){
                        String relevantTerm = termRS.getString(1);
                        double relevantTermWeight = termRS.getDouble(2);

                        mergeToQuery.clearParameters();

                        mergeToQuery.setString(1, relevantTerm);
                        mergeToQuery.setDouble(2, relevantTermWeight);
                        mergeToQuery.setString(3, relevantTerm);
                        mergeToQuery.setDouble(4, relevantTermWeight);

                        //Add the new terms, and update the weights in those that already exist
                        mergeToQuery.executeUpdate();
                    }
                }
            }


            /**
             * Calculate similarity
             */

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
}
