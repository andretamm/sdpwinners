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

public class KillerCatchBall extends GeneralBehavior {

	public KillerCatchBall(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}

	@Override
	public void action() {
	
		System.out.println("catching ball");
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		s.send(0, RobotCommand.GRAB);
		
		ws.setHaveBall(true); // UGLY FILTHY HACK
		s.receiveHaveBall();
	}

	/** 
	 * Triggers if are facing the ball, but haven't caught it yet
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		Point robot = new Point(ws.getRobotX(r), ws.getRobotY(r));
		Point ball = new Point(ws.ballX, ws.ballY);
		double orientation = Orientation.getAngle(robot, ball);
		
//		System.out.println("catch ball " + StrategyHelper.inRange(ws.getRobotOrientation(r.type, r.colour), orientation, ANGLE_ERROR));
//		System.out.println(ws.getRobotOrientation(r.type, r.colour) - orientation);
		return (StrategyHelper.inRange(ws.getRobotOrientation(r.type, r.colour), orientation, ANGLE_ERROR) &&
				!ws.haveBall());
	}
}
