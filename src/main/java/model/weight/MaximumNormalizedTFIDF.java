package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 26/09/16.
 *
 * Based on the Maximum normalized TF and the TF*IDF, using the formula:
 *      w(i,j)=0.4+0.6*[(tf(i,j)/m(i))*IDF(j)]
 */
public class MaximumNormalizedTFIDF extends Weight {
    public MaximumNormalizedTFIDF(Connection connection) throws SQLException {
        super("Maximum normalized TF-IDF", connection);

        //Query to calculate every weight of every term in every document
        stCalculateWeight = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX i SET i.weight=(0.4 + (0.6)*(i.tf/(SELECT MAX(i2.tf) FROM SPATIA.INVERTEDINDEX i2 WHERE i2.idDoc=i.idDoc)))*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=i.term)");
        //Query to calculate the weights for every term in teh query
        stCalculateQueryWeight = connection.prepareStatement("UPDATE QUERY q SET q.weight=IFNULL(((0.4 + (0.6)*(q.tf/(SELECT MAX(q2.tf) FROM QUERY q2)))*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=q.term)), 0)");
    }
}
