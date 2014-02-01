import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;


public class Kicker {
	static NXTRegulatedMotor kicker;
	
	public static void main(String[] args) throws InterruptedException {
	LCD.drawString("Kicker Version 1", 0, 0);
	while(true)
	{
		Button.ENTER.waitForPressAndRelease();
		Thread.sleep(1000);
		reset();
		
		Thread.sleep(1000);
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
	//method called to take the kicker back for the back swing.
	private static void reset() throws InterruptedException{
		kicker = Motor.A;
		kicker.setSpeed(120);
		kicker.setAcceleration(3000);
		kicker.rotate(20);
		kicker.stop();
		
	}
	
	
	}
	
