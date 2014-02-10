package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;

import common.Robot;

public class SimpleDefendGoal extends GeneralBehavior {
	
	public static final double ANGLE_ERROR = 0.3;
	public static final double DISTANCE_ERROR = 0.1;
	public static final double ANGLE270 = Math.PI * 3 / 2.0;
	
	public SimpleDefendGoal(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}
	
	@Override
	public void action() {
		isActive = true;
		System.out.println("omg");
		while (isActive()) {
			if (ws == null) {
				System.err.println("worldstate not intialised");
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			try {
				int x = ws.getRobotX(r);
				int y = ws.getRobotY(r);
				int ballX = ws.ballX;
				int ballY = ws.ballY;
				double orientation = ws.getRobotOrientation(r.type, r.colour);
				System.out.print("Robot orientation: " + orientation + " | ");
				
				// Turn to 270
				if (orientation > ANGLE270 + ANGLE_ERROR || orientation < Math.PI/2.0) {
					s.send(0, RobotCommand.CW);
					continue;
				} else if (orientation < ANGLE270 - ANGLE_ERROR) {
					s.send(0, RobotCommand.CCW);
					continue;
				}
				
				System.out.println("Robot: " + x + ", " + y + " | " + "Ball: " + ballX + ", " + ballY);
				
				// Move to same y as ball
				if (ballY - y > DISTANCE_ERROR) {
					System.out.println("First distance: " + (ballY - y));
					s.send(0, RobotCommand.BACK);
					continue;
				} else if (y - ballY > DISTANCE_ERROR) {
					System.out.println("Second distance: " + (y - ballY));
					s.send(0, RobotCommand.FORWARD);
					continue;
				}
				
				// We're in the right position, just chill
				s.send(0, RobotCommand.STOP);
			} catch (Exception e) {
				System.err.println("We don't know where the robot is :((((");
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean takeControl() {
		return true;
	}

}
