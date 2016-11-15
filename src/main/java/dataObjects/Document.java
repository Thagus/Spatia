package dataObjects;

import utilities.TermExtractor;

import java.util.HashMap;

/**
 * Created by Thagus on 03/09/16.
 * Purpose: Store the document data
 */
public class Document implements Comparable<Document>{
    private String url;
    private String text;

    private double similarity;

    public Document(String url){
        this.url = url;
        this.text = "";
        this.similarity = 0;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /****************
     *  Getters     *
     ****************/
    public String getUrl() { return url; }
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
