package test;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Thagus on 11/09/16.
 */
public class TestsDatabase {
    private static TestsDatabase uniqueInstance;

    private Statement st;
    private Connection con;

    private PreparedStatement stGetRelevant, stGetQueries, stAddQuery, stAddRelevant;

    private TestsDatabase(){
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:./database/spatiaTests", "spatia", "hi");
            st = con.createStatement();

            //Create the schema and tables if they don't already exists
            //clearDB();
            createSchema();
            createTables();

            //Prepare statements
            stGetRelevant = con.prepareStatement("SELECT did FROM SPATIATESTS.RELEVANT WHERE qid=?");
            stGetQueries = con.prepareStatement("SELECT * FROM SPATIATESTS.QUERIES");
            stAddQuery = con.prepareStatement("INSERT INTO SPATIATESTS.QUERIES(qid,query) VALUES(?,?)");
            stAddRelevant = con.prepareStatement("INSERT INTO SPATIATESTS.RELEVANT(qid,did) VALUES(?,?)");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error, database driver not found", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error creating connection to database. Please, restart the program and ensure that there's no other instance running", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static TestsDatabase instance() {
        if (uniqueInstance==null) {
            try {
                uniqueInstance = new TestsDatabase();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return uniqueInstance;
        }  else {
            return uniqueInstance;
        }
    }



    /**
     * Create the operations to handle requests
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


    /**
     * Create database schema
     */
    private void createSchema(){
        try {
            st.execute("CREATE SCHEMA SPATIATESTS");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Create database tables
     */
    private void createTables(){
        //Create Queries table
        try {
            st.execute("CREATE TABLE SPATIATESTS.QUERIES(" +
                    "qid INTEGER NOT NULL," +         //Must have an ID
                    "query VARCHAR NOT NULL," +       //Must have a query
                    "PRIMARY KEY (qid)" +
                    ")");
        } catch (SQLException e) {
            //e.printStackTrace();
        }

        //Create Relevant documents for query table
        try {
            st.execute("CREATE TABLE SPATIATESTS.RELEVANT(" +
                    "qid INTEGER NOT NULL," +
                    "did INTEGER NOT NULL," +
                    "FOREIGN KEY(qid) REFERENCES QUERIES(qid) ON DELETE CASCADE," +
                    "PRIMARY KEY (qid,did)" +
                    ")");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Delete the database tables
     */
    public void clearDB(){
        try {
            st.execute("DROP TABLE SPATIATESTS.QUERIES");
            st.execute("DROP TABLE SPATIATESTS.RELEVANT");
        } catch (SQLException e) {
            //System.out.println("Error cleaning DB:");
            //e.printStackTrace();
        }
    }

    /**
     * Close the connection to the database
     */
    public void close() throws SQLException {
        st.close();
        stGetRelevant.close();
        stGetQueries.close();
        stAddRelevant.close();
        stAddQuery.close();
        con.close();
    }

}
