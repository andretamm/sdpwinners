package behavior.friendlyattacker.notused;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
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
		
		// Stop this madness if we didn't actually grab the ball <.<
//		if (!StrategyHelper.hasBall(robot(), ws)) {
//			ws.setRobotGrabbedBall(robot(), false);
//			s.send(type, RobotCommand.OPEN_GRABBER);
//			return;
//		}

		Point robot = ws.getRobotPoint(robot());
		Point goal = ws.getOppositionGoalCentre();
		double orientation = Orientation.getAngle(robot, goal);
		
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateTo(orientation);
			isRotating = true;
			return;
		}
		
		if (isRotating) {
			s.send(type, RobotCommand.STOP);
			isRotating = false;
		}
		
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// No longer have the ball
		ws.setRobotGrabbedBall(robot(), false);
		s.send(type, RobotCommand.KICK);
		
		// Wait a wee bit so we don't retrigger grabbing the ball
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
