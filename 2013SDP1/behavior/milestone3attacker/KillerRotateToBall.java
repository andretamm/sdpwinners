package behavior.milestone3attacker;

import java.awt.Point;

import behavior.GeneralBehavior;
import behavior.StrategyHelper;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.Orientation;
import sdp.vision.WorldState;
import sun.awt.X11.InfoWindow.Balloon;
import common.Robot;
import constants.C;
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
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 
	 * Triggers if we are not facing the ball
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
//		System.out.println("do robot rotation?");
		Point robot = ws.getRobotPoint(robot());
		Point ball = new Point(ws.ballX, ws.ballY);
		double orientation = Orientation.getAngle(robot, ball);
//		System.out.println("RESULT: "+!StrategyHelper.inRange(ws.getRobotOrientation(r.type, r.colour), orientation, ANGLE_ERROR));
		return (!StrategyHelper.inRange(ws.getRobotOrientation(robot()), orientation, ANGLE_ERROR) &&
				!ws.haveBall());
	}

}
