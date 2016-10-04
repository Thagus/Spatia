package model.weight;

import model.ModelDatabase;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Weight {
    private String weightMethodName;
    private PreparedStatement stCalculateIDF;
    protected PreparedStatement stCalculateWeight, stCalculateQueryWeight;

    public Weight(String weightMethodName, Connection connection) throws SQLException {
        this.weightMethodName = weightMethodName;
        //The query to calculate the IDF for every term in the collection
        stCalculateIDF = connection.prepareStatement("MERGE INTO SPATIA.TERM(term,idf) " +
                "SELECT i.term, log(?/count(*)) as idf FROM SPATIA.INVERTEDINDEX i GROUP BY i.term");
    }

    /**
     * @return the name of the weighting method
     */
    public String getWeightMethodName() {
        return weightMethodName;
    }

    /**
     * Calculates the IDFs for every term in the TERM table
     */
    public void calculateIDFs(){
        System.out.println("Calculating IDFs...");
        try {
            //Calculate the IDF for every term in the collection
            int numberOfDocuments = ModelDatabase.instance().opDocuments.countDocuments();
            stCalculateIDF.clearParameters();
            stCalculateIDF.setInt(1, numberOfDocuments);
            stCalculateIDF.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the weights for every term in the documents
     */
    public void calculateWeights(){
        System.out.println("Calculating weights using: " + weightMethodName + "...");
        try{
            //Calculate weights
            stCalculateWeight.executeUpdate();  //Executes the query that updates the term weights from every document
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error calculating weights", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculates the term weights for every term in the current query
     */
    public void calculateQueryWeights(){
        try{
            //Calculate weights
            stCalculateQueryWeight.executeUpdate(); //Executes the query that updates the term weights
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error calculating query weights", JOptionPane.ERROR_MESSAGE);
        }
    }
}
