package behavior.friendlyattacker;


import behavior.GeneralBehavior;
import ourcommunication.RobotCommand;
import ourcommunication.Server;
import sdp.vision.WorldState;
import constants.RobotType;

public class KillerGotoBallAndGrab extends GeneralBehavior {

	public KillerGotoBallAndGrab(WorldState ws, RobotType type, Server s) {
		super(ws, type, s);
	}

	@Override
	public void action() {
		System.out.println("Going to ball");
		
		if (ws == null) {
			System.err.println("worldstate not intialised");
		}
		
		if (goToBall()) {
			// We're at the ball. so grab it
			s.send(type, RobotCommand.GRAB);
			ws.setHaveBall(true);
		}
	}

	/** 
	 * Triggers if we do NOT have the ball
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		return (!ws.haveBall());
	}

}
