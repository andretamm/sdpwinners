package behavior;

import lejos.robotics.subsumption.Behavior;

public class AndreArbitrator {
	Behavior[] behaviors;
	
	public AndreArbitrator(Behavior[] behaviors) {
		this.behaviors = behaviors;
	}
	
	public void start() {
		while (true) {
			Behavior winner = arbitrate();
			
			if (winner != null) {
				winner.action();
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
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
