	
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
	

public class Ultra{
	
		public static int distanceToFloor = 24; //the distance to the floor from the current design
		public static int travelSpeed = 600; //the speed of the robot
		static NXTRegulatedMotor grabber;
		static DifferentialPilot pilot;
		static UltrasonicSensor sonic;
		
	    public static void main(String[] args) throws InterruptedException {
	    	
	    	sonic = new UltrasonicSensor(SensorPort.S2);
	    	pilot  = new DifferentialPilot(56, 112, Motor.A, Motor.B, false); 
	    	grabber = Motor.C;
	    	
	    	pilot.setTravelSpeed(travelSpeed);
	    	pilot.forward();
	    	//When we are in line with the ball
	    	activateSensor();
	    	closeGrabber();
	    	kickBall();
	    		        
	    }
	    
	    
	    //sensor will be activated, this should be called when the robot is in line with the ball
	    private static void activateSensor() throws InterruptedException{
	    	
	    	grabber.resetTachoCount();
	    	grabber.rotateTo(0);
	    	
	    	while(sonic.getDistance()>distanceToFloor){
        		pilot.setTravelSpeed(travelSpeed);
        		pilot.forward();	
            }
	    	Thread.sleep(200);
	    	
	    }
	    
	    //called to grab the ball when the sensor sees the ball
	    private static void closeGrabber() throws InterruptedException{
	    	//the grabber closing speed 
	    	grabber.setSpeed(800);
        	grabber.setAcceleration(10000);
        	//close the grabber
        	grabber.rotateTo(-15);
        	pilot.stop();
        	Thread.sleep(1000);
	    }

		private static void kickBall() throws InterruptedException{
        	//full speed kick used from mile stone 2
        	grabber.setSpeed(1000);
        	grabber.setAcceleration(14000);
        	grabber.rotateTo(30);
        	//reset the grabber to its original starting position
        	Thread.sleep(300);
        	grabber.rotateTo(-30);
			
		}
	    
	   
}
	            
	            
	      
	    	
	    
