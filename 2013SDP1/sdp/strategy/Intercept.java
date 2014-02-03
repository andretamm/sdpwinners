package sdp.strategy;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import sdp.vision.WorldState;

public class Intercept {

	final static double dampingConstant=0.95;
	final static double robotSpeed=0.02;
	
	private static double travelTime(double distance, double ballVelocityMagnitude) {
		return Math.log(distance*Math.log(dampingConstant)/ballVelocityMagnitude) / Math.log(dampingConstant);
	}
	
	public static ArrayList<Point> interceptPoint(WorldState ws) {
		/*
		Point ballStart=ws.getBallPoint();
		Point robotStart = ws.getOurPosition();
		double c = 3;
		Point2D.Double ballVelocity = new Point2D.Double(ws.getBallVelocity().getX(), ws.getBallVelocity().getY());
		double ballVelocityMagnitude = Math.sqrt(Math.pow(ballVelocity.getX(),2) + Math.pow(ballVelocity.getY(),2));
		Point2D.Double ballVelocityUnitVector = new Point2D.Double(
				(ballVelocity.getX()/ballVelocityMagnitude), 
				(ballVelocity.getY()/ballVelocityMagnitude));
		double finalBallTime = ballVelocityMagnitude/c;
		double ballFinalDistance = ballVelocityMagnitude*finalBallTime - (c/2)*finalBallTime*finalBallTime;
		
		double travelTimeMiddle;
		double robotTravelTime;
		double distanceTop=ballFinalDistance-1;
		double distanceBottom=1;
		double distanceMiddle=(distanceTop+distanceBottom)/2;
		
		ArrayList<Point> intercepts = new ArrayList<Point>();
		//enter binary search
		//do {
		
			double travelTimeBottom = travelTime(distanceBottom, ballVelocityMagnitude);
			travelTimeMiddle = travelTime(distanceMiddle, ballVelocityMagnitude);
					
			Point ballTravelVector = new Point((int) (ballVelocityUnitVector.getX()*ballFinalDistance), (int) (ballVelocityUnitVector.getY()*ballFinalDistance));
			Point intercept = new Point((int) (ballStart.getX()-ballTravelVector.getX()), (int) (ballStart.getY()-ballTravelVector.getY()));
					
			robotTravelTime = (intercept.distance(robotStart))/robotSpeed;

			System.out.println("distanceMiddle1="+distanceMiddle);
			if ((robotTravelTime>travelTimeBottom) && (robotTravelTime<travelTimeMiddle)) {
				distanceBottom=distanceMiddle;
			} else {
				distanceTop=distanceMiddle;
			}
			distanceMiddle=(distanceTop+distanceBottom)/2;
			
			intercepts.add(intercept);
			System.out.println("finalBallTime="+finalBallTime);
			System.out.println("ballFinalDistance="+ballFinalDistance);
			System.out.println("ballVelocityMagnitude="+ballVelocityMagnitude);
			System.out.println("ball velocity unit vector ("+ballVelocityUnitVector.getX()+", "+ballVelocityUnitVector.getY()+")");
			System.out.println("distanceMiddle2="+distanceMiddle);
			System.out.println("ballTravelTime="+travelTimeMiddle);
			System.out.println("robotTraveltime="+robotTravelTime);
			System.out.println("intercept=("+intercept.getX()+", "+intercept.getY()+")");
		//} while ((intercepts.size()<20)&&((Math.abs(robotTravelTime-travelTimeMiddle))>5));
		*/
		//TODO Alter for Attacker Robot
		Point ballStart=ws.getBallPoint();
		Point robotStart = ws.getOurDefenderPosition();
		double c = 300;
		Point2D.Double ballVelocity = new Point2D.Double(c*ws.getBallVelocity().getX(), c*ws.getBallVelocity().getY());
		double ballVelocityMagnitude = Math.sqrt(Math.pow(ballVelocity.getX(),2) + Math.pow(ballVelocity.getY(),2));
		ArrayList<Point> intercepts = new ArrayList<Point>();
		if (ballVelocityMagnitude>0.01*c) {

			Point2D.Double ballVelocityUnitVector = new Point2D.Double(
					(ballVelocity.getX()/ballVelocityMagnitude), 
					(ballVelocity.getY()/ballVelocityMagnitude));
			double ballFinalDistance = Math.sqrt(Math.pow(ballVelocity.getX(),2) + Math.pow(ballVelocity.getY(),2))/Math.log(dampingConstant);
			
			double travelTimeMiddle;
			//double robotTravelTime;
			double distanceTop=ballFinalDistance-1;
			double distanceBottom=1;
			double distanceMiddle=(distanceTop+distanceBottom)/2;
			
			//enter binary search
			//do {
				//double travelTimeBottom = travelTime(distanceBottom, ballVelocityMagnitude);
				travelTimeMiddle = travelTime(distanceMiddle, ballVelocityMagnitude);
						
				Point ballTravelVector = new Point((int) (ballVelocityUnitVector.getX()*travelTimeMiddle), (int) (ballVelocityUnitVector.getY()*travelTimeMiddle));
				Point intercept = new Point((int) (ballStart.getX()+ballTravelVector.getX()), (int) (ballStart.getY()+ballTravelVector.getY()));
						
				//robotTravelTime = (intercept.distance(robotStart))/robotSpeed;

				//System.out.println("distanceMiddle1="+distanceMiddle);
				//if ((robotTravelTime>travelTimeBottom) && (robotTravelTime<travelTimeMiddle)) {
				//	distanceBottom=distanceMiddle;
				//} else {
				//	distanceTop=distanceMiddle;
				//}
				//distanceMiddle=(distanceTop+distanceBottom)/2;
				
				intercepts.add(intercept);
//				System.out.println("ballFinalDistance="+ballFinalDistance);
				//System.out.println("ballVelocityMagnitude="+ballVelocityMagnitude);
//				System.out.println("ball velocity unit vector ("+ballVelocityUnitVector.getX()+", "+ballVelocityUnitVector.getY()+")");
//				System.out.println("distanceMiddle2="+distanceMiddle);
//				System.out.println("ballTravelTime="+travelTimeMiddle);
//				System.out.println("robotTraveltime="+robotTravelTime);
//				System.out.println("intercept=("+intercept.getX()+", "+intercept.getY()+")");
			//} while ((intercepts.size()<20)&&((Math.abs(robotTravelTime-travelTimeMiddle))>5));
		} else {
			intercepts.add(ballStart);
		}
		
		//Point ballTravelVector = new Point((int) (ballVelocityUnitVector.getX()*robotTravelTime), (int) (ballVelocityUnitVector.getY()*robotTravelTime));
		//Point intercept = new Point((int) (ballStart.getX()+ballTravelVector.getX()), (int) (robotStart.getY()+ballTravelVector.getY()));

		//System.out.println("done intercepts\n");
		return intercepts;
	}
}
