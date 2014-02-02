package behavior;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Behavior;

/**
 * Behavior for the second milestone - kicks the ball placed
 * in front of the robot.
 */
public class Milestone2 implements Behavior {
	boolean isActive = false;
	static NXTRegulatedMotor kicker;

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		isActive = true;
		
		LCD.drawString("Kicker Version 1", 0, 0);
		while(isActive)
		{
			Button.ENTER.waitForPressAndRelease();
			//Plays a little beep before it kicks the ball.
			
			try {
				Thread.sleep(1000);
				reset();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("Robot sleep interrupted");
				e.printStackTrace();
			}
			
			Sound.beep();
			
			//for the forward motion of the kicker
			kicker.setSpeed(150);
			kicker.setAcceleration(6000);
			kicker.rotate(-50);
			kicker.stop();
			
			//takes the kicker back to 0 degrees
			kicker.rotate(30);
			kicker.stop();
			kicker.stop();	
		}
	}
	
	/**
	 * Moves kicker back for the back swing.
	 * 
	 * @throws InterruptedException
	 */
	private static void reset() {
		kicker = Motor.A;
		kicker.setSpeed(120);
		kicker.setAcceleration(3000);
		kicker.rotate(20);
		kicker.stop();
	}

	@Override
	public void suppress() {
		isActive = false;
	}

}
