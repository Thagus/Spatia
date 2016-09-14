package model;

import dataObjects.Document;
import dataObjects.Term;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utilities.Tokenizer;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private PreparedStatement stCalculateTFIDF;

    protected ModelOperations(Connection connection, ModelDatabase db) throws SQLException{
        this.db = db;
        stCalculateTFIDF = connection.prepareStatement("UPDATE SPATIA.TERMS a SET a.tfidf=(SELECT b.idf*a.tf FROM SPATIA.IDF b WHERE a.term=b.term)");
    }

    /**
     * A method to calculate tfidf from query and calculate similarity against documents,
     * in order to get the documents sorted by relevance
     *
     * @param query The input query from the user
     * @return An ArrayList of Documents sorted by their similarity to the query
     */
    public ObservableList<Document> evaluateQuery(String query){
        HashMap<String, Integer> wordCount = Tokenizer.tokenizeString(query);           //Counter for word occurrence in the query
        HashMap<Integer, ArrayList<Term>> documentTerms = new HashMap<>();              //Holds the document id of those who have terms of the query, and an array of those terms with their similarity
        ObservableList<Document> searchResult = FXCollections.observableArrayList();

        //Calculate tfidf for query
        for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
            double tfidfQuery = entry.getValue()*db.opIDF.getTermIDF(entry.getKey());

            //Get documents that contain terms from query
            ArrayList<Term> docTerms = db.opTerm.getDocumentsContainingTerm(entry.getKey());
            for(Term t : docTerms){
                documentTerms.putIfAbsent(t.getIdDoc(), new ArrayList<Term>());  //If there is no entry for IdDoc, put a new entry
                t.setSimilarity(t.getTfidf()*tfidfQuery);                       //Calculate term similarity to query term
                documentTerms.get(t.getIdDoc()).add(t);                          //Put the current Term in their corresponding ArrayList in the HashMap
            }
        }

        //Calculate similarity for documents
        for(Map.Entry<Integer, ArrayList<Term>> entry : documentTerms.entrySet()){
            //Get document object by idDoc
            Document document = db.opDocuments.getDocument(entry.getKey());
            //Calculate similarity by getting the current and adding the new one
            double similarity = 0;
            //Put it on the ArrayList
            for(Term term : entry.getValue()){
                similarity += term.getSimilarity();
            }

            document.setSimilarity(similarity);

            searchResult.add(document);
        }

        //Sort the ArrayList
        Collections.sort(searchResult, Collections.reverseOrder());

        //Return
        return searchResult;
    }

    /**
     * Execute the query to calculate all the TFIDFs from the TERMS table in the database
     */
    public void calculateTFIDFs(){
        try{
            stCalculateTFIDF.executeUpdate();
        } catch(SQLException e){
            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term to IDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculate the IDF for the given term
     *
     * @param term The term that we want to calculate the IDF
     * @param numTotalDocs The total number of documents in the database
     * @param newNumDocs The number of occurrences of the term in tha newly added documents
     */
    public void calculateIDF(String term, int numTotalDocs, int newNumDocs){
        //Get the current numDocs of the term, in case that the term exists
        int currNumDocs = db.opIDF.getNumDocs(term);

        if(currNumDocs==-1){    //The term hasn't been indexed, therefore there aren't documents containing the term
            double idf = Math.log10((double)numTotalDocs/(double)newNumDocs);
            db.opIDF.addTermIDF(term, newNumDocs, idf);
        }
        else{                   //The term has been indexed, therefore we have a currNumDocs
            int numDocs = currNumDocs + newNumDocs;
            double idf = Math.log10((double)numTotalDocs/(double)numDocs);
            db.opIDF.updateIDF(term, numDocs, idf);     //Update the value
        }
    }
}
