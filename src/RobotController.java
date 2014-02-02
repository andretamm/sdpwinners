import behavior.Manager;

/**
 * Main class for starting the robot and everything else
 */
public class RobotController {
	private Manager manager;
	
    public static void main(String[] args) {
    	RobotController controller = new RobotController();
    	controller.manager = new Manager();
    	controller.manager.start();
    } 
}


