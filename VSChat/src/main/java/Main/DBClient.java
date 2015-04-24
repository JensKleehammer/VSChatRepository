package Main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.ReplicationResult;
import org.lightcouch.Replicator;
import org.lightcouch.View;

import com.google.gson.JsonObject;

public class DBClient extends Thread {
	private CouchDbClient couchDbClient;
	private ReplicationResult replicationOne, replicationTwo;
	private Replicator replicatorOne, replicatorTwo;
	private Integer messageID;
	private HashMap<String, Object> map;
	private ClientGUI clientGui;
	private Changes changes;
	private SimpleDateFormat dateFormat;

	public DBClient(ClientGUI clientGui) {
		this.dateFormat = new SimpleDateFormat("dd.MM.yyyy ' - ' HH:mm");
		this.map = new HashMap<String, Object>();
		this.messageID = 0;
		this.couchDBconnect();
		this.createReplication();
		this.clientGui = clientGui;

		CouchDbInfo info = couchDbClient.context().info();
		String since = info.getUpdateSeq();

		this.changes = couchDbClient.changes().includeDocs(true).since(since)
				.heartBeat(3000).continuousChanges();
	}

	private void couchDBconnect() {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("chat").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		couchDbClient = new CouchDbClient(properties);
	}

	private void createReplication() {
		this.setReplicationOne(couchDbClient.replication().source("chat")
				.target("target_chat").createTarget(true).trigger());

		this.setReplicationTwo(couchDbClient.replication()
				.source("target_chat").target("chat").createTarget(true)
				.trigger());

		this.replicatorOne = couchDbClient.replicator().source("chat")
				.target("target_chat").continuous(true);
		this.replicatorOne.save();

		this.replicatorTwo = couchDbClient.replicator().source("target_chat")
				.target("chat").continuous(true);
		this.replicatorTwo.save();
	}

	public void run() {
		while (changes.hasNext()) {
			if (changes.next() != null) {
				ChangesResult.Row feed = changes.next();

				String docId = feed.getId();
				JsonObject document = feed.getDoc();

				Date date = new Date(System.currentTimeMillis());

				String message = dateFormat.format(date) + " "
						+ document.get("username") + ": "
						+ document.get("message");

				clientGui.append(message + "\n");
			}
		}
	}

	public void writeMessage(String msg, String username) {
		while (this.couchDbClient.contains("message" + messageID.toString())) {
			messageID++;
		}

		// write into database
		map.put("_id", "message" + messageID.toString());
		map.put("username", username);
		map.put("message", msg);
		map.put("timestamp", System.currentTimeMillis());// this.getDateAndTime());
		this.couchDbClient.save(map);
	}

	public void lastHour() {
		List<JsonObject> listHour = couchDbClient.view("History/History")
				.startKey(System.currentTimeMillis() - (60 * 60 * 1000))
				.includeDocs(true).query(JsonObject.class);

		for (JsonObject object : listHour) {

			Date date = new Date(object.get("timestamp").getAsLong());

			clientGui.append(dateFormat.format(date) + " "
					+ object.get("username") + ": " + object.get("message")
					+ "\n");
		}
	}
	
	public void lastSevenDays(){
		List<JsonObject> listHour = couchDbClient.view("History/History")
				.startKey(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000))
				.includeDocs(true).query(JsonObject.class);

		for (JsonObject object : listHour) {

			Date date = new Date(object.get("timestamp").getAsLong());

			clientGui.append(dateFormat.format(date) + " "
					+ object.get("username") + ": " + object.get("message")
					+ "\n");
		}
	}
	
	public void completeHistory(){
		List<JsonObject> listHour = couchDbClient.view("History/History")
				.includeDocs(true).query(JsonObject.class);

		for (JsonObject object : listHour) {

			Date date = new Date(object.get("timestamp").getAsLong());

			clientGui.append(dateFormat.format(date) + " "
					+ object.get("username") + ": " + object.get("message")
					+ "\n");
		}
	}

	public CouchDbClient getCouchDbClient() {
		return couchDbClient;
	}

	public void setCouchDbClient(CouchDbClient dbClient) {
		this.couchDbClient = dbClient;
	}

	public ReplicationResult getReplicationOne() {
		return replicationOne;
	}

	public void setReplicationOne(ReplicationResult replicationOne) {
		this.replicationOne = replicationOne;
	}

	public ReplicationResult getReplicationTwo() {
		return replicationTwo;
	}

	public void setReplicationTwo(ReplicationResult replicationTwo) {
		this.replicationTwo = replicationTwo;
	}
}
