package ourcommunication;

import constants.RobotType;

public class TestComm {
	
	public static void main(String[] args) throws InterruptedException{
		System.out.println("Starting the Bluetooth communication...");
		Server server = new Server(null);
		
		server.connectToRobot(RobotType.DEFENDER);
		server.sendDiagonalMovement(RobotType.DEFENDER, 45);
		Thread.sleep(10000);
		server.close();
	}

}
