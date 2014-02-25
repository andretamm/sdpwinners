package ourcommunication;

import java.io.IOException;

import constants.RobotType;

import sdp.vision.WorldState;

/**
 * Server that controls connections to the robots
 */
public class Server {
	public static final int DEFENDER = 0;
	public static final int ATTACKER = 1;
	
	private static final String DEFENDER_NXT_MAC_ADDRESS = "00:16:53:0D:53:3E";
	private static final String DEFENDER_NXT_NAME = "NXT";

	//	private static final String DEFENDER_NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
//	private static final String DEFENDER_NXT_NAME = "4s";
	
	private static final String ATTACKER_NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	private static final String ATTACKER_NXT_NAME = "4s";
	
	
	private static BluetoothCommunication defenderRobot;
	private static BluetoothCommunication attackerRobot;
	
	private WorldState ws;
	
	/**
	 *	Initialises the server and connects to the robots 
	 */
	public Server(WorldState ws) {
		this.ws = ws;
		defenderRobot = new BluetoothCommunication(DEFENDER_NXT_NAME, DEFENDER_NXT_MAC_ADDRESS);
		attackerRobot = new BluetoothCommunication(ATTACKER_NXT_NAME, ATTACKER_NXT_MAC_ADDRESS);
	}
	
	/**
	 * Gets the attacker or defender robot
	 */
	private BluetoothCommunication getRobot(RobotType type) {
		if (type == RobotType.ATTACKER) {
			return attackerRobot;
		} else {
			return defenderRobot;
		}
	}
	
	/**
	 * Connects to a robot
	 * @param type Attacker or Defender
	 */
	public void connectToRobot(RobotType type) {
		System.out.println("Trying to connect to " + type + " Robot.");
		
		if (getRobot(type).openBluetoothConnection()) {
			System.out.println("Connected to " + type + " Robot.");
		} else {
			System.out.println("Failed to connect to " + type + " Robot.");
		}
	}
	
	/**
	 * Waits to receive a signal from a robot saying it has the
	 * ball in its grabber
	 * @param type Attacker or Defender
	 */
	public void receiveHaveBall(RobotType type) {
		while (true) {
			int[] res;
			
			try {
				System.out.println("WAITING FOR SUCCESS PING FROM " + type + " ROBOT");
				res = getRobot(type).receiveFromRobot();
				
				boolean equals = true;
				int[] haveball = {1, 0, 0, 0};
				System.out.print("Got: ");
				
				for (int i = 0; i < 4; i++) { // wait for ready signal
					System.out.print(res[i] + " ");
					if (res[i] != haveball[i]) {
						equals = false;
					}
				}
				System.out.println();
				
				if (equals) {
					ws.setHaveBall(true);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("OMGOMGOMGOMG WE HAVE THE BALL");
	}
	
	/**
	 * Sends a command to the robot
	 * 
	 * @param robot Defender or attacker
	 * @param command Command byte
	 */
	public void send(int robot, int command) {
		System.out.println(command);
		
		if (robot == DEFENDER) {
			defenderRobot.sendToRobot(command);
		} else if (robot == ATTACKER) {
			attackerRobot.sendToRobot(command);
		}
	}

	/**
	 * Closes the bluetooth connections to both robots
	 */
	public void close() {
		defenderRobot.closeBluetoothConnection();
		attackerRobot.closeBluetoothConnection();
	}

	/**
	 * Closes the bluetooth connection to a specific robot
	 */
	public void disconnectFromRobot(RobotType type) {
		getRobot(type).sendToRobot(7);
		getRobot(type).closeBluetoothConnection();
	}

}
