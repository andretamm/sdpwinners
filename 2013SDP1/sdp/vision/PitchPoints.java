package sdp.vision;

import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.HashMap;

import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.RobotType;

/**
 * Contains the image info for the whole pitch.
 * 
 * @author Andre
 * @author Thomas Wallace
 */
public class PitchPoints extends ObjectPoints {
	
	// The four quadrants	
	private HashMap<Quadrant, ObjectPoints> quadrants;
	
	private Point ballPosition;
	
	public PitchPoints(WorldState worldstate) {
		super();
		this.ballPosition = new Point(0,0);
		initQuadrants(worldstate);
	}
	
	public ObjectPoints getQuadrant(Quadrant q) {
		return quadrants.get(q);
	}
	
	/**
	 * Initialises the four quarters by figuring out which robot each one
	 * contains. Call this in the constructor before you do anything else 
	 * with the class or suffer the consequences.
	 */
	private void initQuadrants(WorldState worldstate) {
		quadrants = new HashMap<Quadrant, ObjectPoints>();
		
		for (Quadrant q: Quadrant.values()) {
			quadrants.put(q, new ObjectPoints());
		}
		
		RobotColour ourColour = worldstate.getColour();
		RobotColour oppositionColour = ourColour == RobotColour.BLUE ? RobotColour.YELLOW : RobotColour.BLUE;
		
		// Figure out which quadrant is responsible for what
		if (worldstate.getDirection() == 1) {
			// Left goal is ours...
			// TODO omg just use a constant instead of 0/1
			quadrants.get(Quadrant.Q1).setrColour(ourColour);
			quadrants.get(Quadrant.Q2).setrColour(oppositionColour);
			quadrants.get(Quadrant.Q3).setrColour(ourColour);
			quadrants.get(Quadrant.Q4).setrColour(oppositionColour);
		} else {
			// Right goal is ours
			quadrants.get(Quadrant.Q1).setrColour(oppositionColour);
			quadrants.get(Quadrant.Q2).setrColour(ourColour);
			quadrants.get(Quadrant.Q3).setrColour(oppositionColour);
			quadrants.get(Quadrant.Q4).setrColour(ourColour);			
		}
		
		quadrants.get(Quadrant.Q1).setrType(RobotType.DEFENDER);
		quadrants.get(Quadrant.Q2).setrType(RobotType.ATTACKER);
		quadrants.get(Quadrant.Q3).setrType(RobotType.ATTACKER);
		quadrants.get(Quadrant.Q4).setrType(RobotType.DEFENDER);
	}
	
	/**
	 * Get grey points for a specific robot
	 * @param colour
	 * @param type
	 * @return
	 */
	/* useless now?
	public ArrayList<Point> getGreyPoints(RobotColour colour, RobotType type) {
		ObjectPoints quadrant = getRobotQuadrant(colour, type);
		return quadrant.getGreyPoints();
	}
	*/
	
	/**
	 * Gets all the (plate-)green points in the robot's quadrant
	 * @param colour
	 * @param type
	 * @return
	 */
	/* useless now?
	public ArrayList<Point> getGreenPoints(RobotColour colour, RobotType type) {
		ObjectPoints quadrant = getRobotQuadrant(colour, type);
		return quadrant.getGreenPoints();
	}
	*/

	/* -------------------------------------------------- */
	/* Methods for each robot's position and orientation
	/* -------------------------------------------------- */
	public Point getRobotPosition(RobotColour rColour, RobotType rType) {
		ObjectPoints quadrant = getRobotQuadrant(rColour, rType);
		return quadrant.getRobotPosition();
	}
	
	public void setRobotPosition(RobotColour rColour, RobotType rType, Point position) {
		ObjectPoints quadrant = getRobotQuadrant(rColour, rType);
		quadrant.setRobotPosition(position);
	}
	
	public double getRobotOrientation(RobotColour rColour, RobotType rType) {
		ObjectPoints quadrant = getRobotQuadrant(rColour, rType);
		return quadrant.getRobotOrientation();
	}
	
	public void setRobotOrientation(RobotColour rColour, RobotType rType, double orientation) {
		ObjectPoints quadrant = getRobotQuadrant(rColour, rType);
		quadrant.setRobotOrientation(orientation);
	}
	
	/* -------------------------------------------------- */
	/* Methods for the ball position
	/* -------------------------------------------------- */
	public Point getBallPosition() {
		return ballPosition;
	}
	
	public void setBallPosition(Point ballPosition) {
		this.ballPosition = ballPosition;
	}
	
	/* -------------------------------------------------- */
	/* Everything else
	/* -------------------------------------------------- */
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
	
	/** Returns all the points of the given colour in the robot's quadrant 
	 * @param rColour Colour of the robot
	 * @param rType Type of the robot
	 * @param colour Which colour points to return
	 * @return
	 */
	public ArrayList<Point> getColouredPoints(RobotColour rColour, RobotType rType, Colours colour) {
		ObjectPoints quadrant = getRobotQuadrant(rColour, rType);
		return quadrant.getPoints(colour);
	}
	
	@Override
	public ArrayList<Point> getPoints(Colours colour) {
		if (super.getPoints(colour).isEmpty()) {
			// Need to compile the list of colours
			ArrayList<Point> result = new ArrayList<Point>();
			
			for (Quadrant q: Quadrant.values()) {
				result.addAll(getQuadrant(q).getPoints(colour));
			}
			
			setPoints(colour, result);
		}
		
		return super.getPoints(colour);
	}
}
