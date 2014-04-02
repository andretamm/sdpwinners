package behavior.finalattacker;

import java.util.ArrayList;

import communication.Server;
import lejos.robotics.subsumption.Behavior;
import sdp.vision.WorldState;
import constants.RobotType;

public class FinalKillerVerticalManager extends behavior.Manager {

	public FinalKillerVerticalManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {		
		ArrayList<Behavior> behaviorList = new ArrayList<Behavior>();

 		// Add behaviors in ascending order of priority
		behaviorList.add(new KillerBlockDefender(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerGotoBallAndGrab(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerGetInPositionForVerticalKick(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerDoVerticalKick(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerGetPass(getWorldState(), getRobotType(), getServer()));
		
		return behaviorList.toArray(new Behavior[0]);
	}
}
