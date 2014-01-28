
/**
 * Stores the states of the various thresholds.
 * 
 * @author s0840449
 *
 */
public class ThresholdsState {
	
	/* Ball. */
	private int ball_r_low;
	private int ball_r_high;
	private int ball_g_low;
	private int ball_g_high;
	private int ball_b_low;
	private int ball_b_high;
	private double ball_h_low;
	private double ball_h_high;
	private double ball_s_low;
	private double ball_s_high;
	private double ball_v_low;
	private double ball_v_high;
	
	/* Blue Goalkeeper Robot. */
	private int blueG_r_low;
	private int blueG_r_high;
	private int blueG_g_low;
	private int blueG_g_high;
	private int blueG_b_low;
	private int blueG_b_high;
	private double blueG_h_low;
	private double blueG_h_high;
	private double blueG_s_low;
	private double blueG_s_high;
	private double blueG_v_low;
	private double blueG_v_high;
	
	/* Blue Striker Robot. */
	private int blueS_r_low;
	private int blueS_r_high;
	private int blueS_g_low;
	private int blueS_g_high;
	private int blueS_b_low;
	private int blueS_b_high;
	private double blueS_h_low;
	private double blueS_h_high;
	private double blueS_s_low;
	private double blueS_s_high;
	private double blueS_v_low;
	private double blueS_v_high;
	
	/* Yellow Goalkeeper Robot. */
	private int yellowG_r_low;
	private int yellowG_r_high;
	private int yellowG_g_low;
	private int yellowG_g_high;
	private int yellowG_b_low;
	private int yellowG_b_high;
	private double yellowG_h_low;
	private double yellowG_h_high;
	private double yellowG_s_low;
	private double yellowG_s_high;
	private double yellowG_v_low;
	private double yellowG_v_high;
	
	/* Yellow Striker Robot. */
	private int yellowS_r_low;
	private int yellowS_r_high;
	private int yellowS_g_low;
	private int yellowS_g_high;
	private int yellowS_b_low;
	private int yellowS_b_high;
	private double yellowS_h_low;
	private double yellowS_h_high;
	private double yellowS_s_low;
	private double yellowS_s_high;
	private double yellowS_v_low;
	private double yellowS_v_high;
	
	/* Grey Circle. */
	private int grey_r_low;
	private int grey_r_high;
	private int grey_g_low;
	private int grey_g_high;
	private int grey_b_low;
	private int grey_b_high;
	private double grey_h_low;
	private double grey_h_high;
	private double grey_s_low;
	private double grey_s_high;
	private double grey_v_low;
	private double grey_v_high;
	
	/* Green plates */
	private int green_r_low;
	private int green_r_high;
	private int green_g_low;
	private int green_g_high;
	private int green_b_low;
	private int green_b_high;
	private double green_h_low;
	private double green_h_high;
	private double green_s_low;
	private double green_s_high;
	private double green_v_low;
	private double green_v_high;
	
	/* Debug flags. */
	private boolean ball_debug;
	private boolean blue_debug;
	private boolean yellow_debug;
	private boolean grey_debug;
	private boolean green_debug;
	
	/**
	 * Default constructor.
	 */
	public ThresholdsState() {
	}

	public int getBall_r_low() {
		return ball_r_low;
	}

	public void setBall_r_low(int ballRLow) {
		ball_r_low = ballRLow;
	}

	public int getBall_r_high() {
		return ball_r_high;
	}

	public void setBall_r_high(int ballRHigh) {
		ball_r_high = ballRHigh;
	}

	public int getBall_g_low() {
		return ball_g_low;
	}

	public void setBall_g_low(int ballGLow) {
		ball_g_low = ballGLow;
	}

	public int getBall_g_high() {
		return ball_g_high;
	}

	public void setBall_g_high(int ballGHigh) {
		ball_g_high = ballGHigh;
	}

	public int getBall_b_low() {
		return ball_b_low;
	}

	public void setBall_b_low(int ballBLow) {
		ball_b_low = ballBLow;
	}

