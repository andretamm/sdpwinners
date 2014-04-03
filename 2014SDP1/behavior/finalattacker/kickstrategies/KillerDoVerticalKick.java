package behavior.finalattacker.kickstrategies;

import java.awt.Point;
import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import sdp.vision.WorldState;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerDoVerticalKick extends GeneralBehavior {
	
	public static Point targetPoint;

	public KillerDoVerticalKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		targetPoint = null;
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
				
		/*-----------------------------------------------*/
		/* Figure out which way to move                  */
		/*-----------------------------------------------*/
		Point targetPoint = ws.getDirection() == ShootingDirection.LEFT ? ws.getOppositionGoalBottom() : ws.getOppositionGoalTop();
		Point quadrantMiddle = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
//		targetPoint.x = quadrantMiddle.x;
		targetPoint.x = ws.getRobotPoint(robot()).x;
		
		Point robot = ws.getRobotPoint(robot());
		
		/*-----------------------------------------------*/
		/* WE HAVE MOTIOOOON                             */
		/*-----------------------------------------------*/
		goDiagonallyTo(targetPoint);

		
		/*-----------------------------------------------*/
		/* Wait until we're at the middle of the quadrant*/
		/*-----------------------------------------------*/
		if (robot.y < quadrantMiddle.y) {
			return;
		}

		/*-----------------------------------------------*/
		/* Make a killer (heh heh) kick                  */
		/*-----------------------------------------------*/
		s.send(type, RobotCommand.FAST_KICK);
		
		/*-----------------------------------------------*/
		/* Still keep on moving to confuse the opponent  */
		/*-----------------------------------------------*/
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		s.send(type, RobotCommand.AIM_RESET);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		/*-----------------------------------------------*/
		/* STAHP!!! - we're done here :)                 */
		/*-----------------------------------------------*/
		s.send(type, RobotCommand.STOP);
		
		ws.setRobotGrabbedBall(robot(), false);
		Strategy.attackerReadyForKick = false;
		
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
}
