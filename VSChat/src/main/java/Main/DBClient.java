package Main;

import java.util.HashMap;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.ReplicationResult;
import org.lightcouch.Replicator;

import com.google.gson.JsonObject;

public class DBClient {
	private CouchDbClient couchDbClient;
	private ReplicationResult replicationOne, replicationTwo;
	private Replicator replicatorOne, replicatorTwo;
	private Integer messageID;
	private HashMap<String, Object> map;
	
	public DBClient()
	{
		this.map = new HashMap<String, Object>();
		this.messageID = 0;
		this.couchDBconnect();
		this.createReplication();
	}
	
	private void couchDBconnect() {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("chat").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		couchDbClient = new CouchDbClient(properties);
	}
	
	private void createReplication(){
		this.replicationOne = couchDbClient.replication().source("chat")
				.target("target_chat").createTarget(true).trigger();
		
		this.replicationTwo = couchDbClient.replication().source("target_chat")
				.target("chat").createTarget(true).trigger();
		
		this.replicatorOne = couchDbClient.replicator().source("chat")
				.target("target_chat").continuous(true);
		this.replicatorOne.save();
		
		this.replicatorTwo = couchDbClient.replicator().source("target_chat")
				.target("chat").continuous(true);
		this.replicatorTwo.save();
	}
	
	public void writeMessage(String msg, String username)
	{
		while(this.couchDbClient.contains(messageID.toString()))
		{
			messageID++;
		}
		
		// write into database
		map.put("_id", "message"+messageID.toString());
		map.put("username", username);
		map.put("message", msg);
		this.couchDbClient.save(map);
	}
	
	public JsonObject readMessage(){
		return null;
	}
	
	public CouchDbClient getCouchDbClient() {
		return couchDbClient;
	}

	public void setCouchDbClient(CouchDbClient dbClient) {
		this.couchDbClient = dbClient;
	}

}
