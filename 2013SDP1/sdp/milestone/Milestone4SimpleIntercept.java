package sdp.milestone;

import java.awt.Point;

import sdp.communication.CommsInterface;
import sdp.strategy.CommandHelper;
import sdp.vision.WorldState;

public class Milestone4SimpleIntercept extends Thread{

	private boolean running;
	private WorldState mWorldState;
	private CommsInterface mComms;
	private CommandHelper mCommandHelper;
	private Point ballStartPos; //might not be a good idea
//	private boolean redButtonPressed;
	
	/** Constructor for Milestone4SimpleIntercept.
	 * 	When created it stores the location of the ball at that time, as the ballStartPos.
	 * @param ch
	 * @param comms
	 * @param worldstate
	 */
	public Milestone4SimpleIntercept(CommandHelper ch, CommsInterface comms, WorldState worldstate){
		this.mCommandHelper = ch;
		this.mComms = comms;
		this.mWorldState = worldstate;
//		this.redButtonPressed = false;
		
		//below might not be a good idea
		if (mWorldState.getBallVisible()) {
			this.ballStartPos = mWorldState.getBallPoint();
		} else {
			//assumes opponent has ball, makes an estimate of where it could be, depending on
			//opponents orientation and which side of the pitch is ours
//			double opOrien = mWorldState.getOppositionOrientation();
//			Point ourGoal = mWorldState.getOurGoalCentre();
//			int estimateBallx;
//			if (opOrien > Math.PI) {
//				opOrien -= Math.PI;
//			}
//			double grad = Math.tan(opOrien);
//			if (ourGoal.equals(mWorldState.getLeftGoalPoint()))
//				estimateBallx = mWorldState.getOppositionX()-10;
//			else
//				estimateBallx = mWorldState.getOppositionX()+10;
//
//			int estimateBally = (int)((grad*(estimateBallx-mWorldState.getOppositionX())) + mWorldState.getOppositionY());
//			ballStartPos.setLocation(estimateBallx, estimateBally);
			this.ballStartPos = new Point(mWorldState.getOppositionX(), mWorldState.getOppositionX());
		}

	}
	
	public boolean isRunning() {
		if (running == true) 
			return true;
		return false;
	}
	
//	public void pressRedButton() {
////		System.out.println("redButtonPressed = True now :>");
//		this.redButtonPressed = true;
//	}
	
	/** If ball moved more than the distance specified, then ball has moved.
	 * 	Note: this is an attempt to remove noise
	 * @param dist
	 * @return
	 */
	public boolean ballMoved(double dist) {
		Point ballCurrentPos = mWorldState.getBallPoint();
		if (mWorldState.getBallVisible()) {
			if (norm(subtrVectors(ballStartPos,ballCurrentPos)) > dist)
				return true;
		}
		return false;
	}

	/** Adds two given vectors (points).
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public Point addVectors(Point p1, Point p2) {
		int v_x = p1.x + p2.x;
		int v_y = p1.y + p2.y;
		Point v = new Point(v_x,v_y);
		return v;
	}

	/** Subtracts the given vector (point) p2 from the given vector (point) p1.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public Point subtrVectors(Point p1, Point p2) {
		int v_x = p1.x - p2.x;
		int v_y = p1.y - p2.y;

		Point v = new Point(v_x, v_y);
		return v;
	}

	/** Computes the vector dot product of two given vectors (points).
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public int dotProduct(Point p1, Point p2) {
		int prod = (p1.x*p2.x)+(p1.y*p2.y);
		return prod;
	}

	/** Computes the norm (length) of the given vector.
	 * 
	 * @param p
	 * @return
	 */
	public double norm(Point p){
		double norm = Math.sqrt(dotProduct(p,p));
		return norm;
	}


	/** Given vectors u and v, this method calculates the projection (a vector p) of u onto v.
	 * 
	 * @param u
	 * @param v
	 * @return
	 */
	public Point projection(Point u, Point v) {
		double scalar = (dotProduct(v,u))/(double)(dotProduct(v,v));
		Point p = new Point();
		p.setLocation(scalar*v.x, scalar*v.y);
		return p;
	}

	/** Given two points, find the gradient of the line joining them.
	 * 
	 * @param u
	 * @param v
	 * @return
	 */
	public double findGradient(Point u, Point v){
		double gradient = (u.y-v.y)/((double)(u.x-v.x));
		return gradient;
	}