	public int getBall_b_high() {
		return ball_b_high;
	}

	public void setBall_b_high(int ballBHigh) {
		ball_b_high = ballBHigh;
	}

	public double getBall_h_low() {
		return ball_h_low;
	}

	public void setBall_h_low(double ballHLow) {
		ball_h_low = ballHLow;
	}

	public double getBall_h_high() {
		return ball_h_high;
	}

	public void setBall_h_high(double ballHHigh) {
		ball_h_high = ballHHigh;
	}

	public double getBall_s_low() {
		return ball_s_low;
	}

	public void setBall_s_low(double ballSLow) {
		ball_s_low = ballSLow;
	}

	public double getBall_s_high() {
		return ball_s_high;
	}

	public void setBall_s_high(double ballSHigh) {
		ball_s_high = ballSHigh;
	}

	public double getBall_v_low() {
		return ball_v_low;
	}

	public void setBall_v_low(double ballVLow) {
		ball_v_low = ballVLow;
	}

	public double getBall_v_high() {
		return ball_v_high;
	}

	public void setBall_v_high(double ballVHigh) {
		ball_v_high = ballVHigh;
	}
	

	public int getBlueGoalkeeper_r_low() {
		return blueG_r_low;
	}
	
	public int getBlueStriker_r_low() {
		return blueS_r_low;
	}

	public void setBlueGoalkeeper_r_low(int blueRLow) {
		blueG_r_low = blueRLow;
	}
	
	public void setBlueStriker_r_low(int blueRLow) {
		blueS_r_low = blueRLow;
	}

	public int getBlueGoalkeeper_r_high() {
		return blueG_r_high;
	}
	
	public int getBlueStriker_r_high() {
		return blueS_r_high;
	}

	public void setBlueGoalkeeper_r_high(int blueRHigh) {
		blueG_r_high = blueRHigh;
	}
	
	public void setBlueStriker_r_high(int blueRHigh) {
		blueS_r_high = blueRHigh;
	}

	public int getBlueGoalkeeper_g_low() {
		return blueG_g_low;
	}
	
	public int getBlueStriker_g_low() {
		return blueS_g_low;
	}

	public void setBlueGoalkeeper_g_low(int blueGLow) {
		blueG_g_low = blueGLow;
	}
	
	public void setBlueStriker_g_low(int blueGLow) {
		blueS_g_low = blueGLow;
	}

	public int getBlueGoalkeeper_g_high() {
		return blueG_g_high;
	}
	
	public int getBlueStriker_g_high() {
		return blueS_g_high;
	}

	public void setBlueGoalkeeper_g_high(int blueGHigh) {
		blueG_g_high = blueGHigh;
	}
	
	public void setBlueStriker_g_high(int blueGHigh) {
		blueS_g_high = blueGHigh;
	}

	public int getBlueGoalkeeper_b_low() {
		return blueG_b_low;
	}
	
	public int getBlueStriker_b_low() {
		return blueS_b_low;
	}

	public void setBlueGoalkeeper_b_low(int blueBLow) {
		blueG_b_low = blueBLow;
	}
	
	public void setBlueStriker_b_low(int blueBLow) {
		blueS_b_low = blueBLow;
	}

	public int getBlueGoalkeeper_b_high() {
		return blueG_b_high;
	}
	
	public int getBlueStriker_b_high() {
		return blueS_b_high;
	}

	public void setBlueGoalkeeper_b_high(int blueBHigh) {
		blueG_b_high = blueBHigh;
	}
	
	public void setBlueStriker_b_high(int blueBHigh) {
		blueS_b_high = blueBHigh;
	}

	public double getBlueGoalkeeper_h_low() {
		return blueG_h_low;
	}
	
	public double getBlueStriker_h_low() {
		return blueS_h_low;
	}

	public void setBlueGoalkeeper_h_low(double blueHLow) {
		blueG_h_low = blueHLow;
	}
	
	public void setBlueStriker_h_low(double blueHLow) {
		blueS_h_low = blueHLow;
	}

