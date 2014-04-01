package behavior.finaldefender;


import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.Strategy;
import sdp.vision.WorldState;
import constants.RobotType;

public class DefenderGotoBallAndGrab extends GeneralBehavior {

	public DefenderGotoBallAndGrab(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		d("going to ball and grabbing");
		
		// Mark the grabber as being possibly closed
		state().grabberState = 1;
		
		// Reset the target counter
		state().defenderNumberOfTargetsTried = 0;
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		d("going to ball");
		
		// Global flag to let the attacker know we're doing a pass.
		// NB - the attacker is responsible for setting this to false when he realises
		// the pass isn't happening any more!
		ws.setDoingPass(true);
		
		if (goToBallDefender()) {
			// We're at the ball, so grab it
			System.out.println("GRABBING");
			s.send(type, RobotCommand.CLOSE_GRABBER);
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
