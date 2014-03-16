package behavior.friendlyattacker;


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
		
		d("going to ball");
		

		state().grabberState = 1;
//		if (state().grabberState > 0) {
//			s.send(type, RobotCommand.OPEN_GRABBER);
//			state().grabberState--;
//		}
		
		if (goToBall()) {
			// We're at the ball, so grab it
			System.out.println("GRABBING");
			s.send(type, RobotCommand.GRAB);
			ws.setRobotGrabbedBall(robot(), true);
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** 
	 * Triggers if we do NOT have the ball AND the ball is in our quadrant on the pitch
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return !ws.getRobotGrabbedBall(robot()) && ws.onPitch(ws.getBallPoint()) && ws.getBallQuadrant() == ws.getRobotQuadrant(robot());
	}

}