	public double getBlueGoalkeeper_h_high() {
		return blueG_h_high;
	}
	
	public double getBlueStriker_h_high() {
		return blueS_h_high;
	}

	public void setBlueGoalkeeper_h_high(double blueHHigh) {
		blueG_h_high = blueHHigh;
	}
	
	public void setBlueStriker_h_high(double blueHHigh) {
		blueS_h_high = blueHHigh;
	}

	public double getBlueGoalkeeper_s_low() {
		return blueG_s_low;
	}
	
	public double getBlueStriker_s_low() {
		return blueS_s_low;
	}

	public void setBlueGoalkeeper_s_low(double blueSLow) {
		blueG_s_low = blueSLow;
	}
	
	public void setBlueStriker_s_low(double blueSLow) {
		blueS_s_low = blueSLow;
	}

	public double getBlueGoalkeeper_s_high() {
		return blueG_s_high;
	}
	
	public double getBlueStriker_s_high() {
		return blueS_s_high;
	}

	public void setBlueGoalkeeper_s_high(double blueSHigh) {
		blueG_s_high = blueSHigh;
	}
	
	public void setBlueStriker_s_high(double blueSHigh) {
		blueS_s_high = blueSHigh;
	}

	public double getBlueGoalkeeper_v_low() {
		return blueG_v_low;
	}
	
	public double getBlueStriker_v_low() {
		return blueS_v_low;
	}

	public void setBlueGoalkeeper_v_low(double blueVLow) {
		blueG_v_low = blueVLow;
	}
	
	public void setBlueStriker_v_low(double blueVLow) {
		blueS_v_low = blueVLow;
	}

	public double getBlueGoalkeeper_v_high() {
		return blueG_v_high;
	}
	
	public double getBlueStriker_v_high() {
		return blueS_v_high;
	}
	
	//Oh my god, I actually survived until here.

	public int getYellowGoalkeeper_r_low() {
		return yellowG_r_low;
	}
	
	public int getYellowStriker_r_low() {
		return yellowG_r_low;
	}

	public void setYellowGoalkeeper_r_low(int yellowRLow) {
		yellowG_r_low = yellowRLow;
	}
	
	public void setYellowStriker_r_low(int yellowRLow) {
		yellowS_r_low = yellowRLow;
	}

	public int getYellowGoalkeeper_r_high() {
		return yellowG_r_high;
	}
	
	public int getYellowStriker_r_high() {
		return yellowS_r_high;
	}

	public void setYellowGoalkeeper_r_high(int yellowRHigh) {
		yellowG_r_high = yellowRHigh;
	}
	
	public void setYellowStriker_r_high(int yellowRHigh) {
		yellowS_r_high = yellowRHigh;
	}

	public int getYellowGoalkeeper_g_low() {
		return yellowG_g_low;
	}
	
	public int getYellowStriker_g_low() {
		return yellowS_g_low;
	}

	public void setYellowGoalkeeper_g_low(int yellowGLow) {
		yellowG_g_low = yellowGLow;
	}
	
	public void setYellowStriker_g_low(int yellowGLow) {
		yellowS_g_low = yellowGLow;
	}

	public int getYellowGoalkeeper_g_high() {
		return yellowG_g_high;
	}
	
	public int getYellowStriker_g_high() {
		return yellowS_g_high;
	}

	public void setYellowGoalkeeper_g_high(int yellowGHigh) {
		yellowG_g_high = yellowGHigh;
	}
	
	public void setYellowStriker_g_high(int yellowGHigh) {
		yellowS_g_high = yellowGHigh;
	}

	public int getYellowGoalkeeper_b_low() {
		return yellowG_b_low;
	}
	
	public int getYellowStriker_b_low() {
		return yellowS_b_low;
	}

	public void setYellowGoalkeeper_b_low(int yellowBLow) {
		yellowG_b_low = yellowBLow;
	}
	
	public void setYellowStriker_b_low(int yellowBLow) {
		yellowS_b_low = yellowBLow;
	}

	public int getYellowGoalkeeper_b_high() {
		return yellowG_b_high;
	}
	
