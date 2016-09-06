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
    private PreparedStatement stUpdateIDF;
    private PreparedStatement stGetTermIDF;

    protected IDFOperations(Connection connection) throws SQLException{
        stAddTermIDF = connection.prepareStatement("INSERT INTO SPATIA.IDF(term,numDocs,idf) VALUES(?,?,?)");
        stUpdateIDF = connection.prepareStatement("UPDATE SPATIA.IDF SET idf=? WHERE term=?");
        stGetTermIDF = connection.prepareStatement("SELECT idf FROM SPATIA.IDF WHERE term=?");
    }

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

    public void updateIDF(String term, double idf){
        try {
            stGetTermIDF.clearParameters();
            stGetTermIDF.setDouble(1, idf);
            stGetTermIDF.setString(2, term);

            stGetTermIDF.executeUpdate();
        }catch (SQLException e){
            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term to IDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateNumDocs(String term, int numDocs){
        //Get current numDocs, if null assume 0

        //Add the current amount and the new amount

        //Recalculate idf, and call updateIDF
    }

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
                JOptionPane.showMessageDialog(null, "There is no IDF for term: " + term, "Error!", JOptionPane.ERROR_MESSAGE);
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
