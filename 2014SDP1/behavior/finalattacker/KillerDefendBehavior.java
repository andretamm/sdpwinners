package behavior.finalattacker;


import java.awt.Point;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import sdp.vision.WorldState;
import constants.Quadrant;
import constants.QuadrantX;
import constants.RobotType;

public class KillerDefendBehavior extends GeneralBehavior {

	public KillerDefendBehavior(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		// If the ball has just left our quadrant, try opening the grabbers just in case
		if (state().grabberState != 0) {
			s.send(type, RobotCommand.OPEN_GRABBER);
			s.forceSend(type, RobotCommand.OPEN_GRABBER);
			state().grabberState = 0;
		}
		
		// Make robot go to the middle of the quadrant
		Quadrant q = ws.getRobotQuadrant(robot());
		int quadrantMiddleX = (ws.getQuadrantX(q, QuadrantX.LOW) + ws.getQuadrantX(q, QuadrantX.HIGH)) / 2 ;
		Point target = new Point(quadrantMiddleX, ws.getBallY());
		
		if (!ws.onPitch(target)) {
			// Ball not on pitch, go to centre of our quadrant
			target = new Point(quadrantMiddleX, ws.getOurGoalCentre().y);
		}
		
		if (quickGoTo(target)) {
			stop();
		}
		
		// TODO use time to rotate to 270'
	}

	/** 
	 * Triggers if we the ball is not in our Quadrant
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return (ws.getBallQuadrant() != ws.getRobotQuadrant(robot()));
	}

}
