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
        stAddDocument = connection.prepareStatement("INSERT INTO SPATIA.DOCUMENT(idDoc,title,journal,libraryNotes,authors,abstractText,keywords,classification,citations) VALUES(?,?,?,?,?,?,?,?,?)");
        stGetDocument = connection.prepareStatement("SELECT * FROM SPATIA.DOCUMENT WHERE idDoc=?");
        stCountDocuments = connection.prepareStatement("SELECT COUNT(*) FROM SPATIA.DOCUMENT");
    }

    public boolean addDocument(int idDoc, String title, String journal, String libraryNotes, String authors, String abstractText, String keywords, String classification, String citations){
        try{
            stAddDocument.clearParameters();
            stAddDocument.setInt(1, idDoc);
            stAddDocument.setString(2, title);
            stAddDocument.setString(3, journal);
            stAddDocument.setString(4, libraryNotes);
            //The following might be null
            if(authors==null)
                stAddDocument.setNull(5, Types.VARCHAR);
            else
                stAddDocument.setString(5, authors);

            if(abstractText==null)
                stAddDocument.setNull(6, Types.VARCHAR);
            else
                stAddDocument.setString(6, abstractText);

            if(keywords==null)
                stAddDocument.setNull(7, Types.VARCHAR);
            else
                stAddDocument.setString(7, keywords);

            if(classification==null)
                stAddDocument.setNull(8, Types.VARCHAR);
            else
                stAddDocument.setString(8, classification);

            if(citations==null)
                stAddDocument.setNull(9, Types.VARCHAR);
            else
                stAddDocument.setString(9, citations);


            stAddDocument.executeUpdate();
            return true;
        } catch(SQLException e){
            //The insertion fails due to duplicate key
            if(e.getErrorCode()==23505){
                JOptionPane.showMessageDialog(null, "There is already a document with id: " + idDoc);
                return false;
            }

            //Unhandled error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error adding Document", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public Document getDocument(int idDoc){
        try {
            stGetDocument.clearParameters();
            stGetDocument.setInt(1, idDoc);

            ResultSet rs = stGetDocument.executeQuery();

            Document document = new Document(idDoc);
            boolean check = false;

            while(rs.next()){
                check = true;
                document.appendTitle(rs.getString("title"));
                document.setJournal(rs.getString("journal"));
                document.setLibraryNotes(rs.getString("libraryNotes"));
                document.appendAuthor(rs.getString("authors"));
                document.appendAbstract(rs.getString("abstractText"));
                document.appendKeywords(rs.getString("keywords"));
                document.appendClassification(rs.getString("classification"));
                document.appendCitations(rs.getString("citations"));
            }
            rs.close();

            if(!check){
                JOptionPane.showMessageDialog(null, "There is no document with id: " + idDoc);
                return null;
            }

            return document;
        } catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error getting Document with id: " + idDoc, JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

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
