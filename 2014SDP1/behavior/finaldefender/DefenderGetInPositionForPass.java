package behavior.finaldefender;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.RobotType;
import constants.ShootingDirection;

public class DefenderGetInPositionForPass extends GeneralBehavior {

	public DefenderGetInPositionForPass(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		
		// Stop this madness if we didn't actually grab the ball <.<
		// Use a slightly bigger error margin than usual :)
		if (!StrategyHelper.hasBall(robot(), ws, 38, ANGLE_ERROR * 3)) {
			ws.setRobotGrabbedBall(robot(), false);
			ws.setDoingPass(false);
			
			s.send(type, RobotCommand.OPEN_GRABBER);
			s.forceSend(type, RobotCommand.OPEN_GRABBER);
			return;
		}
		
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
		
		if (!quickGoTo(middlePoint)) {
			// not there yet
			return;
		}
		
		// Don't need to move any more!
		stopMovement();
		
		// We're there!
		Strategy.defenderReadyForPass = true;
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 
	 * Triggers if we have the ball
	 * AND we're not in the middle of the quadrant
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getRobotGrabbedBall(robot()) &&
			   !Strategy.defenderReadyForPass;
	}

}
