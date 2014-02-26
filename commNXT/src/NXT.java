
/**
 * Main class for starting the NXT brick code
 */
public class NXT {
   
    private static RobotController robot;

    public static void main(String[] args){
    	robot = new RobotController();
    	
    	Thread receiver = new BluetoothConnection(robot);
    	receiver.start();
    	
    	// Wait for commands from the bluetoothconnection
    	while (true) {
    		int command = robot.command;
    		
    		if (command != robot.previousCommand) {
    			// Only do smth if we got a new command
	    		switch (command) {
					case 0:
						robot.fail();
						break;
					case 1:
						robot.forward();
						break;
					case 2:
						robot.backward();
						break;
					case 3:
						robot.chill();
						break;
					case 4:
						robot.turnCW();
						break;
					case 5:
						robot.turnCCW();
						break;
					case 6:
						robot.kick();
						break;
					default:
						robot.chill();
				}
	    		
	    		robot.previousCommand = command;
    		}
    	}
    }
    
    
}