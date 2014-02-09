package sdp.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import sdp.common.RotationDirection;
import sdp.communication.CommsClient;
import sdp.milestone.Milestone3GoToBall;
import sdp.milestone.Milestone3Score;
import sdp.milestone.Milestone4Intercept;
import sdp.milestone.Milestone4SimpleIntercept;
import sdp.strategy.CommandHelper;
import sdp.strategy.Strategy;
import sdp.vision.Drawable;
import sdp.vision.RunVision;
import sdp.vision.Vision;
import sdp.vision.WorldState;

/**
 * Control window to run a robot
 * 
 * @author Euan Reid
 */

public class MainWindow {
	private JFrame frmSexyRobotControl;
	static Component buttonSpacer = Box.createVerticalStrut(10);
	private Strategy strategy;
	private Milestone4SimpleIntercept intercept;
	private Vision mVision;
	private WorldState mWorldState;
	private CommsClient mComms;
	private CommandHelper mCommandHelper;

	private static HashMap<String, Collection<Drawable>> mDrawables = new HashMap<String, Collection<Drawable>>();

	public static void addOrUpdateDrawable(String key, Collection<Drawable> drawables){
		mDrawables.put(key, drawables);
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public static HashMap<String, Collection<Drawable>> getDrawables() {
		return mDrawables;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmSexyRobotControl.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() throws IOException {
//		initVision();
		mWorldState = new WorldState();
		initControl();
		//initComms();
		//initStrategy();
	}

	////////////////////////////////////////////////////////////////////////////////
	////	AUTO TAB															////
	////////////////////////////////////////////////////////////////////////////////

	private JPanel autoTab(){
		JPanel auto = new JPanel();
		auto.setLayout(new BoxLayout(auto, BoxLayout.Y_AXIS));

		auto.add(Box.createVerticalGlue());
		auto.add(controlButtons());
		auto.add(Box.createVerticalGlue());

		return auto;
	}

	public JPanel connectButtons(){
		JButton btnConnect = new JButton("Connect");

		btnConnect.addActionListener(new ConnectListener());

		JPanel buttons = new JPanel();
		buttons.setAlignmentX(Component.RIGHT_ALIGNMENT);
		buttons.add(btnConnect);

		return buttons;
	}
	private JPanel controlButtons(){
		// Create buttons
		JButton btnGoStop = new JButton("Go");
		JButton btnTakePenalty = new JButton("Take Penalty");
		JButton btnDefendPenalty = new JButton("Defend Penalty");

		// Set action
		btnGoStop.setActionCommand("go");

		// Add listeners
		btnGoStop.addActionListener(new GoStopButtonListener());
		btnTakePenalty.addActionListener(new TakePenaltyButtonListener(btnGoStop));
		btnDefendPenalty.addActionListener(new DefendPenaltyButtonListener(btnGoStop));

		// Group buttons onto panels
		JPanel goStop = Helper.titledPanel("Main");
		goStop.add(btnGoStop);
		JPanel penalty = Helper.titledPanel("Penalty");
		penalty.add(btnTakePenalty);
		penalty.add(btnDefendPenalty);


		// Create control button panel
		JPanel buttons = new JPanel();
		buttons.add(Box.createHorizontalGlue());
		buttons.add(goStop);
		buttons.add(penalty);
		buttons.add(Box.createHorizontalGlue());

		return buttons;
	}


	////////////////////////////////////////////////////////////////////////////////
	////	BASIC TAB															////
	////////////////////////////////////////////////////////////////////////////////

	private JPanel basicTab(){
		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));

		// Container to vertically centre contents
		JPanel basic_container = new JPanel();
		basic.add(Box.createVerticalGlue());
		basic.add(basic_container);

		// Add buttons
		basic_container.add(rotationButtons());
		basic_container.add(movementButtons());
		basic_container.add(resetButton());
		basic_container.add(kickButton());

		return basic;
	}

