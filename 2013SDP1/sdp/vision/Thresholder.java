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

public class Thresholder{
	
	private final static int plateSize = 20;//35
	
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
	
	private static class ThresholderThread implements Runnable {
		
		private BufferedImage image;
		private PitchPoints pp;
		private ThresholdsState ts;
		private WorldState ws;
		private Quadrant q;
		private int qLow;
		private int qHigh;
		
		public ThresholderThread (BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState ws, Quadrant q, int qLow, int qHigh){
			this.image = image;
			this.pp = pp;
			this.ts = ts;
			this.ws = ws;
			this.q = q;
			this.qLow = qLow;
			this.qHigh = qHigh;
		}
		
		public void run() {
			int rg;
			int rb;
			int gb;
			
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
						pp.getQuadrant(q).getPoints(Colours.RED).add(new Point(column, row));
					}
				}
			}
//			System.out.println("Thread Running Quadrant:" + q);
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
	public static void initialThresholds(BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState ws){
	
		/*
		 * For every pixel within the pitch, test to see if it belongs to the ball, the yellow T, or the blue T.
		 */
		Thread q1Thread = new Thread(new ThresholderThread(image, pp, ts, ws, Quadrant.Q1, ws.getQ1LowX(), ws.getQ1HighX()));
		Thread q2Thread = new Thread(new ThresholderThread(image, pp, ts, ws, Quadrant.Q2, ws.getQ2LowX(), ws.getQ2HighX()));
		Thread q3Thread = new Thread(new ThresholderThread(image, pp, ts, ws, Quadrant.Q3, ws.getQ3LowX(), ws.getQ3HighX()));
		Thread q4Thread = new Thread(new ThresholderThread(image, pp, ts, ws, Quadrant.Q4, ws.getQ4LowX(), ws.getQ4HighX()));
		
		q1Thread.start();
		q2Thread.start();
		q3Thread.start();
		q4Thread.start();
		
		try {
			q1Thread.join();
			q2Thread.join();
			q3Thread.join();
			q4Thread.join();
		} catch (Exception e) {
			System.err.println("Thresholding threads killed prematurely!");
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
	public static void secondaryThresholds(BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState worldState) {

		int rg;
		int rb;
		int gb;

		/*
		 * For each quadrant, in the list of Quadrant enums, look at the plates
		 */
		for(Quadrant q : Quadrant.values()){
			
			ObjectPoints quadrant = pp.getQuadrant(q);
			
			/*
			 * For every pixel near the blue i, test to see if it belongs to either a green plate or a grey circle.
			 */
			for(int column = quadrant.getRobotPosition().x - plateSize; column < quadrant.getRobotPosition().x + plateSize; column++){
				for(int row = quadrant.getRobotPosition().y - plateSize; row < quadrant.getRobotPosition().y + plateSize; row++){
					try {
						Color c = new Color(image.getRGB(column, row));
						float hsbvals[] = new float[3];
						Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
						
						rg=c.getRed()-c.getGreen();
						rb=c.getRed()-c.getBlue();
						gb=c.getGreen()-c.getBlue();
						
						if (ts.isGreen(c, hsbvals, rg, rb, gb)) {
							quadrant.getPoints(Colours.GREEN).add(new Point(column, row));
						}

						if (ts.isGrey(c, hsbvals, rg, rb, gb)) {
							quadrant.getPoints(Colours.GRAY).add(new Point(column, row));
						}
					} catch (Exception e) {
						//point was outside the image?
					}
				}
			}
		}
	}
}
