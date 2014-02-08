package common;

import java.util.ArrayList;

import constants.RobotColour;
import constants.RobotType;

/**
 * Wrapper class for a robot with a colour and a type
 * @author Andre
 */
public class Robot {
	public RobotColour c;
	public RobotType t;
	
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
		this.c = c;
		this.t = t;
	}
}
