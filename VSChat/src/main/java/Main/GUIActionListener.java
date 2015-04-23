package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class GUIActionListener implements ActionListener{
	private ClientGUI clientGui; 
	
	public GUIActionListener(ClientGUI clientGui)
	{
		this.clientGui = clientGui;
	}
	
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == clientGui.getLogout()) {
//			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			clientGui.getDbClient().getCouchDbClient().shutdown();
			System.exit(0);
			return;
		}

		// ok it is coming from the JTextField
		if(clientGui.isConnected()) {
			// just have to send the message
			clientGui.getDbClient().writeMessage(clientGui.getTextfield().getText(), clientGui.getUsername());				
			clientGui.append(clientGui.getTextfield().getText());
			clientGui.getTextfield().setText("");
			return;
		}
		

//		if(o == clientGui.getLogin()) {
////			// ok it is a connection request
////			String username = clientGui.getTextfield().getText().trim();
////			// empty username ignore it
////			if(username.length() == 0)
////				return;
////			// empty serverAddress ignore it
////			String server = tfServer.getText().trim();
////			if(server.length() == 0)
////				return;
////			// empty or invalid port numer, ignore it
////			String portNumber = tfPort.getText().trim();
////			if(portNumber.length() == 0)
////				return;
////			int port = 0;
////			try {
////				port = Integer.parseInt(portNumber);
////			}
////			catch(Exception en) {
////				return;   // nothing I can do if port number is not valid
////			}
//
//			// try creating a new Client with GUI
////			client = new Client(server, port, username, this);
//			// test if we can start the Client
////			if(!client.start()) 
////				return;
////			tf.setText("");
////			label.setText("Enter your message below");
//			clientGui.getClient().start();
//			clientGui.setConnected(true);
//			
//			// disable login button
//			clientGui.getLogin().setEnabled(false);
//			// enable the 2 buttons
//			clientGui.getLogout().setEnabled(true);
//			clientGui.getWhoIsIn().setEnabled(true);
//			// disable the Server and Port JTextField
////			tfServer.setEditable(false);
////			tfPort.setEditable(false);
//			// Action listener for when the user enter a message
//			clientGui.getTextfield().addActionListener(this);
//		}
//
//	}

	// to start the whole thing the server
//	public static void main(String[] args) {
//		new ClientGUI("localhost", 1500);
//	}

}
}
