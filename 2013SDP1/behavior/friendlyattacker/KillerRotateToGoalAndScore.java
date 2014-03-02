package behavior.friendlyattacker;

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

		Point robot = ws.getRobotPoint(robot());
		Point goal = ws.getOppositionGoalCentre();
		double orientation = Orientation.getAngle(robot, goal);
		
		if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR)) {
			rotateTo(orientation);
			isRotating = true;
			return;
		}
		
		if (isRotating) {
			s.send(type, RobotCommand.STOP);
			isRotating = false;
		}
		
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		ws.setHaveBall(false);
		s.send(type, RobotCommand.KICK);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/** 
	 * Triggers if we have the ball
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.haveBall();
	}

}
