package communication;

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
//		server.connectToRobot(RobotType.ATTACKER);
//		
//		server.sendRotateDegrees(RobotType.ATTACKER, 180, false);
//		
//		Thread.sleep(4000);
//		
//		server.sendRotateDegrees(RobotType.ATTACKER, -180, false);
		
//		server.sendRotateDegrees(RobotType.ATTACKER, 90, false);
//		
//		
//		
//		Thread.sleep(2500);
//		server.sendRotateDegrees(RobotType.ATTACKER, 90, true);
//		
//		Thread.sleep(2500);
//		server.sendRotateDegrees(RobotType.ATTACKER, 90, true);
//		
//		Thread.sleep(2500);
//		server.sendRotateDegrees(RobotType.ATTACKER, 90, true);
//		
		
//		
//		Thread.sleep(4000);
//		
//		server.disconnectFromRobot(RobotType.ATTACKER);
//		
//		server.close();
		
		//Strategy manager
		Strategy strategy = new Strategy(worldstate, server);
		
		// Start GUI
		new MainWindow(worldstate, server, strategy);
		
	}
}
