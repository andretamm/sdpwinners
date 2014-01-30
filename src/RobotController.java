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
    	LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
    	LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
    	
    	// Wheel sizes are in mm !
        DifferentialPilot pilot  = new DifferentialPilot(56, 112, Motor.A, Motor.B, false); 
        
        // Distance from wherever the robot is placed on the pitch until it hits
        // the first white line
        float distanceToFirstLine;
        
        // The total distance the robot has traveled and how much it has turned
        // while travelling on the white line (i.e. excluding the distance it travels until it
        // first meets the white line)
        float distanceTravelled = 0;
        float anglesTurned = 0;
        
        boolean isRotating = false; // True if the robot is rotating, false if moving forward
        boolean onWhiteLine = false; // If we've on/tracing the white line
        
        // If we're on the attacker or defender field
        boolean onAttackerField = false;
        
        float attackerFieldLength = 2200; // Circumference of the attacker field 2100
        float defenderFieldLEngth = 1700; // Circumference of the defender field 1600
        
        // Start the robot moving
        pilot.setTravelSpeed(200);
        pilot.forward();
        
        // Readings from the light sensors
        int leftSensorValue;
        int rightSensorValue;
        
        // Direction of rotation, false for left
        boolean rotatingRight = true;
        
        // Main control loop
        while (true) {
        	leftSensorValue = leftLightSensor.getLightValue();
        	rightSensorValue = rightLightSensor.getLightValue(); 

            if (rightSensorValue < GreenWhiteThreshold && leftSensorValue < GreenWhiteThreshold){
            	// Both sensors on green
            	
            	if (isRotating) {
            		// The robot is rotating, but should be moving forward
            		
            		if (!onWhiteLine) {
            			// We can only rotate if we're on the white line, so we HAVE to
            			// be on the white line now
            			onWhiteLine = true;
            		} else {
            			// We made a turn on the white line, add to total turned amount
            			anglesTurned += pilot.getAngleIncrement();
            		}
            		
            		// Stop rotating and move forward instead
            		// First correct for the overturn
            		if (rotatingRight) {
            			pilot.rotate(1); 
            		} else {
            			pilot.rotate(-1);
            		}

            		System.out.println("Rotated: " + pilot.getAngleIncrement() + " total: " + anglesTurned);
            		isRotating = false;
            		
            		// Move forward in a slight arc back towards the white line
            		pilot.setTravelSpeed(200);
            		
            		if (rotatingRight) {
            			pilot.arcForward(13200);
            		} else {
            			pilot.arcForward(-13200);
            		}
            	}
            } else {
            	// One sensor is on the white strip
            	
            	if (!isRotating) {
            		// We're currently moving forward, should stop and start rotating
            		
            		if (!onWhiteLine) {
            			// This is the first time we meet the white line, so record the distance
            			// from the starting point until the white line
            			distanceToFirstLine = pilot.getMovementIncrement();
            			
            			// Decide which way we're going to rotate
            			if (rightSensorValue >= GreenWhiteThreshold) {
                			rotatingRight = true;
                		} else if (leftSensorValue >= GreenWhiteThreshold) {
                			rotatingRight = false;
                		}
            		} else {
            			// Travelling on the white line, add to total distance travelled
            			distanceTravelled += pilot.getMovementIncrement();
            		}
            		
            		// Start rotating
            		System.out.println("Moved: " + pilot.getMovementIncrement() + " total: " + distanceTravelled);
            		
            		pilot.stop();
            		isRotating = true;
            		pilot.setTravelSpeed(60);
            		
            		if (rotatingRight) {
            			pilot.rotateRight();
            		} else {
            			pilot.rotateLeft();
            		}
            		
            	}
            }
            
            // Checks if we've made the full trip around the field
            if (onAttackerField) {
            	if (distanceTravelled + pilot.getMovementIncrement() >= attackerFieldLength) {
	            	System.out.println("Reached attacker field starting point");
	            	pilot.stop();
	            	break;
            	}
            } else {
            	// Defender field
            	if (distanceTravelled + pilot.getMovementIncrement() >= defenderFieldLEngth) {
            		System.out.println("Reached defender field starting point");
            		pilot.stop();
            		break;
            	}
            }
        } 
    } 
}


