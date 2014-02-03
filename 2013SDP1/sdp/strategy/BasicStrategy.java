package sdp.strategy;

import java.awt.Point;
import java.io.IOException;

import sdp.communication.CommsClient;
import sdp.communication.CommsInterface;
import sdp.navigation.AStarPathfinding;
import sdp.navigation.OriginalMovement;
import sdp.vision.RunVision;
import sdp.vision.Vision;
import sdp.vision.WorldState;

/**
 * The basic strategy work for a single robot on the field. 
 * 
 * @author Catalina Predoi
 * @author Simona Cartuta
 *
 */
public class BasicStrategy extends Thread {
/**
 * @author Joe Tam
 * @author Ben Ledbury
 * @author Matt
 * @author Lau
 * @author Martin
 *
 * TODO:
 * 1.	handle unknown ball locations, our robot or opponent's robot might be covering the ball (we wont
 * see this at the ends of the pitch because of the camera curvature
 *
 * 2.	"creep mode", keeps blocking opponent's shooting angle using strafing and closing in at the same time
 *
 * 3.	penalties stuff
 *
 * when
 ********** List of available commands
 *	Type 1: forward
 *	Type 2: backward
 *	Type 3: strafe left
 *	Type 4: strafe right
 *	Type 8: kick
 *	Type 9: forward and kick
 *
 *	COMMAND format: [command type,command value,angle to turn,way-point x,way-point y, speed]
 */

	//variables associated with Simulator
//	Simulation simulation;
	boolean simulated;
	//game play variables

	private long refreshInterval = 50;		//50 milliseconds
	private long movementCorrectionInterval = 180;
	private boolean finished = false;
	private int ourSide;
	private int oppSide;
	private boolean stateSwitchable = true;
	private boolean movementCorrection;
	private static CommandHelper commandHelper;
	//game play constants
	public final static int LEFT = 0;
	public final static int RIGHT = 1;
	public static int pitchWidth;
	public static int pitchHeight;
	public static int pitchMidLine;
	//objects coordinates and angles
	private int ballX;
	private int ballY;
	private int prevballX;
	private int prevballY;
	//system integration
	private int debug;
//	private Singleton singleton;
//	private ArrayList<int[]> commands = null;
	//variables related to strategy
	private boolean intercept = false;
	private Point ballPreviousPosition = null;
	//constants related to strategy
	private boolean interceptFinished = false;
	private boolean running = true;
	private Point ourPosition;
	private Point oppPosition;
	public static OriginalMovement m;
	//variables for goalMoves
	//Goal Left
//	static int goalStartY = 165;
//	static int goalEndY = 275;
//	static int goalLeftX = 50;
//	static int goalRightX = 700;
//	static int interval = 20;
	private Vision vision;
	private WorldState worldState;
	private CommsInterface c;
/*
	public BasicStrategy(WorldState worldState, boolean simulated, boolean movementCorrection, int side, int debug) throws IOException, InterruptedException {
		this.worldState = worldState;
		this.movementCorrection = movementCorrection;
//		pitchWidth = Vision.PITCH_END_X - Vision.PITCH_START_X;
//		pitchHeight = Vision.PITCH_END_Y - Vision.PITCH_START_Y;
		pitchMidLine = pitchWidth / 2;
//		commands = new ArrayList<int[]>();
		this.debug = debug;
		if (side == LEFT) {
			ourSide = LEFT;
			oppSide = RIGHT;
		} else {
			ourSide = RIGHT;
			oppSide = LEFT;
		}
		commandHelper = new CommandHelper();
		System.out.println("Created");
		this.simulated = simulated;
//		if (simulated == false) {
//			singleton = Singleton.getSingleton();
//		} else {
//			simulation = Simulation.getSimulation();
//		}
	}*/

	public static void main(String[] args) throws IOException, InterruptedException {
	    WorldState mWorldState = new WorldState();
	    Vision mVision = RunVision.setupVision(mWorldState);
	    //allow for vision setup
	    try {
	            Thread.sleep(2000);
	    } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    }
		CommsClient mComms = new CommsClient();
		mComms.connect();
		mComms.setMaximumSpeed(255);
		BasicStrategy s = new BasicStrategy(mVision, mWorldState, mComms);
//		s.run();
	/*	System.out.println("Should be going to ball now");
		try {
		s.commandHelper.goToBall();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		*/
		Thread.sleep(5000);
		s.run();
	}
	
	public BasicStrategy(Vision vision, WorldState worldState, CommsInterface c) {
		this.vision = vision;
		this.worldState = worldState;
		this.c = c;
	}
	
	public void run() {			
	}
		//commandHelper.faceBall(0.1, (float)0.2);
