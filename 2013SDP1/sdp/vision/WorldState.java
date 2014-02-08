package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;

import common.Robot;
import common.RobotMap;
import constants.Quadrant;
import constants.QuadrantX;
import constants.RobotColour;
import constants.RobotType;
import constants.ShootingDirection;


public class WorldState implements VisionInterface {

	private ShootingDirection direction; // 0 = right, 1 = left.
	private RobotColour colour;
	private int pitch; // 0 = main, 1 = side room
	
	private RobotMap<Point> robotPosition; // Positions of all the robots

	private int ballX;
	private int ballY;
	
	private RobotMap<Double> robotOrientation; // Orientations of all the robots
	
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
	private Point pitchTopLeft1 = new Point(29, 78);
	private Point pitchBottomRight1 = new Point(609, 396);

	private Point outerPitchTopLeft2 = new Point(5, 68);
	private Point outerPitchBottomRight2 = new Point(634,400);

	//these two are already barrelcorrected
	private Point pitchTopLeft2 = new Point(20, 68);
	private Point pitchBottomRight2 = new Point(606,400);
	
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
	private Point[] ourDefenderHistory;
	private Point[] ourAttackerHistory;
	private Point ourDefenderVelocity;
	private Point ourAttackerVelocity;
	private long[] ourTimeStamps;

	private boolean removeShadows = false;

	/* Getter and Setter methods for the quadrants */
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

	private Point[] oppositionDefenderHistory;
	private Point[] oppositionAttackerHistory;
	private Point oppositionDefenderVelocity;
	private Point oppositionAttackerVelocity;
	private long[] oppositionTimeStamps;

	//horrible hack:
	private double targetAngle;  //the destination angle
	private double correctedAngle; //the corrected angle, aim for this
	private boolean showDrawables;
	private boolean ballVisible;
	private int goalHeight=70; //the full length of the goal

	public WorldState() {

		/* control properties */
		this.direction = ShootingDirection.RIGHT;
		this.colour = RobotColour.YELLOW;
		this.pitch = 1;

		//TODO Alter object properties for velocities, history
		
		/* object properties */
		this.robotPosition = new RobotMap<Point>(new Point(0,0));
		this.robotOrientation = new RobotMap<Double>(0.0);
		
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
		this.ourDefenderVelocity = new Point(0,0);
		this.ourAttackerVelocity = new Point(0,0);
		this.ourDefenderHistory = new Point[5];
		this.ourAttackerHistory = new Point[5];
		this.ourTimeStamps = new long[5];
		this.oppositionDefenderVelocity = new Point(0,0);
		this.oppositionAttackerVelocity = new Point(0,0);
		this.oppositionDefenderHistory = new Point[5];
		this.oppositionAttackerHistory = new Point[5];
		this.oppositionTimeStamps = new long[5];
		for (int i=0; (i<this.ourDefenderHistory.length); i++) {
			this.ballHistory[i] = new Point(1,1);
			this.ballTimeStamps[i] = 1;
			this.ourDefenderHistory[i] = new Point(1,1);
			this.ourTimeStamps[i] = 1;
			this.oppositionDefenderHistory[i] = new Point(1,1);
			this.oppositionTimeStamps[i] = 1;
		}
		// TODO not sure what's going on here will need to check it out
		/*
		for (int i=0; (i<this.ourAttackerHistory.length); i++) {
			this.ballHistory[i] = new Point(1,1);
			this.ballTimeStamps[i] = 1;
			this.ourAttackerHistory[i] = new Point(1,1);
			this.ourTimeStamps[i] = 1;
			this.oppositionAttackerHistory[i] = new Point(1,1);
			this.oppositionTimeStamps[i] = 1;
		}
		*/
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
		return ballX-getPitchTopLeft().x;
	}

	public int getBallY() {
		return ballY-getPitchTopLeft().y;
	}
	
	
	/*-------------------------------------------------------------------*/
	/*-----Getters and setters for robot positions and orientation-------*/
	/*-------------------------------------------------------------------*/
	public void setRobotOrientation(RobotType rType, RobotColour rColour, double orientation) {		
		robotOrientation.put(new Robot(rColour, rType), orientation);
	}
	
	@Override
	public double getRobotOrientation(RobotType rType, RobotColour rColour) {
		return robotOrientation.get(new Robot(rColour, rType));
	}
	
	// New position getters/setters
	public int getRobotX(Robot r) {
		return robotPosition.get(r).x;
	}
	
	public int getRobotY(Robot r) {
		return robotPosition.get(r).y;
	}
	
	public void setRobotX(Robot r, int x) {
		robotPosition.get(r).x = x;
	}
	
	public void setRobotY(Robot r, int y) {
		robotPosition.get(r).y = y;
	}
	

	int getBallXVision() {
		return ballX;
	}

	public void setBallX(int ballX) {
		this.ballX = ballX;
	}

	int getBallYVision() {
		return ballY;
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
		return colour;
	}

	public void setColour(RobotColour colour) {
		this.colour = colour;
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
		return getRobotX(new Robot(colour, RobotType.DEFENDER));
	}

	int getOurDefenderYVision(){
		return getRobotY(new Robot(colour, RobotType.DEFENDER));
	}
	
