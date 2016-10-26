package model.clustering;

import model.clustering.strategies.LinkageStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HierarchyBuilder {

    private SimilarityMap similarities;
    private List<Cluster> clusters;

    public HierarchyBuilder(List<Cluster> clusters, SimilarityMap similarities) {
        this.clusters = clusters;
        this.similarities = similarities;
    }

    public void agglomerate(LinkageStrategy linkageStrategy) {
        ClusterPair maxSimLink = similarities.removeFirst();
        if (maxSimLink != null) {
            clusters.remove(maxSimLink.getrCluster());
            clusters.remove(maxSimLink.getlCluster());

            Cluster oldClusterL = maxSimLink.getlCluster();
            Cluster oldClusterR = maxSimLink.getrCluster();
            Cluster newCluster = maxSimLink.agglomerate(null);

            for (Cluster iClust : clusters) {
                ClusterPair link1 = similarities.findByCodePair(iClust, oldClusterL);
                ClusterPair link2 = similarities.findByCodePair(iClust, oldClusterR);
                ClusterPair newLinkage = new ClusterPair();
                newLinkage.setlCluster(iClust);
                newLinkage.setrCluster(newCluster);
                Collection<Double> similarityValues = new ArrayList<>();

                if (link1 != null) {
					Double distVal = link1.getLinkSimilarity();
                    similarityValues.add(distVal);
                    similarities.remove(link1);
                }
                if (link2 != null) {
					Double distVal = link2.getLinkSimilarity();
                    similarityValues.add(distVal);
                    similarities.remove(link2);
                }

                Double newSimilarity = linkageStrategy.calculateSimilarity(similarityValues);

				newLinkage.setLinkSimilarity(newSimilarity);
                similarities.add(newLinkage);

            }
            clusters.add(newCluster);
        }
    }

    public boolean isTreeComplete() {
        System.out.println(clusters.size() + " cluster remaining");
        return clusters.size() == 1;
    }

    public Cluster getRootCluster() {
        if (!isTreeComplete()) {
            throw new RuntimeException("No root available");
        }
        return clusters.get(0);
    }

}
