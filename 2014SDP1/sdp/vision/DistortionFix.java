package sdp.vision;


import java.awt.image.BufferedImage;
import java.awt.Point;

/**
 * Class to remove barrel distortion from bufferedimages
 * 
 * @author Rado
 * @author James Hulme
 * 
 */


public class DistortionFix {
	
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
}
