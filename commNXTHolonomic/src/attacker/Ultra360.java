package attacker;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

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
	public double diagonalMaxSpeed = 180; // This is changed in RobotController to tune the speed
										  // for a slow/fast diagonal movement
	
	//Actual robot speed
	public byte forwardSpeed;
	
	// Fast forward speed
	public byte forwardSpeedFast = (byte) 240;
	
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
		fastRotationSpeed = (byte) 42; //50 60 
		slowRotationSpeed = (byte) 42; // 45 is okay for attacker, 49 for defender 
		
		// Default moving speed
		forwardSpeed = (byte) 60; // 70 is too much for full batteries, 
								  // but is optimal for slightly used ones
								  // 60 is okay for full ones
		
		// Init motors
		rotator = Motor.C;
		kicker = Motor.B;
		grabber = Motor.A;
		
		rotator.resetTachoCount();
	}
	
	public static int EAST = 0;
	public static int SOUTH = 1;
	public static int NORTH = 2;
	public static int WEST = 3;
	
	public static double[] sinLookup = {0, 0, 0, 0.0523359562429, 0.0697564737441, 0.0871557427477, 0.104528463268, 0.121869343405, 0.13917310096, 0.15643446504, 0.173648177667, 0.190808995377, 0.207911690818, 0.224951054344, 0.2419218956, 0.258819045103, 0.275637355817, 0.292371704723, 0.309016994375, 0.325568154457, 0.342020143326, 0.358367949545, 0.374606593416, 0.390731128489, 0.406736643076, 0.422618261741, 0.438371146789, 0.45399049974, 0.469471562786, 0.484809620246, 0.5, 0.51503807491, 0.529919264233, 0.544639035015, 0.559192903471, 0.573576436351, 0.587785252292, 0.601815023152, 0.615661475326, 0.62932039105, 0.642787609687, 0.656059028991, 0.669130606359, 0.681998360062, 0.694658370459, 0.707106781187, 0.719339800339, 0.731353701619, 0.743144825477, 0.754709580223, 0.766044443119, 0.777145961457, 0.788010753607, 0.798635510047, 0.809016994375, 0.819152044289, 0.829037572555, 0.838670567945, 0.848048096156, 0.857167300702, 0.866025403784, 0.874619707139, 0.882947592859, 0.891006524188, 0.898794046299, 0.906307787037, 0.913545457643, 0.920504853452, 0.927183854567, 0.933580426497, 0.939692620786, 0.945518575599, 0.951056516295, 0.956304755963, 0.961261695938, 0.965925826289, 0.970295726276, 0.974370064785, 0.978147600734, 0.981627183448, 0.984807753012, 0.987688340595, 0.990268068742, 0.992546151641, 0.994521895368, 0.996194698092, 0.99756405026, 0.998629534755, 0.999390827019, 0.999847695156, 1.0, 0.999847695156, 0.999390827019, 0.998629534755, 0.99756405026, 0.996194698092, 0.994521895368, 0.992546151641, 0.990268068742, 0.987688340595, 0.984807753012, 0.981627183448, 0.978147600734, 0.974370064785, 0.970295726276, 0.965925826289, 0.961261695938, 0.956304755963, 0.951056516295, 0.945518575599, 0.939692620786, 0.933580426497, 0.927183854567, 0.920504853452, 0.913545457643, 0.906307787037, 0.898794046299, 0.891006524188, 0.882947592859, 0.874619707139, 0.866025403784, 0.857167300702, 0.848048096156, 0.838670567945, 0.829037572555, 0.819152044289, 0.809016994375, 0.798635510047, 0.788010753607, 0.777145961457, 0.766044443119, 0.754709580223, 0.743144825477, 0.731353701619, 0.719339800339, 0.707106781187, 0.694658370459, 0.681998360062, 0.669130606359, 0.656059028991, 0.642787609687, 0.62932039105, 0.615661475326, 0.601815023152, 0.587785252292, 0.573576436351, 0.559192903471, 0.544639035015, 0.529919264233, 0.51503807491, 0.5, 0.484809620246, 0.469471562786, 0.45399049974, 0.438371146789, 0.422618261741, 0.406736643076, 0.390731128489, 0.374606593416, 0.358367949545, 0.342020143326, 0.325568154457, 0.309016994375, 0.292371704723, 0.275637355817, 0.258819045103, 0.2419218956, 0.224951054344, 0.207911690818, 0.190808995377, 0.173648177667, 0.15643446504, 0.13917310096, 0.121869343405, 0.104528463268, 0.0871557427477, 0.0697564737441, 0.0523359562429, 0, 0, 0, 0, 0, -0.0523359562429, -0.0697564737441, -0.0871557427477, -0.104528463268, -0.121869343405, -0.13917310096, -0.15643446504, -0.173648177667, -0.190808995377, -0.207911690818, -0.224951054344, -0.2419218956, -0.258819045103, -0.275637355817, -0.292371704723, -0.309016994375, -0.325568154457, -0.342020143326, -0.358367949545, -0.374606593416, -0.390731128489, -0.406736643076, -0.422618261741, -0.438371146789, -0.45399049974, -0.469471562786, -0.484809620246, -0.5, -0.51503807491, -0.529919264233, -0.544639035015, -0.559192903471, -0.573576436351, -0.587785252292, -0.601815023152, -0.615661475326, -0.62932039105, -0.642787609687, -0.656059028991, -0.669130606359, -0.681998360062, -0.694658370459, -0.707106781187, -0.719339800339, -0.731353701619, -0.743144825477, -0.754709580223, -0.766044443119, -0.777145961457, -0.788010753607, -0.798635510047, -0.809016994375, -0.819152044289, -0.829037572555, -0.838670567945, -0.848048096156, -0.857167300702, -0.866025403784, -0.874619707139, -0.882947592859, -0.891006524188, -0.898794046299, -0.906307787037, -0.913545457643, -0.920504853452, -0.927183854567, -0.933580426497, -0.939692620786, -0.945518575599, -0.951056516295, -0.956304755963, -0.961261695938, -0.965925826289, -0.970295726276, -0.974370064785, -0.978147600734, -0.981627183448, -0.984807753012, -0.987688340595, -0.990268068742, -0.992546151641, -0.994521895368, -0.996194698092, -0.99756405026, -0.998629534755, -0.999390827019, -0.999847695156, -1.0, -0.999847695156, -0.999390827019, -0.998629534755, -0.99756405026, -0.996194698092, -0.994521895368, -0.992546151641, -0.990268068742, -0.987688340595, -0.984807753012, -0.981627183448, -0.978147600734, -0.974370064785, -0.970295726276, -0.965925826289, -0.961261695938, -0.956304755963, -0.951056516295, -0.945518575599, -0.939692620786, -0.933580426497, -0.927183854567, -0.920504853452, -0.913545457643, -0.906307787037, -0.898794046299, -0.891006524188, -0.882947592859, -0.874619707139, -0.866025403784, -0.857167300702, -0.848048096156, -0.838670567945, -0.829037572555, -0.819152044289, -0.809016994375, -0.798635510047, -0.788010753607, -0.777145961457, -0.766044443119, -0.754709580223, -0.743144825477, -0.731353701619, -0.719339800339, -0.707106781187, -0.694658370459, -0.681998360062, -0.669130606359, -0.656059028991, -0.642787609687, -0.62932039105, -0.615661475326, -0.601815023152, -0.587785252292, -0.573576436351, -0.559192903471, -0.544639035015, -0.529919264233, -0.51503807491, -0.5, -0.484809620246, -0.469471562786, -0.45399049974, -0.438371146789, -0.422618261741, -0.406736643076, -0.390731128489, -0.374606593416, -0.358367949545, -0.342020143326, -0.325568154457, -0.309016994375, -0.292371704723, -0.275637355817, -0.258819045103, -0.2419218956, -0.224951054344, -0.207911690818, -0.190808995377, -0.173648177667, -0.15643446504, -0.13917310096, -0.121869343405, -0.104528463268, -0.0871557427477, -0.0697564737441, -0.0523359562429, 0, 0, 0};
	public static double[] cosLookup = {1.0, 0.999847695156, 0.999390827019, 0.998629534755, 0.99756405026, 0.996194698092, 0.994521895368, 0.992546151641, 0.990268068742, 0.987688340595, 0.984807753012, 0.981627183448, 0.978147600734, 0.974370064785, 0.970295726276, 0.965925826289, 0.961261695938, 0.956304755963, 0.951056516295, 0.945518575599, 0.939692620786, 0.933580426497, 0.927183854567, 0.920504853452, 0.913545457643, 0.906307787037, 0.898794046299, 0.891006524188, 0.882947592859, 0.874619707139, 0.866025403784, 0.857167300702, 0.848048096156, 0.838670567945, 0.829037572555, 0.819152044289, 0.809016994375, 0.798635510047, 0.788010753607, 0.777145961457, 0.766044443119, 0.754709580223, 0.743144825477, 0.731353701619, 0.719339800339, 0.707106781187, 0.694658370459, 0.681998360062, 0.669130606359, 0.656059028991, 0.642787609687, 0.62932039105, 0.615661475326, 0.601815023152, 0.587785252292, 0.573576436351, 0.559192903471, 0.544639035015, 0.529919264233, 0.51503807491, 0.5, 0.484809620246, 0.469471562786, 0.45399049974, 0.438371146789, 0.422618261741, 0.406736643076, 0.390731128489, 0.374606593416, 0.358367949545, 0.342020143326, 0.325568154457, 0.309016994375, 0.292371704723, 0.275637355817, 0.258819045103, 0.2419218956, 0.224951054344, 0.207911690818, 0.190808995377, 0.173648177667, 0.15643446504, 0.13917310096, 0.121869343405, 0.104528463268, 0.0871557427477, 0.0697564737441, 0.0523359562429, 0, 0, 0, 0, 0, -0.0523359562429, -0.0697564737441, -0.0871557427477, -0.104528463268, -0.121869343405, -0.13917310096, -0.15643446504, -0.173648177667, -0.190808995377, -0.207911690818, -0.224951054344, -0.2419218956, -0.258819045103, -0.275637355817, -0.292371704723, -0.309016994375, -0.325568154457, -0.342020143326, -0.358367949545, -0.374606593416, -0.390731128489, -0.406736643076, -0.422618261741, -0.438371146789, -0.45399049974, -0.469471562786, -0.484809620246, -0.5, -0.51503807491, -0.529919264233, -0.544639035015, -0.559192903471, -0.573576436351, -0.587785252292, -0.601815023152, -0.615661475326, -0.62932039105, -0.642787609687, -0.656059028991, -0.669130606359, -0.681998360062, -0.694658370459, -0.707106781187, -0.719339800339, -0.731353701619, -0.743144825477, -0.754709580223, -0.766044443119, -0.777145961457, -0.788010753607, -0.798635510047, -0.809016994375, -0.819152044289, -0.829037572555, -0.838670567945, -0.848048096156, -0.857167300702, -0.866025403784, -0.874619707139, -0.882947592859, -0.891006524188, -0.898794046299, -0.906307787037, -0.913545457643, -0.920504853452, -0.927183854567, -0.933580426497, -0.939692620786, -0.945518575599, -0.951056516295, -0.956304755963, -0.961261695938, -0.965925826289, -0.970295726276, -0.974370064785, -0.978147600734, -0.981627183448, -0.984807753012, -0.987688340595, -0.990268068742, -0.992546151641, -0.994521895368, -0.996194698092, -0.99756405026, -0.998629534755, -0.999390827019, -0.999847695156, -1.0, -0.999847695156, -0.999390827019, -0.998629534755, -0.99756405026, -0.996194698092, -0.994521895368, -0.992546151641, -0.990268068742, -0.987688340595, -0.984807753012, -0.981627183448, -0.978147600734, -0.974370064785, -0.970295726276, -0.965925826289, -0.961261695938, -0.956304755963, -0.951056516295, -0.945518575599, -0.939692620786, -0.933580426497, -0.927183854567, -0.920504853452, -0.913545457643, -0.906307787037, -0.898794046299, -0.891006524188, -0.882947592859, -0.874619707139, -0.866025403784, -0.857167300702, -0.848048096156, -0.838670567945, -0.829037572555, -0.819152044289, -0.809016994375, -0.798635510047, -0.788010753607, -0.777145961457, -0.766044443119, -0.754709580223, -0.743144825477, -0.731353701619, -0.719339800339, -0.707106781187, -0.694658370459, -0.681998360062, -0.669130606359, -0.656059028991, -0.642787609687, -0.62932039105, -0.615661475326, -0.601815023152, -0.587785252292, -0.573576436351, -0.559192903471, -0.544639035015, -0.529919264233, -0.51503807491, -0.5, -0.484809620246, -0.469471562786, -0.45399049974, -0.438371146789, -0.422618261741, -0.406736643076, -0.390731128489, -0.374606593416, -0.358367949545, -0.342020143326, -0.325568154457, -0.309016994375, -0.292371704723, -0.275637355817, -0.258819045103, -0.2419218956, -0.224951054344, -0.207911690818, -0.190808995377, -0.173648177667, -0.15643446504, -0.13917310096, -0.121869343405, -0.104528463268, -0.0871557427477, -0.0697564737441, -0.0523359562429, 0, 0, 0, 0, 0, 0.0523359562429, 0.0697564737441, 0.0871557427477, 0.104528463268, 0.121869343405, 0.13917310096, 0.15643446504, 0.173648177667, 0.190808995377, 0.207911690818, 0.224951054344, 0.2419218956, 0.258819045103, 0.275637355817, 0.292371704723, 0.309016994375, 0.325568154457, 0.342020143326, 0.358367949545, 0.374606593416, 0.390731128489, 0.406736643076, 0.422618261741, 0.438371146789, 0.45399049974, 0.469471562786, 0.484809620246, 0.5, 0.51503807491, 0.529919264233, 0.544639035015, 0.559192903471, 0.573576436351, 0.587785252292, 0.601815023152, 0.615661475326, 0.62932039105, 0.642787609687, 0.656059028991, 0.669130606359, 0.681998360062, 0.694658370459, 0.707106781187, 0.719339800339, 0.731353701619, 0.743144825477, 0.754709580223, 0.766044443119, 0.777145961457, 0.788010753607, 0.798635510047, 0.809016994375, 0.819152044289, 0.829037572555, 0.838670567945, 0.848048096156, 0.857167300702, 0.866025403784, 0.874619707139, 0.882947592859, 0.891006524188, 0.898794046299, 0.906307787037, 0.913545457643, 0.920504853452, 0.927183854567, 0.933580426497, 0.939692620786, 0.945518575599, 0.951056516295, 0.956304755963, 0.961261695938, 0.965925826289, 0.970295726276, 0.974370064785, 0.978147600734, 0.981627183448, 0.984807753012, 0.987688340595, 0.990268068742, 0.992546151641, 0.994521895368, 0.996194698092, 0.99756405026, 0.998629534755, 0.999390827019, 0.999847695156, 1.0};
	
	public void moveDiagonally(int angle){
		/*---------------------------*/
		/* Get speeds and directions */
		/*---------------------------*/
		byte[] speeds = andreDiagonalSpeeds(angle);
		
		/*-----------------------*/
		/* Send directions first */
		/*-----------------------*/
		//EAST Wheel
		I2Csensor.sendData(0x01,speeds[0]); 
		
		//SOUTH Wheel
		I2Csensor.sendData(0x03,speeds[2]); 
		
		//NORTH Wheel
		I2Csensor.sendData(0x05,speeds[4]); 
		
		//WEST Wheel
		I2Csensor.sendData(0x07,speeds[6]);
		
		/*-----------------------*/
		/* Then all the speeds   */
		/*-----------------------*/
		I2Csensor.sendData(0x02,speeds[1]);
		I2Csensor.sendData(0x04,speeds[3]);
		I2Csensor.sendData(0x06,speeds[5]);
		I2Csensor.sendData(0x08,speeds[7]);
	}
	
	/** This function takes a number of degrees to rotate, as I have made it from scratch,
	  * it uses time to know how long it must rotate, according to the speed of the wheels
	  *
	  * @param degrees Takes an integer from -360 to 360.
	  */
	public void rotateTo(int degrees){
	
		/* These are the values to use for fully charged Duracells*/
		
		byte rotateSpeed = (byte) 250;
//		long degToTime = (long) Math.rint(Math.abs(degrees) * 2.25); //3.0 is the value that works on the vision
	
//		byte rotateSpeed = (byte) 200; //Changing this will fuck up the function by the way
//		long degToTime = (long) Math.rint(Math.abs(degrees)*3.3); // Based on the current speed of rotation
		
		if(degrees > 0){
			long degToTime = (long) Math.rint(Math.abs(degrees) * 2.1);
			
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
			
			Delay.msDelay(degToTime);
//			
//			try {
//				Thread.sleep(degToTime);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			stop();
		}
		else if(degrees <= 0){
			long degToTime = (long) Math.rint(Math.abs(degrees) * 2.18);
			
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
			
			Delay.msDelay(degToTime);
			
//			try {
//				Thread.sleep(degToTime);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
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
		grabber.setSpeed(400);
	        grabber.setAcceleration(6000);
	        grabber.rotateTo(80, true);
	}
	
	public void openGrabber() {
		grabber.setSpeed(800);
		grabber.setAcceleration(10000);
		grabber.rotateTo(0, true); 
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
		kick(100);
		aimReset();
	}
	
	//Rotates the rotator 25 degrees right and kicks the ball.
	public void kickRight() {
		rotator.rotateTo(-25); 
		kick(100);
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
		rotator.rotateTo(0);
		rotator.resetTachoCount();
	}
	
	
	/**
	 * Given an angle in DEGREES the method return an byte array consisting of
	 * the speeds and directions for all of the motors.
	 * 
	 * The motors on the robot are positioned as thus, with the arrow showing the
	 * movement direction for FORWARD.
	 *                        
	 *                      (4 and 5)                                  
	 *                        NORTH (This is the way the robot is facing)                           
	 *                                                        
	 *                         -->                       
	 *                         |                              
	 *                      <__|__>                           
	 *                      |  |  |     ^                      
	 * (6 and 7) WEST |--------x--------|  EAST (0 and 1)                 
	 *                v     |__|__|                           
	 *                         |                              
	 *                         |                              
	 *                       <--                             
	 *              	  
	 *                       SOUTH                          
	 *                     (2 and 3)                                   
	 * @param angle The angle in degrees to move diagonally at
	 * @return array - [EAST direction, EAST speed, SOUTH direction, SOUTH speed, NORTH direction, NORTH speed, WEST direction, WEST speed]
	 */
	public byte[] andreDiagonalSpeeds(int angle) {
		// Init return array
		byte[] directionsAndSpeeds = new byte[8];
		
		for (int i = 0; i < 8; i++) {
			directionsAndSpeeds[i] = 0;
		}
		
		// Find the speeds and movement directions through trigonometry		
		final double horisontalMovement = cosLookup[angle];
		final double verticalMovement = sinLookup[angle];

		// Normalise values and get them to range [40, 200]
		double maxMovement = Math.max(Math.abs(horisontalMovement), Math.abs(verticalMovement));
		
		// Just some facts ---
		// Our min trig value is ~0.05
		// so if maxSpeed = 210, it could go down to 210 * 0.05 = 10.5
		// if minSpeed = 30, then that will go to 40.5
		// and actual maximum will be 240.
		// Note that rotating speeds below 40 are DANGEROUS, we can kind of rotate with 40
		// if we have mostly full batteries, 35 only works with out-of-the-package fresh
		// batteries, so should drop total minimum below 40.
		double maxSpeed = diagonalMaxSpeed; // Actual MAXIMUM is maxSpeed + minSpeed :))
		double minSpeed = 20;

		// Find final speeds, these are zero by default!
		byte horisontalSpeed = 0;
		byte verticalSpeed = 0;

		if (horisontalMovement != 0) {
			horisontalSpeed = (byte) (Math.min(Math.abs((maxSpeed * horisontalMovement / maxMovement)), maxSpeed) + minSpeed);
		}
		if (verticalMovement != 0) {
			verticalSpeed = (byte) (Math.min(Math.abs((maxSpeed * verticalMovement / maxMovement)), maxSpeed) + minSpeed);
		}

		/* Save motor speeds, they're at odd positions in the array (1, 3, 5, 7) */
		directionsAndSpeeds[EAST * 2 + 1] = verticalSpeed;
		directionsAndSpeeds[WEST * 2 + 1] = verticalSpeed;
		directionsAndSpeeds[NORTH * 2 + 1] = horisontalSpeed;
		directionsAndSpeeds[SOUTH * 2 + 1] = horisontalSpeed;

		/* Save motor directions, they're at even positions in the array (0, 2, 4, 6) */

		// Vertical movement controlled by east/west wheels
		if (verticalSpeed == 0) {
			// No movement :)
			directionsAndSpeeds[EAST * 2] =  off;
			directionsAndSpeeds[WEST * 2] =  off;
		} else {
			// Movement - boooyaaaah
			directionsAndSpeeds[EAST * 2] =  (verticalMovement > 0) ? forward : backward;
			directionsAndSpeeds[WEST * 2] =  (verticalMovement > 0) ? backward : forward;
		}

		// Horisontal movement controlled by north/south wheels
		if (horisontalSpeed == 0) {
			// No movement :(
			directionsAndSpeeds[NORTH * 2] =  off;
			directionsAndSpeeds[SOUTH * 2] =  off;
		} else {
			// Movement - boooyaaaah
			directionsAndSpeeds[NORTH * 2] =  (horisontalMovement > 0) ? forward : backward;
			directionsAndSpeeds[SOUTH * 2] =  (horisontalMovement > 0) ? backward : forward;
		}

		return directionsAndSpeeds;
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
