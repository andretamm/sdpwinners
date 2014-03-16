package behavior;

/**
 * Class for keeping track of the state of the robot
 * @author Andre
 *
 */
public class RobotState {
	// Variables for keeping track of the state of the robot
	public boolean isRotating = false;
	public boolean isMoving = false;
	public boolean isMovingUp = false;
	public boolean isMovingDown = false;
	public boolean isAimingLeft = false;
	public boolean isAimingRight = false;
	
	public int grabberState = 0;
}
