package sdp.vision;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;

import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;

/**
 * TODO
 * 
 * @author 
 *
 */

public class ThresholdsState implements Serializable {

	// Whether to show a colour in the Vision GUI.
	// The value for each colour will be True when we view the tab for that
	// colour and False otherwise
	EnumMap<Colours, Boolean> debug;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Quadrant, QuadrantThresholdsState> thresholds;
	
	public ThresholdsState() {
		initQuadrantThresholds();

		// Init the colour debug values
		debug = new EnumMap<Colours, Boolean>(Colours.class);
		
		for (Colours c : Colours.values()) {
			debug.put(c, false);
		}
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
	
	public void updateState(ThresholdsState newState) {
		for (Quadrant q : Quadrant.values()){
			thresholds.put(q, newState.getQuadrantThresholds(q));
			//System.out.println(q + " " + thresholds.get(q));
		}
	}
	
	public int ScaleTo255(double value) {
		return (int) (value*255);
	}
	
	public boolean getDebug(Colours c) {
		return debug.get(c);
	}
	
	public void setDebug(Colours c, boolean value) {
		debug.put(c, value);
	}
	
}