package behavior.friendlyattacker;


import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerRotateForPass extends GeneralBehavior {

	public KillerRotateForPass(WorldState ws, RobotType type, Server s) {
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
					ws.setKickedPass(false);
					return;
				}
			} else {
				if (ws.getBallVelocity().getX() < 0) {
					// The ball is moving away from us, pass has failed
					System.out.println("PASS HAS FAILED");
					ws.setDoingPass(false);
					ws.setKickedPass(false);
					return;
				}
			}
		}
		
		// Check if the ball has reached our quadrant
		if (ws.getBallQuadrant() == ws.getRobotQuadrant(robot())) {
			ws.setDoingPass(false);
			ws.setKickedPass(false);
			return;
		}
		
		/*--------------------------------------------------*/
		/* We think the pass is happening, rotate right way */
		/*--------------------------------------------------*/
		if (ws.getDirection() == ShootingDirection.LEFT) {
			// Ball coming from the right
			if (!rotateTo(C.RIGHT)) {
				// Still rotating
				return;
			}
			
			stopRotating();
		} else {
			// Ball coming from the left
			if (!rotateTo(C.LEFT)) {
				// Still rotating
				return;
			}
			
			stopRotating();
		}
		
		// Right orientation, chill there
		s.send(type, RobotCommand.STOP);
		
		state().attackerOrientationSetForPass = true;
	}

	/** 
	 * Triggers if we're trying to do a pass. We are responsible for
	 * cancelling the doingPass flag in this action!!!
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getDoingPass() && !state().attackerOrientationSetForPass;
	}

}
