package communication;

import java.io.IOException;
import java.util.EnumMap;

import behavior.StrategyHelper;
import sdp.vision.WorldState;
import constants.RobotType;

/**
 * Server that controls connections to the robots
 */
public class Server {
//	private static final String DEFENDER_NXT_MAC_ADDRESS = "00:16:53:0D:53:3E";
//	private static final String DEFENDER_NXT_NAME = "NXT";

	private static final String DEFENDER_NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	private static final String DEFENDER_NXT_NAME = "4s";
	
//	private static final String ATTACKER_NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
//	private static final String ATTACKER_NXT_NAME = "4s";
	
	private static final String ATTACKER_NXT_MAC_ADDRESS = "00:16:53:0D:53:3E";
	private static final String ATTACKER_NXT_NAME = "NXT";
	
	private static BluetoothCommunication defenderRobot;
	private static BluetoothCommunication attackerRobot;
	
	public static EnumMap<RobotType, Integer> previousCommand;
	private EnumMap<RobotType, Integer> previousAngle;
	
	private WorldState ws;
	
	/**
	 *	Initialises the server and connects to the robots 
	 */
	public Server(WorldState ws) {
		this.ws = ws;
		defenderRobot = new BluetoothCommunication(DEFENDER_NXT_NAME, DEFENDER_NXT_MAC_ADDRESS);
		attackerRobot = new BluetoothCommunication(ATTACKER_NXT_NAME, ATTACKER_NXT_MAC_ADDRESS);
		
		previousCommand = new EnumMap<RobotType, Integer>(RobotType.class);
		previousCommand.put(RobotType.DEFENDER, RobotCommand.NO_COMMAND);
		previousCommand.put(RobotType.ATTACKER, RobotCommand.NO_COMMAND);
		
		previousAngle = new EnumMap<RobotType, Integer>(RobotType.class);
		previousAngle.put(RobotType.DEFENDER, 0);
		previousAngle.put(RobotType.ATTACKER, 0);
	}
	
	/**
	 * Gets the attacker or defender robot bluetooth communication class
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
					ws.setRobotGrabbedBall(ws.getOur(type), true);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("OMGOMGOMGOMG WE HAVE THE BALL");
	}

	/**
	 * Sends a command to the robot. Doesn't send the command
	 * if the last command we sent was the same command or if
	 * the robot isn't connected. This helps prevent overspamming 
	 * the robot with commands.
	 * 
	 * @param type Defender or attacker
	 * @param command Command byte
	 */
	public void send(RobotType type, int command) {
		// Pick right robot channel
		
		BluetoothCommunication robot;

		if (type == RobotType.DEFENDER) {
			robot = defenderRobot;
		} else {
			robot = attackerRobot;
		}

		// Only send command if it is different from the last one
		// we sent and the robot is actually connected
		if (previousCommand.get(type) != command) {
			if(robot.isConnected()) {
				previousCommand.put(type, command);
				robot.sendToRobot(command);
			} else {
				previousCommand.put(type, command);
				System.out.println(type + " is not connected, not sending command");
			}
		}
	}
	
	/**
	 * Sends a command to the robot. Always sends it, no
	 * matter what. ONLY USE THIS IN EXTREME SITUATIONS.
	 * Use send() instead for normal commands.
	 * 
	 * @param type Defender or attacker
	 * @param command Command byte
	 */
	public void forceSend(RobotType type, int command) {
		// Pick right robot channel
		BluetoothCommunication robot;

		if (type == RobotType.DEFENDER) {
			robot = defenderRobot;
		} else {
			robot = attackerRobot;
		}

		// Only send command if robot is actually connected
		if (robot.isConnected()) {
			previousCommand.put(type, command);
			robot.sendToRobot(command);
		}
	}
	
	/**
	 * Send a command to the robot to move diagonally. The method
	 * choppes an angle and send it to the NXT.
	 * 
	 * @param type Defender or attacker
	 * @param angle to rotate to
	 */
	public void sendDiagonalMovement(RobotType type, int angleToGo) {
		if (previousCommand.get(type) == 8) {
			if ((Math.abs(previousAngle.get(type) - angleToGo)) < 3) {
				// Angle no change enough, do nothing lol
				return;
			}
		}
		
		previousCommand.put(type, 8);
		previousAngle.put(type, angleToGo);
		
		 // Create the angle that is send to the NXT
		int angle = 0;
		
		// Get the robot's orientation from the Vision System.
		int angleRobotIsFacing = (int)Math.toDegrees(ws.getRobotOrientation(type, ws.getColour()));
		
		// Get the orientation of the robot's zero
		int robotZero = (angleRobotIsFacing + 90) % 360;
		
		// Get the difference between the robot zero and the angle we want to go to
		double angleDiff = StrategyHelper.angleDiff(Math.toRadians(robotZero), Math.toRadians(angleToGo));
		
		if (angleDiff > 0) {
			angle = 360 - (int)Math.toDegrees(angleDiff);	
		} else if (angleDiff < 0) {
			angle = (int)Math.toDegrees(angleDiff) ;
		}
		
		System.out.println(angleToGo + " " + Math.abs(angle));
		
		angle = Math.abs(angle);
		
		// Represent the angle as two bytes
		byte[] angleArray = new byte[2];
		
		// Mask all but the lower eight bits.
		angleArray[0] = (byte) (angle & 0xFF);
		
		// >> 8 discards the lowest 8 bits by moving all bits 8 places to the right
		angleArray[1] = (byte) ((angle >> 8) & 0xFF);
		
		if (type == RobotType.DEFENDER) {
			defenderRobot.sendToRobot(8);
			defenderRobot.sendToRobot(angleArray[0]);
			defenderRobot.sendToRobot(angleArray[1]);
			
		} else if (type == RobotType.ATTACKER) {
			attackerRobot.sendToRobot(8);
			attackerRobot.sendToRobot(angleArray[0]);
			attackerRobot.sendToRobot(angleArray[1]);
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
