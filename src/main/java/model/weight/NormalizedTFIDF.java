package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 26/09/16.
 *
 * Definition of the query to calculate the normalized TFIDF, using the formula:
 *      w(i,j)=(log(tf(i,j)+1)*IDF(j)/(∑(k=1,t)[(log(tf(i,k)+1)*IDF(k)])
 */
public class NormalizedTFIDF extends Weight {
    public NormalizedTFIDF(Connection connection) throws SQLException{
        super("Normalized TF-IDF", connection);

        //Calculation of normalized TFIDF for every term in every document in the collection
        stCalculateWeight = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX i SET i.weight=((log(i.tf+1)*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=i.term))/\n" +
                "(SELECT SUM(POWER(log(i2.tf+1)*t2.idf, 2)) FROM SPATIA.INVERTEDINDEX i2, SPATIA.TERM t2 WHERE i2.idDoc=i.idDoc AND t2.term=i2.term))");
        //Calculation of normalized TFIDF for every term in the current query
        stCalculateQueryWeight = connection.prepareStatement("UPDATE QUERY q SET q.weight=IFNULL(((log(q.tf+1)*(SELECT t.idf FROM SPATIA.TERM t WHERE t.term=q.term))/\n" +
                "(SELECT SUM(POWER(log(q2.tf+1)*t2.idf, 2)) FROM QUERY q2, SPATIA.TERM t2 WHERE t2.term=q2.term)), 0)");
    }
}
