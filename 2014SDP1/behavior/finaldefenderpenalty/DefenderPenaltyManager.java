package behavior.finaldefenderpenalty;

import java.util.ArrayList;

import communication.Server;
import lejos.robotics.subsumption.Behavior;
import sdp.vision.WorldState;
import constants.RobotType;

public class DefenderPenaltyManager extends behavior.Manager {

	public DefenderPenaltyManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {		
		ArrayList<Behavior> behaviorList = new ArrayList<Behavior>();

 		// Add behaviors in ascending order of priority
		behaviorList.add(new DefenderPenaltyProtectGoal(getWorldState(), getRobotType(), getServer()));
		
		return behaviorList.toArray(new Behavior[0]);
	}
}
