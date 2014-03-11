package sdp.vision;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import constants.Colours;
import constants.RobotColour;
import constants.RobotType;

/**
 * Vision's internal representation of one quadrant. To get the data 
 * for the whole field you need to aggregate the data from all four quadrants. 
 * This should be done by PitchPoints.
 * 
 * @author Thomas Wallace
 */
public class QuadrantPoints {
	
	private RobotColour rColour;
	private RobotType rType;
	
	// List of points for all the colours that interest us
	private HashMap<Colours, ArrayList<Point>> points = null;
	
	// Info about the robot in this quadrant
	private Point robotPosition;
	private double robotOrientation;
	
	public QuadrantPoints() {
		// Initialise list of points of each colour
		points = new HashMap<Colours, ArrayList<Point>>();
		for (Colours colour: Colours.values()) {
			points.put(colour, new ArrayList<Point>());
		}
		
		robotOrientation = 0;
		robotPosition = new Point();
	}
	
	/* -------------------------------------------------- */
	/* Methods for the robot's position and orientation  
	/* -------------------------------------------------- */
	
	public Point getRobotPosition() {
		return robotPosition;
	}
	
	public double getRobotOrientation() {
		return robotOrientation;
	}
	
	public void setRobotPosition(Point robotPosition) {
		this.robotPosition = robotPosition;
	}

	public void setRobotOrientation(double robotOrientation) {
		this.robotOrientation = robotOrientation;
	}
	
	/* -------------------------------------------------- */
	/* Methods for setting the robot type for this quadrant
	/* -------------------------------------------------- */
	public RobotColour getrColour() {
		return rColour;
	}

	public void setrColour(RobotColour rColour) {
		this.rColour = rColour;
	}

	public RobotType getrType() {
		return rType;
	}

	public void setrType(RobotType rType) {
		this.rType = rType;
	}
	
	/** Returns all points of that colour
	 * @param colour
	 * @return
	 */
	public ArrayList<Point> getPoints(Colours colour) {
		return points.get(colour);
	}
	
	public ArrayList<Point> getPoints(RobotColour colour) {
		return points.get(colour);
	}
	
	/** Set the points of the colour
	 * @param colour Colour of the points
	 * @param points List of points of that colours
	 */
	public void setPoints(Colours colour, ArrayList<Point> points) {
		this.points.put(colour, points);
	}
}
