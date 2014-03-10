package sdp.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Circle2D {

	private Point2D subPoint(Point2D a, Point2D b) {
		return new Point2D.Double(a.getX() - b.getX(), a.getY() - b.getY());
	}

	private Point2D addPoint(Point2D a, Point2D b) {
		return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
	}

	private Point2D scalePoint(Point2D a, double factor) {
		return new Point2D.Double(a.getX() * factor, a.getY() * factor);
	}

	public Point2D getCenter(){
		return mCenter;
	}
	
	public double getRadius(){
		return mRadius;
	}
	
	Point2D mCenter;
	double mRadius;

	public Circle2D(Point2D center, double radius) {
		mCenter = center;
		mRadius = radius;
	}
	
	public boolean intersects(Line2D line){
		return line.ptSegDist(getCenter()) < getRadius();
	}
	
	public Collection<Point2D> intersections(Line2D line){
        double baX = line.getP2().getX() - line.getP1().getX();
        double baY = line.getP2().getY() - line.getP1().getY();
        double caX = getCenter().getX() - line.getP1().getX();
        double caY = getCenter().getY() - line.getP1().getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - getRadius() * getRadius();

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point2D p1 = new Point2D.Double(line.getP1().getX() - baX * abScalingFactor1, line.getP1().getY()
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point2D p2 = new Point2D.Double(line.getP1().getX() - baX * abScalingFactor2, line.getP1().getY()
                - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }

	public Collection<Point2D> intersections(Circle2D c) {
		Point2D P0 = getCenter();
		Point2D P1 = c.getCenter();
		double d, a, h;
		d = P0.distance(P1);
		a = (getRadius() * getRadius() - c.getRadius() * c.getRadius() + d * d) / (2 * d);
		h = Math.sqrt(getRadius() * getRadius() - a * a);
		Point2D P2 = addPoint(scalePoint(subPoint(P1, P0), a / d), P0);
		double x3, y3, x4, y4;
		x3 = P2.getX() + h * (P1.getY() - P0.getY()) / d;
		y3 = P2.getY() - h * (P1.getX() - P0.getX()) / d;
		x4 = P2.getX() - h * (P1.getY() - P0.getY()) / d;
		y4 = P2.getY() + h * (P1.getX() - P0.getX()) / d;

		Collection<Point2D> r = new LinkedList<Point2D>();
		r.add(new Point2D.Double(x3, y3));
		r.add(new Point2D.Double(x4, y4));
		return r;
	}

}
