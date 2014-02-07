package sdp.vision;

import java.awt.Point;

import constants.Quadrant;
import constants.QuadrantX;
import constants.RobotColour;
import constants.RobotType;


public interface VisionInterface {
		
	//Method signatures
	public int getDirection();
	public int getPitch();
	
	public Point getRobotXY(RobotColour colour, RobotType type);
	public Double getOrientation(RobotColour colour, RobotType type);
	public Point getBallXY();
	public int getQuadrantX(Quadrant quadrant, QuadrantX quadrantX);
	
	public Point getBallVelocity();
	public Point getRobotVelocity(RobotColour colour, RobotType type);
	
	public Point[] getBallHistory();
	public Point[] getRobotHistory(RobotColour colour, RobotType type);
	
	public double getAimingAngle();
	
}
