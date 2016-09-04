package dataObjects;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

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

    //Count terms while adding?

    public void appendTitle(String string){
        title += " " + string.trim();
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
            this.journal = journal.trim();
        }
        else {
            JOptionPane.showMessageDialog(null, "There is more than one journal entry for document id=" + id);
        }
    }

    public void appendText(String text){
        if(contentText==null){
            contentText = text.trim();
        } else {
            contentText += " " + text.trim();
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

    public void countWords(HashMap<String, Integer> wordOccurrence){
        String text = title + " " + contentText;
        String lowercase = text.toLowerCase();
        //lowercase = lowercase.replaceAll("[()|\"]","");
        //lowercase = lowercase.replaceAll("","");
        //String[] spplited = lowercase.split("(\\s*(:|,|\\s)\\s*)|(\\.\\s+)|(\\.{2,})|\\.(?!\\S)|\\p{P}");

        //Remove numbers
        lowercase = lowercase.replaceAll("\\d","");
        //Split at spaces or punctuation
        String[] spplited = lowercase.split("(\\s+)|\\p{Punct}");

        for(String word : spplited){
            if(word.length()<=1)
                continue;
            //System.out.println(word);
            Integer count = wordOccurrence.get(word);
            if(count==null){
                count = 0;
            }
            wordOccurrence.put(word, count+1);
        }
    }
}
