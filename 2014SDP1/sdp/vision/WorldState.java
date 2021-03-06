package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;

import behavior.StrategyHelper;
import common.Robot;
import common.RobotMap;
import constants.Quadrant;
import constants.QuadrantX;
import constants.RobotColour;
import constants.RobotType;
import constants.ShootingDirection;


public class WorldState implements VisionInterface {
	
	public Point andresPoint = new Point(0,0);
	
	public static final int HISTORY_LENGTH = 5;
	public static final int ORIENTATION_HISTORY_LENGTH = 2; // Do NOT change this unless you
															// change how the orientation is average
															// (that's hard!)

	private ShootingDirection direction; // 0 = right, 1 = left.
	private RobotColour ourColour;
	private int pitch; // 0 = main, 1 = side room

	// Variables for calculating the velocity and location of the robots and ball
	private RobotMap<Point> robotPosition; // Positions of all the robots
	private RobotMap<long[]> robotTimestamps; // Timestamps for all the positions <.<

	public int ballX;
	public int ballY;
	
	// Defender goDiagonallyTo target
	private int defenderGoDiagonallyToX = 0;
	private int defenderGoDiagonallyToY = 0;
	
	// Defender goTo target and safe target (used for testing safe point)
	private int defenderGoToX = 0;
	private int defenderGoToY = 0;
	private int defenderGoToSafeX = 0;
	private int defenderGoToSafeY = 0;
	
	public int getDefenderGoToX() {
		return defenderGoToX;
	}

	public void setDefenderGoToX(int defenderGoToX) {
		this.defenderGoToX = defenderGoToX;
	}

	public int getDefenderGoToY() {
		return defenderGoToY;
	}

	public void setDefenderGoToY(int defenderGoToY) {
		this.defenderGoToY = defenderGoToY;
	}

	public int getDefenderGoToSafeX() {
		return defenderGoToSafeX;
	}

	public void setDefenderGoToSafeX(int defenderGoToSafeX) {
		this.defenderGoToSafeX = defenderGoToSafeX;
	}

	public int getDefenderGoToSafeY() {
		return defenderGoToSafeY;
	}

	public void setDefenderGoToSafeY(int defenderGoToSafeY) {
		this.defenderGoToSafeY = defenderGoToSafeY;
	}

	public int getDefenderGoDiagonallyToX() {
		return defenderGoDiagonallyToX;
	}

	public void setDefenderGoDiagonallyToX(int defenderGoDiagonallyToX) {
		this.defenderGoDiagonallyToX = defenderGoDiagonallyToX;
	}

	public int getDefenderGoDiagonallyToY() {
		return defenderGoDiagonallyToY;
	}

	public void setDefenderGoDiagonallyToY(int defenderGoDiagonallyToY) {
		this.defenderGoDiagonallyToY = defenderGoDiagonallyToY;
	}
	
	private RobotMap<Double> robotOrientation; // Orientations of all the robots
	private RobotMap<Point2D.Double> robotOrientationVector; // Orientation as a vector

	private long counter;
	private boolean subtractBackground;
	private boolean findRobotsAndBall; //used when setting thresholds. Disables filtering, clustering etc so
	//as to enable fast response even when the whole view tests positive for ball or robot
	private boolean showNoDistortion; //Corrects the initial image displayed. The output coords should always be corrected anyway
	private boolean normaliseRGB; //whether to normalise each RGB vector before processing

	//these two are not barrelcorrected
	private Point outerPitchTopLeft1 = new Point(11, 78);
	private Point outerPitchBottomRight1 = new Point(630, 396);

	//these two are already barrelcorrected
	private Point pitchTopLeft1 = new Point(75, 53);
	private Point pitchBottomRight1 = new Point(588, 350);

	private Point outerPitchTopLeft2 = new Point(5, 68);
	private Point outerPitchBottomRight2 = new Point(634,400);

	//these two are already barrelcorrected
	private Point pitch2TopLeft = new Point(60, 90);
	private Point pitch2BottomRight = new Point(579,388);

