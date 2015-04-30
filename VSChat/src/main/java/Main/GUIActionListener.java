package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* Diese Klasse ist dafür zuständig, Events auf der GUI abzufragen und 
 * entsprechende Schritte je nach Event auszuführen. Dabei erbt die Klasse
 * von der Klasse ActionListener.  
 */
public class GUIActionListener implements ActionListener {
	private ClientGUI clientGui;

	public GUIActionListener(ClientGUI clientGui) {
		this.clientGui = clientGui;
	}

	public void actionPerformed(ActionEvent e) {		
/* Die Verbindung zur CouchDB wird getrennt und der Client ist nichtmehr 
 * connected. Des Weiteren wird das Fenster geschlossen.
 */
		if (e.getSource() == clientGui.getLogout()) {
			clientGui.getDbClient().getCouchDbClient().shutdown();
			clientGui.setConnected(false);
			System.exit(0);
			return;
		}

		if (e.getSource() == clientGui.getLastHour()) {
			Long time = System.currentTimeMillis() - (60 * 60 * 1000);
			clientGui.getDbClient().readHistory(time);
		}

		if (e.getSource() == clientGui.getLastSevenDays()) {
			Long time = (System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000));
			clientGui.getDbClient().readHistory(time);
		}
		
		if(e.getSource() == clientGui.getShowAll()){
			clientGui.getDbClient().readHistory(null);
		}

/* Solange der Client connceted ist wird das geschriebene aus dem Textfeld
 * in der Datenbank gespeichert und auf der GUI im TextArea angezeigt
 */
		if (clientGui.isConnected()
				&& !clientGui.getTextfield().getText().isEmpty()) {
			clientGui.getDbClient()
					.writeMessage(clientGui.getTextfield().getText(),
							clientGui.getUsername());
			
			clientGui.getTextfield().setText("");
			return;
		}
	}
}
