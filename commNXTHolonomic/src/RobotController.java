import lejos.nxt.Sound;


public class RobotController {

	Ultra360 ULTRA;
	
	public volatile int previousCommand = 1;
	public volatile int command = 3;
	public volatile int previousAngle = 0;
	public volatile int angle = 0;

	public volatile int rotateAngle = 0;
	
	public volatile long commandTime = 0;
	public volatile long previousCommandTime = 0;

	
	public RobotController() {
		ULTRA = new Ultra360();
	}
	
	public void goDiagonally(int angle) {
		ULTRA.stop();
		
		// Go diagonally at an given angle
		ULTRA.diagonalMaxSpeed = 220;
		ULTRA.moveDiagonally(angle);
	}
	
	public void goDiagonallySlow(int angle) {
		ULTRA.stop();
		
		// Go diagonally at an given angle
		ULTRA.diagonalMaxSpeed = 100;
		ULTRA.moveDiagonally(angle);
	}
	
	public void fastKick() {
		ULTRA.kick(100);
	}
	
	public void slowKick() {
		// Wait for the ball in the grabber to stabilise
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		ULTRA.kick(100);
	}
	
	public void forward() {
//		System.out.println("Going forward");
		ULTRA.stop();
		ULTRA.forward(ULTRA.forwardSpeed);
	}
	
	public void fastForward() {
		ULTRA.forward(ULTRA.forwardSpeedFast);
	}
	
	public void fastBackward() {
		ULTRA.backward(ULTRA.forwardSpeedFast);
	}
	
	public void backward() {
//		System.out.println("Going backward");
		ULTRA.stop();
		ULTRA.backward(ULTRA.forwardSpeed);
	}
	
	public void turnCW() {
//		System.out.println("Turning clockwise");
		ULTRA.stop();
		ULTRA.rotateClockwise(ULTRA.fastRotationSpeed);
	}
	
	public void turnCCW() {
//		System.out.println("Turning counterclockwise");
		ULTRA.stop();
		ULTRA.rotateAntiClockwise(ULTRA.fastRotationSpeed);
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
		ULTRA.goLeft(ULTRA.forwardSpeed);
	}

	public void goRight() {
		ULTRA.stop();
		ULTRA.goRight(ULTRA.forwardSpeed);
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

	public void slowCW() {
		ULTRA.rotateClockwise(ULTRA.slowRotationSpeed);
	}

	public void slowCCW() {
		ULTRA.rotateAntiClockwise(ULTRA.slowRotationSpeed);
	}

	public void rotateTo(int rotateAngle) {
		ULTRA.rotateTo(rotateAngle);
	}

}
