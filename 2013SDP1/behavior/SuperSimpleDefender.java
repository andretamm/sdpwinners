package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import common.Robot;
import constants.C;

public class SuperSimpleDefender extends GeneralBehavior {

	

	public SuperSimpleDefender(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		try {
//			System.out.println("Defender in action");
			//Get the robots coordinates
			int x = ws.getRobotX(r);
			int y = ws.getRobotY(r);
			
			//Get the balls coordinates
			int ballX = ws.ballX;
			int ballY = ws.ballY;
			
			// Move to same y as ball
			if (ballY - y > DISTANCE_ERROR) {
				System.out.println("Moving DOWN: " + (ballY - y));
				if (!isMoving || movingCounter == 0) {
					isMoving = true;
					s.send(0, RobotCommand.BACK);
				}
				return;
			} else if (y - ballY > DISTANCE_ERROR) {
				System.out.println("Moving UP: " + (y - ballY));
				if (!isMoving || movingCounter == 0) {
					isMoving = true;
					s.send(0, RobotCommand.FORWARD);
				}
				return;
			}
			
			// We're in the right position, just chill
			s.send(0, RobotCommand.STOP);
			isMoving = false;
			
		} catch (Exception e) {
			System.err.println("We don't know where the robot is :((((");
			e.printStackTrace();
		}
	}

	@Override
	public boolean takeControl() {
		return true;
	}

}
