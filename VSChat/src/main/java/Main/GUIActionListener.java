package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIActionListener implements ActionListener {
	private ClientGUI clientGui;

	public GUIActionListener(ClientGUI clientGui) {
		this.clientGui = clientGui;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if (o == clientGui.getLogout()) {
			clientGui.getDbClient().getCouchDbClient().shutdown();
			clientGui.getDbClient().stop();
			System.exit(0);
			return;
		}

		if (o == clientGui.getLastHour()) {
			clientGui.getDbClient().lastHour();
		}

		if (o == clientGui.getLastSevenDays()) {
			clientGui.getDbClient().lastSevenDays();
		}
		
		if(o == clientGui.getShowAll()){
			clientGui.getDbClient().completeHistory();
		}

		// ok it is coming from the JTextField
		if (clientGui.isConnected()
				&& !clientGui.getTextfield().getText().isEmpty()) {
			// just have to send the message
			clientGui.getDbClient()
					.writeMessage(clientGui.getTextfield().getText(),
							clientGui.getUsername());
			// clientGui.append(clientGui.getTextfield().getText());
			clientGui.getTextfield().setText("");
			return;
		}
	}
}
