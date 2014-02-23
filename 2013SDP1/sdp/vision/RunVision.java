package sdp.vision;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import sdp.vision.ui.VisionGUI;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class RunVision {
    private static VisionGUI thresholdsGUI;
    
    /**
     * The main method for the class. Creates the control
     * GUI, and initialises the image processing.
     * 
     * @param args        Program arguments. Not used.
     */
    public static void main(String[] args) {
    	WorldState worldState = new WorldState();
    	setupVision(worldState);
	}
    
    public static Vision setupVision(WorldState worldState) {
        ThresholdsState thresholdsState = new ThresholdsState();
        
        try
        {
           FileInputStream fileIn = new FileInputStream("./constants/pitchThresholds0.ser");
           ObjectInputStream in = new ObjectInputStream(fileIn);
           thresholdsState = (ThresholdsState) in.readObject();
           in.close();
           fileIn.close();
        } catch(IOException i)
        {
           i.printStackTrace();
        } catch(ClassNotFoundException c)
        {
           System.out.println("pitchThresholds0 class not found");
           c.printStackTrace();
        }

        /* Default to main pitch. */
        PitchConstants pitchConstants = new PitchConstants(0);
        /* Default values for the main vision window. */
        String videoDevice = "/dev/video0";
        int width = 640;
        int height = 480;
        int channel = 0;
        int videoStandard = V4L4JConstants.STANDARD_PAL;
        int compressionQuality = 80;
        Vision vision = null;

        try {
            /* Create a new Vision object to serve the main vision window. */
            vision = new Vision(videoDevice, width, height, channel, videoStandard,
                    compressionQuality, worldState, thresholdsState, pitchConstants);
            
            /* Create the Control GUI for threshold setting/etc. */
            thresholdsGUI = new VisionGUI(thresholdsState, worldState, pitchConstants, vision);      
            thresholdsGUI.initGUI();
        } catch (V4L4JException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vision;
    }
}
