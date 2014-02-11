package behavior;

import behavior.milestone3attacker.MatchAttackPositionX;
import behavior.milestone3attacker.MatchAttackPositionY;
import lejos.robotics.subsumption.Behavior;
import ourcommunication.Server;
import sdp.vision.WorldState;
import common.Robot;

public class Milestone3AttackerManager extends behavior.Manager {

	public Milestone3AttackerManager(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}
	
	@Override
	Behavior[] getAllBehaviors() {
		int numOfBehaviors = 2;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];

		// Add behaviors in ascending order of priority
		behaviorList[0] = new MatchAttackPositionY(getWs(), getR(), getS());
		behaviorList[1] = new MatchAttackPositionX(getWs(), getR(), getS());

		return behaviorList;
	}
}
