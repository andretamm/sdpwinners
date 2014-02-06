package sdp.vision;

import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;

/**
 * Contains the image info for the whole pitch.
 * 
 * @author Andre
 * @author Thomas Wallace
 */
public class PitchPoints extends ObjectPoints {
	
	// The four quadrants
	private ObjectPoints q1;
	private ObjectPoints q2;
	private ObjectPoints q3;
	private ObjectPoints q4;
	
	public PitchPoints(
			ArrayList<Point> ballPoints,
			ArrayList<Point> bluePoints,
			ArrayList<Point> yellowPoints,
			ArrayList<Point> greenPoints,
			ArrayList<Point> greyPoints,
			ArrayList<Point> blueGreenPlate,
			ArrayList<Point> yellowGreenPlate,
			Point[] blueGreenPlate4Points,
			Point[] yellowGreenPlate4Points,
			ArrayList<Point> blueGreyPoints,
			ArrayList<Point> yellowGreyPoints,
			Point ball,
			Point blue,
			Point yellow,
			double blueOrientation,
			double yellowOrientation, 
			ObjectPoints q1,
			ObjectPoints q2,
			ObjectPoints q3,
			ObjectPoints q4) {
		
		super(
				ballPoints,
				bluePoints,
				yellowPoints,
				greenPoints,
				greyPoints,
				blueGreenPlate,
				yellowGreenPlate,
				blueGreenPlate4Points,
				yellowGreenPlate4Points,
				blueGreyPoints,
				yellowGreyPoints,
				ball,
				blue,
				yellow,
				blueOrientation,
				yellowOrientation);
		this.q1 = q1;
		this.q2 = q2;
		this.q3 = q3;
		this.q4 = q4;
	}
	
	public PitchPoints(WorldState worldstate) {
		super();
		initQuarters(worldstate);
	}
	
	/* world state RobotColour to the robot colour of our choice */
	
	public ObjectPoints getQuadrant(Quadrant q) {
		switch (q) {
			case Q1:
				return q1;
			case Q2:
				return q2;
			case Q3:
				return q3;
			case Q4:
				return q4;
			default:
				System.err.println("Unknown quadrant " + q + " specified for getQuadrant");
				return null;
		}
	}	
	
	/**
	 * Initialises the four quarters by figuring out which robot each one
	 * contains. Call this before you do anything else with the class or
	 * suffer the consequences.
	 */
	private void initQuarters(WorldState worldstate) {
		this.q1 = new ObjectPoints();
		this.q2 = new ObjectPoints();
		this.q3 = new ObjectPoints();
		this.q4 = new ObjectPoints();
		
		RobotColour ourColour = worldstate.getColour();
		RobotColour oppositionColour = ourColour == RobotColour.BLUE ? RobotColour.YELLOW : RobotColour.BLUE;
		
		// Figure out which quadrant is responsible for what
		if (worldstate.getDirection() == 1) {
			// Left goal is ours...
			// TODO omg just use a constant instead of 0/1
			q1.setrColour(ourColour);
			q2.setrColour(oppositionColour);
			q3.setrColour(ourColour);
			q4.setrColour(oppositionColour);
		} else {
			// Right goal is ours
			q1.setrColour(oppositionColour);
			q2.setrColour(ourColour);
			q3.setrColour(oppositionColour);
			q4.setrColour(ourColour);			
		}
		
		q1.setrType(RobotType.DEFENDER);
		q2.setrType(RobotType.ATTACKER);
		q3.setrType(RobotType.ATTACKER);
		q4.setrType(RobotType.DEFENDER);
	}
	
	/* MILA NEEDS:
	 * getBlueGreyPoints(), getBluePoints(), getBlueGreenPlate()
	 */
	
	/**
	 * Get grey points for a specific robot
	 * @param colour
	 * @param type
	 * @return
	 */
	public ArrayList<Point> getGreyPoints(RobotColour colour, RobotType type) {
		ObjectPoints quadrant = getRobotQuadrant(colour, type);
		return quadrant.getGreyPoints();
	}
	
	/**
	 * Returns the same coloured points in the robot's quadrant
	 * @param colour
	 * @param type
	 * @return
	 */
	public ArrayList<Point> getColouredPoints(RobotColour colour, RobotType type) {
		ObjectPoints quadrant = getRobotQuadrant(colour, type);
		return quadrant.getPoints(colour);
	}
	
	
	/**
	 * Gets all the (plate-)green points in the robot's quadrant
	 * @param colour
	 * @param type
	 * @return
	 */
	public ArrayList<Point> getGreenPoints(RobotColour colour, RobotType type) {
		ObjectPoints quadrant = getRobotQuadrant(colour, type);
		return quadrant.getGreenPoints();
	}
	
	/**
	 * Returns the quadrant that contains this robot
	 * @param colour
	 * @param type
	 * @return
	 */
	private ObjectPoints getRobotQuadrant(RobotColour colour, RobotType type) {
		// Check all quadrants and see which one has our desired robot
		for (Quadrant q: Quadrant.values()) {
			ObjectPoints quadrant = getQuadrant(q);
			
			if (quadrant.getrColour() == colour && quadrant.getrType() == type) {
				return quadrant;
			}
		}
		
		// Didn't find matching quadrant, something is seriously wrong
		System.err.println("Couldn't find quadrant for robot (" + colour + ", " + type + ")");
		return null;
	}
}
