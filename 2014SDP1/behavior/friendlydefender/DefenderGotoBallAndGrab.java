package behavior.friendlydefender;


import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import sdp.vision.WorldState;
import constants.RobotType;

public class DefenderGotoBallAndGrab extends GeneralBehavior {

	public DefenderGotoBallAndGrab(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		d("going to ball and grabbing");
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		d("going to ball");
		
		if (goToBall()) {
			// We're at the ball, so grab it
			System.out.println("GRABBING");
			s.send(type, RobotCommand.GRAB);
			ws.setRobotGrabbedBall(robot(), true);
		}
	}

	/** 
	 * Triggers if we 
	 * 1) do NOT have the ball
	 * 2) the ball is on the pitch 
	 * 3) AND the ball is in our quadrant on the pitch 
	 * 4) AND the ball is 'relatively' still
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return (!ws.getRobotGrabbedBall(robot()) && 
				 ws.onPitch(ws.getBallPoint()) && 
				 ws.getBallQuadrant() == ws.getRobotQuadrant(robot()) &&
			    !ws.ballIsMoving(0.05));
	}
}
