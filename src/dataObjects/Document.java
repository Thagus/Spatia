package dataObjects;

import javafx.util.Pair;

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
    private String abstractText;

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

    public void appendAbstract(String text){
        if(abstractText ==null){
            abstractText = text.trim();
        } else {
            abstractText += " " + text.trim();
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

    public HashMap<String, Integer> countWords(HashMap<String, Integer> documentWordOccurrence){
        String text = title + " " + abstractText;
        String lowercase = text.toLowerCase();
        //lowercase = lowercase.replaceAll("[()|\"]","");
        //lowercase = lowercase.replaceAll("","");
        //String[] spplited = lowercase.split("(\\s*(:|,|\\s)\\s*)|(\\.\\s+)|(\\.{2,})|\\.(?!\\S)|\\p{P}");

        //Remove numbers
        lowercase = lowercase.replaceAll("\\d","");
        //Split at spaces or punctuation
        String[] spplited = lowercase.split("(\\s+)|\\p{Punct}");

        HashMap<String, Integer> wordCountLocal = new HashMap<>();

        for(String word : spplited){
            if(word.length()<=2)    //Filter void(0) words, and those with length 1 or 2
                continue;

            //Local count
            Integer count = wordCountLocal.get(word);
            if(count==null){
                count = 0;

                //When first counting the word, add it to the global document word count
                //Global document word count
                Integer countGl = documentWordOccurrence.get(word);

                if(countGl==null){
                    countGl = 0;
                }
                documentWordOccurrence.put(word, countGl+1);
            }
            wordCountLocal.put(word, count+1);
        }

        return wordCountLocal;
    }
}
