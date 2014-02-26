package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Finds the orientation by setting a line through the centre of the grey circle and the centre of a green plate.
 */

public class Orientation {
	
	/**
	 * Finds the orientation of the robot in the given quadrant in RADIANS.
	 * @param greyCircle All the grey points in the quadrant
	 * @param greenPlate All the green points in the quadrant
	 * @param qp The QuadrantPoints of the quadrant
	 * @return The angle the robot is facing in RADIANS
	 * @throws NoAngleException
	 */
	public static double findRobotOrientation(ArrayList<Point> greyCircle, ArrayList<Point> greenPlate, QuadrantPoints qp) throws NoAngleException {
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
	        greyCentreX = totalX / greyCircle.size();
	        greyCentreY = totalY / greyCircle.size();
	               
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
//        if (qp.getrType() == RobotType.DEFENDER && qp.getrColour() == RobotColour.BLUE) {
//        	System.out.println(x0 + " " + y0 + " | " + greyCentreX + " " + greyCentreY);
//        }
        return getAngle(greyCentre, plateCentre);
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
//	        	System.out.println("4");
	        	angle = 2*Math.PI - triangleAngle;
	        } else if ((from.getX() <= to.getX()) && (from.getY() <= to.getY())) {
	        	// Quadrant 1 case
//	        	System.out.println("1");
//	        	System.out.println(from.x + " " + from.y + " | " + to.x + " " + to.y);
	        	angle = triangleAngle;
	        }
        }        
        return angle;
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
		Point2D.Double o = new Point2D.Double(0, 0);
		Point2D.Double a = new Point2D.Double(1, 1);
		Point2D.Double b = new Point2D.Double(-1, 1);
		Point2D.Double c = new Point2D.Double(-1, -1);
		Point2D.Double d = new Point2D.Double(1, -1);

		System.out.println(Math.toDegrees(getAngle(o, a))); // 45
		System.out.println(Math.toDegrees(getAngle(o, b))); // 135
		System.out.println(Math.toDegrees(getAngle(o, c))); // 225
		System.out.println(Math.toDegrees(getAngle(o, d))); // 315
	}
}
