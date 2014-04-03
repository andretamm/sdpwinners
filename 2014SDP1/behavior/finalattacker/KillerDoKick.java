package behavior.finalattacker;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import behavior.finalattacker.kickstrategies.KillerDoFASTKick;
import behavior.finalattacker.kickstrategies.KillerDoStillAimKick;
import behavior.finalattacker.kickstrategies.KillerDoVerticalKick;
import behavior.finalattacker.kickstrategies.KillerDoWallKick;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerDoKick extends GeneralBehavior {

	KillerDoFASTKick fastKick;
	KillerDoStillAimKick stillKick;
	KillerDoVerticalKick verticalKick;
	KillerDoWallKick wallKick;
	
	public KillerDoKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		
		fastKick = new KillerDoFASTKick(ws, type, s);
		stillKick = new KillerDoStillAimKick(ws, type, s);
		verticalKick = new KillerDoVerticalKick(ws, type, s);
		wallKick = new KillerDoWallKick(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		int attackMod = state().attackNumber % 3; 
		

		
		
		if (attackMod == 0 || attackMod == 1) {
			fastKick.action();
		} else {
			stillKick.action();
		}
//		else {
//			// attackMod == 3 and default case
//			verticalKick.action();
//		}
	}

	/** 
	 * Triggers if we have the ball but haven't moved in the
	 * kicking position yet
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getRobotGrabbedBall(robot()) &&
			   Strategy.attackerReadyForKick;
	}
}
