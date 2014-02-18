import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;


public class I2CTest {

	public static void main(String[] args) {


		
		I2CPort I2Cport; //Create a I2C port 
		I2Cport = SensorPort.S1; //Assign port 
		I2CSensor I2Csensor = new I2CSensor(I2Cport, 0xB4, I2CPort.STANDARD_MODE, I2CSensor.TYPE_LOWSPEED_9V); 
		
		//Creates an I2CSensor 
		byte forward = (byte)1; 
		byte backward = (byte)2; 
		byte off = (byte)0; 
		byte speed = (byte)100; 
		
		//Facing forward towards the kicker, the wheels are programmed accordingly:
		
		/*              ** Forwards facing wheels **		*/
		//0x01 (I2C - Port 1) - Wheel on the left of the robot, facing forwards.
		//0x05 (I2C - Port 3) - Wheel on the right of the robot, facing forwards.
		
		/*				** Side Ways Facing Wheels **		*/
		//0x03 (I2C - Port 2) - The front wheel, closest to the kicker.
		//0x07 (I2C - Port 4) - The back wheel away from the kicker.
		 
		
		//Instructions for wheels
		LCD.drawString("LEFT -> Rotation", 0, 0);
		LCD.drawString("ENTER -> Backwards", 0, 2);
		LCD.drawString("RIGHT -> Forwards", 0, 4);
		LCD.drawString("ESCAPE -> STOP", 0, 6);
		
		while (true){
			int buttonID = Button.waitForAnyPress();
			switch (buttonID) {
			case Button.ID_LEFT:  //rotation movement
				//left wheel facing forward
				I2Csensor.sendData(0x01,backward); 
				I2Csensor.sendData(0x02,speed); 
				//right wheel facing forward
				I2Csensor.sendData(0x05,backward); 
				I2Csensor.sendData(0x06,speed); 
				//front wheel at the kicker
				I2Csensor.sendData(0x03,forward); 
				I2Csensor.sendData(0x04,speed); 
				//back wheel away from the kicker
				I2Csensor.sendData(0x07,forward); 
				I2Csensor.sendData(0x08,speed); 
				break;
			case Button.ID_ENTER:
				//left wheel facing forward
				I2Csensor.sendData(0x01,backward); 
				I2Csensor.sendData(0x02,speed); 
				//right wheel facing forward
				I2Csensor.sendData(0x05,forward); 
				I2Csensor.sendData(0x06,speed); 
				break;
			case Button.ID_RIGHT: 
				//left wheel facing forward
				I2Csensor.sendData(0x01,forward); 
				I2Csensor.sendData(0x02,speed); 
				//right wheel facing forward
				I2Csensor.sendData(0x05,backward); 
				I2Csensor.sendData(0x06,speed); 
				break;
			case Button.ID_ESCAPE:
				I2Csensor.sendData(0x01,off); 
				I2Csensor.sendData(0x02,off);
				I2Csensor.sendData(0x03,off); 
				I2Csensor.sendData(0x04,off); 
				I2Csensor.sendData(0x05,off); 
				I2Csensor.sendData(0x06,off); 
				I2Csensor.sendData(0x07,off); 
				I2Csensor.sendData(0x08,off); 
				break;
		}
		}
			
		}
		
		

	}


