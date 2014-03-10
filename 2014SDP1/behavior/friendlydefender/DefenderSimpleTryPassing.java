package behavior.friendlydefender;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.Strategy;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.C;
import constants.Quadrant;
import constants.RobotType;
import constants.ShootingDirection;

public class DefenderSimpleTryPassing extends GeneralBehavior {

	public DefenderSimpleTryPassing(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		// Stop this madness if we didn't actually grab the ball <.<
//		if (!StrategyHelper.hasBall(robot(), ws)) {
//			ws.setRobotGrabbedBall(robot(), false);
//			s.send(type, RobotCommand.OPEN_GRABBER);
//			return;
//		}
		
		/*-----------------------------------------------*/
		/* Decide which way to shoot                     */
		/*-----------------------------------------------*/
		Point target = null;
		Point robot = ws.getRobotPoint(robot());
		
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
		
		/*-----------------------------------------------*/
		/* Rotate to that way                            */
		/*-----------------------------------------------*/
		double orientation = Orientation.getAngle(robot, target);
		
		if (Math.abs(StrategyHelper.angleDiff(ws.getRobotOrientation(robot()), orientation)) > ANGLE_ERROR) {
			rotateTo(orientation);
			return;
		}
		
		stopRotating();
		
		/*-----------------------------------------------*/
		/* Kick the baby                                 */
		/*-----------------------------------------------*/
		
		// No longer have the ball after kick
		System.out.println("KICK NOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		s.send(type, RobotCommand.KICK);
		ws.setRobotGrabbedBall(robot(), false);
		Strategy.defenderReadyForPass = false;
		
		// Wait a wee bit so we don't retrigger grabbing the ball
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
