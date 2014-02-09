import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;


public class Robot {

	NXTRegulatedMotor kicker;
	
	public void init() {
		kicker = Motor.A;
	}
	
	public void kick() {
		kicker.setSpeed(6500);
		kicker.rotate(-78);
		kicker.rotate(78);
	}
	
	public void forward() {
		System.out.println("Going forward");
	}
	
	public void backward() {
		System.out.println("Going backward");
	}
	
	public void turnCW() {
		System.out.println("Turning clockwise");
	}
	
	public void turnCCW() {
		System.out.println("Turning counterclockwise");
	}
	
	public void chill() {
		System.out.println("Chilling in place");
	}
	
	public void fail() {
		Sound.beep();
	}
	
	public void close() {
		Sound.beep();
	}
}
