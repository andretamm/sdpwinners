package vision.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vision.ObjectThresholdState;
import vision.QuadrantThresholdsState;
import vision.ThresholdsState;
import vision.Vision;
import vision.WorldState;
import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.ShootingDirection;

/**
 * Creates and maintains the swingbased Control GUI, which provides both control
 * manipulation (pitch choice, direction, etc) and threshold setting. Also
 * allows the saving/loading of threshold values to a file.
 * 
 * @author s0840449
 */
public class VisionGUI implements ChangeListener {

	/*
	 * The thresholds state class stores the current state of the thresholds.
	 */
	private ThresholdsState thresholdsState;

	/*
	 * Stores information about the current world state, such as shooting
	 * direction, ball location, etc.
	 */
	private WorldState worldState;

	/*
	 * The current quadrant thresholding values we're changing
	 */
	private Quadrant q;

	// In calibration mode
	private boolean calibrationMode = false;

	/*
	 * The vision!
	 */
	private Vision vision;

	/* The main frame holding the Control GUI. */
	private JFrame frame;
	/* Load/Save buttons. */
	private JButton saveButton;
	private JButton loadButton;
	/* Tabs. */
	private JTabbedPane tabPane;
	private JPanel defaultPanel;
	private JPanel ballPanel;
	private JPanel bluePanel;
	private JPanel yellowPanel;
	private JPanel greyPanel;
	private JPanel greenPanel;
	private JPanel quadrantPanel;

	/* Radio buttons */
	JRadioButton pitch_0;
	JRadioButton pitch_1;
	JRadioButton colour_yellow;
	JRadioButton colour_blue;
	JRadioButton direction_right;
	JRadioButton direction_left;
	JRadioButtonMenuItem quadrant1;
	JRadioButtonMenuItem quadrant2;
	JRadioButtonMenuItem quadrant3;
	JRadioButtonMenuItem quadrant4;

	/* Ball sliders. */
	private RangeSlider ball_r;
	private RangeSlider ball_g;
	private RangeSlider ball_b;
	private RangeSlider ball_h;
	private RangeSlider ball_s;
	private RangeSlider ball_v;
	private RangeSlider ball_rg;
	private RangeSlider ball_rb;
	private RangeSlider ball_gb;

	/* Blue robot sliders. */
	private RangeSlider blue_r;
	private RangeSlider blue_g;
	private RangeSlider blue_b;
	private RangeSlider blue_h;
	private RangeSlider blue_s;
	private RangeSlider blue_v;
	private RangeSlider blue_rg;
	private RangeSlider blue_rb;
	private RangeSlider blue_gb;

	/* Yellow robot sliders. */
	private RangeSlider yellow_r;
	private RangeSlider yellow_g;
	private RangeSlider yellow_b;
	private RangeSlider yellow_h;
	private RangeSlider yellow_s;
	private RangeSlider yellow_v;
	private RangeSlider yellow_rg;
	private RangeSlider yellow_rb;
	private RangeSlider yellow_gb;

	/* Grey circle sliders. */
	private RangeSlider grey_r;
	private RangeSlider grey_g;
	private RangeSlider grey_b;
	private RangeSlider grey_h;
	private RangeSlider grey_s;
	private RangeSlider grey_v;
	private RangeSlider grey_rg;
	private RangeSlider grey_rb;
	private RangeSlider grey_gb;

	/* Green circle sliders. */
	private RangeSlider green_r;
	private RangeSlider green_g;
	private RangeSlider green_b;
	private RangeSlider green_h;
	private RangeSlider green_s;
	private RangeSlider green_v;
	private RangeSlider green_rg;
	private RangeSlider green_rb;
	private RangeSlider green_gb;

	/* Quadrant Sliders. */
	private RangeSlider q1;
	private RangeSlider q2;
	private RangeSlider q3;
	private RangeSlider q4;

	/* Calibrate button */
	private JButton calibrate;
	private Point pixelClicked;

	/**
	 * Default constructor.
	 * 
	 * @param thresholdsState
	 *            A ThresholdsState object to update the threshold slider
	 *            values.
	 * @param worldState
	 *            A WorldState object to update the pitch choice, shooting
	 *            direction, etc.
	 * @param pitchConstants
	 *            A PitchConstants object to allow saving/loading of data.
	 */
	public VisionGUI(ThresholdsState thresholdsState, WorldState worldState,
			Vision vision) {

		/* All three state objects must not be null. */
		assert (thresholdsState != null);
		assert (worldState != null);

		this.thresholdsState = thresholdsState;
		this.worldState = worldState;
		this.vision = vision;
	}

	/**
	 * Initialise the GUI, setting up all of the components and adding the
	 * required listeners.
	 */
	public void initGUI() {

		frame = new JFrame("Control GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		vision.getWindowFrame().getComponent(0).addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (calibrationMode) {
					pixelClicked = arg0.getPoint();
//					System.out.println(pixelClicked);
					calibrationMode = false;
					
					System.out.println("Pixel Clicked:" + pixelClicked.x + " " + pixelClicked.y);
					
					BufferedImage screenCapture = vision.getFrameImage();
					int colour = screenCapture.getRGB(pixelClicked.x, pixelClicked.y);

					int  red = (colour & 0x00ff0000) >> 16;
					int  green = (colour & 0x0000ff00) >> 8;
					int  blue = colour & 0x000000ff;
					
					System.out.println("R: " + red + " G: " + green + " B: " + blue);
					
					int index = tabPane.getSelectedIndex();
					
					Colours c = null;
					switch (index) {
					case (1):
						c = Colours.RED;
					break;
					case (2):
						c = Colours.BLUE;
					break;
					case (3):
						c = Colours.YELLOW;
					break;
					case (4):
						c = Colours.GRAY;
					break;
					case (5):
						c = Colours.GREEN;
					break;
					}
					ObjectThresholdState ots = thresholdsState.getQuadrantThresholds(q).getObjectThresholds(c);
					ots.set_r_low(red-10);
					ots.set_r_high(red+10);
					ots.set_g_low(green-10);
					ots.set_g_high(green+10);
					ots.set_b_low(blue-10);
					ots.set_b_high(blue+10);
					
					float[] hsv = new float[3];
					Color.RGBtoHSB(red, green, blue, hsv);
					ots.set_h_low(hsv[0]);
					ots.set_h_high(hsv[0]);
					ots.set_s_low(hsv[1]);
					ots.set_s_high(hsv[1]);
					ots.set_v_low(hsv[2]);
					ots.set_v_high(hsv[2]);
					
					ots.set_rb_low(red-blue - 20);
					ots.set_rb_high(red-blue + 20);
					ots.set_gb_low(green-blue-20);
					ots.set_gb_high( green-blue+20);
					ots.set_rg_low(red-green-20);
					ots.set_rg_high(red-green+20);
					
					reloadSliderDefaults();
					
//					Graphics g = screenCapture.getGraphics();
//					g.setColor(Color.BLUE);
//					
//					g.fillOval(pixelClicked.x, pixelClicked.y, 1, 1);
//					JFrame frame = new JFrame();
//					frame.getContentPane().setLayout(new FlowLayout());
//					frame.getContentPane().add(new JLabel(new ImageIcon(screenCapture)));
//					frame.pack();
//					frame.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		frame.setLayout(new FlowLayout());

		/* Create panels for each of the tabs */
		tabPane = new JTabbedPane();

		defaultPanel = new JPanel();
		defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.Y_AXIS));

		ballPanel = new JPanel();
		ballPanel.setLayout(new BoxLayout(ballPanel, BoxLayout.Y_AXIS));

