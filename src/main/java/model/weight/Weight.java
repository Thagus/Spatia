package model.weight;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Weight {
    private String weightMethodName;

    public Weight(String weightMethodName) {
        this.weightMethodName = weightMethodName;
    }

    public String getWeightMethodName() {
        return weightMethodName;
    }

    public abstract void calculateWeights();
}
