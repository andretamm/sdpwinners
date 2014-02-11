package ourcommunication;

import common.Robot;
import constants.RobotColour;
import constants.RobotType;
import sdp.vision.RunVision;
import sdp.vision.WorldState;
import behavior.Manager;
import behavior.Milestone3AttackerManager;

public class StartRobot {
	public static void main(String[] args){
		System.out.println("Starting the Bluetooth communication...");
		Server server = new Server();
		
		WorldState worldstate = new WorldState();
		RunVision.setupVision(worldstate); 
		// Start Behavior manager for the yellow defender
		Manager m = new Milestone3AttackerManager(worldstate, new Robot(RobotColour.YELLOW, RobotType.DEFENDER), server);
		m.start();
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Close the Bluetooth communication
//		server.close();
	}
}
