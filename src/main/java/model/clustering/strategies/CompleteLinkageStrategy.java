package model.clustering.strategies;

import java.util.Collection;

public class CompleteLinkageStrategy implements LinkageStrategy {

	@Override
	public double calculateSimilarity(Collection<Double> similarities) {
		double minSim = Double.NaN;

		for (Double sim : similarities) {
		    if (Double.isNaN(minSim) || sim < minSim)
		        minSim = sim;
		}
		return minSim;
	}
}
