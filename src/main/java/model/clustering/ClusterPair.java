package model.clustering;

public class ClusterPair implements Comparable<ClusterPair> {
    private static long clusterCount = 0;

    private Cluster lCluster;
    private Cluster rCluster;
	private Double linkSimilarity;

    public Cluster getlCluster() {
        return lCluster;
    }

    public void setlCluster(Cluster lCluster) {
        this.lCluster = lCluster;
    }

    public Cluster getrCluster() {
        return rCluster;
    }

    public void setrCluster(Cluster rCluster) {
        this.rCluster = rCluster;
    }

	public Double getLinkSimilarity() {
        return linkSimilarity;
    }

	public void setLinkSimilarity(Double similarity) {
        this.linkSimilarity = similarity;
    }

    @Override
    public int compareTo(ClusterPair o) {
        return o.getLinkSimilarity().compareTo(getLinkSimilarity());
    }

    public Cluster agglomerate(String code) {
        if (code == null) {
            code = "C" + (++clusterCount);
        }
        Cluster cluster = new Cluster(code);
        cluster.setLeftChild(lCluster);
        cluster.setRightChild(rCluster);
        lCluster.setParent(cluster);
        rCluster.setParent(cluster);

        return cluster;
    }
}
