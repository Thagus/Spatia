package test;

import java.util.ArrayList;

/**
 * Created by Thagus on 11/09/16.
 */
public class QueryObject {
    private int qid;
    private String query;
    private ArrayList<Integer> relevantDocuments;
    private ArrayList<Integer> documentRetrieved;

    public QueryObject(int qid){
        this.qid = qid;
        //this.relevantDocuments = new ArrayList<>();
    }

    public QueryObject(int qid, String query) {
        this.qid = qid;
        this.query = query;
        this.relevantDocuments = new ArrayList<>();
        this.documentRetrieved = new ArrayList<>();
    }

    public int getQid() {
        return qid;
    }

    public String getQuery() {
        return query;
    }

    public ArrayList<Integer> getRelevantDocuments() {
        return relevantDocuments;
    }

    public void appendQuery(String line){
        if(line==null)  //Handle nulls
            return;
        if(query==null) //We dont have a query String yet
            query = line;
        else            //We already have a query
            query += " " + line;
    }
}
