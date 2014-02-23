package sdp.vision;

import java.io.Serializable;
import java.util.HashMap;

import constants.Quadrant;

/**
 * TODO
 * 
 * @author 
 *
 */

public class ThresholdsState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Quadrant, QuadrantThresholdsState> thresholds;
	
	public ThresholdsState() {
		initQuadrantThresholds();
		//System.out.println(this);
	}
	
	private void initQuadrantThresholds(){
		thresholds = new HashMap<Quadrant, QuadrantThresholdsState>();
		
		for (Quadrant q : Quadrant.values()){
			thresholds.put(q, new QuadrantThresholdsState());
			//System.out.println(q + " " + thresholds.get(q));
		}
	}
	
	public QuadrantThresholdsState getQuadrantThresholds(Quadrant q){
		//System.out.println(this);
		//System.out.println("returning: " + thresholds.get(q) + " " + q);
		return thresholds.get(q);
	}
	
	public void updateState(ThresholdsState newState) {
		for (Quadrant q : Quadrant.values()){
			thresholds.put(q, newState.getQuadrantThresholds(q));
			//System.out.println(q + " " + thresholds.get(q));
		}
	}
	
	public int ScaleTo255(double value) {
		return (int) (value*255);
	}
	
}