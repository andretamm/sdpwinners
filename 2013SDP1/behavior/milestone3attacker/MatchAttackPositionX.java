package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import common.Robot;
import constants.C;

public class MatchAttackPositionX extends GeneralBehavior {

	public MatchAttackPositionX(WorldState ws, Robot r, Server s) {
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
				int x = ws.getRobotX(r);
				
				// Make sure we are at a good distance from the ball
				if (StrategyHelper.inRange(y, ws.getBallY(), StrategyHelper.ROBOT_SAFETY_DISTANCE)) {
					// Too close to the ball to just go to x coordinate straight away
					// move away in y direction first
					
					if (ws.getBallY() - y > 0) {
						// Robot above the ball, quicker to move up
						rotateTo(C.UP);	
					} else {
						// Robot below the ball, quicker to move down
						rotateTo(C.DOWN);
					}
					
					// Move away from the ball
					s.send(0, RobotCommand.FORWARD);
					continue;
				}
				
				// Move to the right x
				if (StrategyHelper.inRange(x, kickP.getX(), DISTANCE_ERROR)) {
					try {
						if (x - kickP.getX() > 0) {
							// Robot to the right of the kickpoint, quicker to move left
							rotateTo(C.LEFT);	
						} else {
							// Robot to the left of the kickpoint, quicker to move right
							rotateTo(C.RIGHT);
						}
						
						s.send(0, RobotCommand.FORWARD);
						continue;
					} catch (Exception e) {
						System.err.println("We don't know where the robot or kick point is :((((");
						s.send(0, RobotCommand.STOP);
						e.printStackTrace();
					}
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
	 * Triggers if we are are not in the right X coordinate to make the kick
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		Point ballP = ws.getBallPoint();
		Point kickP = StrategyHelper.findRobotKickPosition(ballP, ws.getOppositionGoalCentre());
		Point robotP = new Point(ws.getRobotX(r), ws.getRobotY(r));
		
		if (!StrategyHelper.inRange(robotP.getX(), kickP.getX(), DISTANCE_ERROR)) {
			// We are in the wrong X position, get to the right X first
			return true;
		} else {
			// We are in the right X position, do smth else
			return false;
		}
	}

}
