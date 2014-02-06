package sdp.vision;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Finds the orientation by setting a line through the centre of the grey circle and the centre of a green plate.
 */

public class Orientation {
	
	public static double findOrient(ArrayList<Point> greyCircle, ArrayList<Point> greenPlate) throws NoAngleException {
		
		// Calculate centre of grey circle points
        int totalX = 0;
        int totalY = 0;
        for (int i = 0; i < greyCircle.size(); i++) {
            totalX += greyCircle.get(i).getX();
            totalY += greyCircle.get(i).getY();
        }

        // Centre of grey circle
        double greyCentreX = totalX / greyCircle.size();
        double greyCentreY = totalY / greyCircle.size();
        
        Point2D greyCentre = new Point2D.Double(greyCentreX, greyCentreY);
        
        //Centre of green plate:
        int greenTotalX = 0;
        int greenTotalY = 0;
        for (int i = 0; i < greenPlate.size(); i++) {
            greenTotalX += greenPlate.get(i).getX();
            greenTotalY += greenPlate.get(i).getY();
        }

        // Centre of grey circle
        double x0 = greenTotalX / greenPlate.size();
        double y0 = greenTotalY / greenPlate.size();
        
        Point2D plateCentre = new Point2D.Double(x0, y0);
        
        //Distance between grey centre and plate centre
        double distance = plateCentre.distance(greyCentre);
        
        //To get the angle need to establish the location of the grey circle with respect to the centre of the green plate.
        //Quadrant 1 case:
        if ((greyCentreX >= x0) && (greyCentreY >= y0)) {
        	return Math.acos((greyCentreX-x0)/distance);
        }
        
        //Quadrant 2 case:
        if ((greyCentreX >= x0) && (greyCentreY <= y0)) {
        	return (Math.PI - Math.acos((greyCentreX-x0)/distance));
        }
        
        //Quadrant 3 case:
        if ((greyCentreX <= x0) && (greyCentreY <= y0)) {
        	return Math.acos((x0-greyCentreX)/distance);
        }
        
        //Quadrant 4 case:
        if ((greyCentreX <= x0) && (greyCentreY >= y0)) {
        	return (Math.PI - Math.acos((x0-greyCentreX)/distance));
        }
        
        throw new NoAngleException();
	}

}
