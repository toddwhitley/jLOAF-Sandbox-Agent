package agent.backtracking;

import org.jLOAF.inputs.Input;
import org.jLOAF.sim.SimilarityInputMetricStrategy;

public class SandboxSimilarity implements SimilarityInputMetricStrategy{

	@Override
	public double similarity(Input i1, Input i2) {
		if (!(i1 instanceof SandboxFeatureInput) || !(i2 instanceof SandboxFeatureInput)){
			throw new IllegalArgumentException("Equality.similarity(...): One of the arguments was not an SandboxFeatureInput.");
		}
		
		SandboxFeatureInput s1 = (SandboxFeatureInput)i1;
		SandboxFeatureInput s2 = (SandboxFeatureInput)i2;
		double feature1 = -1;
		double feature2 = -2;
		if (!s1.getName().equals(s2.getName())){
			return 0;
		}
		
		feature1 = s1.getFeature().getValue();
		feature2 = s2.getFeature().getValue();

		if(feature1 == feature2){
			return 1.0;
		}
		
		return 0;
	}

}
