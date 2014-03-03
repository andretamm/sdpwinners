package behavior;

import java.awt.Point;
import java.awt.geom.Point2D;

import common.Robot;
import constants.C;
import constants.ShootingDirection;
import sdp.vision.Orientation;
import sdp.vision.WorldState;

public class StrategyHelper {
	
	public static final double ROBOT_SAFETY_DISTANCE = 40;
	
	/**
	 * Normalises the vector to have length one.
	 */
	public static Point2D.Double normaliseVector(Point2D.Double vector) {
		double sum = Math.abs(vector.getX()) + Math.abs(vector.getY());
		return new Point2D.Double(vector.getX() / sum, vector.getY() / sum);
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
	 * Find the point on the horizontal line where the line defined by the 
	 * given grounded vector will intersect with it.
	 * @param lineY The y coordinate of the line
	 * @param origin The grounding point for the vector
	 * @param vector E.g. a velocity vector
	 * @param worldstate Handle to the worldstate (needed for the wall x coordinates)
	 * @return The (x,y) coordinates of the intersection point, or null if doesn't intersect
	 */
	public static Point getIntersectWithHorizontalLine(int lineY, Point2D.Double vector, 
													   Point origin, WorldState ws) {
		Point intersect = null;
		
		// If origin is below line, then vector needs to point upwards
		// If origin is above line, then vector needs to point downwards
		if ((origin.y > lineY && vector.y < 0) ||
			(origin.y < lineY && vector.y > 0)) {
			// Find x difference from origin where the intersection point is
			// This will already have the right sign, so don't mess with the
			// order of the subtraction!
			double diffX = (double) (lineY - origin.y) * vector.x / vector.y;
			
			// Find intersection point
			intersect = new Point((int) (origin.x + diffX), lineY);
			
			// Check it is within the pitch x coordinate-wise
			if (intersect.x > ws.getPitchTopLeft().x && intersect.x < ws.getPitchBottomRight().x) {
				return intersect;
			} else {
				// Will hit the wall outside the pitch
				return null;
			}
		} else {
			// Won't hit this wall :P
			return null;
		}
	}
	
	/**
	 * Find the point on our goal where the line defined by the 
	 * given grounded vector will intersect with it.
	 * @param origin The grounding point for the vector
	 * @param vector E.g. a velocity vector
	 * @param worldstate Handle to the worldstate (needed for the wall y coordinates)
	 * @return The (x,y) coordinates of the intersection point, or null if doesn't intersect
	 */
	public static Point getIntersectWithOurGoal(Point2D.Double vector, Point origin, WorldState ws) {
		Point intersect = null;
		int goalX = ws.getOurGoalCentre().x;
		
		//   o   ball
		//   |   goal 
		//   ->  vector direction
		if ((origin.x < goalX && vector.x > 0) ||  //        o  ->  |
			(goalX < origin.x && vector.x < 0)) {  // |  <-  o
			// Find y difference from origin where the intersection point is
			// This will already have the right sign, so don't mess with the
			// order of the subtraction!
			double diffY = (double) (goalX - origin.x) * vector.y / vector.x;
			
			// Find intersection point
			intersect = new Point(goalX, (int) (origin.y + diffY));
			
			// Check it is within the goal y coordinate-wise, add +- 5 for error
			//   _____
			//  / _  _  _  _
			//  |  <- o
			//  | _  _  _  _
			//  \_____
			if (intersect.y > ws.getOurGoalTop().y - 5 && intersect.y < ws.getOurGoalBottom().y + 5) {
				return intersect;
			} else {
				// Will hit the wall outside the goal lolololo
				return null;
			}
		} else {
			// Won't even hit this wall, not to mention the goal :DDD
			return null;
		}
	}
	
	/**
	 * Find the point on the vertical line where the line defined by the 
	 * given grounded vector will intersect with it.
	 * @param x The x coordinate of the vertical line
	 * @param origin The grounding point for the vector
	 * @param vector E.g. a velocity vector
	 * @return The (x,y) coordinates of the intersection point, or null if doesn't intersect
	 */
	public static Point getIntersectWithVerticalLine(int x, Point origin, Point2D.Double vector) {
		Point intersect = null;
		
		//   o   ball
		//   |   vertical line 
		//   ->  vector direction
		if ((origin.x < x && vector.x > 0) ||  //        o  ->  |
			(x < origin.x && vector.x < 0)) {  // |  <-  o
			// Find y difference from origin where the intersection point is
			// This will already have the right sign, so don't mess with the
			// order of the subtraction!
			double diffY = (double) (x - origin.x) * vector.y / vector.x;
			
			// Find intersection point
			intersect = new Point(x, (int) (origin.y + diffY));
			
			return intersect;
		} else {
			// Won't hit this vertical line
			return null;
		}
	}

//	/**
//	 * Find where the given grounded vector will intersect with our goal.
//	 * @param vector The vector applied to the origin point (e.g. velocity vector)
//	 * @param origin The origin (grounding) point for the vector
//	 * @param ws Handle to the WorldState
//	 * @return Point of intersection or null if it won't hit our goal
//	 */
//	public static Point interceptionPoint(Point2D.Double vector, Point origin, WorldState ws) {
//		// The x coordinate of our goal
//		int x = ws.getOurGoalCentre().x;
//		
//		// Find intersection point of line with our goal
//		Point intersection = getIntersectWithOurGoal(vector, origin, ws);
//		
//		return intersection;
//	}
	
	/**
	 * Find the point where the given grounded vector will collide with
	 * one of the horizontal walls
	 * @param origin The grounding (origin) point for the vector
	 * @param vector Vector applied to the origin e.g. a velocity vector
	 * @param worldstate Handle to the worldstate (needed for the wall coordinates)
	 * @return The (x,y) coordinates of the intersection point, 
	 * 		   null if doesn't intersect with either of the walls
	 */
	public static Point getIntersectsWithWalls(Point2D.Double vector, Point origin, WorldState worldstate) {
		int topWall = worldstate.getPitchTopLeft().y;
		int bottomWall = worldstate.getPitchBottomLeft().y;
		
		// Check intersection with upper wall
		Point upperIntersect = getIntersectWithHorizontalLine(topWall, vector, origin, worldstate);
		
		if (upperIntersect != null) {
			return upperIntersect;
		}
		
		// Check intersection with lower wall
		Point lowerIntersect = getIntersectWithHorizontalLine(bottomWall, vector, origin, worldstate);
		
		if (lowerIntersect != null) {
			return lowerIntersect;
		}
		
		// No intersection points found
		return null;
	}
	
	/**
	 * Get the movement vector after having collided with a horisontal wall
	 * @param vector Original vector
	 * @return The vector after the collision
	 */
	public static Point2D.Double collideWithHorizontalWall(Point2D.Double vector) {
		// After colliding with a wall, the y co-ordinate flips, hopefully everything else
		// should stay the same
		return new Point2D.Double(vector.getX(), -vector.getY());
	}
	
	/**
	 * Returns the magnitude of a vector
	 */
	public static double magnitude(Point2D.Double vector) {
		return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
	}
	
	/**
	 * Sees if our value is in a certain range of the target value
	 * @param x Our value
	 * @param target Target value
	 * @param error Error range that is satisfiable
	 * @return
	 */
	public static boolean inRange(double x, double target, double error) {
		return Math.abs(x - target) <= error;
	}
	
	
	/**
	 * Gets the angle that is 180 degrees from the original angle. Need
	 * to use radians!
	 * E.g. angleComplement(270') = 90'  
	 * @return The angle complement
	 */
	public static double angleComplement(double angle) {
		double newAngle = angle + C.A180;
		return newAngle < C.A360 ? newAngle : newAngle - C.A360;
	}
	
	/**
	 * Find the shortest angle between the origin and target.
	 * @param origin Starting angle
	 * @param target End angle
	 * @return The angle difference with + for a CW turn and - for a CCW turn
	 */
	public static double angleDiff(double origin, double target) {
		double turnAngle = target - origin;
		
		if (turnAngle > C.A180) {
			turnAngle -= C.A360;
		}
		if (turnAngle < - C.A180) {
			turnAngle += C.A360;
		}
		
		return turnAngle;
	}
	
	/**
	 * The vertical line where we want the defender robot to be for maximum coverage
	 * @param ws Handle to WorldState
	 * @return The x coordinate of the vertical line
	 */
	public static int getDefendLineX(WorldState ws) {
		int ourGoalX = ws.getOurGoalCentre().x;
		return ws.getDirection() == ShootingDirection.LEFT ? ourGoalX - 60 : ourGoalX + 60; 
	}
	
	public static double getDistance(Point a, Point b){
		return Math.sqrt(Math.pow(Math.abs(a.x - b.x),2) + Math.pow(Math.abs(a.y - b.y),2));
	}
	
	/**
	 * Checks if the given robot has a ball
	 * @param r Robot to check
	 * @param ws Handle to WorldState
	 * @return True if the robot has the ball, False otherwise
	 */
	public static boolean hasBall(Robot r, WorldState ws) {
		//Verify difference between Orientation Angle & Robot-to-Ball angle
		double orientationAngle = ws.getRobotOrientation(r.type, r.colour);
		double robotToBallAngle = Orientation.getAngle(ws.getRobotPoint(r), ws.getBallPoint());

		double difference = Math.abs(orientationAngle - robotToBallAngle);		


		if(difference <= 0.15){
			// TODO figure out good value
			//Verify distance between Robot & Ball
			double distance = getDistance(ws.getRobotPoint(r), ws.getBallPoint());
			
			if (distance <= 40){
				// TODO figure out actual value
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Find out which robot has the ball
	 * @param ws Handle to WorldState
	 * @return Robot that has the ball or null if nobody has the ball
	 */
	public static Robot getRobotWithBall(WorldState ws) {
		for (Robot r: Robot.listAll()) {
			if (hasBall(r, ws)) {
				// This robot has the ball :o
				return r;
			}
		}
		
		// Nobody has the ball :p
		return null;
	}
}
