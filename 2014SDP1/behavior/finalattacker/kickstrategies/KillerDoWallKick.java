package behavior.finalattacker.kickstrategies;

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

public class KillerDoWallKick extends GeneralBehavior {
	
	public static Point targetPoint;

	public KillerDoWallKick(WorldState ws, RobotType type, Server s) {
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
		
		Point goalTarget = ws.getDirection() == ShootingDirection.LEFT ? ws.getOppositionGoalTop() : ws.getOppositionGoalBottom();
		int wallY = ws.getDirection() == ShootingDirection.LEFT ? ws.getPitchTopLeft().y : ws.getPitchBottomRight().y; 
		
		Point target = new Point((int) (goalTarget.x + robot.x)/2, wallY);

		// Rotate towards target using Super Ultra Precise Fast Rotation (SUPFR)
		rotateQuickTowards(target, false);
			
		// Wait until we're close enough
		double orientation = Orientation.getAngle(robot, target);
		
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateQuickTowards(target);
			return;
		}
		
		// Robot stops automagically after doing rotating
		if (state().isRotating) {
			state().isRotating = false;
		}
		
		
		/*-----------------------------------------------*/
		/* Ready for a kick!!                            */
		/*-----------------------------------------------*/
		

		// Do the kick together with the 'misleading' rotation :)
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, RobotCommand.FAST_KICK);
		
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
		
		state().attackNumber++;
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
