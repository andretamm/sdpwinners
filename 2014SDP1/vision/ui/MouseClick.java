package vision.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;


/**
 * Get the colour value of yellow and do the manual testing stuff
 * 
 * @author James Hulme
 * @author Dale Myers
 */
public class MouseClick implements MouseListener, MouseMotionListener {
    
    private int count = 0;
    private Point coords = new Point();
    private boolean mouseClick = false;
    
    //private JFrame windowFrame;
    //The below variables are for the testing system
    private ArrayList<Point> points = new ArrayList<Point>();
    public boolean testMouseClick = false;
    
    /**
     * The constructor
     * @param visionFeed 
     * @param windowFrame
     * @param ts
     */
    public MouseClick(JFrame windowFrame) {
        windowFrame.addMouseListener(this);
        windowFrame.addMouseMotionListener(this);
    }

	public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    //When the mouse has been clicked get the location.
    /**
     * Checks when mouse is clicked
     */
    public void mouseClicked(MouseEvent e){
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
    public ArrayList<Point> getTestPoints(){
    	return this.points;
    }
    
    /**
     * Get the data for the test framework
     * 
     * @param image Frame to test
     * @param filename What to call saved file
     */
    public ArrayList<Point> getTestData(){
    	
    	getCoords("Click top");
    	points.add(coords);
    	getCoords("Click bottom");
    	points.add(coords);
    	
    	return points;
    }

    /**
     * Get the color where we click
     * 
     * @param message Asks what to click on
     * @return The colour of pixel where we clicked
     */
    public Point getCoords(String message){
        System.out.println(message);

//        while (!mouseClick) {
//        	System.out.println("waiting for mouseclick");
//            try{
//                Thread.sleep(100);
//            } catch (Exception e) {}
//        }
        
        mouseClick = false;
        count++;
        return coords;
    }
    
    /**
     * Necessary to correct for the window borders
     * 
     * @param p Point to correct
     * @return Corrected Point
     */
    public Point correctPoint(Point p){
        return new Point(correctX(p.x),correctY(p.y));
    }
    
    public int correctX(int x){
    	return x-4;
    }
    
    public int correctY(int y){
    	return y-24;
    }
}