	private JPanel rotationButtons(){
		JPanel rotation = Helper.titledPanel("Rotation");
		rotation.setLayout(new GridLayout(0, 3, 0, 0));

		JButton btnAnticlockwise = new JButton("â†¶");
		JButton btnClockwise = new JButton("â†·");
		JButton btnStop = new JButton("â—¾");

		btnAnticlockwise.setActionCommand("â†¶");
		btnClockwise.setActionCommand("â†·");

		btnAnticlockwise.addActionListener(new RotateListener());
		btnClockwise.addActionListener(new RotateListener());
		btnStop.addActionListener(new StopListener());

		rotation.add(btnAnticlockwise);
		rotation.add(btnStop);
		rotation.add(btnClockwise);

		return rotation;
	}
	private JPanel movementButtons(){
		// Movement panel
		JPanel movement = Helper.titledPanel("Movement");
		movement.setLayout(new GridLayout(0, 3, 0, 0));
		movement.setBounds(0, 0, 0, 0);

		JButton btnNW = new JButton("â†–");
		btnNW.setActionCommand("â†–");
		JButton btnN = new JButton("â†‘");
		btnN.setActionCommand("â†‘");
		JButton btnNE = new JButton("â†—");
		btnNE.setActionCommand("â†—");
		JButton btnW = new JButton("â†�");
		btnW.setActionCommand("â†�");
		JButton btnStop = new JButton("â—¾");
		JButton btnE = new JButton("â†’");
		btnE.setActionCommand("â†’");
		JButton btnSW = new JButton("â†™");
		btnSW.setActionCommand("â†™");
		JButton btnS = new JButton("â†“");
		btnS.setActionCommand("â†“");
		JButton btnSE = new JButton("â†˜");
		btnSE.setActionCommand("â†˜");

		btnNW.addActionListener(new MoveListener());
		btnN.addActionListener(new MoveListener());
		btnNE.addActionListener(new MoveListener());
		btnW.addActionListener(new MoveListener());
		btnStop.addActionListener(new StopListener());
		btnE.addActionListener(new MoveListener());
		btnSW.addActionListener(new MoveListener());
		btnS.addActionListener(new MoveListener());
		btnSE.addActionListener(new MoveListener());

		movement.add(btnNW);
		movement.add(btnN);
		movement.add(btnNE);
		movement.add(btnW);
		movement.add(btnStop);
		movement.add(btnE);
		movement.add(btnSW);
		movement.add(btnS);
		movement.add(btnSE);

		return movement;
	}
	private JPanel kickButton(){
		JPanel kick = Helper.titledPanel("Kick");

		JButton btnKick = new JButton("Kick");

		btnKick.addActionListener(new KickListener());

		kick.add(btnKick);

		return kick;
	}
	private JPanel resetButton(){
		JPanel reset = Helper.titledPanel("Reset");

		JButton btnReset = new JButton("Reset");

		btnReset.addActionListener(new ResetButtonListener());

		reset.add(btnReset);

		return reset;
	}

	////////////////////////////////////////////////////////////////////////////////
	////	MILESTONE TAB														////
	////////////////////////////////////////////////////////////////////////////////

	private JPanel milestoneTab(){
		JPanel milestone = new JPanel();
		milestone.setLayout(new BoxLayout(milestone, BoxLayout.X_AXIS));

		JPanel milestone_container = new JPanel();

		milestone_container.add(milestone1Panel());
		milestone_container.add(milestone2Panel());
		milestone_container.add(milestone3Panel());
		milestone_container.add(milestone4Panel());

		milestone.add(Box.createVerticalGlue());
		milestone.add(milestone_container);

		return milestone;
	}

