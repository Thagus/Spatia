package model.similarity;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 24/09/16.
 */
public class DotProduct extends Similarity {

    public DotProduct(Connection connection) throws SQLException {
        super("Dot product", connection);
        stCalculateSimilarity = connection.prepareStatement("SELECT i.idDoc, SUM(q.weight*i.weight) as sim " +
                                                        "FROM QUERY q, SPATIA.INVERTEDINDEX i " +
                                                        "WHERE q.term=i.term " +
                                                        "GROUP BY i.idDoc " +
                                                        "HAVING sim>0");
    }
}
