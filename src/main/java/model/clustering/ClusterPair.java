package model.clustering;

public class ClusterPair implements Comparable<ClusterPair> {
    private static long clusterCount = 0;

    private Cluster lCluster;
    private Cluster rCluster;

	private Double linkSimilarity;  //The similarity between the clusters in this ClusterPair

    /**
     * Getters
     */
    public Cluster getlCluster() {
        return lCluster;
    }
    public Cluster getrCluster() {
        return rCluster;
    }
    public Double getLinkSimilarity() {
        return linkSimilarity;
    }
    /**
     * Setters
     */
    public void setlCluster(Cluster lCluster) {
        this.lCluster = lCluster;
    }
    public void setrCluster(Cluster rCluster) {
        this.rCluster = rCluster;
    }
	public void setLinkSimilarity(Double similarity) {
        this.linkSimilarity = similarity;
    }

    @Override
    public int compareTo(ClusterPair o) {
        //This object is greater than another if it has a smaller similarity
        //  so if we use the Double default comparator, we must compare against the other object similarity
        //  this way we get the one with higher similarity first in a PriorityQueue
        return o.getLinkSimilarity().compareTo(getLinkSimilarity());
    }

    /**
     * Create a CLuster that agglomerates the clusters in this ClusterPair
     * @param code The code for the new Cluster
     * @return The new Cluster
     */
    public Cluster agglomerate(String code) {
        if (code == null) { //If the given code is null, it means the new Cluster isn't a Document
            code = "C" + (++clusterCount);  //Asign the code starting with a C and the next clusterCount value
        }
        Cluster cluster = new Cluster(code);    //Create the CLuster with it's own code
        cluster.setLeftChild(lCluster);         //Set left cluster in this pair as the left child of the new cluster
        cluster.setRightChild(rCluster);        //Set right cluster in this pair as the right child of the new cluster
        //Set the parent of the child clusters as the newly created cluster
        lCluster.setParent(cluster);
        rCluster.setParent(cluster);

        return cluster; //Return the new cluster
    }
}
