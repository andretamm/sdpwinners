package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;


public class WorldState {

	private int direction; // 0 = right, 1 = left.
	private RobotColour colour;
	private int pitch; // 0 = main, 1 = side room
	private int blueX;
	private int blueY;
	private int yellowX;
	private int yellowY;
	private int ballX;
	private int ballY;
	private double blueOrientation;
	private double yellowOrientation;
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

	public static final int ballRadius = 12;
	public static double cmToPixels = 0.38;

	private Point[] ballHistory;
	private long[] ballTimeStamps;
	private Point2D.Double ballVelocity;
	private Point[] ourHistory;
	private Point ourVelocity;
	private long[] ourTimeStamps;

	private boolean removeShadows = false;

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

	private Point[] oppositionHistory;
	private Point oppositionVelocity;
	private long[] oppositionTimeStamps;

	//horrible hack:
	private double targetAngle;  //the destination angle
	private double correctedAngle; //the corrected angle, aim for this
	private boolean showDrawables;
	private boolean ballVisible;
	private int goalHeight=70; //the full length of the goal

	public WorldState() {

		/* control properties */
		this.direction = 0;
		this.colour = RobotColour.YELLOW;
		this.pitch = 1;

		/* object properties */
		this.blueX = 0;
		this.blueY = 0;
		this.yellowX = 0;
		this.yellowY = 0;
		this.ballX = 0;
		this.ballY = 0;
		this.blueOrientation = 0;
		this.yellowOrientation = 0;
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
		this.ourVelocity = new Point(0,0);
		this.ourHistory = new Point[5];
		this.ourTimeStamps = new long[5];
		this.oppositionVelocity = new Point(0,0);
		this.oppositionHistory = new Point[5];
		this.oppositionTimeStamps = new long[5];
		for (int i=0; (i<this.ourHistory.length); i++) {
			this.ballHistory[i] = new Point(1,1);
			this.ballTimeStamps[i] = 1;
			this.ourHistory[i] = new Point(1,1);
			this.ourTimeStamps[i] = 1;
			this.oppositionHistory[i] = new Point(1,1);
			this.oppositionTimeStamps[i] = 1;
		}
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

	int getBlueXVision() {
		return blueX;
	}

	public void setBlueX(int blueX) {
		this.blueX = blueX;
	}

	int getBlueYVision() {
		return blueY;
	}

	void setBlueY(int blueY) {
		this.blueY = blueY;
	}

	int getYellowXVision() {
		return yellowX;
	}

	public void setYellowX(int yellowX) {
		this.yellowX = yellowX;
	}

	int getYellowYVision() {
		return yellowY;
	}

	public void setYellowY(int yellowY) {
		this.yellowY = yellowY;
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

	public double getBlueOrientation() {
		return blueOrientation;
	}

	public void setBlueOrientation(double blueOrientation) {
		this.blueOrientation = blueOrientation;
	}

	public double getYellowOrientation() {
		return yellowOrientation;
	}

	public void setYellowOrientation(double yellowOrientation) {
		this.yellowOrientation = yellowOrientation;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
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

	int getOurXVision(){
		return colour == RobotColour.YELLOW ? getYellowXVision() : getBlueXVision();
	}

	int getOurYVision(){
		return colour == RobotColour.YELLOW ? getYellowYVision() : getBlueYVision();
	}

	public int getOurX(){
		int x = colour == RobotColour.YELLOW ? getYellowXVision() : getBlueXVision();
		return x - getPitchTopLeft().x;
	}

	public int getOurY(){
		int y = colour == RobotColour.YELLOW ? getYellowYVision() : getBlueYVision();
		return y - getPitchTopLeft().y;
	}

	public double getOurOrientation(){
		return colour == RobotColour.YELLOW ? getYellowOrientation() : getBlueOrientation();
	}

	public double getOppositionOrientation(){
		return colour == RobotColour.YELLOW ? getBlueOrientation() : getYellowOrientation();
	}

	public int getOppositionX() {
		int x = colour == RobotColour.YELLOW ? getBlueXVision() : getYellowXVision();
		return x - getPitchTopLeft().x;
	}

	public int getOppositionY() {
		int y = colour == RobotColour.YELLOW ? getBlueYVision() : getYellowYVision();
		return y - getPitchTopLeft().y;
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
		return direction == 0 ? getLeftGoalPoint() : getRightGoalPoint();
	}

	public Point getOppositionGoalCentre() {
		return direction == 1 ? getLeftGoalPoint() : getRightGoalPoint();
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

	public Point getOurPosition(){
		return new Point(getOurX(), getOurY());
	}

	public Point getOppositionPosition(){
		return new Point(getOppositionX(), getOppositionY());
	}

	public void setBallVisible(boolean ballVisible) {
		this.ballVisible = ballVisible;
	}

	public boolean getBallVisible() {
		return ballVisible;
	}

	public Point2D.Double getBallVelocity() {
		return ballVelocity;
	}

	public Point[] getBallHistory() {
		return ballHistory;
	}

	public Point getOurVelocity() {
		return ourVelocity;
	}

	public Point[] getOurHistory() {
		return ourHistory;
	}

	public Point getOppositionVelocity() {
		return oppositionVelocity;
	}

	public Point[] getOppositionHistory() {
		return oppositionHistory;
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

	public void setOurVelocity(Point ov) {
		this.ourVelocity=ov;
	}

	public void setOurHistory(Point[] oh) {
		this.ourHistory=oh;
	}

	public void setOppositionVelocity(Point opv) {
		this.oppositionVelocity=opv;
	}

	public void setOppositionHistory(Point[] oph) {
		this.oppositionHistory=oph;
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

	/**
	 *  If we can't see the opposition robot, it may be off the pitch. This method provides the location in this case.
	 * @param robot The colour of the robot for which a position should be provided
	 * @return The default location of the off-pitch robot
	 */
	Point getDefaultPoint(RobotColour robot) {
		if (robot==colour) {
			// if the robot is ours, just make the best on pitch guess
			return new Point((int) (getPitchTopLeft().getX()+getOurPosition().getX()), (int) (getPitchTopLeft().getY()+getOurPosition().getY()));
		} else {
			// if the robot is opposition, assume it's off the pitch
			if (direction==0) {
				return new Point((int) (getPitchTopLeft().getX()+getOurGoalCentre().getX()-50), (int) (getPitchTopLeft().getY()+getOurGoalCentre().getY()));
			} else {
				return new Point((int) (getPitchTopLeft().getX()+getOurGoalCentre().getX()+50), (int) (getPitchTopLeft().getY()+getOurGoalCentre().getY()));
			}
		}
	}

	int getOppositionXVision(){
		return colour == RobotColour.YELLOW ? getBlueXVision() : getYellowXVision();
	}

	int getOppositionYVision(){
		return colour == RobotColour.YELLOW ? getBlueYVision() : getYellowYVision();
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
}