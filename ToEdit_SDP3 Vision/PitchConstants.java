import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A state object that holds the constants for various values about
 * the pitch, such as thresholding values and dimension variables.
 * 
 * @author s0840449
 */
public class PitchConstants {
	
	/* The pitch number. 0 is the main pitch, 1 is the side pitch. */
	private int pitchNum;

	/* Ball */
	public int ball_r_low;
	public int ball_r_high;
	public int ball_g_low;
	public int ball_g_high;
	public int ball_b_low;
	public int ball_b_high;
	public int ball_h_low;
	public int ball_h_high;
	public int ball_s_low;
	public int ball_s_high;
	public int ball_v_low;
	public int ball_v_high;

	/* Blue Goalkeeper Robot */
	public int blueG_r_low;
	public int blueG_r_high;
	public int blueG_g_low;
	public int blueG_g_high;
	public int blueG_b_low;
	public int blueG_b_high;
	public int blueG_h_low;
	public int blueG_h_high;
	public int blueG_s_low;
	public int blueG_s_high;
	public int blueG_v_low;
	public int blueG_v_high;
	
	/* Blue Striker Robot */
	public int blueS_r_low;
	public int blueS_r_high;
	public int blueS_g_low;
	public int blueS_g_high;
	public int blueS_b_low;
	public int blueS_b_high;
	public int blueS_h_low;
	public int blueS_h_high;
	public int blueS_s_low;
	public int blueS_s_high;
	public int blueS_v_low;
	public int blueS_v_high;

	/* Yellow Goalkeeper Robot */
	public int yellowG_r_low;
	public int yellowG_r_high;
	public int yellowG_g_low;
	public int yellowG_g_high;
	public int yellowG_b_low;
	public int yellowG_b_high;
	public int yellowG_h_low;
	public int yellowG_h_high;
	public int yellowG_s_low;
	public int yellowG_s_high;
	public int yellowG_v_low;
	public int yellowG_v_high;
	
	/* Yellow Striker Robot */
	public int yellowS_r_low;
	public int yellowS_r_high;
	public int yellowS_g_low;
	public int yellowS_g_high;
	public int yellowS_b_low;
	public int yellowS_b_high;
	public int yellowS_h_low;
	public int yellowS_h_high;
	public int yellowS_s_low;
	public int yellowS_s_high;
	public int yellowS_v_low;
	public int yellowS_v_high;
	
	/* Grey Circles */
	public int grey_r_low;
	public int grey_r_high;
	public int grey_g_low;
	public int grey_g_high;
	public int grey_b_low;
	public int grey_b_high;
	public int grey_h_low;
	public int grey_h_high;
	public int grey_s_low;
	public int grey_s_high;
	public int grey_v_low;
	public int grey_v_high;
	
	/* Green plates */
	public int green_r_low;
	public int green_r_high;
	public int green_g_low;
	public int green_g_high;
	public int green_b_low;
	public int green_b_high;
	public int green_h_low;
	public int green_h_high;
	public int green_s_low;
	public int green_s_high;
	public int green_v_low;
	public int green_v_high;
	
	/* Pitch dimensions:
	 * When scanning the pitch we look at pixels starting from 0 + topBuffer and 
	 * 0 + leftBuffer, and then scan to pixels at 480 - bottomBuffer and 
	 * 640 - rightBuffer. */
	public int topBuffer;
	public int bottomBuffer;
	public int leftBuffer;
	public int rightBuffer;
	
	/**
	 * Default constructor.
	 * 
	 * @param pitchNum		The pitch that we are on.
	 */
	public PitchConstants(int pitchNum) {
		
		/* Just call the setPitchNum method to load in the constants. */
		setPitchNum(pitchNum);
		
	}
	
	/**
	 * Sets a new pitch number, loading in constants from the corresponding file.
	 * 	
	 * @param newPitchNum		The pitch number to use.
	 */
	public void setPitchNum(int newPitchNum) {
		
		assert (newPitchNum >= 0 && newPitchNum <= 1);
		
		this.pitchNum = newPitchNum;
		
		loadConstants("./constants/pitch" + pitchNum);
		
	}
	
	/**
	 * Load in the constants from a file. Note that this assumes that the constants
	 * file is well formed.
	 * 
	 * @param fileName		The file name to load constants from.
	 */
	public void loadConstants(String fileName) {
		
		Scanner scanner;
		
		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot load constants file " + fileName + ":");
			System.err.println(e.getMessage());
			loadDefaultConstants();
			return;
		}
		
