package behavior;

import java.awt.Point;

import common.Robot;
import communication.RobotCommand;
import communication.Server;
import constants.C;
import constants.RobotType;
import vision.Orientation;
import vision.WorldState;
import lejos.robotics.subsumption.Behavior;

/**
 * General Behavior class. To create a behavior you must first set
 * the conditions for when the behavior triggers through takeControl()
 * and then the actions the behavior does through action().
 * 
 * You can think of action() as running in a big loop, if the takeControl()
 * conditions are still true, then action() will be run again and again until
 * takeControl() stops being true. So the implementer should make sure that
 * action() does small computations and exits quickly so it can be called
 * again.
 * 
 * @author Andre
 */
public abstract class GeneralBehavior implements Behavior {
	public static final double ANGLE_ERROR = 0.20; //15
	public static final double DISTANCE_ERROR = 25; //30
	
	protected boolean isActive = false;
	protected WorldState ws;
	protected RobotType type;
	protected Server s;
	
	// Variables for keeping track of the state of the robot
	// NB - these are behavior-specific, if you switch behaviors
	// then these will not carry on!!
	protected boolean isRotating = false;
	protected boolean isMoving = false;
	protected boolean isMovingUp = false;
	protected boolean isMovingDown = false;
	protected boolean isAimingLeft = false;
	protected boolean isAimingRight = false;
	
	
	/**
	 * Whether to print debug statements 
	 */
	private static boolean DEBUG = true;
	
	/**
	 * Prints a debug statement only if the DEBUG flag is set
	 * @param s Message to print
	 */
	public void d(String s) {
		if (DEBUG) {
			System.out.println(type + " :" + s);
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
	
	/* ------------------------------------- */
	/* --- Getters and Setters           --- */
	/* ------------------------------------- */
	
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
	public boolean rotateTo(double angle) {
		double orientation = ws.getRobotOrientation(type, ws.getColour());
		
		// Find the quickest angle to rotate towards our target
		double turnAngle = StrategyHelper.angleDiff(orientation, angle);
		
		// Check if we need to rotate at all
		if (Math.abs(turnAngle) > ANGLE_ERROR) {
			if (turnAngle > C.A30) {
				// Rotate fast if more than 30' away
				if (turnAngle < 0) {
					s.send(type, RobotCommand.CCW);
				} else {
					s.send(type, RobotCommand.CW);
				}
			} else {
				// Rotate slow if less than 30' away
				if (turnAngle < 0) {
					s.send(type, RobotCommand.SLOW_CCW);
				} else {
					s.send(type, RobotCommand.SLOW_CW);
				}
			}
			
			d("rotating");
			isRotating = true;
			
			return false;
		}
		
		return true;
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
		
		Point safeTarget = StrategyHelper.safePoint(ws, robot(), target);
		
		Point robot = ws.getRobotPoint(robot());
		double orientation = ws.getRobotOrientation(robot());
		double targetAngle = Orientation.getAngle(robot, safeTarget);
		double targetAngleComplement = StrategyHelper.angleComplement(targetAngle);

		int direction; 
//		d(orientation + " " + targetAngle + " " + targetAngleComplement);
//		d(Math.abs(StrategyHelper.angleDiff(orientation, targetAngle)) + " " + Math.abs(StrategyHelper.angleDiff(orientation, targetAngleComplement)));
//		d("Robot pos: "+ robot);
		d("Target: " + safeTarget);
		if (StrategyHelper.getDistance(robot, safeTarget) <= DISTANCE_ERROR) {
			// Already close enough, don't do anything
//			d("already close enough, stopping");
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
		if (StrategyHelper.getDistance(robot, safeTarget) > DISTANCE_ERROR) {
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
	 * @param safeTarget Target point.
	 * @return True if we have reached the target, False otherwise
	 */
	public boolean goTo(Point target) {
		// Correct the angle
		Point robot = ws.getRobotPoint(robot());
		Point safeTarget = StrategyHelper.safePoint(ws, robot(), target);
		
		double orientation = Orientation.getAngle(robot, safeTarget);
		 
		
		if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR)) {
			rotateTo(orientation);
			return false;
		}
		
		stopRotating();
		
		// Move forward until we get there
		if (StrategyHelper.getDistance(robot, safeTarget) > DISTANCE_ERROR) {
			s.send(type, RobotCommand.FORWARD);
			isMoving = true;
			return false;
		}
		
		stopMovement();
		
		// We're there!
		return true;
	}
	
	public boolean goDiagonallyTo(Point target) {
		// Correct the angle
		Point robot = ws.getRobotPoint(robot());
		double orientation = Orientation.getAngle(robot, target);
		
		if (StrategyHelper.getDistance(robot, target) > DISTANCE_ERROR - 12) {
			isMoving = true;
			s.sendDiagonalMovement(type, (int) Math.toDegrees(orientation));
			return false;
		}
		
		// We're there!
		return true;
	}
	
	
	/**
	 * Move the robot left. Note that this is relative
	 * to the robot, not the vision system!
	 */
	public void moveLeft() {
		isMoving = true;
		s.send(type, RobotCommand.MOVE_LEFT);
	}
	
	/**
	 * Move the robot right. Note that this is relative
	 * to the robot, not the vision system!
	 */
	public void moveRight() {
		isMoving = true;
		s.send(type, RobotCommand.MOVE_RIGHT);
	}
	
	/**
	 * Kick left. Do NOT call more than once. Kthnx
	 */
	public void kickLeft() {
		s.send(type, RobotCommand.KICK_LEFT);
	}
	
	/**
	 * Kick right. Do NOT call more than once. Kthnx
	 */
	public void kickRight() {
		s.send(type, RobotCommand.KICK_RIGHT);
	}
	
	/**
	 * Aim left. Do NOT call more than once.
	 */
	public void aimLeft() {
		s.send(type, RobotCommand.AIM_LEFT);
		isAimingLeft = true;
	}
	
	/**
	 * Aim right. Do NOT call more than once.
	 */
	public void aimRight() {
		s.send(type, RobotCommand.AIM_RIGHT);
		isAimingRight = true;
	}
	
	/**
	 * Reset the aim to its original position.
	 */
	public void aimReset() {
		s.send(type, RobotCommand.AIM_RESET);
		isAimingLeft = false;
		isAimingRight = false;
	}
	
	/**
	 * Stops moving if we're rotating or moving. Call this after every
	 * change from rotation to movement or vice versa!!!
	 */
	private void stopMovement() {
		if (isMoving || isRotating) {
			stop();
			isMoving = false;
			isRotating = false;
		}
	}
	
	/**
	 * Sends the STOP command
	 */
	protected void stop() {
		d("Stopping");
		s.send(type, RobotCommand.STOP);
	}
	
	/**
	 * Stops rotating if we're currently rotating.
	 */
	protected void stopRotating() {
		if (isRotating) {
			stop();
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
