package Main;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogIn extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField textField;
	private JPanel panel;
	private JButton logIn;
	private LogInActionListener logInActionListener;
	
/*Hier wird das LogIn Fenster generiert mit dem Label, Textfeld und dem Button
des weiteren wird der ActionListener initialisiert und dem Button hinzugefügt*/	
	public LogIn(){
		this.logInActionListener = new LogInActionListener(this);
		
		this.panel = new JPanel(new GridLayout(3, 1));
		
		this.label = new JLabel("Enter your username");
		this.panel.add(label);
		
		this.textField = new JTextField();
		this.panel.add(textField);
		
		this.logIn = new JButton("LogIn");
		this.logIn.addActionListener(logInActionListener);
		this.panel.add(logIn);
		
		this.add(panel);
		this.setSize(new Dimension(100, 100));
		this.setResizable(false);
		this.setLocation(600, 300);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}	
}

