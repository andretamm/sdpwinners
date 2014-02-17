package behavior;

import constants.RobotType;
import ourcommunication.Server;
import sdp.vision.WorldState;

/**
 * Controls the strategies running on the robots. Has methods
 * for changing strategies, starting and stopping them.
 * 
 * All strategies have to be subclasses of Manager.
 * 
 * @author Andre
 */
public class Strategy {
	WorldState ws;
	Server server;
	
	Manager defender;
	Manager attacker;
	
	public Strategy(WorldState ws, Server server) {
		this.ws = ws;
		this.server = server;
	}
	
	/**
	 * Set the strategy to use for the robot
	 * @param robottype Attacker or Defender
	 * @param strategy Manager for the strategy to use
	 */
	public void setStrategy(RobotType robottype, Manager strategy) {
		if (robottype == RobotType.ATTACKER) {
			attacker = strategy;
		} else {
			defender = strategy;
		}
	}
	
	/**
	 * Starts all selected strategies
	 */
	public void start() {
		System.out.println("Start running strategies");
		
		if (defender != null) {
			defender.start();
		}
		
		if (attacker != null) {
			attacker.start();
		}
	}
	
	/**
	 * Stops all currently running strategies
	 */
	public void stop() {
		System.out.println("Stop running strategies");
		
		if (defender != null) {
			defender.stop();
		}
		
		if (attacker != null) {
			attacker.stop();
		}
	}
}
