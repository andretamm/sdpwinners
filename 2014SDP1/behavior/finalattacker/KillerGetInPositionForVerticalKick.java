package behavior.finalattacker;

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

public class KillerGetInPositionForVerticalKick extends GeneralBehavior {

	public KillerGetInPositionForVerticalKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		
		// Stop this madness if we didn't actually grab the ball <.<
		// Use a slightly bigger error margin than usual :)
		if (!StrategyHelper.hasBall(robot(), ws, 43, ANGLE_ERROR * 1.8)) {
			ws.setRobotGrabbedBall(robot(), false);

			s.send(type, RobotCommand.OPEN_GRABBER);
			s.forceSend(type, RobotCommand.OPEN_GRABBER);
			return;
		}
		
		/*---------------------------------------------------------*/
		/* Make robot go to either the top or bottom of their goal */
		/*---------------------------------------------------------*/	
		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		Point goalPoint;
		
		if (ws.getDirection() == ShootingDirection.LEFT) {
			// Go to top
			goalPoint = ws.getOppositionGoalTop();
		} else {
			// Go to bottom
			goalPoint = ws.getOppositionGoalBottom();
		}
		
		Point target = new Point(middlePoint.x, goalPoint.y);
		
		if (!quickGoTo(target)) {
			// not there yet
			return;
		}
		
		// We're there
		stopMovement();
		
		/*---------------------------------------------------------*/
		/* Rotate to face their goal                               */
		/*---------------------------------------------------------*/
		double shootingDirection = ws.getDirection() == ShootingDirection.LEFT ? C.LEFT : C.RIGHT;
		
		rotateBy((int) Math.toDegrees(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), shootingDirection)));
		
		/*---------------------------------------------------------*/
		/* Aim the kicker right                                    */
		/*---------------------------------------------------------*/
		s.send(type, RobotCommand.AIM_RIGHT);
		
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
