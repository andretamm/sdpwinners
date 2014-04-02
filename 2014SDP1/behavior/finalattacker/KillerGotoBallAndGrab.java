package behavior.finalattacker;


import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerGotoBallAndGrab extends GeneralBehavior {

	public KillerGotoBallAndGrab(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
//		System.out.println("Going to ball");
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		System.out.println("GotoBallAndGrab");
		
		// If the ball is in our quadrant then the pass is over
		ws.setDoingPass(false);
		ws.setKickedPass(false);
		state().attackerOrientationSetForPass = false;
		
		// This is the start of the offensive phase, initialise variable for future use
		state().attackerNumberOfTargetsTried = 0;
		
		d("going to ball");
		
		// Mark the grabber as being potentially closed
		state().grabberState = 1;
		
		if (goToBall()) {
			// We're at the ball, so grab it
			s.send(type, RobotCommand.STOP);
			System.out.println("GRABBING");
			s.send(type, RobotCommand.CLOSE_GRABBER);
			ws.setRobotGrabbedBall(robot(), true);
		}
	}

	/** 
	 * Triggers if we do NOT have the ball AND the ball is in our quadrant on the pitch AND
	 * the ball isn't moving too fast
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return !ws.getRobotGrabbedBall(robot()) && 
				ws.onPitch(ws.getBallPoint()) && 
				ws.getBallQuadrant() == ws.getRobotQuadrant(robot()) &&
			   !ws.ballIsMoving(0.1);
	}

}
