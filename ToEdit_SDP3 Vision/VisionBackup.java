import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.ImageFormatException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/**
 * The main class for showing the video feed and processing the video
 * data. Identifies ball and robot locations, and robot orientations.
 *
 * @author s0840449
 * 
 * Possible improvements:
 * -Raise or lower the blob size constant?
 * -Combine vision with a simulated guess - feed all motions into simulator, guess/predict where objects will be 
 * next, storing momentums. Average this guess location with the vision output, giving the simulation a larger
 * weighting when vision is giving an erratic location? Sim should be given a weighting that is given a sudden boost when
 * the vision gives an erratic/conflicting location, and then falls exponentially till boosted again? Implement simulator in
 * 'WorldState' class
 * -Could subtract the background from the image
 * -Right now, the system ignores the green boards
 * 
 */
public class VisionBackup extends WindowAdapter {
    private VideoDevice videoDev;
    private JLabel label;
    private JFrame windowFrame;
    private FrameGrabber frameGrabber;
    private Thread captureThread;
    private boolean stop;
    private int width, height;
    private WorldState worldState;
    private ThresholdsState thresholdsState;
    private PitchConstants pitchConstants;
    //private int[] xDistortion;
    //private int[] yDistortion;

    /**
     * Default constructor.
     *
     * @param videoDevice           The video device file to capture from.
     * @param width                 The desired capture width.
     * @param height                The desired capture height.
     * @param videoStandard         The capture standard.
     * @param channel               The capture channel.
     * @param compressionQuality    The JPEG compression quality.
     * @param worldState
     * @param thresholdsState
     * @param pitchConstants
     *
     * @throws V4L4JException   If any parameter if invalid.
     */
    public VisionBackup(String videoDevice, int width, int height, int channel, int videoStandard,
            int compressionQuality, WorldState worldState, ThresholdsState thresholdsState,
            PitchConstants pitchConstants) throws V4L4JException {

        /* Set the state fields. */
        this.worldState = worldState;
        this.thresholdsState = thresholdsState;
        this.pitchConstants = pitchConstants;

        /* Initialise the GUI that displays the video feed. */
        initFrameGrabber(videoDevice, width, height, channel, videoStandard, compressionQuality);
        initGUI();
    }

     /**
     * Initialises a FrameGrabber object with the given parameters.
     *
     * @param videoDevice           The video device file to capture from.
     * @param inWidth               The desired capture width.
     * @param inHeight              The desired capture height.
     * @param channel               The capture channel.
     * @param videoStandard         The capture standard.
     * @param compressionQuality    The JPEG compression quality.
     *
     * @throws V4L4JException   If any parameter is invalid.
     */
    private void initFrameGrabber(String videoDevice, int inWidth, int inHeight, int channel,
            int videoStandard, int compressionQuality) throws V4L4JException {
        videoDev = new VideoDevice(videoDevice);

        DeviceInfo deviceInfo = videoDev.getDeviceInfo();

        if (deviceInfo.getFormatList().getNativeFormats().isEmpty()) {
          throw new ImageFormatException("Unable to detect any native formats for the device!");
        }
        ImageFormat imageFormat = deviceInfo.getFormatList().getNativeFormat(0);

        frameGrabber = videoDev.getJPEGFrameGrabber(inWidth, inHeight, channel, videoStandard,
                compressionQuality, imageFormat);

        frameGrabber.setCaptureCallback(new CaptureCallback() {
            public void exceptionReceived(V4L4JException e) {
                System.err.println("Unable to capture frame:");
                e.printStackTrace();
            }

            public void nextFrame(VideoFrame frame) {
                long before = System.currentTimeMillis();
                BufferedImage frameImage = frame.getBufferedImage();
                frame.recycle();
                processAndUpdateImage(frameImage, before);
            }
        });

        frameGrabber.startCapture();

        width = frameGrabber.getWidth();
        height = frameGrabber.getHeight();
    }

    /**
     * Creates the graphical interface components and initialises them
     */
    private void initGUI() {
        windowFrame = new JFrame("Vision Window");
        label = new JLabel();
        windowFrame.getContentPane().add(label);
        windowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        windowFrame.addWindowListener(this);
        windowFrame.setVisible(true);
        windowFrame.setSize(width, height);
    }

    /**
     * Catches the window closing event, so that we can free up resources
     * before exiting.
     *
     * @param e         The window closing event.
     */
    public void windowClosing(WindowEvent e) {
        /* Dispose of the various swing and v4l4j components. */
        frameGrabber.stopCapture();
        videoDev.releaseFrameGrabber();

        windowFrame.dispose();

        System.exit(0);
    }

