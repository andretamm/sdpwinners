package behavior.finalattacker;


import java.awt.Point;
import java.awt.geom.Point2D;

import communication.RobotCommand;
import communication.Server;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.Quadrant;
import constants.QuadrantX;
import constants.RobotType;
import constants.ShootingDirection;

public class KillerBlockDefender extends GeneralBehavior {

	public KillerBlockDefender(WorldState ws, RobotType type, Server s) {
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
		
		
		// Where we need to go to
		Point target = null;
		
		// Get the robot's quadrant's middle point's x coordinate
		Quadrant q = ws.getRobotQuadrant(robot());
		int quadrantMiddleX = (ws.getQuadrantX(q, QuadrantX.LOW) + ws.getQuadrantX(q, QuadrantX.HIGH)) / 2 ;
		
		
		// The opponent's defender's orientation
		Point2D.Double opOrientationVector = ws.getRobotOrientationVector(ws.getOpposition(RobotType.DEFENDER));
		Point opPosition = ws.getOppositionDefenderPosition();
		
		if (ws.getBallQuadrant() == ws.getRobotQuadrant(ws.getOpposition(RobotType.DEFENDER))
		    &&
		     ((ws.getDirection() == ShootingDirection.RIGHT &&
			  opOrientationVector.x < 0)
		      ||
		     (ws.getDirection() == ShootingDirection.LEFT &&
			  opOrientationVector.x > 0))) {
			// Their defender has the ball and is oriented towards us, show 'em who's boss
			// by blocking their shooting path
			
			// Find where a kick from them would pass our quadrant's middle point
			target = StrategyHelper.getIntersectWithVerticalLine(quadrantMiddleX, opPosition, opOrientationVector);
		} else if (ws.onPitch(ws.getBallPoint())) {
			// The ball is not in their defender's quadrant or 
			// their defender is facing the other way - track the ball instead
			// (but only if it's on the pitch)
			target = new Point(quadrantMiddleX, ws.getBallY());
		}
		
		if (target == null) {
			// Didn't find a point, go to the middle instead :)
			target = ws.getQuadrantMiddlePoint(q);
		} else {
			// Make sure point is in the bounds
			target.y = Math.max(ws.getPitchTopLeft().y + 45, Math.min(ws.getPitchBottomLeft().y - 45, target.y));
		}
		
		/*-------------------------------------*/
		/* Attacker calibration points         */
		/*-------------------------------------*/
		// We always try going towards one of these, that will stop us from 
		// making unnecessarily jerky movements.
		Point topPoint = new Point(quadrantMiddleX, ws.getPitchTopLeft().y - 50);
		Point bottomPoint = new Point(quadrantMiddleX, ws.getPitchBottomRight().y + 50);

		// Pick calibration point to use
		Point calibrationTarget;
		Point robot = ws.getRobotPoint(robot());

		if (target.y < robot.y) {
			calibrationTarget = topPoint;
		} else {
			calibrationTarget = bottomPoint;
		}
		
		/*-------------------------------------*/
		/* Get going!                          */
		/*-------------------------------------*/
		// Measure error based on our target, but actually go towards the calibrationpoint!
		if (StrategyHelper.getDistance(robot, target) > DISTANCE_ERROR) {
			if (!goDiagonallyTo(calibrationTarget)) {
				// not there yet
				return;
			}
		}

		// We're there!
		stop();
	}

	/** 
	 * Triggers if we the ball is not in our Quadrant
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return true;
//		return (ws.getBallQuadrant() != ws.getRobotQuadrant(robot()));
	}

}
