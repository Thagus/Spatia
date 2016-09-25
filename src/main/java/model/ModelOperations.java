package model;

import dataObjects.Document;
import javafx.collections.ObservableList;
import model.similarity.DotProduct;
import model.similarity.Similarity;
import model.weight.IDF;
import model.weight.Weight;
import utilities.TermExtractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Thagus on 04/09/16.
 */
public class ModelOperations {
    private Weight weight;
    private Similarity similarity;

    protected ModelOperations(Connection connection) throws SQLException{
        weight = new IDF(connection);
        similarity = new DotProduct(connection);
    }

    /**
     * A method to evaluate a query calculating its similarity against the documents,
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
