import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;


public class I2CTest {
	static I2CPort I2Cport;
	static I2CSensor I2Csensor;
	static byte forward; 
	static byte backward; 
	static byte off; 
	static byte speed;
	static byte EWrotSpeed;
	static byte NSrotSpeed;
	public static void main(String[] args) {


		
		 //Create a I2C port 
		I2Cport = SensorPort.S1; //Assign port 
		I2Csensor = new I2CSensor(I2Cport, 0xB4, I2CPort.STANDARD_MODE, I2CSensor.TYPE_LOWSPEED_9V); 
		
		//Creates an I2CSensor 
		forward = (byte)1; 
		backward = (byte)2; 
		off = (byte)0; 
		speed = (byte)200; 
		
		//to achieve rotation in place, the outside wheel have to catch up with the inside wheels.
		EWrotSpeed = (byte)100; //the speed of the outside wheels
		NSrotSpeed = (byte)(EWrotSpeed*0.76); //the speed of the inside wheels as a function of the speed of the outside wheels.
		
		//Facing forward towards the kicker, the wheels are programmed accordingly:
		
		/*              ** Forwards facing wheels **		*/
		// Distance between wheel is 650mm
		//0x01 (I2C - Port 1) - WEST Wheel.
		//0x07 (I2C - Port 4) - EAST Wheel.
		
		/*				** Side Ways Facing Wheels **		*/
		//Distance between wheels are 500mm
		//0x03 (I2C - Port 2) - NORTH Wheel
		//0x05 (I2C - Port 3) - SOUTH Wheel
		 
		
		//Instructions for wheels
		LCD.drawString("LEFT -> 90", 0, 0);
		LCD.drawString("ENTER -> 180", 0, 2);
		LCD.drawString("RIGHT -> 270", 0, 4);
		LCD.drawString("ESCAPE -> 360", 0, 6);
		
		while (true){
			int buttonID = Button.waitForAnyPress();
			//button presses to test rotation
			switch (buttonID) {
			case Button.ID_LEFT:  
				try {
					rotateTo(90);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			
				break;
			case Button.ID_ENTER:
				try {
					rotateTo(180);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				break;
			case Button.ID_RIGHT: 
				try {
					rotateTo(270);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				break;
			case Button.ID_ESCAPE:
				try {
					rotateTo(360);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				break;
		}
		}
			   
		}
	
	/*This function takes a number of degrees to rotate, as I have made it from scratch,
	  it uses time to know how long it must rotate, according to the speed of the wheels*/
	
	public static void rotateTo(int degrees) throws InterruptedException{
		
		if(degrees < 0){
			backward = (byte) -backward;
			forward = (byte) -forward;
			degrees = -degrees;
		}
		long degToTime = (long) Math.rint(degrees*8.3); // Based on the current speed of rotation
		
		//WEST Wheel
		I2Csensor.sendData(0x01,forward); 
		I2Csensor.sendData(0x02,EWrotSpeed); 
		//EAST Wheel
		I2Csensor.sendData(0x07,backward); 
		I2Csensor.sendData(0x08,EWrotSpeed); 
		//NORTH Wheel
		I2Csensor.sendData(0x03,forward); 
		I2Csensor.sendData(0x04,NSrotSpeed); 
		//SOUTH Wheel
		I2Csensor.sendData(0x05,backward); 
		I2Csensor.sendData(0x06,NSrotSpeed); 
		Thread.sleep(degToTime);
		stop();
	}
	
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
		
		

	}


