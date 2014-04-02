package behavior.finaldefender;

import communication.Server;

import lejos.robotics.subsumption.Behavior;
import sdp.vision.WorldState;
import constants.RobotType;

public class FinalDefenderManager extends behavior.Manager {

	public FinalDefenderManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {
		int numOfBehaviors = 4;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

		// Add behaviors in ascending order of priority
		behaviorList[0] = new DefenderProtectGoal(getWorldState(), getRobotType(), getServer());
		behaviorList[1] = new DefenderDoPass(getWorldState(), getRobotType(), getServer());
		behaviorList[2] = new DefenderGetInPositionForPass(getWorldState(), getRobotType(), getServer());
		behaviorList[3] = new DefenderGotoBallAndGrab(getWorldState(), getRobotType(), getServer());
		
		return behaviorList;
	}
}
