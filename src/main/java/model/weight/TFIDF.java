package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 24/09/16.
 */
public class TFIDF extends Weight{
    public TFIDF(Connection connection) throws SQLException {
        super("TF-IDF", connection);

        stCalculateWeight = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX i SET i.weight=tf*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=i.term)");
    }
}
