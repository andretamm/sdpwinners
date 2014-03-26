package sdp.vision;


import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.Point;

import behavior.StrategyHelper;

/**
 * Class to remove barrel distortion from bufferedimages
 * 
 * @author Rado
 * @author James Hulme
 * 
 */


public class DistortionFix {
	
	private static final double distCameraPitch = 270;
	private static final Point cameraPos = new Point(323,211);
	private static int width = 640;
	private static int height = 480;
	private static final double barrelCorrectionX = -0.016;	
	private static final double barrelCorrectionY = -0.115;


	/**
	 * Remove barrel distortion on whole image
	 * 
	 * Buffers should be used so we only correct the pitch area not the useless background area
	 * 
	 * @param image Frame to correct
	 * @return A new image with no barrel distortion
	 */
    public static BufferedImage removeBarrelDistortion(BufferedImage image){
    	
    	BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    	Point p;

		for (int row = 0; (row < image.getHeight()); row++) {
			for (int column = 0; (column < image.getWidth()); column++) {
    			p = barrelCorrected(new Point(column, row));
    			
    			if(0 <= p.x && p.x < image.getWidth() && 0 <=  p.y&& p.y < image.getHeight() ){
    			newImage.setRGB(p.x,p.y, image.getRGB(column, row));
    		    }
			}
		}
		for (int row = 0; (row < image.getHeight()); row++) {
			for (int column = 0; (column < image.getWidth()); column++) {    			
    			try{
    				image.setRGB(column, row, newImage.getRGB(column,row));
    		    }
    			catch(Exception e) {}
			}
		}
    	
        return image;
    }
    
    /**
     * Correct for single points
     * 
     * Called by the above function, but also called when overlay is turned off
     * 
     * @param p1 Point to fix
     * @return Fixed Point
     */
    public static Point barrelCorrected(Point p1) {
    	// System.out.println("Pixel: (" + x + ", " + y + ")");
    	// first normalise pixel
    	double px = (2 * p1.x - width) / (double) width;
    	double py = (2 * p1.y - height) / (double) height;

    	// System.out.println("Norm Pixel: (" + px + ", " + py + ")");
    	// then compute the radius of the pixel you are working with
    	double rad = px * px + py * py;

    	// then compute new pixel'
    	double px1 = px * (1 - barrelCorrectionX * rad);
    	double py1 = py * (1 - barrelCorrectionY * rad);

    	// then convert back
    	int pixi = (int)Math.round ((px1 + 1) * width / 2);
    	int pixj = (int)Math.round ((py1 + 1) * height / 2);
    	// System.out.println("New Pixel: (" + pixi + ", " + pixj + ")");
    	return new Point(pixi, pixj);
    	}
    
    /**
     * 
     * @param p1 Point to fix
     * @return The corrected Point
     */
    public static Point perspectiveFix(int height, Point p1){
    	double distanceFix;
    	double distanceFromCamera = Math.sqrt(Position.sqrdEuclidDist(cameraPos.x, cameraPos.y, p1.x, p1.y));
    	distanceFix = (height*distanceFromCamera)/distCameraPitch;
    	
    	double angle = 0.0;
    	try {
			angle = Position.angleTo(cameraPos, p1);
		} catch (NoAngleException e) {
			e.printStackTrace();
		}
    	
		int x = (int)((distanceFromCamera - distanceFix)*Math.cos(angle));
		int y = (int)((distanceFromCamera - distanceFix)*Math.sin(angle));
		
		return (new Point(x,y));
    }

    public static Point AndrePerspectiveFix(Point p) {
    	/* Side-view of the perspective fix
    	 * 
    	 * camera
    	 *   |\
    	 *   | \
    	 *   | _\_
    	 *   | | \|
    	 *   | |  \
    	 *   | |  |\
    	 *   @--x---y
    	 *      <----
    	 *      
    	 * Top view of the perspective fix
    	 *   @ -------- x ----- y
    	 * camera     robot   vision (where the vision says the robot is)
    	 *              <--------
    	 *          errorCorrectionVector
    	 */
    	double distFromCentre = StrategyHelper.getDistance(cameraPos, p);
    	Point2D.Double vector = StrategyHelper.normaliseVector(new Point2D.Double(cameraPos.x - p.x, cameraPos.y - p.y));
    	double robotHeight = 20;

    	double error = robotHeight * distFromCentre / distCameraPitch;

    	Point2D.Double errorCorrectionVector = StrategyHelper.multiplyVector(vector, error);

    	Point result = StrategyHelper.addVectorToPoint(errorCorrectionVector, p);

    	return result;
    }
}
