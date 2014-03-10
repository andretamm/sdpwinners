package vision;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Normalisation {
        
        /**
         * Normalises color given.
         * 
         * @param color
         * @return normalised color
         */
        private static Color normaliseColour(Color color) {
                int red, blue, green, sum;
                red = color.getRed();
                blue = color.getBlue();
                green = color.getGreen();
                sum = red + green + green;
                float normalise = (float) (255.0 / sum);
                Color normalisedColor = new Color((int) (normalise * red), (int) (normalise * green), (int) (normalise * blue));
                return normalisedColor;
        }

        public static void normaliseBufferedImage(BufferedImage img, int top, int bottom, int left, int right) {
        	//For every point, normalise it
    		for (int column= left; column< right; column++) {
            	for (int row= top; row< bottom; row++) {
    				//System.out.println("normalising pixel "+column+" "+row);
    				Color c = new Color(img.getRGB(column, row));
					c = Normalisation.normaliseColour(c);
					img.setRGB(column, row, c.getRGB());
    			}
    		}
    	}
}