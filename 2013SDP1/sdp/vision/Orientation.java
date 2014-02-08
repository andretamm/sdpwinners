package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Finds the orientation by setting a line through the centre of the grey circle and the centre of a green plate.
 */

public class Orientation {
	
	public static double findOrient(ArrayList<Point> greyCircle, ArrayList<Point> greenPlate, ObjectPoints op) throws NoAngleException {
		// Calculate centre of grey circle points
        int totalX = 0;
        int totalY = 0;
        
        for (int i = 0; i < greyCircle.size(); i++) {
        	Point p = greyCircle.get(i);
        	double distanceFromRobotCentre = op.getRobotPosition().distance(new Point((int) p.getX(), (int) p.getY()));
        	
        	if (distanceFromRobotCentre <= WorldState.ballRadius+3) {
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
				System.err.println("No grey points!");
			}
        } else {
        	System.err.println("No points in grey circle");
        }
        
        Point2D greyCentre = new Point2D.Double(greyCentreX, greyCentreY);
        
        //Add up the values of the green pixels:
        int greenTotalX = 0;
        int greenTotalY = 0;
        for (int i = 0; i < greenPlate.size(); i++) {
            greenTotalX += greenPlate.get(i).getX();
            greenTotalY += greenPlate.get(i).getY();
        }
        
         
        // Centre of green plate
        double x0 = 0, y0 = 0;
        
        if (greenPlate.size() != 0) {
	        x0 = greenTotalX / greenPlate.size();
	        y0 = greenTotalY / greenPlate.size();
        } else {
        	System.err.println("No points in green plate!");
        }
        
        // USE ROBOT'S COORDINATES AS THE CENTRE OF THE GREEN PLATE INSTEAD
        x0 = op.getRobotPosition().getX();
        y0 = op.getRobotPosition().getY();
        Point2D plateCentre = new Point2D.Double(x0, y0);

        
        //Distance between grey centre and plate centre
        double distance = plateCentre.distance(greyCentre);
        
        //To get the angle need to establish the location of the grey circle with respect to the centre of the green plate.
        //Quadrant 1 case:
        if ((greyCentreX >= x0) && (greyCentreY >= y0)) {
        	return (Math.PI + Math.acos((greyCentreX-x0)/distance));
        }
        
        //Quadrant 2 case:
        if ((greyCentreX >= x0) && (greyCentreY <= y0)) {
        	return (Math.PI - Math.acos((greyCentreX-x0)/distance));
        }
        
        //Quadrant 3 case:
        if ((greyCentreX <= x0) && (greyCentreY <= y0)) {
        	return  Math.acos((x0-greyCentreX)/distance);
        }
        
        //Quadrant 4 case:
        if ((greyCentreX <= x0) && (greyCentreY >= y0)) {
        	return (2*Math.PI - Math.acos((x0-greyCentreX)/distance));
        }
        
        throw new NoAngleException();
	}

}
