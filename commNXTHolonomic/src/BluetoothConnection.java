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
    		} else if (i == 8 || i == 25) {
    			byte h = (byte)receiveIntSignal();
    			byte k = (byte)receiveIntSignal();
    			
    			int angle = ((int) h & 0xFF | (int) (k << 8));
    			robot.angle   = angle;	
    	    	robot.command = i;
    	    	
    	    } else if (i == 22) {
    	    	/* Rotate by a specific angle */

    	    	// Get angle lower 8 bits
    			byte angle2 = (byte)receiveIntSignal();
    			// Get angle higher 8 bits
    			byte angle1 = (byte)receiveIntSignal();

    			int angle = ((int) (angle1 << 8) | (int) angle2 & 0xFF);
    			
    			// Convert from [0, 360] to [-180, 180]
    			if (angle > 180) {
    				angle -= 360;
    			}
    			
    			robot.commandTime = System.currentTimeMillis();
    			robot.rotateAngle = angle;	

    	    	robot.command = 22;
    	    } else if (i == 11) {
    	    	robot.openGrabber();
    	    } else if (i == 10) {
    	    	robot.grab();
    	    } else if (i == 16) {
    	    	// Aiming can be done independently from movement
    	    	robot.aimLeft();
    	    } else if (i == 17) {
    	    	// Aiming can be done independently from movement
    	    	robot.aimRight();
    	    } else if (i == 18) {
    	    	// Aiming can be done independently from movement
    	    	robot.aimReset();
    	    } else if (i == 6) {
    	    	// Kicking is independent as well
				robot.fastKick();
    	    } else if (i == 23) {
    	    	// Kick then rotate LEFT
    	    	robot.fastKick();
    	    	robot.rotateTo(-45);
    	    } else if (i == 24) {
    	    	// Kick then rotate RIGHT
    	    	robot.fastKick();
    	    	robot.rotateTo(45);
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
