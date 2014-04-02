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
	
	/** How many different targets has the attacker tried */
	public int attackerNumberOfTargetsTried = 0;
	
	/** How many different targets the defender has tried */
	public int defenderNumberOfTargetsTried = 0;
	
	/** If the attacker has rotated the right way to catch a pass */
	public boolean attackerOrientationSetForPass = false;
	
	/**
	 * Different states for the grabber of this robot.
	 * 0 - opened (or so we think <.<)
	 * 1 - (possibly) closed, used e.g. when we're about to grab the ball
	 *     so we can later open the grabber just in case even if we didn't
	 *     actually close the grabber.
	 */
	public int grabberState = 0;
}