	//Quadrant low/high X values
	private int q1LowX;
	private int q1HighX;
	private int q2LowX;
	private int q2HighX;
	private int q3LowX;
	private int q3HighX;
	private int q4LowX;
	private int q4HighX;

	public static final int ballRadius = 12;
	public static final int plateRadius = 15;

	public static double cmToPixels = 0.38;

	//TODO Alter history and velocity for two robots
	
	private Point[] ballHistory;
	private long[] ballTimeStamps;
	private Point2D.Double ballVelocity;
	
	private RobotMap<Point[]> robotHistory;
	private RobotMap<Point2D.Double> robotVelocity;
	private RobotMap<Double[]> robotOrientationHistory;
	private RobotMap<Boolean> robotGrabbedBall;

	private boolean removeShadows = false;
	
	/*---------------------------------------------*/
	/*   Variables for the strategy system         */
	/*---------------------------------------------*/
	/** If the defender is making a pass to the attacker */
	boolean doingPass;
	
	/** If the defender has kicked the ball in the pass */
	boolean kickedPass;
	
	/** Where the attacker should go to catch the pass */
	Point attackerPassPosition;
	
	/*---------------------------------------------*/
	/* Getter and Setter methods for the quadrants */
	/*---------------------------------------------*/
	public int getQ1LowX(){
		return q1LowX;
	}
	
	public void setQ1LowX(int q1LowX) {
		this.q1LowX = q1LowX;
	}
	
	public int getQ1HighX(){
		return q1HighX;
	}
	
	public void setQ1HighX(int q1HighX) {
		this.q1HighX = q1HighX;
	}
	
	public int getQ2LowX(){
		return q2LowX;
	}
	
	public void setQ2LowX(int q2LowX) {
		this.q2LowX = q2LowX;
	}
	
	public int getQ2HighX(){
		return q2HighX;
	}
	
	public void setQ2HighX(int q2HighX) {
		this.q2HighX = q2HighX;
	}
	
	public int getQ3LowX(){
		return q3LowX;
	}
	
	public void setQ3LowX(int q3LowX) {
		this.q3LowX = q3LowX;
	}
	
	public int getQ3HighX(){
		return q3HighX;
	}
	
	public void setQ3HighX(int q3HighX) {
		this.q3HighX = q3HighX;
	}
	
	public int getQ4LowX(){
		return q4LowX;
	}
	
	public void setQ4LowX(int q4LowX) {
		this.q4LowX = q4LowX;
	}
	
	public int getQ4HighX(){
		return q4HighX;
	}
	
	public void setQ4HighX(int q4HighX) {
		this.q4HighX = q4HighX;
	}
	
	public Point getOuterPitchTopLeft() {
		return pitch == 0 ? outerPitchTopLeft1 : outerPitchTopLeft2;
	}

	void setOuterPitchTopLeft(Point outerPitchTopLeft) {
		this.outerPitchTopLeft1 = outerPitchTopLeft;
	}

	public Point getOuterPitchBottomRight() {
		return pitch == 0 ? outerPitchBottomRight1 : outerPitchBottomRight2;
	}

	void setOuterPitchBottomRight(Point outerPitchBottomRight) {
		this.outerPitchBottomRight1 = outerPitchBottomRight;
	}

	void setOuterPitchTopLeft2(Point outerPitchTopLeft2) {
		this.outerPitchTopLeft2 = outerPitchTopLeft2;
	}

	void setOuterPitchBottomRight2(Point outerPitchBottomRight2) {
		this.outerPitchBottomRight2 = outerPitchBottomRight2;
	}

	Point getOuterPitchTopRight() {
		return new Point((int) getOuterPitchBottomRight().getX(), (int) getOuterPitchTopLeft().getY());
	}

	Point getOuterPitchBottomLeft() {
		return new Point((int) getOuterPitchTopLeft().getX(), (int) getOuterPitchBottomRight().getY());
	}

