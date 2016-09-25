package model;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Thagus on 04/09/16.
 */
public class TermOperations {
    private PreparedStatement stAddTerm;
    private PreparedStatement stGetTermWeight;
    private PreparedStatement stUpdateTerm;

    protected TermOperations(Connection connection) throws SQLException{
        stAddTerm = connection.prepareStatement("INSERT INTO SPATIA.TERM(term,weight) VALUES(?,?)");
        stGetTermWeight = connection.prepareStatement("SELECT weight FROM SPATIA.TERM WHERE term=?");
        stUpdateTerm = connection.prepareStatement("UPDATE SPATIA.TERM SET weight=? WHERE term=?");
    }

    /**
     * Add a Term to the database
     * @param term The term to be added
     * @param weight The weight value of the term
     */
    public void addTerm(String term, double weight){
        try {
            stAddTerm.clearParameters();
            stAddTerm.setString(1, term);
            stAddTerm.setDouble(2, weight);

            stAddTerm.executeUpdate();
        }catch (SQLException e){
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There term \"" + term + "\" already exists");
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding term to TERM", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update the weight of a term
     * @param term The term that will be updated
     * @param weight The new weight value
     */
    public void updateTerm(String term, double weight){
        try{
            stUpdateTerm.clearParameters();
            stUpdateTerm.setDouble(1, weight);
            stUpdateTerm.setString(2, term);

            stUpdateTerm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error updating weight", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get the weight of a term
     * @param term The term
     * @return The weight of the term
     */
    public double getTermWeight(String term){
        try {
            stGetTermWeight.clearParameters();
            stGetTermWeight.setString(1, term);

            ResultSet rs = stGetTermWeight.executeQuery();
            double weight = 0;
            boolean check = false;

            while(rs.next()){
                check = true;
                weight = rs.getDouble(1);
            }

            if(!check){
                //System.out.println("There is no weight for term: " + term);
            }

            return weight;
        } catch(SQLException e){
            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting term weight", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
}
