package behavior.friendlydefender;

import java.awt.Point;
import java.awt.geom.Point2D;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;

/**
 * Tries to go to the position where the opponent would kick the ball
 * based on the orientation of their attacker robot.
 */
public class DefenderProtectGoal extends GeneralBehavior {

	public DefenderProtectGoal(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		// If the ball has just left our quadrant, try opening the grabbers just in case
		if (state().grabberState != 0) {
			s.send(type, RobotCommand.OPEN_GRABBER);
			s.forceSend(type, RobotCommand.OPEN_GRABBER);
			state().grabberState = 0;
		}

		try {
			/*-------------------------------------*/
			/* Decide which blocking method to use */
			/*-------------------------------------*/
			
			/* Decide what to use as a vector modelling the ball movement */
			Point2D.Double ballVector = null;
			Point ballOrigin = null;
			
			// The opponent's orientation by default
			ballVector = ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER));
			ballOrigin = ws.getOppositionAttackerPosition();
			
			/*-------------------------------------*/
			/* Figure out where we need to be      */
			/* to intercept the ball               */
			/*-------------------------------------*/
			
			// Our defend line
			int defendX = StrategyHelper.getDefendLineX(ws);

			// Check if it intersects with our defending line
			Point target = StrategyHelper.getIntersectWithVerticalLine(defendX, ballOrigin, ballVector);
			
			if (target == null || !ws.onPitch(target)) {
				// Doesn't intersect with the defending line
				// Check if intersects after a wall bounce
				ballOrigin = StrategyHelper.getIntersectsWithWalls(ballVector, ballOrigin, ws);
				
				if (ballOrigin != null) {
					// Will hit a wall, see what happens afterwards
					ballVector = StrategyHelper.collideWithHorizontalWall(ballVector);
					
					// Only defend if it'll actually hit our goal
					Point goalIntersect = StrategyHelper.getIntersectWithOurGoal(ballVector, ballOrigin, ws);
					
					if (goalIntersect != null) {
						// Find defending point after wall collision
						target = StrategyHelper.getIntersectWithVerticalLine(defendX, ballOrigin, ballVector);
					} else {
						// TODO - Find where they COULD hit the goal and try to intercept that
						StrategyHelper.findGoalTopDefendPosition(ws);
						
						double oppositionOrientation = ws.getRobotOrientation(ws.getOpposition(RobotType.ATTACKER));
						
						if (StrategyHelper.angleDiff(oppositionOrientation, C.A270) < C.A90) {
							// They're aiming UP, try to protect kick to BOTTOM goal
							target = StrategyHelper.findGoalBottomDefendPosition(ws);
						} else {
							// They're aiming DOWN, try to protect kick to TOP goal
							target = StrategyHelper.findGoalTopDefendPosition(ws);
						}
					}
				}
			}
			
			// If we didn't find an intersect, then just match the ball's y coordinate
			if (target == null) {
				target = new Point(defendX, ws.getBallY());
			}
			
			// Don't go past the goal!
			target.y = Math.min(Math.max(target.y, ws.getOurGoalTop().y + 5), ws.getOurGoalBottom().y - 5);
			
			/*-------------------------------------*/
			/* Finally know where we need to be !  */
			/*-------------------------------------*/
			
			// Quickly go there :))
			if (quickGoTo(target)) {
				stop();
			}

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