		bluePanel = new JPanel();
		bluePanel.setLayout(new BoxLayout(bluePanel, BoxLayout.Y_AXIS));

		yellowPanel = new JPanel();
		yellowPanel.setLayout(new BoxLayout(yellowPanel, BoxLayout.Y_AXIS));

		greyPanel = new JPanel();
		greyPanel.setLayout(new BoxLayout(greyPanel, BoxLayout.Y_AXIS));

		greenPanel = new JPanel();
		greenPanel.setLayout(new BoxLayout(greenPanel, BoxLayout.Y_AXIS));

		quadrantPanel = new JPanel();
		quadrantPanel.setLayout(new BoxLayout(quadrantPanel, BoxLayout.Y_AXIS));

		/* Quadrant Choice */

		ButtonGroup quadrant_choice = new ButtonGroup();
		quadrant1 = new JRadioButtonMenuItem("q1");
		quadrant2 = new JRadioButtonMenuItem("q2");
		quadrant3 = new JRadioButtonMenuItem("q3");
		quadrant4 = new JRadioButtonMenuItem("q4");
		quadrant_choice.add(quadrant1);
		frame.add(quadrant1);
		quadrant_choice.add(quadrant2);
		frame.add(quadrant2);
		quadrant_choice.add(quadrant3);
		frame.add(quadrant3);
		quadrant_choice.add(quadrant4);
		frame.add(quadrant4);

		quadrant1.setSelected(true);
		q = Quadrant.Q1;

		MouseListener quadrantRadioButtonClickListener = new QuadrantRadioButtonClickListener();
		quadrant1.addMouseListener(quadrantRadioButtonClickListener);
		quadrant2.addMouseListener(quadrantRadioButtonClickListener);
		quadrant3.addMouseListener(quadrantRadioButtonClickListener);
		quadrant4.addMouseListener(quadrantRadioButtonClickListener);

		calibrate = new JButton("Calibrate!");
		frame.add(calibrate);

		MouseListener calibrateButtonClickListener = new CalibrateButtonClickListener();
		calibrate.addMouseListener(calibrateButtonClickListener);

		/* The main (default) tab */
		setUpMainPanel();

		/* The five threshold tabs. */
		setUpBallSliders();
		setUpBlueSliders();
		setUpYellowSliders();
		setUpGreySliders();
		setUpGreenSliders();
		setUpQuadrantSliders();

		tabPane.addTab("default", defaultPanel);
		tabPane.addTab("Ball", ballPanel);
		tabPane.addTab("Blue Robot", bluePanel);
		tabPane.addTab("Yellow Robot", yellowPanel);
		tabPane.addTab("Grey Circles", greyPanel);
		tabPane.addTab("Green Plates", greenPanel);
		tabPane.addTab("Quadrant Guides", quadrantPanel);

		tabPane.addChangeListener(this);

		frame.add(tabPane);

		//frame.pack();
		frame.setSize(350, 540);
		frame.setLocation(640, 0);
		frame.setVisible(true);

		/*
		 * Fires off an initial pass through the ChangeListener method, to
		 * initialise all of the default values.
		 */
		// pitchConstants.loadConstants("./constants/pitch" +
		// worldState.getPitch());
		this.stateChanged(null);

