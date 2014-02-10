package behavior;

import java.awt.Point;
import java.awt.geom.Point2D;

public class StrategyHelper {
	
	public static final double ROBOT_DISTANCE_FROM_BALL = 30;
	
	/**
	 * Normalises the vector to have length one.
	 */
	public static Point2D.Double normaliseVector(Point2D.Double point) {
		double sum = point.getX() + point.getY();
		return new Point2D.Double(point.getX() / sum, point.getY() / sum);
	}
	
	/**
	 * Multiplies a vector point with a constant.
	 * E.g. multiplyVector((1,2), 3) = (3, 6)
	 */
	public static Point2D.Double multiplyVector(Point2D.Double vector, double weight) {
		return new Point2D.Double(vector.getX() * weight, vector.getY() * weight);
	}
	
	/**
	 * Adds a vector point (such as velocity) to a point (x, y location on the pitch) 
	 */
	public static Point addVectorToPoint(Point2D.Double vector, Point point) {
		return new Point((int) (point.getX() + vector.getX()), (int) (point.getY() + vector.getY()));
	}
	
	/**
	 * Finds the location from where, if facing the right angle, the robot can
	 * score a goal if the move forward and kick. This is done by finding the line
	 * between the ball and the centre of the goal and finding a point on this line. 
	 */
	public static Point findRobotKickPosition(Point ball, Point goalCentre) {
		Point2D.Double orientationVector = normaliseVector(new Point2D.Double(goalCentre.getX() - ball.getX(), goalCentre.getY() - ball.getY()));		
		Point robotPosition =  addVectorToPoint(multiplyVector(orientationVector, ROBOT_DISTANCE_FROM_BALL), ball);
		
		return robotPosition;
	}
	
	
}
