package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInActionListener implements ActionListener{
	private LogIn logInClient;
	private ClientGUI clientGui;
//	private Client client;
	private GUIActionListener guiActionListener;
	
	public LogInActionListener(LogIn logIn){
		this.logInClient = logIn;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == logInClient.getLogIn())
		{
			if(!logInClient.getTextField().getText().isEmpty())
			{
				clientGui = new ClientGUI();
				
				new Client("127.0.0.1", 1500, 
						logInClient.getTextField().getText(), clientGui);
				
				clientGui.getClient().start();
				clientGui.setConnected(true);
				
				clientGui.setTitle(logInClient.getTextField().getText());
				
				guiActionListener = new GUIActionListener(clientGui);
				
				clientGui.getTextfield().addActionListener(guiActionListener);
				
				logInClient.setVisible(false);
			}
			
		}
		
	}

}
