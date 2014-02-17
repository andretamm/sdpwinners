package ourcommunication;

import common.Robot;
import constants.RobotColour;
import constants.RobotType;
import sdp.vision.RunVision;
import sdp.vision.WorldState;
import behavior.KillerManager;
import behavior.Manager;
import behavior.DefenderManager;

public class StartRobot {
	
	private static Server server;
	
	public static void main(String[] args){
		WorldState worldstate = new WorldState();
		RunVision.setupVision(worldstate);
		
		System.out.println("Starting the Bluetooth communication...");
		server = new Server(worldstate);
			
		System.out.println("trolololo");
		// Start Behavior manager for the yellow defender

		Manager m = new KillerManager(worldstate, new Robot(RobotColour.BLUE, RobotType.ATTACKER), server);
//		Manager m = new DefenderManager(worldstate, new Robot(RobotColour.BLUE, RobotType.DEFENDER), server);

		m.start();
		
		// Close the Bluetooth communication
//		server.close();
	}
}
