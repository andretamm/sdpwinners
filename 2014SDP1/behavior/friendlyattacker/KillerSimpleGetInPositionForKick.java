package behavior.friendlyattacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import vision.Orientation;
import vision.WorldState;
import constants.C;
import constants.Quadrant;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerSimpleGetInPositionForKick extends GeneralBehavior {

	public KillerSimpleGetInPositionForKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		// Stop this madness if we didn't actually grab the ball <.<
//		if (!StrategyHelper.hasBall(robot(), ws)) {
//			ws.setRobotGrabbedBall(robot(), false);
//			s.send(type, RobotCommand.OPEN_GRABBER);
//			return;
//		}
		
		/*-----------------------------------------------*/
		/* Make robot go to the middle of the quadrant   */
		/*-----------------------------------------------*/	
		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		
		if (!quickGoTo(middlePoint)) {
			// not there yet
			return;
		}
		
		// Don't need to move any more!
		if (isMoving) {
			isMoving = false;
			stop();
		}
		
		// We're there!
		Strategy.attackerReadyForKick = true;
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
