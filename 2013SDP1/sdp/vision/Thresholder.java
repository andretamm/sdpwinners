package sdp.vision;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import constants.Colours;
import constants.Quadrant;

import sdp.vision.ThresholdsState;
import sdp.vision.WorldState;

public class Thresholder {
	
	private final static int plateSize = 35;//35
	
	/**
	 * Thresholds every point in the image, for ball red, robot yellow, robot blue, plate green and spot grey. The results are stored in op.
	 * @param image The image to be thresholded
	 * @param pp The results are stored here
	 * @param ts The thresholds to be used
	 * @param top Index of the top row
	 * @param bottom Index of the bottom row, plus one
	 * @param left Index of the leftmost column
	 * @param right Index of the rightmost column, plus one
	 */
	public static void simpleThresholds(BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState worldState, int top, int bottom, int left, int right) {
		
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
					pp.getPoints(Colours.GRAY).add(new Point(column, row));
				}

				if (ts.isBlue(c, hsbvals, rg, rb, gb)) {
					pp.getPoints(Colours.BLUE).add(new Point(column, row));
				}

				if (ts.isGreen(c, hsbvals, rg, rb, gb)) {
					pp.getPoints(Colours.GREEN).add(new Point(column, row));
				}

				if (ts.isYellow(c, hsbvals, rg, rb, gb)) {
					pp.getPoints(Colours.YELLOW).add(new Point(column, row));
				}
				
				if (ts.isBall(c, hsbvals, rg, rb, gb)) {
					pp.getPoints(Colours.RED).add(new Point(column, row));
				}
			}
		}
	}
	
	/**
	 * Thresholds every point in the image, for ball red, robot yellow and robot blue. The results are stored in op.
	 * @param image The image to be thresholded
	 * @param pp The results are stored here
	 * @param ts The thresholds to be used
	 * @param top Index of the top row
	 * @param bottom Index of the bottom row, plus one
	 * @param left Index of the leftmost column
	 * @param right Index of the rightmost column, plus one
	 */
	public static void initialThresholds(BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState ws) {
		
		int rg;
		int rb;
		int gb;
		
		/*
		 * For every pixel within the pitch, test to see if it belongs to the ball, the yellow T, or the blue T.
		 */
		
		// For Q1
		
		int qLow = 0, qHigh = 0;
		
		for (Quadrant q : Quadrant.values()) {
			
			if(q == Quadrant.Q1){
				qLow = ws.getQ1LowX();
				qHigh = ws.getQ1HighX();
			}
			else if(q == Quadrant.Q2){
				qLow = ws.getQ2LowX();
				qHigh = ws.getQ2HighX();
			}
			else if(q == Quadrant.Q3){
				qLow = ws.getQ3LowX();
				qHigh = ws.getQ3HighX();
			}
			else if(q == Quadrant.Q4){
				qLow = ws.getQ4LowX();
				qHigh = ws.getQ4HighX();
			}
			
			for (int column= qLow; column< qHigh; column++) {
	        	for (int row= ws.getPitchTopLeft().y; row < ws.getPitchBottomLeft().y; row++) {
					
					/* The RGB colours and hsv values for the current pixel. */
					Color c = new Color(image.getRGB(column, row));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
					rg=c.getRed()-c.getGreen();
					rb=c.getRed()-c.getBlue();
					gb=c.getGreen()-c.getBlue();

					if (ts.isBlue(c, hsbvals, rg, rb, gb)) {
						pp.getQuadrant(q).getPoints(Colours.BLUE).add(new Point(column, row));
					}

					if (ts.isYellow(c, hsbvals, rg, rb, gb)) {
						pp.getQuadrant(q).getPoints(Colours.YELLOW).add(new Point(column, row));
					}
					
					if (ts.isBall(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.RED).add(new Point(column, row));
					}
				}
			}
		}
	}

	/**
	 * Thresholds points near the robot T's for plate green and spot grey. The results are stored in op.
	 * @param image The image to be thresholded
	 * @param pp The results are stored here
	 * @param ts The thresholds to be used
	 * @param worldState Contains the current robot positions.
	 * @param top Index of the top row
	 * @param bottom Index of the bottom row, plus one
	 * @param left Index of the leftmost column
	 * @param right Index of the rightmost column, plus one
	 */
	public static void secondaryThresholds(BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState worldState, int top, int bottom, int left, int right) {
		
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
						pp.getPoints(Colours.GREEN).add(new Point(column, row));
					}

					if (ts.isGrey(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.GRAY).add(new Point(column, row));
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
						pp.getPoints(Colours.GREEN).add(new Point(column, row));
					}

					if (ts.isGrey(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.GRAY).add(new Point(column, row));
					}
				} catch (Exception e) {
					//point was outside the image?
				}
			}
		}
	}
}
