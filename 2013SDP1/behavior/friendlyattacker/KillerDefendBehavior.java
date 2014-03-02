package behavior.friendlyattacker;


import java.awt.Point;

import behavior.GeneralBehavior;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
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
		
		// Make robot go to the middle of the quadrant
		Quadrant q = ws.getRobotQuadrant(robot());
		int quadrantMiddleX = (ws.getQuadrantX(q, QuadrantX.LOW) + ws.getQuadrantX(q, QuadrantX.HIGH)) / 2 ;
		Point target = new Point(quadrantMiddleX, ws.getBallY());
		
		// Doesn't care about what way we're facing
		quickGoTo(target);
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
