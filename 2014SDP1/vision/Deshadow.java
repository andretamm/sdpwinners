package vision;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Deshadow {
	
	/**
	 * For every pixel in the image, will assume it to be shadowed if it's brightness is below bThresh. 
	 * If it is shadowed, scale up the brightness to a similar level to the normal pixels
	 * @param image The image to remove shadow from.
	 * @param top Top of the deshadow area
	 * @param bottom Bottom of the deshadow area
	 * @param left Left of the deshadow area
	 * @param right Right of the deshadow area
	 */
	public static void deshadowImage(WorldState worldState, BufferedImage image, int top, int bottom, int left, int right) {
		for (int column= left; column< right; column++) {
        	for (int row= top; row< bottom; row++) {
        		//image.setRGB(column, row, deshadowPoint(image.getRGB(column, row)));
				if (worldState.getRemoveShadows()) {
					int correctedY=(int) DistortionFix.barrelCorrected(new Point(column, row)).getY();
					if ((correctedY<worldState.getShadowTopY()) || (correctedY>worldState.getShadowBottomY())) {
						// if in shadowed area
						Color colour = new Color(image.getRGB(column, row));
						colour=Deshadow.forceDeshadowPoint(colour, hsbvals);
						image.setRGB(column, row, colour.getRGB());
					}
				}
        	}
        }
	}

	private static double c=1.25;
	private static double bThresh=0.42;
    private static float[] hsbvals = new float[3];
    private static Color colour;
    
    /**
     * Takes the colour and hsbvals of a pixel, and updates the hsbvals and returns the new colour to remove shadowing
     * @param colourOut
     * @param hsbvals2Out
     *//*
    public static void deshadowPoint(Color colourOut, float[] hsbvals2Out) {
		if (hsbvals2Out[2]<bThresh) {
			// is shadowed
			int r=(int) (colourOut.getRed()*c);
			r=Math.min(r, 255);
			int g=(int) (colourOut.getGreen()*c);
			g=Math.min(g, 255);
			int b=(int) (colourOut.getBlue()*c);
			b=Math.min(b, 255);
			colourOut = new Color(r, g, b);
		    Color.RGBtoHSB(colourOut.getRed(), colourOut.getBlue(), colourOut.getGreen(), hsbvals2Out);
		}
    }*/
    public static Color deshadowPoint(Color colourOut, float[] hsbvals2Out) {
		if (hsbvals2Out[2]<bThresh) {
			// is shadowed
			colourOut=forceDeshadowPoint(colourOut, hsbvals2Out);
		    return colourOut;
		} else { return colourOut;}
    }

    public static Color forceDeshadowPoint(Color colourOut, float[] hsbvals2Out) {
		// is shadowed
		int r=(int) (colourOut.getRed()*c);
		r=Math.min(r, 255);
		int g=(int) (colourOut.getGreen()*c);
		g=Math.min(g, 255);
		int b=(int) (colourOut.getBlue()*c);
		b=Math.min(b, 255);
		colourOut = new Color(r,g,b);
	    Color.RGBtoHSB(colourOut.getRed(), colourOut.getBlue(), colourOut.getGreen(), hsbvals2Out);
	    return colourOut;
    }

	
	/**
	 * Takes the rgb hex integer value of a pixel, and returns the rgb hex integer value of that pixel but with shadow removed.
	 * @param pixel
	 * @return
	 */
	public static int deshadowPoint(int pixel) {
		colour = new Color(pixel);
		//System.out.println("1:colour.getRGB()"+colour.getRGB());
	    Color.RGBtoHSB(colour.getRed(), colour.getBlue(), colour.getGreen(), hsbvals);
	    colour=deshadowPoint(colour, hsbvals);
		//System.out.println("2:colour.getRGB()"+colour.getRGB());
		//System.out.println();
	    return colour.getRGB();
	}
}
