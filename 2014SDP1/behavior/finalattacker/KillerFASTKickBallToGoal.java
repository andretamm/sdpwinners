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
		
		Point robot = ws.getRobotPoint(robot());
		
		// Get a target to aim for! Try the bottom or top of their goal
		if (targetPoint == null) {
			// Attack points to try
			ArrayList<Point> attackPoints = new ArrayList<Point>();
			
			Point possibleAttackPoint = ws.getOppositionGoalTop();
			possibleAttackPoint.y += 10;
			attackPoints.add(possibleAttackPoint);
			
			possibleAttackPoint = ws.getOppositionGoalBottom();
			possibleAttackPoint.y -= 10;
			attackPoints.add(possibleAttackPoint);
			
			double bestDistance = 1000000;
			
			for (Point p: attackPoints) {
				double shotAngle = Orientation.getAngle(robot, p);
				double oppositionDistance = StrategyHelper.getOpponentDistanceFromPath(robot(), shotAngle, ws);
				
				if (oppositionDistance < bestDistance) {
					bestDistance = oppositionDistance;
					targetPoint = p;
				}
			}
		}
		
		if (targetPoint == null) {
			targetPoint = ws.getOppositionGoalCentre();
		}
		
		// Rotate towards target using Super Ultra Precise Fast Rotation (SUPFR)
		rotateQuickTowards(targetPoint);
		
		// Wait until we're close enough
		double orientation = Orientation.getAngle(robot, targetPoint);
		
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			return;
		}
		
		// Robot stops automagically after doing rotating
		if (state().isRotating) {
			state().isRotating = false;
		}
		
		// Ready for a kick!
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, RobotCommand.FAST_KICK);
		
		// No longer have the ball
		ws.setRobotGrabbedBall(robot(), false);
		Strategy.attackerReadyForKick = false;
		targetPoint = null;
		
		// Wait a wee bit so we don't retrigger grabbing the ball
		try {
			Thread.sleep(100);
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
