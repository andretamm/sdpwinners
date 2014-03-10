package sdp.gui;

import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public final class Helper {
	/**
	 * Adds a centred button to a panel
	 * @param panel - parent panel of button
	 * @param text - button text
	 */
	static void addCenterButton(JPanel panel, final String text){
		addCenterButton(panel, text, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, text + " is not implemented", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Adds a centred button with specified action to a panel
	 * @param panel - parent panel of button
	 * @param text - button text
	 * @param listener - ActionListener for button
	 */
	static void addCenterButton(JPanel panel, String text, ActionListener listener){
		JButton button = new JButton(text);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);

		button.addActionListener(listener);

		panel.add(button);
		panel.add(MainWindow.buttonSpacer);
	}

	/**
	 * Generates a bordered JPanel with a specified title
	 * @param String title
	 * @return JPanel
	 */
	static JPanel titledPanel(String title){
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(title));

		return panel;
	}

	/**
	 * Generates a bordered JPanel titled Milestone n
	 * @param int n - milestone number
	 * @return JPanel
	 */
	static JPanel milestonePanel(int n){
		JPanel milestone = titledPanel("Milestone " + n);
		milestone.setLayout(new BoxLayout(milestone, BoxLayout.Y_AXIS));

		return milestone;
	}

	static JPanel statePane(MainWindow window){
		JPanel statePanel = new JPanel();

		Label stratState = new Label("State: Unknown");
		Label memState = new Label("Mem Usage: ?");

		Timer t = new Timer();
		t.schedule(new StateUpdateTimer(window, stratState, memState), 100, 100);

		statePanel.add(window.connectButtons());
		statePanel.add(stratState);
		statePanel.add(memState);

		return statePanel;
	}
}

class StateUpdateTimer extends TimerTask{

	private Label mGameStateLabel;
	private MainWindow mParent;
	private Label mMem;

	public StateUpdateTimer(MainWindow parent, Label gameState, Label mem){
		mMem = mem;
		mParent = parent;
		mGameStateLabel = gameState;
	}

	private static long bytesToMegabytes(long bytes) {
		return bytes / (1024L * 1024L);
	}

	@Override
	public void run() {
		String text = "No Strategy";
//		if ( mParent.getStrategy() != null ){
//			text = mParent.getStrategy().getGameState().toString();
//		}
		mGameStateLabel.setText("Game State: " + text);

		Runtime runtime = Runtime.getRuntime();
		long memory = runtime.totalMemory() - runtime.freeMemory();

		mMem.setText("Mem Usage: " + bytesToMegabytes(memory) + "Mb");
	}

}
