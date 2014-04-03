package behavior.finalattacker.kickstrategies;

import java.awt.Point;
import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerDoStillAimKick extends GeneralBehavior {
	
	public static Point targetPoint;

	public KillerDoStillAimKick(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
		targetPoint = null;
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
				
		/*-----------------------------------------------*/
		/* Make a killer (heh heh) kick to the right     */
		/*-----------------------------------------------*/
		s.send(type, RobotCommand.KICK_RIGHT);
		
		/*-----------------------------------------------*/
		/* Kicker resets automagically, wait a bit       */
		/*-----------------------------------------------*/
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

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
