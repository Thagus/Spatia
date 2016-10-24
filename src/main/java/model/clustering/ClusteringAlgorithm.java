package model.clustering;

import model.clustering.strategies.LinkageStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusteringAlgorithm {
    private HashMap<String, Cluster> documents;

    public Cluster performClustering(ArrayList<Double> similarities, ArrayList<String> clusterNames, LinkageStrategy linkageStrategy) {
		/* Argument checks */
        if (similarities == null || similarities.size() == 0) {
            throw new IllegalArgumentException("Invalid similarities matrix");
        }
        if (similarities.size() != clusterNames.size()*(clusterNames.size()-1)/2) {
            throw new IllegalArgumentException("Invalid cluster name array");
        }
        if (linkageStrategy == null) {
            throw new IllegalArgumentException("Undefined linkage strategy");
        }

		/* Setup model */
		documents = new HashMap<>();
        List<Cluster> clusters = createClusters(clusterNames);
        SimilarityMap linkages = createLinkages(similarities, clusters);


		/* Process */
        HierarchyBuilder builder = new HierarchyBuilder(clusters, linkages);
        while (!builder.isTreeComplete()) {
            builder.agglomerate(linkageStrategy);
        }

        return builder.getRootCluster();
    }

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

    private List<Cluster> createClusters(ArrayList<String> clusterNames) {
        List<Cluster> clusters = new ArrayList<>();
        for (String clusterName : clusterNames) {
            Cluster cluster = new Cluster(clusterName);
            clusters.add(cluster);
            documents.put(clusterName, cluster);
        }
        return clusters;
    }

    // Credit to this function goes to
    // http://stackoverflow.com/questions/13079563/how-does-condensed-distance-matrix-work-pdist
    private static int accessFunction(int i, int j, int n) {
        return n * j - j * (j + 1) / 2 + i - 1 - j;
    }

    public Cluster getDocumentCluster(int id){
        return documents.get("D"+id);
    }
}
