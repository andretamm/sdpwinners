package behavior.finalattacker.kickpositions;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerGetInPositionForVerticalKick extends GeneralBehavior {

	public KillerGetInPositionForVerticalKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		/*---------------------------------------------------------*/
		/* Make robot go to either the top or bottom of their goal */
		/*---------------------------------------------------------*/	
		Point middlePoint = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		Point goalPoint;
		
		if (ws.getDirection() == ShootingDirection.LEFT) {
			// Go to top
			goalPoint = ws.getOppositionGoalTop();
			goalPoint.y -= 20;
		} else {
			// Go to bottom
			goalPoint = ws.getOppositionGoalBottom();
			goalPoint.y += 20;
		}
		
		Point target = new Point(middlePoint.x, goalPoint.y);
		
		if (!quickGoTo(target)) {
			// not there yet
			return;
		}
		
		// We're there
		stopMovement();
		
		/*---------------------------------------------------------*/
		/* Aim the kicker left                                     */
		/*---------------------------------------------------------*/
		s.send(type, RobotCommand.AIM_LEFT);
		
		/*---------------------------------------------------------*/
		/* Rotate to face their goal                               */
		/*---------------------------------------------------------*/
		double shootingDirection = ws.getDirection() == ShootingDirection.LEFT ? C.LEFT : C.RIGHT;
		int degreesToRotate = (int) Math.toDegrees(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), shootingDirection));
		
		System.out.println(String.format("Degrees: %d, current: %f, target: %f", degreesToRotate, ws.getRobotOrientation(robot()), shootingDirection));
		
		rotateBy(degreesToRotate);
		
		// We're there!
		Strategy.attackerReadyForKick = true;
		
		// Wait until we rotate
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Triggers if we have the ball but haven't moved in the
	 * kicking position yet
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getRobotGrabbedBall(robot()) &&
			   !Strategy.attackerReadyForKick;
	}

}
