package vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Circle implements Drawable {
	
	private Color mColour;
	private Point mCenter;
	private int mRadius;

	public Circle(Color colour, Point center, int radius){
		mColour = colour;
		mCenter = center;
		mRadius = radius;
	}
	
	public Circle(Color colour, Point2D center, int radius){
		mColour = colour;
		mCenter = new Point((int)center.getX(), (int)center.getY());
		mRadius = radius;
	}

	@Override
	public void draw(Graphics g, Point topLeft) {
		g.setColor(mColour);
		g.drawOval(mCenter.x + topLeft.x - mRadius,
				   mCenter.y + topLeft.y  - mRadius,
				   mRadius*2, mRadius*2);		
	}

}
