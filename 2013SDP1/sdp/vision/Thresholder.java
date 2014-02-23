package sdp.vision;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import constants.Colours;
import constants.Quadrant;
import constants.QuadrantX;

import sdp.vision.ThresholdsState;
import sdp.vision.WorldState;

public class Thresholder{
	
	public static final int plateSize = 14;//35
	public static final double threshValue = 196.0;
	
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
		for (Quadrant q : Quadrant.values()){
			for (int column= worldState.getQuadrantX(q, QuadrantX.LOW); column< worldState.getQuadrantX(q, QuadrantX.HIGH); column++) {
				for(int row = worldState.getPitchTopLeft().y; row < worldState.getPitchBottomLeft().y; row++){
					
					/* The RGB colours and hsv values for the current pixel. */
					Color c = new Color(image.getRGB(column, row));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
					
					rg=c.getRed()-c.getGreen();
					rb=c.getRed()-c.getBlue();
					gb=c.getGreen()-c.getBlue();
	
					if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.GRAY).isColour(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.GRAY).add(new Point(column, row));
					}
	
					if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.BLUE).isColour(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.BLUE).add(new Point(column, row));
					}
	
					if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.GREEN).isColour(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.GREEN).add(new Point(column, row));
					}
	
					if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.YELLOW).isColour(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.YELLOW).add(new Point(column, row));
					}
					
					if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.RED).isColour(c, hsbvals, rg, rb, gb)) {
						pp.getPoints(Colours.RED).add(new Point(column, row));
					}
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
		private Point plateCentroid;
		
		public ThresholderThread (BufferedImage image, PitchPoints pp, ThresholdsState ts, WorldState ws, Quadrant q, int qLow, int qHigh){
			this.image = image;
			this.pp = pp;
			this.ts = ts;
			this.ws = ws;
			this.q = q;
			try {
				this.plateCentroid = Position.findMean(pp.getQuadrant(q).getPoints(Colours.GREEN));
			} catch (Exception e) {
//				System.err.println("No green points: ThresholdThread");;
			}
		}
		
		public void run() {
			int rg;
			int rb;
			int gb;
			
//			System.out.println(q + " " + plateCentroid.x + " " + plateCentroid.y);
			
			for (int column= plateCentroid.x - plateSize; column < plateCentroid.x + plateSize; column++) {
	        	for (int row= plateCentroid.y - plateSize; row < plateCentroid.y + plateSize; row++) {
					
	        		//System.out.println(Position.sqrdEuclidDist(plateCentroid.x, plateCentroid.y, column, row) + ": " + column + " " + row + " : " + plateCentroid.x + plateSize);
	        		if (Position.sqrdEuclidDist(plateCentroid.x, plateCentroid.y, column, row) <= threshValue) {
						/* The RGB colours and hsv values for the current pixel. */
						Color c = new Color(image.getRGB(column, row));
						float hsbvals[] = new float[3];
						Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
						rg=c.getRed()-c.getGreen();
						rb=c.getRed()-c.getBlue();
						gb=c.getGreen()-c.getBlue();
						
						if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.GRAY).isColour(c, hsbvals, rg, rb, gb)) {
							pp.getQuadrant(q).getPoints(Colours.GRAY).add(new Point(column, row));
						}
	
						if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.BLUE).isColour(c, hsbvals, rg, rb, gb)) {
							pp.getQuadrant(q).getPoints(Colours.BLUE).add(new Point(column, row));
						}
	
						if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.YELLOW).isColour(c, hsbvals, rg, rb, gb)) {
							pp.getQuadrant(q).getPoints(Colours.YELLOW).add(new Point(column, row));
						}
					
					}
				}
			}
			
			
//			System.out.println(plateCentroid);
			
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

			/*
			 * For every pixel in the quadrant check to see if the pixel belongs to a green plate or a ball
			 */
			for(int column = worldState.getQuadrantX(q, QuadrantX.LOW); column < worldState.getQuadrantX(q, QuadrantX.HIGH); column++){
				for(int row = worldState.getPitchTopLeft().y; row < worldState.getPitchBottomLeft().y; row++){
					try {
						Color c = new Color(image.getRGB(column, row));
						float hsbvals[] = new float[3];
						Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
						
						rg=c.getRed()-c.getGreen();
						rb=c.getRed()-c.getBlue();
						gb=c.getGreen()-c.getBlue();
						
						if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.GREEN).isColour(c, hsbvals, rg, rb, gb)) {
							pp.getQuadrant(q).getPoints(Colours.GREEN).add(new Point(column, row));
						}
						
						if (ts.getQuadrantThresholds(q).getObjectThresholds(Colours.RED).isColour(c, hsbvals, rg, rb, gb)) {
							pp.getQuadrant(q).getPoints(Colours.RED).add(new Point(column, row));
						}
					} catch (Exception e) {
						//point was outside the image?
					}
				}
			}
		}
	}
}
