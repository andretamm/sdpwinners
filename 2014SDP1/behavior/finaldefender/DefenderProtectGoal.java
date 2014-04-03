package behavior.finaldefender;

import java.awt.Point;
import java.awt.geom.Point2D;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;
import constants.ShootingDirection;

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
				
		/*-------------------------------------*/
		/* Defender calibration points         */
		/*-------------------------------------*/
		// We always try going towards one of these, that will stop us from 
		// making unnecessarily jerky movements.
		Point topPoint = new Point(ws.getOurGoalTop());
		topPoint.y -= 10;
		topPoint.x = StrategyHelper.getDefendLineX(ws);

		Point bottomPoint = new Point(ws.getOurGoalBottom());
		bottomPoint.y += 10;
		bottomPoint.x = StrategyHelper.getDefendLineX(ws);

		try {
			/*-------------------------------------*/
			/* Decide which blocking method to use */
			/*-------------------------------------*/
			
			/* Decide what to use as a vector modelling the ball movement */
			Point2D.Double ballVector = null;
			Point ballOrigin = null;
					
			if (StrategyHelper.hasBall(ws.getOpposition(RobotType.ATTACKER), ws, 35, ANGLE_ERROR * 1.2)) {
				// They have the ball, defo use their attacker's orientation
				Strategy.opAttackerHadBall = true;
				ballVector = ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER));
				ballOrigin = ws.getOppositionAttackerPosition();
			} else if (Strategy.opAttackerHadBall &&
					   ((ws.getBallVelocity().x > 0.02 && ws.getDirection() == ShootingDirection.LEFT) ||
					    (ws.getBallVelocity().x < -0.02 && ws.getDirection() == ShootingDirection.RIGHT)) &&
					   StrategyHelper.magnitude(ws.getBallVelocity()) > 0.05) {
				// They just had the ball and now it's headed towards our goal, better try blocking the ball!
				ballVector = ws.getBallVelocity();
				ballOrigin = ws.getBallPoint();
			} else {
				// They don't have the ball (or they lost it), but block them any way just in case
				Strategy.opAttackerHadBall = false;
				ballVector = ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER));
				ballOrigin = ws.getOppositionAttackerPosition();
			}
			
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
			// Actually use calibration points when deciding where to go to
			Point calibrationTarget;
			Point robot = ws.getRobotPoint(robot());

			if (target.y < robot.y) {
				calibrationTarget = topPoint;
			} else {
				calibrationTarget = bottomPoint;
			}

			/*-------------------------------------*/
			/* Go to our target                    */
			/*-------------------------------------*/

			// Measure error based on our target, but actually go towards the calibrationpoint!
			if (Math.abs(robot.y - target.y) > DISTANCE_ERROR - 6) {
				if (!goDiagonallyTo(calibrationTarget)) {
					// not there yet
					return;
				}
			}

			// We're there!
			stop();
			
		} catch (Exception e) {
			System.err.println("We don't know where the robot is :((((");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * 
	 * This is the default behavior for the defender
	 * 
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return true;
	}

}
