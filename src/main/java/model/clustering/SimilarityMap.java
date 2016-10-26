package model.clustering;

import java.util.*;

public class SimilarityMap {
    private HashMap<String, ClusterPair> pairHash;
    private PriorityQueue<ClusterPair> pairQueue;

    /**
     * Constructor that initializes the HashMap and PriorityQueue
     */
    public SimilarityMap() {
        pairQueue = new PriorityQueue<>();
        pairHash = new HashMap<>();
    }

    /**
     * Obtain the first ClusterPair in the queue, and remove it from the queue and the HashMap
     * @return the obtained ClusterPair from the queue
     */
    public ClusterPair removeFirst() {
        ClusterPair poll = pairQueue.poll();    //Obtain the nex pair in the queue, this is the one with highest linkSimilarity

        //If the obtained pair is null, return null to avoid NullPointerException (just in case, never happened)
        if (poll == null) {
            return null;
        }

        System.out.println("Grouping " + poll.getlCluster().getCode() + " - " + poll.getrCluster().getCode());

        //Remove the recently obtained pair from the HashMap
        pairHash.remove(pairCode(poll));
        //Return the obtained pair
        return poll;
    }

    /**
     * Remove a ClusterPair from the pairHash and pairQueue
     * @param link the ClusterPair to be removed
     */
    public void remove(ClusterPair link) {
        //Remove a pair from the HashMap and obtain the deleted entry value
        ClusterPair remove = pairHash.remove(pairCode(link));
        //If the value obtained from the deleted entry is null, it means the pair we try to delete doesn't exist
        if (remove != null) {
            //Remove the pair from the queue if it exists in the HashMap
            pairQueue.remove(remove);
        }
    }

    /**
     * Add a ClusterPair to the pairHash and pairQueue
     * @param link the ClusterPair to be added
     */
    public void add(ClusterPair link) {
        //Add the pair to the HashMap, calculating the code for it
        pairHash.put(pairCode(link), link);
        //Add the pair to the queue
        pairQueue.add(link);
    }

    /**
     * Find a ClusterPair that is stored on the local HashMap by providing the CLusters that compose it
     * @param c1 One cluster in the pair
     * @param c2 The other cluster in the pair
     * @return The pair containing the given clusters
     */
    public ClusterPair findByCodePair(Cluster c1, Cluster c2) {
        //Calculate the code of the given cluster pair
        String inCode = pairCode(c1, c2);
        //Return the pair that has the calculated code
        return pairHash.get(inCode);
    }

    /**
     * Create a code that identifies a pair
     * @param link the pair we want to calculate a code for
     * @return the code
     */
    private String pairCode(ClusterPair link) {
        //The code of a CLusterPair is the pairCode of it left and right clusters
        return pairCode(link.getlCluster(), link.getrCluster());
    }

    /**
     * Create a code that identifies a Cluster pair
     * @param lCluster one of the Clusters in the pair
     * @param rCluster the other Cluster in the pair
     * @return the code for the pair
     */
    private String pairCode(Cluster lCluster, Cluster rCluster) {
        //The alphabetical order of the pairs in the code matters to avoid duplicate keys with different order
        if (lCluster.getCode().compareTo(rCluster.getCode()) < 0) {
            return lCluster.getCode() + "-" + rCluster.getCode();
        } else {
            return rCluster.getCode() + "-" + lCluster.getCode();
        }
    }
}
