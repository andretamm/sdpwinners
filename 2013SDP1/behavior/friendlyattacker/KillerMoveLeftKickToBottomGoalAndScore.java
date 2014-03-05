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
		
		// Stop this madness if we didn't actually grab the ball <.<
//		if (!StrategyHelper.hasBall(robot(), ws)) {
//			ws.setRobotGrabbedBall(robot(), false);
//			s.send(type, RobotCommand.OPEN_GRABBER);
//			return;
//		}

		int optimalYValue = 344;
		Point robot = ws.getRobotPoint(robot());
		Point goal = new Point(ws.getRobotY(robot()),ws.getOppositionGoalCentre().x);
		double orientation = Orientation.getAngle(robot, goal);
		
		/*
		 * Rotate until horizontal with the opposition's goal
		 */
		if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR)) {
			rotateTo(orientation);
			isRotating = true;
			return;
		}
		
		if (isRotating) {
			s.send(type, RobotCommand.STOP);
			isRotating = false;
		}
		
		/*
		 * Aim at the right. i.e. at the bottom of the opposition's goal
		 */
		if(!isAimingRight){
			aimRight();
			s.send(type, RobotCommand.AIM_RIGHT);
		}
		
		/*
		 * Continue to move left until it reaches the optimal y coordinate
		 */
		if (!(ws.getRobotPoint(robot()).y == optimalYValue)){
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
		s.send(type, RobotCommand.AIM_RESET);
		
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
