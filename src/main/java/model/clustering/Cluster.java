package model.clustering;

/**
 * Created by Thagus on 22/10/16.
 *
 * Represents a cluster in the hierarchy
 */
public class Cluster {
    private String code;

    private Cluster parent;
    private Cluster leftChild, rightChild;

    public Cluster(String code) {
        this.leftChild = null;
        this.rightChild = null;
        this.code = code;
    }

    public Cluster getLeftChild() {
        return leftChild;
    }

    public Cluster getRightChild() {
        return rightChild;
    }

    public Cluster getParent() {
        return parent;
    }

    public void setParent(Cluster parent) {
        this.parent = parent;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return (code == null) ? 0 : code.hashCode();
    }

    public boolean isLeaf() {
        return leftChild==null && rightChild==null;
    }

    public void setLeftChild(Cluster child) {
        this.leftChild = child;
    }

    public void setRightChild(Cluster child) {
        this.rightChild = child;
    }
}
