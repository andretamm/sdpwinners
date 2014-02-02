package behavior;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

/**
 * Manages all the different behaviors and the arbitrator which picks which
 * behavior to use. 
 * 
 * @author Andre
 *
 */
public class Manager {
	
	private Arbitrator arbitrator;
	
	/**
	 * Initialises manager with a list of default behaviors 
	 */
	public Manager() {
		arbitrator = new Arbitrator(getAllBehaviors());
	}
	
	/**
	 * Starts picking and running behaviors on a new thread
	 */
	public void start() {
		Thread arbitratorThread = new Thread() {
			@Override
			public void run() {
				super.run();
				arbitrator.start();
				System.out.println("Manager started");
			}
		};
		
		arbitratorThread.run();
	}
	
	/**
	 * Initialises all the behaviors and returns them.
	 * When you implement a new Behavior you have to add it here
	 * for it to work on the robot
	 * 
	 * @return All behaviors in descending order of priority 
	 */
	private Behavior[] getAllBehaviors() {
		List<Behavior> behaviorList = new ArrayList<Behavior>();
		
		// Add behaviors in descending order of priority
		behaviorList.add(new Milestone1());
		behaviorList.add(new Milestone2());
		
		return behaviorList.toArray(new Behavior[0]);
	}
	
}
