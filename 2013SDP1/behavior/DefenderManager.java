package behavior;

import lejos.robotics.subsumption.Behavior;
import ourcommunication.Server;
import sdp.vision.WorldState;
import constants.RobotType;

public class DefenderManager extends behavior.Manager {

	public DefenderManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	Behavior[] getAllBehaviors() {
		int numOfBehaviors = 1;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

		// Add behaviors in ascending order of priority
		behaviorList[0] = new SimpleReactiveDefendGoal(getWorldState(), getRobotType(), getServer());

		return behaviorList;
	}
}
