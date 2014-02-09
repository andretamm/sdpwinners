package behavior;

import java.util.ArrayList;
import java.util.List;

import common.Robot;

import ourcommunication.Server;
import sdp.vision.WorldState;
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
	private WorldState ws;
	private Robot r;
	private Server s;
	
	/**
	 * Initialises manager with a list of default behaviors 
	 */
	public Manager(WorldState ws, Robot r, Server s) {
		arbitrator = new Arbitrator(getAllBehaviors());
		this.ws = ws;
		this.r = r;
		this.s = s;
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
	 * @return All behaviors in ascending order of priority 
	 */
	private Behavior[] getAllBehaviors() {
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
		behaviorList[0] = new SimpleDefendGoal(ws, r, s);
		
//		return behaviorList.toArray(new Behavior[0]);
		return behaviorList;
	}
}
