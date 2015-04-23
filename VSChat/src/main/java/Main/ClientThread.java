package Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientThread extends Thread {
	// the socket where to listen/talk
	private Socket socket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	// my unique id (easier for deconnection)
	private int id;
	// the Username of the Client
	private String username;
	// the only type of message a will receive
	private ChatMessage cm;
	// the date I connect
	private String date;
	
	private Server server;
	
	private Map<String, Object> map = new HashMap<String, Object>();

	private Integer messageID;

	// Constructore
	ClientThread(Socket socket, int uniqueId, Server server) {
		// a unique id
		this.id          = ++uniqueId;
		this.messageID   = 0;
		this.socket      = socket;
		this.server      = server;
		/* Creating both Data Stream */
		System.out
				.println("Thread trying to create Object Input/Output Streams");
		try {
			// create output first
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput  = new ObjectInputStream(socket.getInputStream());
			// read the username
			username = (String) sInput.readObject();
			server.display(username + " just connected.");
		} catch (IOException e) {
			server.display("Exception creating new Input/output Streams: " + e);
			return;
		}
		// have to catch ClassNotFoundException
		// but I read a String, I am sure it will work
		catch (ClassNotFoundException e) {
		}
		date = new Date().toString() + "\n";
	}

	// what will run forever
	public void run() {
		// to loop until LOGOUT
		boolean keepGoing = true;
		
		while (keepGoing) {
			// read a String (which is an object)
			try {
				cm = (ChatMessage) sInput.readObject();
			} catch (IOException e) {
				server.display(username + " Exception reading Streams: " + e);
				break;
			} catch (ClassNotFoundException e2) {
				break;
			}
			// the messaage part of the ChatMessage
			String message = cm.getMessage();

			// Switch on the type of message receive
			switch (cm.getType()) {

			case ChatMessage.MESSAGE:
				server.broadcast(username + ": " + message);

//				letzte MessageID suchen und finden
				while(this.server.getDbClient().contains(messageID+""))
				{
					messageID++;
				}
				
				// write into database
				map.put("_id", messageID);
				map.put("username", username);
				map.put("message", message);
				this.server.getDbClient().save(map);
//				this.server.getReplicator().save();

				break;
				
			case ChatMessage.LOGOUT:
				this.server.display(username + " disconnected with a LOGOUT message.");
				keepGoing = false;
				break;
				
//			case ChatMessage.WHOISIN:
//				writeMsg("List of the users connected at "
//						+ sdf.format(new Date()) + "\n");
//				// scan al the users connected
//				for (int i = 0; i < clientList.size(); ++i) {
//					ClientThread ct = clientList.get(i);
//					writeMsg((i + 1) + ") " + ct.username + " since "
//							+ ct.date);
//				}
//				break;
			}
		}
		// remove myself from the arrayList containing the list of the
		// connected Clients
		this.server.remove(this.id);
		close();
	}

	// try to close everything
	private void close() {
		// try to close the connection
		try {
			if (this.sOutput != null)
				this.sOutput.close();
		} catch (Exception e) {
		}
		try {
			if (this.sInput != null)
				this.sInput.close();
		} catch (Exception e) {
		}
		
		try {
			if (this.socket != null)
				this.socket.close();
		} catch (Exception e) {
		}
	}

	/*
	 * Write a String to the Client output stream
	 */
	public boolean writeMsg(String msg) {
		// if Client is still connected send the message to it
		if (!this.socket.isConnected()) {
			close();
			return false;
		}
		// write the message to the stream
		try {
			this.sOutput.writeObject(msg);
		}
		// if an error occurs, do not abort just inform the user
		catch (IOException e) {
			this.server.display("Error sending message to " + username);
			this.server.display(e.toString());
		}
		return true;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectInputStream getsInput() {
		return sInput;
	}

	public void setsInput(ObjectInputStream sInput) {
		this.sInput = sInput;
	}

	public ObjectOutputStream getsOutput() {
		return sOutput;
	}

	public void setsOutput(ObjectOutputStream sOutput) {
		this.sOutput = sOutput;
	}
	
//	public int getId() {
//		return id;
//	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ChatMessage getCm() {
		return cm;
	}

	public void setCm(ChatMessage cm) {
		this.cm = cm;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}
}


