package behavior.friendlyattacker;


import java.awt.Point;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerGetPass extends GeneralBehavior {

	public KillerGetPass(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		/*-----------------------------------------------*/
		/* Check if the pass is still happening          */
		/*-----------------------------------------------*/
		
		// Check if the ball isn't going to reach us :P
		if (ws.getBallQuadrant() == ws.getRobotQuadrant(ws.getOpposition(RobotType.ATTACKER))) {
			if (ws.getDirection() == ShootingDirection.LEFT) {
				if (ws.getBallVelocity().getX() > 0) {
					// The ball is moving away from us, pass has failed
					System.out.println("PASS HAS FAILED");
					ws.setDoingPass(false);
					return;
				}
			} else {
				if (ws.getBallVelocity().getX() < 0) {
					// The ball is moving away from us, pass has failed
					System.out.println("PASS HAS FAILED");
					ws.setDoingPass(false);
					return;
				}
			}
		}
		
		// Check if the ball has reached our quadrant
		if (ws.getBallQuadrant() == ws.getRobotQuadrant(robot())) {
			ws.setDoingPass(false);
			return;
		}
		
		/*------------------------------------------------*/
		/* We think the pass is happening, do positioning */
		/*------------------------------------------------*/
		
		// Make robot go to the middle of the quadrant
		Point target = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		
		if (!quickGoTo(target)) {
			return;
		}
		
		// Rotate towards the ball so we can make an amazing catch
		double orientation = Orientation.getAngle(ws.getRobotPoint(robot()), ws.getBallPoint());

		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateTo(orientation);
			return;
		}
		
		// Right spot and orientation, chill there
		s.send(type, RobotCommand.STOP);
	}

	/** 
	 * Triggers if we're trying to do a pass. We are responsible for
	 * cancelling the doingPass flag in this action!!!
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getDoingPass();
	}

}
