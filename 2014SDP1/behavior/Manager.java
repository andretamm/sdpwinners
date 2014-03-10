package behavior;

import constants.RobotType;
import ourcommunication.Server;
import vision.WorldState;
import lejos.robotics.subsumption.Behavior;

/**
 * Manages all the different behaviors and the arbitrator which picks which
 * behavior to use. 
 * 
 * @author Andre
 *
 */
public class Manager {
	
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
	public Behavior[] getAllBehaviors() {
		// TODO - all the commented out code here should work once we
		// start running this on the PC. leJOS doesn't seem to like
		// converting ArrayLists to arrays, so doing this by hand
		
//		ArrayList<Behavior> behaviorList = new ArrayList<Behavior>();
		
		int numOfBehaviors = 1;
		Behavior[] behaviorList = new Behavior[numOfBehaviors];
		
		// Add behaviors in ascending order of priority
//		behaviorList.add(new Milestone1());
//		behaviorList.add(new Milestone2());
//		behaviorList[0] = new Milestone1();
//		behaviorList[1] = new Milestone2();
		behaviorList[0] = new SimpleDefendGoal(ws, type, s);
		
//		return behaviorList.toArray(new Behavior[0]);
		return behaviorList;
	}

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
