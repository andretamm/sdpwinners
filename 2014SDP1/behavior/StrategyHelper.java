package behavior;

import java.awt.Point;
import java.awt.geom.Point2D;

import common.Robot;
import constants.C;
import constants.Quadrant;
import constants.QuadrantX;
import constants.ShootingDirection;
import constants.RobotColour;
import constants.RobotType;
import sdp.vision.Orientation;
import sdp.vision.WorldState;

/**
 * Loads of methods to help with robot strategy planning. Everything
 * from vector manipulation to finding intersection points between
 * vectors & goals to calculating safe points for the robots to go to. 
 * 
 * @author Andre
 */
public class StrategyHelper {
	
	/**
	 * Safe distance from a ball so we don't hit it when trying to move
	 * e.g. behind it 
	 */
	public static final double ROBOT_SAFETY_DISTANCE = 40;
	
	/**
	 * The safe distance from the quadrant x value
	 */
	public static final int DISTANCE_FROM_QUADRANTX = 20;
	
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
		return ws.getDirection() == ShootingDirection.LEFT ? ourGoalX - 37 : ourGoalX + 40; 
	}
	
	public static double getDistance(Point a, Point b){
		return Math.sqrt(Math.pow(Math.abs(a.x - b.x),2) + Math.pow(Math.abs(a.y - b.y),2));
	}
	
	public static double pointToLineDistance(Point A, Point B, Point P) {
		//Calculates distance between point P & line created by points A & B
		double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
		return Math.abs((P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x))/normalLength;
	}
	
	/**
	 * Checks if the given robot has a ball
	 * @param r Robot to check
	 * @param ws Handle to WorldState
	 * @return True if the robot has the ball, False otherwise
	 */
	public static boolean hasBall(Robot r, WorldState ws){
		//Verify difference between Orientation Angle & Robot-to-Ball angle
		double orientationAngle = ws.getRobotOrientation(r.type, r.colour);
		double robotToBallAngle = Orientation.getAngle(ws.getRobotPoint(r), ws.getBallPoint());

		double difference = Math.abs(StrategyHelper.angleDiff(orientationAngle,robotToBallAngle));		


		if(difference <= GeneralBehavior.ANGLE_ERROR){
			// TODO figure out good value
			//Verify distance between Robot & Ball
			double distance = getDistance(ws.getRobotPoint(r), ws.getBallPoint());
			
			if (distance <= 32) {
				System.out.println(distance);
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

	/**
	 * Determine weather robot 'r's pass/shot will pass the intercepter or not
	 * @param r Robot that is trying to pass/shoot
	 * @param a Angle at which robot is trying to pass/shoot at
	 * @param ws Handle to WorldState

	 * @return True if passing/shooting path is clear; False otherwise
	 */
	public static boolean isPathClear(Robot r, double a, WorldState ws) {
		double distanceThresh = 50;
		double distance = getOpponentDistanceFromPath(r, a, ws);
		
		if(distance <= distanceThresh)
			return false;
		
		return true;
	}
	
	/**
	 * How far the opponent robot is from the line defined by our robot's position and
	 * a given shooting angle. Will only consider the opposing attacker if using our
	 * defender and opposing defender if using our attacker.
	 * @param r Robot making the shot
	 * @param angle Angle of the shot
	 * @param ws Handle to WorldState
	 * @return Distance of the opponent from the shooting line
	 */
	public static double getOpponentDistanceFromPath(Robot r, double angle, WorldState ws) {
		// Position of intercepter Robot
		Point opponentPoint;
		
		//Determine opponent Robot that might intercept our pass/shot
		if(r.colour == RobotColour.BLUE){
			if(r.type == RobotType.DEFENDER)
				opponentPoint = ws.getRobotXY(RobotColour.YELLOW, RobotType.ATTACKER);
			else
				opponentPoint = ws.getRobotXY(RobotColour.YELLOW, RobotType.DEFENDER);
		}
		else{
			if(r.type == RobotType.DEFENDER)
				opponentPoint = ws.getRobotXY(RobotColour.BLUE, RobotType.ATTACKER);
			else
				opponentPoint = ws.getRobotXY(RobotColour.BLUE, RobotType.DEFENDER);
		}
		
		// Distance from shooting/passing robot to intercepter robot
		double k = getDistance(ws.getRobotPoint(r), opponentPoint);
		
		// Angle between (shooting_Robot-intercepter_Robot) line & shooting/passing angle 'a'
		double beta = Math.abs(StrategyHelper.angleDiff(angle, Orientation.getAngle(ws.getRobotPoint(r), opponentPoint)));
		
		// Distance between intercepter robot & line created by shooting angle
		double distance = k * Math.sin(beta);
		
		return distance;
	}
	
	/**
	 * How close to an object can we get before we risk going over the quadrant x value
	 * @param ws Handle to WorldState
	 * @param r Robot going towards a point
	 * @param object A point in which the robot is trying to get to
	 * @return Closest point it can reach without going over the quadrant x value
	 */
	public static Point safePoint(WorldState ws, Robot r, Point object){
		int xObject = object.x;
		int yObject = object.y;
		Quadrant q = ws.getRobotQuadrant(r);
		
		int quadrantXLow = ws.getQuadrantX(q, QuadrantX.LOW);
		int quadrantXHigh = ws.getQuadrantX(q, QuadrantX.HIGH);
		
		int distanceFromLow = Math.abs(xObject - quadrantXLow);
		int distanceFromHigh = Math.abs(xObject - quadrantXHigh);
		
		if (distanceFromLow > DISTANCE_FROM_QUADRANTX && distanceFromHigh > DISTANCE_FROM_QUADRANTX){
			return object;
		}
		
		if(distanceFromLow < distanceFromHigh){
			return new Point(quadrantXLow + DISTANCE_FROM_QUADRANTX, yObject);
		} else {
			return new Point(quadrantXHigh - DISTANCE_FROM_QUADRANTX, yObject);
		}
		
	}

	/**
	 * If the opponent were to make a kick that would end up at the top of our goal
	 * (from their current position with a wall kick at the bottom), 
	 * where would we have to be to block it.
	 * @param ws Handle to worldstate
	 * @return Position for blocking
	 */
	public static Point findGoalTopDefendPosition(WorldState ws) {
		Point attacker = ws.getRobotPoint(ws.getOpposition(RobotType.ATTACKER));
		
		// Bottom of the pitch
		Point pitchBottom;
		
		if (ws.getDirection() == ShootingDirection.LEFT) {
			pitchBottom = ws.getPitchBottomRight();
		} else {
			pitchBottom = ws.getPitchBottomLeft();
		}
		
		// Distance of robot from pitch bottom
		double lP = Math.abs(pitchBottom.y - attacker.y);
		
		// Distance from goal top to bottom of pitch
		double l = Math.abs(pitchBottom.y - ws.getOurGoalTop().y);
		
		// Distance of robot from goal
		double k = Math.abs(pitchBottom.x - attacker.x);
		
		// Distance from goal where ball will hit the wall 
		double s = (double) (k * l) / (lP + l);
		
		// Distance from goal to defend point
		double aP = Math.abs(getDefendLineX(ws) - pitchBottom.x);
		
		// Distance from goal top to our defend point y wise
		int lPPP = (int) Math.abs(l * aP / s);
		
		return new Point(getDefendLineX(ws), ws.getOurGoalTop().y + lPPP);
	}
	
	/**
	 * If the opponent were to make a kick that would end up at the bottom of our goal
	 * (from their current position from a wall kick from the top), 
	 * where would we have to be to block it.
	 * @param ws Handle to worldstate
	 * @return Position for blocking
	 */
	public static Point findGoalBottomDefendPosition(WorldState ws) {
		Point attacker = ws.getRobotPoint(ws.getOpposition(RobotType.ATTACKER));
		
		// Top of the pitch
		Point pitchTop;
		
		if (ws.getDirection() == ShootingDirection.LEFT) {
			pitchTop = ws.getPitchTopRight();
		} else {
			pitchTop = ws.getPitchTopLeft();
		}
		
		// Distance of robot from pitch bottom
		double lP = Math.abs(pitchTop.y - attacker.y);
		
		// Distance from goal bottom to bottom of pitch
		double l = Math.abs(pitchTop.y - ws.getOurGoalBottom().y);
		
		// Distance of robot from goal
		double k = Math.abs(pitchTop.x - attacker.x);
		
		// Distance from goal where ball will hit the wall 
		double s = (double) (k * l) / (lP + l);
		
		// Distance from goal to defend point
		double aP = Math.abs(getDefendLineX(ws) - pitchTop.x);
		
		// Distance from goal top to our defend point y wise
		int lPPP = (int) Math.abs(l * aP / s);
		
		return new Point(getDefendLineX(ws), ws.getOurGoalBottom().y - lPPP);
	}

}





















