package sdp.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class DrawableRectangle implements Drawable {
	
	private Point mStart;
	private int mWidth;
	private int mHeight;
	private Color mColour;

	public DrawableRectangle(Color colour, Point start, int width, int height){
		mStart = start;
		mWidth = width;
		mHeight = height;
		
		mColour = colour;
	}

	@Override
	public void draw(Graphics g, Point topLeft) {
		
		Point actualStartPoint = new Point(mStart.x + topLeft.x, mStart.y + topLeft.y);
		
		g.setColor(mColour);
		g.drawLine(actualStartPoint.x, actualStartPoint.y, actualStartPoint.x + mWidth, actualStartPoint.y);
		g.drawLine(actualStartPoint.x  + mWidth, actualStartPoint.y, actualStartPoint.x + mWidth, actualStartPoint.y + mHeight);
		g.drawLine(actualStartPoint.x  + mWidth, actualStartPoint.y + mHeight, actualStartPoint.x, actualStartPoint.y + mHeight);
		g.drawLine(actualStartPoint.x, actualStartPoint.y + mHeight, actualStartPoint.x, actualStartPoint.y);
	}

}
