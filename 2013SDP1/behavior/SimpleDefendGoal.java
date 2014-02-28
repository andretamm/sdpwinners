package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;

public class SimpleDefendGoal extends GeneralBehavior {

	public SimpleDefendGoal(WorldState ws, RobotType type, Server s) {
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
			
			// Rotate to 90'			
			rotatingCounter = 1;
			rotatingCounter = rotatingCounter % 20;

			if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.DOWN, ANGLE_ERROR)) {
				isRotating = true;
				System.out.println("Rotating");
				if(rotatingCounter == 1) {
					rotateTo(C.DOWN);
				}
				return;
			}
			
			// Finished rotating
			if (isRotating) {
				isRotating = false;
				s.send(0, RobotCommand.STOP);
			}
			rotatingCounter = 0;
			
//			while (ws.ballX > ws.getQ4LowX()) {
////				if (ws.getOurGoalCentre().y - y > DISTANCE_ERROR + 35) {
////					s.send(0, RobotCommand.BACK);
////				} else if (y - ws.getOurGoalCentre().y > DISTANCE_ERROR + 35) {
////					s.send(0, RobotCommand.FORWARD);
////				} else {
////					s.send(0, RobotCommand.STOP);
////				}
//			}

			// Move to same y as ball
//			movingCounter++;
//			movingCounter = movingCounter % 20;
//			System.out.println(y + " <? " + (ws.getPitchBottomLeft().getY() - 50));
//			System.out.println(y + " >? " + (ws.getPitchTopLeft().getY() + 50));
			
			if ((ballY - y > (DISTANCE_ERROR + 25)) && (y < (ws.getPitchBottomLeft().getY() - 90))) {
//				if (!isMoving || movingCounter == 1) {
				if(!isMovingDown) {
					System.out.println("Moving DOWN: " + (ballY - y));
					isMovingDown = true;
					isMovingUp = false;
					s.send(0, RobotCommand.BACK);
				}
//				}
				return;
			}
			else if (((y - ballY) > (DISTANCE_ERROR + 25)) && (y > (ws.getPitchTopLeft().getY() + 90))) {
				
//				if (!isMoving || movingCounter == 1) {
				if (!isMovingUp) {
					System.out.println("Moving UP: " + (y - ballY));
					isMovingUp = true;
					isMovingDown = false;
					s.send(0, RobotCommand.FORWARD);
				}
//				}
				return;
			} 
			movingCounter = 0;
			
			
			// We're in the right position, just chill
//			stopCounter++;
//			stopCounter = stopCounter % 10;
//			if (stopCounter == 1) {
			if (isMovingDown || isMovingUp) {
				System.out.println("Stopping");
				isMovingUp = false;
				isMovingDown = false;
				s.send(0, RobotCommand.STOP);
			}
//			}
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
