package dataObjects;

import utilities.Tokenizer;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by Thagus on 03/09/16.
 */
public class Document implements Comparable<Document>{
    private int idDoc;
    private String title;
    private String journal;
    private String libraryNotes;
    private String authors;
    private String abstractText;
    private String keywords;
    private String classification;
    private String citations;

    private double similarity;

    public Document(int idDoc){
        this.idDoc = idDoc;
        title = "";
    }

    public void appendTitle(String string){
        title += " " + string.trim();
        title = title.trim();
        similarity = 0;
    }

    public void appendAuthor(String author){
        if(author == null)
            return;
        if(authors==null){
            authors = author.trim();
        } else {
            authors = authors + "; " + author.trim();
        }
    }

    public void setJournal(String journal){
        if(this.journal == null) {
            this.journal = journal.trim();
        }
        else {
            JOptionPane.showMessageDialog(null, "There is more than one journal entry for document idDoc: " + idDoc);
        }
    }

    public void setLibraryNotes(String libraryNotes){
        if(this.libraryNotes == null)
            this.libraryNotes = libraryNotes;
        else {
            JOptionPane.showMessageDialog(null, "There is more than one library note entry for document idDoc: " + idDoc);
        }
    }

    public void appendAbstract(String text){
        if(text==null)
            return;
        if(abstractText ==null){
            abstractText = text.trim();
        } else {
            abstractText += " " + text.trim();
        }
    }

    public void appendKeywords(String keywords) {
        if(keywords==null)
            return;
        if(this.keywords == null){
            this.keywords = keywords.trim();
        } else {
            this.keywords += " " + keywords.trim();
        }
    }

    public void appendClassification(String classification) {
        if(classification==null)
            return;
        if(this.classification == null){
            this.classification = classification.trim();
        } else {
            this.classification += "; " + classification.trim();
        }
    }

    public void appendCitations(String citations) {
        if(citations==null)
            return;
        if(this.citations == null){
            this.citations = citations.trim();
        }
        else{
            this.citations += "; " + citations.trim();
        }
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getIdDoc() { return idDoc; }

    public String getTitle() {
        return title;
    }

    public String getJournal() {
        return journal;
    }

    public String getLibraryNotes() { return libraryNotes; }

    public String getAuthors(){
        return authors;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getClassification() {
        return classification;
    }

    public String getCitations() {
        return citations;
    }

    public double getSimilarity() {
        return similarity;
    }

    public HashMap<String, Integer> countWords(HashMap<String, Integer> documentWordOccurrence){
        String text;
        if(abstractText!=null)
            text = title + " " + abstractText;
        else
            text = title;

        String[] splited = Tokenizer.tokenizeString(text);

        HashMap<String, Integer> wordCountLocal = new HashMap<>();

        for(String word : splited){
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

    @Override
    public int compareTo(Document o) {
        if(this.similarity == o.similarity)
            return 0;
        else if (this.similarity<o.similarity)
            return -1;
        else
            return 1;
    }
}
