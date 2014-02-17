package sdp.vision;

import java.util.HashMap;
import constants.Colours;

/**
 * TODO
 * 
 * @author 
 *
 */

public class QuadrantThresholdsState {

	private HashMap<Colours,ObjectThresholdState> quadrantThresholds;
	
	public QuadrantThresholdsState() {
		initQuadrantThresholds();
		
	}
	
	private void initQuadrantThresholds(){
		
		for (Colours c : Colours.values()){
			quadrantThresholds.put(c, new ObjectThresholdState());
		}
	}
	
	public ObjectThresholdState getObjectThresholds(Colours c){
		return quadrantThresholds.get(c);
	}
	
}