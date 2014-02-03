package sdp.vision;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sdp.vision.ThresholdsState;
import sdp.vision.WorldState;

public class Thresholder {
	
	private final static int plateSize = 35;//35
	
	/**
	 * Thresholds every point in the image, for ball red, robot yellow, robot blue, plate green and spot grey. The results are stored in op.
	 * @param image The image to be thresholded
	 * @param op The results are stored here
	 * @param ts The thresholds to be used
	 * @param top Index of the top row
	 * @param bottom Index of the bottom row, plus one
	 * @param left Index of the leftmost column
	 * @param right Index of the rightmost column, plus one
	 */
	public static void simpleThresholds(BufferedImage image, ObjectPoints op, ThresholdsState ts, WorldState worldState, int top, int bottom, int left, int right) {
		
		int rg;
		int rb;
		int gb;
		
		/*
		 * For every pixel within the pitch, test to see if it belongs to the
		 * ball, the yellow T, the blue T, either green plate or a grey circle.
		 */
		for (int column= left; column< right; column++) {
        	for (int row= top; row< bottom; row++) {
				
				/* The RGB colours and hsv values for the current pixel. */
				Color c = new Color(image.getRGB(column, row));
				float hsbvals[] = new float[3];
				Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
				
				rg=c.getRed()-c.getGreen();
				rb=c.getRed()-c.getBlue();
				gb=c.getGreen()-c.getBlue();

				if (ts.isGrey(c, hsbvals, rg, rb, gb)) {
					op.getGreyPoints().add(new Point(column, row));
				}

				if (ts.isBlue(c, hsbvals, rg, rb, gb)) {
					op.getBluePoints().add(new Point(column, row));
				}

				if (ts.isGreen(c, hsbvals, rg, rb, gb)) {
					op.getGreenPoints().add(new Point(column, row));
				}

				if (ts.isYellow(c, hsbvals, rg, rb, gb)) {
					op.getYellowPoints().add(new Point(column, row));
				}
				
				if (ts.isBall(c, hsbvals, rg, rb, gb)) {
					op.getBallPoints().add(new Point(column, row));
				}
			}
		}
	}
	
	/**
	 * Thresholds every point in the image, for ball red, robot yellow and robot blue. The results are stored in op.
	 * @param image The image to be thresholded
	 * @param op The results are stored here
	 * @param ts The thresholds to be used
	 * @param top Index of the top row
	 * @param bottom Index of the bottom row, plus one
	 * @param left Index of the leftmost column
	 * @param right Index of the rightmost column, plus one
	 */
	public static void initialThresholds(BufferedImage image, ObjectPoints op, ThresholdsState ts, int top, int bottom, int left, int right) {
		
		int rg;
		int rb;
		int gb;
		
		/*
		 * For every pixel within the pitch, test to see if it belongs to the ball, the yellow T, or the blue T.
		 */
		for (int column= left; column< right; column++) {
        	for (int row= top; row< bottom; row++) {
				
				/* The RGB colours and hsv values for the current pixel. */
				Color c = new Color(image.getRGB(column, row));
				float hsbvals[] = new float[3];
				Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
				rg=c.getRed()-c.getGreen();
				rb=c.getRed()-c.getBlue();
				gb=c.getGreen()-c.getBlue();

				if (ts.isBlue(c, hsbvals, rg, rb, gb)) {
					op.getBluePoints().add(new Point(column, row));
				}

				if (ts.isYellow(c, hsbvals, rg, rb, gb)) {
					op.getYellowPoints().add(new Point(column, row));
				}
				
				if (ts.isBall(c, hsbvals, rg, rb, gb)) {
					op.getBallPoints().add(new Point(column, row));
				}
			}
		}
	}

	/**
	 * Thresholds points near the robot T's for plate green and spot grey. The results are stored in op.
	 * @param image The image to be thresholded
	 * @param op The results are stored here
	 * @param ts The thresholds to be used
	 * @param worldState Contains the current robot positions.
	 * @param top Index of the top row
	 * @param bottom Index of the bottom row, plus one
	 * @param left Index of the leftmost column
	 * @param right Index of the rightmost column, plus one
	 */
	public static void secondaryThresholds(BufferedImage image, ObjectPoints op, ThresholdsState ts, WorldState worldState, int top, int bottom, int left, int right) {
		
		int rg;
		int rb;
		int gb;
		
		/*
		 * For every pixel near the blue T, test to see if it belongs to either a green plate or a grey circle.
		 */
		for (int column= worldState.getBlueDefenderXVision()-plateSize; column< worldState.getBlueDefenderXVision()+plateSize; column++) {
        	for (int row= worldState.getBlueDefenderYVision()-plateSize; row< worldState.getBlueDefenderYVision()+plateSize; row++) {
				try {
					/* The RGB colours and hsv values for the current pixel. */
					Color c = new Color(image.getRGB(column, row));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
//					if (worldState.getRemoveShadows()) {
//						int correctedY=(int) DistortionFix.barrelCorrected(new Point(column, row)).getY();
//						if ((correctedY<worldState.getShadowTopY()) || (correctedY>worldState.getShadowBottomY())) {
//							// if in shadowed area
//							c=Deshadow.forceDeshadowPoint(c, hsbvals);
//							image.setRGB(column, row, c.getRGB());
//						}
//					}
					rg=c.getRed()-c.getGreen();
					rb=c.getRed()-c.getBlue();
					gb=c.getGreen()-c.getBlue();

					if (ts.isGreen(c, hsbvals, rg, rb, gb)) {
						op.getGreenPoints().add(new Point(column, row));
					}

					if (ts.isGrey(c, hsbvals, rg, rb, gb)) {
						op.getGreyPoints().add(new Point(column, row));
					}
				} catch (Exception e) {
					//point was outside the image?
				}
			}
		}
		
		/*
		 * For every pixel near the yellow T, test to see if it belongs to either a green plate or a grey circle.
		 */
		for (int column= worldState.getYellowDefenderXVision()-plateSize; column< worldState.getYellowDefenderXVision()+plateSize; column++) {
        	for (int row= worldState.getYellowDefenderYVision()-plateSize; row< worldState.getYellowDefenderYVision()+plateSize; row++) {
				try {
					/* The RGB colours and hsv values for the current pixel. */
					Color c = new Color(image.getRGB(column, row));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
//					if (worldState.getRemoveShadows()) {
//						int correctedY=(int) DistortionFix.barrelCorrected(new Point(column, row)).getY();
//						if ((correctedY<worldState.getShadowTopY()) || (correctedY>worldState.getShadowBottomY())) {
//							// if in shadowed area
//							c=Deshadow.forceDeshadowPoint(c, hsbvals);
//							image.setRGB(column, row, c.getRGB());
//						}
//					}
					rg=c.getRed()-c.getGreen();
					rb=c.getRed()-c.getBlue();
					gb=c.getGreen()-c.getBlue();

					if (ts.isGreen(c, hsbvals, rg, rb, gb)) {
						op.getGreenPoints().add(new Point(column, row));
					}

					if (ts.isGrey(c, hsbvals, rg, rb, gb)) {
						op.getGreyPoints().add(new Point(column, row));
					}
				} catch (Exception e) {
					//point was outside the image?
				}
			}
		}
	}
}
