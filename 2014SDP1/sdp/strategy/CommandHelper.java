package sdp.strategy;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import sdp.communication.CommsClient;
import sdp.communication.CommsInterface;
import sdp.geom.LineMethods;
import sdp.gui.MainWindow;
import sdp.navigation.AStarPathfinding;
import sdp.navigation.Movement;
import sdp.navigation.PIDMovement;
import sdp.navigation.PIDRotation;
import sdp.navigation.Pathfinding;
import sdp.navigation.Rotation;
import sdp.vision.Circle;
import sdp.vision.Drawable;
import sdp.vision.DrawablePolygon;
import sdp.vision.ImageProcessor;
import sdp.vision.NoAngleException;
import sdp.vision.RunVision;
import sdp.vision.Vision;
import sdp.vision.WorldState;

import static sdp.geom.LineMethods.*;

/**
 * 
 * all deprecated methods are not being used. However, this doesn't mean it
 * won't be used.
 * 
 * @author Simona Cartuta
 * @author Catalina Predoi
 */
public class CommandHelper {
	CommsClient mCommsClient;
	WorldState mWorldState;
	Pathfinding pathFinder;
	public Rotation rotation;
	public Movement movement;
	static Vision mVision;
	// for obstacleDist and avoidObstacle:
	static double distance;

	public CommandHelper(Vision vision, CommsInterface comms, WorldState worldState) {
		mCommsClient = (CommsClient) comms;
		mWorldState = worldState;
		mVision = vision;
		pathFinder = new AStarPathfinding(mWorldState);
		final int sleepTime = 40;
		movement = new PIDMovement(mWorldState, pathFinder, mCommsClient, 100);
		rotation = new PIDRotation(mWorldState, comms, sleepTime);
		try {
			mCommsClient.connect();
		} catch (IOException e) {
			System.out.println("mCommsClient failed to connect");
			e.printStackTrace();
		}
	}

	public CommandHelper(CommsClient commsClient, WorldState worldState) {
		this(RunVision.setupVision(worldState), commsClient, worldState);
	}

	/**
	 * Gives the distance between our robot and the point it receives.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	//TODO Alter for Attacker Robot
	public double distanceFrom(Point point) {
		Point2D ourPoint = mWorldState.getOurDefenderPosition();
		return ourPoint.distance(point);
	}

	/**
	 * @author Jackie Shek
	 * @return
	 */
	public boolean someoneScored() {
		//System.out.println("BallX: " + mWorldState.getBallX() + "\n GoalX: " + mWorldState.getLeftGoalPoint().getX() + "\n GoalX: "
		//		+ mWorldState.getRightGoalPoint().getX());
		return (mWorldState.getBallVisible()&&(mWorldState.getBallX() <= mWorldState.getLeftGoalPoint().getX() || mWorldState.getBallX() >= mWorldState.getRightGoalPoint().getX()));
	}

	/**
	 * Returns the angle by which our direction must change in order to have the
	 * point straight ahead of us. Positive angle means counterclockwise
	 * rotation and negative angel means clockwise rotation.
	 * 
	 * @param x
	 * @param y
	 * @throws NoAngleException
	 * @returnBasicStrategy
	 */
	public double ourAngleTo(Point point) {
		//return Position.angleTo(mWorldState.getOurPosition(), point);
		return ourAngleTo(point.x, point.y);
	}
	
	/**
	 * Calculates the intersection point between the two lines:
	 * line of the moving ball and x = starting x Co-ord of robot.
	 * Used for intercepting the ball when we defend a penalty
	 * @param ballStartPos The position the ball started at
	 * @param holdThisX Keep the intercept point at this position on the x-axis
	 * @return
	 */
	public Point getInterceptPoint(Point ballStartPos, int holdThisX) {
		Point ballCurPos = mWorldState.getBallPoint();
		double m = findGradient(ballStartPos,ballCurPos);
		int y = (int)((m*(holdThisX-ballStartPos.x)) + ballStartPos.y);
		Point inters = new Point(holdThisX,y);
		return inters;
	}

	/** Given two points, find the gradient of the line joining them.
	 * 
	 * @param u
	 * @param v
	 * @return
	 */
	public static double findGradient(Point u, Point v){
		double gradient = (u.getY()-v.getY())/((double)(u.getX()-v.getX()));
		return gradient;
	}
	
