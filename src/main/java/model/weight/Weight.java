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
    protected PreparedStatement stCalculateWeight;

    public Weight(String weightMethodName, Connection connection) throws SQLException {
        this.weightMethodName = weightMethodName;
        stCalculateIDF = connection.prepareStatement("MERGE INTO SPATIA.TERM(term,idf) " +
                "SELECT i.term, log(?/count(*)) as idf FROM SPATIA.INVERTEDINDEX i GROUP BY i.term");
    }

    public String getWeightMethodName() {
        return weightMethodName;
    }

    public void calculateWeights(){
        try{
            //Calculate IDF
            int numberOfDocuments = ModelDatabase.instance().opDocuments.countDocuments();
            stCalculateIDF.clearParameters();
            stCalculateIDF.setInt(1, numberOfDocuments);
            stCalculateIDF.executeUpdate();

            //Calculate weights
            stCalculateWeight.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error calculating weights", JOptionPane.ERROR_MESSAGE);
        }
    }
}
