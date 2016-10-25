package model.clustering;

import model.clustering.strategies.LinkageStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HierarchicalClustering {
    private HashMap<String, Cluster> documents; //A HashMap that contains the document clusters, and indexed by their document names

    /**
     * Initialize the creation of the clusters
     * @param similarities a condensed matrix containing the similarities between documents
     * @param documentNames an array containing the document names
     * @param linkageStrategy the strategy object that will be use to recalculate similarities
     * @return the root of the cluster hierarchy
     */
    public Cluster performClustering(ArrayList<Double> similarities, ArrayList<String> documentNames, LinkageStrategy linkageStrategy) {
		/* Validate the received arguments */
        if (similarities == null || similarities.size() == 0) { //We must have at least one value in the similarities condensed matrix
            throw new IllegalArgumentException("Invalid similarities matrix");
        }
        if (similarities.size() != documentNames.size()*(documentNames.size()-1)/2) { //The number of entries in the condensed matrix must match for the number of documents
            throw new IllegalArgumentException("Invalid cluster name array or similarities matrix");
        }
        if (linkageStrategy == null) {  //We must have a linkage strategy
            throw new IllegalArgumentException("Undefined linkage strategy");
        }


		documents = new HashMap<>();    //Initialize the documents HashMap

        List<Cluster> clusters = createClusters(documentNames); //Create the base clusters based on the documents array
        SimilarityMap linkages = createLinkages(similarities, clusters);    //Create the first links between clusters using the similarity condensed matrix

        //Give the hierarchy builder the initial clusters with the similarity map
        HierarchyBuilder builder = new HierarchyBuilder(clusters, linkages);

        //While there is more than one cluster, keep making clusters
        while (!builder.isTreeComplete()) {
            builder.agglomerate(linkageStrategy);
        }

        //Return the root of the hierarchy
        return builder.getRootCluster();
    }

    /**
     * Creates the similarity map based on the given similarities condensed matrix
     * @param similarities the similarities condensed matrix
     * @param clusters the list of initial clusters that have their similarities in the previous matrix
     * @return the built similarity map
     */
    private SimilarityMap createLinkages(ArrayList<Double> similarities, List<Cluster> clusters) {
        SimilarityMap linkages = new SimilarityMap();
        for (int col = 0; col < clusters.size(); col++) {
            Cluster cluster_col = clusters.get(col);
            for (int row = col + 1; row < clusters.size(); row++) {
                ClusterPair link = new ClusterPair();
                Double d = similarities.get(accessFunction(row, col, clusters.size()));
                link.setLinkSimilarity(d);
                link.setlCluster(cluster_col);
                link.setrCluster(clusters.get(row));
                linkages.add(link);
            }
        }
        return linkages;
    }

    /**
     * Create the initial clusters array based on the document names
     * @param clusterNames the names of the documents
     * @return the array containing the created clusters
     */
    private ArrayList<Cluster> createClusters(ArrayList<String> clusterNames) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (String clusterName : clusterNames) {
            Cluster cluster = new Cluster(clusterName);
            clusters.add(cluster);
            documents.put(clusterName, cluster);
        }
        return clusters;
    }

    /**
     * A method to convert from normal matrix coordinates to the position in a condensed matrix on an array
     * @param i the row of the matrix
     * @param j the column of the matrix
     * @param n the N of an NxN matrix
     * @return the position of the element in the condensed matrix array
     */
    private static int accessFunction(int i, int j, int n) {
        return n*j - j*(j+1)/2 + i-1-j;
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
