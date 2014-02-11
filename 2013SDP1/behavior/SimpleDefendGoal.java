package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import common.Robot;
import constants.C;

public class SimpleDefendGoal extends GeneralBehavior {

	

	public SimpleDefendGoal(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}

	@Override
	public void action() {
		isActive = true;

		while (isActive()) {
			if (ws == null) {
				System.err.println("worldstate not intialised");
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			try {
				int x = ws.getRobotX(r);
				int y = ws.getRobotY(r);
				int ballX = ws.ballX;
				int ballY = ws.ballY;
				
				// Rotate to 270
				rotateTo(C.A270);
				
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
