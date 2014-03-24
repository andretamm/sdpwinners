package behavior;

import lejos.robotics.subsumption.Behavior;

/**
 * Arbitrator that picks which behavior to use and runs the winner. Gets a 
 * list of behaviors from the Behavior manager and keeps on picking
 * Behaviors until it is told to stop. 
 * @author Andre
 *
 */
public class AndreArbitrator {
	private Behavior[] behaviors;
	private boolean active;
	
	public AndreArbitrator(Behavior[] behaviors) {
		this.behaviors = behaviors;
	}
	
	/**
	 * Start picking and running behaviors
	 */
	public void start() {
		active = true;
		
		while (active) {
			Behavior winner = arbitrate();
			
			if (winner != null) {
				winner.action();
			} else {
				System.err.println("ERROR - couldn't pick a behavior, no conditions were met!!");
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stop running any more behaviors
	 */
	public void stop() {
		active = false;
	}
	
	/**
	 * Picks the Behavior to run. 
	 * @return The Behavior that should be executed
	 */
	private Behavior arbitrate() {
		// Loop through all behaviors, starting with the highest priority one
		for (int i = behaviors.length - 1; i >= 0; i--) {
			if(behaviors[i].takeControl()) {
				// Found behavior that can take control
				return behaviors[i];
			}
		}
		
		// No behavior found
		return null;
	}
}
