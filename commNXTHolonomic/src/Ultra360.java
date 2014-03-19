import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;

/*              ** Forwards facing wheels **		*/
//			0x01 (I2C - Port 1) - EAST Wheel		//
//			0x07 (I2C - Port 4) - WEST Wheel		//

/*				** Side Ways Facing Wheels **		*/
//			0x03 (I2C - Port 2) -  SOUTH Wheel       //
//			0x05 (I2C - Port 3) - NORTH Wheel       //


public class Ultra360 {
	
	//Set up the I2C Board Connections and Ports. This is used for the wheels only.
	private I2CPort I2Cport = SensorPort.S1; 
	private I2CSensor I2Csensor = new I2CSensor(I2Cport, 0xB4, I2CPort.STANDARD_MODE, I2CSensor.TYPE_LOWSPEED_9V);
	
	//bytes to send to registers on the I2C boards
	private byte forward; 
	private byte backward; 
	private byte off;
	
	// Used to tune the maximum speed for the diagonal movement
	static double MAXIMUMSPEED = 90;
	
	//Actual robot speed
	public byte forwardSpeed;
	
	//Rotation speed for the wheels
	public byte fastRotationSpeed;
	public byte slowRotationSpeed;
	
	//Set Up the front part of the robot. This is used for the kicker part of the robot.
	private NXTRegulatedMotor rotator;
	private NXTRegulatedMotor kicker;
	private NXTRegulatedMotor grabber;
	
	/**
	 * Default constructor
	 */
	public Ultra360() {
		// Init direction bytes
		forward = (byte)1; 
		backward = (byte)2; 
		off = (byte)0;
		
		// Default rotating speed
		fastRotationSpeed = (byte) 50; //50 60 
		slowRotationSpeed = (byte) 50; 
		
		// Default moving speed
		forwardSpeed = (byte) 100; //70
		
		// Init motors
		rotator = Motor.C;
		kicker = Motor.B;
		grabber = Motor.A;
	}
	
	public void moveDiagonally(int angle){
		byte[] speeds = diagonalSpeeds(angle);
		//EAST Wheel
		System.out.println(speeds[0] + " " + speeds[1] + " EAST") ;
		I2Csensor.sendData(0x01,speeds[0]); 
		I2Csensor.sendData(0x02,speeds[1]); 
		//SOUTH Wheel
		System.out.println(speeds[2] + " " + speeds[3] + " SOUTH");
		I2Csensor.sendData(0x03,speeds[2]); 
		I2Csensor.sendData(0x04,speeds[3]);
		//NORTH Wheel
		System.out.println(speeds[4] + " " + speeds[5] + " NORTH");
		I2Csensor.sendData(0x05,speeds[4]); 
		I2Csensor.sendData(0x06,speeds[5]); 
		//WEST Wheel
		System.out.println(speeds[6] + " " + speeds[7] + " WEST");
		I2Csensor.sendData(0x07,speeds[6]); 
		I2Csensor.sendData(0x08,speeds[7]);
	}
	
