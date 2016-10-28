package model;

import dataObjects.Document;
import javafx.collections.ObservableList;
import model.clustering.Clustering;
import model.similarity.Cosine;
import model.similarity.DotProduct;
import model.similarity.Similarity;
import model.weight.*;
import utilities.TermExtractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Thagus on 04/09/16.
 */
public class ModelOperations {
    private Weight weight;
    private Similarity similarity;

    private Clustering clustering;
    private boolean clusteringActivated;

    protected ModelOperations(Connection connection) throws SQLException{
        clusteringActivated = true;

        clustering = new Clustering(connection);
        clustering.readClusters();

        weight = new TFIDF(connection);
        similarity = new Cosine(connection);
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

        if(clusteringActivated){
            //Get the documents that are related by cluster to the must relevant document based on its similarity to the query
            searchResult = clustering.getClusteredDocumentsFor(similarity.getMostRelevantDocumentID(wordCount));
        }
        else {
            //Request the calculation of similarity for the query, and save the results in the searchResult list
            searchResult = similarity.calculateSimilarity(wordCount);

            //Sort the results by similarity, from highest to lowest
            Collections.sort(searchResult, Collections.reverseOrder());
        }

        //Return the results
        return searchResult;
    }

    /**
     * Recalculate IDFs and weights
     */
    public void recalculateWeights(){
        weight.calculateIDFs();
        weight.calculateWeights();
    }

    public void calculateQueryWeights() {
        this.weight.calculateQueryWeights();
    }

    public void setClusteringActivated(boolean clusteringActivated) {
        this.clusteringActivated = clusteringActivated;
    }

    public void toggleClustering() {
        //this.clusteringActivated ^= true;
        this.clusteringActivated = !this.clusteringActivated;
    }
}
