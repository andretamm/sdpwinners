package behavior.finalattacker;

import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.finalattacker.kickpositions.KillerGetInPositionForKick;
import behavior.finalattacker.kickpositions.KillerGetInPositionForStillKick;
import behavior.finalattacker.kickpositions.KillerGetInPositionForVerticalKick;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerGetIntoAttackPosition extends GeneralBehavior {

	KillerGetInPositionForKick fastKick;
	KillerGetInPositionForStillKick stillKick;
	KillerGetInPositionForVerticalKick verticalKick;
	
	public KillerGetIntoAttackPosition(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		
		fastKick = new KillerGetInPositionForKick(ws, type, s);
		stillKick = new KillerGetInPositionForStillKick(ws, type, s);
		verticalKick = new KillerGetInPositionForVerticalKick(ws, type, s);
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
			   !Strategy.attackerReadyForKick;
	}
}
