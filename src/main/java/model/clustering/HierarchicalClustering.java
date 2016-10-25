package model.clustering;

import model.ModelDatabase;
import model.clustering.strategies.CompleteLinkageStrategy;
import model.clustering.strategies.LinkageStrategy;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HierarchicalClustering {
    private PreparedStatement stSimilaritiesCondensed;    //The statement that retrieves all document vs document similarities as a condensed matrix
    private PreparedStatement stAddCluster;

    private LinkageStrategy linkageStrategy;    //the strategy object that will be use to recalculate similarities

    public HierarchicalClustering(Connection connection) throws SQLException{
        stSimilaritiesCondensed = connection.prepareStatement("SELECT i2.idDoc, SUM((i1.weight*i2.weight)/SQRT((SELECT SUM(i11.weight) FROM (SELECT weight FROM SPATIA.INVERTEDINDEX WHERE idDoc=?) i11)*(SELECT SUM(i22.weight) FROM SPATIA.INVERTEDINDEX i22 WHERE i2.idDoc=i22.idDoc))) as sim " +
                "FROM (SELECT term, weight FROM SPATIA.INVERTEDINDEX WHERE idDoc=?) AS i1, SPATIA.INVERTEDINDEX i2 " +
                "WHERE i2.term=i1.term AND i2.idDoc>? " +
                "GROUP BY i2.idDoc " +
                "ORDER BY i2.idDoc");

        stAddCluster = connection.prepareStatement("INSERT INTO SPATIA.CLUSTER(clusterName,parentName,level,strategy) VALUES(?,?,?,?)");

        //The default linkage strategy is the Complete linkage
        linkageStrategy = new CompleteLinkageStrategy();
    }

    /**
     * Reads the database and extracts the similarities between every document in it
     * Saves the condensed matrix of similarities into an array
     * Starts the clustering if everything was correctly obtained from the database
     * @throws SQLException if there's a problem executing the stSimilaritiesCondensed query
     */
    public void beginClustering() throws SQLException{
        //Initialize arrays
        ArrayList<String> documentsList = new ArrayList<>();
        ArrayList<Double> similarities = new ArrayList<>();
        //Obtain the number of documents in the database
        int numDocs = ModelDatabase.instance().opDocuments.countDocuments();
        //As we know we have consecutive document ids in the database from 1 to numDocs, obtain the data from every id until the number of documents
        for(int i=1; i<=numDocs; i++){
            documentsList.add("D"+i);   //Add the name of the document as "D"+id
            System.out.println("Processing document " + i);

            //Set the parameters for the query (AKA the document id)
            stSimilaritiesCondensed.setInt(1, i);
            stSimilaritiesCondensed.setInt(2, i);
            stSimilaritiesCondensed.setInt(3, i);

            ResultSet rs = stSimilaritiesCondensed.executeQuery();
            int lastDoc = i;

            while (rs.next()) {
                double sim = rs.getDouble(2);   //Get the similarity between the documents evaluated

                //Add the missing documents if they exist before the currently being added and the last added
                if(rs.getInt(1)!=(lastDoc+1)){
                    for(int k=lastDoc+1; k<rs.getInt(1); k++){
                        similarities.add(0.0);
                        lastDoc++;
                    }
                }
                //The last document added is the currently being added
                lastDoc++;

                similarities.add(sim);
                //System.out.println("Document " + i + " with " + rs.getInt(1) + " = " + sim);
            }

            //Add the missing documents if they exist after the ones added
            if(lastDoc!=numDocs){
                for(int k=lastDoc+1; k<=numDocs; k++){
                    similarities.add(0.0);
                    lastDoc++;
                }
            }
        }

        //Security checks before start clustering
        if(documentsList.size()==0){    //If we don't have documents, don't cluster
            JOptionPane.showMessageDialog(null, "The are no documents");
        }
        else if(similarities.size()==0){    //If we don't have similarities, we cannot cluster
            JOptionPane.showMessageDialog(null, "The are no similarities in the matrix");
        }
        else if(similarities.size() != documentsList.size()*(documentsList.size()-1)/2){    //If we dont have enough similarities for the number of documents, we cannot cluster
            JOptionPane.showMessageDialog(null, "The similarities array doesn't have the expected size for the number of documents");
        }
        else {  //We execute the clustering if all checks have passed
            performClustering(similarities, documentsList);
        }
    }

    /**
     * Initialize the creation of the clusters
     * @param similarities a condensed matrix containing the similarities between documents
     * @param documentNames an array containing the document names
     * @return the root of the cluster hierarchy
     */
    private void performClustering(ArrayList<Double> similarities, ArrayList<String> documentNames) throws SQLException {
        //We must ensure the existence of a linkage strategy
        if (linkageStrategy == null) {
            JOptionPane.showMessageDialog(null, "Undefined linkage strategy");
            return;
        }

        List<Cluster> clusters = beginClustering(documentNames); //Create the base clusters based on the documents array
        SimilarityMap linkages = createLinkages(similarities, clusters);    //Create the first links between clusters using the similarity condensed matrix

        //Give the hierarchy builder the initial clusters with the similarity map
        HierarchyBuilder builder = new HierarchyBuilder(clusters, linkages);

        //While there is more than one cluster, keep making clusters
        while (!builder.isTreeComplete()) {
            builder.agglomerate(linkageStrategy);
        }

        System.out.println();
        saveClustersToDatabase(builder.getRootCluster(), null, 0);
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
    private ArrayList<Cluster> beginClustering(ArrayList<String> clusterNames) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (String clusterName : clusterNames) {
            Cluster cluster = new Cluster(clusterName);
            clusters.add(cluster);
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
    private int accessFunction(int i, int j, int n) {
        return n*j - j*(j+1)/2 + i-1-j;
    }

    /**
     * Saves the generated clusters into database, recursively from the hierarchy root
     * @param cluster the cluster currently being saved
     * @param parent the parent of the cluster
     */
    private void saveClustersToDatabase(Cluster cluster, Cluster parent, int level) throws SQLException {
        stAddCluster.setString(1, cluster.getCode());
        if(parent==null){
            stAddCluster.setString(2, "null");
        }
        else {
            stAddCluster.setString(2, parent.getCode());
        }
        stAddCluster.setInt(3, level);
        stAddCluster.setString(4, linkageStrategy.getStrategyName());

        stAddCluster.executeUpdate();

        if(parent!=null)
            System.out.println("Level " + level +  ". Child of " + parent.getCode() + ", name: " + cluster.getCode());
        else
            System.out.println("Level " + level +  ". Name: " + cluster.getCode() + " (root)");
        
        if(cluster.isLeaf()){
            return;
        }

        level++;

        if(cluster.getLeftChild()!=null){
            saveClustersToDatabase(cluster.getLeftChild(), cluster, level);
        }

        if(cluster.getRightChild()!=null){
            saveClustersToDatabase(cluster.getRightChild(), cluster, level);
        }
    }

    /**
     * Linkage strategy setter
     */
    public void setLinkageStrategy(LinkageStrategy linkageStrategy) {
        this.linkageStrategy = linkageStrategy;
    }
}
