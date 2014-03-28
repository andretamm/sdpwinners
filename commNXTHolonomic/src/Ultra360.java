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
	static double MAXIMUMSPEED = 70;
	
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
		forwardSpeed = (byte) 90; //70
		
		// Init motors
		rotator = Motor.C;
		kicker = Motor.B;
		grabber = Motor.A;
	}
	
	public void moveDiagonally(int angle){
		byte[] speeds = diagonalSpeeds(angle);
		byte correction = 60;
		//EAST Wheel
		System.out.println(speeds[0] + " " + speeds[1] + " EAST") ;
		I2Csensor.sendData(0x01,speeds[0]); 
		I2Csensor.sendData(0x02,(byte)(speeds[1] + correction)); 
		//SOUTH Wheel
		System.out.println(speeds[2] + " " + speeds[3] + " SOUTH");
		I2Csensor.sendData(0x03,speeds[2]); 
		I2Csensor.sendData(0x04,(byte)(speeds[3] + correction));
		//NORTH Wheel
		System.out.println(speeds[4] + " " + speeds[5] + " NORTH");
		I2Csensor.sendData(0x05,speeds[4]); 
		I2Csensor.sendData(0x06,(byte)(speeds[5] + correction)); 
		//WEST Wheel
		System.out.println(speeds[6] + " " + speeds[7] + " WEST");
		I2Csensor.sendData(0x07,speeds[6]); 
		I2Csensor.sendData(0x08,(byte)(speeds[7] + correction));
	}
	
	/**This function takes a number of degrees to rotate, as I have made it from scratch,
	  * it uses time to know how long it must rotate, according to the speed of the wheels
	  *
	  *@param degrees Takes an integer from -360 to 360.
	  */
	public void rotateTo(int degrees){
		
		byte rotateSpeed = (byte) 200; //Changing this will fuck up the function by the way
		long degToTime = (long) Math.rint(Math.abs(degrees)*3.5); // Based on the current speed of rotation
		
		if(degrees > 0){
			//EAST Wheel
			I2Csensor.sendData(0x01,backward); 
			I2Csensor.sendData(0x02,rotateSpeed); 
			//WEST Wheel
			I2Csensor.sendData(0x07,backward); 
			I2Csensor.sendData(0x08,rotateSpeed); 
			//SOUTH Wheel
			I2Csensor.sendData(0x03,forward); 
			I2Csensor.sendData(0x04,rotateSpeed); 
			//NORTH Wheel
			I2Csensor.sendData(0x05,forward); 
			I2Csensor.sendData(0x06,rotateSpeed); 
			try {
				Thread.sleep(degToTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stop();
		}
		else if(degrees <= 0){
			//EAST Wheel
			I2Csensor.sendData(0x01,forward); 
			I2Csensor.sendData(0x02,rotateSpeed); 
			//WEST Wheel
			I2Csensor.sendData(0x07,forward); 
			I2Csensor.sendData(0x08,rotateSpeed); 
			//SOUTH Wheel
			I2Csensor.sendData(0x03,backward); 
			I2Csensor.sendData(0x04,rotateSpeed); 
			//NORTH Wheel
			I2Csensor.sendData(0x05,backward); 
			I2Csensor.sendData(0x06,rotateSpeed); 
			try {
				Thread.sleep(degToTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stop();
		}
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
	public void oldStop() {
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
		grabber.rotateTo(-10, true); 
	}
	
	/**
	 * Kick the ball in a straight direction with the power given in from 0 - 100. 
	 * Optimal Speed for defender is 20.
	 * Optimal Speed for attacker is 100.
	 * After the kick is performed, the grabber will reset to the starting open position.
     * 
     * @param speed From 0 - 100 */
    public void kick(int speed) {
		int kickSpeed = speed*10;
		int accel = speed*140;
        kicker.resetTachoCount();
        kicker.setSpeed(kickSpeed);
        kicker.setAcceleration(accel);
        grabber.setSpeed(800);
        grabber.setAcceleration(10000);
        grabber.rotateTo(0, true);
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
		rotator.rotateTo(-25, true); 		
	}
	public void aimLeft() {
		rotator.rotateTo(25, true); 		
	}
	
	//This is to reset the rotator angle and align it straight again.
	public void aimReset() {
		rotator.rotateTo(0, true);
	}


	//****************************************************************//
	//THESE FOLLOWING METHODS CALCULATE SPEED FOR DIAGONAL MOVEMENT
	//author Konstantin
	//****************************************************************//
	/*
	 * Given an angle in radians or degrees the method return an byte array consisting of
	 * the speeds and directions for all of the motors.
	 * 
	 * @return array - [EAST direction, EAST speed, SOUTH direction, SOUTH speed, NORTH direction, NORTH speed, WEST direction, WEST speed]
	 */
	
	public static byte[] diagonalSpeeds(double mMoveDirection) {
		/* Scale the maximum speed in relation to sine and cosine, so the 
		 * resultant force from the wheels drives the robot in the direction
		 * we want.
		 */
	    
		/* 
		 * Indexes used for the speedAndDirection array.
		 */
		int EAST = 0;
		int NORTH = 1;
		int WEST = 2;
		int SOUTH = 3;
	
		/* 
		 * This array will hold the calculated speeds and directions for the different wheels
		 */
		byte[] speedAndDirection = new byte[8];
		
		/* Initialize the array with zeros */
		for (int i = 0; i < 8; i++){
			speedAndDirection[i] = 0;
		}
		
		/*
		 *  Get the sin and cos of the movement direction
		 */
		mMoveDirection = Math.toRadians(mMoveDirection);

		final double cosDirection = Math.cos(mMoveDirection);
		final double sinDirection = Math.sin(mMoveDirection);

		/*  North and South provide force in the x-axis.
		 *  East and West provide force in the y-axis.
		 *  
		 *  Therefore the speed of the North and the South will be scaled
		 *  to the values of the cosine, and for the other two, they will 
		 *  be scaled to the sine of the angle */
		
		/* Here we set the values of the corresponding wheels to the sine or cosine */
		
		double[] motorSpeed = new double[4];
		motorSpeed[EAST]  = sinDirection;
		motorSpeed[NORTH] = cosDirection;
		motorSpeed[WEST]  = sinDirection;
		motorSpeed[SOUTH] = cosDirection;
		
		/* Check which wheels will be moving faster */
		double maxSpeed = Math.max(Math.abs(cosDirection), Math.abs(sinDirection));


		/* This calculates the actual speed value send to the speed register.
		 * By dividing by the maxSpeed we ensure that two of the wheels
		 * with the highest weight from cos/sin will be running at the max speed.
		 */
		for (int i = 0; i < motorSpeed.length; ++i) {
			motorSpeed[i] *= MAXIMUMSPEED / maxSpeed;
		}

		/* Set the speed for each to the minimum between the MAX actual speed and calculated one */
		for (int i = 0; i < 4; ++i) {
			motorSpeed[i] = Math.min(MAXIMUMSPEED,
					(int) Math.round(motorSpeed[i]));
		}

		
		/* This sets the speeds and direction in the array that is returned,
		 * the method returnDirection takes a speed and a wheel direction and
		 * returns the direction wheel need to move. Because I2C uses a direction
		 * we pass the speed as an absolute value. */
		
		// 0x01 (I2C - Port 1) - EAST Wheel
		speedAndDirection[1] =  (byte)Math.abs(motorSpeed[EAST]);
		speedAndDirection[0] =  (byte)returnDirection(motorSpeed[EAST], EAST);
		
		// 0x03 (I2C - Port 2) - SOUTH Wheel
		speedAndDirection[3] =  (byte)Math.abs(motorSpeed[SOUTH]);
		speedAndDirection[2] =  (byte)returnDirection(motorSpeed[SOUTH], SOUTH);
		
		// 0x05 (I2C - Port 3) - NORTH Wheel
		speedAndDirection[5] =  (byte)Math.abs(motorSpeed[NORTH]);
		speedAndDirection[4] =  (byte)returnDirection(motorSpeed[NORTH], NORTH);
		
		// 0x07 (I2C - Port 4) - WEST Wheel
		speedAndDirection[7] =  (byte)Math.abs(motorSpeed[WEST]);
		speedAndDirection[6] =  (byte)returnDirection(motorSpeed[WEST], WEST);

	return speedAndDirection;

	}


	/* 
	 * This method sets the direction in relation to the wheels 
	 * direction, and the speed. The values are specific for our
	 * wheel, because our wheels directions are scrambled.
	 */
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
