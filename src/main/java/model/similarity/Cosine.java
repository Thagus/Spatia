package model.similarity;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Created by Thagus on 25/09/16.
 *
 * Calculation of the cosine using the formula:
 *      sim(d(i),q)=(∑[wd(i,j)*wq(j) ]) / √(|d(i)|*|q|)
 */
public class Cosine extends Similarity {

    public Cosine(Connection connection) throws SQLException {
        super("Cosine", connection);
        //Query definition to calculate cosine similarity, and return those documents that have more than 0 similarity to the query
        stCalculateSimilarity = connection.prepareStatement("SELECT i.idDoc, SUM((q.weight*i.weight)/SQRT((SELECT SUM(q1.weight) FROM QUERY q1)*(SELECT SUM(i2.weight) FROM SPATIA.INVERTEDINDEX i2 WHERE i2.idDoc=i.idDoc))) as sim\n" +
                "FROM QUERY q, SPATIA.INVERTEDINDEX i\n" +
                "WHERE q.term=i.term\n" +
                "GROUP BY i.idDoc\n" +
                "HAVING sim>0");
    }
}
