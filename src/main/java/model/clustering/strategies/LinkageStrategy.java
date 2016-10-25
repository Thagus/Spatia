package model.clustering.strategies;

import java.util.Collection;

public abstract class LinkageStrategy {
	private String strategyName;

	public LinkageStrategy(String strategyName) {
		this.strategyName = strategyName;
	}
	public String getStrategyName() {
		return strategyName;
	}

	public abstract double calculateSimilarity(Collection<Double> similarities);
}
