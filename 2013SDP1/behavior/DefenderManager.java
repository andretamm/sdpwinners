package behavior;

import behavior.friendlydefender.DefenderProtectGoal;
import lejos.robotics.subsumption.Behavior;
import ourcommunication.Server;
import sdp.vision.WorldState;
import constants.RobotType;

public class DefenderManager extends behavior.Manager {

	public DefenderManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {
		int numOfBehaviors = 1;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

		// Add behaviors in ascending order of priority
		behaviorList[0] = new DefenderProtectGoal(getWorldState(), getRobotType(), getServer());

		return behaviorList;
	}
}
