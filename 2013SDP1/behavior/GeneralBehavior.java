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
	public static final double ANGLE_ERROR = 0.15; //15
	public static final double DISTANCE_ERROR = 2;
	
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
	 * Rotates the robot to the specified angle
	 * @param angle The angle (in rad) the robot will be facing by the end
	 * @author Andre
	 */
	public void rotateTo(double angle) {
		// Find the complement angle, aka the angle 180deg 
		// from the angle we want to turn to
		double angleComplement = angle + C.A180;
		if (angleComplement > C.A360) {
			angleComplement -= C.A360;
		}

		// Now rotate to the correct angle
		double orientation = ws.getRobotOrientation(type, ws.getColour());
		
		double turnAngle = angle - orientation;
		if (turnAngle > C.A180) {
			turnAngle -= C.A360;
		}
		if (turnAngle < - C.A180) {
			turnAngle += C.A360;
		}
		
		if (turnAngle < 0) {
			s.send(type, RobotCommand.CCW);
		} else {
			s.send(type, RobotCommand.CW);
		}
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
		
		// Move forward until we get there
		// TODO figure out what 20 should be
		if (StrategyHelper.getDistance(robot, target) > DISTANCE_ERROR + 20) {
			s.send(type, RobotCommand.FORWARD);
			return false;
		}
		
		// We're there, so chill
		s.send(type, RobotCommand.STOP);
		return true;
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
