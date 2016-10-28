package model.clustering;

/**
 * Created by Thagus on 22/10/16.
 *
 * An object to store a cluster
 */
public class Cluster {
    private String code;

    private Cluster parent;
    private Cluster leftChild, rightChild;

    private int level;

    public Cluster(String code) {
        this.leftChild = null;
        this.rightChild = null;
        this.code = code;
    }

    /**
     * Getters
     */
    public Cluster getLeftChild() {
        return leftChild;
    }
    public Cluster getRightChild() {
        return rightChild;
    }
    public Cluster getParent() {
        return parent;
    }
    public String getCode() {
        return code;
    }
    public int getLevel() {
        return level;
    }

    /**
     * Setters
     */
    public void setLeftChild(Cluster child) {
        this.leftChild = child;
    }
    public void setRightChild(Cluster child) {
        this.rightChild = child;
    }
    public void setParent(Cluster parent) {
        this.parent = parent;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public void addChild(Cluster child){
        if(this.leftChild==null){
            this.leftChild = child;
        }
        else if(this.rightChild==null){
            this.rightChild = child;
        }
        else{
            System.out.println(code + " cluster full!");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Cluster other = (Cluster) obj;
        return code.equals(other.code);
    }

    @Override
    public int hashCode() {
        //Calculate the hashcode based on the code of this Cluster
        return (code == null) ? 0 : code.hashCode();
    }

    /**
     * A method to check if the Cluster is a leaf
     * @return whether this Cluster is a leaf or not
     */
    public boolean isLeaf() {
        //If there are no children, this is a leaf
        return leftChild==null && rightChild==null;
    }

    /**
     * Get the child that is not the one passed
     * @param cluster the cluster we want to get its sibling
     * @return the sibling cluster
     */
    public Cluster getOtherChild(Cluster cluster) {
        if(cluster.equals(leftChild)){
            return rightChild;
        }
        else if(cluster.equals(rightChild)){
            return leftChild;
        }
        else {
            return null;
        }
    }
}
