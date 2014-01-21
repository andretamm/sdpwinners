

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;


/*
 * Controls the robot's movement and sensors
 * NB! - all units used with the DifferentialPilot MUST BE in millimetres (mm)
 */
public class RobotController {
    /**
     * @param args
     */
	
	public static int GreenWhiteThreshold = 40;   //light sensor border reading for running the pitch
	
    public static void main(String[] args) {
    	// Light sensors in front of the robot
    	LightSensor leftLightSensor = new LightSensor(SensorPort.S1);
    	LightSensor rightLightSensor = new LightSensor(SensorPort.S4);
    	
    	// Wheel sizes are in mm !
        DifferentialPilot pilot  = new DifferentialPilot(56, 112, Motor.A, Motor.B, false); 
        
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
        
        // If we're on the attacker or defender field
        boolean onAttackerField = true;
        float smallEdge = 210; // Length of the goal edge
        float attackerFieldLength = 1600; // Circumference of the attacker field 2100
        float defenderFieldLEngth = 1600; // Circumference of the defender field 1600
        
        pilot.setTravelSpeed(200); // used to be 4
        
        pilot.forward();
        
        while (true) {

            if (rightLightSensor.getLightValue() < GreenWhiteThreshold && leftLightSensor.getLightValue() < GreenWhiteThreshold){
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
            		pilot.rotate(-2); // To correct for the overturn
            		pilot.stop();

            		System.out.println("Rotated: " + pilot.getAngleIncrement() + " total: " + anglesTurned);
            		isRotating = false;
            		
            		// Move forward
            		pilot.setTravelSpeed(200); // used to be 4
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
            			
            			// If this movement was a small one, then we're on the defender field
            			if (pilot.getMovementIncrement() >= smallEdge - 50 && pilot.getMovementIncrement() <= smallEdge + 50) {
            				System.out.println("OMG I'M ON THE DEFENDER COURT!!!!!!!");
            				onAttackerField = false;
            			}
            		}
            		
            		// Start rotating
            		System.out.println("Moved: " + pilot.getMovementIncrement() + " total: " + distanceTravelled);
            		
            		pilot.stop();
            		isRotating = true;
            		pilot.setTravelSpeed(60);
            		pilot.rotateLeft();
            	}
            }
            
//            // Robot will run until it's rotated itself 360 degrees while moving around,
//            // then stop
//            if (anglesTurned > 360) {
//            	System.out.println("ROTATED 360 DEGREES lol");
//            	try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            	break;
//            }
            
            if (onAttackerField) {
            	if (distanceTravelled + pilot.getMovementIncrement() >= attackerFieldLength) {
	            	System.out.println("Reached attacker field starting point");
	            	break;
            	}
            } else {
            	// Defender field
            	if (distanceTravelled + pilot.getMovementIncrement() >= defenderFieldLEngth) {
            		System.out.println("Reached defender field starting point");
            		break;
            	}
            }
        } 
    } 
}


