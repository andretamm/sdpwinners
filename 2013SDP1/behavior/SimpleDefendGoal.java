package behavior;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import common.Robot;
import sdp.vision.WorldState;

public class SimpleDefendGoal extends GeneralBehavior {
	
	public static final int ANGLE_ERROR = 2;
	public static final int DISTANCE_ERROR = 3;
	
	public SimpleDefendGoal(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}
	
	@Override
	public void action() {
		while (isActive()) {
			int x = ws.getRobotX(r);
			int y = ws.getRobotY(r);
			int ballY = ws.getBallY();
			double orientation = ws.getRobotOrientation(r.type, r.colour);
			
			// Turn to 270
			if (orientation > 270 + ANGLE_ERROR || orientation < 90) {
				s.send(0, RobotCommand.CW);
				continue;
			} else if (orientation < 270 - ANGLE_ERROR) {
				s.send(0, RobotCommand.CCW);
				continue;
			}
			
			// Move to same y as ball
			if (ballY - y > DISTANCE_ERROR) {
				s.send(0, RobotCommand.BACK);
				continue;
			} else if (y - ballY > DISTANCE_ERROR) {
				s.send(0, RobotCommand.FORWARD);
				continue;
			}
			
			// We're in the right position, just chill
			s.send(0, RobotCommand.STOP);
		}
	}

	@Override
	public boolean takeControl() {
		return true;
	}

}
