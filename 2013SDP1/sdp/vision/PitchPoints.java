package sdp.vision;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.RobotType;
import constants.ShootingDirection;

/**
 * Contains the image info for the whole pitch.
 * 
 * @author Andre
 * @author Thomas Wallace
 */
public class PitchPoints extends QuadrantPoints {
	
	// The four quadrants	
	private HashMap<Quadrant, QuadrantPoints> quadrants;
	
	private Point ballPosition;
	
	public PitchPoints(WorldState worldstate) {
		super();
		this.ballPosition = new Point(0,0);
		initQuadrants(worldstate);
	}
	
	public QuadrantPoints getQuadrant(Quadrant q) {
		return quadrants.get(q);
	}
	
	/**
	 * Initialises the four quarters by figuring out which robot each one
	 * contains. Call this in the constructor before you do anything else 
	 * with the class or suffer the consequences.
	 */
	private void initQuadrants(WorldState worldstate) {
		quadrants = new HashMap<Quadrant, QuadrantPoints>();
		
		for (Quadrant q: Quadrant.values()) {
			quadrants.put(q, new QuadrantPoints());
		}
		
		RobotColour ourColour = worldstate.getColour();
		RobotColour oppositionColour = ourColour == RobotColour.BLUE ? RobotColour.YELLOW : RobotColour.BLUE;
		
		// Figure out which quadrant is responsible for what
		if (worldstate.getDirection() == ShootingDirection.RIGHT) {
			// Left goal is ours...
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
	
	
	/**
	 * Gets all the (plate-)green points in the robot's quadrant
	 * @param colour
	 * @param type
	 * @return
	 */
	

	/* -------------------------------------------------- */
	/* Methods for each robot's position and orientation
	/* -------------------------------------------------- */
	public Point getRobotPosition(RobotColour rColour, RobotType rType) {
		QuadrantPoints quadrant = getRobotQuadrant(rColour, rType);
		return quadrant.getRobotPosition();
	}
	
	public void setRobotPosition(RobotColour rColour, RobotType rType, Point position) {
		QuadrantPoints quadrant = getRobotQuadrant(rColour, rType);
		quadrant.setRobotPosition(position);
	}
	
	public double getRobotOrientation(RobotColour rColour, RobotType rType) {
		QuadrantPoints quadrant = getRobotQuadrant(rColour, rType);
		return quadrant.getRobotOrientation();
	}
	
	public void setRobotOrientation(RobotColour rColour, RobotType rType, double orientation) {
		QuadrantPoints quadrant = getRobotQuadrant(rColour, rType);
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
	public QuadrantPoints getRobotQuadrant(RobotColour colour, RobotType type) {
		// Check all quadrants and see which one has our desired robot
		for (Quadrant q: Quadrant.values()) {
			QuadrantPoints quadrant = getQuadrant(q);
			
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
		QuadrantPoints quadrant = getRobotQuadrant(rColour, rType);
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