	public void setRemoveShadows(boolean removeShadows) {
		this.removeShadows = removeShadows;
	}

	public boolean getRemoveShadows() {
		return new Boolean(this.removeShadows);
	}

	private Point oppositionDefenderVelocity;
	private Point oppositionAttackerVelocity;

	//horrible hack:
	private double targetAngle;  //the destination angle
	private double correctedAngle; //the corrected angle, aim for this
	private boolean showDrawables;
	private boolean ballVisible;
	private int goalHeight=70; //the full length of the goal

	public WorldState() {

		/* control properties */
		this.direction = ShootingDirection.RIGHT;
		this.ourColour = RobotColour.YELLOW;
		this.pitch = 0;

		//TODO Alter object properties for velocities, history
		
		/* object properties */
		this.robotOrientation = new RobotMap<Double>(0.0);
		this.robotOrientationVector = new RobotMap<Point2D.Double>(new Point2D.Double(0.0, 0.0));
		this.robotPosition = new RobotMap<Point>();
		this.robotHistory = new RobotMap<Point[]>();
		this.robotVelocity = new RobotMap<Point2D.Double>();
		this.robotTimestamps = new RobotMap<long[]>();	
		this.robotOrientationHistory = new RobotMap<Double[]>();
		this.robotGrabbedBall = new RobotMap<Boolean>(false);
		
		// Set default values for all the robots
		for (Robot r: Robot.listAll()) {
			robotPosition.put(r, new Point(0,0));
			
			robotVelocity.put(r, new Point2D.Double(0,0));
			
			// History values
			// TODO MAKE SURE THESE ARE RIGHT AND MAKE SENSE
			Point[] history = new Point[HISTORY_LENGTH];
			long[] timestamps = new long[HISTORY_LENGTH];
			Double[] orientationHistory = new Double[ORIENTATION_HISTORY_LENGTH];
				
			for (int i = 0; (i < HISTORY_LENGTH); i++) {
				history[i] = new Point(1,1);
				timestamps[1] = 1;
			}
			
			for (int i = 0; (i < ORIENTATION_HISTORY_LENGTH); i++) {
				orientationHistory[i] = 0.0;
			}
			
			robotHistory.put(r, history);
			robotTimestamps.put(r, timestamps);
			robotOrientationHistory.put(r, orientationHistory);
		}
		
		this.ballX = 0;
		this.ballY = 0;
		this.setBallVisible(false);
		this.setSubtractBackground(false);
		this.setFindRobotsAndBall(true);
		this.setShowNoDistortion(false);
		this.setNormaliseRGB(false);
		this.setShowDrawables(true);
		this.setRemoveShadows(false);
		this.ballVelocity = new Point2D.Double(1,0);
		this.ballHistory = new Point[5];
		this.ballTimeStamps = new long[5];
		
		this.oppositionDefenderVelocity = new Point(0,0);
		this.oppositionAttackerVelocity = new Point(0,0);


		for (int i = 0; (i < HISTORY_LENGTH); i++) {
			this.ballHistory[i] = new Point(1,1);
			this.ballTimeStamps[i] = 1;
		}
		
		// Initialise strategy variables
		this.doingPass = false;
		this.kickedPass = false;
		this.attackerPassPosition = null;
	}

	public boolean getShowDrawables() {
		return showDrawables;
	}

	public void setShowDrawables(boolean showDrawables) {
		this.showDrawables = showDrawables;
	}

	public double getTargetAngle() {
		return targetAngle;
	}

	public void setTargetAngle(double targetAngle) {
		this.targetAngle = targetAngle;
	}

	public double getCorrectedAngle() {
		return correctedAngle;
	}

	public void setCorrectedAngle(double correctedAngle) {
		this.correctedAngle = correctedAngle;
	}

	public int getBallX() {
		return ballX;
	}

