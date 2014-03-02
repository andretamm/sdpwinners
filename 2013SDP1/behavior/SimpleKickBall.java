package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;

import constants.RobotType;

public class SimpleKickBall extends GeneralBehavior {
	
	public static final double ANGLE_ERROR = 0.3;
	public static final double DISTANCE_ERROR = 0.1;
	public static final double ANGLE270 = Math.PI * 3 / 2.0;

	public SimpleKickBall(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		isActive = true;
		
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
				int x = ws.getRobotX(robot());
				int y = ws.getRobotY(robot());
				int ballX = ws.ballX;
				int ballY = ws.ballY;
				double orientation = ws.getRobotOrientation(robot());
				System.out.print("Robot orientation: " + orientation + " | ");
				
				// Turn to 270
				if (orientation > ANGLE270 + ANGLE_ERROR || orientation < Math.PI/2.0) {
					s.send(type, RobotCommand.CW);
					continue;
				} else if (orientation < ANGLE270 - ANGLE_ERROR) {
					s.send(type, RobotCommand.CCW);
					continue;
				}
				
				System.out.println("Robot: " + x + ", " + y + " | " + "Ball: " + ballX + ", " + ballY);
				
				// Move to same y as ball
				if (ballY - y > DISTANCE_ERROR) {
					System.out.println("First distance: " + (ballY - y));
					s.send(type, RobotCommand.BACK);
					continue;
				} else if (y - ballY > DISTANCE_ERROR) {
					System.out.println("Second distance: " + (y - ballY));
					s.send(type, RobotCommand.FORWARD);
					continue;
				}
				
				// We're in the right position, just chill
				s.send(type, RobotCommand.STOP);
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
