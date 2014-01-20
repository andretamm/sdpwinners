

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
    	LightSensor lightS2 = new LightSensor(SensorPort.S4);  //defining the sensor port used
    	
    	
        DifferentialPilot pilot  = new DifferentialPilot(1f, 2f, Motor.A, Motor.B, false); 
        LCD.drawString(Boolean.toString(lightS2.isFloodlightOn()), 0, 0);
        LCD.drawString("HelloWorld", 0, 0);
        LCD.drawInt(lightS2.getLightValue(), 0, 0); //print light sensor reading to screen
        
        // Distance from wherever the robot is placed on the pitch until it hits
        // the first white line
        float distanceToFirstLine;
        // The angle the robot turns after first hitting the white line
        float firstTurnAngle;
        
        // The total distance the robot has traveled and how much it has turned
        // while travelling on the white line (i.e. excluding distanceToFirstLine and
        // firstTurnAngle)
        float distanceTravelled = 0;
        float anglesTurned = 0;
        
        boolean isRotating = false;
        boolean onWhiteLine = false; // If we've reached the white line
        
        pilot.setTravelSpeed(4);
        pilot.forward();
        
        pilot.setAcceleration(30);
        
        while (true) {
            if (lightS2.getLightValue() < GreenWhiteThreshold && lightS1.getLightValue() < GreenWhiteThreshold){
            	// We're on green
            	
            	if (isRotating) {
            		if (!onWhiteLine) {
            			// First time moving on the line, remember how much we rotated
            			onWhiteLine = true;
            			firstTurnAngle = pilot.getAngleIncrement();
            		} else {
            			// We made a turn on the white line
            			anglesTurned += pilot.getAngleIncrement();
            		}
            		
            		// Stop rotating
            		System.out.println("Rotated: " + pilot.getAngleIncrement() + " total: " + anglesTurned);
            		pilot.stop();
            		isRotating = false;
            		
            		// Move forward
            		pilot.setTravelSpeed(3);
	            	pilot.forward();
            	}
            } else {
            	// We're on white
            	
            	if (!isRotating) {
            		if (!onWhiteLine) {
                		// First time we rotate, remember distance from starting point
            			distanceToFirstLine = pilot.getMovementIncrement();
            		} else {
            			// Travelling on the white line
            			distanceTravelled += pilot.getMovementIncrement();
            		}
            		
            		// Start rotating
            		System.out.println("Moved: " + pilot.getMovementIncrement() + " total: " + distanceTravelled);
            		
            		pilot.stop();
            		isRotating = true;
            		pilot.rotateLeft();
            	}
            }
            
            // Robot will run until it's rotated itself 360 degrees while moving around,
            // then stop
            if (anglesTurned > 360) {
            	System.out.println("ROTATED 360 DEGREES lol");
            	try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	break;
            }
        } 
    } 
}


