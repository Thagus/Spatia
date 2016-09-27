package model.similarity;

import dataObjects.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ModelDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 25/09/16.
 */
public class Cosine extends Similarity {
    private PreparedStatement calculateCosine;

    public Cosine(Connection connection) throws SQLException {
        super("Cosine", connection);

        calculateCosine = connection.prepareStatement("SELECT i.idDoc, SUM((q.weight*i.weight)/((SELECT SUM(q1.weight) FROM QUERY q1)*(SELECT SUM(i2.weight) FROM SPATIA.INVERTEDINDEX i2 WHERE i2.idDoc=i.idDoc))) as sim\n" +
                "FROM QUERY q, SPATIA.INVERTEDINDEX i\n" +
                "WHERE q.term=i.term\n" +
                "GROUP BY i.idDoc\n" +
                "HAVING sim>0");
    }

    @Override
    public ObservableList<Document> calculateSimilarity(HashMap<String, Integer> wordCount) {
        try{
            //Insert query terms to memory table
            for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
                addQuery.clearParameters();
                addQuery.setString(1, entry.getKey());
                addQuery.setInt(2, entry.getValue());

                addQuery.executeUpdate();
            }

            //Calculate the query weight
            super.calculateQueryWeight();

            //Execute calculation of similarity
            ResultSet rs = calculateCosine.executeQuery();

            //Create Document objects
            ObservableList<Document> searchResult = FXCollections.observableArrayList();
            ModelDatabase db = ModelDatabase.instance();

            while (rs.next()){
                Document document = db.opDocuments.getDocument(rs.getInt(1));
                document.setSimilarity(rs.getDouble(2));

                searchResult.add(document);
            }


            //Clear Query database
            clearQuery.executeUpdate();

            return  searchResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
