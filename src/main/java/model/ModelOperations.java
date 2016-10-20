package model;

import dataObjects.Document;
import javafx.collections.ObservableList;
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

    private HashMap<String, Weight> weightHashMap;
    private HashMap<String, Similarity> similarityHashMap;

    private boolean idfCalculated;

    protected ModelOperations(Connection connection) throws SQLException{
        idfCalculated = true;

        similarityHashMap = new HashMap<>();
        weightHashMap = new HashMap<>();

        //Create similarity objects
        //similarityHashMap.put("Dot product", new DotProduct(connection));
        similarityHashMap.put("Cosine", new Cosine(connection));

        //Create weight objects
        weightHashMap.put("TF-IDF", new TFIDF(connection));

        //Initialize with TF-IDF and DotProduct
        setSimilarityMethod("Cosine");
        setWeightMethod("TF-IDF");
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

        //Request the calculation of similarity for the query, and save the results in the searchResult list
        searchResult = similarity.calculateSimilarity(wordCount);

        //Sort the results by similarity, from highest to lowest
        Collections.sort(searchResult, Collections.reverseOrder());

        //Return the results
        return searchResult;
    }

    /**
     * Recalculate IDFs and weights
     */
    public void recalculateWeights(){
        weight.calculateIDFs();
        idfCalculated = true;
        weight.calculateWeights();
    }

    /**
     *  Getters
     */

    public Set<String> getSimilarityMethods(){
        return similarityHashMap.keySet();
    }

    public Set<String> getWeightMethods(){
        return weightHashMap.keySet();
    }

    /**
     * Set the desired weight method
     * @param weightMethod the weight method name to be used
     */
    public void setWeightMethod(String weightMethod){
        if(this.weight==null || !this.weight.getWeightMethodName().equals(weightMethod)) {
            this.weight = weightHashMap.get(weightMethod);
            if(!idfCalculated) {
                weight.calculateIDFs();
                idfCalculated = true;
                weight.calculateWeights();
            }
        }
    }

    /**
     * Set the desired similarity method
     * @param similarityMethod The similarity method name to be used
     */
    public void setSimilarityMethod(String similarityMethod){
        this.similarity = similarityHashMap.get(similarityMethod);
    }

    public void calculateQueryWeights() {
        this.weight.calculateQueryWeights();
    }
}
