package dataObjects;

import utilities.Tokenizer;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thagus on 03/09/16.
 * Purpose: Store the document data
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

    /**
     * Add a string to the title
     * @param string The string that will be added to the title
     */
    public void appendTitle(String string){
        title += " " + string.trim();
        title = title.trim();
        similarity = 0;
    }

    /**
     * Add an author to the authors string
     * @param author
     */
    public void appendAuthor(String author){
        if(author == null)  //Handle null values
            return;
        if(authors==null){
            authors = author.trim();
        } else {
            authors = authors + "; " + author.trim();
        }
    }

    /**
     * Set the journal attribute
     * @param journal The attribute' data
     */
    public void setJournal(String journal){
        if(this.journal == null) {
            this.journal = journal.trim();
        }
        else {
            JOptionPane.showMessageDialog(null, "There is more than one journal entry for document idDoc: " + idDoc);
        }
    }

    /**
     * Set the library notes attribute
     * @param libraryNotes The value of the attribute
     */
    public void setLibraryNotes(String libraryNotes){
        if(this.libraryNotes == null)
            this.libraryNotes = libraryNotes;
        else {
            JOptionPane.showMessageDialog(null, "There is more than one library note entry for document idDoc: " + idDoc);
        }
    }

    /**
     * Add a line to the abstract of the document
     * @param text The line to be added to the abstract
     */
    public void appendAbstract(String text){
        if(text==null)      //Handle null values
            return;
        if(abstractText ==null){
            abstractText = text.trim();
        } else {
            abstractText += " " + text.trim();
        }
    }

    /**
     * Append a line of keywords
     * @param keywords The line of keywords to be added
     */
    public void appendKeywords(String keywords) {
        if(keywords==null)      //Handle null values
            return;
        if(this.keywords == null){
            this.keywords = keywords.trim();
        } else {
            this.keywords += " " + keywords.trim();
        }
    }

    /**
     * Append classifications to teh attribute
     * @param classification the classifications to be added
     */
    public void appendClassification(String classification) {
        if(classification==null)    //Handle null values
            return;
        if(this.classification == null){
            this.classification = classification.trim();
        } else {
            this.classification += "; " + classification.trim();
        }
    }

    /**
     * Add a line of citations to the attribute
     * @param citations The line that will be added
     */
    public void appendCitations(String citations) {
        if(citations==null)     //Handle null values
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

    /****************
     *  Getters     *
     ****************/

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

    /**
     * Count the terms contained in the document, and add those terms to a global list of terms
     * @param documentWordOccurrence The global HashMap of terms
     * @return The HashMap containing the terms and their TF
     */
    public HashMap<String, Integer> countWords(HashMap<String, Integer> documentWordOccurrence){
        String text;
        if(abstractText!=null)
            text = title + " " + abstractText;
        else
            text = title;

        ArrayList<String> splited = Tokenizer.tokenizeString(text);

        HashMap<String, Integer> wordCountLocal = new HashMap<>();

        for(String word : splited){
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

    /**
     * Compare two Documents
     * @param o The document to compare to
     * @return 0 if they are equal, -1 if this document is smaller, 1 if its bigger in similarity
     */
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
