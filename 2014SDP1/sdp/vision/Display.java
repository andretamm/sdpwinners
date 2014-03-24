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

import javax.imageio.ImageIO;

import behavior.StrategyHelper;
import common.Robot;
import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.RobotType;

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
		
//		System.out.println("ball magnitude: " + StrategyHelper.magnitude(ws.getBallVelocity()));
		
		Graphics graphics = img.getGraphics();

		//draw pitch bounds
		graphics.drawRect((int) ws.getPitchTopLeft().getX(), (int) ws.getPitchTopLeft().getY(), (int) ws.getPitchWidth(), (int) ws.getPitchHeight());
//		int width = (int) (ws.getOuterPitchBottomRight().getX()-ws.getOuterPitchTopLeft().getX());
//		int height = (int) (ws.getOuterPitchBottomRight().getY()-ws.getOuterPitchTopLeft().getY());
//		graphics.drawRect((int) ws.getOuterPitchTopLeft().getX(), (int) ws.getOuterPitchTopLeft().getY(), width, height);
//		
		// Point where we would have to be to stop them hitting our top goal
		Point topDefend = StrategyHelper.findGoalTopDefendPosition(ws);
		graphics.fillOval(topDefend.x - 3, topDefend.y - 3, 6, 6);
//		System.out.println(topDefend.x + " " + topDefend.y);
		
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
		
		
		//XXX Drawing our robots' commands
		
		/*
		// This is for testing
			BufferedImage arrowUp = null;
	        try {
	        	arrowUp = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowUp.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowUp, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		*/
	    
        // Draw Defender's command
		if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 1){
			BufferedImage arrowUp = null;
	        try {
	        	arrowUp = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowUp.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowUp, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 2){
			BufferedImage arrowDown = null;
	        try {
	        	arrowDown = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowDown.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowDown, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 3){
			BufferedImage stop = null;
	        try {
	            stop = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/stop.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(stop, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 4){
			BufferedImage cw = null;
	        try {
	            cw = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/cw.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(cw, ws.getOurDefenderXVision()-40, ws.getOurDefenderYVision()-40, 75, 75, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 5){
			BufferedImage ccw = null;
	        try {
	            ccw = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/ccw.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(ccw, ws.getOurDefenderXVision()-40, ws.getOurDefenderYVision()-40, 75, 75, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 6){
			BufferedImage kick = null;
	        try {
	            kick = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/kick.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(kick, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 10){
			BufferedImage grab = null;
	        try {
	            grab = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/grab.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(grab, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 11){
			BufferedImage open = null;
	        try {
	            open = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/open.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(open, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 12){
			BufferedImage arrowLeft = null;
	        try {
	        	arrowLeft = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowLeft.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowLeft, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 13){
			BufferedImage arrowRight = null;
	        try {
	        	arrowRight = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowRight.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowRight, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 14){
			BufferedImage kickLeft = null;
	        try {
	        	kickLeft = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/kickLeft.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(kickLeft, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 15){
			BufferedImage kickRight = null;
	        try {
	        	kickRight = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/kickRight.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(kickRight, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 16){
			BufferedImage aimLeft = null;
	        try {
	            aimLeft = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/aimLeft.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(aimLeft, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 17){
			BufferedImage aimRight = null;
	        try {
	            aimRight = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/aimRight.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(aimRight, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 18){
			BufferedImage resetAim = null;
	        try {
	            resetAim = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/resetAim.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(resetAim, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 19){
			BufferedImage ccwSlow = null;
	        try {
	            ccwSlow = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/ccwSlow.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(ccwSlow, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.DEFENDER) == 20){
			BufferedImage ccwFast = null;
	        try {
	        	ccwFast = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/ccwFast.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(ccwFast, ws.getOurDefenderXVision()-25, ws.getOurDefenderYVision()-25, 50, 50, null);
		}
		
		// Draw Attacker's Command
		
		if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 1){
			BufferedImage arrowUp = null;
	        try {
	        	arrowUp = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowUp"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowUp, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 2){
			BufferedImage arrowDown = null;
	        try {
	        	arrowDown = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowDown.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowDown, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 3){
			BufferedImage stop = null;
	        try {
	            stop = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/stop.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(stop, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 4){
			BufferedImage cw = null;
	        try {
	            cw = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/cw.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(cw, ws.getOurAttackerXVision()-40, ws.getOurAttackerYVision()-40, 75, 75, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 5){
			BufferedImage ccw = null;
	        try {
	            ccw = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/ccw.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(ccw, ws.getOurAttackerXVision()-40, ws.getOurAttackerYVision()-40, 75, 75, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 6){
			BufferedImage kick = null;
	        try {
	            kick = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/kick.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(kick, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 10){
			BufferedImage grab = null;
	        try {
	            grab = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/grab.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(grab, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 11){
			BufferedImage open = null;
	        try {
	            open = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/open.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(open, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 12){
			BufferedImage arrowLeft = null;
	        try {
	        	arrowLeft = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowLeft.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowLeft, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 13){
			BufferedImage arrowRight = null;
	        try {
	        	arrowRight = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/arrowRight.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(arrowRight, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 14){
			BufferedImage kickLeft = null;
	        try {
	        	kickLeft = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/kickLeft.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(kickLeft, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 15){
			BufferedImage kickRight = null;
	        try {
	        	kickRight = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/kickRight.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(kickRight, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 16){
			BufferedImage aimLeft = null;
	        try {
	            aimLeft = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/aimLeft.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(aimLeft, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 17){
			BufferedImage aimRight = null;
	        try {
	            aimRight = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/aimRight.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(aimRight, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 18){
			BufferedImage resetAim = null;
	        try {
	            resetAim = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/resetAim.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(resetAim, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 19){
			BufferedImage ccwSlow = null;
	        try {
	            ccwSlow = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/ccwSlow.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(ccwSlow, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		else if (communication.Server.previousCommand.get(RobotType.ATTACKER) == 20){
			BufferedImage ccwFast = null;
	        try {
	        	ccwFast = ImageIO.read(new File("/afs/inf.ed.ac.uk/user/s11/s1132388/Desktop/GIT/sdpwinners/2014SDP1/sdp/vision/images/ccwFast.png"));
	        } catch (IOException e) {
	        }
	        graphics.drawImage(ccwFast, ws.getOurAttackerXVision()-25, ws.getOurAttackerYVision()-25, 50, 50, null);
		}
		
		//XXX Finished drawing our robots' commands
		
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
	}
	
	public static void renderDrawables(WorldState ws, BufferedImage image) {
		for ( String key : MainWindow.getDrawables().keySet() ){
			for ( Drawable d : MainWindow.getDrawables().get(key) ){
				d.draw(image.getGraphics(), ws.getPitchTopLeft());
			}
		}
	}
}
