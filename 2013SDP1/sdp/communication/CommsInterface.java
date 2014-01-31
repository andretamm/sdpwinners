package sdp.communication;

import java.io.IOException;
import sdp.common.*;

public interface CommsInterface {
	public void move(double direction) throws IOException;
	
	public void rotate(double angle, double weight) throws IOException;
	public void rotate(RotationDirection direction, double weight) throws IOException;
	public void setMaximumSpeed(int speed) throws IOException;
	public void stopMoving() throws IOException;
	public void stopRotating() throws IOException;
	public void kick() throws IOException;
	public void exit() throws IOException;
	
	public boolean connect() throws IOException;
}
