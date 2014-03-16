package sdp.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

public class DrawableLine implements Drawable{
	
	private Point mStart;
	private Point mEnd;
	private Color mColour;
	
	public DrawableLine(Color colour, Point start, Point end){
		mStart = start;
		mEnd = end;
		mColour = colour;
	}
	
	public DrawableLine(Color colour, Point2D start, Point2D end){
		mStart = new Point((int)start.getX(), (int)start.getY());
		mEnd = new Point((int)end.getX(), (int)end.getY());;
		mColour = colour;
	}

	@Override
	public void draw(Graphics g, Point topLeft) {
		g.setColor(mColour);
		g.drawLine(mStart.x + topLeft.x, mStart.y + topLeft.y, mEnd.x + topLeft.x, mEnd.y  + topLeft.y);
	}
	
}
