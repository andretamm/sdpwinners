
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
    		if (command != robot.previousCommand || (command == 8 && robot.angle != robot.previousAngle)) {
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
						robot.chill();
						robot.fastKick();
						break;
					case 8:
						System.out.println("Moving Diagonally");
						System.out.println("angle: " + robot.angle);
						robot.goDiagonally(robot.angle);
						break;
					case 10:
						robot.chill();
						robot.grab();
						break;
					case 11:
						robot.openGrabber();
						break;
					case 12:
						robot.goLeft();
						break;
					case 13:
						robot.goRight();
						break;
					case 14:
						robot.kickLeft();
						break;
					case 15:
						robot.kickRight();
						break;
					case 16:
						robot.aimLeft();
						break;
					case 17:
						robot.aimRight();
						break;
					case 18:
						robot.aimReset();
						break;
					case 19:
						robot.slowCW();
						break;
					case 20:
						robot.slowCCW();
						break;
					case 21:
						robot.slowKick();
						break;
					default:
						robot.chill();
				}
	    		
	    		robot.previousCommand = command;
	    		robot.previousAngle   = robot.angle;
    		}
    	}
    }
}