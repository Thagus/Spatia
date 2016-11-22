package dataObjects;

import utilities.LanguageDetector;
import utilities.TermExtractor;

import java.util.HashMap;

/**
 * Created by Thagus on 03/09/16.
 * Purpose: Store the document data
 */
public class Document implements Comparable<Document>{
    private String url;
    private String title;
    private String text;
    private String language;

    private double similarity;

    public Document(String url){
        this.url = url;
        this.similarity = 0;
    }

    public Document(String url, String title, String text) {
        this.url = url;

        setTitle(title);
        setText(text);

        this.language = LanguageDetector.detectLanguage(this.text);

        this.similarity = 0;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setText(String text) {
        this.text = text.replaceAll("\\s+", " ");
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /****************
     *  Getters     *
     ****************/
    public String getUrl() { return url; }
    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
    public String getLanguage() {
        return language;
    }
    public double getSimilarity() {
        return similarity;
    }

    /**
     * Count the terms contained in the document, and add those terms to a global list of terms
     * @return The HashMap containing the terms and their TF
     */
    public HashMap<String, Integer> countWords(){
        return TermExtractor.extractTerms(this.text, language);
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
