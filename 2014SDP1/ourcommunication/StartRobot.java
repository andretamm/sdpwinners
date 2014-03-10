package ourcommunication;

import gui.MainWindow;
import vision.RunVision;
import vision.WorldState;
import behavior.Strategy;

public class StartRobot {
	
	private static Server server;
	
	public static void main(String[] args){
		// Start vision
		WorldState worldstate = new WorldState();
		RunVision.setupVision(worldstate);
		
		// Set up Bluetooth communications class
		server = new Server(worldstate);
			
		// Strategy manager
		Strategy strategy = new Strategy(worldstate, server);
		
		// Start GUI
		MainWindow mw = new MainWindow(worldstate, server, strategy);
	}
}
