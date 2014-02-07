package sdp.vision;

import java.awt.Point;

import constants.Quadrant;
import constants.QuadrantX;
import constants.RobotColour;
import constants.RobotType;


public interface VisionInterface {
		
	//Method signature
	
	/* Retrieve the direction we want to shoot in: 0 = Right, 1 = Left */
	public int getDirection();
	/* Retrieve the pitch we're playing on: 0 = Main Pitch, 1 = Side Pitch */
	public int getPitch();
	
	/**
	 * Retrieve the coordinates of the specified robot 
	 * @param colour The colour of the robot: BLUE or YELLOW
	 * @param type The type of the robot: ATTACKER or DEFENDER
	 * @return The X,Y coordinate
	 */
	public Point getRobotXY(RobotColour colour, RobotType type);
	
	/**
	 * Retrieve the orientation direction of the specified robot 
	 * @param colour The colour of the robot: BLUE or YELLOW
	 * @param type The type of the robot: ATTACKER or DEFENDER
	 * @return The angle at which the robot is facing
	 */
	public Double getOrientation(RobotColour colour, RobotType type);
	/* Retrieve the location of the ball */
	public Point getBallXY();
	/**
	 * Retrieve the boundaries of the specified quadrant
	 * @param quadrant The quadrant number: Q1, Q2, Q3, Q4
	 * @param quadrantX The boundary which you want: LOW or HIGH
	 * @return The X value of the specified quadrant boundary
	 */
	public int getQuadrantX(Quadrant quadrant, QuadrantX quadrantX);
	
	/* Retrieve the ball velocity */
	public Point getBallVelocity();
	/**
	 * Retrieve the specified robot's velocity
	 * @param colour The colour of the robot: BLUE or YELLOW
	 * @param type The type of the robot: ATTACKER or DEFENDER
	 * @return The vector representing the velocity
	 */
	public Point getRobotVelocity(RobotColour colour, RobotType type);
	
	/* Retrieve the ball's coordinate history */
	public Point[] getBallHistory();
	/**
	 * Retrieve the specified robot's coordinate history
	 * @param colour The colour of the robot: BLUE or YELLOW
	 * @param type The type of the robot: ATTACKER or DEFENDER
	 * @return The history of coordinates a robot has been
	 */
	public Point[] getRobotHistory(RobotColour colour, RobotType type);
	
	/* Retrieve the angle at which you want to aim */
	public double getAimingAngle();
	
}
