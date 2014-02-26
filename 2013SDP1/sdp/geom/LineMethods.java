package sdp.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class LineMethods {
	

	public static double lineLengthSq(Line2D line){
		return line.getP1().distanceSq(line.getP2());
	}
	
	/**
     * Gets a normal to the line, going through at its midpoint the point, and of length
	 * @param line
	 * @param point
	 * @param length
	 * @return
	 */
    public static Line2D normalLine(Line2D line, Point2D point, double length){
        double dx = line.getP2().getX() - line.getP1().getX();
        double dy = line.getP2().getY() - line.getP1().getY();

        double mag = Math.sqrt(dx * dx + dy * dy);
        dx /= mag;
        dy /= mag;

        double d = length/2;
        Point2D p1 = new Point2D.Double( point.getX() + (-dy * d), point.getY() + ( dx * d));
        Point2D p2 = new Point2D.Double( point.getX() + ( dy * d), point.getY() + (-dx * d));

        return new Line2D.Double(p1, p2);
    }
    

    public static Point2D midpoint(Line2D line){
    	return new Point2D.Double(((line.getX1() + line.getX2())/2),
    				  	((line.getY1() + line.getY2())/2));
    }
    
    /**
     * Get line intersections assuming that both lines are infinate
     */
    public static Point2D infiniteLineIntersections(Line2D l1, Line2D l2){
        double  x1 = l1.getX1(), y1 = l1.getY1(),
                x2 = l1.getX2(), y2 = l1.getY2(),
                x3 = l2.getX1(), y3 = l2.getY1(),
                x4 = l2.getX2(), y4 = l2.getY2();

        double ix = det(det(x1, y1, x2, y2), x1 - x2, det(x3, y3, x4, y4), x3 - x4)/ det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
        double iy = det(det(x1, y1, x2, y2), y1 - y2, det(x3, y3, x4, y4), y3 - y4)/ det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
        return new Point2D.Double(ix, iy);
    }
    

    private static double det(double a, double b, double c, double d){
            return a * d - b * c;
    }

}