	private JPanel milestone1Panel(){
		JPanel milestone = Helper.milestonePanel(1);

		Helper.addCenterButton(milestone, "Task 1");
		Helper.addCenterButton(milestone, "Task 2");
		Helper.addCenterButton(milestone, "Task 3");

		return milestone;
	}
	private JPanel milestone2Panel(){
		JPanel milestone = Helper.milestonePanel(2);

		Helper.addCenterButton(milestone, "Task 1");
		Helper.addCenterButton(milestone, "Task 2");
		Helper.addCenterButton(milestone, "Task 3");

		return milestone;
	}
	private JPanel milestone3Panel(){
		JPanel milestone = Helper.milestonePanel(3);

		Helper.addCenterButton(milestone, "Task 1", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Milestone3GoToBall(mWorldState, mCommandHelper).start();
			}
		});
		Helper.addCenterButton(milestone, "Task 2", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mComms.setMaximumSpeed(150);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				new Milestone3Score(mCommandHelper, mComms, mWorldState).start();
			}
		});
		Helper.addCenterButton(milestone, "Task 3", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mComms.setMaximumSpeed(150);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				new Milestone3Score(mCommandHelper, mComms, mWorldState).start();
			}
		});



		return milestone;
	}
	private JPanel milestone4Panel(){
		JPanel milestone = Helper.milestonePanel(4);

		Helper.addCenterButton(milestone, "SimpleIntercept", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					initComms();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				intercept = new Milestone4SimpleIntercept(mCommandHelper, mComms, mWorldState);
				intercept.start();
			}
		});
		Helper.addCenterButton(milestone, "Intercept-Loop", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					initComms();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				new Milestone4Intercept(mCommandHelper, mComms, mWorldState).start();
			}
		});
		Helper.addCenterButton(milestone, "Intercept and Score", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					initComms();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				intercept = new Milestone4SimpleIntercept(mCommandHelper, mComms, mWorldState);
				intercept.run();
				while (intercept.isRunning()){
					System.out.println("milestone running");
				}
				//				initStrategy();
				strategy.play();
			}
		});

		JButton btnGo = new JButton("Go-Before-Intercept");

		class GoInterceptListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		}

		btnGo.addActionListener(new GoInterceptListener());
		Helper.addCenterButton(milestone, "go before intercept", new GoInterceptListener());

		return milestone;
	}

	////////////////////////////////////////////////////////////////////////////////
	////	ADVANCED TAB														////
	////////////////////////////////////////////////////////////////////////////////

	private JPanel advancedTab(){
		JPanel advanced = new JPanel();

		advanced.add(resetResetPoint());
		advanced.add(removeShadows());
		//		Helper.addCenterButton(advanced, "Set pitch bounds", new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				SetBounds.setPitchBounds(mVision, mWorldState);
		//			}
		//		});
		//		Helper.addCenterButton(advanced, "Set outer pitch bounds", new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				SetBounds.setOuterPitchBounds(mVision, mWorldState);
		//			}
		//		});
		return advanced;
	}

	private JPanel resetResetPoint() {
		JPanel reset = Helper.titledPanel("Reset Point");

		JButton btnReset = new JButton("Reset");

		//TODO:btnReset.addActionListener(new ResetResetButtonListener());

		reset.add(btnReset);

		return reset;
	}
	private JPanel removeShadows() {
		JPanel holder = Helper.titledPanel("Remove Shadows");
		ButtonGroup group = new ButtonGroup();

		JRadioButton removeShadowsTrue = new JRadioButton("True");
		JRadioButton removeShadowsFalse = new JRadioButton("False");
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(removeShadowsTrue);
		buttonGroup.add(removeShadowsFalse);

		removeShadowsTrue.setActionCommand("true");
		removeShadowsFalse.setActionCommand("false");

		removeShadowsTrue.addActionListener(new RemoveShadowsListener());
		removeShadowsFalse.addActionListener(new RemoveShadowsListener());

		removeShadowsTrue.setSelected(mWorldState.getRemoveShadows());
		removeShadowsFalse.setSelected(!mWorldState.getRemoveShadows());

		group.add(removeShadowsTrue);
		group.add(removeShadowsFalse);

		holder.add(removeShadowsTrue);
		holder.add(removeShadowsFalse);

		return holder;
	}

	////////////////////////////////////////////////////////////////////////////////
	////	INITIALISERS														////
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialise the contents of the frame.
	 */
	private void initControl(){
		frmSexyRobotControl = new JFrame();

		frmSexyRobotControl.setTitle("Sexy Robot Control Centre");
		frmSexyRobotControl.setBounds(100, 100, 450, 300);
		frmSexyRobotControl.setLocation(0,505);
		frmSexyRobotControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSexyRobotControl.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmSexyRobotControl.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		frmSexyRobotControl.setLocation(0,505);

		tabbedPane.addTab("Auto", autoTab());
		tabbedPane.addTab("Milestone", milestoneTab());
		tabbedPane.addTab("Basic", basicTab());
		tabbedPane.addTab("Advanced", advancedTab());

		frmSexyRobotControl.getContentPane().add(Helper.statePane(this), BorderLayout.PAGE_END);

		frmSexyRobotControl.pack();
	}

	/**
	 * Initialise the vision window.
	 */
	private void initVision(){
		mWorldState = new WorldState();
		mVision = RunVision.setupVision(mWorldState);
	}

	/**
	 * Initialise the communications client.
	 * @return
	 * @throws IOException
	 */
	private boolean initComms() throws IOException{
		mComms = new CommsClient();
		if ( mComms.connect() ){
			mComms.setMaximumSpeed(255);
			return true;
		}
		return false;
	}

	/**
	 * Initialise the command helper and strategy thread.
	 */
	private void initStrategy(){
		mCommandHelper = new CommandHelper(mVision, mComms, mWorldState);

		strategy = new Strategy(mVision.getImageProcessor(), mCommandHelper, mComms, mWorldState);
		strategy.start();
	}

	////////////////////////////////////////////////////////////////////////////////
	////	ACTION LISTENERS													////
	////////////////////////////////////////////////////////////////////////////////

	// Connect button listeners
	class ConnectListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				if ( initComms() ){
					initStrategy();
					((JButton) e.getSource()).setEnabled(false);
				}
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	// Auto command button listeners
	/*	class GoButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Boolean running=false;
			if (strategy!=null) {
				running = strategy.isAlive();
			}
			if (running){
				System.out.println("starting");
				strategy.play();
			}
		}
	}
	class StopButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Boolean running=false;
			if (strategy!=null) {
				running = strategy.isAlive();
			}
			if (running){
				System.out.println("stopping");
				strategy.standBy();
			}
		}
	}*/
	class GoStopButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean running = false;
			if (strategy != null) {
				running = strategy.isAlive();
			}
			if (running){
				if ("go" == e.getActionCommand()) {
					System.out.println("Starting playing");
					((JButton) e.getSource()).setText("Stop");
					((JButton) e.getSource()).setActionCommand("stop");
					strategy.play();
				} else if ("stop" == e.getActionCommand()) {
					System.out.println("Stopping playing");
					((JButton) e.getSource()).setText("Go");
					((JButton) e.getSource()).setActionCommand("go");
					strategy.standBy();
				}
			}
		}
	}
	class TakePenaltyButtonListener implements ActionListener {
		JButton button;
		
		TakePenaltyButtonListener(JButton button){
			this.button = button;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			button.setText("Stop");
			button.setActionCommand("stop");
			strategy.kickingPenalty();
		}
	}
	class DefendPenaltyButtonListener implements ActionListener {
		JButton button;
		
		DefendPenaltyButtonListener(JButton button){
			this.button = button;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Boolean running=false;
			if (strategy!=null) {
				running = strategy.isAlive();
			}
			if (running){
				System.out.println("defending penalty");
				button.setText("Stop");
				button.setActionCommand("stop");
				strategy.defendPenalty();
			}
		}
	}

	// Basic command button listeners
	class StopListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				mComms.stopMoving();
				mComms.stopRotating();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	class RotateListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				if ("â†·" == e.getActionCommand()){
					mComms.rotate(RotationDirection.CLOCKWISE, 10);
				} else {
					mComms.rotate(RotationDirection.COUNTERCLOCKWISE, 10);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	class MoveListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if ("â†–" == e.getActionCommand()){
					mComms.move(-Math.PI/4);
				} else if("â†‘" == e.getActionCommand()){
					mComms.move(0);
				} else if("â†—" == e.getActionCommand()){
					mComms.move(Math.PI/4);
				} else if("â†�" == e.getActionCommand()){
					mComms.move(-Math.PI/2);
				} else if("â†’" == e.getActionCommand()){
					mComms.move(Math.PI/2);
				} else if("â†™" == e.getActionCommand()){
					mComms.move(-3 * Math.PI/4);
				} else if("â†“" == e.getActionCommand()){
					mComms.move(Math.PI);
				} else if("â†˜" == e.getActionCommand()){
					mComms.move(3 * Math.PI/4);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	class KickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			mCommandHelper.kick();
		}
	}
	class ResetButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Boolean running = false;
			if (strategy!=null) {
				running = strategy.isAlive();
			}
			if (running){
				System.out.println("resetting");
				strategy.reset();
			}
		}
	}

	// Advanced tab listeners
	class RemoveShadowsListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if ("true" == e.getActionCommand()){
				mWorldState.setRemoveShadows(true);
			} else {
				mWorldState.setRemoveShadows(false);
			}
		}
	}
}
