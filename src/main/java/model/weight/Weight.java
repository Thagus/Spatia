package model.weight;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Weight {
    String weightMethodName;

    public String getWeightMethodName() {
        return weightMethodName;
    }

    public abstract void calculateWeights();
}
