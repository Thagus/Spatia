package model;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Thagus on 02/09/16.
 */
public class ModelDatabase {
    private static ModelDatabase uniqueInstance;
    private Statement st;
    private Connection con;

    public TermOperations opTerm;

    private ModelDatabase() {
        try {
            Class.forName("org.h2.Driver");
            //String addr = System.getProperty("user.home") + "\\.spatia\\spatia";
            con = DriverManager.getConnection("jdbc:h2:./database/spatia", "spatia", "hi");
            st = con.createStatement();

            //clearDB();
            //createSchema();
            //createTables();

            createOperations();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error, database driver not found", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error creating connection to database. Please, restart the program and ensure that there's no other instance running", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static synchronized ModelDatabase instance(){
        if (uniqueInstance==null) {
            try {
                uniqueInstance = new ModelDatabase();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return uniqueInstance;
        }  else {
            return uniqueInstance;
        }
    }

    private void createOperations(){
        try {
            opTerm = new TermOperations(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createSchema(){
        try {
            st.execute("CREATE SCHEMA SPATIA");
        } catch (SQLException e) {
            System.out.println("Error creating schema:");
            e.printStackTrace();
        }
    }

    private void createTables(){
        //Create Documents table
        try {
            st.execute("CREATE TABLE SPATIA.DOCUMENT(" +
                    "idDoc INTEGER NOT NULL," +         //Must have an ID
                    "title VARCHAR NOT NULL," +         //Must have a title
                    "journal VARCHAR NOT NULL," +       //It must belong to a journal
                    "libraryInfo VARCHAR NOT NULL," +   //There must be library info
                    "authors VARCHAR," +                //There can be no authors
                    "abstractText VARCHAR," +           //Might have an abstract
                    "keyWords VARCHAR," +               //There might be keyWords
                    "chapters VARCHAR," +               //There might be chapters
                    "citations VARCHAR," +              //There might be citations
                    "PRIMARY KEY (idDoc)" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Error creating DOCUMENT table:");
            e.printStackTrace();
        }

        //Create Terms table
        try {
            st.execute("CREATE TABLE SPATIA.TERMS(" +
                    "idDoc INTEGER NOT NULL," +
                    "term VARCHAR NOT NULL," +
                    "tf INTEGER NOT NULL," +
                    "tfidf FLOAT," +    //Can be null when first creating the term
                    "FOREIGN KEY(idDoc) REFERENCES DOCUMENT(idDoc) ON DELETE CASCADE," +
                    "PRIMARY KEY (idDoc,term)" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Error creating TERMS table:");
            e.printStackTrace();
        }

        //Create IDF table
        try {
            st.execute("CREATE TABLE SPATIA.IDF(" +
                    "term VARCHAR NOT NULL," +
                    "numDocs INTEGER NOT NULL," +
                    "idf FLOAT NOT NULL," +
                    "PRIMARY KEY (term)" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Error creating IDF table:");
            e.printStackTrace();
        }
    }

    public void clearDB(){
        try {
            st.execute("DROP TABLE SPATIA.DOCUMENT");
            st.execute("DROP TABLE SPATIA.TERMS");
            st.execute("DROP TABLE SPATIA.IDF");
        } catch (SQLException e) {
            System.out.println("Error cleaning DB:");
            e.printStackTrace();
        }
    }

    public void close(){

    }
}
