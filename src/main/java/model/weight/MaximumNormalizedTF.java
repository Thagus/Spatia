package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 26/09/16.
 *
 * Based on the Maximum normalized term frequency as described in the following link: http://nlp.stanford.edu/IR-book/html/htmledition/maximum-tf-normalization-1.html
 * The used formula is:
 *      w(i,j)=0.4+0.6*(tf(i,j)/m(i))
 */
public class MaximumNormalizedTF extends Weight {
    public MaximumNormalizedTF(Connection connection) throws SQLException {
        super("Maximum normalized TF", connection);

        //Query to calculate the weights for every term in every document
        stCalculateWeight = connection.prepareStatement("UPDATE SPATIA.INVERTEDINDEX i SET i.weight=0.4 + (0.6)*(i.tf/(SELECT MAX(i2.tf) FROM SPATIA.INVERTEDINDEX i2 WHERE i2.idDoc=i.idDoc))");
        //Query to calculate the weight of every term in the current query, ensuring that we dont assign a null value
        stCalculateQueryWeight = connection.prepareStatement("UPDATE QUERY q SET q.weight=IFNULL(0.4 + (0.6)*(q.tf/(SELECT MAX(q2.tf) FROM QUERY q2)), 0)");
    }
}
