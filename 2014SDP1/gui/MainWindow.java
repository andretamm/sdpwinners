package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import communication.Server;
import behavior.finalattacker.FinalKillerManager;
import behavior.finalattacker.FinalKillerVerticalManager;
import behavior.finaldefender.FinalDefenderManager;
import behavior.finaldefenderpenalty.DefenderPenaltyManager;
import behavior.friendlyattacker.FriendlyKillerManager;
import behavior.friendlydefender.FriendlyDefenderManager;
import constants.RobotType;
import sdp.vision.Drawable;
import sdp.vision.WorldState;

/**
 * Control window to run a robot
 * 
 * @author Euan Reid
 */

public class MainWindow {
	private JFrame jfrmRobotController;
	static Component buttonSpacer = Box.createVerticalStrut(10);
	private WorldState worldstate;
	private Server server;
	private behavior.Strategy strat;

	private static HashMap<String, Collection<Drawable>> mDrawables = new HashMap<String, Collection<Drawable>>();

	public static void addOrUpdateDrawable(String key, Collection<Drawable> drawables){
		mDrawables.put(key, drawables);
	}


	public static HashMap<String, Collection<Drawable>> getDrawables() {
		return mDrawables;
	}

	/**
	 * Create the application.
	 */
	public MainWindow(WorldState worldstate, Server server, behavior.Strategy strategy) {
		this.worldstate = worldstate;
		this.server = server;
		this.strat = strategy;
		initControl();
		this.jfrmRobotController.setVisible(true);
	}

	////////////////////////////////////////////////////////////////////////////////
	////	AUTO TAB															////
	////////////////////////////////////////////////////////////////////////////////

	private JPanel autoTab() {
		JPanel auto = new JPanel();
		auto.setLayout(new BoxLayout(auto, BoxLayout.Y_AXIS));

		auto.add(Box.createVerticalGlue());
		auto.add(controlButtons());
		auto.add(Box.createVerticalGlue());

		return auto;
	}

	public JPanel connectButtons(){
		// Attacker and defender vertical panels
		JPanel attacker = Helper.titledPanel("Attacker");
		attacker.setLayout(new BoxLayout(attacker, BoxLayout.Y_AXIS));
		JPanel defender = Helper.titledPanel("Defender");
		defender.setLayout(new BoxLayout(defender, BoxLayout.Y_AXIS));
		
		// Connect/disconnect buttons
		JButton btnConnectAttacker = new JButton("Connect Attacker");
		JButton btnDisconnectAttacker = new JButton("Disconnect Attacker");
		
		JButton btnConnectDefender = new JButton("Connect Defender");
		JButton btnDisconnectDefender = new JButton("Disconnect Defender");
		
		// Button listeners
		btnConnectAttacker.addActionListener(new ConnectListener(RobotType.ATTACKER));
		btnConnectDefender.addActionListener(new ConnectListener(RobotType.DEFENDER));
		btnDisconnectAttacker.addActionListener(new DisconnectListener(RobotType.ATTACKER));
		btnDisconnectDefender.addActionListener(new DisconnectListener(RobotType.DEFENDER));

		// Add buttons to vertical panels
		attacker.add(btnConnectAttacker);
		attacker.add(btnDisconnectAttacker);
		
		defender.add(btnConnectDefender);
		defender.add(btnDisconnectDefender);
		
		// Panel with everything
		JPanel buttons = new JPanel();
		buttons.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		buttons.add(attacker);
		buttons.add(defender);

		return buttons;
	}
	
