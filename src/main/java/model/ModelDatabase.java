package model;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Thagus on 02/09/16.
 * Purpose: A singleton class for creating a connection to the database or creating the database if non existent
 */
public class ModelDatabase {
    private static ModelDatabase uniqueInstance;
    private Statement st;
    private Connection con;

    public InvertedIndexOperations opInvertedIndex;
    public DocumentOperations opDocuments;
    public ModelOperations opModel;

    private ModelDatabase() {
        try {
            Class.forName("org.h2.Driver");

            con = DriverManager.getConnection("jdbc:h2:./database/spatia", "spatia", "hi");
            st = con.createStatement();

            //Create the schema and tables if they don't already exists
            //clearDB();
            createSchema();
            createTables();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error, database driver not found", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error creating connection to database. Please, restart the program and ensure that there's no other instance running", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Singleton instancing
     * @return The single instance of the class
     */
    public static synchronized ModelDatabase instance(){
        if (uniqueInstance==null) {
            uniqueInstance = new ModelDatabase();   //Create connction to the database
            uniqueInstance.createOperations();      //Create operations
            return uniqueInstance;
        }  else {
            return uniqueInstance;
        }
    }

    /**
     * Create the operations objects to handle requests
     */
    private void createOperations(){
        try {
            opInvertedIndex = new InvertedIndexOperations(con);
            opDocuments = new DocumentOperations(con);
            opModel = new ModelOperations(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create database schema
     */
    private void createSchema(){
        try {
            st.execute("CREATE SCHEMA SPATIA");
        } catch (SQLException e) {
            //System.out.println("Error creating schema:");
            //e.printStackTrace();
        }
    }

    /**
     * Create database tables
     */
    private void createTables(){
        //Create Documents table
        try {
            st.execute("CREATE TABLE SPATIA.DOCUMENT(" +
                    "idDoc INTEGER NOT NULL," +         //Must have an ID
                    "title VARCHAR NOT NULL," +         //Must have a title
                    "journal VARCHAR NOT NULL," +       //It must belong to a journal
                    "libraryNotes VARCHAR NOT NULL," +   //There must be library notes
                    "authors VARCHAR," +                //There can be no authors
                    "abstractText VARCHAR," +           //Might have an abstract
                    "keyWords VARCHAR," +               //There might be keyWords
                    "classification VARCHAR," +         //There might be classification
                    "citations VARCHAR," +              //There might be citations
                    "PRIMARY KEY (idDoc)" +
                    ")");
        } catch (SQLException e) {
            //System.out.println("Error creating DOCUMENT table:");
            //e.printStackTrace();
        }

        //Create InvertedIndex table
        try {
            st.execute("CREATE TABLE SPATIA.INVERTEDINDEX(" +
                    "idDoc INTEGER NOT NULL," +
                    "term VARCHAR NOT NULL," +
                    "tf INTEGER NOT NULL," +
                    "FOREIGN KEY(idDoc) REFERENCES DOCUMENT(idDoc) ON DELETE CASCADE," +
                    "PRIMARY KEY (idDoc,term)" +
                    ")");
        } catch (SQLException e) {
            //System.out.println("Error creating TERMS table:");
            //e.printStackTrace();
        }

        //Create Terms table
        try {
            st.execute("CREATE TABLE SPATIA.TERM(" +
                    "term VARCHAR NOT NULL," +
                    "idf DOUBLE NOT NULL," +
                    "weight DOUBLE NOT NULL," +
                    "PRIMARY KEY (term)" +
                    ")");
        } catch (SQLException e) {
            //System.out.println("Error creating IDF table:");
            //e.printStackTrace();
        }

        //Create Query table in memory
        try {
            st.execute("CREATE MEMORY TEMPORARY TABLE QUERY(" +
                    "term VARCHAR NOT NULL," +
                    "tf INTEGER NOT NULL," +
                    "PRIMARY KEY (term,TF)" +
                    ") NOT PERSISTENT");
        } catch (SQLException e) {
            //System.out.println("Error creating Query table:");
            //e.printStackTrace();
        }
    }

    /**
     * Delete the database tables
     */
    public void clearDB(){
        try {
            st.execute("DROP TABLE SPATIA.DOCUMENT");
            st.execute("DROP TABLE SPATIA.TERMS");
            st.execute("DROP TABLE SPATIA.IDF");
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
        con.close();
    }
}
