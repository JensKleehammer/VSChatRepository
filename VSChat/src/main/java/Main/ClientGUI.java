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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import lombok.Getter;
import lombok.Setter;


/*
 * The Client with its GUI
 */
@Getter
@Setter
public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField textfield;
	// to Logout and get the list of the users
	private JButton logout, lastHour, lastSevenDays, showAll;

	// for the chat room
	private JTextArea textArea;
	// if it is for connection
	private boolean connected;
	// the Client object
	private DBClient dbClient;
	private String username;
	private JScrollPane scrollPane;

	private GUIActionListener guiActionListener;

	// Constructor connection receiving a socket number
	ClientGUI(String username) {
		super(username);
		this.setUsername(username);
		this.guiActionListener = new GUIActionListener(this);
		this.setDbClient(new DBClient(this));
		this.dbClient.start();
		
		// the Panels
		JPanel northPanel = new JPanel(new GridLayout(1,1));
		JPanel centerPanel = new JPanel(new GridLayout(2,1));
		JPanel southPanel = new JPanel();
		
		// The NorthPanel which is the chat room
		
		this.textArea = new JTextArea("", 30, 30);
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
		
		
		this.scrollPane = new JScrollPane(this.textArea);
		this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		northPanel.add(this.scrollPane);
		this.textArea.setEditable(false);
		this.add(northPanel, BorderLayout.NORTH);
		


		// the Label and the TextField
		this.label = new JLabel("Message:", SwingConstants.LEFT);
		centerPanel.add(label);
		this.textfield = new JTextField("");
		this.textfield.setBackground(Color.WHITE);
		centerPanel.add(textfield);
		this.add(centerPanel, BorderLayout.CENTER);

		this.logout = new JButton("Logout");
		this.logout.addActionListener(this.guiActionListener);
		this.logout.setEnabled(true);		// you have to login before being able to logout

		this.lastHour = new JButton("Last Hour");
		this.lastHour.addActionListener(this.guiActionListener);
		this.lastHour.setEnabled(true);
		
		this.lastSevenDays = new JButton("Last 7 days");
		this.lastSevenDays.addActionListener(this.guiActionListener);
		this.lastSevenDays.setEnabled(true);
		
		this.showAll = new JButton("Show all");
		this.showAll.addActionListener(this.guiActionListener);
		this.showAll.setEnabled(true);
		
		southPanel.add(logout);
		southPanel.add(lastHour);
		southPanel.add(lastSevenDays);
		southPanel.add(showAll);
		this.add(southPanel, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(600, 600);
		this.setVisible(true);
		this.setResizable(false);
		this.textfield.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	public void append(String message) {
		textArea.append(message);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}
}

