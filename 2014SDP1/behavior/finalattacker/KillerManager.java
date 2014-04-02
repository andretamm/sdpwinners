package behavior.finalattacker;

import java.util.ArrayList;

import communication.Server;
import lejos.robotics.subsumption.Behavior;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerManager extends behavior.Manager {

	public KillerManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {		
		ArrayList<Behavior> behaviorList = new ArrayList<Behavior>();

 		// Add behaviors in ascending order of priority
		behaviorList.add(new KillerBlockDefender(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerGotoBallAndGrab(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerGetInPositionForKick(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerFASTKickBallToGoal(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerCatchPassComplicated(getWorldState(), getRobotType(), getServer()));
		behaviorList.add(new KillerRotateForPass(getWorldState(), getRobotType(), getServer()));
		
		return behaviorList.toArray(new Behavior[0]);
	}
}