	public int getYellowStriker_b_high() {
		return yellowS_b_high;
	}

	public void setYellowStriker_b_high(int yellowBHigh) {
		yellowS_b_high = yellowBHigh;
	}
	
	public void setYellowGoalkeeper_b_high(int yellowBHigh) {
		yellowG_b_high = yellowBHigh;
	}

	public double getYellowGoalkeeper_h_low() {
		return yellowG_h_low;
	}
	
	public double getYellowStriker_h_low() {
		return yellowS_h_low;
	}

	public void setYellowGoalkeeper_h_low(double yellowHLow) {
		yellowG_h_low = yellowHLow;
	}
	
	public void setYellowStriker_h_low(double yellowHLow) {
		yellowS_h_low = yellowHLow;
	}

	public double getYellowGoalkeeper_h_high() {
		return yellowG_h_high;
	}
	
	public double getYellowStriker_h_high() {
		return yellowS_h_high;
	}

	public void setYellowGoalkeeper_h_high(double yellowHHigh) {
		yellowG_h_high = yellowHHigh;
	}
	
	public void setYellowStriker_h_high(double yellowHHigh) {
		yellowS_h_high = yellowHHigh;
	}

	public double getYellowGoalkeeper_s_low() {
		return yellowG_s_low;
	}
	
	public double getYellowStriker_s_low() {
		return yellowS_s_low;
	}

	public void setYellowGoalkeeper_s_low(double yellowSLow) {
		yellowG_s_low = yellowSLow;
	}
	
	public void setYellowStriker_s_low(double yellowSLow) {
		yellowS_s_low = yellowSLow;
	}

	public double getYellowGoalkeeper_s_high() {
		return yellowG_s_high;
	}
	
	public double getYellowStriker_s_high() {
		return yellowS_s_high;
	}

	public void setYellowGoalkeeper_s_high(double yellowSHigh) {
		yellowG_s_high = yellowSHigh;
	}
	
	public void setYellowStriker_s_high(double yellowSHigh) {
		yellowS_s_high = yellowSHigh;
	}

	public double getYellowGoalkeeper_v_low() {
		return yellowG_v_low;
	}
	
	public double getYellowStriker_v_low() {
		return yellowS_v_low;
	}

	public void setYellowGoalkeeper_v_low(double yellowVLow) {
		yellowG_v_low = yellowVLow;
	}
	
	public void setYellowStriker_v_low(double yellowVLow) {
		yellowS_v_low = yellowVLow;
	}

	public double getYellowGoalkeeper_v_high() {
		return yellowG_v_high;
	}
	
	public double getYellowStriker_v_high() {
		return yellowS_v_high;
	}

	public void setYellowGoalkeeper_v_high(double yellowVHigh) {
		yellowG_v_high = yellowVHigh;
	}
	
	public void setYellowStriker_v_high(double yellowVHigh) {
		yellowS_v_high = yellowVHigh;
	}

	public void setBlueGoalkeeper_v_high(double blueVHigh) {
		blueG_v_high = blueVHigh;
	}
	
	public void setBlueStriker_v_high(double blueVHigh) {
		blueS_v_high = blueVHigh;
	}
	
	//wow.

	public boolean isBall_debug() {
		return ball_debug;
	}

	public void setBall_debug(boolean ballDebug) {
		ball_debug = ballDebug;
	}

	public boolean isBlue_debug() {
		return blue_debug;
	}

	public void setBlue_debug(boolean blueDebug) {
		blue_debug = blueDebug;
	}

	public boolean isYellow_debug() {
		return yellow_debug;
	}

	public void setYellow_debug(boolean yellowDebug) {
		yellow_debug = yellowDebug;
	}

	public int getGrey_r_low() {
		return grey_r_low;
	}

	public void setGrey_r_low(int greyRLow) {
		grey_r_low = greyRLow;
	}

	public int getGrey_r_high() {
		return grey_r_high;
	}

	public void setGrey_r_high(int greyRHigh) {
		grey_r_high = greyRHigh;
	}

