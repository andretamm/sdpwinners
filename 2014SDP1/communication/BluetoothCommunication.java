package communication;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * Provides communication between PC and Robot
 * 
 * @author Sarun Gulyanon
 * @author Richard Kenyon
 * @author Nikolay Bogoychev
 */
public class BluetoothCommunication {

	public static final int[] ROBOT_READY = {0, 0, 0, 0};
	private InputStream in;
	private OutputStream out;
	private NXTComm nxtComm;
	private NXTInfo nxtInfo;
	private boolean robotReady = false;
	private boolean connected = false;

	/**
	 * @param deviceName
	 *            The name of the Bluetooth device
	 * @param deviceMACAddress
	 *            The MAC address of the Bluetooth device
	 */
	public BluetoothCommunication(String deviceName, String deviceMACAddress) {
		nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceName,
				deviceMACAddress);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (connected) {
					closeBluetoothConnection();
				}
			}
		});
	}

	/**
	 * Returns true if the server is connected to the robot, returns false
	 * otherwise
	 * 
	 * @return a boolean indicating whether the server is connected to the robot
	 *         or not
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Receive a byte from the robot
	 * 
	 * @return An integer array containing the byte we received from the robot
	 * 
	 * @throws IOException
	 *             when fail to receive a byte from robot
	 */
	public int[] receiveFromRobot() throws IOException {
		byte[] res = new byte[4];
		in.read(res);
		System.out.println("Receiving from robot");
		int[] ret = { (int) (res[0]), (int) (res[1]), (int) (res[2]),
				(int) (res[3]) };
		return ret;
	}

	/**
	 * Returns whether the robot is ready to receive data or not. Always check
	 * that the robot is ready before sending any commands.
	 */
	public boolean isRobotReady() {
		return robotReady;
	}

	/**
	 * Opens a new Bluetooth connection and connects the input and output
	 * streams to this new Bluetooth connection
	 * 
	 * @throws IOException
	 *             when we fail to open the Bluetooth connection
	 * @return true for success, false for failure
	 */
	public boolean openBluetoothConnection() {
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException e) {
			System.err.println("Could not create connection: " + e.toString());
		}

		System.out.println("Attempting to connect to robot...");
		
		for (int attempt = 0; attempt < 2; attempt++) {
			try {
				nxtComm.open(nxtInfo);
				in = nxtComm.getInputStream();
				out = nxtComm.getOutputStream();
	
				while (true) {
					int[] res = receiveFromRobot();
					boolean equals = true;
					for (int i = 0; i < 4; i++) { // wait for ready signal
						if (res[i] != ROBOT_READY[i]) {
							equals = false;
							break;
						}
					}
					if (equals) {
						break;
					} else {
						Thread.sleep(10); // Prevent 100% CPU usage
					}
				}
				// Success!
				robotReady = true;
				System.out.println("Connected to robot!");
				connected = true;
				
				return true;
			} catch (NXTCommException e) {
//				throw new IOException("Failed to connect " + e.toString());
				System.out.println("Failed to connect (NXTComm) " + e.toString());
			} catch (InterruptedException e) {
//				throw new IOException("Failed to connect " + e.toString());
				System.out.println("Failed to connect (INTERR) " + e.toString());
			} catch (IOException e) {
				System.out.println("Failed to connect (IO) " + e.toString());
			}
		}
		
		// Failed to connect
		return false;
	}

	/**
	 * Closes the Bluetooth connection and closes the input and output streams
	 */
	public void closeBluetoothConnection() {
		try {
			connected = false;
			in.close();
			out.close();
			nxtComm.close();
			robotReady = false;
			
			System.out.println("Communication Closed!");
			
		} catch (IOException e) {
			System.err.println("Couldn't close Bluetooth connection: "
					+ e.toString());
		}
	}
	
	/**
	 * Send an integer to the robot
	 */
	public void sendToRobot(int command) {
		try {
			out.write(command);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
