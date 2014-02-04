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
	
	
	
	
	
	public void fail() {
		Sound.beep();
	}
	
	public void close() {
		Sound.beep();
	}
}
