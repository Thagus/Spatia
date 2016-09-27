package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 26/09/16.
 *
 * Based on the Maximun normalized TF and the TF*IDF
 */
public class MaximumNormalizedTFIDF extends Weight {
    public MaximumNormalizedTFIDF(Connection connection) throws SQLException {
        super("Maximum normalized TF-IDF", connection);

        stCalculateWeight = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX i SET i.weight=(0.4 + (0.6)*(i.tf/(SELECT MAX(i2.tf) FROM SPATIA.INVERTEDINDEX i2 WHERE i2.idDoc=i.idDoc)))*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=i.term)");

        stCalculateQueryWeight = connection.prepareStatement("UPDATE QUERY q SET q.weight=IFNULL(((0.4 + (0.6)*(q.tf/(SELECT MAX(q2.tf) FROM QUERY q2)))*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=q.term)), 0)");
    }
}
