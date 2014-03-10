package behavior;

import communication.Server;

import constants.RobotType;
import vision.WorldState;
import lejos.robotics.subsumption.Behavior;

/**
 * Manages all the different behaviors and the arbitrator which picks which
 * behavior to use. 
 * 
 * @author Andre
 *
 */
public abstract class Manager {
	
	private AndreArbitrator arbitrator;
	private WorldState ws;
	private RobotType type;
	private Server s;
	private Thread arbitratorThread;
	
	/**
	 * Initialises manager with a list of default behaviors 
	 */
	public Manager(WorldState ws, RobotType type, Server s) {
		this.ws = ws;
		this.type = type;
		this.s = s;
		arbitrator = new AndreArbitrator(getAllBehaviors());
	}
	
	/**
	 * Starts picking and running behaviors on a new thread
	 */
	public void start() {
		arbitratorThread = new Thread() {
			@Override
			public void run() {
				super.run();
				System.out.println("Manager started");
				arbitrator.start();				
			}
		};
		
		arbitratorThread.start();
	}
	
	/**
	 * Stops the arbitrator
	 */
	public void stop() {
		arbitrator.stop();
	}
	
	/**
	 * Initialises all the behaviors and returns them.
	 * When you implement a new Behavior you have to add it here
	 * for it to work on the robot
	 * 
	 * @return All behaviors in ascending order of priority 
	 */
	public abstract Behavior[] getAllBehaviors();

	
	public WorldState getWorldState() {
		return ws;
	}

	public Server getServer() {
		return s;
	}
	
	public RobotType getRobotType() {
		return type;
	}
	
}
