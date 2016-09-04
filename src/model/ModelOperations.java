package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Thagus on 04/09/16.
 */
public class ModelOperations {

    private PreparedStatement stGetTFIDF;

    protected ModelOperations(Connection connection) throws SQLException{
        stGetTFIDF = connection.prepareStatement("SELECT tfidf FROM SPATIA.TERMS WHERE idDoc=? AND term=?");

    }

    public float getSimilarity(int idDoc, HashMap<String, Float> query){

        return 0;
    }

    public void getDocumentTermOccurrence(){

    }
}
