package behavior.finalattacker.kickpositions;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerGetInPositionForWallKick extends GeneralBehavior {

	public KillerGetInPositionForWallKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		/*---------------------------------------------------------*/
		/* Make robot go to either the top or bottom of their goal */
		/*---------------------------------------------------------*/	
		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		Point goalPoint;
		
		if (ws.getDirection() == ShootingDirection.LEFT) {
			// Go to top
			goalPoint = ws.getOppositionGoalTop();
			goalPoint.y -= 30;
		} else {
			// Go to bottom
			goalPoint = ws.getOppositionGoalBottom();
			goalPoint.y += 30;
		}
		
		Point target = new Point(middlePoint.x, goalPoint.y);
		
		if (!quickGoTo(target)) {
			// not there yet
			return;
		}
		
		// We're there
		stopMovement();
		Strategy.attackerReadyForKick = true;
		
		// Wait until we rotate
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