//		Pathfinding p = new Pathfinding(mVision.getImageProcessor(), mWorldState);
//		Movement m = new Movement(mWorldState, p, c, 40);
//		m.start();
//		m.setMoving(true);
//		while (true) {
//			while (mWorldState.getOurPosition().distance(mWorldState.getOppositionGoalCentre())>40) {
//				System.out.println("mWorldState.getBallPoint().distance(mWorldState.getOurPosition())="+mWorldState.getBallPoint().distance(mWorldState.getOurPosition()));
//				while (mWorldState.getBallPoint().distance(mWorldState.getOurPosition())>50) {
//					m.setTarget(KickFrom.whereToKickFrom(mWorldState));
//					sleep(500);
//				}
//				System.out.println("m.isAlive()="+m.isAlive());
//				if (!m.isAlive()) {
//					m = new Movement(mWorldState, p, c, 40);
//					m.start();
//					m.setMoving(true);
//				}
//				m.setTarget(mWorldState.getOppositionGoalCentre());
//				System.out.println("going to kick");
//				c.kick();
//				sleep(150);
//				System.out.println("just kicked");
//			}
//			if (!m.isAlive()) {
//				m = new Movement(mWorldState, p, c, 40);
//				m.start();
//				m.setMoving(true);
//			}
//			m.setTarget(new Point((int) (mWorldState.getOurGoalCentre().getX()*(3/4)+mWorldState.getOppositionGoalCentre().getX()*(1/4)),
//					(int) (mWorldState.getOurGoalCentre().getY()*(3/4)+mWorldState.getOppositionGoalCentre().getY()*(1/4))));
//			while (m.isAlive()) {
//				sleep(50);
//				System.out.println("going back to nice position");
//				if (mWorldState.getOurPosition().distance(new Point((int) (mWorldState.getOurGoalCentre().getX()*(3/4)+mWorldState.getOppositionGoalCentre().getX()*(1/4)),
//						(int) (mWorldState.getOurGoalCentre().getY()*(3/4)+mWorldState.getOppositionGoalCentre().getY()*(1/4))))<10)
//					continue;
//			}
//			System.exit(0);
//	}
			
			/*
			Pathfinding p = new Pathfinding(vision.getImageProcessor(), worldState);
			if (!m.isAlive()) {
				m = new Movement(worldState, p, c, 40);
				m.start();
				try {
					m.setMoving(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while (worldState.getOurPosition().distance(worldState.getOppositionGoalCentre())>40) {
				System.out.println("mWorldState.getBallPoint().distance(mWorldState.getOurPosition())="+worldState.getBallPoint().distance(worldState.getOurPosition()));
				while (worldState.getBallPoint().distance(worldState.getOurPosition())>50) {
					m.setTarget(KickFrom.whereToKickFrom(worldState));
					try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				p = new Pathfinding(vision.getImageProcessor(), worldState);
				System.out.println("m.isAlive()="+m.isAlive());
				if (!m.isAlive()) {
					m = new Movement(worldState, p, c, 40);
					m.start();
					try {
						m.setMoving(true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				m.setTarget(worldState.getOppositionGoalCentre());
				System.out.println("going to kick");
				try {
					c.kick();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					sleep(120);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("just kicked");
			}
			//CommandHelper.facePoint(mWorldState.getBallPoint(), Math.PI/32, c);
			try {
				CommandHelper.rotateToAngle(worldState, c, Position.angleTo(worldState.getOurPosition(), worldState.getBallPoint()));
			} catch (NoAngleException e) {	
				System.out.println("Couldn't calculate the angle between our position and the ball");
				e.printStackTrace();
			}
			p = new Pathfinding(vision.getImageProcessor(), worldState);
			if (!m.isAlive()) {
				m = new Movement(worldState, p, c, 40);
				m.start();
				try {
					m.setMoving(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			m.setTarget(new Point((int) ((worldState.getOurGoalCentre().getX()*(3/4))+(worldState.getOppositionGoalCentre().getX()*(1/4))),
					(int) ((worldState.getOurGoalCentre().getY()*(3/4))+(worldState.getOppositionGoalCentre().getY()*(1/4)))));
			while (m.isAlive()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		commandHelper.faceBall(0.1);
		System.exit(0);
		}*/
		/*
		Pathfinding p = new Pathfinding(mVision.getImageProcessor(), mWorldState);
		m = new Movement(mWorldState, p, c, 40);
		m.start();
		m.setMoving(true);
		while (true) {
			while (mWorldState.getOurPosition().distance(mWorldState.getOppositionGoalCentre())>40) {
				System.out.println("mWorldState.getBallPoint().distance(mWorldState.getOurPosition())="+mWorldState.getBallPoint().distance(mWorldState.getOurPosition()));
				while (mWorldState.getBallPoint().distance(mWorldState.getOurPosition())>50) {
					m.setTarget(KickFrom.whereToKickFrom(mWorldState));
					sleep(500);
				}
				p = new Pathfinding(mVision.getImageProcessor(), mWorldState);
				System.out.println("m.isAlive()="+m.isAlive());
				if (!m.isAlive()) {
					m = new Movement(mWorldState, p, c, 40);
					m.start();
					m.setMoving(true);
				}
				m.setTarget(mWorldState.getOppositionGoalCentre());
				System.out.println("going to kick");
				c.kick();
				sleep(120);
				System.out.println("just kicked");
			}
			//CommandHelper.facePoint(mWorldState.getBallPoint(), Math.PI/32, c);
			try {
				CommandHelper.rotateToAngle(worldState, c, Position.angleTo(mWorldState.getOurPosition(), mWorldState.getBallPoint()));
			} catch (NoAngleException e) {	
				System.out.println("Couldn't calculate the angle between our position and the ball");
				e.printStackTrace();
			}
			p = new Pathfinding(mVision.getImageProcessor(), mWorldState);
			if (!m.isAlive()) {
				m = new Movement(mWorldState, p, c, 40);
				m.start();
				m.setMoving(true);
			}
			m.setTarget(new Point((int) ((mWorldState.getOurGoalCentre().getX()*(3/4))+(mWorldState.getOppositionGoalCentre().getX()*(1/4))),
					(int) ((mWorldState.getOurGoalCentre().getY()*(3/4))+(mWorldState.getOppositionGoalCentre().getY()*(1/4)))));
			while (m.isAlive()) {
				sleep(30);
			}
		commandHelper.faceBall(0.1);
		System.exit(0);

	/**
	 ************************************************************************************************************
	 ******************** METHODS FOR CONTROLLING GAME FLOW AND INTEGRATION WITH SYSTEM OVERVIEW ****************
	 ************************************************************************************************************
	 */
	public void startMatch() {
		finished = false;
	}

	public void stopMatch() {
		finished = true;
	}

	public void startStrategy() {
		running = true;
	}

	public void stopStrategy() {
		running = false;
		sendStop();
	}


	public boolean isRunning() {
		return running;
	}

	/**
	 * Updates object coordinates.
	 */
	private void update() {
		prevballX = ballX;
		prevballY = ballY;
	
		ballX = worldState.getBallX();
		ballY = worldState.getBallY();
	}

	public void setSideLeft() {
		ourSide = LEFT;
		oppSide = RIGHT;
	}

	public void setSideRight() {
		ourSide = RIGHT;
		oppSide = LEFT;
	}

	public int getOurSide() {
		return ourSide;
	}

	public int getOppSide() {
		return oppSide;
	}
	//TODO Alter for Attacker Robot
	public double getOurOrientation() {
		return worldState.getOurDefenderOrientation();
	}

	/**
	 ***********************************************************************************************************
	 ************************************ MAIN STRATEGY CONTROL FLOW *******************************************
	 ***********************************************************************************************************
	 */
	/*
	@Override
	public void run() {

		while (!finished) {
			try {
				update();
				if (running) {
//					if (!movementCorrection) {
//						if (singleton.getWaiting()) {
//							computeStrategy();
//						}
//					} else {
//						computeStrategy();
//					}
				}
				Thread.sleep(refreshInterval);
//				if (!movementCorrection) {
//					Thread.sleep(refreshInterval);
//				} else {
//					Thread.sleep(movementCorrectionInterval);
//				}
			} catch (InterruptedException ex) {
				Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}*/

	public boolean canTurn() {
		return true;
	}

	public void play() {
		run();
	}

	private boolean isFacingBall(RobotTeam robotColor) {
			int angle = (int) (getAngle(robotColor) - Math.abs(Math.toDegrees(Math.atan2((worldState.getBallPoint().y - getPosition(robotColor).y),
					(worldState.getBallPoint().x - getPosition(robotColor).x)))));
			if (Math.abs(angle) < 20) {
				return true;
			}
		return false;
	}

	private boolean isNearBall(RobotTeam robotColor) {
		if (getPosition(robotColor).distance(worldState.getBallPoint()) < 100) {
			return true;
		}
		return false;
	}

	private boolean hasBall(RobotTeam robotColor) {
		//check the angle and distance between robot and the ball
		if (isFacingBall(robotColor)) {
			if (isNearBall(robotColor)) {
				return true;
			}
		}
		return false;
	}



	private boolean inOwnHalf(RobotTeam robotTeam) {
		//our robot
		if (robotTeam == RobotTeam.OURS) {
			if (ourSide == LEFT && getPosition(RobotTeam.OURS).x < pitchMidLine) {
				return true;
			} else if (ourSide == RIGHT && getPosition(RobotTeam.OURS).x > pitchMidLine) {
				return true;
			}
		} //opp robot
		else {
			if (oppSide == LEFT && getPosition(RobotTeam.OPPOSITION).x < pitchMidLine) {
				return true;
			} else if (oppSide == RIGHT && getPosition(RobotTeam.OPPOSITION).x > pitchMidLine) {
				return true;
			}
		}
		return false;
	}
	//TODO Alter for Attacker Robot
	Point getPosition(RobotTeam robotColor) {
		if (robotColor == RobotTeam.OPPOSITION) {
			return worldState.getOppositionDefenderPosition();
		} else {
			return worldState.getOurDefenderPosition();
		}
	}
	//TODO Alter for Attacker Robot
	private double getAngle(RobotTeam robotColor) {
		if (robotColor == RobotTeam.OPPOSITION) {
			return worldState.getOppositionDefenderOrientation();
		} else {
			return worldState.getOurDefenderOrientation();
		}
	}

	public void simulate() {
		update();
	}

	/**
	 * Gives the side the robot is facing
	 * 
	 * 
	 * @param angle
	 * @return
	 */
	private int facingSide(double angle) {
		if (angle > 270 || (angle < 90 && angle > -90) || angle < -270) {
			return RIGHT;
		}
		return LEFT;
	}

	private void turnAndkick(int[] pos, int ourSide, int ourAngle) throws IOException {
//		if (ourSide == RIGHT) {
//			// we are facing our own goal
//			if (ourAngle <= 90 || ourAngle >= 270) {
//				gotoBall();
//			} else {
//				print("angle to turn from turnAndKick " + pos[2], 2);
//				int[] turnCmd = new int[7];
//				turnCmd[0] = 1;									//command type, kick
//				turnCmd[1] = 0;								    //command value
//				turnCmd[2] = pos[2];							//angle to turn
//				turnCmd[3] = getPosition(RobotTeam.OURS).x;				//way-point x
//				turnCmd[4] = getPosition(RobotTeam.OURS).y;				//way-point y
//				turnCmd[5] = 700;								//speed
//				//slowly turn to the ball
//				turnCmd[6] = 200;                               //speed for turning
//				commands.clear();
//				commands.add(turnCmd);
//				print("turning!!", 2);
//				int[] kickCmd = new int[7];
//				kickCmd[0] = 9;									//command type, kick
//				kickCmd[1] = 900;								//command value
//				kickCmd[2] = 0;									//angle to turn
//				kickCmd[3] = 0;									//way-point x
//				kickCmd[4] = 0;									//way-point y
//				kickCmd[5] = 700;								//speed
//				kickCmd[6] = 700;								//speed for turning
//				commands.add(kickCmd);
//				print("kick!!", 2);
//			}
//			//else do nothing because we are not facing our goal
//		} else {
//			// our side is left and we are facing it
//			if (ourAngle > 90 && ourAngle < 270) {
//				gotoBall();
//			} else {
//				print("angle to turn from turnAndKick " + pos[2], 2);
//				int[] turnCmd = new int[7];
//				turnCmd[0] = 1;									//command type, kick
//				turnCmd[1] = 0;								    //command value
//				turnCmd[2] = pos[2];							//angle to turn
//				turnCmd[3] = getPosition(RobotTeam.OURS).x;				//way-point x
//				turnCmd[4] = getPosition(RobotTeam.OURS).y;				//way-point y
//				turnCmd[5] = 700;								//speed
//				//slowly turn to the ball
//				turnCmd[6] = 200;                               //speed for turning
//				commands.clear();
//				commands.add(turnCmd);
//				print("turning!!", 2);
//	
//				int[] kickCmd = new int[7];
//				kickCmd[0] = 9;									//command type, kick
//				kickCmd[1] = 900;								//command value
//				kickCmd[2] = 0;									//angle to turn
//				kickCmd[3] = 0;									//way-point x
//				kickCmd[4] = 0;									//way-point y
//				kickCmd[5] = 700;								//speed
//				kickCmd[6] = 700;								//speed for turning
//	
//				commands.add(kickCmd);
//				print("kick!!", 2);
//			}
//		}
	}

	private void sendStop(){

		

	}

	/**
	 * @return Midpoint of the opponent's goal.
	 */
	public Point getOppGoal() {
		Point goal = new Point();
		if (getOurSide() == RIGHT) {
			goal.setLocation(0, pitchHeight / 2);
		} else {
			goal.setLocation(pitchWidth, pitchHeight / 2);
		}
		return goal;
	}

	/**
	 * Distance from our robot to goal
	 * 
	 * @return
	 */
	    public double getDistanceFromGoal() {
	        return getPosition(RobotTeam.OURS).distance(getOppGoal());
	    }

	private void turnToGoal() {
		Point goalPoint = getOppGoal();
		Point ourPoint = getPosition(RobotTeam.OURS);
		int angleToTurn = (int) (Math.toDegrees(Math.atan2((ourPoint.y - goalPoint.y), (goalPoint.x - ourPoint.x))) - getAngle(RobotTeam.OURS));
		if (angleToTurn < -180) {
			angleToTurn = 360 + angleToTurn;
		}
		if (angleToTurn > 180) {
			angleToTurn = angleToTurn - 360;
		}
		int[] turnToGoalCmd = {1, 0, angleToTurn, ourPoint.x, ourPoint.y, 100, 100};
//		commands.add(turnToGoalCmd);
		print("STRATEGY: TURNING TO GOAL", 1);
	}

	/**
	 * Given an angle it checks whether we are facing the goal.
	 * @param angle
	 * @return NULL if we are not facing the goal, else returns the intersection.
	 */
	private boolean facingGoal(int angle) {
		if(facingSide(getAngle(RobotTeam.OURS)) == ourSide) {
			return false;
		} else {
			if (getAngle(RobotTeam.OURS) == Math.tan((getPosition(RobotTeam.OURS).y - getOppGoal().y)/Math.abs(getPosition(RobotTeam.OURS).y - getOppGoal().y))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Does a kick or move-and-kick based on the distance from the ball.
	 * @param distance
	 */
	private void kick(int dist, int wayX, int wayY) {
//		//if (getPosition(ourColor).distance(worldState.getBallPoint()) < 10) {
//		int moveThr = 50;
//		int[] kickCmd = new int[7];
//
//		if (dist > moveThr) {
//			kickCmd[0] = 9;		// type
//		} else {
//			kickCmd[0] = 8;		// type
//		}
//		kickCmd[1] = dist;		// move distance
//		kickCmd[2] = 0;			// rotate angle
//		kickCmd[3] = wayX;		// way-point x
//		kickCmd[4] = wayY;		// way-point y
//		kickCmd[5] = 700;		// move speed
//		kickCmd[6] = 0;			// rotate speed
//
//		commands.clear();
//		commands.add(kickCmd);
//		if (dist > moveThr) {
//			print("kick(): moving and kicking!", 2);
//		} else {
//			print("kick(): kicking!", 2);
//		}
		//}
	}

//	private void move(int type, int value, int angle, int speed) {
//		int[] cmd = new int[7];
//		cmd[0] = type;								//command type, kick
//		cmd[1] = value;								//command value
//		cmd[2] = angle;								//angle to turn
//		cmd[3] = 0;									//way-point x
//		cmd[4] = 0;									//way-point y
//		cmd[5] = speed;								//speed
//		cmd[6] = 0;									//speed for turning
//
//		commands.clear();
//		commands.add(cmd);
//		print("move!!", 2);
//	}

	/**
	 * Strafe left to score.
	 * @param dist - distance to move when strafing
	 * @param wayX - X coordinate of waypoint
	 * @param wayY - Y coordinate of waypoint
	 */
	private void strafeLeftGoal(int dist, int wayX, int wayY) {
//		int[] cmd = new int[7];
//		cmd[0] = 3;		// type
//		cmd[1] = dist;	// move distance
//		cmd[2] = 0;		// rotate angle
//		cmd[3] = wayX;	// way-point x
//		cmd[4] = wayY;	// way-point y
//		cmd[5] = 255;	// move speed
//		cmd[6] = 0;		// rotate speed
//
//		commands.clear();
//		commands.add(cmd);
//		print("strafeLeftGoal(): strafe left to score!", 2);
	}

	/**
	 * Strafe right to score.
	 * @param dist - distance to move when strafing
	 * @param wayX - X coordinate of waypoint
	 * @param wayY - Y coordinate of waypoint
	 */
	private void strafeRightGoal(int dist, int wayX, int wayY) {
//		int[] cmd = new int[7];
//		cmd[0] = 4;		// type
//		cmd[1] = dist;	// move distance
//		cmd[2] = 0;		// rotate angle
//		cmd[3] = wayX;	// way-point x
//		cmd[4] = wayY;	// way-point y
//		cmd[5] = 255;	// move speed
//		cmd[6] = 0;		// rotate speed
//
//		commands.clear();
//		commands.add(cmd);
//		print("strafeRightGoal(): strafe right to score!", 2);
	}

	private void shootPenalty() {
        // our strategy: wait 3 seconds, then kick with full power in front
        // we assume the opponent will be standing sideways so the ball
        // bounces back, we go to normal mode, have chances to get a ball
        // opponent still needs some time to turn.

//		if(!singleton.getMovement().isRunning()){
//			return;
//		}

//		stateSwitchable = false;
//
//        print("shooting penalty", 2);
//        commands.clear();
//        int[] kickCmd = {8, 900, 0, getPosition(RobotTeam.OURS).x, getPosition(RobotTeam.OURS).y, 700, 0};
//        commands.add(kickCmd);
//
//
//        try {
//			 print("IM SLEEPING", 2);
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        print("kick!!", 2);
//        commands.clear();
//
//        //wait one second for a ball to bounce off the opponent's side. then we switch to
//        // normal mode
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//		stateSwitchable = true;
//		
    }

//strafe right, left then kick
	private void shootPenalty2() {
//		print("shooting penalty", 1);
//		if (commands.size() > 0) {
//			commands.clear();
//		}
//			int[] strCmd = {3, 0, 0, getPosition(RobotTeam.OURS).x, getPosition(RobotTeam.OURS).y-15, 300, 200};
//			commands.add(strCmd);
//			int[] strCmd2 = {4, 0, -35, getPosition(RobotTeam.OURS).x, getPosition(RobotTeam.OURS).y+15, 0, 200};
//			commands.add(strCmd2);
//		int[] kickCmd = {8, 900, 0, getPosition(RobotTeam.OURS).x, getPosition(RobotTeam.OURS).y, 700, 900};
//		commands.add(kickCmd);
//		print("kick!!", 1);
//		//pause everything after kicking
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//switches back to normal mode
//		System.out.println("Im switching to normal mode");
//		stateSwitchable = true;
//		System.out.println("Switching done!!");
	}

	/**
	 * Helps print messages
	 * @param msg
	 * @param msgLevel
	 */
	private void print(String msg, int msgLevel) {
		if (msgLevel <= debug && debug > 0) {
			System.out.println("STRATEGY >> " + msg);
		}
	}

	/**
         * This function should make our robot to defend from a goal.
         * 
         * @param ourSide
         */
	public void blockOpp(int ourSide) {
		//check if the opponent is closer to our goal than us
		// if closer- we need some immediate movements, preferably only one - strafe, diagonal etc
		Point aim;
		boolean closer = false;
		if (ourSide == LEFT) {
			if (getPosition(RobotTeam.OURS).x > getPosition(RobotTeam.OPPOSITION).x) {
				closer = true;
			}
		} else if (ourSide == RIGHT) {
			if (getPosition(RobotTeam.OURS).x < getPosition(RobotTeam.OPPOSITION).x) {
				closer = true;
			}
		}
		if (!closer) { 	//we are closer to our goal, there's a bigger chance to defend it!!
			if (facingSide(getAngle(RobotTeam.OPPOSITION)) == getOurSide()) {
				//run, we still can defend it!! maybe one move only?
				aim = new Point(getPosition(RobotTeam.OURS).x, worldState.getBallPoint().y);
				System.out.println("must try to defend");
			} else {
				// opponent has a ball, but is not facing our goal, we might have more time to
				// defend it by blocking/strafing
				//go to same y 
				aim = new Point(getPosition(RobotTeam.OURS).x, worldState.getBallPoint().y);
				System.out.println("must try to defend");
			}
		} else {
			//they're facing the goal and are closer - uupsss
			System.out.println("They might score");
		}
	}
}
