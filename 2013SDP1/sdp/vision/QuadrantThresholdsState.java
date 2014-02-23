package sdp.vision;

import java.io.Serializable;
import java.util.HashMap;
import constants.Colours;

/**
 * TODO
 * 
 * @author 
 *
 */

public class QuadrantThresholdsState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<Colours,ObjectThresholdState> quadrantThresholds;
	
	public QuadrantThresholdsState() {
		initQuadrantThresholds();
		
	}
	
	private void initQuadrantThresholds(){
		quadrantThresholds = new HashMap<Colours, ObjectThresholdState>();
		
		for (Colours c : Colours.values()){
			quadrantThresholds.put(c, new ObjectThresholdState());
		}
	}
	
	public ObjectThresholdState getObjectThresholds(Colours c){
		return quadrantThresholds.get(c);
	}
	
}