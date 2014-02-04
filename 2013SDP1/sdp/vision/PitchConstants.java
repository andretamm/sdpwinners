package sdp.vision;
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
	public WorldState worldState;

	/* Ball. */
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
	public int ball_rg_low;
	public int ball_rg_high;
	public int ball_rb_low;
	public int ball_rb_high;
	public int ball_gb_low;
	public int ball_gb_high;
	
	/* Blue Robot. */
	public int blue_r_low;
	public int blue_r_high;
	public int blue_g_low;
	public int blue_g_high;
	public int blue_b_low;
	public int blue_b_high;
	public int blue_h_low;
	public int blue_h_high;
	public int blue_s_low;
	public int blue_s_high;
	public int blue_v_low;
	public int blue_v_high;
	public int blue_rg_low;
	public int blue_rg_high;
	public int blue_rb_low;
	public int blue_rb_high;
	public int blue_gb_low;
	public int blue_gb_high;
	
	/* Yellow Robot. */
	public int yellow_r_low;
	public int yellow_r_high;
	public int yellow_g_low;
	public int yellow_g_high;
	public int yellow_b_low;
	public int yellow_b_high;
	public int yellow_h_low;
	public int yellow_h_high;
	public int yellow_s_low;
	public int yellow_s_high;
	public int yellow_v_low;
	public int yellow_v_high;
	public int yellow_rg_low;
	public int yellow_rg_high;
	public int yellow_rb_low;
	public int yellow_rb_high;
	public int yellow_gb_low;
	public int yellow_gb_high;
	
	
	/* Grey Circle. */
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
	public int grey_rg_low;
	public int grey_rg_high;
	public int grey_rb_low;
	public int grey_rb_high;
	public int grey_gb_low;
	public int grey_gb_high;
	
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
	public int green_rg_low;
	public int green_rg_high;
	public int green_rb_low;
	public int green_rb_high;
	public int green_gb_low;
	public int green_gb_high;
	
	/*Quadrant*/
	public int q1_low;
	public int q1_high;
	public int q2_low;
	public int q2_high;
	public int q3_low;
	public int q3_high;
	public int q4_low;
	public int q4_high;
	
	/* Pitch dimensions:
	 * When scanning the pitch we look at pixels starting from topBuffer and 
	 * leftBuffer, and then scan to pixels at 480-bottomBuffer and 
	 * 640-rightBuffer. */
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
		System.out.println("The constants are being loaded from "+fileName);
		
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
		

		/* Ball. */
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
		this.ball_rg_low = scanner.nextInt();
		this.ball_rg_high = scanner.nextInt();
		this.ball_rb_low = scanner.nextInt();
		this.ball_rb_high = scanner.nextInt();
		this.ball_gb_low = scanner.nextInt();
		this.ball_gb_high = scanner.nextInt();
		
		/* Blue Robot. */
		this.blue_r_low = scanner.nextInt();
		this.blue_r_high = scanner.nextInt();
		this.blue_g_low = scanner.nextInt();
		this.blue_g_high = scanner.nextInt();
		this.blue_b_low = scanner.nextInt();
		this.blue_b_high = scanner.nextInt();
		this.blue_h_low = scanner.nextInt();
		this.blue_h_high = scanner.nextInt();
		this.blue_s_low = scanner.nextInt();
		this.blue_s_high = scanner.nextInt();
		this.blue_v_low = scanner.nextInt();
		this.blue_v_high = scanner.nextInt();
		this.blue_rg_low = scanner.nextInt();
		this.blue_rg_high = scanner.nextInt();
		this.blue_rb_low = scanner.nextInt();
		this.blue_rb_high = scanner.nextInt();
		this.blue_gb_low = scanner.nextInt();
		this.blue_gb_high = scanner.nextInt();
		
		/* Yellow Robot. */
		this.yellow_r_low = scanner.nextInt();
		this.yellow_r_high = scanner.nextInt();
		this.yellow_g_low = scanner.nextInt();
		this.yellow_g_high = scanner.nextInt();
		this.yellow_b_low = scanner.nextInt();
		this.yellow_b_high = scanner.nextInt();
		this.yellow_h_low = scanner.nextInt();
		this.yellow_h_high = scanner.nextInt();
		this.yellow_s_low = scanner.nextInt();
		this.yellow_s_high = scanner.nextInt();
		this.yellow_v_low = scanner.nextInt();
		this.yellow_v_high = scanner.nextInt();
		this.yellow_rg_low = scanner.nextInt();
		this.yellow_rg_high = scanner.nextInt();
		this.yellow_rb_low = scanner.nextInt();
		this.yellow_rb_high = scanner.nextInt();
		this.yellow_gb_low = scanner.nextInt();
		this.yellow_gb_high = scanner.nextInt();
		
		
		/* Grey Circle. */
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
		this.grey_rg_low = scanner.nextInt();
		this.grey_rg_high = scanner.nextInt();
		this.grey_rb_low = scanner.nextInt();
		this.grey_rb_high = scanner.nextInt();
		this.grey_gb_low = scanner.nextInt();
		this.grey_gb_high = scanner.nextInt();
		
		/* Green plates */
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
		this.green_rg_low = scanner.nextInt();
		this.green_rg_high = scanner.nextInt();
		this.green_rb_low = scanner.nextInt();
		this.green_rb_high = scanner.nextInt();
		this.green_gb_low = scanner.nextInt();
		this.green_gb_high = scanner.nextInt();
		
		
		/* Pitch Dimensions */
		this.topBuffer = scanner.nextInt();
		this.bottomBuffer = scanner.nextInt();
		this.leftBuffer = scanner.nextInt();
		this.rightBuffer = scanner.nextInt();
		
		/* Quadrants */
		 this.q1_low = scanner.nextInt();
		 this.q1_high = scanner.nextInt();
		 this.q2_low = scanner.nextInt();
		 this.q2_high = scanner.nextInt();
		 this.q3_low = scanner.nextInt();
		 this.q3_high = scanner.nextInt();
		 this.q4_low = scanner.nextInt();
		 this.q4_high = scanner.nextInt();
	}
	
	/**
	 * Loads default values for the constants, used when loading from a file
	 * fails.
	 */
	public void loadDefaultConstants() {


		/* Ball. */
		this.ball_r_low = 0;
		this.ball_r_high = 255;
		this.ball_g_low = 0;
		this.ball_g_high = 255;
		this.ball_b_low = 0;
		this.ball_b_high = 255;
		this.ball_h_low = 0;
		this.ball_h_high = 255;
		this.ball_s_low = 0;
		this.ball_s_high = 255;
		this.ball_v_low = 0;
		this.ball_v_high = 255;
		this.ball_rg_low = -255;
		this.ball_rg_high = 255;
		this.ball_rb_low = -255;
		this.ball_rb_high = 255;
		this.ball_gb_low = -255;
		this.ball_gb_high = 255;
		
		/* Blue Robot. */
		this.blue_r_low = 0;
		this.blue_r_high = 255;
		this.blue_g_low = 0;
		this.blue_g_high = 255;
		this.blue_b_low = 0;
		this.blue_b_high = 255;
		this.blue_h_low = 0;
		this.blue_h_high = 255;
		this.blue_s_low = 0;
		this.blue_s_high = 255;
		this.blue_v_low = 0;
		this.blue_v_high = 255;
		this.blue_rg_low = -255;
		this.blue_rg_high = 255;
		this.blue_rb_low = -255;
		this.blue_rb_high = 255;
		this.blue_gb_low = -255;
		this.blue_gb_high = 255;
		
		/* Yellow Robot. */
		this.yellow_r_low = 0;
		this.yellow_r_high = 255;
		this.yellow_g_low = 0;
		this.yellow_g_high = 255;
		this.yellow_b_low = 0;
		this.yellow_b_high = 255;
		this.yellow_h_low = 0;
		this.yellow_h_high = 255;
		this.yellow_s_low = 0;
		this.yellow_s_high = 255;
		this.yellow_v_low = 0;
		this.yellow_v_high = 255;
		this.yellow_rg_low = -255;
		this.yellow_rg_high = 255;
		this.yellow_rb_low = -255;
		this.yellow_rb_high = 255;
		this.yellow_gb_low = -255;
		this.yellow_gb_high = 255;
		
		
		/* Grey Circle. */
		this.grey_r_low = 0;
		this.grey_r_high = 255;
		this.grey_g_low = 0;
		this.grey_g_high = 255;
		this.grey_b_low = 0;
		this.grey_b_high = 255;
		this.grey_h_low = 0;
		this.grey_h_high = 255;
		this.grey_s_low = 0;
		this.grey_s_high = 255;
		this.grey_v_low = 0;
		this.grey_v_high = 255;
		this.grey_rg_low = -255;
		this.grey_rg_high = 255;
		this.grey_rb_low = -255;
		this.grey_rb_high = 255;
		this.grey_gb_low = -255;
		this.grey_gb_high = 255;
		
		/* Green plates */
		this.green_r_low = 0;
		this.green_r_high = 255;
		this.green_g_low = 0;
		this.green_g_high = 255;
		this.green_b_low = 0;
		this.green_b_high = 255;
		this.green_h_low = 0;
		this.green_h_high = 255;
		this.green_s_low = 0;
		this.green_s_high = 255;
		this.green_v_low = 0;
		this.green_v_high = 255;
		this.green_rg_low = -255;
		this.green_rg_high = 255;
		this.green_rb_low = -255;
		this.green_rb_high = 255;
		this.green_gb_low = -255;
		this.green_gb_high = 255;

		/* Pitch Dimensions */
		this.topBuffer = 0;
		this.bottomBuffer = 0;
		this.leftBuffer = 0;
		this.rightBuffer = 0;
	
	}

}
