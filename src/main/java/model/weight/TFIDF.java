package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 24/09/16.
 *
 * Definition of the query to calculate TF-IDF following the formula:
 *      dw(i,j)*idf(j) for every term j and every document i
 */
public class TFIDF extends Weight{
    public TFIDF(Connection connection) throws SQLException {
        super("TF-IDF", connection);

        //Weight calculation of TF-IDF for every term in every document
        stCalculateWeight = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX i SET i.weight=i.tf*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=i.term)");
        //Updates the query terms weight if the value to update is not null, otherwise leave it as a 0
        stCalculateQueryWeight = connection.prepareStatement("UPDATE QUERY q SET q.weight=q.tf*IFNULL((SELECT t.idf FROM SPATIA.TERM t WHERE t.term=q.term),0)");
    }
}
