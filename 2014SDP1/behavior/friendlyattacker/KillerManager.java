package behavior.friendlyattacker;

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
		int numOfBehaviors = 4;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

//		// Add behaviors in ascending order of priority
//		behaviorList[4] = new KillerGetPass(getWorldState(), getRobotType(), getServer());
//		behaviorList[2] = new KillerRotateToGoalAndScore(getWorldState(), getRobotType(), getServer());
		behaviorList[2] = new KillerSimpleGetInPositionForKick(getWorldState(), getRobotType(), getServer());
		behaviorList[3] = new KillerSimpleKickBallToGoal(getWorldState(), getRobotType(), getServer());
		behaviorList[1] = new KillerGotoBallAndGrab(getWorldState(), getRobotType(), getServer());
		behaviorList[0] = new KillerDefendBehavior(getWorldState(), getRobotType(), getServer());
//		behaviorList[4] = new KillerGetPass(getWorldState(), getRobotType(), getServer());
		
//		behaviorList[2] = new KillerGetInPositionForKick(getWorldState(), getRobotType(), getServer());
//		behaviorList[3] = new KillerKickBallToGoal(getWorldState(), getRobotType(), getServer());
		
//		behaviorList[1] = new SimpleFalse(getWs(), getR(), getS());
//		behaviorList[0] = new SimpleTrue(getWs(), getR(), getS());
		return behaviorList;
	}
}
