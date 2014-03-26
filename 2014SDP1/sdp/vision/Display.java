package sdp.vision;

import gui.MainWindow;

import java.awt.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.activation.CommandInfo;
import javax.imageio.ImageIO;

import behavior.StrategyHelper;
import common.Robot;
import communication.RobotCommand;
import communication.Server;
import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.RobotType;

public class Display {
	
	/** Maps RobotCommands to images corresponding to that command */
	private static HashMap<Integer, BufferedImage> commandImages;

	private static void initCommandImages() {
		commandImages = new HashMap<Integer, BufferedImage>();
		
		// Commands in order - these need to correspond to the files in
		// the images array
		int commands[] = {
			RobotCommand.FORWARD,
			RobotCommand.BACK,
			RobotCommand.STOP,
			RobotCommand.CW,
			RobotCommand.CCW,
			RobotCommand.FAST_KICK,
			RobotCommand.CLOSE_GRABBER,
			RobotCommand.OPEN_GRABBER,
			RobotCommand.MOVE_LEFT,
			RobotCommand.MOVE_RIGHT,
			RobotCommand.KICK_LEFT,
			RobotCommand.KICK_RIGHT,
			RobotCommand.AIM_LEFT,
			RobotCommand.AIM_RIGHT,
			RobotCommand.AIM_RESET,
			RobotCommand.SLOW_CCW,
			RobotCommand.SLOW_CW
		};
		
		// The names of the image files
		String images[] = {
			"arrowUp.png",
			"arrowDown.png",
			"stop.png",
			"cw.png",
			"ccw.png",
			"kick.png",
			"grab.png",
			"open.png",
			"arrowLeft.png",
			"arrowRight.png",
			"kickLeft.png",
			"kickRight.png",
			"aimLeft.png",
			"aimRight.png",
			"resetAim.png",
			"ccwSlow.png",
			"ccwFast.png"
		};
		
		// Folder where all the images are stored
		String imageFolder = "sdp/vision/images/";
		
		if (commands.length != images.length) {
			System.err.println("ERROR - Number of commands and command images DOES NOT MATCH");
			return;
		}
		
		for (int index = 0; index < commands.length; index++) {
			String imageFile = imageFolder + images[index];
			
			// Read in image and store in hashmap
			try {
				BufferedImage image = ImageIO.read(new File(imageFile));
				commandImages.put(commands[index], image);
			} catch (IOException e) {
				System.err.println("Couldn't read in image " + imageFile);
			}
		}
	}

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
				img.setRGB((int) op.getPoints(Colours.GREEN).get(i).getX(), (int) op.getPoints(Colours.GREEN).get(i).getY(), Color.RED.getRGB());	
			}					
		}
		
		//Debug graphics for the ball
		if (ts.getDebug(Colours.RED)) {
			for (int i=0; (i<op.getPoints(Colours.RED).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.RED).get(i).getX(), (int) op.getPoints(Colours.RED).get(i).getY(), Color.RED.getRGB());	
			}					
		}
		
		//Debug graphics for the blue plate
		if (ts.getDebug(Colours.BLUE)) {
			for (int i=0; (i<op.getPoints(Colours.BLUE).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.BLUE).get(i).getX(), (int) op.getPoints(Colours.BLUE).get(i).getY(), Color.RED.getRGB());	
			}					
		}
		
		//Debug graphics for the yellow plate
		if (ts.getDebug(Colours.YELLOW)) {
			for (int i=0; (i<op.getPoints(Colours.YELLOW).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.YELLOW).get(i).getX(), (int) op.getPoints(Colours.YELLOW).get(i).getY(), Color.RED.getRGB());	
			}					
		}
	}
	
	public static void markers(ThresholdsState ts, BufferedImage img, PitchPoints op, WorldState ws) {
		
		if (commandImages == null) {
			initCommandImages();
		}
		
//		System.out.println("ball magnitude: " + StrategyHelper.magnitude(ws.getBallVelocity()));
		
		Graphics graphics = img.getGraphics();

		//draw pitch bounds
		graphics.drawRect((int) ws.getPitchTopLeft().getX(), (int) ws.getPitchTopLeft().getY(), (int) ws.getPitchWidth(), (int) ws.getPitchHeight());
//		int width = (int) (ws.getOuterPitchBottomRight().getX()-ws.getOuterPitchTopLeft().getX());
//		int height = (int) (ws.getOuterPitchBottomRight().getY()-ws.getOuterPitchTopLeft().getY());
//		graphics.drawRect((int) ws.getOuterPitchTopLeft().getX(), (int) ws.getOuterPitchTopLeft().getY(), width, height);
//		

		// Point where we would have to be to stop them hitting the top/bottom of our goal
		Point topDefend = StrategyHelper.findGoalTopDefendPosition(ws);
		Point bottomDefend = StrategyHelper.findGoalBottomDefendPosition(ws);
		graphics.setColor(Color.BLACK);
		graphics.fillOval(topDefend.x - 3, topDefend.y - 3, 6, 6);
		graphics.setColor(Color.WHITE);
		graphics.fillOval(bottomDefend.x - 3, bottomDefend.y - 3, 6, 6);
		
		

		graphics.setColor(Color.cyan);
		graphics.drawLine(ws.getRobotX(ws.getOur(RobotType.DEFENDER)), ws.getRobotY(ws.getOur(RobotType.DEFENDER)), ws.getDefenderGoDiagonallyToX(),  ws.getDefenderGoDiagonallyToY());

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
			// Orientation with the distortion fix :)
			Point robotPoint;
//			Point robotPoint = DistortionFix.AndrePerspectiveFix(ws.getRobotPoint(r)); 

//			int x2=(int) (robotPoint.x + 50*Math.cos(ws.getRobotOrientation(r)));
//			int y2=(int) (robotPoint.y + 50*Math.sin(ws.getRobotOrientation(r)));
//			graphics.drawLine(robotPoint.x, robotPoint.y, x2, y2);

			// Orientation WITHOUT the distortion fix
			robotPoint = ws.getRobotPoint(r);
			int x2=(int) (robotPoint.x + 50*Math.cos(ws.getRobotOrientation(r)));
			int y2=(int) (robotPoint.y + 50*Math.sin(ws.getRobotOrientation(r)));
			
			graphics.drawLine(robotPoint.x, robotPoint.y, x2, y2);
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
		
        /*-------------------------------------------------------*/
        /*  Draw Defender's and Attacker's command on the screen */
		/*-------------------------------------------------------*/
		
		for (RobotType type : RobotType.values()) {
			int command = Server.previousCommand.get(type);
			BufferedImage image = commandImages.get(command);
			
			Point pos = ws.getRobotPoint(ws.getOur(type));
			
			if (image != null) {
				graphics.drawImage(image, pos.x - 20, pos.y - 20, 40, 40, null);
			} else if (command != RobotCommand.NO_COMMAND) {
				System.out.println("DISPLAY: Don't have an image for command : " + command);
			}
		}		
		
		/*-------------------------------------------------------*/
        /*  Show defense points on screen - using physics engine */
		/*-------------------------------------------------------*/
		
		// Predict a point on the line in front of our goal
		Point defendPos = StrategyHelper.getIntersectWithVerticalLine(StrategyHelper.getDefendLineX(ws), ws.getOppositionAttackerPosition(), ws.getRobotOrientationVector(ws.getOpposition(RobotType.ATTACKER)));
		if (defendPos != null) {
			graphics.setColor(Color.ORANGE);
			defendPos.y = Math.min(Math.max(defendPos.y, ws.getOurGoalTop().y + 5), ws.getOurGoalBottom().y - 5);
			graphics.fillOval(defendPos.x - 3, defendPos.y - 3, 6, 6);
		}
		
		// Only check ball prediction positions if the ball is moving with at least some minimum speed
		if (ws.ballIsMoving()) {
			// Draw the position on the wall where the ball will hit it if it keeps
			// moving in the same direction
			Point wallHitPosition = StrategyHelper.getIntersectsWithWalls(ws.getBallVelocity(), new Point(ws.ballX, ws.ballY), ws);
			
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
		
//		// Point after distortion fix
//		Point defDistorted = null;
//		defDistorted = DistortionFix.barrelCorrected(ws.getRobotPoint(ws.getOur(RobotType.DEFENDER)));
//		defDistorted = ws.getRobotPoint(ws.getOur(RobotType.DEFENDER));
//		
//		System.out.println(defDistorted.x + " " + defDistorted.y);
//		Point defDistorted2 = DistortionFix.perspectiveFix(20, defDistorted);
//		Point andreDistorted = DistortionFix.AndrePerspectiveFix(defDistorted);
//		
//		graphics.setColor(Color.BLUE);
//		graphics.fillOval(defDistorted.x - 3, defDistorted.y - 3, 6, 6);
//		
//		System.out.println(defDistorted2.x + " " + defDistorted2.y);
//		System.out.println(andreDistorted.x + " " + andreDistorted.y);
//		
//		graphics.setColor(Color.RED);
//		graphics.fillOval(andreDistorted.x - 3, andreDistorted.y - 3, 6, 6);
		
		/*-------------------------------------------------------*/
        /*  TESTING GROUNDS - do whatever you want here :)       */
		/*-------------------------------------------------------*/
		
	}
	
	public static void renderDrawables(WorldState ws, BufferedImage image) {
		for ( String key : MainWindow.getDrawables().keySet() ){
			for ( Drawable d : MainWindow.getDrawables().get(key) ){
				d.draw(image.getGraphics(), ws.getPitchTopLeft());
			}
		}
	}
}
