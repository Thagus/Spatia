package model.weight;

import model.ModelDatabase;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Thagus on 24/09/16.
 */
public class IDF extends Weight{
    private PreparedStatement calculateIDF;

    public IDF(Connection connection) throws SQLException {
        weightMethodName = "IDF";
        calculateIDF = connection.prepareStatement("INSERT INTO SPATIA.TERM(term,weight) " +
                "SELECT term, log(?/count(*)) FROM SPATIA.INVERTEDINDEX GROUP BY term ");
    }

    /**
     * Execute the query to calculate all the IDFs for the TERM table in the database
     */
    @Override
    public void calculateWeights(){
        try{
            int numberOfDocuments = ModelDatabase.instance().opDocuments.countDocuments();

            calculateIDF.clearParameters();
            calculateIDF.setInt(1, numberOfDocuments);

            calculateIDF.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error calculating IDFs", JOptionPane.ERROR_MESSAGE);
        }
    }
}
