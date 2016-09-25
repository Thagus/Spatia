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
 * Created by Thagus on 24/09/16.
 */
public class DotProduct extends Similarity {
    private PreparedStatement calculateDotProduct;
    private PreparedStatement addQuery, clearQuery;

    public DotProduct(Connection connection) throws SQLException {
        similarityMethodName = "Dot product";
        calculateDotProduct = connection.prepareStatement("SELECT i.idDoc, SUM(q.tf * t.weight * i.tf * t.weight) as TFIDF\n" +
                "FROM QUERY q, SPATIA.INVERTEDINDEX i, SPATIA.TERM t\n" +
                "WHERE q.term = t.term\n" +
                "AND i.term = t.term\n" +
                "GROUP BY i.idDoc\n" +
                "HAVING TFIDF > 0");

        addQuery = connection.prepareStatement("INSERT INTO QUERY(term,tf) VALUES(?,?)");
        clearQuery = connection.prepareStatement("TRUNCATE TABLE QUERY");
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

            //Execute calculation of similarity
            ResultSet rs = calculateDotProduct.executeQuery();

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