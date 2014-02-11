package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import common.Robot;
import constants.C;

public class MatchAttackPositionY extends GeneralBehavior {

	public MatchAttackPositionY(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}

	@Override
	public void action() {
		super.action();
		
		while (isActive()) {
			if (ws == null) {
				System.err.println("worldstate not intialised");
			}
			
			Point kickP = StrategyHelper.findRobotKickPosition(ws.getBallPoint(), ws.getOppositionGoalCentre());
			
			try {
				int y = ws.getRobotY(r);
				
				// Always face up
				rotateTo(C.UP);

				// Move to the right Y
				if (kickP.getY() - y > DISTANCE_ERROR) {
					// Kick point below us
					System.out.println("Kick point below the robot by: " + (kickP.getY() - y));
					s.send(0, RobotCommand.BACK);
					continue;
				} else if (y - kickP.getY() > DISTANCE_ERROR) {
					// Kick point above us
					System.out.println("Kick point above the robot by: " + (y - kickP.getY()));
					s.send(0, RobotCommand.FORWARD);
					continue;
				}

				// We're in the right position, just chill
				s.send(0, RobotCommand.STOP);
			} catch (Exception e) {
				System.err.println("We don't know where the robot or kick point is :((((");
				s.send(0, RobotCommand.STOP);
				e.printStackTrace();
			}
		}
	}

	/** 
	 * Triggers if we are 'behind' the ball X-wise and can navigate to the Y 
	 * coordinate for making a kick
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		Point ballP = ws.getBallPoint();
		Point kickP = StrategyHelper.findRobotKickPosition(ballP, ws.getOppositionGoalCentre());
		Point robotP = new Point(ws.getRobotX(r), ws.getRobotY(r));
		
		if (StrategyHelper.inRange(robotP.getX(), kickP.getX(), DISTANCE_ERROR)) {
			// We are 'behind' the ball in the right X position, so
			// we can move to the right Y coordinate
			return true;
		} else {
			// We are not behind the ball, wait until we get there
			return false;
		}
	}

}
