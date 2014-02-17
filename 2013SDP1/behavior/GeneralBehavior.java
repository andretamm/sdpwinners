package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import common.Robot;
import constants.C;
import constants.RobotType;
import sdp.vision.WorldState;
import lejos.robotics.subsumption.Behavior;

public abstract class GeneralBehavior implements Behavior {
	public static final double ANGLE_ERROR = 0.15;
	public static final double DISTANCE_ERROR = 25;
	
	protected boolean isActive = false;
	protected WorldState ws;
	protected RobotType type;
	protected Server s;
	
	protected boolean isRotating = false;
	protected boolean isMoving = false;
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
			s.send(0, RobotCommand.CW);
		} else {
			s.send(0, RobotCommand.CCW);
		}
	}
}
