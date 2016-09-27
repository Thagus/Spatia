package model;

import dataObjects.Document;
import javafx.collections.ObservableList;
import model.similarity.DotProduct;
import model.similarity.Similarity;
import model.weight.TFIDF;
import model.weight.NormalizedTFIDF;
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
        //similarityHashMap.put("Cosine", new Cosine(connection));

        //Create weight objects
        weightHashMap.put("TF-IDF", new TFIDF(connection));
        weightHashMap.put("Normalized TF-IDF", new NormalizedTFIDF(connection));


        //Initialize with TF-IDF and DotProduct
        setSimilarityMethod("Dot product");
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

    public Set<String> getSimilarityMethods(){
        return similarityHashMap.keySet();
    }

    public Set<String> getWeightMethods(){
        return weightHashMap.keySet();
    }

    /**
     * Set the weight method
     * @param weightMethod the weight method name to be used
     */
    public void setWeightMethod(String weightMethod){
        if(this.weight==null || this.weight.getWeightMethodName()!=weightMethod) {
            this.weight = weightHashMap.get(weightMethod);
            System.out.print("\nCalculating weights...");
            calculateWeights();
            System.out.print("Weights calculated!\n");
        }
        else{
            System.out.println("\nTrying to calculate an already calculated weight method!\nSkipping...\n");
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
