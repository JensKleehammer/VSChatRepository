package Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.ReplicationResult;
import org.lightcouch.Replicator;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> clientList;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;

	private CouchDbClient dbClient;
	private ReplicationResult replication;
	private Replicator replicator;

	/*
	 * server constructor that receive the port to listen to for connection as
	 * parameter in console
	 */
	public Server(int port) {
		this.port       = port;
		this.sdf        = new SimpleDateFormat("HH:mm:ss");
		this.clientList = new ArrayList<ClientThread>();
		this.couchDBconnect();
		this.createReplication();
	}

	public void couchDBconnect() {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("chat").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("127.0.0.1").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		setDbClient(new CouchDbClient(properties));
	}
	
	public void createReplication(){
		this.replication = dbClient.replication().source("chat")
				.target("target_chat").createTarget(true).trigger();
		
		this.replicator = dbClient.replicator().source("chat")
				.target("target_chat").continuous(true);
		this.replicator.save();
	}

	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try {
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while (keepGoing) {
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept(); // accept connection
				// if I was asked to stop
				if (!keepGoing)
					break;
				ClientThread clientThread = new ClientThread(socket, uniqueId, this); // make a
																		// thread
																		// of it
				this.clientList.add(clientThread); // save it in the ArrayList
				clientThread.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for (int i = 0; i < this.clientList.size(); ++i) {
					ClientThread tc = this.clientList.get(i);
					try {
						tc.getsInput().close();
						tc.getsOutput().close();
						tc.getSocket().close();
					} catch (IOException ioE) {
						// not much I can do
					}
				}
			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
			String msg = sdf.format(new Date())
					+ " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}

	/*
	 * For the GUI to stop the server
	 */
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		} catch (Exception e) {
			// nothing I can really do
		}
	}

	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	public void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
	}

	/*
	 * to broadcast a message to all Clients
	 */
	public synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console or GUI
		System.out.print(messageLf);

		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for (int i = this.clientList.size(); --i >= 0;) {
			ClientThread ct = this.clientList.get(i);
			// try to write to the Client if it fails remove it from the list
			if (!ct.writeMsg(messageLf)) {
				this.clientList.remove(i);
				display("Disconnected Client " + ct.getUsername()
						+ " removed from list.");
			}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for (int i = 0; i < this.clientList.size(); ++i) {
			ClientThread ct = this.clientList.get(i);
			// found it
			if (ct.getId() == id) {
				this.clientList.remove(i);
				return;
			}
		}
	}
	
//	public List<String> readHistory(){
//		List<String> historyList = new ArrayList<String>();
//		JsonObject jsonObject = new JsonObject();
//		Integer id = 0;
//		
//		while (dbClient.contains(id.toString())) {
//			jsonObject = dbClient.find(JsonObject.class, id.toString());
//			
//			id++;			
//		}		
//		return historyList;		
//	}

	public CouchDbClient getDbClient() {
		return dbClient;
	}

	public void setDbClient(CouchDbClient dbClient) {
		this.dbClient = dbClient;
	}
	
	public Replicator getReplicator() {
		return replicator;
	}
}

	

	/** One instance of this thread will run for each client */
	
