package sdp.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class DrawablePolygon implements Drawable {
	private Polygon mPolygon;
	private Color mColour;
	
	public DrawablePolygon(Color colour, Polygon poly){
		mPolygon = poly;
		mColour = colour;
	}

	@Override
	public void draw(Graphics g, Point topLeft) {
		g.setColor(mColour);

		for ( int i = 0; i < mPolygon.npoints; ++i ){
			g.drawLine(mPolygon.xpoints[i] + topLeft.x,
					   mPolygon.ypoints[i] + topLeft.y,
					   mPolygon.xpoints[(i+1) % mPolygon.npoints] + topLeft.x,
					   mPolygon.ypoints[(i+1) % mPolygon.npoints] + topLeft.y);			
		}
		
	}

}
