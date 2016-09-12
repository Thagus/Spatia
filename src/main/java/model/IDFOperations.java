package model;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Thagus on 04/09/16.
 */
public class IDFOperations {
    private PreparedStatement stAddTermIDF;
    private PreparedStatement stGetTermIDF;
    private PreparedStatement stGetNumDocs;
    private PreparedStatement stUpdateIDF;

    protected IDFOperations(Connection connection) throws SQLException{
        stAddTermIDF = connection.prepareStatement("INSERT INTO SPATIA.IDF(term,numDocs,idf) VALUES(?,?,?)");
        stGetTermIDF = connection.prepareStatement("SELECT idf FROM SPATIA.IDF WHERE term=?");
        stGetNumDocs = connection.prepareStatement("SELECT numDocs FROM SPATIA.IDF WHERE term=?");
        stUpdateIDF = connection.prepareStatement("UPDATE SPATIA.IDF SET numDocs=?, idf=? WHERE term=?");
    }

    /**
     * Add a Term to the database
     * @param term The term to be added
     * @param numDocs The number of documents containing the term
     * @param idf The IDF value of the term
     */
    public void addTermIDF(String term, int numDocs, double idf){
        try {
            stAddTermIDF.clearParameters();
            stAddTermIDF.setString(1, term);
            stAddTermIDF.setInt(2, numDocs);
            stAddTermIDF.setDouble(3, idf);

            stAddTermIDF.executeUpdate();
        }catch (SQLException e){
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There term \"" + term + "\" already exists");
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term to IDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update the numDocs and IDF of a term
     * @param term The term that will be updated
     * @param numDocs The new numDocs value
     * @param idf The new IDF value
     */
    public void updateIDF(String term, int numDocs, double idf){
        try{
            stUpdateIDF.clearParameters();
            stUpdateIDF.setInt(1, numDocs);
            stUpdateIDF.setDouble(2, idf);
            stUpdateIDF.setString(3, term);

            stUpdateIDF.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term to IDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get the numDocs of a term
     * @param term The term
     * @return The numDOcs of teh term
     */
    public int getNumDocs(String term){
        try {
            stGetNumDocs.clearParameters();
            stGetNumDocs.setString(1, term);

            ResultSet rs = stGetNumDocs.executeQuery();

            if(rs.next()){
                return rs.getInt(1);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term to IDF", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    /**
     * Get the IDF of a term
     * @param term The term
     * @return The IDF of the term
     */
    public double getTermIDF(String term){
        try {
            stGetTermIDF.clearParameters();
            stGetTermIDF.setString(1, term);

            ResultSet rs = stGetTermIDF.executeQuery();
            double idf = 0;
            boolean check = false;

            while(rs.next()){
                check = true;
                idf = rs.getDouble(1);
            }

            if(!check){
                System.out.println("There is no IDF for term: " + term);
            }

            return idf;
        } catch(SQLException e){
            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting term IDF", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
}