	int getOurAttackerXVision(){
		return getRobotX(new Robot(colour, RobotType.ATTACKER));
	}

	int getOurAttackerYVision(){
		return getRobotY(new Robot(colour, RobotType.ATTACKER));
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
		return robotOrientation.get(new Robot(colour, RobotType.DEFENDER));
	}
	
	public double getOurAttackerOrientation() {
		return robotOrientation.get(new Robot(colour, RobotType.ATTACKER));
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
		return pitch == 0 ? pitchTopLeft1 : pitchTopLeft2;
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
		return pitch == 0 ? pitchBottomRight1 : pitchBottomRight2;
	}

	public double getPitchHeight() {
		return getPitchBottomRight().getY()-getPitchTopLeft().getY();
	}

	public double getPitchWidth() {
		return getPitchBottomRight().getX()-getPitchTopLeft().getX();
	}

	public Point getLeftGoalPoint() {
		return new Point(0, (getPitchBottomRight().y - getPitchTopLeft().y) / 2);
	}

	public Point getRightGoalPoint() {
		return new Point((int) getPitchWidth(), (getPitchBottomRight().y - getPitchTopLeft().y) / 2);
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

	public Point getBallPoint() {
		return new Point(getBallX(), getBallY());
	}

	public Point getOurDefenderPosition(){
		return new Point(getOurDefenderX(), getOurDefenderY());
	}
	
	public Point getOurAttackerPosition(){
		return new Point(getOurAttackerX(), getOurAttackerY());
	}

	public Point getOppositionDefenderPosition(){
		return new Point(getOppositionDefenderX(), getOppositionDefenderY());
	}
	
	public Point getOppositionAttackerPosition(){
		return new Point(getOppositionAttackerX(), getOppositionAttackerY());
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

	public Point getOurDefenderVelocity() {
		return ourDefenderVelocity;
	}
	
	public Point getOurAttackerVelocity() {
		return ourAttackerVelocity;
	}

	public Point[] getOurDefenderHistory() {
		return ourDefenderHistory;
	}
	
	public Point[] getOurAttackerHistory() {
		return ourAttackerHistory;
	}

	public Point getOppositionDefenderVelocity() {
		return oppositionDefenderVelocity;
	}
	
	public Point getOppositionAttackerVelocity() {
		return oppositionAttackerVelocity;
	}

	public Point[] getOppositionDefenderHistory() {
		return oppositionDefenderHistory;
	}
	
	public Point[] getOppositionAttackerHistory() {
		return oppositionAttackerHistory;
	}

	public long[] getBallTimes() {
		return ballTimeStamps;
	}

	public long[] getOurTimes() {
		return ourTimeStamps;
	}

	public long[] getOppositionTimes() {
		return oppositionTimeStamps;
	}

	public void setBallVelocity(Point2D.Double bv) {
		this.ballVelocity=bv;
	}

	public void setBallHistory(Point[] bh) {
		this.ballHistory=bh;
	}

	public void setOurDefenderVelocity(Point ov) {
		this.ourDefenderVelocity=ov;
	}
	
	public void setOurAttackerVelocity(Point ov) {
		this.ourAttackerVelocity=ov;
	}

	public void setOurDefenderHistory(Point[] oh) {
		this.ourDefenderHistory=oh;
	}
	
	public void setOurAttackerHistory(Point[] oh) {
		this.ourAttackerHistory=oh;
	}

	public void setOppositionDefenderVelocity(Point opv) {
		this.oppositionDefenderVelocity=opv;
	}
	
	public void setOppositionAttackerVelocity(Point opv) {
		this.oppositionAttackerVelocity=opv;
	}

	public void setOppositionDefenderHistory(Point[] oph) {
		this.oppositionDefenderHistory=oph;
	}
	
	public void setOppositionAttackerHistory(Point[] oph) {
		this.oppositionAttackerHistory=oph;
	}

	public void setBallTimes(long[] bt) {
		this.ballTimeStamps=bt;
	}

	public void setOurTimes(long[] ot) {
		this.ourTimeStamps=ot;
	}

	public void setOppositionTimes(long[] opt) {
		this.oppositionTimeStamps=opt;
	}

	public Point2D.Double getRobotVelocity() {
		return new Point2D.Double(1.5, 3);
	}

	
	//TODO Alter for getOurAttackerPosition
	/**
	 *  If we can't see the opposition robot, it may be off the pitch. This method provides the location in this case.
	 * @param robot The colour of the robot for which a position should be provided
	 * @return The default location of the off-pitch robot
	 */
	Point getDefaultPoint(RobotColour robot) {
		if (robot==colour) {
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
		return colour == RobotColour.YELLOW ? RobotColour.BLUE : RobotColour.YELLOW;
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

	@Override
	public Point getRobotXY(RobotColour colour, RobotType type) {
		Robot r = new Robot(colour, type);
		return new Point(getRobotX(r), getRobotY(r));
	}

	@Override
	public Point getBallXY() {
		return new Point(getBallX(), getBallY());
	}

	@Override
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
	
	@Override
	public Point2D.Double getBallVelocity() {
		return ballVelocity;
	}

	@Override
	public Point getRobotVelocity(RobotColour colour, RobotType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point[] getRobotHistory(RobotColour colour, RobotType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAimingAngle() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}