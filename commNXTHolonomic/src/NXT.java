
/**
 * Main class for starting the NXT brick code
 */
public class NXT {
   
    private static RobotController robot;

    public static void main(String[] args){
    	robot = new RobotController();
    	
    	Thread receiver = new BluetoothConnection(robot);
    	receiver.setPriority(Thread.MIN_PRIORITY);
    	receiver.start();
    	
    	// Wait for commands from the bluetooth connection
    	while (true) {
    		long commandTime = robot.commandTime;
    		int command = robot.command;
    		int angle = robot.angle;
    		
    		if (command != robot.previousCommand ||  
    			((command == 8 || command == 25) && robot.previousAngle != angle) || // Diagonal movement
    			(command == 22 && robot.previousCommandTime != commandTime)) { // SUPFR rotation
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
						robot.goDiagonally(robot.angle);
						robot.previousAngle = angle;
						break;
					case 10:
						robot.chill();
						robot.grab();
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
					case 22:
						// Store the time of the last time we did the rotation,
						// if the time of the command coming in hasn't changed, then
						// we won't redo the rotation until we get a new command with
						// a new timestamp
						robot.previousCommandTime = commandTime;
						robot.rotateTo(robot.rotateAngle);
						break;
					case 25:
						robot.goDiagonallySlow(robot.angle);
						robot.previousAngle = angle;
						break;
					case 26:
						robot.fastForward();
						break;
					case 27:
						robot.fastBackward();
						break;
					default:
						robot.chill();
				}
	    		
	    		robot.previousCommand = command;
	    		
    		}
    		
    		try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
}