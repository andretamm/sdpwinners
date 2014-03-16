package behavior.friendlydefender;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.C;
import constants.Quadrant;
import constants.RobotType;
import constants.ShootingDirection;

public class DefenderSimpleGetInPositionForPass extends GeneralBehavior {

	public DefenderSimpleGetInPositionForPass(WorldState ws, RobotType type, Server s) {
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
		
		// Global flag to let the attacker know we're doing a pass.
		// NB - the attacker is responsible for setting this to false when he realises
		// the pass isn't happening any more!
		ws.setDoingPass(true);
		ws.setAttackerPassPosition(ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(ws.getOur(RobotType.ATTACKER))));
		
		/*-----------------------------------------------*/
		/* Make robot go to the middle of the quadrant   */
		/*-----------------------------------------------*/	
		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		if (ws.getDirection() == ShootingDirection.LEFT) {
			middlePoint.x += 20;
		} else {
			middlePoint.x -= 20;
		}
		
//		if (!quickGoTo(middlePoint)) {
//			// not there yet
//			return;
//		}
		
		if (!quickGoTo(middlePoint)) {
			// not there yet
			return;
		}
		
		// Don't need to move any more!
		if (state().isMoving) {
			state().isMoving = false;
			stop();
		}
		
		// We're there!
		Strategy.defenderReadyForPass = true;
		
	}

	/** 
	 * Triggers if we have the ball
	 * AND we're not in the middle of the quadrant
	 * AND not facing the right direction
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
//		double orientation = ws.getRobotOrientation(type, ws.getColour());
//		
//		double targetAngle;
//		
//		if (ws.getDirection() == ShootingDirection.LEFT) {
//			targetAngle = C.A180;
//		} else {
//			targetAngle = 0;
//		}
//		
//		// Find the quickest angle to rotate towards our target
//		double turnAngle = StrategyHelper.angleDiff(orientation, targetAngle);
//		
//		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
//		
		return ws.getRobotGrabbedBall(robot()) &&
			   !Strategy.defenderReadyForPass;
		
//		return ws.getRobotGrabbedBall(robot()) &&
//			   Math.abs(turnAngle) > ANGLE_ERROR &&
//			   StrategyHelper.getDistance(ws.getRobotPoint(robot()), middlePoint) > DISTANCE_ERROR - 10;
	}

}
