

	import lejos.nxt.LCD;
	import lejos.nxt.LightSensor;
	import lejos.nxt.Motor;
	import lejos.nxt.SensorPort;
	import lejos.robotics.navigation.DifferentialPilot;
	public class HelloWorld3 {
	    /**
	     * @param args
	     */
		
		public static int GreenWhiteThreshold = 40;
		
	    public static void main(String[] args) {
	    	LightSensor light = new LightSensor(SensorPort.S2);
	        DifferentialPilot pilot  = new DifferentialPilot(2f, 2f, Motor.A, Motor.B, false); 
	        LCD.drawString(Boolean.toString(light.isFloodlightOn()), 0, 0);
	        LCD.drawString("HelloWorld", 0, 0);
	        LCD.drawInt(light.getLightValue(), 0, 0);
	        
	        while (true) {
	        	
	            while (light.getLightValue() < GreenWhiteThreshold){
	            	
	            	pilot.setTravelSpeed(4);
	            	pilot.forward();
	            	LCD.drawInt(light.getLightValue(), 0, 0);          	
	            }
	            
	            while (light.getLightValue() >= 22){
	            	pilot.rotateLeft();
	            }
	      } 
	    	
	    } 
	}


