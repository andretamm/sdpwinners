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
	//private static final double barrelCorrectionX = -0.4;
	//private static final double barrelCorrectionY = -0.2;
	//private static final double barrelCorrectionX = 0.1;
	//private static final double barrelCorrectionY = 0.85;
	private static final double barrelCorrectionX = -0.016;	
	//private static final double barrelCorrectionY = -0.032;	
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
    			//if (left <= p.x && p.x < right
    			//		 && top <=  p.y&& p.y < bottom ){
    			//if (0 <= p.x && p.x < image.getWidth()
    			//		 && 0 <=  p.y&& p.y < image.getHeight() ){
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

	/* Doesn't work */
	/*
	 * private void calculateDistortion() { this.xDistortion = new int[640];
	 * this.yDistortion = new int[480];
	 * 
	 * int centerX = 320; int centerY = 240; float k = (float) 0.01;
	 * 
	 * for (int i = 0; i < 480; i++) { for (int j = 0; j < 640; j++) { int x =
	 * (int) Math.floor(getRadialX(j, i, centerX, centerY, (float) Math.pow(k,
	 * 2))); int y = (int) Math.floor(getRadialY(j, i, centerX, centerY, (float)
	 * Math.pow(k, 2)));
	 * 
	 * if (y >= 480) { y = 240; } if (x >= 640) { x = 320; }
	 * 
	 * xDistortion[j] = x; yDistortion[i] = y; } } }
	 */
}
