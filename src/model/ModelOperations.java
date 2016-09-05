package model;

import dataObjects.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 04/09/16.
 */
public class ModelOperations {
    private ModelDatabase db;

    private PreparedStatement stGetTFIDF;

    protected ModelOperations(Connection connection, ModelDatabase db) throws SQLException{
        this.db = db;
        stGetTFIDF = connection.prepareStatement("SELECT tfidf FROM SPATIA.TERMS WHERE idDoc=? AND term=?");

    }

    public ArrayList<Document> evaluateQuery(String query){
        String lowercase = query.toLowerCase();

        //Remove numbers
        lowercase = lowercase.replaceAll("\\d","");
        //Split at spaces or punctuation
        String[] spplited = lowercase.split("(\\s+)|\\p{Punct}");

        HashMap<String, Integer> wordCount = new HashMap<>();
        HashMap<String, Float> wordTFIDF = new HashMap<>();
        ArrayList<Document> searchResult = new ArrayList<>();

        for(String word : spplited){
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
            wordTFIDF.put(entry.getKey(), entry.getValue()*db.opIDF.getTermIDF(entry.getKey()));
        }

        //Get documents that contain terms in query

        //Calculate similarity

        //Sort the according similarity

        //Return
        return searchResult;
    }

    public void termSearch(String term){

    }


    public void calculateTFIDFs(){

    }
}
