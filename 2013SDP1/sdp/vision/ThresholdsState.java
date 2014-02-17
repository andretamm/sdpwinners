package sdp.vision;

import java.util.HashMap;
import constants.Quadrant;

/**
 * TODO
 * 
 * @author 
 *
 */

public class ThresholdsState {

	private HashMap<Quadrant, QuadrantThresholdsState> thresholds;
	
	public ThresholdsState() {
		initQuadrantThresholds();
		
	}
	
	private void initQuadrantThresholds(){
		thresholds = new HashMap<Quadrant, QuadrantThresholdsState>();
		
		for (Quadrant q : Quadrant.values()){
			thresholds.put(q, new QuadrantThresholdsState());
		}
	}
	
	public QuadrantThresholdsState getQuadrantThresholds(Quadrant q){
		return thresholds.get(q);
	}
	
}