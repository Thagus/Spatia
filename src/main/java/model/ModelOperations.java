package model;

import dataObjects.Document;
import javafx.collections.ObservableList;
import model.similarity.Cosine;
import model.similarity.DotProduct;
import model.similarity.Similarity;
import model.weight.IDF;
import model.weight.NormalizedIDF;
import model.weight.Weight;
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

    protected ModelOperations(Connection connection) throws SQLException{
        similarityHashMap = new HashMap<>();
        weightHashMap = new HashMap<>();

        //Create similarity objects
        similarityHashMap.put("Dot product", new DotProduct(connection));
        similarityHashMap.put("Cosine", new Cosine(connection));

        //Create weight objects
        weightHashMap.put("IDF", new IDF(connection));
        weightHashMap.put("Normalized IDF", new NormalizedIDF(connection));


        //Initialize with IDF and DotProduct
        setSimilarityMethod("Dot product");
        setWeightMethod("IDF");
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

    /**
     *  Getters
     */

    public String getSimilarityName(){
        return similarity.getSimilarityMethodName();
    }

    public String getWeightName(){
        return weight.getWeightMethodName();
    }

    /**
     * Set the weight method
     * @param weightMethod the weight method name to be used
     */
    public void setWeightMethod(String weightMethod){
        if(this.weight==null || this.weight.getWeightMethodName()!=weightMethod) {
            this.weight = weightHashMap.get(weightMethod);
            System.out.println("Calculating weights...");
            calculateWeights();
            System.out.println("Weights calculated!");
        }
        else{
            System.out.println("\nTrying to calculate an already calculated weight method!\nSkippong...\n");
        }
    }

    /**
     * Set the similarity method
     * @param similarityMethod The similarity method name to be used
     */
    public void setSimilarityMethod(String similarityMethod){
        this.similarity = similarityHashMap.get(similarityMethod);
    }
}
