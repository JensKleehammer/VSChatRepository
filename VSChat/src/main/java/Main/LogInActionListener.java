package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInActionListener implements ActionListener{
	private LogIn logInClient;
	private ClientGUI clientGui;
	private GUIActionListener guiActionListener;
	
/* Der Konstruktor für den ActionListener, der zuständig ist für das empfangen
 * von Events, die auf dem LogIn Fenster passieren. Damit die Events abgefragt
 * werden können muss ein Objekt der Klasse LogIn übergeben werden
 */
	public LogInActionListener(LogIn logIn){
		this.logInClient = logIn;
	}

/* Diese Methode nimmt das Event entgegen und analysiert dieses. Falls der Button
 * "LogIn" gedrückt wurde und das Textfeld einen Namen enthält wird eine neues 
 * GUIClient Objekt erstellt sowie der ActionListener, der für die Events, die die 
 * GUI abfeuert, zuständig ist
 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == logInClient.getLogIn())
		{
			if(!logInClient.getTextField().getText().isEmpty())
			{
				clientGui = new ClientGUI(logInClient.getTextField().getText());
				
				guiActionListener = new GUIActionListener(clientGui);				
				clientGui.getTextfield().addActionListener(guiActionListener);
				clientGui.setConnected(true);
				
				logInClient.setVisible(false);
			}
			
		}
		
	}

}

