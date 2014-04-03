package behavior.finalattacker;

import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.finalattacker.kickstrategies.KillerDoStillKick;
import behavior.finalattacker.kickstrategies.KillerDoVerticalKick;
import behavior.finalattacker.kickstrategies.KillerDoFASTKick;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerDoKick extends GeneralBehavior {

	KillerDoFASTKick fastKick;
	KillerDoStillKick stillKick;
	KillerDoVerticalKick verticalKick;
	
	public KillerDoKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		
		fastKick = new KillerDoFASTKick(ws, type, s);
		stillKick = new KillerDoStillKick(ws, type, s);
		verticalKick = new KillerDoVerticalKick(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		int attackMod = state().attackNumber % 4; 
		
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
			   Strategy.attackerReadyForKick;
	}
}
