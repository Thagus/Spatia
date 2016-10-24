package model.clustering;

import java.util.*;

public class SimilarityMap {
    private Map<String, ClusterPair> pairHash;
    private PriorityQueue<ClusterPair> data;

    public SimilarityMap() {
        data = new PriorityQueue<>();
        pairHash = new HashMap<>();
    }

    public ClusterPair findByCodePair(Cluster c1, Cluster c2) {
        String inCode = pairCode(c1, c2);
        return pairHash.get(inCode);
    }

    public ClusterPair removeFirst() {
        ClusterPair poll = data.poll();

        if (poll == null) {
            return null;
        }

        pairHash.remove(pairCode(poll));
        return poll;
    }

    public boolean remove(ClusterPair link) {
        ClusterPair remove = pairHash.remove(pairCode(link));
        if (remove == null) {
            return false;
        }
        data.remove(remove);
        return true;
    }


    public boolean add(ClusterPair link) {
        pairHash.put(pairCode(link), link);
        data.add(link);
        return true;
    }

    private String pairCode(ClusterPair link) {
        return pairCode(link.getlCluster(), link.getrCluster());
    }

    private String pairCode(Cluster lCluster, Cluster rCluster) {
        if (lCluster.getCode().compareTo(rCluster.getCode()) < 0) {
            return lCluster.getCode() + "-" + rCluster.getCode();//getlCluster().hashCode() + 31 * (getrCluster().hashCode());
        } else {
            return rCluster.getCode() + "-" + lCluster.getCode();//return getrCluster().hashCode() + 31 * (getlCluster().hashCode());
        }
    }
}