		assert(scanner != null);
		
		/* We assume that the file is well formed. */
		
		/* Ball */
		this.ball_r_low = scanner.nextInt();
		this.ball_r_high = scanner.nextInt();
		this.ball_g_low = scanner.nextInt();
		this.ball_g_high = scanner.nextInt();
		this.ball_b_low = scanner.nextInt();
		this.ball_b_high = scanner.nextInt();
		this.ball_h_low = scanner.nextInt();
		this.ball_h_high = scanner.nextInt();
		this.ball_s_low = scanner.nextInt();
		this.ball_s_high = scanner.nextInt();
		this.ball_v_low = scanner.nextInt();
		this.ball_v_high = scanner.nextInt();

		/* Blue Goalkeeper Robot */
		this.blueG_r_low = scanner.nextInt();
		this.blueG_r_high = scanner.nextInt();
		this.blueG_g_low = scanner.nextInt();
		this.blueG_g_high = scanner.nextInt();
		this.blueG_b_low = scanner.nextInt();
		this.blueG_b_high = scanner.nextInt();
		this.blueG_h_low = scanner.nextInt();
		this.blueG_h_high = scanner.nextInt();
		this.blueG_s_low = scanner.nextInt();
		this.blueG_s_high = scanner.nextInt();
		this.blueG_v_low = scanner.nextInt();
		this.blueG_v_high = scanner.nextInt();
		
		/* Blue Striker Robot */
		this.blueS_r_low = scanner.nextInt();
		this.blueS_r_high = scanner.nextInt();
		this.blueS_g_low = scanner.nextInt();
		this.blueS_g_high = scanner.nextInt();
		this.blueS_b_low = scanner.nextInt();
		this.blueS_b_high = scanner.nextInt();
		this.blueS_h_low = scanner.nextInt();
		this.blueS_h_high = scanner.nextInt();
		this.blueS_s_low = scanner.nextInt();
		this.blueS_s_high = scanner.nextInt();
		this.blueS_v_low = scanner.nextInt();
		this.blueS_v_high = scanner.nextInt();

		/* Yellow Goalkeeper Robot */
		this.yellowG_r_low = scanner.nextInt();
		this.yellowG_r_high = scanner.nextInt();
		this.yellowG_g_low = scanner.nextInt();
		this.yellowG_g_high = scanner.nextInt();
		this.yellowG_b_low = scanner.nextInt();
		this.yellowG_b_high = scanner.nextInt();
		this.yellowG_h_low = scanner.nextInt();
		this.yellowG_h_high = scanner.nextInt();
		this.yellowG_s_low = scanner.nextInt();
		this.yellowG_s_high = scanner.nextInt();
		this.yellowG_v_low = scanner.nextInt();
		this.yellowG_v_high = scanner.nextInt();
		
		/* Yellow Striker Robot */
		this.yellowS_r_low = scanner.nextInt();
		this.yellowS_r_high = scanner.nextInt();
		this.yellowS_g_low = scanner.nextInt();
		this.yellowS_g_high = scanner.nextInt();
		this.yellowS_b_low = scanner.nextInt();
		this.yellowS_b_high = scanner.nextInt();
		this.yellowS_h_low = scanner.nextInt();
		this.yellowS_h_high = scanner.nextInt();
		this.yellowS_s_low = scanner.nextInt();
		this.yellowS_s_high = scanner.nextInt();
		this.yellowS_v_low = scanner.nextInt();
		this.yellowS_v_high = scanner.nextInt();
		
		/* Grey Circles */
		this.grey_r_low = scanner.nextInt();
		this.grey_r_high = scanner.nextInt();
		this.grey_g_low = scanner.nextInt();
		this.grey_g_high = scanner.nextInt();
		this.grey_b_low = scanner.nextInt();
		this.grey_b_high = scanner.nextInt();
		this.grey_h_low = scanner.nextInt();
		this.grey_h_high = scanner.nextInt();
		this.grey_s_low = scanner.nextInt();
		this.grey_s_high = scanner.nextInt();
		this.grey_v_low = scanner.nextInt();
		this.grey_v_high = scanner.nextInt();
		
