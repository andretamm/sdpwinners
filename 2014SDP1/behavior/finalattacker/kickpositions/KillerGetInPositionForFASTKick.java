package behavior.finalattacker.kickpositions;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerGetInPositionForFASTKick extends GeneralBehavior {

	public KillerGetInPositionForFASTKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		

		/*-----------------------------------------------*/
		/* Make robot go to the middle of the quadrant   */
		/*-----------------------------------------------*/	
		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		
		if (!quickGoTo(middlePoint)) {
			// not there yet
			return;
		}
		
		// Don't need to move any more!
		stop();
		
		// We're there!
		Strategy.attackerReadyForKick = true;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	/** 
	 * Triggers if we have the ball but haven't moved in the
	 * kicking position yet
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getRobotGrabbedBall(robot()) &&
			   !Strategy.attackerReadyForKick;
	}

}