	/**
	 * Returns the angle with respect to our orientation
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	//TODO Alter for Attacker Robot
	public double ourAngleTo(int x, int y) {
		double o = mWorldState.getOurDefenderOrientation();
		int ourX = mWorldState.getOurDefenderX();
		int ourY = mWorldState.getOurDefenderY();
		Line2D line1 = new Line2D.Double(ourX, ourY, ourX + Math.cos(o), ourY + Math.sin(o));
		Line2D line2 = new Line2D.Double(ourX, ourY, x, y);
		double angle1 = Math.atan2(line1.getY1() - line1.getY2(), line1.getX1() - line1.getX2());
		double angle2 = Math.atan2(line2.getY1() - line2.getY2(), line2.getX1() - line2.getX2());
		double angle = -1 * (angle1 - angle2);
		if (angle > Math.PI) {
			angle -= 2 * Math.PI;
		}
		if (angle < -Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}

	/**
	 * Returns angle with respect to OX
	 * 
	 * @param point
	 * @return a value between -PI and PI
	 */
	//TODO Alter for Attacker Robot
	public double absAngleTo(Point point) {
		int ourX = mWorldState.getOurDefenderX();
		int ourY = mWorldState.getOurDefenderY();
		Line2D line1 = new Line2D.Double(ourX, ourY, ourX + 100, ourY);
		Line2D line2 = new Line2D.Double(ourX, ourY, point.x, point.y);
		double angle1 = Math.atan2(line1.getY1() - line1.getY2(), line1.getX1() - line1.getX2());
		double angle2 = Math.atan2(line2.getY1() - line2.getY2(), line2.getX1() - line2.getX2());
		double angle = -1 * (angle1 - angle2);
		if (angle > Math.PI) {
			angle -= 2 * Math.PI;
		}
		if (angle < -Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}
	
	public void facePoint(Point point) throws IOException{
		facePoint(point, 0.25, 0.4);
	}
	
	public void facePoint(Point point, double speed, double radTolerance) throws IOException{
		double angle = absAngleTo(point);
		if (angle < 0)
			angle += 2 * Math.PI;
		if (angle > 2 * Math.PI)
			angle -= 2 * Math.PI;
		rotation.setTargetAngle(angle, speed, radTolerance);
	}

	public void blockingFacePoint(Point point) throws IOException, NoAngleException, InterruptedException {
		float DEFAULT_TOLERANCE = (float) 0.15;
		blockingFacePoint(point, DEFAULT_TOLERANCE);
	}

	/**
	 * Turn to face given point.
	 * 
	 * @author milestone2
	 * @param x
	 * @param y
	 * @param radTolerance
	 *            maximum error allowed for robot's orientation.
	 */

	public void blockingFacePoint(Point point, double radTolerance) throws IOException, NoAngleException, InterruptedException {
		float DEFAULT_SPEED = (float) 0.15;
		blockingFacePoint(point, radTolerance, DEFAULT_SPEED);
	}

	public void blockingFacePoint(Point point, double radTolerance, float startSpeed) throws IOException, NoAngleException, InterruptedException {
		double speed = 0.8;
		double angle = 0;
		angle = absAngleTo(point);
		if (angle < 0)
			angle += 2 * Math.PI;
		System.out.println("abs angle to smth = " + angle);
		if (angle > 2 * Math.PI)
			angle -= 2 * Math.PI;
		System.out.println("Our aim is to rotate by " + angle);
		rotation.setTargetAngle(angle, speed, 0.05);
		sleep(4000);
		while (rotation.isRotating() && speed > 0.15) {
			angle = absAngleTo(point);
			System.out.println("Trying to rotate at an angle " + angle + "with a speed of" + speed);
			if (speed > 0.1) {
				speed = speed * 0.75;
			}
			if (angle > 2 * Math.PI)
				angle -= 2 * Math.PI;
			if (angle < 0)
				angle += 2 * Math.PI;
			rotation.setTargetAngle(angle, speed, 0.10);
			Thread.sleep(3000);
		}
		rotation.stopRotating();
		// rotation.setTargetAngle(0, speed, 0.2);
		// rotation.setRotating(true);
		//
		// Thread.sleep(4000);
		//
		// stopRotating();
		//
		// System.out.println(mWorldState.getOurOrientation());
		//
		// rotation.setTargetAngle(Math.PI, speed, 0.3);
		// rotation.setRotating(true);
		//
		// Thread.sleep(4000);
		//
		//
		// rotation.setRotating(false);
		// stopRotating();
	}

	public void blockingFaceBall() {
		try {
			blockingFacePoint(mWorldState.getBallPoint());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoAngleException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void blockingFaceBall(double tolerance) {
		try {
			blockingFacePoint(mWorldState.getBallPoint(), tolerance);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoAngleException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns to face the ball.
	 * 
	 * @author milestone2
	 * @param tolerance
	 * @throws NoAngleException
	 */
	public void blockingFaceBall(double tolerance, float speed) throws NoAngleException {
		try {
			blockingFacePoint(mWorldState.getBallPoint(), tolerance, speed);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns the robot to face the direction indicated by the parameter.
	 * 
	 * @param angle
	 * @throws IOException
	 */
	public void rotateToAngle(double angle, double radTolerance) throws IOException {
		double DEAFULT_SPEED = 150;
		do {
			// try {
			// angle = Position.angle(mWorldState.getOurPosition(),
			// mWorldState.getOppositionGoalCentre());
			// System.out.println("angle = " + angle);
			// } catch (NoAngleException e) {
			// System.out.println("No angle could be found");
			// e.printStackTrace();
			// } we are supposed to rotate to angle given not compute angle to
			// goal
			rotation.setTargetAngle(angle, DEAFULT_SPEED, radTolerance);
			sleep(4000);
			try {
				mCommsClient.stopRotating();
			} catch (IOException e2) {
				System.out.println("commsInterface failed to stopRotating ");
				e2.printStackTrace();
			}
			System.out.println("Finished Turning");
		} while (Math.abs(angle) > radTolerance);
		stopRotating();
	}

	/**
	 * Goes to face given point.
	 * 
	 * @author milestone2
	 * @param x
	 * @param y
	 * @param tolerance
	 *            maximum distance robot can be away from the given point.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void goToPoint(Point point) throws IOException {
		// try {
		// facePoint(point, 0.1, (float)0.2);
		// } catch (NoAngleException e) {
		// System.out.println("Already on point");
		// e.printStackTrace();
		// }
		// System.out.println("Should be facing point");
		//System.out.println("CH: "+ point);
		movement.setTarget(point);
	}

	/**
	 * Turns to face ball. The robot goes to it correcting it's orientation.
	 * 
	 * @author milestone2
	 * @param tolerance
	 * @param turnTolerance
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void goToBall() throws IOException, InterruptedException {
		movement.setAvoidBall(false);
		goToPoint(mWorldState.getBallPoint());
	}

	/**
	 * Stops all robot movement.
	 * 
	 * @author milestone2
	 */
	public void stop() {
		stopMoving();
		stopRotating();
	}

	/**
	 * Stops robot from moving.
	 * 
	 * @author milestone2
	 */
	public void stopMoving() {
		while (true) {
			try {
				movement.stopMoving();
			} catch (IOException e) {
				continue;
			}
			//System.out.println("commandHelper.stopMoving: done :)");
			return;
		}
	}

	/**
	 * Stops the robot from rotating.
	 * 
	 * @author milestone2
	 */
	public void stopRotating() {
		while (true) {
			try {
				rotation.stopRotating();
			} catch (IOException e) {
				continue;
			}
			return;
		}
	}

	public void kick() {
		try {
			mCommsClient.kick();
		} catch (IOException e) {
			System.out.println("Kick failed");
			e.printStackTrace();
		}
	}

	/*
	 * FUTURE / EXPERIMENTAL STUFF
	 */

	/**
	 * Strafes left around given point.Should work if the robot can rotate while
	 * it moves.
	 * 
	 * @param x
	 * @param y
	 * @param angle
	 *            TODO: ensure robot doesn't get too close or too far from
	 *            target. + same for strafeRight
	 */
	public void strafeLeft(int x, int y, double angle) {
		try {
			mCommsClient.move(0/* left */);
		} catch (IOException e) {
			e.printStackTrace();
		}
		do {
			// facePoint(x,y,0.1);
			//TODO Alter for Attacker Robot
		} while (mWorldState.getOurDefenderOrientation() == angle);

	}

	/**
	 * Should work if the robot can rotate while it moves.
	 * 
	 * @param x
	 * @param y
	 * @param angle
	 */
	public void strafeRight(int x, int y, double angle) {
		try {
			mCommsClient.move(0/* right */);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		do {
			// facePoint(x,y,0.1);
			//TODO Alter for Attacker Robot
		} while (mWorldState.getOurDefenderOrientation() == angle);
	}

	/**
	 * Assuming robot has ball, turns to face goal and kicks.
	 * 
	 * @param aim
	 */
	public void kickGoal() {
		try {
			mCommsClient.move(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			mCommsClient.kick();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Assuming robot has ball, robot kicks LHS wall to score goal.
	 * 
	 * @param aim
	 */
	//TODO Alter for Attacker Robot
	public void kickBankLeft(Point aim) {
		int hDistance;
		Point kickingAim;
		// if aim is left goal
		if (aim.x == 0) {
			hDistance = (int) (mWorldState.getOurDefenderX() / (1 + 2 * mWorldState.getOurDefenderY() / mWorldState.getPitchWidth()));
			kickingAim = new Point((int) mWorldState.getPitchWidth(), hDistance);
		} else {
			// if aim is right goal
			hDistance = (int) ((aim.x - mWorldState.getOurDefenderX()) / (1 + 2 * mWorldState.getOurDefenderY() / mWorldState.getPitchWidth()));
			kickingAim = new Point((int) mWorldState.getPitchWidth(), hDistance);
		}
		System.out.print(kickingAim);
		// kickGoal(kickingAim);
	}

	/**
	 * Assuming robot has ball, robot kicks RHS wall to score goal.
	 * 
	 * @param aim
	 */
	public void kickBankRight(Point aim) {

	}
	//TODO Alter for Attacker Robot
	public int ourDistanceFrom(Point ballPoint) {
		Point us = mWorldState.getOurDefenderPosition();
		return (int) ballPoint.distance(us);
	}
	
	public boolean weHaveBall(WorldState ws, ImageProcessor img, double rotationTolerance, double distance){
        Point kickPoint = KickFrom.whereToKickFrom(ws, (int)distance);
        
        final int distanceAtKickPoint = 100;
        final int minDistAtKickPoint = 75;
        
        final int distanceAtBall = 30;
        
        Line2D ballToKickPoint = new Line2D.Double(ws.getBallPoint(), kickPoint);
//        
       Line2D lineNormalToGoalThroughBall = LineMethods.normalLine(ballToKickPoint, ws.getBallPoint(), distanceAtBall);
       Point goalBottomToBall = new Point((int) (lineNormalToGoalThroughBall.getX1()-ws.getOppositionGoalBottom().getX()), (int) (lineNormalToGoalThroughBall.getY1()-ws.getOppositionGoalBottom().getY()));
       Point goalTopToBall = new Point((int) (lineNormalToGoalThroughBall.getX2()-ws.getOppositionGoalTop().getX()), (int) (lineNormalToGoalThroughBall.getY2()-ws.getOppositionGoalTop().getY()));
       double magnitude = Math.sqrt(Math.pow(goalBottomToBall.getX(),2) + Math.pow(goalBottomToBall.getX(),2));
       goalBottomToBall = new Point((int) (goalBottomToBall.getX()*distanceAtKickPoint/magnitude), (int) (goalBottomToBall.getY()*distanceAtKickPoint/magnitude));
       magnitude = Math.sqrt(Math.pow(goalTopToBall.getX(),2) + Math.pow(goalTopToBall.getX(),2));
       goalTopToBall = new Point((int) (goalTopToBall.getX()*distanceAtKickPoint/magnitude), (int) (goalTopToBall.getY()*distanceAtKickPoint/magnitude));
       goalBottomToBall.translate((int) lineNormalToGoalThroughBall.getX1(), (int) lineNormalToGoalThroughBall.getY1());
       goalTopToBall.translate((int) lineNormalToGoalThroughBall.getX2(), (int) lineNormalToGoalThroughBall.getY2());
       
       ArrayList<Drawable> draw = new ArrayList<Drawable>();
       
       Line2D lineNormalThroughKickPoint = new Line2D.Double(goalBottomToBall, goalTopToBall);
       //this is an ugly hack to prevent the kick area becoming too small
       if ( lineLengthSq(lineNormalThroughKickPoint) < minDistAtKickPoint*minDistAtKickPoint ){
    	   lineNormalThroughKickPoint  = LineMethods.normalLine(ballToKickPoint, kickPoint, minDistAtKickPoint);
       }else{
           draw.add(new Circle(Color.BLACK, new Point((int) (goalBottomToBall.getX()), (int) (goalBottomToBall.getY())),5));
           draw.add(new Circle(Color.BLACK, new Point((int) (goalTopToBall.getX()), (int) (goalTopToBall.getY())),5));   
       }
        
        Polygon hasBallArea = new Polygon();
                        
        hasBallArea.addPoint((int)lineNormalToGoalThroughBall.getX1(), (int)lineNormalToGoalThroughBall.getY1());
        hasBallArea.addPoint((int)lineNormalThroughKickPoint.getX1(), (int)lineNormalThroughKickPoint.getY1());
        hasBallArea.addPoint((int)lineNormalThroughKickPoint.getX2(), (int)lineNormalThroughKickPoint.getY2());
        hasBallArea.addPoint((int)lineNormalToGoalThroughBall.getX2(), (int)lineNormalToGoalThroughBall.getY2());
        
        if ( img != null ){
                draw.add(new DrawablePolygon(Color.BLUE, hasBallArea));
        }
        //TODO Alter for Attacker Robot
        MainWindow.addOrUpdateDrawable("HaveBall", draw);
        return hasBallArea.contains(ws.getOurDefenderPosition());
	}

	public void setAvoidBall(boolean avoidBall) {
		movement.setAvoidBall(avoidBall);
	}

	// TODO: review driveBallToGoal
	public void driveBallToGoal(WorldState worldState, ImageProcessor imageProcessor) {
		stopAvoidingBall();
		try {
			movement.setTarget(mWorldState.getBallPoint());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// wait till we get to the ball
		while (movement.isMoving() && mWorldState.getBallVisible()) {
			sleep(50);
		}
		// go to the goal
		try {
			movement.setTarget(mWorldState.getOppositionGoalCentre());
		} catch (IOException e) {
			System.out.println("Exception thrown while trying to use movement.setTarget");
			e.printStackTrace();
		}
		System.out.println("Go to goal!");
		//TODO Alter for Attacker Robot
		while (Math.abs(mWorldState.getOppositionGoalCentre().x - mWorldState.getOurDefenderPosition().x) > 180) {
			boolean isInFrontOfBall = weHaveBall(worldState, imageProcessor, 0.2, 50);
			boolean isBallFarAway = mWorldState.getBallPoint().distance(mWorldState.getOurDefenderPosition()) > 75;
			System.out.print("see ball?" + mWorldState.getBallVisible() + "        ");
			System.out.print("front of ball?" + isInFrontOfBall + "        ");
			System.out.println("isBallFarAway?" + isBallFarAway);
			// What do we do when we can't see the ball?
			if (!isInFrontOfBall) {
				return;
			}
		}
		if (weHaveBall(worldState, imageProcessor, 0.2, 50)) {
			kickGoal();
		}
	}
	//TODO Alter for Attacker Robot
	public boolean opponentHasBall() {
		double distance = mWorldState.getBallPoint().distance(mWorldState.getOppositionDefenderPosition());
		return distance < 20;
	}

	public void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isMoving() {
		return movement.isMoving();
	}

	public boolean isRotating() {
		return rotation.isRotating();
	}
	
	public void startAvoidingBall() {
		pathFinder.setAvoidBall(true);
	}
	
	public void stopAvoidingBall() {
		pathFinder.setAvoidBall(false);
	}
	
	public boolean isFacingLine(Line2D line) {
		return isFacingLine(new Point((int)line.getX1(), (int)line.getY1()),
							new Point((int)line.getX2(), (int)line.getY2()));
	}
	
	/**
	 * works with 0 - 2*PI angles. To make it work for -Pi to Pi angles replace || with &&
	 * @param top
	 * @param bottom
	 * @return
	 */
	public boolean isFacingLine(Point top, Point bottom) {
		boolean answer = false;
		answer = ourAngleTo( bottom ) * ourAngleTo( top ) < 0 ;
		System.out.println(ourAngleTo( top ));
		System.out.println(ourAngleTo( bottom ));
		System.out.println();
		return answer;
	}

}