package sdp.strategy;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static sdp.geom.LineMethods.*;


import sdp.gui.MainWindow;
import sdp.vision.Drawable;
import sdp.vision.DrawableLine;
import sdp.vision.WorldState;

public class KickFrom {	
	
	
	private static Point point2dRoundedToPoint(Point2D p){
		return new Point((int)p.getX(), (int)p.getY());
	}
	
	public static Line2D getBestGoalLine(WorldState ws){

		List<Line2D> goalAreas = openGoalAreas(ws);

		ArrayList<Drawable> ds = new ArrayList<Drawable>();
		
		Iterator<Line2D> it = goalAreas.iterator();
		while( it.hasNext() ) {
		     Line2D line = it.next();
		     if( lineLengthSq(line) < 30*30 ){
		        it.remove();
		    }
		}
		
		assert goalAreas.size() > 0;

		for ( Line2D l : goalAreas ){
			ds.add(new DrawableLine(Color.RED,
									new Point((int)l.getX1() + 2, (int)l.getY1()),
									new Point((int)l.getX2() + 2, (int)l.getY2())));
		}
		MainWindow.addOrUpdateDrawable("goal line", ds);
		
		Line2D bestLine = null;
		
		if ( ws.getBallVisible() ){
			double minDist = Integer.MAX_VALUE;
			for ( Line2D line : goalAreas ){
				final double dist = line.ptSegDist(ws.getBallPoint());
				if ( dist < minDist ){
					minDist = dist;
					bestLine = line;
				}
			}
		}else{
			double maxSize = 0;
			for ( Line2D line : goalAreas ){
				final double size = lineLengthSq(line);
				if ( maxSize < size ){
					maxSize = size;
					bestLine = line;
				}
			}
		}
		
		return bestLine;
	}
	
	/**
	 * Gets the best point to shoot at
	 * @param ws
	 * @return null if there is no possible point
	 */
	public static Point getPointToShootAt(WorldState ws){
		Line2D goalLine = getBestGoalLine(ws);
		
		//todo find why this could ever be null
		if ( goalLine == null ){
			return ws.getOppositionGoalCentre();
		}
		//TODO Alter for Attacker Robot
		if ( goalLine.ptSegDist(ws.getBallPoint()) < 35 ){
			Line2D lineThroughBall = new Line2D.Double(ws.getOurDefenderPosition(), ws.getBallPoint());
			Point2D intersection = infiniteLineIntersections(goalLine, lineThroughBall);
			
			boolean isIntersectionOutsideGoal = intersection.getY() > ws.getOppositionGoalTop().getY() + 5 ||
			 									intersection.getY() < ws.getOppositionGoalBottom().getY() - 5;
			if ( isIntersectionOutsideGoal ){
				boolean isClosestToTop = ws.getOppositionGoalTop().distanceSq(intersection) < ws.getOppositionGoalBottom().distanceSq(intersection);
				if ( isClosestToTop ){
					return new Point((int)intersection.getX(),(int)ws.getOppositionGoalTop().getY() + 5);
				}
				return new Point((int)intersection.getX(),(int)ws.getOppositionGoalBottom().getY() + 5);
			}
			return point2dRoundedToPoint(intersection);
		}
		return point2dRoundedToPoint(midpoint(goalLine));
	}
	
	/**
	 * gets lines representing open areas of the goal
	 * @param ws
	 * @return
	 */
	//TODO Alter for Attacker Robot
	public static List<Line2D> openGoalAreas(WorldState ws){
		Point goalBottom = ws.getOppositionGoalBottom(); //greatest Y
		Point goalTop = ws.getOppositionGoalTop(); //least Y
		
		int oppX = ws.getOppositionDefenderX();
		int maxX = Math.max(ws.getBallX(), goalBottom.x);
		int minX = Math.min(ws.getBallX(), goalBottom.x);
		boolean oppositionBetweenBallAndTheirGoal = oppX < maxX && oppX > minX ;
		if ( !oppositionBetweenBallAndTheirGoal ){
			return Arrays.asList((Line2D)new Line2D.Double(goalBottom, goalTop));
		}
		
		int bottomY = ws.getOppositionDefenderY() + 20;
		int topY 	= ws.getOppositionDefenderY() - 20;
		
		ArrayList<Line2D> toReturn = new ArrayList<Line2D>();
		
		if ( bottomY < goalBottom.getY() ){
			toReturn.add(new Line2D.Double(goalBottom, new Point2D.Double(goalBottom.x, bottomY)));
		}
		if ( topY > goalTop.getY() ){
			toReturn.add(new Line2D.Double(new Point2D.Double(goalTop.x, topY), goalTop));
		}		
		return toReturn;
	}
	