	//The rotation function is depended on the rotation speed that is tested for a 360 degree turn.
	public void rotateTo(int degrees) throws InterruptedException{

		if(degrees < 0){
			backward = (byte) -backward;
			forward = (byte) -forward;
			degrees = -degrees;
		}
		long degToTime = (long) Math.rint(degrees*8.3); // Based on the current speed of rotation

		//EAST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,fastRotationSpeed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,fastRotationSpeed); 
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,fastRotationSpeed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,fastRotationSpeed); 
		//This function is made up out of a product of the time and speed.
		//This sleep is needed to get the rotation right.
		Thread.sleep(degToTime);
		//NOTE THAT THIS STOP IS NEEDED FOR THE ROBOT TO KNOW WHEN TO STOP AFTER THE GIVEN DEGREES
		stop();
	}
	
	//Rotate clockwise until Andre stops you
	public void rotateClockwise(byte speed) {
		//EAST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,speed); 
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,speed); 
		
	}
		
	//Rotate anti-clockwise until Andre stops you
	public void rotateAntiClockwise(byte speed) {
		//EAST Wheel
		I2Csensor.sendData(0x01,forward); 
		I2Csensor.sendData(0x02,speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,forward); 
		I2Csensor.sendData(0x08,speed); 
		//SOUTH Wheel
		I2Csensor.sendData(0x03,backward); 
		I2Csensor.sendData(0x04,speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,backward); 
		I2Csensor.sendData(0x06,speed); 
		
	}
	
	
	
	//Drives the robot forward at a given speed between 0 - 255
	public void forward(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,forward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,(byte) speed);
	}
	
	//Drives the robot backward at a given speed between 0 - 255
	public void backward(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,forward); 
		I2Csensor.sendData(0x08,(byte) speed);
	}

	//Drives the robot rightwards at a given speed between 0 - 255
	public void goRight(int speed){
		//SOUTH Wheel
		I2Csensor.sendData(0x03,backward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Drives the robot leftwards at a given speed between 0 - 255
	public void goLeft(int speed){
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,backward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Diagonally drives the robot North-Eastwards at a given speed between 0-255
	public void northWest(int speed){
		//EAST Wheel 
		I2Csensor.sendData(0x01,forward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,(byte) speed);
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,backward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Diagonally drives the robot South-Eastwards at a given speed between 0-255
	public void southWest(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,forward); 
		I2Csensor.sendData(0x08,(byte) speed);
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,backward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Diagonally drives the robot South-Westwards at a given speed between 0-255
	public void southEast(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,forward); 
		I2Csensor.sendData(0x08,(byte) speed);
		//SOUTH Wheel
		I2Csensor.sendData(0x03,backward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Diagonally drives the robot North-Westwards at a given speed between 0-255
	public void northEast(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,forward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,(byte) speed);
		//SOUTH Wheel
		I2Csensor.sendData(0x03,backward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//***NOTE***//
	//This is the method to call whenever you want to stop the robot in a SAFE manner.
	//For the best performance, call this method after any movement, before calling another direction method.
	public void stop(){
		//stop dead, I am not making a constant for this for added safety.
		I2Csensor.sendData(0x01,(byte)3); 
		I2Csensor.sendData(0x03,(byte)3); 
		I2Csensor.sendData(0x05,(byte)3); 
		I2Csensor.sendData(0x07,(byte)3); 
		//make the I2C safe again and idiot proof
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} //the best time I could get to make it stop faster
		I2Csensor.sendData(0x01,(byte)0);
		I2Csensor.sendData(0x02,(byte)0);
		I2Csensor.sendData(0x03,(byte)0);
		I2Csensor.sendData(0x04,(byte)0);
		I2Csensor.sendData(0x05,(byte)0);
		I2Csensor.sendData(0x06,(byte)0);
		I2Csensor.sendData(0x07,(byte)0);
		I2Csensor.sendData(0x08,(byte)0);
	}
	
	//if all else fails, here is the old way of stopping
	public void oldStop(){
		//stop dead, I am not making a constant for this for added safety.
		I2Csensor.sendData(0x01,(byte)3); 
		I2Csensor.sendData(0x03,(byte)3); 
		I2Csensor.sendData(0x05,(byte)3); 
		I2Csensor.sendData(0x07,(byte)3); 
		//make the I2C safe again and idiot proof
		I2Csensor.sendData(0x01,off); 
		I2Csensor.sendData(0x02,off);
		I2Csensor.sendData(0x03,off);
		I2Csensor.sendData(0x04,off); 
		I2Csensor.sendData(0x05,off); 
		I2Csensor.sendData(0x06,off); 
		I2Csensor.sendData(0x07,off); 
		I2Csensor.sendData(0x08,off); 
	}
	
	
	//****************************************************************//
	//THESE FOLLOWING METHODS RUNS ON THE KICKER PART OF THE MAX ROBOT//
	//****************************************************************//
	
	public void closeGrabber() {
		grabber.setSpeed(800);
                grabber.setAcceleration(10000);
                grabber.rotateTo(80);
	}
	
	public void openGrabber() {
		grabber.setSpeed(800);
		grabber.setAcceleration(10000);
		grabber.rotateTo(-10);
	}
	
	/**Kick the ball in a straight direction with the power given in from 0 - 100. 
	 * Optimal Speed for defender is 20.
	 * Optimal Speed for attacker is 100.
	 * After the kick is performed, the grabber will reset to the starting open position.
         * 
         * @param speed From 0 - 100 */
         
        public static void kick(int speed) {
		int kickSpeed = speed*10;
		int accel = speed*140;
                kicker.resetTachoCount();
                kicker.setSpeed(kickSpeed);
                kicker.setAcceleration(accel);
                grabber.setSpeed(800);
                grabber.setAcceleration(10000);
                grabber.rotateTo(0);
                kicker.rotate(-50);
                kicker.rotate(50);
        }
	
	//Rotates the rotator 25 degrees left and kicks the ball.
	public void kickLeft() {
		rotator.rotateTo(25); 
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		kick(100);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		aimReset();
		
	}
	
	//Rotates the rotator 25 degrees right and kicks the ball.
	public void kickRight() {
		rotator.rotateTo(-25); 
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		kick(100);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		aimReset();
	}
	
	//Rotate the front of the MaxBot
	public void aimRight() {
		rotator.rotateTo(-25); 		
	}
	public void aimLeft() {
		rotator.rotateTo(25); 		
	}
	
	//This is to reset the rotator angle and align it straight again.
	public void aimReset() {
		rotator.rotateTo(0);
	}


	//****************************************************************//
	//THESE FOLLOWING METHODS CALCULATE SPEED FOR DIAGONAL MOVEMENT
	//author Konstantin
	//****************************************************************//
	/*
	 * Given an angle in radians the method return an byte array consisting of
	 * the speeds and directions for all of the motors
	 */

	public static byte[] diagonalSpeeds(double mMoveDirection) {
		// Array that contains [EAST Direction, EAST Speed, SOUTH Direction, SOUTH Speed, NORTH Direction, NORTH Speed, WEST Direction, WEST Speed]
	    int EAST = 0;
		int NORTH = 1;
		int WEST = 2;
		int SOUTH = 3;
	
		
		byte[] speedAndDirection = new byte[8];
		
		for (int i = 0; i < 8; i++){
			speedAndDirection[i] = 0;
		}
		// Get the sin and cos of the movement direction
		mMoveDirection = Math.toRadians(mMoveDirection);

		final double cosDirection = Math.cos(mMoveDirection);
		final double sinDirection = Math.sin(mMoveDirection);

		//System.out.println("cosDirection = " + cosDirection);
		//System.out.println("sinDirection = " + sinDirection);

		double[] motorSpeed = new double[4];
		double maxSpeed = 0;

		for (int i = 0; i < 4; ++i) {
			double angle = Math.PI;

			/*
			 * 0 -> 0x01 - EAST 1 -> 0x03 - NORTH 2 -> 0x07 - WEST 3 -> 0x05 -> SOUTH
			 * 
			 */
			if (i == EAST) {
				motorSpeed[i] = 0;
				motorSpeed[i] += 0 * cosDirection + 1.0 * sinDirection;
			}

			if (i == NORTH) {
				motorSpeed[i] = 0;
				motorSpeed[i] += 1.0 * cosDirection + 0 * sinDirection;
			}

			if (i == WEST) {
				motorSpeed[i] = 0;
				motorSpeed[i] += 0 * cosDirection + 1.0 * sinDirection;
			}

			if (i == SOUTH) {
				motorSpeed[i] = 0;
				motorSpeed[i] += 1.0 * cosDirection +  0 * sinDirection;
			}
			
			maxSpeed = Math.max(Math.abs(motorSpeed[i]), maxSpeed);
		}

		for (int i = 0; i < motorSpeed.length; ++i) {
			motorSpeed[i] *= MAXIMUMSPEED / maxSpeed;
		}

		for (int i = 0; i < 4; ++i) {
			motorSpeed[i] = Math.min(MAXIMUMSPEED,
					(int) Math.round(motorSpeed[i]));
		}

		
		// 0x01 (I2C - Port 1) - EAST Wheel
		speedAndDirection[1] =  (byte)Math.abs(motorSpeed[0]);
		speedAndDirection[0] =  (byte)returnDirection(motorSpeed[0], EAST);
		
		// 0x03 (I2C - Port 2) - SOUTH Wheel
		speedAndDirection[3] =  (byte)Math.abs(motorSpeed[3]);
		speedAndDirection[2] =  (byte)returnDirection(motorSpeed[3], SOUTH);
		
		// 0x05 (I2C - Port 3) - NORTH Wheel
		speedAndDirection[5] =  (byte)Math.abs(motorSpeed[1]);
		speedAndDirection[4] =  (byte)returnDirection(motorSpeed[1], NORTH);
		
		// 0x07 (I2C - Port 4) - WEST Wheel
		speedAndDirection[7] =  (byte)Math.abs(motorSpeed[2]);
		speedAndDirection[6] =  (byte)returnDirection(motorSpeed[2], WEST);

	return speedAndDirection;

	}

	public static int returnDirection(double motorSpeed, int motor) {
		// Array that contains [EAST Direction, EAST Speed, SOUTH Direction, SOUTH Speed, NORTH Direction, NORTH Speed, WEST Direction, WEST Speed]
		int EAST = 0;
		int SOUTH = 1;
		int NORTH = 2;
		int WEST = 3;
		
		int forward = (byte) 1;
		int backward = (byte) 2;
		int off = (byte) 0;
		
		int direction = off;

		if (motorSpeed > 0) {
			if (motor == EAST) {
				direction = forward;
			}

			if (motor == NORTH) {
				direction = backward;
			}

			if (motor == WEST) {
				direction = backward;
			}

			if (motor == SOUTH) {
				direction = forward;
			}
		} else if (motorSpeed < 0) {
			if (motor == EAST) {
				direction = backward;
			}

			if (motor == NORTH) {
				direction = forward;
			}

			if (motor == WEST) {
				direction = forward;
			}

			if (motor == SOUTH) {
				direction = backward;
			}
		}

		return direction;
	}
}
