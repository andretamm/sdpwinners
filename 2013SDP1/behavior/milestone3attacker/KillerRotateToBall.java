package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.Server;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerRotateToBall extends GeneralBehavior {

	public KillerRotateToBall(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		System.out.println("Rotating towards ball");
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		// Correct the angle
		Point robot = ws.getRobotPoint(robot());
		Point ball = new Point(ws.ballX, ws.ballY);
		double orientation = Orientation.getAngle(robot, ball);
		
		if (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR)) {
			rotateTo(orientation);
		}
	}

	/** 
	 * Triggers if we are NOT facing the ball
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		Point robot = ws.getRobotPoint(robot());
		Point ball = ws.getBallPoint();
		double orientation = Orientation.getAngle(robot, ball);

		return (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR) &&
				!ws.haveBall());
	}

}
