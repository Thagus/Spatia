package model.clustering;

import dataObjects.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ModelDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thagus on 24/10/16.
 */
public class Clustering {
    private HashMap<String, Cluster> clusters;
    private int maxLevel = 2;
    private Cluster root;

    private PreparedStatement stParents;

    public Clustering(Connection connection) throws SQLException {
        clusters = new HashMap<>();    //Initialize the clusters HashMap
        root = null;

        stParents = connection.prepareStatement("SELECT * FROM SPATIA.CLUSTER");
    }

    /**
     * Read clusters from database
     */
    public void readClusters(){
        try {
            ResultSet rs = stParents.executeQuery();

            while (rs.next()){
                String node = rs.getString(1);
                String parent = rs.getString(2);
                int level = rs.getInt(3);
                //String strategy = rs.getString(4);

                Cluster cluster = new Cluster(node);
                cluster.setLevel(level);
                clusters.put(node, cluster);

                Cluster parentCluster = clusters.get(parent);

                if(parentCluster!=null){
                    cluster.setParent(parentCluster);
                    parentCluster.addChild(cluster);
                    //System.out.println(parent + " - " + node);
                }
                else{
                    root = cluster;
                    System.out.println("Found the root!: " + node);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ObservableList<Document> getClusteredDocumentsFor(int docID){
        ObservableList<Document> result = FXCollections.observableArrayList();
        ArrayList<Integer> resultNames = new ArrayList<>();

        Cluster docCluster = clusters.get("D"+docID);

        resultNames.add(Integer.parseInt(docCluster.getCode().substring(1)));

        recursiveIterationUp(docCluster, resultNames);

        ModelDatabase db = ModelDatabase.instance();

        for(int id : resultNames){
            Document document = db.opDocuments.getDocument(id);
            result.add(document);
        }

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        findSuperclusterForDocuments(arrayList, 1);

        return result;
    }

    public void recursiveIterationUp(Cluster cluster, ArrayList<Integer> resultNames){
        //Get parent
        Cluster parent = cluster.getParent();

        //Get sibling
        if(parent!=null && parent.getLevel()> maxLevel){
            Cluster sibling = parent.getOtherChild(cluster);

            if(sibling.isLeaf()){
                //Its a document, we should add it to the results
                resultNames.add(Integer.parseInt(sibling.getCode().substring(1)));

                recursiveIterationUp(parent, resultNames);
            }
            else {
                postOrder(sibling, resultNames);
                Cluster parentOfParent = parent.getParent();

                if(parentOfParent!=null) {
                    recursiveIterationUp(parent, resultNames);
                }
            }
        }
    }

    /**
     * Traverse the tree in postOrder to find the leafs
     * @param root
     * @param resultNames
     */
    public void postOrder(Cluster root, ArrayList<Integer> resultNames) {
        if(root !=  null) {
            postOrder(root.getLeftChild(), resultNames);
            postOrder(root.getRightChild(), resultNames);

            if(root.isLeaf()) {
                resultNames.add(Integer.parseInt(root.getCode().substring(1)));
            }
        }
    }

    /**
     * PostOrder to find the clusters of a certain level
     * @param root
     * @param resultNames
     * @param level
     */
    public void postOrder(Cluster root, ArrayList<Cluster> resultNames, int level) {
        if(root !=  null && root.getLevel()<level) {
            postOrder(root.getLeftChild(), resultNames, level);
            postOrder(root.getRightChild(), resultNames, level);
        }
        else if(root !=  null && root.getLevel()==level){
            if(!root.isLeaf()) {
                resultNames.add(root);
            }
        }
    }

    /**
     * Counts how many documents are within the same clusters up to a certain level
     * @param documentIDs the documents we want to check if they are in the same cluster
     * @param level analyze 2^level clusters
     */
    public void findSuperclusterForDocuments(ArrayList<Integer> documentIDs, int level){
        //Find all the documents within a subtree of a certain level, use postOrder
        System.out.println("Starting search");

        ArrayList<Cluster> results = new ArrayList<>();

        postOrder(root, results, level);

        System.out.println("Results size: " + results.size());

        for(Cluster cluster : results){
            System.out.println("---" + cluster.getCode());
            ArrayList<Integer> documentClusterNames = new ArrayList<>();
            postOrder(cluster, documentClusterNames);

            for(int name : documentClusterNames){
                if(documentIDs.contains(name)){
                    System.out.println(name + " - " + cluster.getCode());
                }
            }
        }
    }

    /**
     * A method to get the corresponding CLuster for a document ID
     * @param id the document ID
     * @return the Cluster that corresponds to the searched document
     */
    public Cluster getDocumentCluster(int id){
        return clusters.get("D"+id);
    }
}
