package model.weight;

import model.ModelDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Thagus on 26/09/16.
 */
public class NormalizedIDF extends Weight {
    private PreparedStatement stCalculateWeight;

    public NormalizedIDF(Connection connection) throws SQLException{
        super("Normalized IDF");

        //[(log tfij + 1) * idfj] / [Σj=1,t [(log tfij + 1) * idfj]2]
        stCalculateWeight = connection.prepareStatement("MERGE INTO SPATIA.TERM(term,idf,weight) " +
                "SELECT t.term, t.idf, ((log(max(i.tf) + 1)*t.idf)/(sum(power(log(i.tf + 1)*t.idf, 2)))) as weight FROM SPATIA.TERM t NATURAL JOIN SPATIA.INVERTEDINDEX i GROUP BY t.term HAVING weight>0");
    }

    @Override
    public void calculateWeights() {
        try{
            stCalculateWeight.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
