package behavior.finaldefender;

import java.awt.Point;

import communication.RobotCommand;
import communication.Server;
import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.RobotType;

public class DefenderDoPass extends GeneralBehavior {
	
	public static Point target = null;
	
	public DefenderDoPass(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
			
		/*-----------------------------------------------*/
		/* Decide which way to shoot                     */
		/*-----------------------------------------------*/
		Point robot = ws.getRobotPoint(robot());
		
		if (target == null) {
			Point opAttackerMiddle = ws.getQuadrantMiddlePoint(ws.getRobotQuadrant(ws.getOpposition(RobotType.ATTACKER)));
			
			Point[] targets = new Point[2];
			double opponentDistances[] = new double[2];
			
			Point topWallMiddle = new Point(opAttackerMiddle.x, ws.getPitchTopLeft().y);
			Point bottomWallMiddle = new Point(opAttackerMiddle.x, ws.getPitchBottomLeft().y);
			
			targets[0] = topWallMiddle;
			targets[1] = bottomWallMiddle;
	
			opponentDistances[0] = StrategyHelper.getOpponentDistanceFromPath(robot(), Orientation.getAngle(robot, targets[0]), ws);
			opponentDistances[1] = StrategyHelper.getOpponentDistanceFromPath(robot(), Orientation.getAngle(robot, targets[1]), ws);
	
			if (opponentDistances[0] < opponentDistances[1]) {
				d("Picked target 1");
				target = targets[1];
			} else {
				d("Picked target 0");
				target = targets[0];
			}
		}

		/*-----------------------------------------------*/
		/* Rotate that way                               */
		/*-----------------------------------------------*/
		double orientation = Orientation.getAngle(robot, target);
		
		// Wait until we're close enough
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateBy((int) Math.toDegrees(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)));
			return;
		}
		
		// Robot stops automagically after doing rotating
		if (state().isRotating) {
			state().isRotating = false;
		}
		
		
		/*-----------------------------------------------*/
		/* Check if the kick is feasible                 */
		/*-----------------------------------------------*/
		// We're close!
		// See how close the opponent is
		double shotAngle = orientation;
		double oppositionDistance = StrategyHelper.getOpponentDistanceFromPath(robot(), shotAngle, ws);

		if (oppositionDistance < 100) {
			if (state().defenderNumberOfTargetsTried < 4) {
				// They're too close, try again
				target = null;
				return;
			} else {
				// Already tried too many times, just shoot
			}
		}
		
		
		/*-----------------------------------------------*/
		/* Kick the baby                                 */
		/*-----------------------------------------------*/
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, RobotCommand.SLOW_KICK);
		
		// No longer have the ball after kick
		ws.setRobotGrabbedBall(robot(), false);
		Strategy.defenderReadyForPass = false;
		
		// Wait a wee bit so we don't retrigger grabbing the ball
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Next time pick new target :))
		target = null;
	}

	/** 
	 * Triggers if we have the ball
	 * NB - this has to be lower priority than GetInPositionForPass!!!
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return Strategy.defenderReadyForPass;
	}
}
