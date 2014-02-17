package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import sun.awt.X11.InfoWindow.Balloon;
import common.Robot;
import constants.C;
import constants.RobotType;

public class MatchAttackPositionX extends GeneralBehavior {

	public MatchAttackPositionX(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		super.action();
		
		while (isActive()) {
			System.out.println("Doing match X");
			if (ws == null) {
				System.err.println("worldstate not intialised");
			}
			
			Point kickP = StrategyHelper.findRobotKickPosition(new Point(ws.ballX, ws.ballY), ws.getOppositionGoalCentre());
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			try {
				int y = ws.getRobotY(robot());
				int x = ws.getRobotX(robot());
				
				System.out.println("Robot: (" + x + ", " + y + ") | kickP: (" + kickP.getX() + ", " + kickP.getY() + ") Ball: (" + ws.ballX + ", " + ws.ballY + ")");
				
				ws.andresPoint = kickP;
				
				// Make sure we are at a good distance from the ball
				if (StrategyHelper.inRange(y, ws.ballY, StrategyHelper.ROBOT_SAFETY_DISTANCE)) {
					// Too close to the ball to just go to x coordinate straight away
					// move away in y direction first
					System.out.println("Robot too close to ball, move to safe Y first");
					
					if (ws.ballY - y > 0) {
						// Robot above the ball, quicker to move up
						if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.UP, ANGLE_ERROR)) {
							rotateTo(C.UP);
							continue;
						}
					} else {
						// Robot below the ball, quicker to move down
						if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.DOWN, ANGLE_ERROR)) {
							rotateTo(C.DOWN);
							continue;
						}
					}
					
					// Move away from the ball
					s.send(0, RobotCommand.FORWARD);
					continue;
				}				

				// Move to the right x
				if (!StrategyHelper.inRange(x, kickP.getX(), DISTANCE_ERROR)) {
					System.out.println("Robot moving to right X");
					
					try {
						if (x - kickP.getX() > 0) {
							// Robot to the right of the kickpoint, quicker to move left
							if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.LEFT, ANGLE_ERROR)) {
								rotateTo(C.LEFT);
								continue;
							}
						} else {
							// Robot to the left of the kickpoint, quicker to move right
							if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.RIGHT, ANGLE_ERROR)) {
								rotateTo(C.RIGHT);
								continue;
							}
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
				System.out.println("Sending STOP");
				s.send(0, RobotCommand.STOP);
			} catch (Exception e) {
				System.err.println("We don't know where the robot or kick point is :((((");
				System.out.println("Sending STOP");
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
		Point ballP = new Point(ws.ballX, ws.ballY);
		Point kickP = StrategyHelper.findRobotKickPosition(ballP, ws.getOppositionGoalCentre());
		Point robotP = ws.getRobotPoint(robot());
		
		if (!StrategyHelper.inRange(robotP.getX(), kickP.getX(), DISTANCE_ERROR)) {
			// We are in the wrong X position, get to the right X first
			return true;
		} else {
			// We are in the right X position, do smth else
			return false;
		}
	}

}
