package dataObjects;

import utilities.TermExtractor;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 03/09/16.
 * Purpose: Store the document data
 */
public class Document implements Comparable<Document>{
    private String idDoc;
    private String text;

    private double similarity;

    public Document(String idDoc){
        this.idDoc = idDoc;
        this.text = "";
        this.similarity = 0;
    }

    /**
     * Add a String to the text
     * @param text the string to add
     */
    public void appendText(String text){
        if(text==null)
            return;

        if(text.equals("\n") && this.text.length()!=0){
            this.text += "\n" + text;
        }
        else {
            this.text += " " + text.trim();
        }
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /****************
     *  Getters     *
     ****************/
    public String getIdDoc() { return idDoc; }
    public String getText() {
        return text;
    }
    public double getSimilarity() {
        return similarity;
    }

    /**
     * Count the terms contained in the document, and add those terms to a global list of terms
     * @return The HashMap containing the terms and their TF
     */
    public HashMap<String, Integer> countWords(){
        return TermExtractor.extractTerms(this.text);
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