    /**
     * Processes an input image, extracting the ball and robot positions and robot
     * orientations from it, and then displays the image (with some additional graphics
     * layered on top for debugging) in the vision frame.
     *
     * @param image     The image to process and then show.
     */
    public void processAndUpdateImage(BufferedImage image, long before) {

        /*
        //Lens distortion - not working fully
        BufferedImage image = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);


        int centerX = 320;
        int centerY = 240;
        float k = (float) 0.006;

        for (int i = 0; i < 480; i++) {
            for (int j = 0; j < 640; j++) {
                int x = (int) Math.floor(getRadialX(j, i, centerX, centerY, (float) Math.pow(k, 2)));
                int y = (int) Math.floor(getRadialY(j, i, centerX, centerY, (float) Math.pow(k, 2)));

                if (y >= 480) { y = 1; }
                if (x >= 640) { x = 1; }
                if (y < 0) { y = 1; }
                if (x < 0) { x = 1; }

                image.setRGB(j, i, input.getRGB(x, y));
            }
        }
        */


        /*
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                image.setRGB(j, i, input.getRGB(xDistortion[j], yDistortion[i]));
                //image.setRGB(j, i, input.getRGB(j, i));
            }
        }
        */

        int ballX = 0;
        int ballY = 0;
        int numBallPos = 0;

        int blueGoalkeeperX = 0;
        int blueGoalkeeperY = 0;
        int numBlueGoalkeeperPos = 0;
        
        /*Claudiu-Modified*/
        int blueStrikerX = 0;
        int blueStrikerY = 0;
        int numBlueStrikerPos = 0;
        /*Modified-Claudiu*/
        
        int yellowGoalkeeperX = 0;
        int yellowGoalkeeperY = 0;
        int numYellowGoalkeeperPos = 0;
        
        /*Claudiu-Modified*/
        int yellowStrikerX = 0;
        int yellowStrikerY = 0;
        int numYellowStrikerPos = 0;
        /*Modified-Claudiu*/

        ArrayList<Integer> ballXPoints = new ArrayList<Integer>();
        ArrayList<Integer> ballYPoints = new ArrayList<Integer>();
        ArrayList<Integer> blueGoalkeeperXPoints = new ArrayList<Integer>();
        ArrayList<Integer> blueGoalkeeperYPoints = new ArrayList<Integer>();
        ArrayList<Integer> blueStrikerXPoints = new ArrayList<Integer>();
        ArrayList<Integer> blueStrikerYPoints = new ArrayList<Integer>();
        ArrayList<Integer> yellowGoalkeeperXPoints = new ArrayList<Integer>();
        ArrayList<Integer> yellowGoalkeeperYPoints = new ArrayList<Integer>();
        ArrayList<Integer> yellowStrikerXPoints = new ArrayList<Integer>();
        ArrayList<Integer> yellowStrikerYPoints = new ArrayList<Integer>();
        
        
        int topBuffer = pitchConstants.topBuffer;
        int bottomBuffer = pitchConstants.bottomBuffer;
        int leftBuffer = pitchConstants.leftBuffer;
        int rightBuffer = pitchConstants.rightBuffer;

        
        /*Claudiu-Modified*/
        /*Code for first half of pitch - Identify yellowT, the blueT, green palte, grey circle + a ball?*/
        for (int row = topBuffer; row < image.getHeight() - bottomBuffer; row++) {

            for (int column = leftBuffer; column < (image.getWidth() - rightBuffer + leftBuffer)/2; column++) {

                /* The RGB colours and hsv values for the current pixel. */
                Color c = new Color(image.getRGB(column, row));
                float hsbvals[] = new float[3];
                Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);

                /* Debug graphics for the grey circles and green plates.
                 * TODO: Move these into the actual detection. */
                if (thresholdsState.isGrey_debug() && isGrey(c, hsbvals)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                if (thresholdsState.isGreen_debug() && isGreen(c, hsbvals)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }
                
                /* Is this pixel part of the Ball? */
                if (isBall(c, hsbvals)) {

                    ballX += column;
                    ballY += row;
                    numBallPos++;

                    ballXPoints.add(column);
                    ballYPoints.add(row);

                    /* If we're in the "Ball" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isBall_debug()) {
                        image.setRGB(column, row, 0xFF000000);
                    }
                }

                /* Is this pixel part of the BlueGoalkeeperT? */
                if (isBlueGoalkeeper(c, hsbvals) ){

                    blueGoalkeeperX += column;
                    blueGoalkeeperY += row;
                    numBlueGoalkeeperPos++;

                    blueGoalkeeperXPoints.add(column);
                    blueGoalkeeperYPoints.add(row);

                    /* If we're in the "Blue" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isBlue_debug()) {
                        image.setRGB(column, row, 0xFFFF0099);
                    }

                }

                /* Is this pixel part of the YellowGoalkeeperT? */
                if (isYellowGoalkeeper(c, hsbvals)) {

                    yellowGoalkeeperX += column;
                    yellowGoalkeeperY += row;
                    numYellowGoalkeeperPos++;

                    yellowGoalkeeperXPoints.add(column);
                    yellowGoalkeeperYPoints.add(row);

                    /* If we're in the "Yellow" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isYellow_debug()) {
                        image.setRGB(column, row, 0xFFFF0099);
                    }
                }
            }
        }
        /*Modified-Claudiu*/
        
        /*Claudiu-Modified*/
        /*Code for second half of pitch - Identify yellow2T, the blue2T, green palte, grey circle, + a ball?*/
        for (int row = topBuffer; row < image.getHeight() - bottomBuffer; row++) {

            for (int column = (image.getWidth() - rightBuffer + leftBuffer)/2; column < (image.getWidth() - rightBuffer); column++) {

                /* The RGB colours and hsv values for the current pixel. */
                Color c2 = new Color(image.getRGB(column, row));
                float hsbvals[] = new float[3];
                Color.RGBtoHSB(c2.getRed(), c2.getBlue(), c2.getGreen(), hsbvals);

                /* Debug graphics for the grey circles and green plates.
                 * TODO: Move these into the actual detection. */
                if (thresholdsState.isGrey_debug() && isGrey(c2, hsbvals)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                if (thresholdsState.isGreen_debug() && isGreen(c2, hsbvals)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }
                
                /* Is this pixel part of the Ball? */
                if (isBall(c2, hsbvals)) {

                    ballX += column;
                    ballY += row;
                    numBallPos++;

                    ballXPoints.add(column);
                    ballYPoints.add(row);

                    /* If we're in the "Ball" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isBall_debug()) {
                        image.setRGB(column, row, 0xFF000000);
                    }
                }

                /* Is this pixel part of the BlueStrikerT? */
                if (isBlueStriker(c2, hsbvals) ){

                    blueStrikerX += column;
                    blueStrikerY += row;
                    numBlueStrikerPos++;

                    blueStrikerXPoints.add(column);
                    blueStrikerYPoints.add(row);

                    /* If we're in the "Blue" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isBlue_debug()) {
                        image.setRGB(column, row, 0xFFFF0099);
                    }

                }

                /* Is this pixel part of the YellowStrikerT? */
                if (isYellowStriker(c2, hsbvals)) {

                    yellowStrikerX += column;
                    yellowStrikerY += row;
                    numYellowStrikerPos++;

                    yellowStrikerXPoints.add(column);
                    yellowStrikerYPoints.add(row);

                    /* If we're in the "Yellow" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isYellow_debug()) {
                        image.setRGB(column, row, 0xFFFF0099);
                    }
                }
            }
        }
        /*Modified-Claudiu*/

        /* Position objects to hold the centre point of the ball and both robots. */
        Position ball;
        Position blueGoalkeeper;
        Position blueStriker;
        Position yellowGoalkeeper;
        Position yellowStriker;
        

        /* If we have only found a few 'Ball' pixels, chances are that the ball has not
         * actually been detected. */
        if (numBallPos > 5) {
            ballX /= numBallPos;
            ballY /= numBallPos;

            ball = new Position(ballX, ballY);
            ball.fixValues(worldState.getBallX(), worldState.getBallY());
            ball.filterPoints(ballXPoints, ballYPoints);
        } else {
            ball = new Position(worldState.getBallX(), worldState.getBallY());
        }

        /* If we have only found a few 'Blue' pixels, chances are that a T has not
         * actually been detected. */

    	if (numBlueGoalkeeperPos > 0) {
            blueGoalkeeperX /= numBlueGoalkeeperPos;
            blueGoalkeeperY /= numBlueGoalkeeperPos;

            blueGoalkeeper = new Position(blueGoalkeeperX, blueGoalkeeperY);
            blueGoalkeeper.fixValues(worldState.getBlueGoalkeeperX(), worldState.getBlueGoalkeeperY());
            blueGoalkeeper.filterPoints(blueGoalkeeperXPoints, blueGoalkeeperYPoints);
        } else {
            blueGoalkeeper = new Position(worldState.getBlueGoalkeeperX(), worldState.getBlueGoalkeeperY());
        }
        
    	if (numBlueStrikerPos > 0) {
            blueStrikerX /= numBlueStrikerPos;
            blueStrikerY /= numBlueStrikerPos;

            blueStriker = new Position(blueStrikerX, blueStrikerY);
            blueStriker.fixValues(worldState.getBlueStrikerX(), worldState.getBlueStrikerY());
            blueStriker.filterPoints(blueStrikerXPoints, blueStrikerYPoints);
        } else {
            blueStriker = new Position(worldState.getBlueStrikerX(), worldState.getBlueStrikerY());
        }
    	
    	
        /* If we have only found a few 'Yellow' pixels, chances are that the yellow T has not
         * actually been detected. */
        if (numYellowGoalkeeperPos > 0) {
            yellowGoalkeeperX /= numYellowGoalkeeperPos;
            yellowGoalkeeperY /= numYellowGoalkeeperPos;

            yellowGoalkeeper = new Position(yellowGoalkeeperX, yellowGoalkeeperY);
            yellowGoalkeeper.fixValues(worldState.getYellowGoalkeeperX(), worldState.getYellowGoalkeeperY());
            yellowGoalkeeper.filterPoints(yellowGoalkeeperXPoints, yellowGoalkeeperYPoints);
        } else {
            yellowGoalkeeper = new Position(worldState.getYellowGoalkeeperX(), worldState.getYellowGoalkeeperY());
        }

        /*Claudiu-Modified*/
        if (numYellowStrikerPos > 0) {
            yellowStrikerX /= numYellowStrikerPos;
            yellowStrikerY /= numYellowStrikerPos;

            yellowStriker = new Position(yellowStrikerX, yellowStrikerY);
            yellowStriker.fixValues(worldState.getYellowStrikerX(), worldState.getYellowStrikerY());
            yellowStriker.filterPoints(yellowStrikerXPoints, yellowStrikerYPoints);
        } else {
            yellowStriker = new Position(worldState.getYellowStrikerX(), worldState.getYellowStrikerY());
        }
        /*Modified-Claudiu*/


        /* Attempt to find the blue Goalkeeper robot's orientation. */
        try {
            float blueGoalkeeperOrientation = findOrientation(blueGoalkeeperXPoints, blueGoalkeeperYPoints, blueGoalkeeper.getX(), blueGoalkeeper.getY(), image, true);
            float diff = Math.abs(blueGoalkeeperOrientation - worldState.getBlueGoalkeeperOrientation());
            if (diff > 0.1) {
                float angle = (float) Math.round(((blueGoalkeeperOrientation / Math.PI) * 180) / 5) * 5;
                worldState.setBlueGoalkeeperOrientation((float) (angle / 180 * Math.PI));
            }
        } catch (NoAngleException e) {
            worldState.setBlueGoalkeeperOrientation(worldState.getBlueGoalkeeperOrientation());
            System.out.println("Blue robot: " + e.getMessage());
        }

        /*Claudiu-Modified*/
        /* Attempt to find the blue Striker robot's orientation. */
        try {
            float blueStrikerOrientation = findOrientation(blueStrikerXPoints, blueStrikerYPoints, blueStriker.getX(), blueStriker.getY(), image, true);
            float diff2 = Math.abs(blueStrikerOrientation - worldState.getBlueStrikerOrientation());
            if (diff2 > 0.1) {
                float angle2 = (float) Math.round(((blueStrikerOrientation / Math.PI) * 180) / 5) * 5;
                worldState.setBlueStrikerOrientation((float) (angle2 / 180 * Math.PI));
            }
        } catch (NoAngleException e) {
            worldState.setBlueStrikerOrientation(worldState.getBlueStrikerOrientation());
            System.out.println("Blue2 robot: " + e.getMessage());
        }
        /*Modified-Claudiu*/

        /* Attempt to find the yellow Goalkeeper robot's orientation. */
        try {
            float yellowGoalkeeperOrientation = findOrientation(yellowGoalkeeperXPoints, yellowGoalkeeperYPoints, yellowGoalkeeper.getX(), yellowGoalkeeper.getY(), image, true);
            float diff = Math.abs(yellowGoalkeeperOrientation - worldState.getYellowGoalkeeperOrientation());
            if (yellowGoalkeeperOrientation != 0 && diff > 0.1) {
                float angle = (float) Math.round(((yellowGoalkeeperOrientation / Math.PI) * 180) / 5) * 5;
                worldState.setYellowGoalkeeperOrientation((float) (angle / 180 * Math.PI));
            }
        } catch (NoAngleException e) {
            worldState.setYellowGoalkeeperOrientation(worldState.getYellowGoalkeeperOrientation());
            System.out.println("Yellow robot: " + e.getMessage());
        }

        /*Claudiu-Modified*/
        /* Attempt to find the yellow2 robot's orientation. */
        try {
            float yellow2Orientation = findOrientation(yellowStrikerXPoints, yellowStrikerYPoints, yellowStriker.getX(), yellowStriker.getY(), image, true);
            float diff2 = Math.abs(yellow2Orientation - worldState.getYellowGoalkeeperOrientation());
            if (yellow2Orientation != 0 && diff2 > 0.1) {
                float angle2 = (float) Math.round(((yellow2Orientation / Math.PI) * 180) / 5) * 5;
                worldState.setYellowGoalkeeperOrientation((float) (angle2 / 180 * Math.PI));
            }
        } catch (NoAngleException e) {
            worldState.setYellowGoalkeeperOrientation(worldState.getYellowGoalkeeperOrientation());
            System.out.println("Yellow2 robot: " + e.getMessage());
        }
        /*Modified-Claudiu*/

        worldState.setBallX(ball.getX());
        worldState.setBallY(ball.getY());

        worldState.setBlueGoalkeeperX(blueGoalkeeper.getX());
        worldState.setBlueGoalkeeperY(blueGoalkeeper.getY());
        worldState.setYellowGoalkeeperX(yellowGoalkeeper.getX());
        worldState.setYellowGoalkeeperY(yellowGoalkeeper.getY());
        /*Claudiu-Modified*/
        worldState.setBlueStrikerX(blueStriker.getX());
        worldState.setBlueStrikerY(blueStriker.getY());
        worldState.setYellowStrikerX(yellowStriker.getX());
        worldState.setYellowStrikerY(yellowStriker.getY());
        worldState.updateCounter();
        /*Modified-Claudiu*/
        worldState.updateCounter();

        /* Draw the image onto the vision frame. */
        Graphics frameGraphics = label.getGraphics();
        Graphics imageGraphics = image.getGraphics();

        /* Only display these markers in non-debug mode. */
        if (!(thresholdsState.isBall_debug() || thresholdsState.isBlue_debug()
                || thresholdsState.isYellow_debug() || thresholdsState.isGreen_debug()
                || thresholdsState.isGrey_debug())) {
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, ball.getY(), 640, ball.getY());
            imageGraphics.drawLine(ball.getX(), 0, ball.getX(), 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval(blueGoalkeeper.getX()-15, blueGoalkeeper.getY()-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval(yellowGoalkeeper.getX()-15, yellowGoalkeeper.getY()-15, 30,30);
            imageGraphics.setColor(Color.white);
            /*Claudiu-Modified*/
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval(blueStriker.getX()-15, blueStriker.getY()-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval(yellowStriker.getX()-15, yellowStriker.getY()-15, 30,30);
            imageGraphics.setColor(Color.white);
            /*Modified-Claudiu*/

            /*
            float ax = (float) Math.cos(worldState.getBlueOrientation());
            float ay = (float) Math.sin(worldState.getBlueOrientation());
            imageGraphics.drawLine(blue.getX(), blue.getY(), (int) (ax*70), (int) (ay*70));

            ax = (float) Math.sin(worldState.getYellowOrientation());
            ay = (float) Math.cos(worldState.getYellowOrientation());
            imageGraphics.drawLine(yellow.getX(), yellow.getY(), (int) (ax*70), (int) (ay*70));
            */
        }

        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 15, 15);
        frameGraphics.drawImage(image, 0, 0, width, height, null);
    }

    /**
     * Determines if a pixel is part of the blue T, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the blue T),
     *                      false otherwise.
     */
    
    /*Code for Blue Goalkeeper*/
    private boolean isBlueGoalkeeper(Color color, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getBlueGoalkeeper_h_high() && hsbvals[0] >= thresholdsState.getBlueGoalkeeper_h_low() &&
        hsbvals[1] <= thresholdsState.getBlueGoalkeeper_s_high() && hsbvals[1] >= thresholdsState.getBlueGoalkeeper_s_low() &&
        hsbvals[2] <= thresholdsState.getBlueGoalkeeper_v_high() && hsbvals[2] >= thresholdsState.getBlueGoalkeeper_v_low() &&
        color.getRed() <= thresholdsState.getBlueGoalkeeper_r_high() && color.getRed() >= thresholdsState.getBlueGoalkeeper_r_low() &&
        color.getGreen() <= thresholdsState.getBlueGoalkeeper_g_high() && color.getGreen() >= thresholdsState.getBlueGoalkeeper_g_low() &&
        color.getBlue() <= thresholdsState.getBlueGoalkeeper_b_high() && color.getBlue() >= thresholdsState.getBlueGoalkeeper_b_low();
    }

    /*Code for Blue Striker*/
    private boolean isBlueStriker(Color color, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getBlueStriker_h_high() && hsbvals[0] >= thresholdsState.getBlueStriker_h_low() &&
        hsbvals[1] <= thresholdsState.getBlueStriker_s_high() && hsbvals[1] >= thresholdsState.getBlueStriker_s_low() &&
        hsbvals[2] <= thresholdsState.getBlueStriker_v_high() && hsbvals[2] >= thresholdsState.getBlueStriker_v_low() &&
        color.getRed() <= thresholdsState.getBlueStriker_r_high() && color.getRed() >= thresholdsState.getBlueStriker_r_low() &&
        color.getGreen() <= thresholdsState.getBlueStriker_g_high() && color.getGreen() >= thresholdsState.getBlueStriker_g_low() &&
        color.getBlue() <= thresholdsState.getBlueStriker_b_high() && color.getBlue() >= thresholdsState.getBlueStriker_b_low();
    }
    
    /**
     * Determines if a pixel is part of the yellow T, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the yellow T),
     *                      false otherwise.
     */
    
    /*Code for Yellow Goalkeeper*/
    private boolean isYellowGoalkeeper(Color colour, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getYellowGoalkeeper_h_high() && hsbvals[0] >= thresholdsState.getYellowGoalkeeper_h_low() &&
        hsbvals[1] <= thresholdsState.getYellowGoalkeeper_s_high() &&  hsbvals[1] >= thresholdsState.getYellowGoalkeeper_s_low() &&
        hsbvals[2] <= thresholdsState.getYellowGoalkeeper_v_high() &&  hsbvals[2] >= thresholdsState.getYellowGoalkeeper_v_low() &&
        colour.getRed() <= thresholdsState.getYellowGoalkeeper_r_high() &&  colour.getRed() >= thresholdsState.getYellowGoalkeeper_r_low() &&
        colour.getGreen() <= thresholdsState.getYellowGoalkeeper_g_high() && colour.getGreen() >= thresholdsState.getYellowGoalkeeper_g_low() &&
        colour.getBlue() <= thresholdsState.getYellowGoalkeeper_b_high() && colour.getBlue() >= thresholdsState.getYellowGoalkeeper_b_low();
    }
    
    /*Code for Yellow Striker*/
    private boolean isYellowStriker(Color colour, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getYellowStriker_h_high() && hsbvals[0] >= thresholdsState.getYellowStriker_h_low() &&
        hsbvals[1] <= thresholdsState.getYellowStriker_s_high() &&  hsbvals[1] >= thresholdsState.getYellowStriker_s_low() &&
        hsbvals[2] <= thresholdsState.getYellowStriker_v_high() &&  hsbvals[2] >= thresholdsState.getYellowStriker_v_low() &&
        colour.getRed() <= thresholdsState.getYellowStriker_r_high() &&  colour.getRed() >= thresholdsState.getYellowStriker_r_low() &&
        colour.getGreen() <= thresholdsState.getYellowStriker_g_high() && colour.getGreen() >= thresholdsState.getYellowStriker_g_low() &&
        colour.getBlue() <= thresholdsState.getYellowStriker_b_high() && colour.getBlue() >= thresholdsState.getYellowStriker_b_low();
    }

    /**
     * Determines if a pixel is part of the ball, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the ball),
     *                      false otherwise.
     */
    private boolean isBall(Color colour, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getBall_h_high() && hsbvals[0] >= thresholdsState.getBall_h_low() &&
        hsbvals[1] <= thresholdsState.getBall_s_high() &&  hsbvals[1] >= thresholdsState.getBall_s_low() &&
        hsbvals[2] <= thresholdsState.getBall_v_high() &&  hsbvals[2] >= thresholdsState.getBall_v_low() &&
        colour.getRed() <= thresholdsState.getBall_r_high() &&  colour.getRed() >= thresholdsState.getBall_r_low() &&
        colour.getGreen() <= thresholdsState.getBall_g_high() && colour.getGreen() >= thresholdsState.getBall_g_low() &&
        colour.getBlue() <= thresholdsState.getBall_b_high() && colour.getBlue() >= thresholdsState.getBall_b_low();
    }

    /**
     * Determines if a pixel is part of either grey circle, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a grey circle),
     *                      false otherwise.
     */
    private boolean isGrey(Color colour, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getGrey_h_high() && 
        hsbvals[0] >= thresholdsState.getGrey_h_low() &&
        hsbvals[1] <= thresholdsState.getGrey_s_high() &&  
        hsbvals[1] >= thresholdsState.getGrey_s_low() &&
        hsbvals[2] <= thresholdsState.getGrey_v_high() &&  
        hsbvals[2] >= thresholdsState.getGrey_v_low() &&
        colour.getRed() <= thresholdsState.getGrey_r_high() &&  
        colour.getRed() >= thresholdsState.getGrey_r_low() &&
        colour.getGreen() <= thresholdsState.getGrey_g_high() && 
        colour.getGreen() >= thresholdsState.getGrey_g_low() &&
        colour.getBlue() <= thresholdsState.getGrey_b_high() && 
        colour.getBlue() >= thresholdsState.getGrey_b_low();
    }

    /**
     * Determines if a pixel is part of either green plate, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a green plate),
     *                      false otherwise.
     */
    private boolean isGreen(Color colour, float[] hsbvals) {
        return hsbvals[0] <= thresholdsState.getGreen_h_high() && hsbvals[0] >= thresholdsState.getGreen_h_low() &&
        hsbvals[1] <= thresholdsState.getGreen_s_high() &&  hsbvals[1] >= thresholdsState.getGreen_s_low() &&
        hsbvals[2] <= thresholdsState.getGreen_v_high() &&  hsbvals[2] >= thresholdsState.getGreen_v_low() &&
        colour.getRed() <= thresholdsState.getGreen_r_high() &&  colour.getRed() >= thresholdsState.getGreen_r_low() &&
        colour.getGreen() <= thresholdsState.getGreen_g_high() && colour.getGreen() >= thresholdsState.getGreen_g_low() &&
        colour.getBlue() <= thresholdsState.getGreen_b_high() && colour.getBlue() >= thresholdsState.getGreen_b_low();
    }

    /**
     * Finds the orientation of a robot, given a list of the points contained within it's
     * T-shape (in terms of a list of x coordinates and y coordinates), the mean x and
     * y coordinates, and the image from which it was taken.
     *
     * @param xpoints           The x-coordinates of the points contained within the T-shape.
     * @param ypoints           The y-coordinates of the points contained within the T-shape.
     * @param meanX             The mean x-point of the T.
     * @param meanY             The mean y-point of the T.
     * @param image             The image from which the points were taken.
     * @param showImage         A boolean flag - if true a line will be drawn showing
     *                          the direction of orientation found.
     *
     * @return                  An orientation from -Pi to Pi degrees.
     * @throws NoAngleException
     */
    public float findOrientation(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints,
            int meanX, int meanY, BufferedImage image, boolean showImage) throws NoAngleException {
        assert (xpoints.size() == ypoints.size()) :
            "Error: Must be equal number of x and y points!";

        if (xpoints.size() == 0) {
            throw new NoAngleException("No T pixels");
        }

        int stdev = 0;
        /* Standard deviation */
        for (int i = 0; i < xpoints.size(); i++) {
            int x = xpoints.get(i);
            int y = ypoints.get(i);

            stdev += Math.pow(Math.sqrt(Position.sqrdEuclidDist(x, y, meanX, meanY)), 2);
        }
        stdev  = (int) Math.sqrt(stdev / xpoints.size());

        /* Find the position of the front of the T. */
        int frontX = 0;
        int frontY = 0;
        int frontCount = 0;
        for (int i = 0; i < xpoints.size(); i++) {
            if (stdev > 15) {
                if (Math.abs(xpoints.get(i) - meanX) < stdev && Math.abs(ypoints.get(i) - meanY) < stdev &&
                        Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > Math.pow(15, 2)) {
                    frontCount++;
                    frontX += xpoints.get(i);
                    frontY += ypoints.get(i);
                    if (!(thresholdsState.isBlue_debug()||thresholdsState.isYellow_debug())) {image.setRGB(xpoints.get(i),ypoints.get(i),0xFF9900FF);}
                }
            } else {
                if (Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > Math.pow(15, 2)) {
                    frontCount++;
                    frontX += xpoints.get(i);
                    frontY += ypoints.get(i);
                    if (!(thresholdsState.isBlue_debug()||thresholdsState.isYellow_debug())) {image.setRGB(xpoints.get(i),ypoints.get(i),0xFF9900FF);}
                }
            }
        }

        /* If no points were found, we'd better bail. */
        if (frontCount == 0) {
            for (int i = 0; i < xpoints.size(); i++) {
                if (stdev > 15) {
                    if (Math.abs(xpoints.get(i) - meanX) < stdev && Math.abs(ypoints.get(i) - meanY) < stdev &&
                            Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > Math.pow(13, 2)) {
                        frontCount++;
                        frontX += xpoints.get(i);
                        frontY += ypoints.get(i);
                        if (!(thresholdsState.isBlue_debug()||thresholdsState.isYellow_debug())) {image.setRGB(xpoints.get(i),ypoints.get(i),0xFF00FFFF);}
                    }
                } else {
                    if (Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > Math.pow(13, 2)) {
                        frontCount++;
                        frontX += xpoints.get(i);
                        frontY += ypoints.get(i);
                        if (!(thresholdsState.isBlue_debug()||thresholdsState.isYellow_debug())) {image.setRGB(xpoints.get(i),ypoints.get(i),0xFF00FFFF);}
                    }
                }
            }

            /* If no points were found, we'd better bail. */
            if (frontCount == 0) {
                throw new NoAngleException("Front of T was not found");
            }
        }

        /* Otherwise, get the frontX and Y. */
        frontX /= frontCount;
        frontY /= frontCount;

        /* In here, calculate the vector between meanX/frontX and
         * meanY/frontY, and then get the angle of that vector. */

        // Calculate the angle from center of the T to the front of the T
        float length = (float) Math.sqrt(Math.pow(frontX - meanX, 2)
                + Math.pow(frontY - meanY, 2));
        float ax = (frontX - meanX) / length;
        float ay = (frontY - meanY) / length;
        float angle = (float) Math.acos(ax);

        if (frontY < meanY) {
            angle = -angle;
        }

        //Look in a cone in the opposite direction to try to find the grey circle
        ArrayList<Integer> greyXPoints = new ArrayList<Integer>();
        ArrayList<Integer> greyYPoints = new ArrayList<Integer>();

        for (int a=-18; a < 18; a++) {
            ax = (float) Math.cos(angle+((a*Math.PI)/180));
            ay = (float) Math.sin(angle+((a*Math.PI)/180));
            for (int i = 15; i < 24; i++) {
                int greyX = meanX - (int) (ax * i);
                int greyY = meanY - (int) (ay * i);
                try {
                    Color c = new Color(image.getRGB(greyX, greyY));
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (thresholdsState.isBlue_debug()||thresholdsState.isYellow_debug()) {image.setRGB(greyX,greyY,0xFFFFFFFF);}                 
                    if (isGrey(c, hsbvals)||(c.equals(new Color(0xFF000000)))) {
                        greyXPoints.add(greyX);
                        greyYPoints.add(greyY);
                        if (thresholdsState.isBlue_debug()||thresholdsState.isYellow_debug()) {image.setRGB(greyX,greyY,0xFF000000);}
                        }
                    } catch (Exception e) {
                    //This happens if part of the search area goes outside the image
                    //This is okay, just ignore and continue
                }
            }
        }
        /* No grey circle found
         * The angle found is probably wrong, skip this value and return 0 */

        if (greyXPoints.size() < 3) {
            throw new NoAngleException("No grey circle found");
        }

        /* Calculate center of grey circle points */
        int totalX = 0;
        int totalY = 0;
        for (int i = 0; i < greyXPoints.size(); i++) {
            totalX += greyXPoints.get(i);
            totalY += greyYPoints.get(i);
        }

        /* Center of grey circle */
        float backX = totalX / greyXPoints.size();
        float backY = totalY / greyXPoints.size();

        /* Check that the circle is surrounded by the green plate
         * Currently checks above and below the circle */

        int foundGreen = 0;
        int greenSides = 0;
        /* Check if green points are above the grey circle */
        for (int x=(int) (backX-2); x < (int) (backX+3); x++) {
            for (int y = (int) (backY-9); y < backY; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }


        /* Check if green points are below the grey circle */
        foundGreen = 0;
        for (int x=(int) (backX-2); x < (int) (backX+3); x++) {
            for (int y = (int) (backY); y < backY+10; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }


        /* Check if green points are left of the grey circle */
        foundGreen = 0;
        for (int x=(int) (backX-9); x < backX; x++) {
            for (int y = (int) (backY-2); y < backY+3; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }

        /* Check if green points are right of the grey circle */
        foundGreen = 0;
        for (int x=(int) (backX); x < (int) (backX+10); x++) {
            for (int y = (int) (backY-2); y < backY+3; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }


        if (greenSides < 1) {
            throw new NoAngleException(greenSides+" is Not enough green areas around the grey circle");
        }


        /*
         * At this point, the following is true:
         * Center of the T has been found
         * Front of the T has been found
         * Grey circle has been found
         * Grey circle is surrounded by green plate pixels on at least 3 sides
         * The grey circle, center of the T and front of the T line up roughly with the same angle
         */

        /* Calculate new angle using just the center of the T and the grey circle */
        length = (float) Math.sqrt(Math.pow(meanX - backX, 2)
                + Math.pow(meanY - backY, 2));
        ax = (meanX - backX) / length;
        ay = (meanY - backY) / length;
        angle = (float) Math.acos(ax);

        if (frontY < meanY) {
            angle = -angle;
        }

        if (showImage) {
            image.getGraphics().drawLine((int)backX, (int)backY, (int)(backX+ax*70), (int)(backY+ay*70));
            image.getGraphics().drawOval((int) backX-4, (int) backY-4, 8, 8);
        }

        if (angle == 0) {
            return (float) 0.001;
        }

        return angle;
    }

    /* Doesn't work */
    /*
    private void calculateDistortion() {
        this.xDistortion = new int[640];
        this.yDistortion = new int[480];

        int centerX = 320;
        int centerY = 240;
        float k = (float) 0.01;

        for (int i = 0; i < 480; i++) {
            for (int j = 0; j < 640; j++) {
                int x = (int) Math.floor(getRadialX(j, i, centerX, centerY, (float) Math.pow(k, 2)));
                int y = (int) Math.floor(getRadialY(j, i, centerX, centerY, (float) Math.pow(k, 2)));

                if (y >= 480) { y = 240; }
                if (x >= 640) { x = 320; }

                xDistortion[j] = x;
                yDistortion[i] = y;
            }
        }
    }
    */
}
