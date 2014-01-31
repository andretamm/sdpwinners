package sdp.vision;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Vision's internal representation of the image. Use this rather than 
 * worldState, which should only recieve the final positions
 * 
 * @author Thomas Wallace
 */
public class ObjectPoints {

	private ArrayList<Point> ballPoints = null;
	private ArrayList<Point> bluePoints = null;
	private ArrayList<Point> yellowPoints = null;
	private ArrayList<Point> greenPoints = null;
	private ArrayList<Point> greyPoints = null;

	private ArrayList<Point> blueGreenPlate = new ArrayList<Point>();
	private ArrayList<Point> yellowGreenPlate = new ArrayList<Point>();
	private Point[] blueGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
	private Point[] yellowGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
	private ArrayList<Point> blueGreyPoints = new ArrayList<Point>();
	private ArrayList<Point> yellowGreyPoints = new ArrayList<Point>();
	
    private Point ball = null;
    private Point blue = null;
    private Point yellow = null;
    private double blueOrientation = 0;
    private double yellowOrientation = 0;
	
	public ObjectPoints(
			ArrayList<Point> ballPoints,
			ArrayList<Point> bluePoints,
			ArrayList<Point> yellowPoints,
			ArrayList<Point> greenPoints,
			ArrayList<Point> greyPoints,
			ArrayList<Point> blueGreenPlate,
			ArrayList<Point> yellowGreenPlate,
			Point[] blueGreenPlate4Points,
			Point[] yellowGreenPlate4Points,
			ArrayList<Point> blueGreyPoints,
			ArrayList<Point> yellowGreyPoints,
			Point ball,
			Point blue,
			Point yellow,
			double blueOrientation,
			double yellowOrientation) {
		
		this.ballPoints = ballPoints;
		this.setBluePoints(bluePoints);
		this.yellowPoints = yellowPoints;
		this.greenPoints = greenPoints;
		this.greyPoints = greyPoints;
		this.blueGreenPlate = blueGreenPlate;
		this.yellowGreenPlate = yellowGreenPlate;
		this.blueGreenPlate4Points = blueGreenPlate4Points;
		this.yellowGreenPlate4Points = yellowGreenPlate4Points;
		this.blueGreyPoints = blueGreyPoints;
		this.yellowGreyPoints = yellowGreyPoints;
		this.ball = ball;
		this.blue = blue;
		this.yellow = yellow;
		this.blueOrientation = blueOrientation;
		this.yellowOrientation = yellowOrientation;
	}
	
	public ObjectPoints() {
		this.ballPoints = new ArrayList<Point>();
		this.bluePoints = new ArrayList<Point>();
		this.yellowPoints = new ArrayList<Point>();
		this.greenPoints = new ArrayList<Point>();
		this.greyPoints = new ArrayList<Point>();
		this.blueGreenPlate = new ArrayList<Point>();
		this.yellowGreenPlate = new ArrayList<Point>();
		this.blueGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
		this.yellowGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
		this.blueGreyPoints = new ArrayList<Point>();
		this.yellowGreyPoints = new ArrayList<Point>();
		this.ball = new Point();
		this.blue = new Point();
		this.yellow = new Point();
		this.blueOrientation = 0;
		this.yellowOrientation = 0;
	}
	
	public void setBallPoints(ArrayList<Point> ballPoints) {
		this.ballPoints = ballPoints;
	}

	public ArrayList<Point> getBallPoints() {
		return ballPoints;
	}

	public void setBluePoints(ArrayList<Point> bluePoints) {
		this.bluePoints = bluePoints;
	}

	public ArrayList<Point> getBluePoints() {
		return bluePoints;
	}
	
	public void setYellowPoints(ArrayList<Point> yellowPoints) {
		this.yellowPoints = yellowPoints;
	}

	public ArrayList<Point> getYellowPoints() {
		return yellowPoints;
	}
	
	public ArrayList<Point> getGreenPoints() {
		return greenPoints;
	}
	
	public void setGreenPoints(ArrayList<Point> greenPoints) {
		this.greenPoints = greenPoints;
	}
	
	public ArrayList<Point> getGreyPoints() {
		return greyPoints;
	}
	
	public void setGreyPoints(ArrayList<Point> greyPoints) {
		this.greyPoints = greyPoints;
	}

	public ArrayList<Point> getBlueGreenPlate() {
		return blueGreenPlate;
	}

	public void setBlueGreenPlate(ArrayList<Point> blueGreenPlate) {
		this.blueGreenPlate = blueGreenPlate;
	}

	public ArrayList<Point> getYellowGreenPlate() {
		return yellowGreenPlate;
	}

	public void setYellowGreenPlate(ArrayList<Point> yellowGreenPlate) {
		this.yellowGreenPlate = yellowGreenPlate;
	}

	public Point[] getBlueGreenPlate4Points() {
		return blueGreenPlate4Points;
	}

	public void setBlueGreenPlate4Points(Point[] blueGreenPlate4Points) {
		this.blueGreenPlate4Points = blueGreenPlate4Points;
	}

	public Point[] getYellowGreenPlate4Points() {
		return yellowGreenPlate4Points;
	}

	public void setYellowGreenPlate4Points(Point[] yellowGreenPlate4Points) {
		this.yellowGreenPlate4Points = yellowGreenPlate4Points;
	}

	public ArrayList<Point> getBlueGreyPoints() {
		return blueGreyPoints;
	}

	public void setBlueGreyPoints(ArrayList<Point> blueGreyPoints) {
		this.blueGreyPoints = blueGreyPoints;
	}

	public ArrayList<Point> getYellowGreyPoints() {
		return yellowGreyPoints;
	}

	public void setYellowGreyPoints(ArrayList<Point> yellowGreyPoints) {
		this.yellowGreyPoints = yellowGreyPoints;
	}

	public Point getBall() {
		return ball;
	}

	public void setBall(Point ball) {
		this.ball = ball;
	}

	public Point getBlue() {
		return blue;
	}

	public void setBlue(Point blue) {
		this.blue = blue;
	}

	public Point getYellow() {
		return yellow;
	}

	public void setYellow(Point yellow) {
		this.yellow = yellow;
	}
	
	public double getBlueOrientation() {
		return blueOrientation;
	}
	
	public void setBlueOrientation(double blueOrientation) {
		this.blueOrientation = blueOrientation;
	}
	
	public double getYellowOrientation() {
		return yellowOrientation;
	}
	
	public void setYellowOrientation(double yellowOrientation) {
		this.yellowOrientation = yellowOrientation;
	}
}
