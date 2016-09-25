package dataObjects;

/**
 * Created by Thagus on 05/09/16.
 */
public class Term {
    private int idDoc;
    private String term;
    private int tf;

    private double similarity;

    public Term(int idDoc, String term, int tf) {
        this.idDoc = idDoc;
        this.term = term;
        this.tf = tf;
    }

    public int getIdDoc() {
        return idDoc;
    }

    public String getTerm() {
        return term;
    }

    public int getTf() {
        return tf;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity){
        this.similarity = similarity;
    }
}
