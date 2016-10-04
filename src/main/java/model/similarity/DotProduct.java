package model.similarity;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 24/09/16.
 *
 * Definition of the calculation of the dot product, using the formula:
 *      sim(d(i),q)=∑[wd(i,j)*wq(j)]
 */
public class DotProduct extends Similarity {

    public DotProduct(Connection connection) throws SQLException {
        super("Dot product", connection);
        //Query definition to calculate similarity and return those documents with more than 0 similarity
        stCalculateSimilarity = connection.prepareStatement("SELECT i.idDoc, SUM(q.weight*i.weight) as sim " +
                                                        "FROM QUERY q, SPATIA.INVERTEDINDEX i " +
                                                        "WHERE q.term=i.term " +
                                                        "GROUP BY i.idDoc " +
                                                        "HAVING sim>0 " +
                                                        "ORDER BY sim DESC");
    }
}
