package behavior.friendlyattacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerMoveLeftKickToBottomGoalAndScore extends GeneralBehavior {

	public KillerMoveLeftKickToBottomGoalAndScore(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
//		System.out.println("rotating to goal and scoring");
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		System.out.println(ws.getRobotPoint(robot()));
		// Stop this madness if we didn't actually grab the ball <.<
//		if (!StrategyHelper.hasBall(robot(), ws)) {
//			ws.setRobotGrabbedBall(robot(), false);
//			s.send(type, RobotCommand.OPEN_GRABBER);
//			return;
//		}

		int optimalYValue = 130;
		Point robot = ws.getRobotPoint(robot());
		Point goal = new Point(ws.getOppositionGoalCentre().x, ws.getRobotY(robot()));
		double orientation = Orientation.getAngle(robot, goal);
		
		/*
		 * Rotate until horizontal with the opposition's goal
		 */
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateTo(orientation);
			isRotating = true;
			return;
		}
		
		stopRotating();
		
		/*
		 * Aim at the right. i.e. at the bottom of the opposition's goal
		 */
		if(!isAimingRight){
			aimRight();
		}
		
		/*
		 * Continue to move left until it reaches the optimal y coordinate
		 */
		if (!StrategyHelper.inRange(ws.getRobotPoint(robot()).y, optimalYValue, DISTANCE_ERROR)) {
			moveLeft();
			return;
		}
		
		if(isMoving){
			s.send(type, RobotCommand.STOP);
			isMoving = false;
		}
		
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// No longer have the ball
		ws.setRobotGrabbedBall(robot(), false);
		s.send(type, RobotCommand.KICK);
		
		/*
		 * Reset the aiming of the robot to the centre (i.e. NORTH)
		 */
		aimReset();
		
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
