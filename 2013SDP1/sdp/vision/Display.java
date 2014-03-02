package sdp.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import behavior.StrategyHelper;
import common.Robot;
import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.RobotType;
import sdp.gui.MainWindow;

public class Display {

	public static void thresholds(BufferedImage img, PitchPoints op, ThresholdsState ts) {
		
		//Debug graphics for the grey circles
		if (ts.getDebug(Colours.GRAY)) {
			for (int i=0; (i<op.getPoints(Colours.GRAY).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.GRAY).get(i).getX(), (int) op.getPoints(Colours.GRAY).get(i).getY(), Color.RED.getRGB());	
			}		
		}
		
		//Debug graphics for the green plates
		if (ts.getDebug(Colours.GREEN)) {
			for (int i=0; (i<op.getPoints(Colours.GREEN).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.GREEN).get(i).getX(), (int) op.getPoints(Colours.GREEN).get(i).getY(), 0xFF00FF00);	
			}					
		}
		
		//Debug graphics for the ball
		if (ts.getDebug(Colours.RED)) {
			for (int i=0; (i<op.getPoints(Colours.RED).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.RED).get(i).getX(), (int) op.getPoints(Colours.RED).get(i).getY(), 0xFFFF0000);	
			}					
		}
		
		//Debug graphics for the blue plate
		if (ts.getDebug(Colours.BLUE)) {
			for (int i=0; (i<op.getPoints(Colours.BLUE).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.BLUE).get(i).getX(), (int) op.getPoints(Colours.BLUE).get(i).getY(), 0xFF0000FF);	
			}					
		}
		
		//Debug graphics for the yellow plate
		if (ts.getDebug(Colours.YELLOW)) {
			for (int i=0; (i<op.getPoints(Colours.YELLOW).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.YELLOW).get(i).getX(), (int) op.getPoints(Colours.YELLOW).get(i).getY(), 0xFFFF7538);	
			}					
		}
	}
	
	public static void markers(ThresholdsState ts, BufferedImage img, PitchPoints op, WorldState ws) {
		Graphics graphics = img.getGraphics();

		//draw pitch bounds
		graphics.drawRect((int) ws.getPitchTopLeft().getX(), (int) ws.getPitchTopLeft().getY(), (int) ws.getPitchWidth(), (int) ws.getPitchHeight());
//		int width = (int) (ws.getOuterPitchBottomRight().getX()-ws.getOuterPitchTopLeft().getX());
//		int height = (int) (ws.getOuterPitchBottomRight().getY()-ws.getOuterPitchTopLeft().getY());
//		graphics.drawRect((int) ws.getOuterPitchTopLeft().getX(), (int) ws.getOuterPitchTopLeft().getY(), width, height);
//		
		
        graphics.setColor(Color.blue);
        
        /* The intersections of these two lines, defines the ball location. 
         * 0 and 640 are the boundaries of the field for the x value.
         * 0 and 480 are the boundaries of the field for the y value.
         */
        
		graphics.drawLine(0, (int) ws.getBallY(), 640, (int) ws.getBallY());
		graphics.drawLine((int) ws.getBallX(), 0, (int) ws.getBallX(), 480);
		
		
		/* Display markers for the quadrants */ 
		graphics.drawRect(ws.getQ1LowX(), ws.getPitchTopLeft().y, ws.getQ1HighX()-ws.getQ1LowX(), (int) ws.getPitchHeight());
		graphics.drawRect(ws.getQ2LowX(), ws.getPitchTopLeft().y, ws.getQ2HighX()-ws.getQ2LowX(), (int) ws.getPitchHeight());
		graphics.drawRect(ws.getQ3LowX(), ws.getPitchTopLeft().y, ws.getQ3HighX()-ws.getQ3LowX(), (int) ws.getPitchHeight());
		graphics.drawRect(ws.getQ4LowX(), ws.getPitchTopLeft().y, ws.getQ4HighX()-ws.getQ4LowX(), (int) ws.getPitchHeight());

		
		graphics.setColor(Color.red);
		
		
		/*graphics.fillOval((int) op.getRobotPosition(RobotColour.BLUE, RobotType.DEFENDER).getX() - WorldState.plateRadius,
						  (int) op.getRobotPosition(RobotColour.BLUE, RobotType.DEFENDER).getY() - WorldState.plateRadius, 
						  2*WorldState.plateRadius+1, 2*WorldState.plateRadius);
		graphics.fillOval((int) op.getRobotPosition(RobotColour.BLUE, RobotType.ATTACKER).getX() - WorldState.plateRadius,
				  		  (int) op.getRobotPosition(RobotColour.BLUE, RobotType.ATTACKER).getY() - WorldState.plateRadius, 
				  		  2*WorldState.plateRadius+1, 2*WorldState.plateRadius); 
		graphics.fillOval((int) op.getRobotPosition(RobotColour.YELLOW, RobotType.DEFENDER).getX() - WorldState.plateRadius,
				  		  (int) op.getRobotPosition(RobotColour.YELLOW, RobotType.DEFENDER).getY() - WorldState.plateRadius, 
				  		  2*WorldState.plateRadius+1, 2*WorldState.plateRadius); 
		graphics.fillOval((int) op.getRobotPosition(RobotColour.YELLOW, RobotType.ATTACKER).getX() - WorldState.plateRadius,
				  		  (int) op.getRobotPosition(RobotColour.YELLOW, RobotType.ATTACKER).getY() - WorldState.plateRadius, 
				  		  2*WorldState.plateRadius+1, 2*WorldState.plateRadius); 
		*/
		
		for (Quadrant q : Quadrant.values()) {
			
			Point mean = new Point(0,0);
			try {
				mean = Position.findMean(op.getQuadrant(q).getPoints(Colours.GREEN));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			graphics.fillOval(mean.x - 2, mean.y - 2, 4, 4);
			graphics.drawOval(mean.x - (int)Math.sqrt(Thresholder.threshValue),mean.y - (int)Math.sqrt(Thresholder.threshValue) , (int)Math.sqrt(Thresholder.threshValue)*2, (int)Math.sqrt(Thresholder.threshValue)*2);
		}
		
		// Draw orientation lines for the robots
		graphics.setColor(new Color(0xFF00FF00));
		
		for (Robot r: Robot.listAll()) {
				int x2=(int) (ws.getRobotX(r)+50*Math.cos(ws.getRobotOrientation(r.type, r.colour)));
				int y2=(int) (ws.getRobotY(r)+50*Math.sin(ws.getRobotOrientation(r.type, r.colour)));
				graphics.drawLine((int) ws.getRobotX(r), (int) ws.getRobotY(r), x2, y2);
		}
		
		graphics.drawOval((int) ws.getBallX() - WorldState.ballRadius, (int) ws.getBallY() - WorldState.ballRadius, 2*WorldState.ballRadius+1, 2*WorldState.ballRadius+1);

		// WHY CLAUDIU???? 
		// This draws all the grey points for a given robot in either black (blue robots) or white (yellow robots) 
		for (int i=0; i<op.getColouredPoints(RobotColour.BLUE, RobotType.DEFENDER, Colours.GRAY).size(); i++) {
			img.setRGB((int)op.getColouredPoints(RobotColour.BLUE, RobotType.DEFENDER, Colours.GRAY).get(i).getX(), (int)op.getColouredPoints(RobotColour.BLUE, RobotType.DEFENDER, Colours.GRAY).get(i).getY(), 0xFF000000);
		}
		
		for (int i=0; i<op.getColouredPoints(RobotColour.YELLOW, RobotType.DEFENDER, Colours.GRAY).size(); i++) {
			img.setRGB((int)op.getColouredPoints(RobotColour.YELLOW, RobotType.DEFENDER, Colours.GRAY).get(i).getX(), (int)op.getColouredPoints(RobotColour.YELLOW, RobotType.DEFENDER, Colours.GRAY).get(i).getY(), 0xFFFFFFFF);
		}
		
		for (int i=0; i<op.getColouredPoints(RobotColour.BLUE, RobotType.ATTACKER, Colours.GRAY).size(); i++) {
			img.setRGB((int)op.getColouredPoints(RobotColour.BLUE, RobotType.ATTACKER, Colours.GRAY).get(i).getX(), (int)op.getColouredPoints(RobotColour.BLUE, RobotType.ATTACKER, Colours.GRAY).get(i).getY(), 0xFF000000);
		}
		
		for (int i=0; i<op.getColouredPoints(RobotColour.YELLOW, RobotType.ATTACKER, Colours.GRAY).size(); i++) {
			img.setRGB((int)op.getColouredPoints(RobotColour.YELLOW, RobotType.ATTACKER, Colours.GRAY).get(i).getX(), (int)op.getColouredPoints(RobotColour.YELLOW, RobotType.ATTACKER, Colours.GRAY).get(i).getY(), 0xFFFFFFFF);
		}

		if ((Math.pow(ws.getBallVelocity().getX(),2)+Math.pow(ws.getBallVelocity().getY(),2))>0.01) {
//                    System.out.println("worldState.getBallVelocity()="+ws.getBallVelocity().getX() + ", " + ws.getBallVelocity().getY());
		}                

		// Draw opposition goal centre
		Point goalC = ws.getOppositionGoalCentre();
		graphics.setColor(Color.WHITE);
		graphics.fillOval((int) goalC.getX() - 10, (int) goalC.getY() - 10, 20, 20);
		
		// Draw our goal top and bottom
		graphics.drawOval(ws.getOurGoalTop().x - 2, ws.getOurGoalTop().y - 2, 4, 4);
		graphics.drawOval(ws.getOurGoalBottom().x - 2, ws.getOurGoalBottom().y - 2, 4, 4);
		
		// Draw movement predictions
		graphics.setColor(Color.ORANGE);
		Point newPos;
		double timeMs = 1000;
		for (Robot r : Robot.listAll()) {
			newPos = StrategyHelper.addVectorToPoint(StrategyHelper.multiplyVector(ws.getRobotVelocity(r), timeMs), ws.getRobotPoint(r));
			graphics.drawLine(ws.getRobotX(r), ws.getRobotY(r), newPos.x, newPos.y);
		}
		
		newPos = StrategyHelper.addVectorToPoint(StrategyHelper.multiplyVector(ws.getBallVelocity(), timeMs), new Point(ws.ballX, ws.ballY));
		graphics.drawLine(ws.ballX, ws.ballY, newPos.x, newPos.y);
		
		// Draw position that attacker robot should go to
		Point kickPos = StrategyHelper.findRobotKickPosition(new Point(ws.ballX, ws.ballY), ws.getOppositionGoalCentre());
		graphics.setColor(Color.CYAN);
		graphics.fillOval(kickPos.x - 3, kickPos.y - 3, 6, 6);
		
		// Draw a line from the kicking position to the centre of the opposition's goal
//		graphics.drawLine(kickPos.x, kickPos.y, ws.getOppositionGoalCentre().x, ws.getOppositionGoalCentre().y);
		
		
		/* BALL MOVEMENT PREDICTION DRAWING */
		
//		if (StrategyHelper.hasBall(new Robot(RobotColour.YELLOW, RobotType.ATTACKER), ws)) {
//			System.out.println("BALL IS IN RANGE FOR KICK!");
//		}
		
		// Predict a point on the line in front of our goal
		Point defendPos = StrategyHelper.getIntersectWithVerticalLine(ws.getOurGoalCentre().x - 60, ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER)), ws.getOppositionAttackerPosition());
		if (defendPos != null) {
			graphics.setColor(Color.ORANGE);
			graphics.fillOval(defendPos.x - 3, defendPos.y - 3, 6, 6);
		}
		
		// Only check ball prediction positions if the ball is moving with at least some minimum speed
		// This threshhold has been experimentally set to 0.01 :P
		if (StrategyHelper.magnitude(ws.getBallVelocity()) > 0.01) {
			// Draw the position on the wall where the ball will hit it if it keeps
			// moving in the same direction
			Point wallHitPosition = StrategyHelper.intersectsWithWalls(ws.getBallVelocity(), new Point(ws.ballX, ws.ballY), ws);
			
			if (wallHitPosition != null) {
				graphics.setColor(Color.GREEN);
				graphics.drawOval(wallHitPosition.x - 4, wallHitPosition.y - 4, 8, 8);
				
				// Draw the line from the collision point with the wall to where the ball will go next
				Point2D.Double velocityAfterCollision = StrategyHelper.collideWithHorizontalWall(ws.getBallVelocity());
				Point lineAfterCollision = StrategyHelper.addVectorToPoint(StrategyHelper.multiplyVector(StrategyHelper.normaliseVector(velocityAfterCollision), 200), wallHitPosition);
				graphics.drawLine(wallHitPosition.x, wallHitPosition.y, lineAfterCollision.x, lineAfterCollision.y);
				
				// Try to find intersection point after wall kick
				Point goalIntersectPoint = StrategyHelper.getIntersectWithOurGoal(velocityAfterCollision, wallHitPosition, ws);
				if (goalIntersectPoint != null) {
					// Ball will intersect with goal if it keeps moving in this direction
					graphics.setColor(Color.GREEN);
					graphics.fillOval(goalIntersectPoint.x - 4, goalIntersectPoint.y - 4, 8, 8);
				}
			} else {
				Point goalIntersectPoint = StrategyHelper.getIntersectWithOurGoal(ws.getBallVelocity(), ws.getBallPoint(), ws);
				if (goalIntersectPoint != null) {
					// Ball will intersect with goal if it keeps moving in this direction
					graphics.setColor(Color.GREEN);
					graphics.fillOval(goalIntersectPoint.x - 4, goalIntersectPoint.y - 4, 8, 8);
				}
			}
		}
	}

	public static void renderDrawables(WorldState ws, BufferedImage image) {
		for ( String key : MainWindow.getDrawables().keySet() ){
			for ( Drawable d : MainWindow.getDrawables().get(key) ){
				d.draw(image.getGraphics(), ws.getPitchTopLeft());
			}
		}
	}
}