	public int getBallY() {
		return ballY;
	}
	
	
	/*-------------------------------------------------------------------*/
	/*-----Getters and setters for robot positions and orientation-------*/
	/*-------------------------------------------------------------------*/
	public void setRobotOrientation(RobotType rType, RobotColour rColour, double orientation) {		
		robotOrientation.put(new Robot(rColour, rType), orientation);
	}
	
	public double getRobotOrientation(RobotType rType, RobotColour rColour) {
		return robotOrientation.get(new Robot(rColour, rType));
	}
	
	public double getRobotOrientation(Robot r) {
		return robotOrientation.get(r);
	}
	
	public Point2D.Double getRobotOrientationVector(Robot r) {
		return robotOrientationVector.get(r);
	}

	public void setRobotOrientationVector (Robot r, Point2D.Double value) { 
		this.robotOrientationVector.put(r, value);
	}

	// New position getters/setters
	public int getRobotX(Robot r) {
		return robotPosition.get(r).x;
	}
	
	public int getRobotY(Robot r) {
		return robotPosition.get(r).y;
	}
	
	public Point getRobotPoint(Robot r) {
		return robotPosition.get(r);
	}
	
	public void setRobotX(Robot r, int x) {
		robotPosition.get(r).x = x;
	}
	
	public void setRobotY(Robot r, int y) {
		robotPosition.get(r).y = y;
	}

	public void setBallX(int ballX) {
		this.ballX = ballX;
	}

	public void setBallY(int ballY) {
		this.ballY = ballY;
	}	
	
	/*-------------------------------------------------------------------*/
	/*--End of getters and setters for robot positions and orientation---*/
	/*-------------------------------------------------------------------*/

	
	public ShootingDirection getDirection() {
		return direction;
	}

	public void setDirection(ShootingDirection direction) {
		this.direction = direction;
	}

	public RobotColour getColour() {
		return ourColour;
	}

