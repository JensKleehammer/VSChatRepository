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

import com.google.gson.JsonObject;

@Getter
@Setter
public class DBClient extends Thread {
	private CouchDbClient couchDbClient;
	private CouchDbProperties properties;
	private Integer messageID;
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
		this.serverList.add(null);
		this.serverList.add(null);
		this.couchDBconnect();
		this.clientGui = clientGui;

		CouchDbInfo info = couchDbClient.context().info();
		String since = info.getUpdateSeq();

		this.changes = couchDbClient.changes().includeDocs(true).since(since)
				.heartBeat(3000).continuousChanges();
	}

	private void couchDBconnect() {
		lastServer = serverList.get((int)(Math.random()*serverList.size()));
		try{
		this.properties = new CouchDbProperties()
				.setDbName("chat").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		couchDbClient = new CouchDbClient(properties);
		} catch (CouchDbException e){
			this.reconnect();
		}
	}

	public void run() {
		try {
	while (changes.hasNext()) {
			if (changes.next() != null) {
				ChangesResult.Row feed = changes.next();

				JsonObject document = feed.getDoc();

				Date date = new Date(System.currentTimeMillis());

				clientGui.append(dateFormat.format(date) + " "
						+ document.get("username") + ":\n "
						+ document.get("message") + "\n");
			}
		}
		} catch (CouchDbException e) {
			this.reconnect();
		}
	}
	
	public void reconnect(){
		for (String serverAddress : this.serverList) {
			if(!serverAddress.equals(this.lastServer)){
				this.lastServer = serverAddress;
				this.properties.setHost(serverAddress);
				this.couchDbClient = new CouchDbClient(this.properties);
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

	public void readHistory(Long startKey) {
		List<JsonObject> history = couchDbClient.view("History/History")
				.startKey(startKey).includeDocs(true).query(JsonObject.class);

		for (JsonObject object : history) {

			Date date = new Date(object.get("timestamp").getAsLong());

			clientGui.append(dateFormat.format(date) + " "
					+ object.get("username") + ":\n " + object.get("message")
					+ "\n");
		}
	}
}
