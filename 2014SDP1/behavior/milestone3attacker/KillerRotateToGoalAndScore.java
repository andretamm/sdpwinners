package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import vision.Orientation;
import vision.WorldState;
import constants.RobotType;

public class KillerRotateToGoalAndScore extends GeneralBehavior {

	public KillerRotateToGoalAndScore(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
//		System.out.println("rotating to goal and scoring");
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Point robot = ws.getRobotPoint(robot());
		Point goal = ws.getOppositionGoalCentre();
		double orientation = Orientation.getAngle(robot, goal);
		
		if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR)) {
			rotateTo(orientation);
			return;
		}
		
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, RobotCommand.KICK);		
	}

	/** 
	 * Triggers if we have the ball
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getRobotGrabbedBall(robot());
	}

}
