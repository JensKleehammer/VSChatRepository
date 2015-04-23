package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInActionListener implements ActionListener{
	private LogIn logInClient;
	private ClientGUI clientGui;
	private GUIActionListener guiActionListener;
	
	public LogInActionListener(LogIn logIn){
		this.logInClient = logIn;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == logInClient.getLogIn())
		{
			if(!logInClient.getTextField().getText().isEmpty())
			{
				clientGui = new ClientGUI(logInClient.getTextField().getText());
				
				guiActionListener = new GUIActionListener(clientGui);				
				clientGui.getTextfield().addActionListener(guiActionListener);
				
				logInClient.setVisible(false);
			}
			
		}
		
	}

}

