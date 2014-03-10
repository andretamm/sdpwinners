package sdp.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import sdp.common.RobotCommand;
import sdp.common.RotationDirection;


public class CommsClient implements CommsInterface {
	private static final int SERVER_PORT = 5678;
	//old milestone 1 port number was 6789
	private static final String SERVER_NAME = "localhost";
	
	private Socket mSocket = null;
	private DataOutputStream mConnectionOutputStream;
		
	public boolean connect() throws IOException{
		if ( mSocket == null ){
			System.out.println("Starting connect");
			mSocket = makeSocket();
			if ( mSocket == null ){
				return false;
			}
			System.out.println("Socket created");
			mConnectionOutputStream = new DataOutputStream(mSocket.getOutputStream());
			System.out.println("Connected");
		}
		return true;
	}
	
	private Socket makeSocket() throws UnknownHostException, IOException{
		Socket socket = null;
		int MAX_ATTEMPTS = 3;
		int attempts = 0;
		while ( attempts++ < MAX_ATTEMPTS ){
			System.out.println("Attempting to connect");
			try {
				socket = new Socket(SERVER_NAME, SERVER_PORT);
				socket.setSendBufferSize(1);
			} catch (IOException e) {
				if (e.getMessage().equals("Connection refused")) {
					if ( attempts == MAX_ATTEMPTS ){
						System.out.println("Max attempts reached");
						return null;
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					continue;
				}
			}
			break;
		}
		return socket;
	}

    public void close() throws IOException{
		mSocket.close();
    }
	
	public synchronized void move(double direction) throws IOException{
		mConnectionOutputStream.write((byte)RobotCommand.MOVE);
		mConnectionOutputStream.writeFloat((float)direction);
		mConnectionOutputStream.flush();
	}
	
	public synchronized void rotate(double angle, double weight) throws IOException{
		if ( angle < 0 ){
			rotate(RotationDirection.CLOCKWISE, weight);
		}else{
			rotate(RotationDirection.COUNTERCLOCKWISE, weight);
		}
	}
	
	public synchronized void rotate(RotationDirection direction, double weight) throws IOException{
		System.out.println("rotate");
		if ( direction == RotationDirection.CLOCKWISE ){
			mConnectionOutputStream.write((byte)RobotCommand.ROTATE_CW);
		}else{
			mConnectionOutputStream.write((byte)RobotCommand.ROTATE_CCW);
		}
		mConnectionOutputStream.writeFloat((float)Math.abs(weight));
		mConnectionOutputStream.flush();
	}
	
	public synchronized void stopMoving() throws IOException{
		mConnectionOutputStream.write((byte)RobotCommand.STOP_MOVE);
		mConnectionOutputStream.writeFloat((float)0);
		mConnectionOutputStream.flush();
		//System.out.println("stopMoving");
	}
	
	public synchronized void stopRotating() throws IOException{
		mConnectionOutputStream.write((byte)RobotCommand.STOP_ROTATE);
		mConnectionOutputStream.writeFloat((float)0);
		mConnectionOutputStream.flush();
		//System.out.println("stopRotating");
	}
	
	public synchronized void kick() throws IOException{
		mConnectionOutputStream.write((byte)RobotCommand.KICK);
		mConnectionOutputStream.writeFloat((float)5);
		mConnectionOutputStream.flush();
		System.out.println("kick");
	}
	
	public synchronized void exit() throws IOException{
		mConnectionOutputStream.write((byte)RobotCommand.EXIT);
		mConnectionOutputStream.writeFloat((float)0);
		mConnectionOutputStream.flush();
		System.out.println("exit");
	}

	public synchronized void setMaximumSpeed(int speed) throws IOException {
		if ( speed > 255 || speed < 0 ){
			throw new IllegalArgumentException("Speed cannot be greater than 255 or less than 0");
		}
		mConnectionOutputStream.write((byte)RobotCommand.MAX_MOVE_SPEED);
		mConnectionOutputStream.writeFloat((float)speed);
		mConnectionOutputStream.flush();
	}

}
