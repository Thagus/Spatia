package model;

import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Created by Thagus on 03/09/16.
 */
public class TermOperations {
    private PreparedStatement stAddTerm;
    private PreparedStatement stSetTFIDF;

    protected TermOperations(Connection connection) throws SQLException{
        stAddTerm = connection.prepareStatement("INSERT INTO SPATIA.TERMS(idDoc,term,tf) VALUES(?,?,?)");
        stSetTFIDF = connection.prepareStatement("UPDATE SPATIA.TERMS SET tfidf=? WHERE idDoc=?");
    }

    public void addTerm(int idDoc, String term, int tf){
        try{
            stAddTerm.clearParameters();
            stAddTerm.setInt(1, idDoc);
            stAddTerm.setString(2, term);
            stAddTerm.setInt(3, tf);

            stAddTerm.executeUpdate();
        } catch(SQLException e){
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There is already the term \"" + term + "\" for document id: " + idDoc);
            }
            //The insertion fails due to foreign key constraint failure
            else if(e.getErrorCode()==23506){
                JOptionPane.showMessageDialog(null, "There is no document with id: " + idDoc);
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding Term", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean setTFIDF(int idDoc, float tfidf){
        try {
            stSetTFIDF.clearParameters();
            stSetTFIDF.setFloat(1, tfidf);
            stSetTFIDF.setInt(2, idDoc);

            stSetTFIDF.executeUpdate();
        } catch(SQLException e){
            //The insertion fails due to foreign key constraint failure
            if(e.getErrorCode()==23506){
                JOptionPane.showMessageDialog(null, "There is no document with id: " + idDoc);
                return false;
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding Term", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
