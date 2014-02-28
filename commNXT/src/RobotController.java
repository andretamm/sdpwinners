import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.MirrorMotor;
import lejos.robotics.navigation.DifferentialPilot;


public class RobotController {

	NXTRegulatedMotor kicker;
	DifferentialPilot pilot;
	
	public volatile int previousCommand = 1;
	public volatile int command = 3;
	
	public RobotController() {
		kicker = Motor.C;
		pilot = new DifferentialPilot(56, 112, Motor.A, Motor.B);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		System.out.println(pilot.getMaxTravelSpeed());
	}
	
	// TODO
	// remove this? :))))
	public void init() {
		kicker = Motor.C;
		pilot = new DifferentialPilot(56, 112, Motor.A, Motor.B);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
	}
	
	public void kick() {
		kicker.setSpeed(6500);
		kicker.rotate(-78);
		kicker.rotate(78);
	}
	
	public void forward() {
//		System.out.println("Going forward");
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		pilot.forward();
	}
	
	public void backward() {
//		System.out.println("Going backward");
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		pilot.backward();
	}
	
	public void turnCW() {
//		System.out.println("Turning clockwise");
		pilot.setRotateSpeed(45);
		pilot.rotateRight();
	}
	
	public void turnCCW() {
//		System.out.println("Turning counterclockwise");
		pilot.setRotateSpeed(45);
		pilot.rotateLeft();
	}
	
	public void chill() {
//		System.out.println("Chilling in place");
		// Use higher acceleration for stopping
		pilot.setAcceleration(4000);
		
		// Stop
		pilot.stop();
		
		// Set back to default
		pilot.setAcceleration((int) (pilot.getMaxTravelSpeed() * 4));
	}
	
	public void fail() {
		Sound.beep();
	}
	
	public void close() {
		Sound.beep();
	}
}
