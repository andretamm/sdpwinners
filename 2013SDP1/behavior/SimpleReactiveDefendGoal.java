package behavior;

import java.awt.Point;

import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;

/**
 * Tries to go to the position where the opponent would kick the ball
 * based on the orientation of their attacker robot.
 */
public class SimpleReactiveDefendGoal extends GeneralBehavior {

	public SimpleReactiveDefendGoal(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		try {
//			System.out.println("Defender in action");
			
			//Get the robot coordinates
			int x = ws.getRobotX(robot());
			int y = ws.getRobotY(robot());
			
			// Rotate to 90'			
			if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.DOWN, ANGLE_ERROR)) {
				isRotating = true;
//				System.out.println("Rotating");
				rotateTo(C.DOWN);
				return;
			}
			
			// Finished rotating
			if (isRotating) {
				isRotating = false;
				s.send(type, RobotCommand.STOP);
			}
			
			// The position where we will intercept the ball
			Point target = StrategyHelper.getIntersectWithVerticalLine(ws.getOurGoalCentre().x - 60, ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER)), ws.getOppositionAttackerPosition());
			int targetY = target.y;
			int targetX = target.x;
			
			// Move to same y as ball
			if ((targetY - y > (DISTANCE_ERROR + 25)) && (y < (ws.getPitchBottomLeft().getY() - 90))) {
				System.out.println("Moving DOWN: " + (targetY - y));
				s.send(type, RobotCommand.BACK);
				return;
			}
			else if (((y - targetY) > (DISTANCE_ERROR + 25)) && (y > (ws.getPitchTopLeft().getY() + 90))) {
				System.out.println("Moving UP: " + (y - targetY));
				s.send(type, RobotCommand.FORWARD);
				return;
			} 

			// We're in the right position, just chill
//			System.out.println("Stopping");
			s.send(type, RobotCommand.STOP);
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
