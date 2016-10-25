package model.clustering;

import java.util.HashMap;

/**
 * Created by Thagus on 24/10/16.
 */
public class ClusterHolder {
    private HashMap<String, Cluster> documents; //A HashMap that contains the document clusters, and indexed by their document names
    private Cluster root;

    public ClusterHolder(){
        documents = new HashMap<>();    //Initialize the documents HashMap
    }

    /**
     * Read clusters from database
     */
    public void readClusters(){

    }

    /**
     * A method to get the corresponding CLuster for a document ID
     * @param id the document ID
     * @return the Cluster that corresponds to the searched document
     */
    public Cluster getDocumentCluster(int id){
        return documents.get("D"+id);
    }
}
