package behavior;

import behavior.milestone3attacker.KillerCatchBall;
import behavior.milestone3attacker.KillerRotateToBall;
import behavior.milestone3attacker.KillerRotateToGoalAndScore;
import behavior.milestone3attacker.MatchAttackPositionX;
import behavior.milestone3attacker.MatchAttackPositionY;
import lejos.robotics.subsumption.Behavior;
import ourcommunication.Server;
import sdp.vision.WorldState;
import common.Robot;

public class KillerManager extends behavior.Manager {

	public KillerManager(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}
	
	@Override
	Behavior[] getAllBehaviors() {
		int numOfBehaviors = 3;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

//		// Add behaviors in ascending order of priority
		behaviorList[0] = new KillerRotateToGoalAndScore(getWs(), getR(), getS());
		behaviorList[1] = new KillerCatchBall(getWs(), getR(), getS());
		behaviorList[2] = new KillerRotateToBall(getWs(), getR(), getS());
		

//		behaviorList[1] = new SimpleFalse(getWs(), getR(), getS());
//		behaviorList[0] = new SimpleTrue(getWs(), getR(), getS());
		return behaviorList;
	}
}
