package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Arrays;

import behavior.StrategyHelper;

import common.Robot;

import constants.C;
import constants.RobotColour;
import constants.RobotType;

/**
 * Finds the orientation by setting a line through the centre of the grey circle and the centre of a green plate.
 */

public class Orientation {
	
	/**
	 * Finds the orientation of the robot in the given quadrant in RADIANS.
	 * @param greyCircle All the grey points in the quadrant
	 * @param greenPlate All the green points in the quadrant
	 * @param qp The QuadrantPoints of the quadrant
	 * @param rColour 
	 * @param rType 
	 * @return The angle the robot is facing in RADIANS
	 * @throws NoAngleException
	 */
	public static double findRobotOrientation(ArrayList<Point> greyCircle, ArrayList<Point> greenPlate, QuadrantPoints qp, WorldState worldState, RobotType rType, RobotColour rColour) throws NoAngleException {
		Point greenCentre = new Point(0,0);
		
		try {
			greenCentre = Position.findMean(greenPlate);
		} catch (Exception e1) {
			//e1.printStackTrace();
		}
		
		// Calculate centre of grey circle points
        int totalX = 0;
        int totalY = 0;
        
        for (int i = 0; i < greyCircle.size(); i++) {
        	Point p = greyCircle.get(i);
        	double distanceFromRobotCentre = greenCentre.distance(new Point((int) p.getX(), (int) p.getY()));
        	
        	if (distanceFromRobotCentre <= Thresholder.plateSize - 3) {
	        	totalX += p.getX();
	            totalY += p.getY();
        	} else {
        		// Remove grey points too far from the robot's centre
        		greyCircle.remove(i);
        		i -= 1;
        	}
        }
        
        double greyCentreX = 0, greyCentreY = 0;
        // Centre of grey circle
        if (greyCircle.size() != 0) {
//	        greyCentreX = totalX / greyCircle.size();
//	        greyCentreY = totalY / greyCircle.size();
	               
	        try {
				Point greyCenter = Position.findMean(greyCircle);
				greyCentreX = greyCenter.x;
				greyCentreY = greyCenter.y;
			} catch (Exception e) {
//				System.err.println("No grey points!");
			}
        } else {
//        	System.err.println("No points in grey circle");
        }
        
        Point2D.Double greyCentre = new Point2D.Double(greyCentreX, greyCentreY);
      
        // USE ROBOT'S COORDINATES AS THE CENTRE OF THE GREEN PLATE INSTEAD
        double x0 = 0, y0 = 0;
        x0 = qp.getRobotPosition().getX();
        y0 = qp.getRobotPosition().getY();


        
        Point2D.Double plateCentre = new Point2D.Double(x0, y0);
        worldState.setRobotOrientationVector(new Robot(rColour, rType), new Point2D.Double(plateCentre.x - greyCentre.x, plateCentre.y - greyCentre.y));

        double orientation = getAngle(greyCentre, plateCentre);
        
        //Insert the new orientation in the orientation history:
        java.lang.Double[] robotOrientationHistory = worldState.getRobotOrientationHistory(rColour, rType);
        for (int i=0; i<robotOrientationHistory.length-1; i++) {
        	robotOrientationHistory[i] = robotOrientationHistory[i+1];
        }

        robotOrientationHistory[robotOrientationHistory.length-1] = orientation;
        worldState.setRobotOrientationHistory(new Robot(rColour, rType), robotOrientationHistory);
        
        /*------------------------------------------------------*/
        /* Get an average orientation based on the contents of  */ 
        /* the orientation history.								*/
        /*------------------------------------------------------*/
        
        // NB - we assume that the length of the history is 2!!! 
        // If that changes, then this thing BREAKS.
        double previousOrientation = robotOrientationHistory[0];

        // Find what's the angle difference between the past and current orientation. Half of that
        // is how much we need to correct the old angle to get the average of the two
        //
        // e.g. if old = 350 deg and new = 10 deg, then anglediff is +20 deg, half of which is 10 deg
        double angleCorrection = StrategyHelper.angleDiff(previousOrientation, orientation) / 2.0;
        
        // Add angle correction, then add 2pi to make sure we don't end up with a negative angle 
        // then take mod 2pi (360 deg) to make 2pi = 0 instead of 2pi and to correct for angles > 2pi
        double averagedOrientation = (previousOrientation + angleCorrection + C.A360) % C.A360;
        
        // ...continuing example from above.. so the averaged angle will be 350 + 10 = 360  degrees
        // we take the mod as well so 360 % 360 = 0 degrees
        // (ofc this is actually all in radians not degrees!)
        
        // Return averaged value
        return averagedOrientation;
	}
	
	/**
	 * Finds the angle between the 'from' and 'to' point.
	 * @param from The origin point of the angle calculation. Essentially the tail of the angle vector 
	 * @param to The destination point of the angle calculation. Essentially the head of the angle vector
	 * @return The angle in RADIANS
	 */
	public static double getAngle(Point2D.Double from, Point2D.Double to) {
        double angle = 0;
        
        if (from != null && to != null) {
        	double distance = Math.abs(from.distance(to));
        	double side = Math.abs(from.getX() - to.getX());
        	double triangleAngle = Math.acos(side/distance);
        	
	        //To get the angle need to establish the location of the "to" point in respect to the "from" point
        	if ((from.getX() >= to.getX()) && (from.getY() <= to.getY())) {
	        	// Quadrant 2 case
	        	angle = Math.PI - triangleAngle;
        	} else if ((from.getX() >= to.getX()) && (from.getY() >= to.getY())) {
	        	// Quadrant 3 case
	        	angle = Math.PI + triangleAngle;	
	        } else if ((from.getX() <= to.getX()) && (from.getY() >= to.getY())) {
	        	// Quadrant 4 case
	        	angle = 2*Math.PI - triangleAngle;
	        } else if ((from.getX() <= to.getX()) && (from.getY() <= to.getY())) {
	        	// Quadrant 1 case
	        	angle = triangleAngle;
	        } else {
	        	System.err.println("angle calculation failed");
	        }
        }        
        
        // Return mod 2pi, so 2pi should be 0 !!!
        return angle % C.A360;
	}
	
	/**
	 * Less precise version of getAngle(Point2D.Double, Point2D.Double)
	 */
	public static double getAngle(Point from, Point to) {
		// Make new Point2D.Double from the integer values, then run original function
		return getAngle(new Point2D.Double(from.getX(), from.getY()), new Point2D.Double(to.getX(), to.getY()));
	}
	
	/**
	 * Test Orientation class methods
	 */
	public static void main(String[] args) {
		/*     c     d
		 *      \   / 
		 *        o  - e
		 *      /   \                         f
		 *     b     a
		 */
		Point2D.Double o = new Point2D.Double(0, 0);
		Point2D.Double a = new Point2D.Double(1, 1);
		Point2D.Double b = new Point2D.Double(-1, 1);
		Point2D.Double c = new Point2D.Double(-1, -1);
		Point2D.Double d = new Point2D.Double(1, -1);
		Point2D.Double e = new Point2D.Double(1, 0);
		Point2D.Double f = new Point2D.Double(200, 1);

		System.out.println(Math.toDegrees(getAngle(o, a))); // 45
		System.out.println(Math.toDegrees(getAngle(o, b))); // 135
		System.out.println(Math.toDegrees(getAngle(o, c))); // 225
		System.out.println(Math.toDegrees(getAngle(o, d))); // 315
		System.out.println(Math.toDegrees(getAngle(o, e))); // 0
		System.out.println(Math.toDegrees(getAngle(o, f))); // ??? :D something really small
	}
}
