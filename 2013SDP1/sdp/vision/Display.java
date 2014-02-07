package sdp.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import constants.Colours;
import constants.RobotColour;
import constants.RobotType;

import sdp.gui.MainWindow;
import sdp.strategy.Intercept;
import sdp.strategy.KickFrom;

public class Display {

	public static void thresholds(BufferedImage img, PitchPoints op, ThresholdsState ts) {
		
		//Debug graphics for the grey circles
		if (ts.isGrey_debug()) {
			for (int i=0; (i<op.getPoints(Colours.GRAY).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.GRAY).get(i).getX(), (int) op.getPoints(Colours.GRAY).get(i).getY(), 0xFF333333);	
			}					
		}
		
		//Debug graphics for the green plates
		if (ts.isGreen_debug()) {
			for (int i=0; (i<op.getPoints(Colours.GREEN).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.GREEN).get(i).getX(), (int) op.getPoints(Colours.GREEN).get(i).getY(), 0xFF00FF00);	
			}					
		}
		
		//Debug graphics for the ball
		if (ts.isBall_debug()) {
			for (int i=0; (i<op.getPoints(Colours.RED).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.RED).get(i).getX(), (int) op.getPoints(Colours.RED).get(i).getY(), 0xFFFF0000);	
			}					
		}
		
		//Debug graphics for the blue plate
		if (ts.isBlue_debug()) {
			for (int i=0; (i<op.getPoints(Colours.BLUE).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.BLUE).get(i).getX(), (int) op.getPoints(Colours.BLUE).get(i).getY(), 0xFF0000FF);	
			}					
		}
		
		//Debug graphics for the yellow plate
		if (ts.isYellow_debug()) {
			for (int i=0; (i<op.getPoints(Colours.YELLOW).size()); i++) {
				img.setRGB((int) op.getPoints(Colours.YELLOW).get(i).getX(), (int) op.getPoints(Colours.YELLOW).get(i).getY(), 0xFFFF7538);	
			}					
		}
	}
	
	public static void markers(ThresholdsState ts, BufferedImage img, PitchPoints op, WorldState ws) {

		Graphics graphics = img.getGraphics();

		//draw pitch bounds
		graphics.drawRect((int) ws.getPitchTopLeft().getX(), (int) ws.getPitchTopLeft().getY(), (int) ws.getPitchWidth(), (int) ws.getPitchHeight());
		int width = (int) (ws.getOuterPitchBottomRight().getX()-ws.getOuterPitchTopLeft().getX());
		int height = (int) (ws.getOuterPitchBottomRight().getY()-ws.getOuterPitchTopLeft().getY());
		graphics.drawRect((int) ws.getOuterPitchTopLeft().getX(), (int) ws.getOuterPitchTopLeft().getY(), width, height);

        graphics.setColor(Color.red);
		graphics.drawLine(0, (int) ws.getBallYVision(), 640, (int) ws.getBallYVision());
		graphics.drawLine((int) ws.getBallXVision(), 0, (int) ws.getBallXVision(), 480);
		
		/* Display markers for the quadrants */ 
		graphics.drawRect(ws.getQ1LowX(), ws.getPitchTopLeft().y, ws.getQ1HighX()-ws.getQ1LowX(), (int) ws.getPitchHeight());
		graphics.drawRect(ws.getQ2LowX(), ws.getPitchTopLeft().y, ws.getQ2HighX()-ws.getQ2LowX(), (int) ws.getPitchHeight());
		graphics.drawRect(ws.getQ3LowX(), ws.getPitchTopLeft().y, ws.getQ3HighX()-ws.getQ3LowX(), (int) ws.getPitchHeight());
		graphics.drawRect(ws.getQ4LowX(), ws.getPitchTopLeft().y, ws.getQ4HighX()-ws.getQ4LowX(), (int) ws.getPitchHeight());

		
		//graphics.drawOval(ball.getX() - ballRadius, ball.getY() - ballRadius, 2*ballRadius, 2*ballRadius);
		
		graphics.setColor(Color.red);
		
		
		graphics.drawOval((int) op.getRobotPosition(RobotColour.BLUE, RobotType.DEFENDER).getX() - WorldState.plateRadius,
						  (int) op.getRobotPosition(RobotColour.BLUE, RobotType.DEFENDER).getY() - WorldState.plateRadius, 
						  2*WorldState.plateRadius+1, 2*WorldState.plateRadius);
		graphics.drawOval((int) op.getRobotPosition(RobotColour.BLUE, RobotType.ATTACKER).getX() - WorldState.plateRadius,
				  		  (int) op.getRobotPosition(RobotColour.BLUE, RobotType.ATTACKER).getY() - WorldState.plateRadius, 
				  		  2*WorldState.plateRadius+1, 2*WorldState.plateRadius); 
		graphics.drawOval((int) op.getRobotPosition(RobotColour.YELLOW, RobotType.DEFENDER).getX() - WorldState.plateRadius,
				  		  (int) op.getRobotPosition(RobotColour.YELLOW, RobotType.DEFENDER).getY() - WorldState.plateRadius, 
				  		  2*WorldState.plateRadius+1, 2*WorldState.plateRadius); 
		graphics.drawOval((int) op.getRobotPosition(RobotColour.YELLOW, RobotType.ATTACKER).getX() - WorldState.plateRadius,
				  		  (int) op.getRobotPosition(RobotColour.YELLOW, RobotType.ATTACKER).getY() - WorldState.plateRadius, 
				  		  2*WorldState.plateRadius+1, 2*WorldState.plateRadius); 
		
		// TODO draw rectangles around each of the robot plates????
		// TODO draw rectangles around each of the robot plates????
		// TODO draw rectangles around each of the robot plates????
		// TODO draw rectangles around each of the robot plates????
		// TODO draw rectangles around each of the robot plates????
		// TODO remove this TODO spam

		graphics.setColor(new Color(0xFF00FF00));
		int x2=(int) (ws.getBlueDefenderXVision()+150*Math.cos(ws.getBlueDefenderOrientation()));
		int y2=(int) (ws.getBlueDefenderYVision()+150*Math.sin(ws.getBlueDefenderOrientation()));
		graphics.drawLine((int) ws.getBlueDefenderXVision(), (int) ws.getBlueDefenderYVision(), x2, y2);

		x2=(int) (ws.getYellowDefenderXVision()+150*Math.cos(ws.getYellowDefenderOrientation()));
		y2=(int) (ws.getYellowDefenderYVision()+150*Math.sin(ws.getYellowDefenderOrientation()));
		graphics.drawLine((int) ws.getYellowDefenderXVision(), (int) ws.getYellowDefenderYVision(), x2, y2);
		
		graphics.drawOval((int) ws.getBallXVision() - WorldState.ballRadius, (int) ws.getBallYVision() - WorldState.ballRadius, 2*WorldState.ballRadius+1, 2*WorldState.ballRadius+1);
		
		//Point pos1 = KickFrom.whereToKickFromSimple(ws.getOppositionGoalTop(), ws.getBallPoint());
		//graphics.drawOval((int) (pos1.x-5+ws.getPitchTopLeft().getX()), (int) (pos1.y-5+ws.getPitchTopLeft().getY()), 10, 10);
		
		//Point pos2 = KickFrom.whereToKickFromSimple(ws.getOppositionGoalCentre(), ws.getBallPoint());
		//graphics.drawOval((int) (pos2.x-5+ws.getPitchTopLeft().getX()), (int) (pos2.y-5+ws.getPitchTopLeft().getY()), 10, 10);
            
		//Point pos3 = KickFrom.whereToKickFromSimple(ws.getOppositionGoalBottom(), ws.getBallPoint());
		//graphics.drawOval((int) (pos3.x-5+ws.getPitchTopLeft().getX()), (int) (pos3.y-5+ws.getPitchTopLeft().getY()), 10, 10);
		
		//Point pos4 = new Point((int) ((pos1.getX()+pos3.getX())/2), (int) ((pos1.getY()+pos3.getY())/2));
		Point pos4 = KickFrom.whereToKickFrom(ws);
		graphics.setColor(Color.GREEN);
		graphics.drawOval((int) (pos4.x-5+ws.getPitchTopLeft().getX()), (int) (pos4.y-5+ws.getPitchTopLeft().getY()), 10, 10);
		
		Point target = KickFrom.getPointToShootAt(ws);
		graphics.setColor(Color.RED);
		graphics.drawOval((int) (target.x-5+ws.getPitchTopLeft().getX()), (int) (target.y-5+ws.getPitchTopLeft().getY()), 10, 10);
		
		//Draw goal centres
		graphics.setColor(Color.WHITE);
		graphics.drawOval((int) (ws.getOppositionGoalCentre().getX()-5+ws.getPitchTopLeft().getX()), (int) (ws.getOppositionGoalCentre().getY()-5+ws.getPitchTopLeft().getY()), 10, 10);
		graphics.drawOval((int) (ws.getOurGoalCentre().getX()-5+ws.getPitchTopLeft().getX()), (int) (ws.getOurGoalCentre().getY()-5+ws.getPitchTopLeft().getY()), 10, 10);

		graphics.drawOval((int) (ws.getOppositionGoalTop().getX()-5+ws.getPitchTopLeft().getX()), (int) (ws.getOppositionGoalTop().getY()-5+ws.getPitchTopLeft().getY()), 10, 10);
		graphics.drawOval((int) (ws.getOurGoalTop().getX()-5+ws.getPitchTopLeft().getX()), (int) (ws.getOurGoalTop().getY()-5+ws.getPitchTopLeft().getY()), 10, 10);

		graphics.drawOval((int) (ws.getOppositionGoalBottom().getX()-5+ws.getPitchTopLeft().getX()), (int) (ws.getOppositionGoalBottom().getY()-5+ws.getPitchTopLeft().getY()), 10, 10);
		graphics.drawOval((int) (ws.getOurGoalBottom().getX()-5+ws.getPitchTopLeft().getX()), (int) (ws.getOurGoalBottom().getY()-5+ws.getPitchTopLeft().getY()), 10, 10);
		
		graphics.setColor(Color.RED);
		//draw pitch corners
		graphics.drawOval((int) (ws.getPitchTopLeft().getX()-3), (int) (ws.getPitchTopLeft().getY()-3), 6, 6);   
		graphics.drawOval((int) (ws.getPitchTopRight().getX()-3), (int) (ws.getPitchTopRight().getY()-3), 6, 6);   
		graphics.drawOval((int) (ws.getPitchBottomLeft().getX()-3), (int) (ws.getPitchBottomLeft().getY()-3), 6, 6);   
		graphics.drawOval((int) (ws.getPitchBottomRight().getX()-3), (int) (ws.getPitchBottomRight().getY()-3), 6, 6);                

		graphics.setColor(Color.BLACK);
		//draw goal lines
		graphics.drawLine((int) (ws.getOppositionGoalTop().getX()+ws.getPitchTopLeft().getX()), (int) (ws.getOppositionGoalTop().getY()+ws.getPitchTopLeft().getY()), 
				(int) (ws.getOppositionGoalBottom().getX()+ws.getPitchTopLeft().getX()), (int) (ws.getOppositionGoalBottom().getY()+ws.getPitchTopLeft().getY()));

		graphics.drawLine((int) (ws.getOurGoalTop().getX()+ws.getPitchTopLeft().getX()), (int) (ws.getOurGoalTop().getY()+ws.getPitchTopLeft().getY()), 
				(int) (ws.getOurGoalBottom().getX()+ws.getPitchTopLeft().getX()), (int) (ws.getOurGoalBottom().getY()+ws.getPitchTopLeft().getY()));

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

		graphics.setColor(Color.ORANGE);
		Point a = ws.getDefendPenaltyPoint();
		graphics.drawOval((int) (a.getX()+ws.getPitchTopLeft().getX()-3), (int) (a.getY()+ws.getPitchTopLeft().getY()-3), 6,6);
	}

	public static void renderDrawables(WorldState ws, BufferedImage image) {
		for ( String key : MainWindow.getDrawables().keySet() ){
			for ( Drawable d : MainWindow.getDrawables().get(key) ){
				d.draw(image.getGraphics(), ws.getPitchTopLeft());
			}
		}
	}
}
