package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import common.Robot;
import constants.C;
import sdp.vision.WorldState;
import lejos.robotics.subsumption.Behavior;

public abstract class GeneralBehavior implements Behavior {
	public static final double ANGLE_ERROR = 0.3;
	public static final double DISTANCE_ERROR = 0.1;
	
	protected boolean isActive = false;
	protected WorldState ws;
	protected Robot r;
	protected Server s;
	
	public GeneralBehavior(WorldState ws, Robot r, Server s) {
		this.ws = ws;
		this.r = r;
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
	
	public Robot getRobot() {
		return r;
	}

	public void setRobot(Robot r) {
		this.r = r;
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
	
	/* ------------------------------------- */
	/* --- General robot control helpers --- */
	/* ------------------------------------- */
	
	/**
	 * Rotates the robot to the specified angle
	 * @param angle The angle (in rad) the robot will be facing by the end
	 */
	public void rotateTo(double angle) {
		// Find the complement angle, aka the angle 180deg 
		// from the angle we want to turn to
		double angleComplement = angle + C.A180;
		if (angleComplement > C.A360) {
			angleComplement -= C.A360;
		}

		// Now rotate to the correct angle
		while (isActive) {
			double orientation = ws.getRobotOrientation(r.type, r.colour);
			System.out.print("Robot orientation: " + orientation + " | ");
			
			if (orientation > (angle - ANGLE_ERROR) && orientation < angleComplement) {
				// Closer to go clockwise
				s.send(0, RobotCommand.CW);
			} else if (orientation < (angle - ANGLE_ERROR) && orientation > angleComplement) {
				// Closer to go counterclockwise
				s.send(0, RobotCommand.CCW);
			} else {
				// Reached the angle, stop
				break;
			}
		}
	}
}
