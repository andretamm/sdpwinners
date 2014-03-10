package sdp.milestone;

import java.awt.Point;
import sdp.communication.CommsInterface;
import sdp.strategy.CommandHelper;
import sdp.vision.WorldState;

public class Milestone4Intercept extends Thread{
	
	private WorldState mWorldState;
	private CommsInterface mComms;
	private CommandHelper mCommandHelper;
	private boolean running;
	

	public Milestone4Intercept(CommandHelper ch, CommsInterface comms, WorldState worldstate) {
		this.mCommandHelper = ch;
		this.mComms = comms;
		this.mWorldState = worldstate;
		this.running = false;
	}
	
	public Point calcAverDirVecBall(){  // calculates the direction vector of the ball
		Point[] history = mWorldState.getBallHistory();
		System.out.println("history");
		System.out.println();
		for (int i=0;i<history.length-1;i++) {
			System.out.println(history[i]);
		}
		System.out.println("--------------");
		int sum_x = 0;
		int sum_y = 0;
		//Point sum = new Point(0,0);
		for (int i=0;i<history.length-2;i++) {
			sum_x += (history[i+1].x-history[i].x);
			sum_y += (history[i+1].y-history[i].y);			
		}
		Point avgDirVec = new Point();
//		sum_x = (int) (sum_x/(double)4);
//		sum_y = (int) (sum_y/(double)4);
		avgDirVec.setLocation(sum_x, sum_y);
		return avgDirVec;
	}
	
	
	// In a sense models the ball as linear through time.
	public Point predPoint(Point avg, int scale) {  // calculates where the ball will go, depending on the scalar given (treat it as time)
		Point mostRecentPoint = mWorldState.getBallHistory()[mWorldState.getBallHistory().length-1];
		Point scaledAvg = new Point(avg.x*scale, avg.y*scale);
		Point predPoint = new Point(scaledAvg.x+mostRecentPoint.x, scaledAvg.y+mostRecentPoint.y);
		return predPoint;
	}
	
	public Point subtrVectors(Point p1, Point p2) {
		int v_x = p1.x - p2.x;
		int v_y = p1.y - p2.y;
		
		Point v = new Point(v_x, v_y);
		return v;
	}
	
	public double norm(Point p){
		double norm = Math.sqrt(dotProduct(p,p));
		return norm;
	}
	
	public int dotProduct(Point p1, Point p2) {
		int prod = (p1.x*p2.x)+(p1.y*p2.y);
		return prod;
	}
	
	public Point projection(Point u, Point v, int vscalar) {
		double scalar = (dotProduct(v,u))/(double)(dotProduct(v,v));
		Point p = new Point();
		p.setLocation(scalar*vscalar*v.x, scalar*vscalar*v.y);
		return p;
	}
	
	public boolean ballMoved(double dist) {
		if (mWorldState.getBallVisible()) {
		Point ballCurrentPos = mWorldState.getBallPoint();
//		if (ballStartPos.equals(ballCurrentPos))
//			return false;
//		return true;
		if (norm(subtrVectors(mWorldState.getBallHistory()[0],ballCurrentPos)) > dist)
			return true;
		}
		return false;
	}
	
	public double euclidDist(Point u, Point v) { //calculate euclid distance from two points
		return Math.sqrt((Math.pow(u.x-v.x,2) + (Math.pow(u.y-v.y,2))));
	}
	//TODO Alter for Attacker Robot
	public boolean nearball(int threshold) { //checks if near the ball
		if (euclidDist(mWorldState.getBallPoint(),mWorldState.getOurDefenderPosition())<threshold)
			return true;
		return false;
	}
	
	public double findGradient(Point u, Point v){
		double gradient = (u.y-v.y)/((double)(u.x-v.x));
		return gradient;
	}
	
	
	public Point calcIntersPoint() {
		Point ballCurPos = mWorldState.getBallPoint();
		// hack to get rid of noise
		// checks if the y change in start pos of ball and current pos of ball is noise (i.e. the change is >= 1)
//		if (Math.abs(ballStartPos.y-ballCurPos.y) >= 1)  //not sure if this is needed, it is best for when ball
//			ballCurPos.y = ballStartPos.y;				//ball is travelling in a straight line
		Point[] ballHist = mWorldState.getBallHistory().clone();
//		ArrayList<Point> ballHistArrayList = new ArrayList<Point>(Arrays.asList(ballHist));
//		Position.filterBallPoints(ballHistArrayList, the mean of ballHistArrayList);
		double m = findGradient(ballHist[0],ballCurPos);
		System.out.println("ball current point:" + ballCurPos.x + " " + ballCurPos.y);
		
		int x;
		/*if (mWorldState.getOurX() < 400) {
			x = 440;
		} else x = mWorldState.getOurX();*/
		
		// hack to disregard curve for task 1 (the ball curves towards the end of its path)
		// get to a point before it curls
//		if (mWorldState.getOurX() > 480) {  //might need adjustments
//			x = 480;
//		} else 
			//TODO Alter for Attacker Robot
			x = mWorldState.getOurDefenderX();

		// get the line of the ball, as an equation of y, solve and find y
		// using x above, and ball starting point
		int y = (int)((m*(x-ballHist[0].x)) + ballHist[0].y);
		// another hack to get rid of issue for slow ball, curving
//		if (mWorldState.getOurX() > 480) {
//			y+=10;
//		}

		Point inters = new Point(x,y);
		return inters;
	}
	
	public boolean isRunning() {
		if (running == true)
			return true;
		return false;
	}
	
	public void run() {
		this.running = true;
		try {
			System.out.println("intercept running");
			while (!ballMoved(3)) {
				Thread.sleep(1);
			}
			while (ballMoved(3)) {
				mComms.setMaximumSpeed(255);
				Point intersect = calcIntersPoint();
				mCommandHelper.setAvoidBall(false);
				mCommandHelper.goToPoint(intersect);
				Thread.sleep(500);	// 500 ms before each re-calculation, could change to lower;
				
				//some old code
//				Point ballDirVect = calcAverDirVecBall();
//				Point balltorobot = subtrVectors(mWorldState.getBallPoint(),mWorldState.getOurPosition());
//				Point proj = projection(balltorobot,ballDirVect, 5);
//				Point pred = predPoint(calcAverDirVecBall(),50);
//				if (pred.x < 30)
//					pred.setLocation(40, pred.y);
//				System.out.println("avg" + calcAverDirVecBall());
//				System.out.println("robot" + mWorldState.getOurPosition());
//				System.out.println("balltorobot" + balltorobot);
//				System.out.println("predicted point" + pred);
//				System.out.println("current ball point" + mWorldState.getBallPoint());
			}
			this.running = false;
			//Thread.sleep(3000);
			//mCommandHelper.goToBall();
		}catch (Exception e){
//			try {
				System.out.println("something went wrong");
//				mCommandHelper.setMoving(false);
//				mRotation.setRotating(false);
//			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}		
		}
	}
}
