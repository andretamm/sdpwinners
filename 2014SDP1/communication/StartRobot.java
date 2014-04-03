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
//		
//		server.sendRotateDegrees(RobotType.DEFENDER, 180, false);
//		
//		Thread.sleep(4000);
//		
//		server.sendRotateDegrees(RobotType.DEFENDER, 180, false);
//		
//		Thread.sleep(4000);
//
//		server.sendRotateDegrees(RobotType.DEFENDER, 90, false);
//		
//		Thread.sleep(4000);
//
//		server.sendRotateDegrees(RobotType.DEFENDER, 90, false);
//
//		Thread.sleep(4000);
		

//		server.send(RobotType.ATTACKER, RobotCommand.CW);
//		
//		server.sendRotateDegrees(RobotType.ATTACKER, -90, false);
//		
//
//		Thread.sleep(2500);
//		server.sendRotateDegrees(RobotType.ATTACKER, -90, true);
//		
//		Thread.sleep(2500);
//		server.sendRotateDegrees(RobotType.ATTACKER, -90, true);
//		
//		Thread.sleep(2500);
//		server.sendRotateDegrees(RobotType.ATTACKER, -90, true);

//		Thread.sleep(3000);
//		
//		System.out.println("Derp");
//		
//		server.send(RobotType.ATTACKER, RobotCommand.STOP);
//		
//		Thread.sleep(3000);
		
//		server.disconnectFromRobot(RobotType.ATTACKER);
//		
//		Thread.sleep(2000);
//		
//		server.close();
//		
		//Strategy manager
		Strategy strategy = new Strategy(worldstate, server);
		
		// Start GUI
		new MainWindow(worldstate, server, strategy);
		
		server.close();
	}
}
