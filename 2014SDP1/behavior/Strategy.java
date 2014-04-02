package behavior;

import communication.Server;

import constants.RobotType;
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
	
	public static RobotState defenderState;
	public static RobotState attackerState;
	
	volatile public static boolean defenderReadyForPass = false;
	volatile public static boolean attackerReadyForKick = false;
	
	/**
	 * This checks if we saw the oppositions attacker have the ball 
	 * _at some point_ now. Used to check whether we might be trying
	 * to protect the goal from a ball being kicked from their robot.
	 * 
	 * This should be set to false straight away when we're sure the opponent
	 * does not have the ball any more.
	 */
	volatile public static boolean opAttackerHadBall = false;
	
	public Strategy(WorldState ws, Server server) {
		this.ws = ws;
		this.server = server;
		
		defenderState = new RobotState();
		attackerState = new RobotState();
	}
	
	public static RobotState state(RobotType type) {
		if (type == RobotType.ATTACKER) {
			return attackerState;
		} else {
			return defenderState;		
		}
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
	
	/**
	 * Start the attacker strategy
	 */
	public void startAttacker() {
		System.out.println("Start running ATTACKER strategy");
		
		if (attacker != null) {
			attacker.start();
		}
	}
	
	/**
	 * Stop the attacker strategy
	 */
	public void stopAttacker() {
		System.out.println("Stop running ATTACKER strategy");
		
		if (attacker != null) {
			attacker.stop();
		}
	}
	
	/**
	 * Start the defender strategy
	 */
	public void startDefender() {
		System.out.println("Start running DEFENDER strategy");
		
		if (defender != null) {
			defender.start();
		}
	}
	
	/**
	 * Stop the defender strategy
	 */
	public void stopDefender() {
		System.out.println("Stop running DEFENDER strategy");
		
		if (defender != null) {
			defender.stop();
		}
	}
}
