import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;

/*              ** Forwards facing wheels **		*/
//			0x01 (I2C - Port 1) - EAST Wheel		//
//			0x07 (I2C - Port 4) - WEST Wheel		//

/*				** Side Ways Facing Wheels **		*/
//			0x03 (I2C - Port 2) - SOUTH Wheel       //
//			0x05 (I2C - Port 3) - NORTH Wheel       //


public class Ultra360 {

	//Set up the I2C Board Connections and Ports. This is used for the wheels only.
	static I2CPort I2Cport = SensorPort.S1; 
	static I2CSensor I2Csensor =new I2CSensor(I2Cport, 0xB4, I2CPort.STANDARD_MODE, I2CSensor.TYPE_LOWSPEED_9V);
	
	//bytes to send to registers on the I2C boards
	static byte forward; 
	static byte backward; 
	static byte off; 
	
	//Actual robot speed
	static byte speed;
	//Rotation speed for the wheels
	static byte rotationSpeed;
	
	//Set Up the front part of the robot. This is used for the kicker part of the robot.
	static NXTRegulatedMotor rotator;
	static NXTRegulatedMotor kicker;
	static NXTRegulatedMotor grabber;
	
	
	//Creates the bytes needed to send directions 
	forward = (byte)1; 
	backward = (byte)2; 
	off = (byte)0;
	rotationSpeed = (byte)70; 
		
	
	
	
	//The rotation function is depended on the rotation speed that is tested for a 360 degree turn.
	public static void rotateTo(int degrees) throws InterruptedException{

		if(degrees < 0){
			backward = (byte) -backward;
			forward = (byte) -forward;
			degrees = -degrees;
		}
		long degToTime = (long) Math.rint(degrees*8.3); // Based on the current speed of rotation

		//WEST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,rotationSpeed); 
		//EAST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,rotationSpeed); 
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,rotationSpeed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,rotationSpeed); 
		//This function is made up out of a product of the time and speed.
		//This sleep is needed to get the rotation right.
		Thread.sleep(degToTime);
		//NOTE THAT THIS STOP IS NEEDED FOR THE ROBOT TO KNOW WHEN TO STOP AFTER THE GIVEN DEGREES
		stop();
	}
	
	//Rotate clockwise until Andre stops you
		public static void rotateClockwise() {
			//EAST Wheel
			I2Csensor.sendData(0x01,backward); 
			I2Csensor.sendData(0x02,rotationSpeed); 
			//WEST Wheel
			I2Csensor.sendData(0x07,backward); 
			I2Csensor.sendData(0x08,rotationSpeed); 
			//SOUTH Wheel
			I2Csensor.sendData(0x03,forward); 
			I2Csensor.sendData(0x04,rotationSpeed); 
			//NORTH Wheel
			I2Csensor.sendData(0x05,forward); 
			I2Csensor.sendData(0x06,rotationSpeed); 
			
		}
		
		//Rotate anti-clockwise until Andre stops you
				public static void rotateAniClockwise() {
					//EAST Wheel
					I2Csensor.sendData(0x01,forward); 
					I2Csensor.sendData(0x02,rotationSpeed); 
					//WEST Wheel
					I2Csensor.sendData(0x07,forward); 
					I2Csensor.sendData(0x08,rotationSpeed); 
					//NORTH Wheel
					I2Csensor.sendData(0x03,backward); 
					I2Csensor.sendData(0x04,rotationSpeed); 
					//SOUTH Wheel
					I2Csensor.sendData(0x05,backward); 
					I2Csensor.sendData(0x06,rotationSpeed); 
					
				}
	
	
	
	//Drives the robot forward at a given speed between 0 - 255
	public static void forward(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,forward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,(byte) speed);
	}
	
	//Drives the robot backward at a given speed between 0 - 255
	public static void backward(int speed){
		//EAST Wheel
		I2Csensor.sendData(0x01,backward); 
		I2Csensor.sendData(0x02,(byte) speed); 
		//WEST Wheel
		I2Csensor.sendData(0x07,forward); 
		I2Csensor.sendData(0x08,(byte) speed);
	}

	//Drives the robot leftwards at a given speed between 0 - 255
	public static void goLeft(int speed){
		//SOUTH Wheel
		I2Csensor.sendData(0x03,backward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,forward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Drives the robot rightwards at a given speed between 0 - 255
	public static void goRight(int speed){
		//SOUTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,(byte) speed); 
		//NORTH Wheel
		I2Csensor.sendData(0x05,backward); 
		I2Csensor.sendData(0x06,(byte) speed);
	}
	
	//Diagonally drives the robot North-Eastwards at a given speed between 0-255
	public static void northWest(int speed){
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
	public static void southWest(int speed){
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
	public static void southEast(int speed){
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
	public static void northEast(int speed){
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
	public static void stop(){
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
	
	//Close the grabber when the ball is with in reach
	public static void closeGrabber() {
		grabber.setSpeed(800);
		grabber.setAcceleration(10000);
		grabber.rotateTo(20);
	}
	
	//Kick the ball in a straight direction. This will reset the grabber to open.
	public static void kick() {
		kicker.resetTachoCount();
		kicker.setSpeed(1000);
		grabber.setSpeed(800);
		grabber.setAcceleration(10000);
		grabber.rotateTo(0);
		kicker.rotate(-90);
		kicker.rotate(90);
	}
	
	//Rotates the rotator 25 degrees left and kicks the ball.
	public static void kickRight() {
		rotator.rotateTo(25); 
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		kick();
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		aimReset();
		
	}
	
	//Rotates the rotator 25 degrees right and kicks the ball.
	public static void kickLeft() {
		rotator.rotateTo(-25); 
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		kick();
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		aimReset();
	}
	
	//This is to reset the rotator angle and align it straight again.
	public static void aimReset() {
		rotator.rotateTo(3);
	}
	
	
	
	
	
	
			


}