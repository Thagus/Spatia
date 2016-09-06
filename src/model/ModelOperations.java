package model;

import dataObjects.Document;
import dataObjects.DocumentTerm;
import dataObjects.Term;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utilities.Tokenizer;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private PreparedStatement stGetDocumentTerm;

    protected ModelOperations(Connection connection, ModelDatabase db) throws SQLException{
        this.db = db;
        stCalculateTFIDF = connection.prepareStatement("UPDATE SPATIA.TERMS a SET a.tfidf=(SELECT b.idf*a.tf FROM SPATIA.IDF b WHERE a.term=b.term)");
        stGetDocumentTerm = connection.prepareStatement("SELECT d.idDoc, d.title, d.journal, t.tf, t.tfidf FROM SPATIA.DOCUMENT d NATURAL JOIN SPATIA.TERMS t WHERE t.term=?");
    }

    /**
     * A method to calculate tfidf from query and calculate similarity against documents,
     * in order to get the documents sorted by relevance
     *
     * @param query The input query from the user
     * @return An ArrayList of Documents sorted by their similarity to the query
     */
    public ArrayList<Document> evaluateQuery(String query){
        String[] splited = Tokenizer.tokenizeString(query);

        HashMap<String, Integer> wordCount = new HashMap<>();
        HashMap<String, Double> wordTFIDF = new HashMap<>();
        HashMap<Integer, ArrayList<Term>> documentTerms = new HashMap<>();
        ArrayList<Document> searchResult = new ArrayList<>();

        for(String word : splited){
            if(word.length()<=2)    //Filter void(0) words, and those with length 1 or 2
                continue;

            //Local count
            Integer count = wordCount.get(word);
            if(count==null){
                count = 0;
            }
            wordCount.put(word, count+1);
        }

        //Calculate tfidf for query
        for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
            double tfidfQuery = entry.getValue()*db.opIDF.getTermIDF(entry.getKey());
            wordTFIDF.put(entry.getKey(), tfidfQuery);

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

            //Calculate similarity by getting the current and adding the new one

            //Put it on the ArrayList
        }

        //Sort the ArrayList
        Collections.sort(searchResult);

        //Return
        return searchResult;
    }

    /**
     *  Search for the documents that contain a term and obtain the document along side the TF and IDF from
     *  the term in that document as DocumentTerm objects
     *
     * @param term The term that will be searched for
     * @return An ObservableList containing the DocumentTerms objects of the Documents containing the term and the TF and IDF from that term in the document
     */
    public ObservableList<DocumentTerm> termSearch(String term){
        ObservableList<DocumentTerm> searchResult = FXCollections.observableArrayList();

        //Natural join between Term and idf and document, filter by term
        try {
            stGetDocumentTerm.clearParameters();
            stGetDocumentTerm.setString(1, term);

            ResultSet rs = stGetDocumentTerm.executeQuery();
            boolean check = false;

            while (rs.next()){
                check = true;
                searchResult.add(new DocumentTerm(rs.getInt("idDoc"), rs.getString("title"), rs.getString("journal"), rs.getInt("tf"), rs.getDouble("tfidf")));
            }

            if(!check){
                JOptionPane.showMessageDialog(null,"There are no documents that join the term: \"" + term + "\"", "Alert!", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting document and term", JOptionPane.ERROR_MESSAGE);
        }
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
