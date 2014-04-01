package behavior.finalattacker;

import java.awt.Point;
import java.util.ArrayList;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerFASTKickBallToGoal extends GeneralBehavior {
	
	public static Point targetPoint;

	public KillerFASTKickBallToGoal(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		targetPoint = null;
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		/*-----------------------------------------------*/
		/* Figure out where to shoot                     */
		/*-----------------------------------------------*/
		
		Point robot = ws.getRobotPoint(robot());
		
		/* USEFUL FACTS:
		 * 1) Height of a goal is ~140 pixels
		 * 2) Distance from a goal top to the middle of the goal is ~70 pixels
		 * 3) Width of our robot is ~50 pixels
		 * 4) Distance from our robot's centre to an edge is ~25 pixels
		 * 
		 * Using a simplistic model, this would mean that if their robot is
		 * at least 70 - 25 = 45 pixels away from a goal top/bottom then
		 * their robot is AT MOST in the middle of their goal. If it's any
		 * closer than that then making a shot will proooobs fail.
		 */
		
		// Get a target to aim for!
		if (targetPoint == null) {
			findTarget();
			
			if (targetPoint == null) {
				targetPoint = ws.getOppositionGoalCentre();
			}
			
			// Rotate towards target using Super Ultra Precise Fast Rotation (SUPFR)
			rotateQuickTowards(targetPoint, true);
			
			// Increment number of targets tried by one
			state().attackerNumberOfTargetsTried++;
		}
		
		// Wait until we're close enough
		double orientation = Orientation.getAngle(robot, targetPoint);
		
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateQuickTowards(targetPoint);
			return;
		}
		
		// Robot stops automagically after doing rotating
		if (state().isRotating) {
			state().isRotating = false;
		}
		
		/*-----------------------------------------------*/
		/* Quick check if the kick is feasible           */
		/*-----------------------------------------------*/
		// We're close!
		// See how close the opponent is
		double shotAngle = ws.getRobotOrientation(robot());
		double oppositionDistance = StrategyHelper.getOpponentDistanceFromPath(robot(), shotAngle, ws);
		System.out.println("Opp distance: " + oppositionDistance);
		if (oppositionDistance < 60) {
			if (state().attackerNumberOfTargetsTried < 4) {
				// They're too close, try again
				targetPoint = null;
				return;
			} else {
				// Already tried too many times, just shoot
			}
		}
		
		/*-----------------------------------------------*/
		/* Ready for a kick!!                            */
		/*-----------------------------------------------*/
		
		/* Also make a 'misleading' rotation ;)          */
	
		// Main idea is to rotate away from where we actually shot -
		// if the opposition is tracking our orientation, then we might
		// fool them to follow our orientation instead of blocking the
		// ball that's already heading toward their goal
		boolean fakeRotationRight = true;
		
		if (targetPoint.y > ws.getOppositionGoalCentre().y) {
			// Point is down, should rotate up
			if (ws.getDirection() == ShootingDirection.LEFT) {
				// Rotate right
			} else {
				// Rotate left
				fakeRotationRight = false; 
			}
		} else {
			// Point is up, should rotate down
			if (ws.getDirection() == ShootingDirection.LEFT) {
				// Rotate left
				fakeRotationRight = false; 
			} else {
				// Rotate right
			}
		}
		
		// Pick right command type
		int kickAndRotateCommand = fakeRotationRight ? RobotCommand.KICK_THEN_ROTATE_RIGHT : RobotCommand.KICK_THEN_ROTATE_LEFT; 
		
		// Do the kick together with the 'misleading' rotation :)
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, kickAndRotateCommand);
		
		// No longer have the ball
		ws.setRobotGrabbedBall(robot(), false);
		Strategy.attackerReadyForKick = false;
		targetPoint = null;
		
		/*-----------------------------------------------*/
		/* Kick done!                                    */
		/*-----------------------------------------------*/
		
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
	
	
	/**
	 * Sets a target that's furthest from our opponent.
	 * Tries the bottom or top of their goal
	 */
	public void findTarget() {
		Point robot = ws.getRobotPoint(robot());
		
		// Attack points to try
		ArrayList<Point> attackPoints = new ArrayList<Point>();
		
		Point possibleAttackPoint = ws.getOppositionGoalTop();
		possibleAttackPoint.y += 30;
		attackPoints.add(possibleAttackPoint);
		
		possibleAttackPoint = ws.getOppositionGoalBottom();
		possibleAttackPoint.y -= 30;
		attackPoints.add(possibleAttackPoint);
		
		double bestDistance = 0;
		
		for (Point p: attackPoints) {
			double shotAngle = Orientation.getAngle(robot, p);
			double oppositionDistance = StrategyHelper.getOpponentDistanceFromPath(robot(), shotAngle, ws);
			
			System.out.println("Point " + p.toString() + " distance: " + oppositionDistance);
			if (oppositionDistance > bestDistance) {
				bestDistance = oppositionDistance;
				targetPoint = p;
			}
		}
	}
}
