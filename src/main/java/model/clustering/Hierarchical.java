package model.clustering;

import model.ModelDatabase;
import model.clustering.strategies.CompleteLinkageStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Thagus on 22/10/16.
 *
 * Hierarchical clustering
 */
public class Hierarchical {
    public void createClusters(Connection connection) throws SQLException {
        //Obtain document list

        //Calculate similarities between every document of the collection against every other


        String q1 = "SELECT i2.idDoc, SUM((i1.weight*i2.weight)/SQRT((SELECT SUM(i11.weight) FROM (SELECT weight FROM SPATIA.INVERTEDINDEX WHERE idDoc=?) i11)*(SELECT SUM(i22.weight) FROM SPATIA.INVERTEDINDEX i22 WHERE i2.idDoc=i22.idDoc))) as sim " +
                "FROM (SELECT term, weight FROM SPATIA.INVERTEDINDEX WHERE idDoc=?) AS i1, " +
                "       SPATIA.INVERTEDINDEX i2 " +
                "WHERE i2.term=i1.term AND i2.idDoc>? " +  //AND i2.idDoc>?
                "GROUP BY i2.idDoc " +
                "ORDER BY i2.idDoc";

        PreparedStatement stCluster = connection.prepareStatement(q1);

        ArrayList<String> documentsList = new ArrayList<>();
        ArrayList<Double> similarities = new ArrayList<>();

        int numDocs = ModelDatabase.instance().opDocuments.countDocuments();

        for(int i=1; i<=numDocs; i++){
            documentsList.add("D"+i);

            stCluster.setInt(1, i);
            stCluster.setInt(2, i);
            stCluster.setInt(3, i);

            ResultSet rs = stCluster.executeQuery();
            int lastDoc = i;

            while (rs.next()) {
                double sim = rs.getDouble(2);

                //Add the missing documents if they exist
                if(rs.getInt(1)!=(lastDoc+1)){
                    for(int k=lastDoc+1; k<rs.getInt(1); k++){
                        System.out.println("For document " + i + " and " + k + ": " + 0.0);
                        similarities.add(0.0);

                        lastDoc++;
                    }
                }
                //The last document added is the currently being added
                lastDoc++;

                similarities.add(sim);

                System.out.println("For document " + i + " and " + rs.getInt(1) + ": " + sim);
            }

            if(lastDoc!=numDocs){
                for(int k=lastDoc+1; k<=numDocs; k++){
                    System.out.println("For document " + i + " and " + k + ": " + 0.0);
                    similarities.add(0.0);

                    lastDoc++;
                }
            }
        }

        ClusteringAlgorithm alg = new ClusteringAlgorithm();
        Cluster cluster = alg.performClustering(similarities, documentsList, new CompleteLinkageStrategy());

        System.out.println();
        printChildrens(cluster, null);
    }

    private void printChildrens(Cluster cluster, Cluster parent){
        if(parent!=null)
            System.out.println( "Child of " + parent.getCode() + ", name: " + cluster.getCode());
        else
            System.out.println("Name: " + cluster.getCode() + " (root)");

        if(cluster.isLeaf()){
            return;
        }

        if(cluster.getLeftChild()!=null){
            printChildrens(cluster.getLeftChild(), cluster);
        }

        if(cluster.getRightChild()!=null){
            printChildrens(cluster.getRightChild(), cluster);
        }
    }

    public static void main(String[] args){
        Hierarchical hierarchical = new Hierarchical();
        try {
            hierarchical.createClusters(ModelDatabase.instance().getCon());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
