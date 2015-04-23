package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField textfield;
	// to Logout and get the list of the users
	private JButton logout;
	// for the chat room
	private JTextArea textArea;
	// if it is for connection
	private boolean connected;
	// the Client object
	private DBClient dbClient;
	private String username;

	private GUIActionListener guiActionListener;

	// Constructor connection receiving a socket number
	ClientGUI(String username) {
		super(username);
		this.setUsername(username);
		this.guiActionListener = new GUIActionListener(this);
		this.setDbClient(new DBClient());
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));

		// the Label and the TextField
		label = new JLabel("Enter your message below", SwingConstants.CENTER);
		northPanel.add(label);
		textfield = new JTextField("");
		textfield.setBackground(Color.WHITE);
		northPanel.add(textfield);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		this.textArea = new JTextArea("", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(this.textArea));
		this.textArea.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		logout = new JButton("Logout");
		logout.addActionListener(this.guiActionListener);
		logout.setEnabled(true);		// you have to login before being able to logout

		JPanel southPanel = new JPanel();
		southPanel.add(logout);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		textfield.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	public void append(String str) {
		textArea.append(str);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}

	public JTextField getTextfield() {
		return textfield;
	}

	public void setTextfield(JTextField textfield) {
		this.textfield = textfield;
	}

	
	public JButton getLogout() {
		return logout;
	}

	public void setLogout(JButton logout) {
		this.logout = logout;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public DBClient getDbClient() {
		return dbClient;
	}

	public void setDbClient(DBClient dbClient) {
		this.dbClient = dbClient;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}

