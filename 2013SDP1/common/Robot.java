package common;

import java.util.ArrayList;

import constants.RobotColour;
import constants.RobotType;

/**
 * Wrapper class for a robot with a colour and a type
 * @author Andre
 */
public class Robot {
	public RobotColour colour;
	public RobotType type;
	
	/**
	 * Gives an iterable list of all the robots 
	 */
	public static ArrayList<Robot> listAll() {
		ArrayList<Robot> allRobots = new ArrayList<Robot>();
		
		for (RobotColour c: RobotColour.values()) {
			for (RobotType t: RobotType.values()) {
				allRobots.add(new Robot(c, t));
			}
		}
		
		return allRobots;
	}
	
	public Robot(RobotColour c, RobotType t) {
		this.colour = c;
		this.type = t;
	}
}
