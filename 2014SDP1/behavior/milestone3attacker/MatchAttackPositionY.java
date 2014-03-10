package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import vision.WorldState;
import constants.C;
import constants.RobotType;

public class MatchAttackPositionY extends GeneralBehavior {

	public MatchAttackPositionY(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		super.action();
		
		while (isActive()) {
			System.out.println("Doing match Y");
			if (ws == null) {
				System.err.println("worldstate not intialised");
			}
			
			Point ball = new Point(ws.ballX, ws.ballY);
			Point kickP = StrategyHelper.findRobotKickPosition(ball, ws.getOppositionGoalCentre());
			
			try {
				int y = ws.getRobotY(robot());
				
				// Always face up
				rotateTo(C.UP);
				
				System.out.println("Facing up");

				// Move to the right Y
				if (kickP.getY() - y > DISTANCE_ERROR) {
					// Kick point below us
					System.out.println("Kick point below the robot by: " + (kickP.getY() - y));
					s.send(type, RobotCommand.BACK);
					continue;
				} else if (y - kickP.getY() > DISTANCE_ERROR) {
					// Kick point above us
					System.out.println("Kick point above the robot by: " + (y - kickP.getY()));
					s.send(type, RobotCommand.FORWARD);
					continue;
				}

				// We're in the right position, just chill
				s.send(type, RobotCommand.STOP);
			} catch (Exception e) {
				System.err.println("We don't know where the robot or kick point is :((((");
				s.send(type, RobotCommand.STOP);
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
		Point ballP = new Point(ws.ballX, ws.ballY);
		Point kickP = StrategyHelper.findRobotKickPosition(ballP, ws.getOppositionGoalCentre());
		Point robotP = new Point(ws.getRobotX(robot()), ws.getRobotY(robot()));
		
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
