package dataObjects;

/**
 * Created by Thagus on 05/09/16.
 */
public class DocumentTerm {
    private int idDoc;
    private String title;
    private String journal;
    private int tf;
    private double tfidf;

    public DocumentTerm(int idDoc, String title, String journal, int tf, double tfidf) {
        this.idDoc = idDoc;
        this.title = title;
        this.journal = journal;
        this.tf = tf;
        this.tfidf = tfidf;
    }

    public int getIdDoc() {
        return idDoc;
    }

    public String getTitle() {
        return title;
    }

    public String getJournal() {
        return journal;
    }

    public int getTf() {
        return tf;
    }

    public double getTfidf() {
        return tfidf;
    }
}