	public int getGrey_g_low() {
		return grey_g_low;
	}

	public void setGrey_g_low(int greyGLow) {
		grey_g_low = greyGLow;
	}

	public int getGrey_g_high() {
		return grey_g_high;
	}

	public void setGrey_g_high(int greyGHigh) {
		grey_g_high = greyGHigh;
	}

	public int getGrey_b_low() {
		return grey_b_low;
	}

	public void setGrey_b_low(int greyBLow) {
		grey_b_low = greyBLow;
	}

	public int getGrey_b_high() {
		return grey_b_high;
	}

	public void setGrey_b_high(int greyBHigh) {
		grey_b_high = greyBHigh;
	}

	public double getGrey_h_low() {
		return grey_h_low;
	}

	public void setGrey_h_low(double greyHLow) {
		grey_h_low = greyHLow;
	}

	public double getGrey_h_high() {
		return grey_h_high;
	}

	public void setGrey_h_high(double greyHHigh) {
		grey_h_high = greyHHigh;
	}

	public double getGrey_s_low() {
		return grey_s_low;
	}

	public void setGrey_s_low(double greySLow) {
		grey_s_low = greySLow;
	}

	public double getGrey_s_high() {
		return grey_s_high;
	}

	public void setGrey_s_high(double greySHigh) {
		grey_s_high = greySHigh;
	}

	public double getGrey_v_low() {
		return grey_v_low;
	}

	public void setGrey_v_low(double greyVLow) {
		grey_v_low = greyVLow;
	}

	public double getGrey_v_high() {
		return grey_v_high;
	}

	public void setGrey_v_high(double greyVHigh) {
		grey_v_high = greyVHigh;
	}

	public boolean isGrey_debug() {
		return grey_debug;
	}

	public void setGrey_debug(boolean greyDebug) {
		grey_debug = greyDebug;
	}

	/**
	 * @return the green_r_low
	 */
	public int getGreen_r_low() {
		return green_r_low;
	}

	public void setGreen_r_low(int greenRLow) {
		green_r_low = greenRLow;
	}

	public int getGreen_r_high() {
		return green_r_high;
	}

	public void setGreen_r_high(int greenRHigh) {
		green_r_high = greenRHigh;
	}

	public int getGreen_g_low() {
		return green_g_low;
	}

	public void setGreen_g_low(int greenGLow) {
		green_g_low = greenGLow;
	}

	public int getGreen_g_high() {
		return green_g_high;
	}

	public void setGreen_g_high(int greenGHigh) {
		green_g_high = greenGHigh;
	}

	public int getGreen_b_low() {
		return green_b_low;
	}

	public void setGreen_b_low(int greenBLow) {
		green_b_low = greenBLow;
	}

	public int getGreen_b_high() {
		return green_b_high;
	}

	public void setGreen_b_high(int greenBHigh) {
		green_b_high = greenBHigh;
	}

	public double getGreen_h_low() {
		return green_h_low;
	}

	public void setGreen_h_low(double greenHLow) {
		green_h_low = greenHLow;
	}

	public double getGreen_h_high() {
		return green_h_high;
	}

	public void setGreen_h_high(double greenHHigh) {
		green_h_high = greenHHigh;
	}

	public double getGreen_s_low() {
		return green_s_low;
	}

	public void setGreen_s_low(double greenSLow) {
		green_s_low = greenSLow;
	}

	public double getGreen_s_high() {
		return green_s_high;
	}

	public void setGreen_s_high(double greenSHigh) {
		green_s_high = greenSHigh;
	}

	public double getGreen_v_low() {
		return green_v_low;
	}

	public void setGreen_v_low(double greenVLow) {
		green_v_low = greenVLow;
	}

	public double getGreen_v_high() {
		return green_v_high;
	}

	public void setGreen_v_high(double greenVHigh) {
		green_v_high = greenVHigh;
	}
	
	public boolean isGreen_debug() {
		return green_debug;
	}

	public void setGreen_debug(boolean greenDebug) {
		green_debug = greenDebug;
	}

}
