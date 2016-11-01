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
    private HashMap<String, HashMap<String, Cluster>> clusters;
    private int maxLevel = 2;
    private HashMap<String, Cluster> roots;

    private PreparedStatement stParents;

    public Clustering(Connection connection) throws SQLException {
        clusters = new HashMap<>();    //Initialize the clusters HashMap
        roots = new HashMap<>();

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
                String strategy = rs.getString(4);

                Cluster cluster = new Cluster(node);
                cluster.setLevel(level);

                HashMap<String, Cluster> stringClusterHashMap = clusters.get(strategy);
                if(stringClusterHashMap==null){
                    stringClusterHashMap = new HashMap<>();
                    clusters.put(strategy, stringClusterHashMap);
                }

                stringClusterHashMap.put(node, cluster);

                Cluster parentCluster = stringClusterHashMap.get(parent);

                if(parentCluster!=null){
                    cluster.setParent(parentCluster);
                    parentCluster.addChild(cluster);
                }
                else{
                    roots.put(strategy, cluster);
                    System.out.println("Found the roots!: " + node);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Test clustering effectiveness
        // knowing that Medline documents have ID greater than 3204, and CACM documents are equal and below that id number

        String[] strategies = {"Complete linkage", "Single linkage", "Average linkage"};

        for(String strategy:strategies) {
            System.out.println("\n" + strategy);
            //Find superclusters
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i = 3205; i <= 4237; i++) {
                arrayList.add(i);
            }
            findSuperclusterForDocuments(arrayList, 3, strategy);
            System.out.println("---------------------");

            arrayList = new ArrayList<>();
            for (int i = 1; i <= 3204; i++) {
                arrayList.add(i);
            }
            findSuperclusterForDocuments(arrayList, 3, strategy);
        }
    }

    /**
     * Get the documents that are in the same cluster as the given document with a particular similarity strategy
     * @param docID the document id from where we start
     * @param strategy the similarity strategy we will use to find the other documents
     * @return an ArrayList containing the found documents in order
     */
    public ObservableList<Document> getClusteredDocumentsFor(int docID, String strategy){
        ObservableList<Document> result = FXCollections.observableArrayList();
        ArrayList<Integer> resultNames = new ArrayList<>();

        Cluster docCluster = clusters.get(strategy).get("D"+docID);

        resultNames.add(Integer.parseInt(docCluster.getCode().substring(1)));

        recursiveIterationUp(docCluster, resultNames);

        ModelDatabase db = ModelDatabase.instance();

        for(int id : resultNames){
            Document document = db.opDocuments.getDocument(id);
            result.add(document);
        }

        return result;
    }

    /**
     * A method to traverse the tree going upwards from a leaf
     * @param cluster the child cluster
     * @param resultNames the ArrayList where we will add the found leaf clusters
     */
    private void recursiveIterationUp(Cluster cluster, ArrayList<Integer> resultNames){
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
     * @param root the starting cluster
     * @param resultNames the ArrayList where we will add the found leafs
     */
    private void postOrder(Cluster root, ArrayList<Integer> resultNames) {
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
     * @param root the starting cluster
     * @param resultNames the ArrayList where we will add the found leafs
     * @param level the level we want the leafs for
     */
    private void postOrder(Cluster root, ArrayList<Cluster> resultNames, int level) {
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
    private void findSuperclusterForDocuments(ArrayList<Integer> documentIDs, int level, String strategy){
        //Find all the documents within a subtree of a certain level, use postOrder
        System.out.println("Starting search using " + strategy + " up to " + level + " levels");

        ArrayList<Cluster> results = new ArrayList<>();

        postOrder(roots.get(strategy), results, level);

        System.out.println("Results size: " + results.size());

        for(Cluster cluster : results){
            System.out.println("---" + cluster.getCode());
            ArrayList<Integer> documentClusterNames = new ArrayList<>();
            postOrder(cluster, documentClusterNames);

            int count = 0;

            System.out.println("Contains " + documentClusterNames.size() + " documents");

            for(int name : documentClusterNames){
                if(documentIDs.contains(name)){
                    count++;
                }
            }

            System.out.println("Contains " + count + " of the documents");
        }
    }
}