	/** Calculates the intersection point between the two lines:
	 * 	line of the moving ball and x = xCo-ord of robot.
	 * @return
	 */
	public Point calcIntersPoint() {
		Point ballCurPos = mWorldState.getBallPoint();
		// hack to get rid of noise
		// checks if the y change in start pos of ball and current pos of ball is noise (i.e. the change is >= 1)
//		if (Math.abs(ballStartPos.y-ballCurPos.y) >= 1)  //not sure if this is needed, it is best for when ball
//			ballCurPos.y = ballStartPos.y;				//ball is travelling in a straight line
//		Point[] ballHist = mWorldState.getBallHistory().clone();
//		ArrayList<Point> ballHistArrayList = new ArrayList<Point>(Arrays.asList(ballHist));
//		Position.filterBallPoints(ballHistArrayList, the mean of ballHistArrayList);
		double m = findGradient(ballStartPos,ballCurPos);
//		System.out.println("ball current point:" + ballCurPos.x + " " + ballCurPos.y);
		
		int x;
		/*if (mWorldState.getOurX() < 400) {
			x = 440;
		} else x = mWorldState.getOurX();*/
		
		// hack to disregard curve for task 1 (the ball curves towards the end of its path)
		// get to a point before it curls
//		if (mWorldState.getOurX() > 480) {  //might need adjustments
//			x = 480;
//		} else 
//			x = (int) (((mWorldState.getOurGoalCentre().getX()*37)+(mWorldState.getOppositionGoalCentre().getX()*3))/40);
			x = mWorldState.getOurX();
		// get the line of the ball, as an equation of y, solve and find y
		// using x above, and ball starting point
		int y = (int)((m*(x-ballStartPos.x)) + ballStartPos.y);
		// another hack to get rid of issue for slow ball, curving
//		if (mWorldState.getOurX() > 480) {
//			y+=10;
//		}

		Point inters = new Point(x,y);
//		System.out.println("inters = " + inters);
		return inters;
	}

	// below two methods are no longer used
	public double euclidDist(Point u, Point v) {
		return Math.sqrt((Math.pow(u.x-v.x,2) + (Math.pow(u.y-v.y,2))));
	}

	public boolean nearball(int threshold) {
		if (euclidDist(mWorldState.getBallPoint(),mWorldState.getOurPosition())<threshold)
			return true;
		//		if (Math.abs(mWorldState.getBallX()-mWorldState.getOurX()) < threshold) {
		//			if (Math.abs(mWorldState.getBallY()-mWorldState.getOurY()) < threshold) {
		//				return true;
		//			}
		//		}
		return false;
	}


	/**
	 * What this does (or should do):
	 * Waits (and checks every 3 ms) until ball has moved.
	 * Then calculates intersection point.
	 * Goes to that point.
	 */
	@Override
	public void run() {
		this.running = true;
		try {
//			System.out.println("milestone4 running");

//			System.out.println("ball start point:" + ballStartPos.x + " " + ballStartPos.y);
//			System.out.println("our start point:" + mWorldState.getOurX() + " " + mWorldState.getOurY());
			
			mComms.setMaximumSpeed(255);
			mCommandHelper.setAvoidBall(false);
			Point helperPoint = new Point (mWorldState.getOurX(), mWorldState.getOurGoalCentre().y);
//			System.out.println("HelperPoint: " + helperPoint);


//			boolean hasStartedMoving = false;
//			while (true) {
//				//if ball has moved at least this distance
//				if (ballMoved(5) || hasStartedMoving ) {
//					hasStartedMoving =  true; 
//					
//					Point p = calcIntersPoint();
//					mCommandHelper.goToPoint(p);
//					Thread.sleep(40);  //sleep used to for recalculation and reposition robot
//					
//					if ( !mCommandHelper.movement.isMoving() || mWorldState.getBallPoint().distance(mWorldState.getOurPosition()) < 80){
//						break;
//					}
//				}
//			}
			while (!ballMoved(4) /*|| !mWorldState.getBallVisible()*/) {
				sleep(5);
//				System.out.println("Got to Simple Intercept!");
//				if (/*redButtonPressed &&*/ !mCommandHelper.isMoving()) {
////					System.out.println("Person was faster than vision :)");
//					mCommandHelper.goToPoint(helperPoint);
//				}
				//System.out.println("waiting for a kick");
			}
			Point p = calcIntersPoint();
			mCommandHelper.goToPoint(p);
			sleep(600);  //Was originally 300
			//Commented all of below out for penalty
//			p = calcIntersPoint();
//			mCommandHelper.goToPoint(p);
//			sleep(2000);
//			
////			System.out.println("Stopping");
//			CommandHelper.stopMoving();
//			mCommandHelper.facePoint(mWorldState.getOppositionPosition());
//			long startTime = System.currentTimeMillis();
//			while ((mCommandHelper.ourAngleTo(mWorldState.getOppositionPosition()) > 0.3 )
//					&& (System.currentTimeMillis()-startTime<6000)){
//				sleep(40);
//			}
//			mCommandHelper.stop();
//			
//			sleep(100);
//			
//			mComms.setMaximumSpeed(150);
			
		} catch (Exception e){
			e.printStackTrace();
			try {
//				System.out.println("Something went wrong");
				mCommandHelper.stop();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		this.running = false;
	}
}
