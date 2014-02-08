package common;

import java.util.HashMap;

import constants.RobotColour;
import constants.RobotType;

/**
 * A generic map for storing values for the robots.
 * @author Andre
 *
 * @param <T> The type of values you want to store for the robot
 */
public class RobotMap<T> {
	HashMap<RobotColour, HashMap<RobotType, T>> map;
	
	public RobotMap() {
		initMap();
	}
	
	/**
	 * Fills the map with a default value for each robot
	 * @param def
	 */
	public RobotMap(T def) {
		initMap();
		
		for (Robot r: Robot.listAll()) {
			
		}
	}
	
	private void initMap() {
		// Initialise hashmaps for all robots
		map = new HashMap<RobotColour, HashMap<RobotType,T>>();
		
		for (RobotColour c: RobotColour.values()) {
			map.put(c, new HashMap<RobotType, T>());
		}
	}

	/**
	 * Gets the stored value in the HashMap for this robot
	 * @param c Robot colour
	 * @param t Robot type
	 * @return Stored value
	 */
	public T get(RobotColour c, RobotType t) {
		return map.get(c).get(t);
	}
	
	/**
	 * Sets the value for the corresponding robot
	 * @param c Robot colour
	 * @param t Robot type
	 * @param value Value to set
	 */
	public void put(RobotColour c, RobotType t, T value) {
		map.get(c).put(t, value);
	}
}
