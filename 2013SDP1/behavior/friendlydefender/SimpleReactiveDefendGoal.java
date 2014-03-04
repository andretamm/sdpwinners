package behavior.friendlydefender;

import java.awt.Point;
import java.awt.geom.Point2D;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
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
			
//			// Rotate to 90'			
//			if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), C.DOWN, ANGLE_ERROR)) {
//				isRotating = true;
//				d("Rotating down");
//				rotateTo(C.DOWN);
//				return;
//			}
//			
//			// Finished rotating
//			if (isRotating) {
//				d("Stop rotating");
//				isRotating = false;
//				s.send(type, RobotCommand.STOP);
//			}
			
			/* Decide what to use as a vector modelling the ball movement */
			Point2D.Double ballVector = null;
			Point ballOrigin = null;
			
			// The opponent's orientation by default
			ballVector = ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER));
			ballOrigin = ws.getOppositionAttackerPosition();
			
			/* Find the position where we will intercept the ball */
			// Our defend line
			int defendX = StrategyHelper.getDefendLineX(ws);

			// Check if it intersects with our defending line
			Point target = StrategyHelper.getIntersectWithVerticalLine(defendX, ballOrigin, ballVector);
			
			if (target == null || !ws.onPitch(target)) {
				// Doesn't intersect with the defending line
				// Check if intersects after a wall bounce
				ballOrigin = StrategyHelper.getIntersectsWithWalls(ballVector, ballOrigin, ws);
				
				if (ballOrigin != null) {
					// Hits a wall, find defending point after wall collision
					ballVector = StrategyHelper.collideWithHorizontalWall(ballVector);
					target = StrategyHelper.getIntersectWithVerticalLine(defendX, ballOrigin, ballVector);
				}
			}
			
			// If we didn't find an intersect, then just match the ball's y coordinate
			if (target == null) {
				target = new Point(defendX, ws.getBallY());
			}
			
			// We finally know where we need to be!
			// Quickly go there :))
			quickGoTo(target);
			
			// TODO - We should use this time to rotate to 270 degrees if we're already there!!!!
			
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
