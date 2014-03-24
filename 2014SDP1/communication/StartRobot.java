package communication;

import constants.RobotType;
import gui.MainWindow;
import sdp.vision.RunVision;
import sdp.vision.WorldState;
import behavior.Strategy;

public class StartRobot {
	
	private static Server server;
	
	public static void main(String[] args) throws InterruptedException{
		// Start vision
		WorldState worldstate = new WorldState();
		RunVision.setupVision(worldstate);
	    
		
		// Set up Bluetooth communications class
		server = new Server(worldstate);
//		server.connectToRobot(RobotType.DEFENDER);
		
//		server.sendDiagonalMovement(RobotType.DEFENDER, 45);
//		Thread.sleep(3000);
//		server.close();
		
		//Strategy manager
		Strategy strategy = new Strategy(worldstate, server);
		
		// Start GUI
		MainWindow mw = new MainWindow(worldstate, server, strategy);
		
	}
}
