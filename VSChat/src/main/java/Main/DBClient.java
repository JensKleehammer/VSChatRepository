package Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.ReplicationResult;
import org.lightcouch.Replicator;

import com.google.gson.JsonObject;

@Getter
@Setter
public class DBClient extends Thread {
	private CouchDbClient couchDbClient;
	private CouchDbProperties properties;
	private Integer messageID;
	private ReplicationResult replicationOne, replicationTwo;
	private Replicator replicatorOne, replicatorTwo;
	private HashMap<String, Object> map;
	private ClientGUI clientGui;
	private Changes changes;
	private SimpleDateFormat dateFormat;
	private List<String> serverList;
	private String lastServer;

	public DBClient(ClientGUI clientGui) {
		this.dateFormat = new SimpleDateFormat("dd.MM.yyyy ' - ' HH:mm");
		this.map = new HashMap<String, Object>();
		this.messageID = 0;
		this.serverList = new ArrayList<String>();
		this.serverList.add("172.16.40.5");
		this.serverList.add("172.16.49.92");
		this.couchDBconnect();
		this.clientGui = clientGui;
		this.createChanges();
		this.createReplication();
		System.out.println(clientGui.getUsername() + " IP: "+ lastServer);
	}

	private void couchDBconnect() {
		lastServer = serverList.get((int) (Math.random() * serverList.size()));
		try {
			this.properties = new CouchDbProperties().setDbName("chat")
					.setCreateDbIfNotExist(true).setProtocol("http")
					.setHost(lastServer).setPort(5984).setMaxConnections(100)
					.setConnectionTimeout(0);
			couchDbClient = new CouchDbClient(properties);
		} catch (CouchDbException e) {
			this.reconnect();
		}
	}

	private void createReplication() {
		this.replicationOne = this.couchDbClient.replication()
				.source("http://172.16.40.5:5984/chat")
				.target("http://172.16.49.92:5984/chat").createTarget(true)
				.trigger();
		
		this.replicationTwo = this.couchDbClient.replication()
				.source("http://172.16.49.92:5984/chat")
				.target("http://172.16.40.5:5984/chat").createTarget(true)
				.trigger();
		
		this.replicatorOne = this.couchDbClient.replicator()
				.source("http://172.16.40.5:5984/chat")
				.target("http://172.16.49.92:5984/chat")
				.continuous(true);
		
		this.replicatorTwo = this.couchDbClient.replicator()
				.source("http://172.16.49.92:5984/chat")
				.target("http://172.16.40.5:5984/chat")
				.continuous(true);
		
		this.replicatorOne.save();
		this.replicatorTwo.save();
	}

	private void createChanges() {
		CouchDbInfo info = this.couchDbClient.context().info();
		String since = info.getUpdateSeq();

		this.changes = this.couchDbClient.changes().includeDocs(true)
				.since(since).heartBeat(3000).continuousChanges();
	}

	public void run() {
		try {
			while (changes.hasNext()) {

				if (changes.next() != null) {
					ChangesResult.Row feed = changes.next();

					JsonObject document = feed.getDoc();

					Date date = new Date(System.currentTimeMillis());

					clientGui.append(dateFormat.format(date)
							+ "|\t "
							+ document.get("username").getAsString()
									.toUpperCase() + ":\n "
							+ document.get("message").getAsString() + "\n");
				}
			}
		} catch (CouchDbException e) {
			this.reconnect();
		}
	}

	public void reconnect() {
		for (String serverAddress : this.serverList) {
			if (!serverAddress.equals(this.lastServer)) {
				this.lastServer = serverAddress;
				this.properties.setHost(serverAddress);
				this.couchDbClient = new CouchDbClient(this.properties);
				this.createChanges();

				if (!this.isAlive())
					this.start();
				System.out.println(clientGui.getUsername()+" neueIP: " +lastServer);
				break;
			}
		}
		this.run();
	}

	public void writeMessage(String msg, String username) {
		// while (this.couchDbClient.contains("message" + messageID.toString()))
		// {
		// messageID++;
		// }

		// write into database
		// map.put("_id", "message" + messageID.toString());
		map.put("username", username);
		map.put("message", msg);
		map.put("timestamp", System.currentTimeMillis());// this.getDateAndTime());
		this.couchDbClient.save(map);
	}

	public void readHistory(Long startKey) {
		List<JsonObject> history = couchDbClient.view("History/History")
				.startKey(startKey).includeDocs(true).query(JsonObject.class);

		for (JsonObject object : history) {
			String message;
			Date date = new Date(object.get("timestamp").getAsLong());

			message = dateFormat.format(date) + "| "
					+ object.get("username").getAsString().toUpperCase()
					+ ":\n " + object.get("message").getAsString() + "\n";
			clientGui.append(message);
		}
	}
}
