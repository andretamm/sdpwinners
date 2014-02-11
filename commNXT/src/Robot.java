import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.MirrorMotor;
import lejos.robotics.navigation.DifferentialPilot;


public class Robot {

	NXTRegulatedMotor kicker;
	DifferentialPilot pilot;
	
	public void init() {
		kicker = Motor.C;
		pilot = new DifferentialPilot(56, 112, Motor.A, MirrorMotor.invertMotor(Motor.B));
		pilot.setTravelSpeed(20);
	}
	
	public void kick() {
		kicker.setSpeed(6500);
		kicker.rotate(-78);
		kicker.rotate(78);
	}
	
	public void forward() {
		System.out.println("Going forward");
		pilot.forward();
	}
	
	public void backward() {
		System.out.println("Going backward");
		pilot.backward();
	}
	
	public void turnCW() {
		System.out.println("Turning clockwise");
		pilot.rotateRight();
	}
	
	public void turnCCW() {
		System.out.println("Turning counterclockwise");
		pilot.rotateLeft();
	}
	
	public void chill() {
		System.out.println("Chilling in place");
		pilot.stop();
	}
	
	public void fail() {
		Sound.beep();
	}
	
	public void close() {
		Sound.beep();
	}
}
