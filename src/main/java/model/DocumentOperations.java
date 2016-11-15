package model;

import dataObjects.Document;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Created by Thagus on 04/09/16.
 */
public class DocumentOperations {
    private PreparedStatement stAddDocument;
    private PreparedStatement stGetDocument;
    private PreparedStatement stCountDocuments;

    protected DocumentOperations(Connection connection) throws SQLException {
        //Create prepared statements
        stAddDocument = connection.prepareStatement("INSERT INTO SPATIA.DOCUMENT(url,title,text) VALUES(?,?,?)");
        stGetDocument = connection.prepareStatement("SELECT * FROM SPATIA.DOCUMENT WHERE url=?");
        stCountDocuments = connection.prepareStatement("SELECT COUNT(*) FROM SPATIA.DOCUMENT");
    }

    /**
     * Adds a document tuple to the database
     *
     * @param url the id of the document
     * @param text the text in the document
     */
    public boolean addDocument(String url, String title, String text){
        try{
            stAddDocument.clearParameters();
            stAddDocument.setString(1, url);
            if(title==null){
                stAddDocument.setString(2, url);
            } else {
                stAddDocument.setString(2, title);
            }
            stAddDocument.setString(3, text);

            stAddDocument.executeUpdate();
            return true;
        } catch(SQLException e){
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There is already a document with id: " + url);
                return false;
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding Document", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Gets a document by id
     *
     * @param url the id of the document we want to retrieve
     * @return the Document object that stores the document info
     */
    public Document getDocument(String url){
        try {
            stGetDocument.clearParameters();
            stGetDocument.setString(1, url);

            ResultSet rs = stGetDocument.executeQuery();

            Document document = new Document(url);
            boolean check = false;

            while(rs.next()){
                check = true;
                document.setTitle(rs.getString("title"));
                document.setText(rs.getString("text"));
            }
            rs.close();

            if(!check){
                JOptionPane.showMessageDialog(null, "There is no document with id: " + url);
                return null;
            }

            return document;
        } catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting Document with id: " + url, JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Counts the total number of documents in teh database
     * @return the numbe of documents
     */
    public int countDocuments(){
        try {
            ResultSet rs = stCountDocuments.executeQuery();

            int count = 0;

            while(rs.next()){
                count = rs.getInt(1);
            }
            rs.close();

            if(count<=0){
                JOptionPane.showMessageDialog(null, "There are no documents");
            }

            return count;
        } catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting documents", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
}
