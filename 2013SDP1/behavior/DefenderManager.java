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
		behaviorList[0] = new SimpleDefendGoal(getWorldState(), getRobotType(), getServer());

		return behaviorList;
	}
}
