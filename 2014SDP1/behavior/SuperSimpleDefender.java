package behavior;

import communication.RobotCommand;
import communication.Server;

import vision.WorldState;
import constants.RobotType;

public class SuperSimpleDefender extends GeneralBehavior {

	

	public SuperSimpleDefender(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		try {
//			System.out.println("Defender in action");
			//Get the robots coordinates
			int x = ws.getRobotX(robot());
			int y = ws.getRobotY(robot());
			
			//Get the balls coordinates
			int ballX = ws.ballX;
			int ballY = ws.ballY;
			
			// Move to same y as ball
			if (ballY - y > DISTANCE_ERROR) {
				System.out.println("Moving DOWN: " + (ballY - y));
				if (!isMoving || movingCounter == 0) {
					isMoving = true;
					s.send(type, RobotCommand.BACK);
				}
				return;
			} else if (y - ballY > DISTANCE_ERROR) {
				System.out.println("Moving UP: " + (y - ballY));
				if (!isMoving || movingCounter == 0) {
					isMoving = true;
					s.send(type, RobotCommand.FORWARD);
				}
				return;
			}
			
			// We're in the right position, just chill
			s.send(type, RobotCommand.STOP);
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
