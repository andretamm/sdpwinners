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

public class KillerRotateToGoalAndScore extends GeneralBehavior {

	public KillerRotateToGoalAndScore(WorldState ws, Robot r, Server s) {
		super(ws, r, s);
	}

	@Override
	public void action() {
		super.action();
		
		while (isActive()) {
			if (ws == null) {
				System.err.println("worldstate not intialised");
			}
			
			Point robot = new Point(ws.getRobotX(r), ws.getRobotY(r));
			Point goal = ws.getOppositionGoalCentre();
			double orientation = Orientation.getAngle(robot, goal);
			
			if (!StrategyHelper.inRange(ws.getRobotOrientation(r.type, r.colour), orientation, ANGLE_ERROR)) {
				rotateTo(orientation);
				continue;
			}
			
			s.send(0, RobotCommand.KICK);
		}
	}

	/** 
	 * Triggers if we have the ball
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return ws.haveBall();
	}

}