		/* Green Plates */
		this.green_r_low = scanner.nextInt();
		this.green_r_high = scanner.nextInt();
		this.green_g_low = scanner.nextInt();
		this.green_g_high = scanner.nextInt();
		this.green_b_low = scanner.nextInt();
		this.green_b_high = scanner.nextInt();
		this.green_h_low = scanner.nextInt();
		this.green_h_high = scanner.nextInt();
		this.green_s_low = scanner.nextInt();
		this.green_s_high = scanner.nextInt();
		this.green_v_low = scanner.nextInt();
		this.green_v_high = scanner.nextInt();
		
		/* Pitch Dimensions */
		this.topBuffer = scanner.nextInt();
		this.bottomBuffer = scanner.nextInt();
		this.leftBuffer = scanner.nextInt();
		this.rightBuffer = scanner.nextInt();
		
	}
	
	/**
	 * Loads default values for the constants, used when loading from a file
	 * fails.
	 */
	public void loadDefaultConstants() {
		
		/* Ball */
		this.ball_r_low = 0;
		this.ball_r_high = 255;
		this.ball_g_low = 0;
		this.ball_g_high = 255;
		this.ball_b_low = 0;
		this.ball_b_high = 255;
		this.ball_h_low = 0;
		this.ball_h_high = 10;
		this.ball_s_low = 0;
		this.ball_s_high = 10;
		this.ball_v_low = 0;
		this.ball_v_high = 10;

		/* Blue Goalkeeper Robot */
		this.blueG_r_low = 0;
		this.blueG_r_high = 255;
		this.blueG_g_low = 0;
		this.blueG_g_high = 255;
		this.blueG_b_low = 0;
		this.blueG_b_high = 255;
		this.blueG_h_low = 0;
		this.blueG_h_high = 10;
		this.blueG_s_low = 0;
		this.blueG_s_high = 10;
		this.blueG_v_low = 0;
		this.blueG_v_high = 10;
		
		/* Blue Striker Robot */
		this.blueS_r_low = 0;
		this.blueS_r_high = 255;
		this.blueS_g_low = 0;
		this.blueS_g_high = 255;
		this.blueS_b_low = 0;
		this.blueS_b_high = 255;
		this.blueS_h_low = 0;
		this.blueS_h_high = 10;
		this.blueS_s_low = 0;
		this.blueS_s_high = 10;
		this.blueS_v_low = 0;
		this.blueS_v_high = 10;

		/* Yellow Goalkeeper Robot */
		this.yellowG_r_low = 0;
		this.yellowG_r_high = 255;
		this.yellowG_g_low = 0;
		this.yellowG_g_high = 255;
		this.yellowG_b_low = 0;
		this.yellowG_b_high = 255;
		this.yellowG_h_low = 0;
		this.yellowG_h_high = 10;
		this.yellowG_s_low = 0;
		this.yellowG_s_high = 10;
		this.yellowG_v_low = 0;
		this.yellowG_v_high = 10;
		
		/* Yellow Striker Robot */
		this.yellowS_r_low = 0;
		this.yellowS_r_high = 255;
		this.yellowS_g_low = 0;
		this.yellowS_g_high = 255;
		this.yellowS_b_low = 0;
		this.yellowS_b_high = 255;
		this.yellowS_h_low = 0;
		this.yellowS_h_high = 10;
		this.yellowS_s_low = 0;
		this.yellowS_s_high = 10;
		this.yellowS_v_low = 0;
		this.yellowS_v_high = 10;
		
		/* Grey Circles */
		this.grey_r_low = 0;
		this.grey_r_high = 255;
		this.grey_g_low = 0;
		this.grey_g_high = 255;
		this.grey_b_low = 0;
		this.grey_b_high = 255;
		this.grey_h_low = 0;
		this.grey_h_high = 10;
		this.grey_s_low = 0;
		this.grey_s_high = 10;
		this.grey_v_low = 0;
		this.grey_v_high = 10;
		
		
		/* Green plates */
		this.green_r_low = 0;
		this.green_r_high = 255;
		this.green_g_low = 0;
		this.green_g_high = 255;
		this.green_b_low = 0;
		this.green_b_high = 255;
		this.green_h_low = 0;
		this.green_h_high = 10;
		this.green_s_low = 0;
		this.green_s_high = 10;
		this.green_v_low = 0;
		this.green_v_high = 10;

		/* Pitch Dimensions */
		this.topBuffer = 0;
		this.bottomBuffer = 0;
		this.leftBuffer = 0;
		this.rightBuffer = 0;
	
	}

}
