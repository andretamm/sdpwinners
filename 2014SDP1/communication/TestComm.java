package communication;

import constants.RobotType;

public class TestComm {
	
	public static void main(String[] args) throws InterruptedException{
	    int SLEEP = 700;
		System.out.println("Starting the Bluetooth communication...");
		Server server = new Server(null);
		
		server.connectToRobot(RobotType.ATTACKER);
		server.sendDiagonalMovement(RobotType.ATTACKER, 45);
		Thread.sleep(SLEEP);
		server.connectToRobot(RobotType.ATTACKER);
		server.sendDiagonalMovement(RobotType.ATTACKER, 135);
		Thread.sleep(SLEEP);
		server.connectToRobot(RobotType.ATTACKER);
		server.sendDiagonalMovement(RobotType.ATTACKER, 225);
		Thread.sleep(SLEEP);
		server.connectToRobot(RobotType.ATTACKER);
		server.sendDiagonalMovement(RobotType.ATTACKER, 315);
		Thread.sleep(SLEEP);
		server.close();
		
		
	}

}
