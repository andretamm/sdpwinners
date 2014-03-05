import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.MirrorMotor;
import lejos.robotics.navigation.DifferentialPilot;


public class RobotController {

	Ultra360 ULTRA;
	
	public volatile int previousCommand = 1;
	public volatile int command = 3;
	public volatile int previousAngle = 0;
	public volatile int angle = 0;

	
	public RobotController() {
		ULTRA = new Ultra360();
	}
	
	public void goDiagonally(int angle){
		// Go diagonally at an given angle
		ULTRA.moveDiagonally(angle);
	}
	
	public void kick() {
		// Wait for the ball in the grabber to stabilise
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		ULTRA.kick();
	}
	
	public void forward() {
//		System.out.println("Going forward");
		ULTRA.stop();
		ULTRA.forward(ULTRA.speed);
	}
	
	public void backward() {
//		System.out.println("Going backward");
		ULTRA.stop();
		ULTRA.backward(ULTRA.speed);
	}
	
	public void turnCW() {
//		System.out.println("Turning clockwise");
		ULTRA.stop();
		ULTRA.rotateClockwise();
	}
	
	public void turnCCW() {
//		System.out.println("Turning counterclockwise");
		ULTRA.stop();
		ULTRA.rotateAntiClockwise();
	}
	
	public void chill() {
//		System.out.println("Chilling in place");
		ULTRA.stop();
	}
	
	public void fail() {
		Sound.beep();
	}
	
	public void close() {
		Sound.beep();
	}

	public void grab() {
		ULTRA.closeGrabber();
	}
	
	public void openGrabber() {
		ULTRA.openGrabber();
	}

	public void goLeft() {
		ULTRA.stop();
		ULTRA.goLeft(ULTRA.speed);
	}

	public void goRight() {
		ULTRA.stop();
		ULTRA.goRight(ULTRA.speed);
	}

	public void kickLeft() {
		ULTRA.kickLeft();
	}

	public void kickRight() {
		ULTRA.kickRight();
	}

	public void aimLeft() {
		ULTRA.aimLeft();
		
	}

	public void aimRight() {
		ULTRA.aimRight();
	}

	public void aimReset() {
		ULTRA.aimReset();
		
	}
}
