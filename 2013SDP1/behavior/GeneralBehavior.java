package behavior;

import java.awt.Point;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import common.Robot;
import constants.C;
import constants.RobotType;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import lejos.robotics.subsumption.Behavior;

public abstract class GeneralBehavior implements Behavior {
	public static final double ANGLE_ERROR = 0.20; //15
	public static final double DISTANCE_ERROR = 30;
	
	protected boolean isActive = false;
	protected WorldState ws;
	protected RobotType type;
	protected Server s;
	
	protected boolean isRotating = false;
	protected boolean isMoving = false;
	protected boolean isMovingUp = false;
	protected boolean isMovingDown = false;
	protected int movingCounter = 0;
	protected int sentCounter = 0;
	protected int rotatingCounter = 0;
	protected int stopCounter = 0;
	protected int btCounter = 0;
	
	private static boolean DEBUG = true;
	
	public void d(String s) {
		if (DEBUG) {
			System.out.println(s);
		}
	}
	
	public GeneralBehavior(WorldState ws, RobotType type, Server s) {
		this.ws = ws;
		this.type = type;
		this.s = s;
	}
	
	@Override
	public void action() {
		setActive();
	}
	
	@Override
	public void suppress() {
		setInActive();
	}
	
	public RobotType getType() {
		return type;
	}

	public void setType(RobotType type) {
		this.type = type;
	}

	public void setActive() {
		this.isActive = true;
	}
	
	public void setInActive() {
		this.isActive = false;
	}
	
	public boolean isActive() {
		return this.isActive;
	}

	public WorldState getWorldState() {
		return ws;
	}

	public void setWorldState(WorldState ws) {
		this.ws = ws;
	}
	
	public Robot robot() {
		return ws.getOur(type);
	}
	
	/* ------------------------------------- */
	/* --- General robot control helpers --- */
	/* ------------------------------------- */
	
	/**
	 * Rotates the robot to the specified angle. Does nothing if already
	 * facing the right direction
	 * @param angle The angle (in rad) the robot will be facing by the end
	 * @author Andre
	 */
	public void rotateTo(double angle) {
		double orientation = ws.getRobotOrientation(type, ws.getColour());
		
		// Check if we need to rotate at all
		if (Math.abs(StrategyHelper.angleDiff(orientation, angle)) > ANGLE_ERROR) {
			// Find the quickest angle to rotate towards our target
			double turnAngle = StrategyHelper.angleDiff(orientation, angle);
			
			// Now rotate
			if (turnAngle < 0) {
				s.send(type, RobotCommand.CCW);
			} else {
				s.send(type, RobotCommand.CW);
			}
			d("rotating");
			isRotating = true;
		}
	}
	
	/**
	 * Takes the robot to the specified point. Rotates to the closest for driving
	 * straight towards the point (either forward or backward). Then
	 * drives forward/backward to reach the point. If you want to turn towards the target
	 * and then move, use goTo() instead.
	 * @param target Target point.
	 * @return True if we have reached the target, False otherwise
	 */
	public boolean quickGoTo(Point target) {
		
		Point robot = ws.getRobotPoint(robot());
		double orientation = ws.getRobotOrientation(robot());
		double targetAngle = Orientation.getAngle(robot, target);
		double targetAngleComplement = StrategyHelper.angleComplement(targetAngle);

		int direction; 
//		d(orientation + " " + targetAngle + " " + targetAngleComplement);
//		d(Math.abs(StrategyHelper.angleDiff(orientation, targetAngle)) + " " + Math.abs(StrategyHelper.angleDiff(orientation, targetAngleComplement)));
//		d("Robot pos: "+ robot);
//		d("Target: " + target);
		if (StrategyHelper.getDistance(robot, target) <= DISTANCE_ERROR) {
//			d("already close enough, stopping");
			stopMovement();
			return true;
		}

		if (Math.abs(StrategyHelper.angleDiff(orientation, targetAngle)) 
		    < Math.abs(StrategyHelper.angleDiff(orientation, targetAngleComplement))) {
			// Closer to go to targetAngle
//			d("closer to go to angle");
			direction = RobotCommand.FORWARD;
			
			if (Math.abs(StrategyHelper.angleDiff(orientation, targetAngle)) > ANGLE_ERROR) {
				// Still need to rotate
				rotateTo(targetAngle);
				return false;
			}
		} else {
			// Closer to go to the complement and then move backward
//			d("closer to go to complement");
			direction = RobotCommand.BACK;
			
			if (Math.abs(StrategyHelper.angleDiff(orientation, targetAngleComplement)) > ANGLE_ERROR) {
				// Still need to rotate
				rotateTo(targetAngleComplement);
				return false;
			}
		}
		
		stopRotating();
		
		// Now move to target point
		if (StrategyHelper.getDistance(robot, target) > DISTANCE_ERROR) {
			d("Moving in direction " + direction);
			s.send(type, direction);
			isMoving = true;
			return false;
		}
		
		stopMovement();
		
		// We're there!
		return true;
	}
	
	/**
	 * Takes the robot to the specified point. Rotates to the correct angle, then drives forward.
	 * @param target Target point.
	 * @return True if we have reached the target, False otherwise
	 */
	public boolean goTo(Point target) {
		// Correct the angle
		Point robot = ws.getRobotPoint(robot());
		double orientation = Orientation.getAngle(robot, target);
		
		if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR)) {
			rotateTo(orientation);
			return false;
		}
		
		stopRotating();
		
		// Move forward until we get there
		if (StrategyHelper.getDistance(robot, target) > DISTANCE_ERROR) {
			s.send(type, RobotCommand.FORWARD);
			isMoving = true;
			return false;
		}
		
		stopMovement();
		
		// We're there!
		return true;
	}
	
	
	/**
	 * Stops moving if we're rotating or moving. Call this after every
	 * change from rotation to movement or vice versa!!!
	 */
	private void stopMovement() {
		if (isMoving || isRotating) {
			s.send(type, RobotCommand.STOP);
			isMoving = false;
			isRotating = false;
		}
	}
	
	private void stopRotating() {
		if (isRotating) {
			s.send(type, RobotCommand.STOP);
			isRotating = false;
		}
	}

	/**
	 * Goes to the ball.
	 * @return True if we're at the ball, ready for grabbing. False if we're still getting there.
	 */
	public boolean goToBall() {
		if (StrategyHelper.hasBall(robot(), ws)) {
			System.out.println("BALL IS IN RANGE FOR KICK!");
			return true;
		} else {
			goTo(ws.getBallPoint());
			return false;
		}
	}
}
