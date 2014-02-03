package sdp.strategy;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import sdp.communication.CommsInterface;
import sdp.gui.MainWindow;
import sdp.vision.Circle;
import sdp.vision.Drawable;
import sdp.vision.ImageProcessor;
import sdp.vision.NoAngleException;
import sdp.vision.Position;
import sdp.vision.WorldState;


/**
 * 
 */
public class Strategy extends Thread {
	private CommandHelper mCommandHelper;
	private WorldState mWorldState;
	private volatile GameState gameState;
	private Point STARTING_POINT;
	private CommsInterface mComms;
	private boolean gameOver;
	private ImageProcessor mImageProcessor;
	private boolean userReset;

	private static int HAS_BALL_DISTANCE_TOLERANCE = 80;
	private static double HAS_BALL_ROT_TOLERANCE = Math.PI/2;

	public Strategy(ImageProcessor imgProcessor, CommandHelper commandHelper, CommsInterface comms, WorldState worldState) {
		mCommandHelper = commandHelper;
		mComms = comms;
		mWorldState = worldState;
		mImageProcessor = imgProcessor;
		gameState = GameState.STANDING_BY;
		gameOver = false;
		userReset = false;
	}

	public GameState getGameState(){
		return gameState;
	}
	//TODO Alter for Attacker Robot
	private void updateStates() throws IOException {
		GameState oldState = gameState;
		if (oldState == GameState.RESETTING && !mCommandHelper.isMoving() && !mCommandHelper.isRotating() && (!userReset || mWorldState.getOurDefenderPosition().distance(STARTING_POINT) < 10)) {
			userReset = false;
			gameState = GameState.STANDING_BY;
		} else if (mCommandHelper.someoneScored() || userReset) {
			gameState = GameState.RESETTING;
		}else if ( isInScrum() ){
			gameState = GameState.SCRUM;
		}
		//		else {
		//			resetJourney=false;
		//			if (mWorldState.getBallPoint().distance(mWorldState.getPitchBottomLeft())<80 ||
		//				mWorldState.getBallPoint().distance(mWorldState.getPitchBottomRight())<80 ||
		//				mWorldState.getBallPoint().distance(mWorldState.getPitchTopLeft())<80 ||
		//				mWorldState.getBallPoint().distance(mWorldState.getPitchTopRight())<80) {
		//			System.out.println("Ball in corner");
		//			CommandHelper.stopAvoidingBall();
		//			gameState = GameState.BALL_IN_CORNER;
		//		}
		else if (mCommandHelper.opponentHasBall() || (oldState == GameState.THEY_HAVE_BALL  && !mWorldState.getBallVisible())) {
			mCommandHelper.stopAvoidingBall();
			gameState = GameState.THEY_HAVE_BALL;
		} else if (mCommandHelper.weHaveBall(mWorldState, mImageProcessor, HAS_BALL_ROT_TOLERANCE, HAS_BALL_DISTANCE_TOLERANCE) || (oldState == GameState.WE_HAVE_BALL  && !mWorldState.getBallVisible())) {
			mCommandHelper.stopAvoidingBall();
			gameState = GameState.WE_HAVE_BALL;
		} else if (!mCommandHelper.someoneScored() && mWorldState.getBallVisible()) {
			mCommandHelper.startAvoidingBall();
			gameState = GameState.PLAYING;
		}
		//		}

		if ( oldState != gameState ){
			mCommandHelper.stopMoving();
			mCommandHelper.stopRotating();
		}
	}

