package model.weight;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Thagus on 26/09/16.
 */
public class NormalizedTFIDF extends Weight {
    public NormalizedTFIDF(Connection connection) throws SQLException{
        super("Normalized TF-IDF", connection);

        //[(log tfij + 1) * idfj] / [Σj=1,t [(log tfij + 1) * idfj]2]
        stCalculateWeight = connection.prepareStatement("MERGE INTO SPATIA.TERM(term,idf) " +
                "SELECT t.term, ((log(max(i.tf) + 1)*t.idf)/(sum(power(log(i.tf + 1)*t.idf, 2)))) as weight FROM SPATIA.TERM t NATURAL JOIN SPATIA.INVERTEDINDEX i GROUP BY t.term HAVING weight>0");
    }
}
