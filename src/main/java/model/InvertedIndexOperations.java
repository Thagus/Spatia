package model;

import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Created by Thagus on 03/09/16.
 */
public class InvertedIndexOperations {
    private PreparedStatement stAddTerm;

    protected InvertedIndexOperations(Connection connection) throws SQLException{
        stAddTerm = connection.prepareStatement("INSERT INTO SPATIA.INVERTEDINDEX(idDoc,term,tf) VALUES(?,?,?)");
    }

    /**
     * Add a term to the database
     * @param idDoc The id of the document containing the term
     * @param term The term to be added
     * @param tf The Term Frequency (TF) of the term in the document
     */
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
}