	public void setColour(RobotColour colour) {
		this.ourColour = colour;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public void updateCounter() {
		this.counter++;
	}

	public long getCounter() {
		return this.counter;
	}

	public boolean subtractBackground() {
		return subtractBackground;
	}

	public void setSubtractBackground(boolean subtractBackground) {
		this.subtractBackground = subtractBackground;
	}

	public boolean isFindRobotsAndBall() {
		return findRobotsAndBall;
	}

	public void setFindRobotsAndBall(boolean findRobotsAndBall) {
		this.findRobotsAndBall = findRobotsAndBall;
	}

	public boolean isShowNoDistortion() {
		return showNoDistortion;
	}

	public void setShowNoDistortion(boolean showNoDistortion) {
		this.showNoDistortion = showNoDistortion;
	}
	
	//TODO fix getters for X and Y Vision for two robots

	int getOurDefenderXVision(){
		return getRobotX(new Robot(ourColour, RobotType.DEFENDER));
	}

	int getOurDefenderYVision(){
		return getRobotY(new Robot(ourColour, RobotType.DEFENDER));
	}
	
	int getOurAttackerXVision(){
		return getRobotX(new Robot(ourColour, RobotType.ATTACKER));
	}

	int getOurAttackerYVision(){
		return getRobotY(new Robot(ourColour, RobotType.ATTACKER));
	}

	public int getOurDefenderX(){
		return getOurDefenderXVision() - getPitchTopLeft().x;
	}
	
	public int getOurAttackerX(){
		return getOurAttackerXVision() - getPitchTopLeft().x;
	}

	public int getOurDefenderY(){  
		return getOurDefenderYVision() - getPitchTopLeft().y;
	}
	
	public int getOurAttackerY(){  
		return getOurAttackerYVision() - getPitchTopLeft().y;
	}

	public double getOurDefenderOrientation() {
		return robotOrientation.get(new Robot(ourColour, RobotType.DEFENDER));
	}
	
	public double getOurAttackerOrientation() {
		return robotOrientation.get(new Robot(ourColour, RobotType.ATTACKER));
	}

	public double getOppositionDefenderOrientation() {
		return robotOrientation.get(new Robot(getOppositionColour(), RobotType.DEFENDER));
	}
	
	public double getOppositionAttackerOrientation() {
		return robotOrientation.get(new Robot(getOppositionColour(), RobotType.ATTACKER));
	}

	public int getOppositionDefenderX() {
		return getOppositionDefenderXVision() - getPitchTopLeft().x;
	}

	public int getOppositionDefenderY() {
		return getOppositionDefenderYVision() - getPitchTopLeft().y;
	}
	
	public int getOppositionAttackerX() {
		return getOppositionAttackerXVision() - getPitchTopLeft().x;
	}

	public int getOppositionAttackerY() {
		return getOppositionAttackerYVision() - getPitchTopLeft().y;
	}

	public void setNormaliseRGB(boolean normaliseRGB) {
		this.normaliseRGB = normaliseRGB;
	}

	public boolean getNormaliseRGB() {
		return normaliseRGB;
	}

	public static double pixelToCm(double pixels) {
		return (pixels*cmToPixels);
	}

	public void setPitchTopLeft(Point pitchTopLeft) {
		this.pitchTopLeft1 = pitchTopLeft;
	}

	public Point getPitchTopLeft() {
		return pitch == 0 ? pitchTopLeft1 : pitch2TopLeft;
	}

	public Point getPitchTopRight() {
		return new Point((int) getPitchBottomRight().getX(), (int) getPitchTopLeft().getY());
	}

	public Point getPitchBottomLeft() {
		return new Point((int) getPitchTopLeft().getX(), (int) getPitchBottomRight().getY());
	}

	public void setPitchBottomRight(Point pitchBottomRight) {
		this.pitchBottomRight1 = pitchBottomRight;
	}

	public Point getPitchBottomRight(){
		return pitch == 0 ? pitchBottomRight1 : pitch2BottomRight;
	}

	public double getPitchHeight() {
		return getPitchBottomRight().getY()-getPitchTopLeft().getY();
	}

	public double getPitchWidth() {
		return getPitchBottomRight().getX()-getPitchTopLeft().getX();
	}

	public Point getLeftGoalPoint() {
		return new Point((int) getPitchTopLeft().getX(), (getPitchBottomRight().y + getPitchTopLeft().y) / 2);
	}

	public Point getRightGoalPoint() {
		return new Point((int) getPitchBottomRight().getX(), (getPitchBottomRight().y + getPitchTopLeft().y) / 2);
	}

	public Point getOurGoalCentre() {
		return direction == ShootingDirection.RIGHT ? getLeftGoalPoint() : getRightGoalPoint();
	}

	public Point getOppositionGoalCentre() {
		return direction == ShootingDirection.LEFT ? getLeftGoalPoint() : getRightGoalPoint();
	}

	public Point getOurGoalTop() {
		Point centre = getOurGoalCentre();
		return new Point((int) centre.getX(), (int) (centre.getY()-goalHeight));
	}

	public Point getOurGoalBottom() {
		Point centre = getOurGoalCentre();
		return new Point((int) centre.getX(), (int) (centre.getY()+goalHeight));
	}

	public Point getOppositionGoalTop() {
		Point centre = getOppositionGoalCentre();
		return new Point((int) centre.getX(), (int) (centre.getY()-goalHeight));
	}

	public Point getOppositionGoalBottom() {
		Point centre = getOppositionGoalCentre();
		return new Point((int) centre.getX(), (int) (centre.getY()+goalHeight));
	}
	
	/**
	 * Gets the Point with the ball x,y coordinates
	 */
	public Point getBallPoint() {
		return new Point(ballX, ballY);
	}
	
	/**
	 * Get the quadrant the ball is currently in
	 * @return The Quadrant or null if the ball is not on the pitch
	 */
	public Quadrant getBallQuadrant() {
		// Check if we're out the y values of the pitch
		if (!(ballY >= getPitchTopLeft().y && ballY <= getPitchBottomRight().y)) {
			return null;
		}
		
		// Manually check x values of all quadrants		
		if (ballX >= q1LowX && ballX <= q1HighX) {
			return Quadrant.Q1;
		} else if (ballX >= q2LowX && ballX <= q2HighX) {
			return Quadrant.Q2;
		} else if (ballX >= q3LowX && ballX <= q3HighX) {
			return Quadrant.Q3;
		} else if (ballX >= q4LowX && ballX <= q4HighX) {
			return Quadrant.Q4;
		} 
		
		// Ball must not be on the pitch
		return null;
	}

	public Point getOurDefenderPosition(){
		return new Point(getOurDefenderX(), getOurDefenderY());
	}
	
	public Point getOurAttackerPosition(){
		return new Point(getOurAttackerX(), getOurAttackerY());
	}

	public Point getOppositionDefenderPosition(){
		return getRobotPoint(getOpposition(RobotType.DEFENDER));
	}
	
	public Point getOppositionAttackerPosition(){
		return getRobotPoint(getOpposition(RobotType.ATTACKER));
	}

	public void setBallVisible(boolean ballVisible) {
		this.ballVisible = ballVisible;
	}

	public boolean getBallVisible() {
		return ballVisible;
	}
	
	public Point[] getBallHistory() {
		return ballHistory;
	}
	
	/**
	 * Get our robot
	 * @param type Type of robot
	 */
	public Robot getOur(RobotType type) {
		return new Robot(ourColour, type);
	}
	
	/**
	 * Get opposition's robot
	 * @param type Type of robot
	 */
	public Robot getOpposition(RobotType type) {
		return new Robot(getOppositionColour(), type);
	}
	
	public Point getOppositionDefenderVelocity() {
		return oppositionDefenderVelocity;
	}
	
	public Point getOppositionAttackerVelocity() {
		return oppositionAttackerVelocity;
	}

	public long[] getBallTimes() {
		return ballTimeStamps;
	}
	
	public long[] getRobotTimestamps(Robot r) {
		return robotTimestamps.get(r);
	}
	
	public void setRobotTimestamps(Robot r, long[] timestamps) {
		robotTimestamps.put(r, timestamps);
	}

	public void setBallVelocity(Point2D.Double bv) {
		this.ballVelocity=bv;
	}

	public void setBallHistory(Point[] bh) {
		this.ballHistory=bh;
	}
	
	public void setBallTimes(long[] bt) {
		this.ballTimeStamps=bt;
	}

	//TODO Alter for getOurAttackerPosition
	
	/**
	 *  If we can't see the opposition robot, it may be off the pitch. This method provides the location in this case.
	 * @param robot The colour of the robot for which a position should be provided
	 * @return The default location of the off-pitch robot
	 */
	Point getDefaultPoint(RobotColour robot) {
		if (robot==ourColour) {
			// if the robot is ours, just make the best on pitch guess
			return new Point((int) (getPitchTopLeft().getX()+getOurDefenderPosition().getX()), (int) (getPitchTopLeft().getY()+getOurDefenderPosition().getY()));
		} else {
			// if the robot is opposition, assume it's off the pitch
			if (direction == ShootingDirection.RIGHT) {
				return new Point((int) (getPitchTopLeft().getX()+getOurGoalCentre().getX()-50), (int) (getPitchTopLeft().getY()+getOurGoalCentre().getY()));
			} else {
				return new Point((int) (getPitchTopLeft().getX()+getOurGoalCentre().getX()+50), (int) (getPitchTopLeft().getY()+getOurGoalCentre().getY()));
			}
		}
	}
	
	/**
	 * @return Colour of the opposition
	 */
	private RobotColour getOppositionColour() {
		return ourColour == RobotColour.YELLOW ? RobotColour.BLUE : RobotColour.YELLOW;
	}
	
	int getOppositionDefenderXVision(){
		return getRobotX(new Robot(getOppositionColour(), RobotType.DEFENDER));
	}

	int getOppositionDefenderYVision(){
		return getRobotY(new Robot(getOppositionColour(), RobotType.DEFENDER));
	}
	
	int getOppositionAttackerXVision(){
		return getRobotX(new Robot(getOppositionColour(), RobotType.ATTACKER));
	}

	int getOppositionAttackerYVision(){
		return getRobotY(new Robot(getOppositionColour(), RobotType.ATTACKER));
	}
	
	int getShadowTopY() {
		if (pitch==0) {
			return (int) getPitchTopLeft().getY()+4;
		} else {
			return (int) getPitchTopLeft().getY()+32;
		}
	}

	int getShadowBottomY() {
		if (pitch==0) {
			return (int) getPitchBottomRight().getY()-11;
		} else {
			return (int) getPitchBottomRight().getY()-31;
		}
	}
			
	public Point getDefendPenaltyPoint() {
		double ratio1=3.25;
		double ratio2=1;
		return new Point((int) (((getOurGoalCentre().getX()*ratio1)+(getOppositionGoalCentre().getX()*ratio2))/(ratio1+ratio2)),
						(int) (((getOurGoalCentre().getY()*ratio1)+(getOppositionGoalCentre().getY()*ratio2))/(ratio1+ratio2)));
	}


	public Point getRobotXY(RobotColour colour, RobotType type) {
		Robot r = new Robot(colour, type);
		return new Point(getRobotX(r), getRobotY(r));
	}

	
	public Point getBallXY() {
		return new Point(getBallX(), getBallY());
	}

	
	public int getQuadrantX(Quadrant quadrant, QuadrantX quadrantX) {
		switch (quadrant) {
			case Q1:
				return quadrantX == QuadrantX.LOW ? getQ1LowX() : getQ1HighX();
			case Q2:
				return quadrantX == QuadrantX.LOW ? getQ2LowX() : getQ2HighX();
			case Q3:
				return quadrantX == QuadrantX.LOW ? getQ3LowX() : getQ3HighX();
			case Q4:
				return quadrantX == QuadrantX.LOW ? getQ4LowX() : getQ4HighX();
			default:
				return getQ1LowX();
		}
	}
	
	/**
	 * Returns the point in the middle of the quadrant
	 * @param q Quadrant we want
	 * @return Point(x,y)
	 */
	public Point getQuadrantMiddlePoint(Quadrant q) {
		int quadrantMiddleX = (getQuadrantX(q, QuadrantX.LOW) + getQuadrantX(q, QuadrantX.HIGH)) / 2 ;
		int quadrantMiddleY = (getPitchTopLeft().y + getPitchBottomRight().y) / 2;
		
		return new Point(quadrantMiddleX, quadrantMiddleY);
	}
	
	// Velocities
	public Point2D.Double getBallVelocity() {
		return ballVelocity;
	}
	
	public Point2D.Double getRobotVelocity(RobotColour colour, RobotType type) {
		return getRobotVelocity(new Robot(colour, type));
	}
	
	public Point2D.Double getRobotVelocity(Robot r) {
		return robotVelocity.get(r);
	}
	
	public void setRobotVelocity(Robot r, Point2D.Double v) {
		this.robotVelocity.put(r, v);
	}

	// Histories
	public Point[] getRobotHistory(RobotColour colour, RobotType type) {
		return getRobotHistory(new Robot(colour, type));
	}
	
	public Point[] getRobotHistory(Robot r) {
		return robotHistory.get(r);
	}
	
	public void setRobotHistory(Robot r, Point[] history) {
		robotHistory.put(r, history);
	}
	
	public Double[] getRobotOrientationHistory(RobotColour colour, RobotType type) {
		return getRobotOrientationHistory(new Robot(colour, type));
	}
	
	public Double[] getRobotOrientationHistory(Robot r) {
		return robotOrientationHistory.get(r);
	}
	
	public void setRobotOrientationHistory(Robot r, Double[] history) {
		robotOrientationHistory.put(r, history);
	}
	
//	public Double[] getRobotOrientationHistory(Robot r) {
//		return robotOrientationHistory.get(r);
//	}
//	
//	public void setRobotOrientationHistory(Robot r, Double[] orientationHistory) {
//		robotOrientationHistory.put(r, orientationHistory);
//	}
	
	
	public double getAimingAngle() {
		// TODO Auto-generated method stub
		System.err.println("getAimingAngle() not yet implemented!");
		return 0;
	}

	/**
	 * Find the Quadrant the robot is in
	 * @param robot The robot to check
	 */
	public Quadrant getRobotQuadrant(Robot robot) {
		if (robot.colour == ourColour) {
			// Our robot
			if (direction == ShootingDirection.LEFT) {
				// Shooting left
				if (robot.type == RobotType.DEFENDER) {
					return Quadrant.Q4;
				} else {
					return Quadrant.Q2;
				}
			} else {
				// Shooting right
				if (robot.type == RobotType.DEFENDER) {
					return Quadrant.Q1;
				} else {
					return Quadrant.Q3;
				}
			}
		} else {
			// Opponent's robot
			if (direction == ShootingDirection.LEFT) {
				// Shooting left
				if (robot.type == RobotType.DEFENDER) {
					return Quadrant.Q1;
				} else {
					return Quadrant.Q3;
				}
			} else {
				// Shooting right
				if (robot.type == RobotType.DEFENDER) {
					return Quadrant.Q4;
				} else {
					return Quadrant.Q2;
				}
			}
		}
	}
	
	/**
	 * Checks if a point is on the pitch
	 * @param p Point to check
	 * @return True for on the pitch, False for out of the pitch
	 */
	public boolean onPitch(Point p) {
		if (p.y < getPitchBottomRight().y + 10 && p.y > getPitchTopLeft().y - 10 &&
				p.x < getPitchTopRight().x + 10 && p.x > getPitchTopLeft().x - 10) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the ball is moving (at a 'reasonable' speed). Note that
	 * this can return False if the ball is moving, but rather slowly. If you
	 * want to have more exact control over what to define as 'moving', then
	 * use ballIsMoving(double threshold)
	 * @return True if the ball is moving at a reasonable speed, False if still or moving slowly
	 */
	public boolean ballIsMoving() {
		return ballIsMoving(0.02);
	}
	
	/**
	 * Checks if the ball's velocity vector has magnitude less than the given
	 * threshold.
	 * @see ballIsMoving()
	 * @param threshold
	 * @return True if the ball is moving more than the threshold, False otherwise
	 */
	public boolean ballIsMoving(double threshold) {
		return StrategyHelper.magnitude(getBallVelocity()) > threshold && onPitch(getBallPoint());
	}

	/**
	 * This method only works with our robots! Make sure to call
	 * setRobotGrabbedBall when we get the ball or this will be useless
	 * @return True if the robot has the ball in the grabber, False otherwise
	 */
	public boolean getRobotGrabbedBall(Robot r) {
		return robotGrabbedBall.get(r);
	}

	/**
	 * Set whether the robot has the ball in the grabber or not. This
	 * should only be used with our robots.
	 * @param robotGrabbedBall
	 */
	public void setRobotGrabbedBall(Robot r, boolean hasGrabbed) {
		this.robotGrabbedBall.put(r, hasGrabbed);
	}

	
	
	/*-----------------------------------------------*/
	/* Strategy methods                              */
	/*-----------------------------------------------*/
	
	public boolean getDoingPass() {
		return doingPass;
	}

	public void setDoingPass(boolean doingPass) {
		this.doingPass = doingPass;
	}
	
	public boolean getKickedPass() {
		return kickedPass;
	}

	public void setKickedPass(boolean kickedPass) {
		this.kickedPass = kickedPass;
	}

	public Point getAttackerPassPosition() {
		return attackerPassPosition;
	}

	public void setAttackerPassPosition(Point attackerPassPosition) {
		this.attackerPassPosition = attackerPassPosition;
	}
	
}