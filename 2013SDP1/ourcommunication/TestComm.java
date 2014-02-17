package ourcommunication;

public class TestComm {
	
	public static void main(String[] args){
		System.out.println("Starting the Bluetooth communication...");
		Server server = new Server(null);
		
		System.out.println("Sending defender robot int 1 to kick");
		server.send(Server.DEFENDER, 1);
		
		// Close the Bluetooth communication
		server.close();
	}

}
