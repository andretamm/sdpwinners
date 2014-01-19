

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
public class HelloWorld3 {
    /**
     * @param args
     */
	
	public static int GreenWhiteThreshold = 40;   //light sensor border reading for running the pitch
	
    public static void main(String[] args) {
    	LightSensor lightS1 = new LightSensor(SensorPort.S1);  //defining the sensor port used
    	LightSensor lightS2 = new LightSensor(SensorPort.S2);  //defining the sensor port used
    	
    	
        DifferentialPilot pilot  = new DifferentialPilot(2f, 4f, Motor.A, Motor.B, false); 
        LCD.drawString(Boolean.toString(lightS2.isFloodlightOn()), 0, 0);
        LCD.drawString("HelloWorld", 0, 0);
        LCD.drawInt(lightS2.getLightValue(), 0, 0); //print light sensor reading to screen
        
        while (true) {
        	
            while (lightS2.getLightValue() < GreenWhiteThreshold){
            	
            	pilot.setTravelSpeed(4);
            	pilot.forward();
            	LCD.drawInt(lightS2.getLightValue(), 0, 0);          	
            }
            
            while (lightS2.getLightValue() >= GreenWhiteThreshold){
            	pilot.stop();
            	pilot.rotateLeft();

//            	pilot.rotate(90);
            }
      } 
    	
    } 
}


