package vision;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * Contains the necessary methods for subtracting backgrounds from the camera image
 */
public class BackgroundSubtraction {
	
	BufferedImage bgImg = null;
	
	public BackgroundSubtraction() {
		
        try {
        	System.out.println("Reading Image now");
            bgImg = ImageIO.read(new File("./constants/pitchBackground.bmp"));
        } catch(IOException e) {
            System.out.println("Read Error:"+e.getMessage());
            System.exit(0);
        }
		
	}

	
	/**
     * Subtracts the background image from that passed to it. The
     * background image is loaded in initFrameGrabber.
     * 
     * @param currentImg		The image to be subtracted from.
     * @throws Exception 
     */
    public BufferedImage subtractBackground(BufferedImage currentImg, int top, int bottom, int left, int right) throws Exception {
    	if (bgImg==null) {
    		throw new Exception("Can't subtract background as bgImg==null");
    	}
    	else {
    		for (int i= left; i< right; i++) {
            	for (int j= top; j< bottom; j++) {
            		Color c = new Color(currentImg.getRGB(i,j));

            		//System.out.println("Colour before="+c);
            		int cred = c.getRed();
            		int cgreen = c.getGreen();
            		int cblue = c.getBlue();
            		
            		//Subtract the background
            		Color cBack = new Color(bgImg.getRGB(i, j));
            		int backRed = cBack.getRed();
            		int backGreen = cBack.getGreen();
            		int backBlue = cBack.getBlue();
            		
            		cred = loopback(cred-backRed);
            		cgreen = loopback(cgreen-backGreen);
            		cblue = loopback(cblue-backBlue);
            		try {
            			currentImg.setRGB(i, j, (new Color(cred, cgreen, cblue)).getRGB());
            			//currentImg.setRGB(i, j, (bgImg.getRGB(i, j)));
            		}
            		catch(Exception e) {
            			System.out.println("Error subtracting background pixels:"+e.getMessage()); 
            			System.out.println("cred="+cred+"; cgreen="+cgreen+"; cblue="+cblue+";");
            		}
            		//System.out.println("Colour after="+new Color(img.getRGB(i, j)));
            	}
            }
        	return currentImg;
        }
    }
	
    /**
     * Helper function for imageStandardization. Ensures that the result of a background subtraction upon a pixel's R G or B hex value
     * remains within an acceptable range.
     */
	private int loopback(int hexValue) {
		return (int)128+(hexValue/2);
	}    
    
	/**
	 * Standardizes the R, G and B values for each pixel.
	 * @param img The image to be standardized.
	 * */
    public BufferedImage imageStandardization(BufferedImage img, int top, int bottom, int left, int right) {
    	int total=0;
    	int maxr=0;
    	int maxb=0;
    	int maxg=0;
    	Color c;
		for (int j = top; j < bottom; j++) {
			for (int i = left; i < right; i++) {
				c = new Color(img.getRGB(i,j));
				total=total+1;
				if (c.getRed()>maxr){maxr=c.getRed();}
				if (c.getGreen()>maxg){maxg=c.getGreen();}
				if (c.getBlue()>maxb){maxb=c.getBlue();}
    		}
    	}
    	int r;
    	int b;
    	int g;
		for (int j = top; j < bottom; j++) {
			for (int i = left; i < right; i++) {
				c = new Color(img.getRGB(i,j));
				r=(int) ((255*c.getRed())/maxr);
				b=(int) ((255*c.getBlue())/maxb);
				g=(int) ((255*c.getGreen())/(maxg));
				img.setRGB(i, j, (new Color(r, b, g)).getRGB());
				total=total+1;
    		}
    	}
    	return img;
    }
}
