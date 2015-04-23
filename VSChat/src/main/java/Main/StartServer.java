package Main;

public class StartServer {
	/*
	 * To run as a console application just open a console window and: > java
	 * Server > java Server portNumber If the port number is not specified 1500
	 * is used
	 */
	public static void main(String[] args) {
		// create a server object and start it
		Server server = new Server(1500);
		server.start();
	}
}
