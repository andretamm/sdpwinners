package behavior.friendlyattacker;

import lejos.robotics.subsumption.Behavior;
import ourcommunication.Server;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerManager extends behavior.Manager {

	public KillerManager(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}
	
	@Override
	public Behavior[] getAllBehaviors() {
		int numOfBehaviors = 4;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

//		// Add behaviors in ascending order of priority
		behaviorList[3] = new KillerGetPass(getWorldState(), getRobotType(), getServer());
		behaviorList[2] = new KillerRotateToGoalAndScore(getWorldState(), getRobotType(), getServer());
//		behaviorList[2] = new KillerMoveLeftKickToBottomGoalAndScore(getWorldState(), getRobotType(), getServer());
		behaviorList[1] = new KillerGotoBallAndGrab(getWorldState(), getRobotType(), getServer());
		behaviorList[0] = new KillerDefendBehavior(getWorldState(), getRobotType(), getServer());
		
//		behaviorList[1] = new SimpleFalse(getWs(), getR(), getS());
//		behaviorList[0] = new SimpleTrue(getWs(), getR(), getS());
		return behaviorList;
	}
}
