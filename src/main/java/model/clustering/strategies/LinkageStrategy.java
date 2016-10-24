package model.clustering.strategies;

import java.util.Collection;

public interface LinkageStrategy {

	public double calculateSimilarity(Collection<Double> similarities);
}
