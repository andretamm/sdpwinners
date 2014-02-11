package behavior;

import java.awt.Point;
import java.awt.geom.Point2D;

import sdp.vision.Orientation;

public class StrategyHelper {
	
	public static final double ROBOT_SAFETY_DISTANCE = 40;
	
	/**
	 * Normalises the vector to have length one.
	 */
	public static Point2D.Double normaliseVector(Point2D.Double point) {
		double sum = Math.abs(point.getX()) + Math.abs(point.getY());
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
		Point2D.Double orientationVector = normaliseVector(new Point2D.Double(ball.getX() - goalCentre.getX(), ball.getY() - goalCentre.getY()));
//		System.out.println("kickposition orientation vector: " + orientationVector.getX() + ", " + orientationVector.getY());
		Point robotPosition =  addVectorToPoint(multiplyVector(orientationVector, ROBOT_SAFETY_DISTANCE), ball);
		
		return robotPosition;
	}
	
	/**
	 * Finds the angle (in radians) that the robot should be facing if it was
	 * behind the ball on the line from the goal to the ball and wanted to kick
	 * the ball in the goal  
	 */
	public static double findRobotKickOrientation(Point ball, Point goalCentre) {
		return Orientation.getAngle(ball, goalCentre);
	}
	
	/**
	 * Sees if our value is in a certain range of the target value
	 * @param x Our value
	 * @param target Target value
	 * @param error Error range that is satisfiable
	 * @return
	 */
	public static boolean inRange(double x, double target, double error) {
		return (x < (target + error)) && (x > (target + error));
	}
}
