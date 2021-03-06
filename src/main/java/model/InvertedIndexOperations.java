package model;

import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Created by Thagus on 03/09/16.
 */
public class InvertedIndexOperations {
    private PreparedStatement stAddTerm;

    protected InvertedIndexOperations(Connection connection) throws SQLException{
        stAddTerm = connection.prepareStatement("INSERT INTO SPATIA.INVERTEDINDEX(url,term,tf) VALUES(?,?,?)");
    }

    /**
     * Add a term to the database
     * @param url The id of the document containing the term
     * @param term The term to be added
     * @param tf The Term Frequency (TF) of the term in the document
     */
    public synchronized void addTerm(String url, String term, int tf){
        try{
            stAddTerm.setString(1, url);
            stAddTerm.setString(2, term);
            stAddTerm.setInt(3, tf);

            stAddTerm.executeUpdate();
        } catch(SQLException e){
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                System.out.println("There is already the term \"" + term + "\" for document id: " + url);
                JOptionPane.showMessageDialog(null, "There is already the term \"" + term + "\" for document id: " + url);
            }
            //The insertion fails due to foreign key constraint failure
            else if(e.getErrorCode()==23506){
                System.out.println("There is no document with id: " + url);
                JOptionPane.showMessageDialog(null, "There is no document with id: " + url);
            }

            //Unhandled error
            System.out.println("Error adding term '" + term + "'");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term '" + term + "' in " + url + " with " + tf + " frequency", JOptionPane.ERROR_MESSAGE);
        }
    }
}
