package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Thagus on 03/09/16.
 */
public class ModelOperations {
    private PreparedStatement stAddTerm;
    private PreparedStatement stSetTFIDF;

    protected ModelOperations(Connection connection) throws SQLException{
        stAddTerm = connection.prepareStatement("INSERT INTO SPATIA.TERMS(idDoc,term,tf) VALUES(?,?,?)");
        stSetTFIDF = connection.prepareStatement("UPDATE SPATIA.TERMS SET tfidf=? WHERE idDoc=?");
    }
}
