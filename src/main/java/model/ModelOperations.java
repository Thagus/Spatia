package model;

import dataObjects.Document;
import dataObjects.Term;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.similarity.DotProduct;
import model.similarity.Similarity;
import model.weight.IDF;
import model.weight.Weight;
import utilities.TermExtractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 04/09/16.
 */
public class ModelOperations {
    private ModelDatabase db;
    private Weight weight;
    private Similarity similarity;

    protected ModelOperations(Connection connection, ModelDatabase db) throws SQLException{
        this.db = db;
        //stCalculateTFIDF = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX a SET a.tfidf=(SELECT b.weight*a.tf FROM SPATIA.TERM b WHERE a.term=b.term)");

        weight = new IDF(connection);
        similarity = new DotProduct(connection);
    }

    /**
     * A method to calculate tfidf from query and calculate similarity against documents,
     * in order to get the documents sorted by relevance
     *
     * @param query The input query from the user
     * @return An ArrayList of Documents sorted by their similarity to the query
     */
    public ObservableList<Document> evaluateQuery(String query){
        HashMap<String, Integer> wordCount = TermExtractor.extractTerms(query);           //Counter for word occurrence in the query
        ObservableList<Document> searchResult;

        searchResult = similarity.calculateSimilarity(wordCount);

        //Sort the ArrayList
        Collections.sort(searchResult, Collections.reverseOrder());

        //Return
        return searchResult;
    }

    /**
     * A method to calculate the weights of the inserted terms
     */
    public void calculateWeights() {
        weight.calculateWeights();
    }
}
