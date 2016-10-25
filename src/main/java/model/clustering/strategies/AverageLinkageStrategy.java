package model.clustering.strategies;

import java.util.Collection;

public class AverageLinkageStrategy extends LinkageStrategy {

	public AverageLinkageStrategy(){
		super("Average linkage");
	}

	@Override
	public double calculateSimilarity(Collection<Double> similarities) {
		double sum = 0;
		double result;

		for (Double sim : similarities) {
			sum += sim;
		}
		if (similarities.size() > 0) {
			result = sum / similarities.size();
		} else {
			result = 0.0;
		}
		return result;
	}
}
