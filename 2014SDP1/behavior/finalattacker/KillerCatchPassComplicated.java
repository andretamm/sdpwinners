package behavior.finalattacker;


import java.awt.Point;
import java.awt.geom.Point2D;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.C;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerCatchPassComplicated extends GeneralBehavior {

	public KillerCatchPassComplicated(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		/*-----------------------------------------------*/
		/* Check if the pass is still happening          */
		/*-----------------------------------------------*/
		
		// Check if the ball isn't going to reach us :P
		if (ws.getBallQuadrant() == ws.getRobotQuadrant(ws.getOpposition(RobotType.ATTACKER))) {
			if (ws.getDirection() == ShootingDirection.LEFT) {
				if (ws.getBallVelocity().getX() > 0) {
					// The ball is moving away from us, pass has failed
					System.out.println("PASS HAS FAILED");
					ws.setDoingPass(false);
					ws.setKickedPass(false);
					return;
				}
			} else {
				if (ws.getBallVelocity().getX() < 0) {
					// The ball is moving away from us, pass has failed
					System.out.println("PASS HAS FAILED");
					ws.setDoingPass(false);
					ws.setKickedPass(false);
					return;
				}
			}
		}
		
		// Check if the ball has reached our quadrant
		if (ws.getBallQuadrant() == ws.getRobotQuadrant(robot())) {
			ws.setDoingPass(false);
			return;
		}
		
		/*--------------------------------------------------*/
		/* We think the pass is happening, rotate right way */
		/*--------------------------------------------------*/
		double angleRadians;
		
		if (ws.getDirection() == ShootingDirection.LEFT) {
			// Ball coming from the right
			angleRadians = StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), C.RIGHT);
		} else {
			// Ball coming from the left
			angleRadians = StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), C.LEFT);
		}
		
		if (angleRadians > ANGLE_ERROR) {
			rotateBy((int) Math.toDegrees(angleRadians));
		}
		
		/*--------------------------------------------------*/
		/* Make robot get in right place to catch the ball  */
		/*--------------------------------------------------*/
		Point target;
		Point qMiddle = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		
		if (ws.getKickedPass()) {
			// Ball is on its way, follow the ball vector instead of the robot orientation
			int defendLine;
			
			if (ws.getDirection() == ShootingDirection.LEFT) {
				defendLine = qMiddle.x + 30;
			} else {
				defendLine = qMiddle.x - 30;
			}
			
			Point2D.Double ballVector = ws.getBallVelocity();
			Point ball = ws.getBallPoint();
			
			// Check where ball would hit the wall
			Point wallHitPoint = StrategyHelper.getIntersectsWithWalls(ballVector, ball, ws);
			
			// Target point on the defend line
			Point defendLinePoint;
					
			if ((ball.y > ws.getOurGoalCentre().y && ballVector.y < 0) ||
				(ball.y < ws.getOurGoalCentre().y && ballVector.y > 0)) {
				// Ball moving to bottom or top of pitch, must collide with wall
				defendLinePoint = StrategyHelper.getIntersectWithVerticalLine(defendLine, wallHitPoint, StrategyHelper.collideWithHorizontalWall(ballVector));
			} else {
				// Heading directly towards us, get intersect
				defendLinePoint = StrategyHelper.getIntersectWithVerticalLine(defendLine, ball, ballVector);
			}
			
			// Our target is behind the defend line so the ball lands in our grabber :)
			target = defendLinePoint;
			target.x = qMiddle.x;
		} else {
			// Still positioning, so stay in the middle
			target = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(robot()));
		}
		
		if (!goDiagonallySlowlyTo(target)) {
			return;
		}
		
		// Right spot and orientation, chill there
		s.send(type, RobotCommand.STOP);
	}

	/** 
	 * Triggers if we're trying to do a pass. We are responsible for
	 * cancelling the doingPass flag in this action!!!
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.getDoingPass();
	}

}
