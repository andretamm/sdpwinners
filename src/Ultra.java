	
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
	

public class Ultra{
	
		public static int distanceToFloor = 25; //the distance to the floor from the current design
		public static int travelSpeed = 600; //the speed of the robot
		static NXTRegulatedMotor grabber;
		static DifferentialPilot pilot;
		static UltrasonicSensor sonic;
		
	    public static void main(String[] args) throws InterruptedException {
	    	
	    	sonic = new UltrasonicSensor(SensorPort.S2);
	    	pilot  = new DifferentialPilot(56, 112, Motor.A, Motor.B, false); 
	    	grabber = Motor.C;
	    	grabber.resetTachoCount();
	    	grabber.rotateTo(0);
	    	Button.ENTER.waitForPressAndRelease(); //Start the program
	        	grabber.setSpeed(200);
	        	grabber.setAcceleration(6000);
	        	grabber.rotateTo(5);
	        

	        	//when the sensor detects a ball, it will go ape shit and run the grabBall method 
	        	while(sonic.getDistance()>distanceToFloor){
	        		pilot.setTravelSpeed(travelSpeed);
	        		pilot.forward();
	        		
	            }
	        	//when a ball is spotted, give it 100 millieseconds so that the grabber can get in position
	        	Thread.sleep(300);
	        	grabBall();
	        
	    }
	    
	    //this will grab the ball and kick it when called
	    /*Note that these values used are in ratio with the robot speed,
	    	by this I mean that the delay in grab time and sensoring time 
	    	is fitted to the speed of 600.***/
	    
	    private static void grabBall() throws InterruptedException{
	    	
	    	pilot.setTravelSpeed(travelSpeed);
	    	pilot.forward();
        	//the grabber starts closed
	    	grabber.setSpeed(600);
        	grabber.setAcceleration(8000);
        	//close the grabber
        	grabber.rotateTo(-10);
        	Thread.sleep(100);
        	
        	//open the grabber
        	pilot.stop(); //stop the robot before it makes the kick
        	//This sleep is required for the ball to settle down, otherwise the kicker misses the ball
        	Thread.sleep(100);
        	//full speed kick used from mile stone 2
        	grabber.setSpeed(1000);
        	grabber.setAcceleration(14000);
        	grabber.rotateTo(30);
        	//reset the grabber to its original starting position
        	Thread.sleep(300);
        	grabber.rotateTo(-30);
	    	
	    	
	    }
}
	            
	            
	      
	    	
	    