	private JPanel controlButtons(){
		// Create buttons	
		JButton bAttackerKiller = new JButton("Killer");
		JButton bAttackerFinalKiller = new JButton("OldKiller");
		JButton bAttackerDefender = new JButton("Defender");
		
		JButton bDefenderKiller = new JButton("Killer");
		JButton bDefenderFinalDefender = new JButton("Defender");
		JButton bDefenderPenalty = new JButton("PENALTY");
		
		JButton bStartStop = new JButton("Start");

		// Add listeners
		bAttackerKiller.addActionListener(new KillerButtonListener(RobotType.ATTACKER));
		bAttackerFinalKiller.addActionListener(new FinalKillerButtonListener(RobotType.ATTACKER));
		bAttackerDefender.addActionListener(new FinalDefenderButtonListener(RobotType.ATTACKER));
		
		bDefenderKiller.addActionListener(new KillerButtonListener(RobotType.DEFENDER));
		bDefenderFinalDefender.addActionListener(new FinalDefenderButtonListener(RobotType.DEFENDER));
		bDefenderPenalty.addActionListener(new PenaltyButtonListener(RobotType.DEFENDER));
		
		bStartStop.addActionListener(new StartStopButtonListener());
		bStartStop.setActionCommand("start");

		// Group buttons onto panels
		JPanel startstop = Helper.titledPanel("Start/Stop");
		startstop.setLayout(new BoxLayout(startstop, BoxLayout.Y_AXIS));
		startstop.add(bStartStop);
		
		JPanel attacker = Helper.titledPanel("Attacker");
		attacker.setLayout(new BoxLayout(attacker, BoxLayout.Y_AXIS));
		attacker.add(bAttackerKiller);
		attacker.add(bAttackerFinalKiller);
		attacker.add(bAttackerDefender);

		
		JPanel defender = Helper.titledPanel("Defender");
		defender.setLayout(new BoxLayout(defender, BoxLayout.Y_AXIS));
		defender.add(bDefenderKiller);
		defender.add(bDefenderFinalDefender);
		defender.add(bDefenderPenalty);


		// Create control button panel
		JPanel buttons = new JPanel();
		buttons.add(Box.createHorizontalGlue());
		buttons.add(startstop);
		buttons.add(attacker);
		buttons.add(defender);
		buttons.add(Box.createHorizontalGlue());

		return buttons;
	}

	////////////////////////////////////////////////////////////////////////////////
	////	INITIALISERS														////
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialise the contents of the frame.
	 */
	private void initControl(){
		jfrmRobotController = new JFrame();

		jfrmRobotController.setTitle("Rockin' Robots Control Centre");
		jfrmRobotController.setBounds(100, 100, 450, 300);
		jfrmRobotController.setLocation(0,505);
		jfrmRobotController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jfrmRobotController.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		jfrmRobotController.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		jfrmRobotController.setLocation(0,505);

		tabbedPane.addTab("Auto", autoTab());

		jfrmRobotController.getContentPane().add(Helper.statePane(this), BorderLayout.PAGE_END);

		jfrmRobotController.pack();
	}

	////////////////////////////////////////////////////////////////////////////////
	////	ACTION LISTENERS													////
	////////////////////////////////////////////////////////////////////////////////

	// Connect button listeners
	class ConnectListener implements ActionListener {
		
		private RobotType type;
		
		public ConnectListener(RobotType type) {
			super();
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			server.connectToRobot(type);
		}
	}
	
	// Disconnect button listeners
	class DisconnectListener implements ActionListener {
		
		private RobotType type;
		
		public DisconnectListener(RobotType type) {
			super();
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			server.disconnectFromRobot(type);
		}
	}
	
	/**
	 * Listener for the Start/Stop button
	 */
	class StartStopButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if ("start" == e.getActionCommand()) {
				System.out.println("Starting playing");
				((JButton) e.getSource()).setText("Stop");
				((JButton) e.getSource()).setActionCommand("stop");
				strat.start();
			} else if ("stop" == e.getActionCommand()) {
				System.out.println("Stopping playing");
				((JButton) e.getSource()).setText("Start");
				((JButton) e.getSource()).setActionCommand("start");
				strat.stop();
			}
		}
	}
	
	class KillerButtonListener implements ActionListener {
		RobotType type;
		
		KillerButtonListener(RobotType type){
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Set Killer strategy for " + type);
			strat.setStrategy(type, new FriendlyKillerManager(worldstate, type, server));
		}
	}
	
	class FinalKillerButtonListener implements ActionListener {
		RobotType type;
		
		FinalKillerButtonListener(RobotType type){
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Set Killer strategy for " + type);
			strat.setStrategy(type, new FinalKillerManager(worldstate, type, server));
		}
	}
	
	class FinalDefenderButtonListener implements ActionListener {
		RobotType type;
		
		FinalDefenderButtonListener(RobotType type){
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Set Defender strategy for " + type);
			strat.setStrategy(type, new FinalDefenderManager(worldstate, type, server));
			
		}
	}
	
	class FriendlyDefenderButtonListener implements ActionListener {
		RobotType type;
		
		FriendlyDefenderButtonListener(RobotType type){
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Set Defender strategy for " + type);
			strat.setStrategy(type, new FriendlyDefenderManager(worldstate, type, server));
		}
	}
	
	
	class PenaltyButtonListener implements ActionListener {
		RobotType type;
		
		PenaltyButtonListener(RobotType type){
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Set PENALTY strategy for " + type);
			strat.setStrategy(type, new DefenderPenaltyManager(worldstate, type, server));
		}
	}

}
