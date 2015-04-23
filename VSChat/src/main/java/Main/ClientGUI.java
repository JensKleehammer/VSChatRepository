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
	// to hold the server address an the port number
//	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton logout;
	// for the chat room
	private JTextArea textArea;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
//	private int defaultPort;
//	private String defaultHost;
	private GUIActionListener guiActionListener;

	// Constructor connection receiving a socket number
	ClientGUI(/*Client client*/) {

//		super("Chat Client");
//		defaultPort = port;
//		defaultHost = host;
		
//		this.client = client;
//		this.client.setClientGui(this);
		this.guiActionListener = new GUIActionListener(this);
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// the server name anmd the port number
//		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// the two JTextField with default value for server address and port number
//		tfServer = new JTextField(host);
//		tfPort = new JTextField("" + port);
//		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

//		serverAndPort.add(new JLabel("Server Address:  "));
//		serverAndPort.add(tfServer);
//		serverAndPort.add(new JLabel("Port Number:  "));
//		serverAndPort.add(tfPort);
//		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
//		northPanel.add(serverAndPort);

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

		// the 3 buttons
//		login = new JButton("Login");
//		login.addActionListener(this.guiActionListener);
		logout = new JButton("Logout");
		logout.addActionListener(this.guiActionListener);
		logout.setEnabled(true);		// you have to login before being able to logout
//		whoIsIn = new JButton("Who is in");
//		whoIsIn.addActionListener(this.guiActionListener);
//		whoIsIn.setEnabled(true);		// you have to login before being able to Who is in

		JPanel southPanel = new JPanel();
//		southPanel.add(login);
		southPanel.add(logout);
//		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		textfield.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		textArea.append(str);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
//		login.setEnabled(true);
		logout.setEnabled(false);
//		whoIsIn.setEnabled(false);
//		label.setText("Enter your username below");
//		textfield.setText("Anonymous");
		// reset port number and host name as a construction time
//		tfPort.setText("" + defaultPort);
//		tfServer.setText(defaultHost);
		// let the user change them
//		tfServer.setEditable(false);
//		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		textfield.removeActionListener(this.guiActionListener);
		connected = false;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	
		
	/*
	* Button or JTextField clicked
	*/
	

}

