package behavior.friendlydefender;

import behavior.Manager;
import lejos.robotics.subsumption.Behavior;
import ourcommunication.Server;
import vision.WorldState;
import constants.RobotType;

public class DefenderManager extends behavior.Manager {

	public DefenderManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {
		int numOfBehaviors = 4;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

		// Add behaviors in ascending order of priority
//		behaviorList[0] = new DefenderProtectGoal(getWorldState(), getRobotType(), getServer());
//		behaviorList[1] = new DefenderMakePass(getWorldState(), getRobotType(), getServer());
//		behaviorList[2] = new DefenderGotoBallAndGrab(getWorldState(), getRobotType(), getServer());
//
		
//		behaviorList[1] = new DefenderTryPassing(getWorldState(), getRobotType(), getServer());
//		behaviorList[2] = new DefenderGetInPositionForPass(getWorldState(), getRobotType(), getServer());
		
		behaviorList[0] = new DefenderProtectGoal(getWorldState(), getRobotType(), getServer());
		behaviorList[1] = new DefenderSimpleTryPassing(getWorldState(), getRobotType(), getServer());
		behaviorList[2] = new DefenderSimpleGetInPositionForPass(getWorldState(), getRobotType(), getServer());
		behaviorList[3] = new DefenderGotoBallAndGrab(getWorldState(), getRobotType(), getServer());
		
		return behaviorList;
	}
}
