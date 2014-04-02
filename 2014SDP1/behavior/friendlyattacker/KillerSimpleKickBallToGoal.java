package behavior.friendlyattacker;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerSimpleKickBallToGoal extends GeneralBehavior {
	
	public static Point targetPoint;

	public KillerSimpleKickBallToGoal(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		targetPoint = null;
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
		
		// Get a target to aim for!
//		if (targetPoint == null) {
//			// Attack points to try
//			Point[] attackPoints = new Point[3];
//			attackPoints[0] = ws.getOppositionGoalTop();
//			attackPoints[0].y += 10;
//			attackPoints[1] = ws.getOppositionGoalCentre();
//			attackPoints[2] = ws.getOppositionGoalBottom();
//			attackPoints[2].y -= 10;
//			
//			
//			double bestDistance = 1000000;
//			
//			for (Point p: attackPoints) {
//				double shotAngle = Orientation.getAngle(robot, p);
//				double oppositionDistance = StrategyHelper.getOpponentDistanceFromPath(robot(), shotAngle, ws);
//				
//				if (oppositionDistance < bestDistance) {
//					bestDistance = oppositionDistance;
//					targetPoint = p;
//				}
//			}
//		}
		
		if (targetPoint == null) {
			targetPoint = ws.getOppositionGoalCentre();
		}
		
		// Turn kicker left or right
//		if (! state().isAimingLeft && ! state().isAimingRight) {
//			if (Math.random() > 0.5) {
//				// Aim right
//				s.send(type, RobotCommand.AIM_RIGHT);
//				state().isAimingRight = true;
//			} else {
//				// Aim left
//				s.send(type, RobotCommand.AIM_LEFT);
//				state().isAimingLeft = true;
//			}
//		}
		
		// Rotate towards target
		double orientation = Orientation.getAngle(robot, targetPoint);
		
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR * 2) {
			rotateTo(orientation);
			state().isRotating = true;
			return;
		}

		if (state().isRotating) {
			s.send(type, RobotCommand.STOP);
			state().isRotating = false;
		}
		
		// Ready for a kick!
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, RobotCommand.FAST_KICK);
		
		// No longer have the ball
		ws.setRobotGrabbedBall(robot(), false);
		Strategy.attackerReadyForKick = false;
		targetPoint = null;
		
//		// Reset the kicker
//		s.send(type, RobotCommand.AIM_RESET);
//		state().isAimingLeft = false;
//		state().isAimingRight = false;
//		
		// Wait a wee bit so we don't retrigger grabbing the ball
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Triggers if we have the ball and are in position for a kick
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getRobotGrabbedBall(robot()) &&
			   Strategy.attackerReadyForKick;
	}

}
