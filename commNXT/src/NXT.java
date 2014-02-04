
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class NXT {
    private static final byte ROBOT_READY = 0;
    	
    private static InputStream in;
    private static OutputStream out;
    
    private static Robot robot;

    public static void main(String[] args){
    	robot = new Robot();
    	establishConnection();
    	sendReadySignal();
    	while(true){
    		int i = receiveIntSignal();
    		switch (i) {
    			case 0:
    				robot.fail();
    			case 1:
    				robot.kick();
    		}    			
    	}
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
    public static void sendReadySignal() {
        sendByte(ROBOT_READY);
        System.out.println("sending ready signal and initializing robot");
        robot.init();
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