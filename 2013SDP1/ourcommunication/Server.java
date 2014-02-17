package ourcommunication;

import java.io.IOException;

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
//		attackerRobot = new BluetoothCommunication(ATTACKER_NXT_NAME, ATTACKER_NXT_MAC_ADDRESS);
//		
		try {
			defenderRobot.openBluetoothConnection();
		} catch (IOException e) {
			System.err.println("Failed to connect to defender robot");
			e.printStackTrace();
		}
		
//		try {
//			attackerRobot.openBluetoothConnection();
//		} catch (IOException e) {                                            
//			System.err.println("Failed to connect to attacker robot");
//			e.printStackTrace();
//		}
		
		System.out.println("Connected");			
	}
	
	public void receiveHaveBall() {
		while (true) {
			int[] res;
			
			try {
				System.out.println("WAITING FOR SUCCESS PING FROM ROBOT");
				res = defenderRobot.receiveFromRobot();
				
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
//		System.out.println(command);
		
		if (robot == DEFENDER) {
			defenderRobot.sendToRobot(command);
		} else if (robot == ATTACKER) {
			attackerRobot.sendToRobot(command);
		}
	}
	
	/**
	 * Closes the bluetooth connections to the robots
	 */
	public void close() {
		defenderRobot.closeBluetoothConnection();
		attackerRobot.closeBluetoothConnection();
	}

}