	@Override
	//TODO Alter for Attacker Robot
	public void run() {
		STARTING_POINT = mWorldState.getOurDefenderPosition();
		//STARTING_POINT = new Point((int) (((mWorldState.getOurGoalCentre().getX()*37)+(mWorldState.getOppositionGoalCentre().getX()*3))/40), (int) mWorldState.getOurGoalCentre().getY());
		// this could go in a run()
		boolean gameOver = false;
		while (!gameOver) {
			try {
				sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				if (gameState != GameState.STANDING_BY && gameState != GameState.DEFENDING_PENALTY && gameState != GameState.KICKING_PENALTY) {
					updateStates();
				}

				switch (gameState) {
				case SCRUM:
					scrum();
					break;
				case BALL_IN_CORNER:
					ballInCorner();
					break;
				case DEFENDING_PENALTY:
					defendingPenalty();
					break;
				case KICKING_PENALTY:
					kickingPenalty();
					break;
				case NO_CLEAR_SHOT:
					noClearShot();
					break;
				case PLAYING:
					playing();
					break;
				case THEY_HAVE_BALL:
					theyHaveBall();
					break;
				case TRIED_TO_SCORE:
					triedToScore();
					break;
				case WE_HAVE_BALL:
					weHaveBall();
					break;
				case RESETTING:
					resetting();
					break;
				case STANDING_BY:
					break;
				}

			} catch (NoAngleException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//TODO Alter for Attacker Robot
	private boolean isInScrum(){
		if ( mWorldState.getBallVisible() ){
			return false;
		}

		final int SCRUM_DIST = 100;

		if ( mWorldState.getOurDefenderPosition().distanceSq(mWorldState.getOppositionDefenderPosition()) > SCRUM_DIST*SCRUM_DIST ){
			return false;
		}

		if ( mWorldState.getBallPoint().getX() > mWorldState.getOurDefenderPosition().getX() ){
			return mWorldState.getBallPoint().getX() < mWorldState.getOppositionDefenderPosition().getX();
		}else{
			return mWorldState.getBallPoint().getX() > mWorldState.getOppositionDefenderPosition().getX();
		}
	}

	//TODO Alter for Attacker Robot
	private void scrum() {
		Point target = mWorldState.getOppositionDefenderPosition();

		if ( mWorldState.getOppositionGoalCentre().getX() < mWorldState.getOurDefenderPosition().getX() ){
			target = new Point(target.x - 25, target.y + 50);
		}else{
			target = new Point(target.x + 25, target.y + 50);
		}

		if (mCommandHelper.ourAngleTo(target) > 15/180 * Math.PI){
			mCommandHelper.stopMoving();
			try {
				mCommandHelper.facePoint(target, 0.2, 0.4);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			mCommandHelper.stopRotating();
			try {
				mCommandHelper.goToPoint(target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void standingBy() {
		// mCommandHelper.stop();
	}

	public void play() {
		mCommandHelper.stop();
		mCommandHelper.startAvoidingBall();
		gameState = GameState.PLAYING;
	}

	public void standBy() {
		mCommandHelper.stop();
		gameState = GameState.STANDING_BY;
	}

	public void reset() {
		mCommandHelper.stop();
		userReset = true;
		gameState = GameState.RESETTING;
	}

	public void defendPenalty() {
		gameState = GameState.DEFENDING_PENALTY;
	}

	/**
	 * Don't know what to do in this situation. We could try to get it out,
	 * or we could let the other robot get it out
	 * ensuring we don't let an opportunity arise for them to score.
	 * @throws NoAngleException
	 * @throws IOException
	 */
	private void ballInCorner() throws IOException, NoAngleException {
		mCommandHelper.stopAvoidingBall();
		if (mWorldState.getBallPoint().distance(mWorldState.getOurGoalCentre()) < mWorldState.getBallPoint().distance(mWorldState.getOppositionGoalCentre())) {
			ballInOurCorner();
		} else {
			ballInTheirCorner();
		}
	}

	private void ballInOurCorner() {
		System.out.println("ballInOurCorner()");
		Point inter;
		//go to ball from centre
		if (mWorldState.getBallY() > mWorldState.getOurGoalCentre().y) {
			inter = new Point(mWorldState.getBallX(), mWorldState.getBallY() - 20);
		} else {
			inter = new Point(mWorldState.getBallX(), mWorldState.getBallY() + 10);
		}
		try {
			System.out.println("Going to the intermediate point to catch ball");
			mCommandHelper.goToPoint(inter);
			Thread.sleep(2000);
			mCommandHelper.goToBall();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//rotateby pi/2 * sideFactor
		try {
			System.out.println("Rotating to get ball out of corner");
			mCommandHelper.facePoint(mWorldState.getOppositionGoalCentre());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO Alter for Attacker Robot
	private void ballInTheirCorner() {
		System.out.println("ballInTheirCorner()");
		Point inter;
		Point goal;
		int sideFactor = (mWorldState.getOppositionGoalCentre().x > mWorldState.getOurGoalCentre().x) ? (-1) : 1;
		if (mWorldState.getBallY() > mWorldState.getOurGoalCentre().y) {
			//upper
			inter = new Point(mWorldState.getBallX() + 40 * sideFactor, mWorldState.getBallY()-5);
			goal = new Point (
					mWorldState.getOurDefenderX(),
					(mWorldState.getOppositionGoalCentre().y + mWorldState.getOppositionGoalTop().y)/2 );
		} else {
			//lower
			inter = new Point(mWorldState.getBallX() + 40 * sideFactor, mWorldState.getBallY()+5);
			goal = new Point (
					mWorldState.getOurDefenderX(),
					(mWorldState.getOppositionGoalCentre().y + mWorldState.getOppositionGoalBottom().y)/2 );
		}
		try {
			mCommandHelper.facePoint(new Point(mWorldState.getOppositionGoalCentre().x, mWorldState.getOurDefenderPosition().y));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCommandHelper.sleep(2000);
		//go to ball from front
		try {
			System.out.println("Going to the intermediate point to catch ball");
			mCommandHelper.goToPoint(inter);
			Thread.sleep(2000);
			mCommandHelper.goToBall();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mCommandHelper.sleep(2000);
		try {
			mCommandHelper.facePoint(new Point(mWorldState.getOppositionGoalCentre().x, mWorldState.getOurDefenderPosition().y));
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//slide ball into goal
		try {
			System.out.println("Going to the goal point" );
			mCommandHelper.goToPoint(goal);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//kick - just in case
		mCommandHelper.kick();
	}	

	/**
	 * Keep same OX coordinate but follow the ball's OY coordinate.
	 * We still need to ensure there is enough time for vision to update worldState
	 * and for movement to react
	 */
	//TODO Alter for Attacker Robot
	private void defendingPenalty() {
		mCommandHelper.stopAvoidingBall();
		Point ballStart;
		if (mWorldState.getBallVisible()) {
			ballStart= mWorldState.getBallPoint();
		} else {
			ballStart= mWorldState.getDefendPenaltyPoint();
		}
		int startX;
		if (mWorldState.getDirection()==0) {
			startX = (int) (mWorldState.getOurDefenderPosition().getX())-8;
		} else {
			startX = (int) (mWorldState.getOurDefenderPosition().getX())+8;
		}
		boolean ballStartedMoving=false;
		Point lastTarget=new Point(999999,99999999);
		while (!ballStartedMoving) {
			//keep blocking the point they are aiming at
			double oppOrientation = mWorldState.getOppositionDefenderOrientation();
			if (oppOrientation > Math.PI) {
				oppOrientation -= Math.PI;
			}
			double grad = Math.tan(oppOrientation);
			int y = (int)((grad*(startX-mWorldState.getOppositionDefenderX()))
					+ mWorldState.getOppositionDefenderY());

			Point inter = new Point(startX,y);

			if (inter.y < mWorldState.getOurGoalTop().y)
				inter.setLocation(startX, mWorldState.getOurGoalTop().y);
			if (inter.y > mWorldState.getOurGoalBottom().y)
				inter.setLocation(startX, mWorldState.getOurGoalBottom().y);
			
			try {
				if (!mCommandHelper.isRotating()) {
					if (lastTarget.distance(inter)>5) {
						mCommandHelper.goToPoint(inter);
						lastTarget=inter;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Point[] hist = mWorldState.getBallHistory();
			Point lastBall = hist[hist.length-4];
			ballStartedMoving = (mWorldState.getBallVisible() && (mWorldState.getBallPoint().distance(lastBall)>2));
		}
		System.out.println("The ball is moving, intercepting");
		//the ball is now moving
		//intercept the ball
		//hold till it's time to enter play
		long start=System.currentTimeMillis();
		do {
			if (!mCommandHelper.isRotating()) {
				try {
					mCommandHelper.goToPoint(mCommandHelper.getInterceptPoint(ballStart, startX));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while ((System.currentTimeMillis()-start)<700);
		gameState = GameState.PLAYING;
	}

	/**
	 * Assuming we are not allowed to dribble in any way, and hoping the other
	 * team has some algorithm i'm thinking to have the robot face one end of
	 * the gate then turn fast and kick of the other one
	 * 
	 * TODO: a fast and reliable rotation system is the trick here + tons of
	 * testing
	 */
	public void kickingPenalty() {
		/*
		try {
			//mCommandHelper.facePoint(mWorldState.getOppositionGoalCentre());
			mCommandHelper.facePoint(KickFrom.getPointToShootAt(mWorldState));
		} catch (IOException e) {
			e.printStackTrace();
		}
		long start=System.currentTimeMillis();
		while ((System.currentTimeMillis()-start<1000) || !mCommandHelper.isRotating()) {
			try {
				sleep(5);
				mCommandHelper.facePoint(KickFrom.getPointToShootAt(mWorldState));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		mCommandHelper.kick();
		//gameState = GameState.STANDING_BY;
		gameState = GameState.PLAYING;
	}

	private void playing() throws IOException, InterruptedException, NoAngleException {
		final int DISTANCE_TOLERANCE = 35;
		final Point kickPoint = KickFrom.whereToKickFrom(mWorldState, DISTANCE_TOLERANCE);
		//final Point2D shootPoint = KickFrom.getPointToShootAt(mWorldState);
		//final Point kickPoint = KickFrom.whereToKickFromSimple(new Point((int) shootPoint.getX(), (int) shootPoint.getY()), mWorldState.getBallPoint(), DISTANCE_TOLERANCE);
		Collection<Drawable> kickDebugs = new ArrayList<Drawable>();
		kickDebugs.add(new Circle(Color.BLACK, new Point((int) kickPoint.getX()-2,(int) kickPoint.getY()-2), 4));
		MainWindow.addOrUpdateDrawable("kickfrom", kickDebugs);
		mCommandHelper.stopRotating();
		mCommandHelper.movement.setAvoidBall(true);
		mCommandHelper.goToPoint(kickPoint);
	}

	private void noClearShot() {
		gameState = GameState.PLAYING;
	}

	private void theyHaveBall() throws IOException, InterruptedException {
		if (!mCommandHelper.isRotating()) {
			mCommandHelper.goToPoint(KickFrom.whereToDefendFrom(mWorldState, 40));
		}
		updateStates();
	}
	//TODO Alter for Attacker Robot
	private boolean isInFrontOfBall(){
		if ( mWorldState.getOppositionGoalCentre().x > mWorldState.getOurDefenderPosition().x){
			if ( mWorldState.getBallPoint().x > mWorldState.getOurDefenderPosition().x ){
				return true;
			}
		}else if (mWorldState.getOppositionGoalCentre().x < mWorldState.getOurDefenderPosition().x){
			if ( mWorldState.getBallPoint().x < mWorldState.getOurDefenderPosition().x ){
				return true;
			}
		}
		return false;
	}

	private long mLastKickTime = 0;
	//TODO Alter for Attacker Robot
	private void weHaveBall() throws IOException, InterruptedException, NoAngleException {
		final double ROTATION_TOLERANCE = 0.4;
		//		if ( mWorldState.getBallVisible() && isInFrontOfBall() ){
		//			CommandHelper.stopRotating();
		//			mCommandHelper.goToBall();
		//			System.out.println("go to ball");
		//		}else
		if ( mWorldState.getBallVisible() && Math.abs(mCommandHelper.ourAngleTo(mWorldState.getBallPoint())) > ROTATION_TOLERANCE ) {
			//System.out.println("Rotating to ball");
			if (!mCommandHelper.isMoving()) {
				//System.out.println("mCommandHelper.rotation.isRotating()="+mCommandHelper.rotation.isRotating());
				//mCommandHelper.facePoint(mWorldState.getBallPoint(), 0.2, (float)ROTATION_TOLERANCE);
				mCommandHelper.facePoint(KickFrom.getPointToShootAt(mWorldState));
			}
		}else{
			System.out.println("Go to goal");
			Point goal = KickFrom.getPointToShootAt(mWorldState);
			if (!mCommandHelper.isRotating()) {
				Line2D bestGoalLine = KickFrom.getBestGoalLine(mWorldState);
				mCommandHelper.goToPoint(goal);
				boolean shouldKickAtGoal = mCommandHelper.isFacingLine(bestGoalLine) &&
				(mWorldState.getOurDefenderPosition().distance(mWorldState.getBallPoint()) < 40 &&
						Math.abs(mCommandHelper.ourAngleTo(mWorldState.getOppositionDefenderPosition())) > Math.PI / 4);
				if ( mLastKickTime+1500 < System.currentTimeMillis() && shouldKickAtGoal ){
					mLastKickTime = System.currentTimeMillis();
					mCommandHelper.kick();
				}
			}
		}
	}

	private void triedToScore() {
	}

	private void resetting() throws IOException, InterruptedException, NoAngleException {
		if (!mCommandHelper.isRotating()) {
			mCommandHelper.goToPoint(STARTING_POINT);
		}
		if (!mCommandHelper.isMoving()) {
			mCommandHelper.facePoint(mWorldState.getOppositionGoalCentre());
		}
	}

	public void gameOver() {
		gameOver = true;
	}
}
