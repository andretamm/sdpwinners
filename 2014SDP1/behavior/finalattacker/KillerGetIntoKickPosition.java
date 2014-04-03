package behavior.finalattacker;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import behavior.finalattacker.kickpositions.KillerGetInPositionForFASTKick;
import behavior.finalattacker.kickpositions.KillerGetInPositionForStillAimKick;
import behavior.finalattacker.kickpositions.KillerGetInPositionForVerticalKick;
import behavior.finalattacker.kickpositions.KillerGetInPositionForWallKick;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerGetIntoKickPosition extends GeneralBehavior {

	KillerGetInPositionForFASTKick fastKick;
	KillerGetInPositionForStillAimKick stillKick;
	KillerGetInPositionForVerticalKick verticalKick;
	KillerGetInPositionForWallKick wallKick;
	
	public KillerGetIntoKickPosition(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		
		fastKick = new KillerGetInPositionForFASTKick(ws, type, s);
		stillKick = new KillerGetInPositionForStillAimKick(ws, type, s);
		verticalKick = new KillerGetInPositionForVerticalKick(ws, type, s);
		wallKick = new KillerGetInPositionForWallKick(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		// Stop this madness if we didn't actually grab the ball <.<
		// Use a slightly bigger error margin than usual :)
		if (!StrategyHelper.hasBall(robot(), ws, 60, ANGLE_ERROR * 3) && Strategy.ballVisible) {
			ws.setRobotGrabbedBall(robot(), false);

			s.send(type, RobotCommand.OPEN_GRABBER);
			s.forceSend(type, RobotCommand.OPEN_GRABBER);
			return;
		}
		
		int attackMod = state().attackNumber % 4; 
		
		if (attackMod == 0) {
			wallKick.action();
			return;
		}
		
		if (attackMod == 0 || attackMod == 1) {
			fastKick.action();
		} else if (attackMod == 2) {
			stillKick.action();
		} else {
			// attackMod == 3 and default case
			verticalKick.action();
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
