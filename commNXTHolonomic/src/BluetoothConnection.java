import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;


public class BluetoothConnection extends Thread {
	
	private static final byte ROBOT_READY = 0;
	
    private static InputStream in;
    private static OutputStream out;
    
    private RobotController robot;
	
	@Override
	public void run() {
		while(true) {
    		int i = receiveIntSignal();
    		   		
    		if (i == 7) {
    			// Signal to restart communication, tell robot to stop first
    			robot.command = 3;
    			establishConnection();
    	    	sendReadySignal();
    		} if (i == 8) {
    	    	byte h = (byte)receiveIntSignal();
    	    	byte k = (byte)receiveIntSignal();
    	    	
    	    	int angle = ((int) h & 0xFF | (int) (k << 8));
    	    	System.out.println("Angle is " + angle);
    			robot.angle   = angle;	
    	    	robot.command = 8;
    	    }
    		else {
    			// Pass on command to robot
    			robot.command = i;
    		}
    	}
	}
	
	public BluetoothConnection(RobotController robot) {
		this.robot = robot;
		establishConnection();
    	sendReadySignal();
	}
	
	/**
     * Create a bluetooth connection to PC
     */
    public static void establishConnection() {
        System.out.println("Waiting for Bluetooth connection...");
        NXTConnection connection = Bluetooth.waitForConnection();
        
        if ( connection == null ){
        	System.out.println("Failed to establish connection");
        	establishConnection();
        	return;
        }
        in = connection.openInputStream();
        out = connection.openOutputStream();
        System.out.println("Connection established!");
    }

    /**
     * Send a byte to PC
     * @param b - a byte of data sending to PC
     */
    public static void sendByte(byte b) {
        try {
            System.out.println("sending byte: " + b);
            out.write(b);
            out.flush();
        } catch (IOException e) {
            System.out.println("Couldn't send byte: " + e.toString());
        }
    }

    /**
     * Send 0 to PC indicating that robot is ready
     */
    public void sendReadySignal() {
        sendByte(ROBOT_READY);
        System.out.println("sending ready signal and initializing robot");
        // Todo - make this be smth other than STOP, so make it actually initialise something
        robot.command = 3;
    }
    
    /*
     * Receive an integer from PC.
     */
    public static int receiveIntSignal() {
    	int i = 0;
    	try {
			i = in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
    	
    }
}
