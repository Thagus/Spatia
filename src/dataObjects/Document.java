package dataObjects;

import javax.swing.*;

/**
 * Created by Thagus on 03/09/16.
 */
public class Document {
    private int id;
    private String title;
    private String journal;
    private String authors;
    private String contentText;

    public Document(int id){
        this.id = id;
        title = "";
    }

    public void appendTitle(String string){
        title += string;
        title = title.trim();
    }

    public void appendAuthor(String author){
        if(authors==null){
            authors = author.trim();
        } else {
            authors = authors + "; " + author.trim();
        }
    }

    public void setJournal(String journal){
        if(this.journal==null) {
            this.journal = journal;
        }
        else {
            JOptionPane.showMessageDialog(null, "There is more than one journal entry for document id=" + id);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors(){
        return authors;
    }

    public String getJournal() {
        return journal;
    }
}