		// Andre's hack - need to do this to make the lines and overlays appear
		// on screen
		pitch_1.doClick();
		pitch_0.doClick();

	}

	class CalibrateButtonClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			calibrationMode = true;
		}

	}

	/**
	 * Listener for the radio buttons for switching quadrants
	 */
	class QuadrantRadioButtonClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		// After we have clicked on a Quadrant button, update state
		@Override
		public void mouseReleased(MouseEvent e) {
			if (quadrant1.isSelected()) {
				q = Quadrant.Q1;
			} else if (quadrant2.isSelected()) {
				q = Quadrant.Q2;
			} else if (quadrant3.isSelected()) {
				q = Quadrant.Q3;
			} else if (quadrant4.isSelected()) {
				q = Quadrant.Q4;
			}
			reloadSliderDefaults();
		}
	}

	/**
	 * Reloads the thresholds for the appropriate pitch, as indicated by the
	 * radiobuttons.
	 * 
	 * @author Thomas Wallace
	 * 
	 */
	class PitchRadioButtonListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			int pitchNum = (pitch_0.isSelected()) ? 0 : 1;
			if (worldState.getPitch() != pitchNum) {
				worldState.setPitch(pitchNum);
				thresholdsState.setPitchNum(pitchNum);
				reloadSliderDefaults();
			}
		}
	}

	/**
	 * Sets up the main tab, adding in the pitch choice, the direction choice,
	 * the robotcolour choice and save/load buttons.
	 */
	private void setUpMainPanel() {

		/* Colour choice */
		JPanel colour_panel = new JPanel();
		JLabel colour_label = new JLabel("Our colour:");
		colour_panel.add(colour_label);

		ButtonGroup colour_choice = new ButtonGroup();
		colour_yellow = new JRadioButton("Yellow");
		colour_blue = new JRadioButton("Blue");
		colour_choice.add(colour_yellow);
		colour_panel.add(colour_yellow);
		colour_choice.add(colour_blue);
		colour_panel.add(colour_blue);

		colour_yellow.setSelected(true);

		colour_yellow.addChangeListener(this);
		colour_blue.addChangeListener(this);

		defaultPanel.add(colour_panel);

		/* Direction choice */
		JPanel direction_panel = new JPanel();
		JLabel direction_label = new JLabel("Our shooting direction:");
		direction_panel.add(direction_label);

		ButtonGroup direction_choice = new ButtonGroup();
		direction_right = new JRadioButton("Right");
		direction_left = new JRadioButton("Left");
		direction_choice.add(direction_right);
		direction_panel.add(direction_right);
		direction_choice.add(direction_left);
		direction_panel.add(direction_left);

		direction_right.setSelected(true);

		direction_right.addChangeListener(this);
		direction_left.addChangeListener(this);

		defaultPanel.add(direction_panel);

		/* Save/load buttons */
		JPanel saveLoadPanel = new JPanel();

		saveButton = new JButton("Save Thresholds");
		saveButton.addActionListener(new ActionListener() {

			/*
			 * Attempt to write all of the current thresholds to a file with a
			 * name based on the currently selected pitch.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {

				int pitchNum = (pitch_0.isSelected()) ? 0 : 1;

				int result = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to save current"
								+ "constants for pitch " + pitchNum + "?");

				if (result == JOptionPane.NO_OPTION
						|| result == JOptionPane.CANCEL_OPTION)
					return;

//				try {
//					FileWriter writer = new FileWriter(new File(
//							"constants/pitch" + pitchNum));

					/*
					 * We need to rewrite the pitch dimensions. TODO: This
					 * currently means that crosssaving values is basically
					 * unsupported as they will overwrite the pitch dimensions
					 * incorrectly.
					 */
//					writer.write(String.valueOf(pitchConstants.topBuffer)
//							+ "\n");
//					writer.write(String.valueOf(pitchConstants.bottomBuffer)
//							+ "\n");
//					writer.write(String.valueOf(pitchConstants.leftBuffer)
//							+ "\n");
//					writer.write(String.valueOf(pitchConstants.rightBuffer)
//							+ "\n");

					/* Write pitch quadrant X values to constants */
//					writer.write(String.valueOf(q1.getValue()) + "\n");
//					writer.write(String.valueOf(q1.getUpperValue()) + "\n");
//					writer.write(String.valueOf(q2.getValue()) + "\n");
//					writer.write(String.valueOf(q2.getUpperValue()) + "\n");
//					writer.write(String.valueOf(q3.getValue()) + "\n");
//					writer.write(String.valueOf(q3.getUpperValue()) + "\n");
//					writer.write(String.valueOf(q4.getValue()) + "\n");
//					writer.write(String.valueOf(q4.getUpperValue()) + "\n");

//					writer.flush();
//					writer.close();
//
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}

				try {
					FileOutputStream fileOut = new FileOutputStream(
							"constants/pitchThresholds" + pitchNum + ".ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(thresholdsState);
					out.close();
					fileOut.close();
					// System.out.printf("Serialized data is saved in /tmp/employee.ser");
				} catch (IOException i) {
					i.printStackTrace();
				}

				System.out.println("Wrote successfully!");

			}
		});

		saveLoadPanel.add(saveButton);

		loadButton = new JButton("Load Thresholds");
		loadButton.addActionListener(new ActionListener() {

			/*
			 * Override the current threshold settings from those set in the
			 * correct constants file for the current pitch.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {

				int pitchNum = (pitch_0.isSelected()) ? 0 : 1;

				int result = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to load "
								+ "pre-saved constants for pitch " + pitchNum
								+ "?");

				if (result == JOptionPane.NO_OPTION
						|| result == JOptionPane.CANCEL_OPTION)
					return;

				ThresholdsState newState = new ThresholdsState();

				try {
					FileInputStream fileIn = new FileInputStream(
							"constants/pitchThresholds" + pitchNum + ".ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					newState = (ThresholdsState) in.readObject();
					in.close();
					fileIn.close();

					System.out.println("ThresholdState " + pitchNum
							+ " was loaded!");
				} catch (IOException i) {
					i.printStackTrace();
				} catch (ClassNotFoundException c)

				{
					System.out.println("pitchThresholds" + pitchNum
							+ " class not found");
					c.printStackTrace();
				}

				thresholdsState.updateState(newState);
				reloadSliderDefaults();

			}
		});

		saveLoadPanel.add(loadButton);
		/*
		 * startButton = new JButton("Start"); startButton.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { try {
		 * BasicStrategy.friendlyMatch(mVision, worldState); } catch
		 * (InterruptedException e1) { // TODO Autogenerated catch block
		 * e1.printStackTrace(); } catch (IOException e1) { // TODO
		 * Autogenerated catch block e1.printStackTrace(); } } });
		 * 
		 * stopButton = new JButton("Stop"); stopButton.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { try {
		 * BasicStrategy.m.setMoving(false); } catch (IOException e1) { // TODO
		 * Autogenerated catch block e1.printStackTrace(); } } });
		 * saveLoadPanel.add(startButton); saveLoadPanel.add(stopButton);
		 */

		defaultPanel.add(saveLoadPanel);
	}

	/**
	 * Sets up the sliders for the thresholding of the ball.
	 */
	private void setUpBallSliders() {

		// /* Quadrant Choice */
		// JPanel quadrant_panel = new JPanel();
		// JLabel quadrant_label = new JLabel("Quadrant Values:");
		// quadrant_panel.add(quadrant_label);
		//
		// ButtonGroup quadrant_choice = new ButtonGroup();
		// quadrant1 = new JRadioButton("q1");
		// quadrant2 = new JRadioButton("q2");
		// quadrant3 = new JRadioButton("q3");
		// quadrant4 = new JRadioButton("q4");
		// quadrant_choice.add(quadrant1);
		// quadrant_panel.add(quadrant1);
		// quadrant_choice.add(quadrant2);
		// quadrant_panel.add(quadrant2);
		// quadrant_choice.add(quadrant3);
		// quadrant_panel.add(quadrant3);
		// quadrant_choice.add(quadrant4);
		// quadrant_panel.add(quadrant4);
		//
		quadrant1.setSelected(true);
		q = Quadrant.Q1;

		/* Pitch choice */
		JPanel pitch_panel = new JPanel();
		JLabel pitch_label = new JLabel("Pitch:");
		pitch_panel.add(pitch_label);

		ButtonGroup pitch_choice = new ButtonGroup();
		pitch_0 = new JRadioButton("Main");
		pitch_1 = new JRadioButton("Side Room");
		pitch_choice.add(pitch_0);
		pitch_panel.add(pitch_0);
		pitch_choice.add(pitch_1);
		pitch_panel.add(pitch_1);

		pitch_0.setSelected(true);

		ChangeListener pitchRadioButtonListener = new PitchRadioButtonListener();
		pitch_0.addChangeListener(pitchRadioButtonListener);
		pitch_1.addChangeListener(pitchRadioButtonListener);

		defaultPanel.add(pitch_panel);
		QuadrantThresholdsState quadrant;

		/* Red. */
		JPanel ball_r_panel = new JPanel();
		JLabel ball_r_label = new JLabel("Red:");
		quadrant = thresholdsState.getQuadrantThresholds(q);

		ball_r = setUpSlider(0, 255, quadrant.getObjectThresholds(Colours.RED)
				.get_r_low(), quadrant.getObjectThresholds(Colours.RED)
				.get_r_high(), 10, 50);
		ball_r_panel.add(ball_r_label);
		ball_r_panel.add(ball_r);
		ballPanel.add(ball_r_panel);

		/* Green. */
		JPanel ball_g_panel = new JPanel();
		JLabel ball_g_label = new JLabel("Green:");
		ball_g = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.RED).get_g_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.RED)
				.get_g_high(), 10, 50);
		ball_g_panel.add(ball_g_label);
		ball_g_panel.add(ball_g);
		ballPanel.add(ball_g_panel);

		/* Blue. */
		JPanel ball_b_panel = new JPanel();
		JLabel ball_b_label = new JLabel("Blue:");
		ball_b = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.RED).get_b_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.RED)
				.get_b_high(), 10, 50);
		ball_b_panel.add(ball_b_label);
		ball_b_panel.add(ball_b);
		ballPanel.add(ball_b_panel);

		/* Hue. */
		JPanel ball_h_panel = new JPanel();
		JLabel ball_h_label = new JLabel("Hue:");
		ball_h = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_h_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_h_high()), 10, 50);
		ball_h_panel.add(ball_h_label);
		ball_h_panel.add(ball_h);
		ballPanel.add(ball_h_panel);

		/* Sat. */
		JPanel ball_s_panel = new JPanel();
		JLabel ball_s_label = new JLabel("Sat:");
		ball_s = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_s_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_s_high()), 10, 50);
		ball_s_panel.add(ball_s_label);
		ball_s_panel.add(ball_s);
		ballPanel.add(ball_s_panel);

		/* Value. */
		JPanel ball_v_panel = new JPanel();
		JLabel ball_v_label = new JLabel("Value:");
		ball_v = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_v_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_v_high()), 10, 50);
		ball_v_panel.add(ball_v_label);
		ball_v_panel.add(ball_v);
		ballPanel.add(ball_v_panel);

		/* Red - Green. */
		JPanel ball_rg_panel = new JPanel();
		JLabel ball_rg_label = new JLabel("Red - Green:");
		ball_rg = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_rg_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_rg_high(), 20,
				100);
		ball_rg_panel.add(ball_rg_label);
		ball_rg_panel.add(ball_rg);
		ballPanel.add(ball_rg_panel);

		/* Red - Blue. */
		JPanel ball_rb_panel = new JPanel();
		JLabel ball_rb_label = new JLabel("Red - Blue");
		ball_rb = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_rb_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_rb_high(), 20,
				100);
		ball_rb_panel.add(ball_rb_label);
		ball_rb_panel.add(ball_rb);
		ballPanel.add(ball_rb_panel);

		/* Green - Blue */
		JPanel ball_gb_panel = new JPanel();
		JLabel ball_gb_label = new JLabel("Green - Blue");
		ball_gb = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_gb_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.RED).get_gb_high(), 20,
				100);
		ball_gb_panel.add(ball_gb_label);
		ball_gb_panel.add(ball_gb);
		ballPanel.add(ball_gb_panel);

		// Andre's hack
		ball_r.addChangeListener(this);
		ball_g.addChangeListener(this);
		ball_b.addChangeListener(this);
		ball_h.addChangeListener(this);
		ball_s.addChangeListener(this);
		ball_v.addChangeListener(this);
		ball_rg.addChangeListener(this);
		ball_rb.addChangeListener(this);
		ball_gb.addChangeListener(this);

	}

	/**
	 * Sets up the sliders for the thresholding of the blue robot.
	 */
	private void setUpBlueSliders() {

		/* Red. */
		JPanel blue_r_panel = new JPanel();
		JLabel blue_r_label = new JLabel("Red:");
		blue_r = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.BLUE).get_r_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.BLUE)
				.get_r_high(), 10, 50);
		blue_r_panel.add(blue_r_label);
		blue_r_panel.add(blue_r);
		bluePanel.add(blue_r_panel);

		/* Green. */
		JPanel blue_g_panel = new JPanel();
		JLabel blue_g_label = new JLabel("Green:");
		blue_g = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_g_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_g_high()), 10,
				50);
		blue_g_panel.add(blue_g_label);
		blue_g_panel.add(blue_g);
		bluePanel.add(blue_g_panel);

		/* Blue. */
		JPanel blue_b_panel = new JPanel();
		JLabel blue_b_label = new JLabel("Blue:");
		blue_b = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.BLUE).get_b_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.BLUE)
				.get_b_high(), 10, 50);
		blue_b_panel.add(blue_b_label);
		blue_b_panel.add(blue_b);
		bluePanel.add(blue_b_panel);

		/* Hue. */
		JPanel blue_h_panel = new JPanel();
		JLabel blue_h_label = new JLabel("Hue:");
		blue_h = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_h_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_h_high()), 10,
				50);
		blue_h_panel.add(blue_h_label);
		blue_h_panel.add(blue_h);
		bluePanel.add(blue_h_panel);

		/* Sat. */
		JPanel blue_s_panel = new JPanel();
		JLabel blue_s_label = new JLabel("Sat:");
		blue_s = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_s_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_s_high()), 10,
				50);
		blue_s_panel.add(blue_s_label);
		blue_s_panel.add(blue_s);
		bluePanel.add(blue_s_panel);

		/* Value. */
		JPanel blue_v_panel = new JPanel();
		JLabel blue_v_label = new JLabel("Value:");
		blue_v = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_v_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_v_high()), 10,
				50);
		blue_v_panel.add(blue_v_label);
		blue_v_panel.add(blue_v);
		bluePanel.add(blue_v_panel);

		/* Red - Green. */
		JPanel blue_rg_panel = new JPanel();
		JLabel blue_rg_label = new JLabel("Red - Green:");
		blue_rg = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_rg_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_rg_high(), 20,
				100);
		blue_rg_panel.add(blue_rg_label);
		blue_rg_panel.add(blue_rg);
		bluePanel.add(blue_rg_panel);

		/* Red - Blue. */
		JPanel blue_rb_panel = new JPanel();
		JLabel blue_rb_label = new JLabel("Red - Blue");
		blue_rb = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_rg_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_rg_high(), 20,
				100);
		blue_rb_panel.add(blue_rb_label);
		blue_rb_panel.add(blue_rb);
		bluePanel.add(blue_rb_panel);

		/* Green - Blue */
		JPanel blue_gb_panel = new JPanel();
		JLabel blue_gb_label = new JLabel("Green - Blue");
		blue_gb = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_gb_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.BLUE).get_gb_high(), 20,
				100);
		blue_gb_panel.add(blue_gb_label);
		blue_gb_panel.add(blue_gb);
		bluePanel.add(blue_gb_panel);

		blue_r.addChangeListener(this);
		blue_g.addChangeListener(this);
		blue_b.addChangeListener(this);
		blue_h.addChangeListener(this);
		blue_s.addChangeListener(this);
		blue_v.addChangeListener(this);
		blue_rg.addChangeListener(this);
		blue_rb.addChangeListener(this);
		blue_gb.addChangeListener(this);
	}

	/**
	 * Sets up the sliders for the thresholding of the yellow robot.
	 */
	private void setUpYellowSliders() {

		/* Red. */
		JPanel yellow_r_panel = new JPanel();
		JLabel yellow_r_label = new JLabel("Red:");
		yellow_r = setUpSlider(
				0,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_r_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_r_high(), 10,
				50);
		yellow_r_panel.add(yellow_r_label);
		yellow_r_panel.add(yellow_r);
		yellowPanel.add(yellow_r_panel);

		/* Green. */
		JPanel yellow_g_panel = new JPanel();
		JLabel yellow_g_label = new JLabel("Green:");
		yellow_g = setUpSlider(
				0,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_g_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_g_high(), 10,
				50);
		yellow_g_panel.add(yellow_g_label);
		yellow_g_panel.add(yellow_g);
		yellowPanel.add(yellow_g_panel);

		/* Blue. */
		JPanel yellow_b_panel = new JPanel();
		JLabel yellow_b_label = new JLabel("Blue:");
		yellow_b = setUpSlider(
				0,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_b_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_b_high(), 10,
				50);
		yellow_b_panel.add(yellow_b_label);
		yellow_b_panel.add(yellow_b);
		yellowPanel.add(yellow_b_panel);

		/* Hue. */
		JPanel yellow_h_panel = new JPanel();
		JLabel yellow_h_label = new JLabel("Hue:");
		yellow_h = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_h_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_h_high()), 10,
				50);
		yellow_h_panel.add(yellow_h_label);
		yellow_h_panel.add(yellow_h);
		yellowPanel.add(yellow_h_panel);

		/* Sat. */
		JPanel yellow_s_panel = new JPanel();
		JLabel yellow_s_label = new JLabel("Sat:");
		yellow_s = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_s_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_s_high()), 10,
				50);
		yellow_s_panel.add(yellow_s_label);
		yellow_s_panel.add(yellow_s);
		yellowPanel.add(yellow_s_panel);

		/* Value. */
		JPanel yellow_v_panel = new JPanel();
		JLabel yellow_v_label = new JLabel("Value:");
		yellow_v = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_v_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.YELLOW).get_v_high()), 10,
				50);
		yellow_v_panel.add(yellow_v_label);
		yellow_v_panel.add(yellow_v);
		yellowPanel.add(yellow_v_panel);

		/* Red - Green. */
		JPanel yellow_rg_panel = new JPanel();
		JLabel yellow_rg_label = new JLabel("Red - Green:");
		yellow_rg = setUpSlider(-255, 255, thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.YELLOW)
				.get_rg_low(), thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.YELLOW).get_rg_high(), 20, 100);
		yellow_rg_panel.add(yellow_rg_label);
		yellow_rg_panel.add(yellow_rg);
		yellowPanel.add(yellow_rg_panel);

		/* Red - Blue. */
		JPanel yellow_rb_panel = new JPanel();
		JLabel yellow_rb_label = new JLabel("Red - Blue");
		yellow_rb = setUpSlider(-255, 255, thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.YELLOW)
				.get_rb_low(), thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.YELLOW).get_rb_high(), 20, 100);
		yellow_rb_panel.add(yellow_rb_label);
		yellow_rb_panel.add(yellow_rb);
		yellowPanel.add(yellow_rb_panel);

		/* Green - Blue */
		JPanel yellow_gb_panel = new JPanel();
		JLabel yellow_gb_label = new JLabel("Green - Blue");
		yellow_gb = setUpSlider(-255, 255, thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.YELLOW)
				.get_gb_low(), thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.YELLOW).get_gb_high(), 20, 100);
		yellow_gb_panel.add(yellow_gb_label);
		yellow_gb_panel.add(yellow_gb);
		yellowPanel.add(yellow_gb_panel);

		yellow_r.addChangeListener(this);
		yellow_g.addChangeListener(this);
		yellow_b.addChangeListener(this);
		yellow_h.addChangeListener(this);
		yellow_s.addChangeListener(this);
		yellow_v.addChangeListener(this);
		yellow_rg.addChangeListener(this);
		yellow_rb.addChangeListener(this);
		yellow_gb.addChangeListener(this);

	}

	/**
	 * Sets up the sliders for the thresholding of the grey robot.
	 */
	private void setUpGreySliders() {

		/* Red. */
		JPanel grey_r_panel = new JPanel();
		JLabel grey_r_label = new JLabel("Red:");
		grey_r = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GRAY).get_r_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.GRAY)
				.get_r_high(), 10, 50);
		grey_r_panel.add(grey_r_label);
		grey_r_panel.add(grey_r);
		greyPanel.add(grey_r_panel);

		/* Green. */
		JPanel grey_g_panel = new JPanel();
		JLabel grey_g_label = new JLabel("Green:");
		grey_g = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GRAY).get_g_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.GRAY)
				.get_g_high(), 10, 50);
		grey_g_panel.add(grey_g_label);
		grey_g_panel.add(grey_g);
		greyPanel.add(grey_g_panel);

		/* Blue. */
		JPanel grey_b_panel = new JPanel();
		JLabel grey_b_label = new JLabel("Blue:");
		grey_b = setUpSlider(0, 255, thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GRAY).get_b_low(), thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.GRAY)
				.get_b_high(), 10, 50);
		grey_b_panel.add(grey_b_label);
		grey_b_panel.add(grey_b);
		greyPanel.add(grey_b_panel);

		/* Hue. */
		JPanel grey_h_panel = new JPanel();
		JLabel grey_h_label = new JLabel("Hue:");
		grey_h = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_h_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_h_high()), 10,
				50);
		grey_h_panel.add(grey_h_label);
		grey_h_panel.add(grey_h);
		greyPanel.add(grey_h_panel);

		/* Sat. */
		JPanel grey_s_panel = new JPanel();
		JLabel grey_s_label = new JLabel("Sat:");
		grey_s = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_s_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_s_high()), 10,
				50);
		grey_s_panel.add(grey_s_label);
		grey_s_panel.add(grey_s);
		greyPanel.add(grey_s_panel);

		/* Value. */
		JPanel grey_v_panel = new JPanel();
		JLabel grey_v_label = new JLabel("Value:");
		grey_v = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_v_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_v_high()), 10,
				50);
		grey_v_panel.add(grey_v_label);
		grey_v_panel.add(grey_v);
		greyPanel.add(grey_v_panel);

		/* Red Green. */
		JPanel grey_rg_panel = new JPanel();
		JLabel grey_rg_label = new JLabel("Red - Green:");
		grey_rg = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_rg_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_rg_high(), 20,
				100);
		grey_rg_panel.add(grey_rg_label);
		grey_rg_panel.add(grey_rg);
		greyPanel.add(grey_rg_panel);

		/* Red Blue. */
		JPanel grey_rb_panel = new JPanel();
		JLabel grey_rb_label = new JLabel("Red - Blue");
		grey_rb = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_rb_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_rb_high(), 20,
				100);
		grey_rb_panel.add(grey_rb_label);
		grey_rb_panel.add(grey_rb);
		greyPanel.add(grey_rb_panel);

		/* Green Blue */
		JPanel grey_gb_panel = new JPanel();
		JLabel grey_gb_label = new JLabel("Green - Blue");
		grey_gb = setUpSlider(
				-255,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_gb_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GRAY).get_gb_high(), 20,
				100);
		grey_gb_panel.add(grey_gb_label);
		grey_gb_panel.add(grey_gb);
		greyPanel.add(grey_gb_panel);

		grey_r.addChangeListener(this);
		grey_g.addChangeListener(this);
		grey_b.addChangeListener(this);
		grey_h.addChangeListener(this);
		grey_s.addChangeListener(this);
		grey_v.addChangeListener(this);
		grey_rg.addChangeListener(this);
		grey_rb.addChangeListener(this);
		grey_gb.addChangeListener(this);
	}

	/**
	 * Sets up the sliders for the thresholding of the green robot.
	 */
	private void setUpGreenSliders() {

		/* Red. */
		JPanel green_r_panel = new JPanel();
		JLabel green_r_label = new JLabel("Red:");
		green_r = setUpSlider(
				0,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_r_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_r_high(), 10,
				50);
		green_r_panel.add(green_r_label);
		green_r_panel.add(green_r);
		greenPanel.add(green_r_panel);

		/* Green. */
		JPanel green_g_panel = new JPanel();
		JLabel green_g_label = new JLabel("Green:");
		green_g = setUpSlider(
				0,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_g_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_g_high(), 10,
				50);
		green_g_panel.add(green_g_label);
		green_g_panel.add(green_g);
		greenPanel.add(green_g_panel);

		/* Blue. */
		JPanel green_b_panel = new JPanel();
		JLabel green_b_label = new JLabel("Blue:");
		green_b = setUpSlider(
				0,
				255,
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_b_low(),
				thresholdsState.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_b_high(), 10,
				50);
		green_b_panel.add(green_b_label);
		green_b_panel.add(green_b);
		greenPanel.add(green_b_panel);

		/* Hue. */
		JPanel green_h_panel = new JPanel();
		JLabel green_h_label = new JLabel("Hue:");
		green_h = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_h_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_h_high()), 10,
				50);
		green_h_panel.add(green_h_label);
		green_h_panel.add(green_h);
		greenPanel.add(green_h_panel);

		/* Sat. */
		JPanel green_s_panel = new JPanel();
		JLabel green_s_label = new JLabel("Sat:");
		green_s = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_s_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_s_high()), 10,
				50);
		green_s_panel.add(green_s_label);
		green_s_panel.add(green_s);
		greenPanel.add(green_s_panel);

		/* Value. */
		JPanel green_v_panel = new JPanel();
		JLabel green_v_label = new JLabel("Value:");
		green_v = setUpSlider(
				0,
				255,
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_v_low()),
				thresholdsState.ScaleTo255(thresholdsState
						.getQuadrantThresholds(q)
						.getObjectThresholds(Colours.GREEN).get_v_high()), 10,
				50);
		green_v_panel.add(green_v_label);
		green_v_panel.add(green_v);
		greenPanel.add(green_v_panel);

		/* Red Green. */
		JPanel green_rg_panel = new JPanel();
		JLabel green_rg_label = new JLabel("Red - Green:");
		green_rg = setUpSlider(-255, 255, thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.GREEN)
				.get_rg_low(), thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GREEN).get_rg_high(), 20, 100);
		green_rg_panel.add(green_rg_label);
		green_rg_panel.add(green_rg);
		greenPanel.add(green_rg_panel);

		/* Red Blue. */
		JPanel green_rb_panel = new JPanel();
		JLabel green_rb_label = new JLabel("Red - Blue");
		green_rb = setUpSlider(-255, 255, thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.GREEN)
				.get_rb_low(), thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GREEN).get_rb_high(), 20, 100);
		green_rb_panel.add(green_rb_label);
		green_rb_panel.add(green_rb);
		greenPanel.add(green_rb_panel);

		/* Green Blue */
		JPanel green_gb_panel = new JPanel();
		JLabel green_gb_label = new JLabel("Green - Blue");
		green_gb = setUpSlider(-255, 255, thresholdsState
				.getQuadrantThresholds(q).getObjectThresholds(Colours.GREEN)
				.get_gb_low(), thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GREEN).get_gb_high(), 20, 100);
		green_gb_panel.add(green_gb_label);
		green_gb_panel.add(green_gb);
		greenPanel.add(green_gb_panel);

		green_r.addChangeListener(this);
		green_g.addChangeListener(this);
		green_b.addChangeListener(this);
		green_h.addChangeListener(this);
		green_s.addChangeListener(this);
		green_v.addChangeListener(this);
		green_rg.addChangeListener(this);
		green_rb.addChangeListener(this);
		green_gb.addChangeListener(this);

	}

	public void setUpQuadrantSliders() {
		/* Quadrant1 */
		QuadrantThresholdsState quadrant1 = thresholdsState.getQuadrantThresholds(Quadrant.Q1);
		JPanel q1_panel = new JPanel();
		JLabel q1_label = new JLabel("q1:");
		q1 = setUpSlider(0, 640, quadrant1.getLowX(), quadrant1.getHighX(),
				20, 100);
		q1_panel.add(q1_label);
		q1_panel.add(q1);
		quadrantPanel.add(q1_panel);
		q1.addChangeListener(this);
		

		/* Quadrant2 */
		QuadrantThresholdsState quadrant2 = thresholdsState.getQuadrantThresholds(Quadrant.Q2);
		JPanel q2_panel = new JPanel();
		JLabel q2_label = new JLabel("q2:");
		q2 = setUpSlider(0, 640, quadrant2.getLowX(), quadrant2.getHighX(),
				20, 100);
		q2_panel.add(q2_label);
		q2_panel.add(q2);
		quadrantPanel.add(q2_panel);
		q2.addChangeListener(this);


		/* Quadrant3 */
		QuadrantThresholdsState quadrant3 = thresholdsState.getQuadrantThresholds(Quadrant.Q3);
		JPanel q3_panel = new JPanel();
		JLabel q3_label = new JLabel("q3:");
		q3 = setUpSlider(0, 640, quadrant3.getLowX(), quadrant3.getHighX(),
				20, 100);
		q3_panel.add(q3_label);
		q3_panel.add(q3);
		quadrantPanel.add(q3_panel);
		q3.addChangeListener(this);


		/* Quadrant4 */
		QuadrantThresholdsState quadrant4 = thresholdsState.getQuadrantThresholds(Quadrant.Q4);
		JPanel q4_panel = new JPanel();
		JLabel q4_label = new JLabel("q4:");
		q4 = setUpSlider(0, 640, quadrant4.getLowX(), quadrant4.getHighX(),
				20, 100);
		q4_panel.add(q4_label);
		q4_panel.add(q4);
		quadrantPanel.add(q4_panel);
		q4.addChangeListener(this);

	}

	/**
	 * Creates and returns a new RangeSlider from a number of parameters.
	 * 
	 * @param minVal
	 *            The minimum value the slider can have.
	 * @param maxVal
	 *            The maximum value the slider can have.
	 * @param lowerVal
	 *            The initial value for the lower end of the range.
	 * @param upperVal
	 *            The initial value for the higher end of the range.
	 * @param minorTick
	 *            The minor tick distance.
	 * @param majorTick
	 *            The major tick distance.
	 * 
	 * @return A new RangeSlider created from the given parameters.
	 */
	private RangeSlider setUpSlider(int minVal, int maxVal, int lowerVal,
			int upperVal, int minorTick, int majorTick) {

		RangeSlider slider = new RangeSlider(minVal, maxVal);

		setSliderVals(slider, lowerVal, upperVal);

		slider.setMinorTickSpacing(minorTick);
		slider.setMajorTickSpacing(majorTick);

		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		return slider;

	}

	private class tabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * A Change listener for various components on the GUI. When a component is
	 * changed all information is updated.
	 * 
	 * @param e
	 *            The event that was created for the change.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// Ignore this state change call if we're currently updating
		// the values ourselves. Otherwise our first change will trigger
		// this method, save all the values and override the rest of the
		// updates we're making
		if (updatingValues) {
			return;
		}

		if (tabPane.getSelectedComponent() == defaultPanel) {
			worldState.setFindRobotsAndBall(true);
		} else {
			worldState.setFindRobotsAndBall(false);
		}

		/* Update the world state. */
		if (pitch_0.isSelected()) {
			worldState.setPitch(0);
		} else {
			worldState.setPitch(1);
		}

		if (colour_yellow.isSelected()) {
			worldState.setColour(RobotColour.YELLOW);
		} else {
			worldState.setColour(RobotColour.BLUE);
		}
		if (direction_right.isSelected()) {
			worldState.setDirection(ShootingDirection.RIGHT);
		} else {
			worldState.setDirection(ShootingDirection.LEFT);
		}

		/* Update the ThresholdsState object. */

		int index = tabPane.getSelectedIndex();
		switch (index) {
		case (0):
			thresholdsState.setDebug(Colours.RED, false);
			thresholdsState.setDebug(Colours.BLUE, false);
			thresholdsState.setDebug(Colours.YELLOW, false);
			thresholdsState.setDebug(Colours.GRAY, false);
			thresholdsState.setDebug(Colours.GREEN, false);
			break;
		case (1):
			thresholdsState.setDebug(Colours.RED, true);
			thresholdsState.setDebug(Colours.BLUE, false);
			thresholdsState.setDebug(Colours.YELLOW, false);
			thresholdsState.setDebug(Colours.GRAY, false);
			thresholdsState.setDebug(Colours.GREEN, false);
			break;
		case (2):
			thresholdsState.setDebug(Colours.RED, false);
			thresholdsState.setDebug(Colours.BLUE, true);
			thresholdsState.setDebug(Colours.YELLOW, false);
			thresholdsState.setDebug(Colours.GRAY, false);
			thresholdsState.setDebug(Colours.GREEN, false);
			break;
		case (3):
			thresholdsState.setDebug(Colours.RED, false);
			thresholdsState.setDebug(Colours.BLUE, false);
			thresholdsState.setDebug(Colours.YELLOW, true);
			thresholdsState.setDebug(Colours.GRAY, false);
			thresholdsState.setDebug(Colours.GREEN, false);
			break;
		case (4):
			thresholdsState.setDebug(Colours.RED, false);
			thresholdsState.setDebug(Colours.BLUE, false);
			thresholdsState.setDebug(Colours.YELLOW, false);
			thresholdsState.setDebug(Colours.GRAY, true);
			thresholdsState.setDebug(Colours.GREEN, false);
			break;
		case (5):
			thresholdsState.setDebug(Colours.RED, false);
			thresholdsState.setDebug(Colours.BLUE, false);
			thresholdsState.setDebug(Colours.YELLOW, false);
			thresholdsState.setDebug(Colours.GRAY, false);
			thresholdsState.setDebug(Colours.GREEN, true);
			break;
		default:
			thresholdsState.setDebug(Colours.RED, false);
			thresholdsState.setDebug(Colours.BLUE, false);
			thresholdsState.setDebug(Colours.YELLOW, false);
			thresholdsState.setDebug(Colours.GRAY, false);
			thresholdsState.setDebug(Colours.GREEN, false);
			break;
		}

		QuadrantThresholdsState quadrantThresholds = thresholdsState
				.getQuadrantThresholds(q);

		/* Ball. */
		ObjectThresholdState ball = quadrantThresholds
				.getObjectThresholds(Colours.RED);

		ball.set_r_low(ball_r.getValue());
		ball.set_r_high(ball_r.getUpperValue());

		ball.set_g_low(ball_g.getValue());
		ball.set_g_high(ball_g.getUpperValue());

		ball.set_b_low(ball_b.getValue());
		ball.set_b_high(ball_b.getUpperValue());

		ball.set_h_low(ball_h.getValue() / 255.0);
		ball.set_h_high(ball_h.getUpperValue() / 255.0);

		ball.set_s_low(ball_s.getValue() / 255.0);
		ball.set_s_high(ball_s.getUpperValue() / 255.0);

		ball.set_v_low(ball_v.getValue() / 255.0);
		ball.set_v_high(ball_v.getUpperValue() / 255.0);

		ball.set_rg_low(ball_rg.getValue());
		ball.set_rg_high(ball_rg.getUpperValue());

		ball.set_rb_low(ball_rb.getValue());
		ball.set_rb_high(ball_rb.getUpperValue());

		ball.set_gb_low(ball_gb.getValue());
		ball.set_gb_high(ball_gb.getUpperValue());

		/* Blue Robot. */

		ObjectThresholdState blue = quadrantThresholds
				.getObjectThresholds(Colours.BLUE);

		blue.set_r_low(blue_r.getValue());
		blue.set_r_high(blue_r.getUpperValue());

		blue.set_g_low(blue_g.getValue());
		blue.set_g_high(blue_g.getUpperValue());

		blue.set_b_low(blue_b.getValue());
		blue.set_b_high(blue_b.getUpperValue());

		blue.set_h_low(blue_h.getValue() / 255.0);
		blue.set_h_high(blue_h.getUpperValue() / 255.0);

		blue.set_s_low(blue_s.getValue() / 255.0);
		blue.set_s_high(blue_s.getUpperValue() / 255.0);

		blue.set_v_low(blue_v.getValue() / 255.0);
		blue.set_v_high(blue_v.getUpperValue() / 255.0);

		blue.set_rg_low(blue_rg.getValue());
		blue.set_rg_high(blue_rg.getUpperValue());

		blue.set_rb_low(blue_rb.getValue());
		blue.set_rb_high(blue_rb.getUpperValue());

		blue.set_gb_low(blue_gb.getValue());
		blue.set_gb_high(blue_gb.getUpperValue());

		/* Yellow Robot. */
		ObjectThresholdState yellow = quadrantThresholds
				.getObjectThresholds(Colours.YELLOW);

		yellow.set_r_low(yellow_r.getValue());
		yellow.set_r_high(yellow_r.getUpperValue());

		yellow.set_g_low(yellow_g.getValue());
		yellow.set_g_high(yellow_g.getUpperValue());

		yellow.set_b_low(yellow_b.getValue());
		yellow.set_b_high(yellow_b.getUpperValue());

		yellow.set_h_low(yellow_h.getValue() / 255.0);
		yellow.set_h_high(yellow_h.getUpperValue() / 255.0);

		yellow.set_s_low(yellow_s.getValue() / 255.0);
		yellow.set_s_high(yellow_s.getUpperValue() / 255.0);

		yellow.set_v_low(yellow_v.getValue() / 255.0);
		yellow.set_v_high(yellow_v.getUpperValue() / 255.0);

		yellow.set_rg_low(yellow_rg.getValue());
		yellow.set_rg_high(yellow_rg.getUpperValue());

		yellow.set_rb_low(yellow_rb.getValue());
		yellow.set_rb_high(yellow_rb.getUpperValue());

		yellow.set_gb_low(yellow_gb.getValue());
		yellow.set_gb_high(yellow_gb.getUpperValue());

		/* Grey Circles. */

		ObjectThresholdState grey = quadrantThresholds
				.getObjectThresholds(Colours.GRAY);

		grey.set_r_low(grey_r.getValue());
		grey.set_r_high(grey_r.getUpperValue());

		grey.set_g_low(grey_g.getValue());
		grey.set_g_high(grey_g.getUpperValue());

		grey.set_b_low(grey_b.getValue());
		grey.set_b_high(grey_b.getUpperValue());

		grey.set_h_low(grey_h.getValue() / 255.0);
		grey.set_h_high(grey_h.getUpperValue() / 255.0);

		grey.set_s_low(grey_s.getValue() / 255.0);
		grey.set_s_high(grey_s.getUpperValue() / 255.0);

		grey.set_v_low(grey_v.getValue() / 255.0);
		grey.set_v_high(grey_v.getUpperValue() / 255.0);

		grey.set_rg_low(grey_rg.getValue());
		grey.set_rg_high(grey_rg.getUpperValue());

		grey.set_rb_low(grey_rb.getValue());
		grey.set_rb_high(grey_rb.getUpperValue());

		grey.set_gb_low(grey_gb.getValue());
		grey.set_gb_high(grey_gb.getUpperValue());

		/* Green Circles. */

		ObjectThresholdState green = quadrantThresholds
				.getObjectThresholds(Colours.GREEN);

		green.set_r_low(green_r.getValue());
		green.set_r_high(green_r.getUpperValue());

		green.set_g_low(green_g.getValue());
		green.set_g_high(green_g.getUpperValue());

		green.set_b_low(green_b.getValue());
		green.set_b_high(green_b.getUpperValue());

		green.set_h_low(green_h.getValue() / 255.0);
		green.set_h_high(green_h.getUpperValue() / 255.0);

		green.set_s_low(green_s.getValue() / 255.0);
		green.set_s_high(green_s.getUpperValue() / 255.0);

		green.set_v_low(green_v.getValue() / 255.0);
		green.set_v_high(green_v.getUpperValue() / 255.0);

		green.set_rg_low(green_rg.getValue());
		green.set_rg_high(green_rg.getUpperValue());

		green.set_rb_low(green_rb.getValue());
		green.set_rb_high(green_rb.getUpperValue());

		green.set_gb_low(green_gb.getValue());
		green.set_gb_high(green_gb.getUpperValue());
		
		QuadrantThresholdsState quadrant1 = thresholdsState.getQuadrantThresholds(Quadrant.Q1);
		QuadrantThresholdsState quadrant2 = thresholdsState.getQuadrantThresholds(Quadrant.Q2);
		QuadrantThresholdsState quadrant3 = thresholdsState.getQuadrantThresholds(Quadrant.Q3);
		QuadrantThresholdsState quadrant4 = thresholdsState.getQuadrantThresholds(Quadrant.Q4);

		quadrant1.setLowX(q1.getValue());
		quadrant1.setHighX(q1.getUpperValue());
		quadrant2.setLowX(q2.getValue());
		quadrant2.setHighX(q2.getUpperValue());
		quadrant3.setLowX(q3.getValue());
		quadrant3.setHighX(q3.getUpperValue());
		quadrant4.setLowX(q4.getValue());
		quadrant4.setHighX(q4.getUpperValue());
		
		//This is annoying:
		worldState.setQ1LowX(q1.getValue());
		worldState.setQ1HighX(q1.getUpperValue());
		worldState.setQ2LowX(q2.getValue());
		worldState.setQ2HighX(q2.getUpperValue());
		worldState.setQ3LowX(q3.getValue());
		worldState.setQ3HighX(q3.getUpperValue());
		worldState.setQ4LowX(q4.getValue());
		worldState.setQ4HighX(q4.getUpperValue());
		// Reloads the slider values from ThresholdState
		reloadSliderDefaults();
	}

	private boolean updatingValues = false;

	/**
	 * Reloads the default values for the sliders from the PitchConstants file.
	 */
	public void reloadSliderDefaults() {
		// Set the global flag to notify that nobody else should be
		// messing with the slider values while we're updating them
		updatingValues = true;

		ObjectThresholdState ball = thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.RED);

		/* Ball slider */
		setSliderVals(ball_r, ball.get_r_low(), ball.get_r_high());
		setSliderVals(ball_g, ball.get_g_low(), ball.get_g_high());
		setSliderVals(ball_b, ball.get_b_low(), ball.get_b_high());
		setSliderVals(ball_h, thresholdsState.ScaleTo255(ball.get_h_low()),
				thresholdsState.ScaleTo255(ball.get_h_high()));
		setSliderVals(ball_s, thresholdsState.ScaleTo255(ball.get_s_low()),
				thresholdsState.ScaleTo255(ball.get_s_high()));
		setSliderVals(ball_v, thresholdsState.ScaleTo255(ball.get_v_low()),
				thresholdsState.ScaleTo255(ball.get_v_high()));
		setSliderVals(ball_rg, ball.get_rg_low(), ball.get_rg_high());
		setSliderVals(ball_rb, ball.get_rb_low(), ball.get_rb_high());
		setSliderVals(ball_gb, ball.get_gb_low(), ball.get_gb_high());

		ObjectThresholdState blue = thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.BLUE);

		/* Blue slider */
		setSliderVals(blue_r, blue.get_r_low(), blue.get_r_high());
		setSliderVals(blue_g, blue.get_g_low(), blue.get_g_high());
		setSliderVals(blue_b, blue.get_b_low(), blue.get_b_high());
		setSliderVals(blue_h, thresholdsState.ScaleTo255(blue.get_h_low()),
				thresholdsState.ScaleTo255(blue.get_h_high()));
		setSliderVals(blue_s, thresholdsState.ScaleTo255(blue.get_s_low()),
				thresholdsState.ScaleTo255(blue.get_s_high()));
		setSliderVals(blue_v, thresholdsState.ScaleTo255(blue.get_v_low()),
				thresholdsState.ScaleTo255(blue.get_v_high()));
		setSliderVals(blue_rg, blue.get_rg_low(), blue.get_rg_high());
		setSliderVals(blue_rb, blue.get_rb_low(), blue.get_rb_high());
		setSliderVals(blue_gb, blue.get_gb_low(), blue.get_gb_high());

		/* Yellow slider */

		ObjectThresholdState yellow = thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.YELLOW);

		setSliderVals(yellow_r, yellow.get_r_low(), yellow.get_r_high());
		setSliderVals(yellow_g, yellow.get_g_low(), yellow.get_g_high());
		setSliderVals(yellow_b, yellow.get_b_low(), yellow.get_b_high());
		setSliderVals(yellow_h, thresholdsState.ScaleTo255(yellow.get_h_low()),
				thresholdsState.ScaleTo255(yellow.get_h_high()));
		setSliderVals(yellow_s, thresholdsState.ScaleTo255(yellow.get_s_low()),
				thresholdsState.ScaleTo255(yellow.get_s_high()));
		setSliderVals(yellow_v, thresholdsState.ScaleTo255(yellow.get_v_low()),
				thresholdsState.ScaleTo255(yellow.get_v_high()));
		setSliderVals(yellow_rg, yellow.get_rg_low(), yellow.get_rg_high());
		setSliderVals(yellow_rb, yellow.get_rb_low(), yellow.get_rb_high());
		setSliderVals(yellow_gb, yellow.get_gb_low(), yellow.get_gb_high());

		/* Grey slider */

		ObjectThresholdState grey = thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GRAY);

		setSliderVals(grey_r, grey.get_r_low(), grey.get_r_high());
		setSliderVals(grey_g, grey.get_g_low(), grey.get_g_high());
		setSliderVals(grey_b, grey.get_b_low(), grey.get_b_high());
		setSliderVals(grey_h, thresholdsState.ScaleTo255(grey.get_h_low()),
				thresholdsState.ScaleTo255(grey.get_h_high()));
		setSliderVals(grey_s, thresholdsState.ScaleTo255(grey.get_s_low()),
				thresholdsState.ScaleTo255(grey.get_s_high()));
		setSliderVals(grey_v, thresholdsState.ScaleTo255(grey.get_v_low()),
				thresholdsState.ScaleTo255(grey.get_v_high()));
		setSliderVals(grey_rg, grey.get_rg_low(), grey.get_rg_high());
		setSliderVals(grey_rb, grey.get_rb_low(), grey.get_rb_high());
		setSliderVals(grey_gb, grey.get_gb_low(), grey.get_gb_high());

		/* Green slider */
		ObjectThresholdState green = thresholdsState.getQuadrantThresholds(q)
				.getObjectThresholds(Colours.GREEN);

		setSliderVals(green_r, green.get_r_low(), green.get_r_high());
		setSliderVals(green_g, green.get_g_low(), green.get_g_high());
		setSliderVals(green_b, green.get_b_low(), green.get_b_high());
		setSliderVals(green_h, thresholdsState.ScaleTo255(green.get_h_low()),
				thresholdsState.ScaleTo255(green.get_h_high()));
		setSliderVals(green_s, thresholdsState.ScaleTo255(green.get_s_low()),
				thresholdsState.ScaleTo255(green.get_s_high()));
		setSliderVals(green_v, thresholdsState.ScaleTo255(green.get_v_low()),
				thresholdsState.ScaleTo255(green.get_v_high()));
		setSliderVals(green_rg, green.get_rg_low(), green.get_rg_high());
		setSliderVals(green_rb, green.get_rb_low(), green.get_rb_high());
		setSliderVals(green_gb, green.get_gb_low(), green.get_gb_high());

		/* Quadrant slider */
		//TODO
		QuadrantThresholdsState quadrant1 = thresholdsState.getQuadrantThresholds(Quadrant.Q1);
		QuadrantThresholdsState quadrant2 = thresholdsState.getQuadrantThresholds(Quadrant.Q2);
		QuadrantThresholdsState quadrant3 = thresholdsState.getQuadrantThresholds(Quadrant.Q3);
		QuadrantThresholdsState quadrant4 = thresholdsState.getQuadrantThresholds(Quadrant.Q4);
		
		//		setSliderVals(q1, 0, 50);
		//		setSliderVals(q2, 0, 50);
		//		setSliderVals(q3, 0, 50);
		//		setSliderVals(q4, 0, 50);
		
		setSliderVals(q1, quadrant1.getLowX(), quadrant1.getHighX());
		setSliderVals(q2, quadrant2.getLowX(), quadrant2.getHighX());
		setSliderVals(q3, quadrant3.getLowX(), quadrant3.getHighX());
		setSliderVals(q4, quadrant4.getLowX(), quadrant4.getHighX());

		// Done updating, other methods can do whatever they want now
		updatingValues = false;
	}

	/**
	 * Set the the values of a range slider.
	 * 
	 * @param rangeSlider
	 *            The range slider to set the values for.
	 * @param low
	 *            The lower end of the range.
	 * @param high
	 *            The higher end of the range.
	 */
	private void setSliderVals(RangeSlider rangeSlider, int low, int high) {
		/*
		 * If try to set lower val setSliderVals(q1, pitchConstants.q1_low,
		 * pitchConstants.q1_high);> current higher val nothing will happen (and
		 * vice-versa), so we set the lower val twice in case of this situation.
		 */
		rangeSlider.setValue(low);
		rangeSlider.setUpperValue(high);
		rangeSlider.setValue(low);
	}

}
