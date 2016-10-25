package model;

import test.QueryObject;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Thagus on 25/10/16.
 */
public class TestsOperations {
    private PreparedStatement stGetRelevant, stGetQueries, stAddQuery, stAddRelevant;
    private PreparedStatement stCountQueries, stClearEmptyQueries;

    public TestsOperations(Connection connection) throws SQLException {
        //Prepare statements
        stGetRelevant = connection.prepareStatement("SELECT did FROM SPATIATESTS.RELEVANT WHERE qid=?");
        stGetQueries = connection.prepareStatement("SELECT * FROM SPATIATESTS.QUERIES");
        stAddQuery = connection.prepareStatement("INSERT INTO SPATIATESTS.QUERIES(qid,query) VALUES(?,?)");
        stAddRelevant = connection.prepareStatement("INSERT INTO SPATIATESTS.RELEVANT(qid,did) VALUES(?,?)");
        stClearEmptyQueries = connection.prepareStatement("DELETE FROM SPATIATESTS.QUERIES WHERE qid not in" +
                "(" +
                "SELECT DISTINCT r.qid FROM SPATIATESTS.RELEVANT r"+
                ")");
        stCountQueries = connection.prepareStatement("SELECT COUNT(*) FROM SPATIATESTS.QUERIES");
    }

    /**
     * Add a new query to the datbase
     * @param qid The query id
     * @param query The query text
     */
    public void addQuery(int qid, String query){
        try{
            stAddQuery.clearParameters();
            stAddQuery.setInt(1, qid);
            stAddQuery.setString(2, query);

            stAddQuery.executeUpdate();
        } catch (SQLException e) {
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There is already a query with id \"" + qid + "\"");
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding Query", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Add a relevant document id to a query
     * @param qid The query id
     * @param did The relevant document id
     */
    public void addRelevant(int qid, int did){
        try{
            stAddRelevant.clearParameters();
            stAddRelevant.setInt(1, qid);
            stAddRelevant.setInt(2, did);

            stAddRelevant.executeUpdate();
        } catch (SQLException e) {
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There is already a document with id \"" + did + "\" for query id \"" + qid + "\"");
            }
            //The insertion fails due to foreign key constraint failure
            else if(e.getErrorCode()==23506){
                JOptionPane.showMessageDialog(null, "There is no query with id: " + qid);
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding Relevant", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get all the queries from the databse
     * @return An ArrayList containing the QueryObjects that correspond to the queries in database
     */
    public ArrayList<QueryObject> getQueries(){
        try{
            ResultSet rs = stGetQueries.executeQuery();

            ArrayList<QueryObject> queries = new ArrayList<>();
            boolean check = false;

            while(rs.next()){
                check = true;
                queries.add(new QueryObject(rs.getInt("qid"), rs.getString("query"), getRelevant(rs.getInt("qid"))));
            }

            if(!check){
                JOptionPane.showMessageDialog(null, "There are no queries!", "Alert!", JOptionPane.ERROR_MESSAGE);
            }

            return queries;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting queries", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Get the relevant documents to a query
     * @param qid The id of the query
     * @return An ArrayList containing the ids of the relevant documents
     */
    private ArrayList<Integer> getRelevant(int qid) {
        try{
            stGetRelevant.clearParameters();
            stGetRelevant.setInt(1, qid);

            ResultSet rs = stGetRelevant.executeQuery();

            ArrayList<Integer> queries = new ArrayList<>();
            boolean check = false;

            while(rs.next()){
                check = true;
                queries.add(rs.getInt(1));
            }

            if(!check){
                System.out.println("There are no relevant documents for query id " + qid);
            }

            return queries;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting relevant documents for query id " + qid, JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public void deleteEmptyQueries(){
        try {
            stClearEmptyQueries.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public int countQueries(){
        try {
            ResultSet rs = stCountQueries.executeQuery();
            while (rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return -1;
    }
}