    public static Point whereToKickFromSimple(Point goal, Point ball, int distance) {
            Point imaginaryLine, imaginaryLine2;
            double distanceToGoal;
            double ratio;
            imaginaryLine = subtractPoints(ball, goal);
            distanceToGoal = Point.distance(goal.x, goal.y, ball.x, ball.y);
            ratio = (distanceToGoal + distance) / distanceToGoal;
            imaginaryLine2 = scalePointBy(imaginaryLine, ratio);
            return addPoints(goal, imaginaryLine2);
    }
    
    public static Point whereToKickFrom(WorldState worldState, int distance) {
    	Point kickPoint = whereToKickFromSimple(getPointToShootAt(worldState),
    											worldState.getBallPoint(),
    											10);
    	Point kickVector = new Point((int) (kickPoint.getX()-worldState.getBallX()), (int) (kickPoint.getY()-worldState.getBallY()));
    	double magnitude = Math.sqrt(Math.pow(kickVector.getX(),2)+Math.pow(kickVector.getY(),2));
    	kickVector = new Point((int) (kickVector.getX()*distance/magnitude), (int) (kickVector.getY()*distance/magnitude));
    	kickPoint = new Point((int) (worldState.getBallPoint().getX()+kickVector.getX()), (int) (worldState.getBallPoint().getY()+kickVector.getY()));

    	if (kickPoint.y < 0) {
    		kickPoint.setLocation(kickPoint.x, 0 + 15);
    	}
    	
    	if (worldState.getPitchBottomRight().getY() - worldState.getPitchTopLeft().getY() < kickPoint.y) {
    		kickPoint.setLocation(kickPoint.x, worldState.getPitchBottomRight().getY() - worldState.getPitchTopLeft().getY() - 15);
    	}
    	
    	return kickPoint;
    }
    
    public static Point whereToDefendFrom(WorldState worldState, int distance) {
    	Point kickPoint = worldState.getOurGoalCentre();
    	Point kickVector = new Point((int) (kickPoint.getX()-worldState.getBallX()), (int) (kickPoint.getY()-worldState.getBallY()));
    	double magnitude = Math.sqrt(Math.pow(kickVector.getX(),2)+Math.pow(kickVector.getY(),2));
    	kickVector = new Point((int) (kickVector.getX()*distance/magnitude), (int) (kickVector.getY()*distance/magnitude));
    	kickPoint = new Point((int) (worldState.getBallPoint().getX()-kickVector.getX()), (int) (worldState.getBallPoint().getY()-kickVector.getY()));

    	if (kickPoint.y < 0) {
    		kickPoint.setLocation(kickPoint.x, 0 + 15);
    	}
    	
    	if (worldState.getPitchBottomRight().getY() - worldState.getPitchTopLeft().getY() < kickPoint.y) {
    		kickPoint.setLocation(kickPoint.x, worldState.getPitchBottomRight().getY() - worldState.getPitchTopLeft().getY() - 15);
    	}
    	
    	return kickPoint;
    }
    
    public static Point whereToKickFrom(WorldState worldState) {
    	return whereToKickFrom(worldState, 40);
    }
    
    private static Point addPoints(Point p1, Point p2) {
            return new Point(p1.x + p2.x, p1.y + p2.y);
    }
    
    /**
     * p1 - p2
     * 
     * @param p1
     * @param p2
     * @return
     */
    public static Point subtractPoints(Point p1, Point p2) {
            return new Point(p1.x - p2.x, p1.y - p2.y);
    }
    
    private static Point scalePointBy(Point p , double scalar) {
            return new Point((int) (p.x * scalar), (int) (p.y * scalar));
    }
    
    public static double distanceFromOrigin(Point p) {
    	return p.distance(0, 0);
    }
}
