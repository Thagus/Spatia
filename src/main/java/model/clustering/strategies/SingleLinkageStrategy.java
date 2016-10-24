package model.clustering.strategies;

import java.util.Collection;

public class SingleLinkageStrategy implements LinkageStrategy {

	@Override
	public double calculateSimilarity(Collection<Double> similarities) {
		double maxSim = Double.NaN;

		for (Double sim : similarities) {
		    if (Double.isNaN(maxSim) || sim > maxSim)
		        maxSim = sim;
		}
		return maxSim;
	}
}
