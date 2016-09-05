package model;

import dataObjects.Term;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Thagus on 03/09/16.
 */
public class TermOperations {
    private PreparedStatement stAddTerm;
    private PreparedStatement stSetTFIDF;
    private PreparedStatement stGetTerms;
    private PreparedStatement stGetTermCount;

    protected TermOperations(Connection connection) throws SQLException{
        stAddTerm = connection.prepareStatement("INSERT INTO SPATIA.TERMS(idDoc,term,tf) VALUES(?,?,?)");
        stSetTFIDF = connection.prepareStatement("UPDATE SPATIA.TERMS SET tfidf=? WHERE idDoc=?");
        stGetTerms = connection.prepareStatement("SELECT * FROM SPATIA.TERMS WHERE term=?");
        stGetTermCount = connection.prepareStatement("SELECT COUNT(*) FROM SPATIA.TERMS WHERE term=?");
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

    public boolean setTFIDF(int idDoc, double tfidf){
        try {
            stSetTFIDF.clearParameters();
            stSetTFIDF.setDouble(1, tfidf);
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

    public ArrayList<Term> getDocumentsContainingTerm(String term){
        try {
            stGetTerms.clearParameters();
            stGetTerms.setString(1, term);

            ResultSet rs = stGetTerms.executeQuery();
            ArrayList<Term> docTerms = new ArrayList<>();
            boolean check = false;

            while(rs.next()){
                check = true;
                docTerms.add(new Term(rs.getInt("idDoc"), rs.getString("term"), rs.getInt("tf"), rs.getDouble("tfidf")));
            }

            if(!check){
                JOptionPane.showMessageDialog(null, "There are no documents containing term: " + term, "Alert!", JOptionPane.ERROR_MESSAGE);
            }

            return docTerms;
        } catch(SQLException e){
            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting term count for: " + term, JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    //Get the amount of documents that contain the term
    public int getTermCount(String term){
        try {
            stGetTermCount.clearParameters();
            stGetTermCount.setString(1, term);

            ResultSet rs = stGetTermCount.executeQuery();
            int count = 0;
            boolean check = false;

            while(rs.next()){
                check = true;
                count = rs.getInt(1);
            }

            if(!check){
                JOptionPane.showMessageDialog(null, "There is no document containing term: " + term, "Error!", JOptionPane.ERROR_MESSAGE);
            }

            return count;
        } catch(SQLException e){
            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting term count for: " + term, JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
}
