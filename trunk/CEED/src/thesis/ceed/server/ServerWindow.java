package thesis.ceed.server;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerWindow extends JFrame {
	private static String DEFAULT_DB_PATH = "D:\\EMODB";
	private JPanel mainPanel;
	private JButton btnSelectDBPath;
	private JButton btnRunPraat;
	private JButton btnAttributeSelection;
	private JLabel lblStatus;
	
	private JFileChooser fileChooser;
	public ServerWindow(){		
		//Init components
		mainPanel = new JPanel();
		btnSelectDBPath = new JButton("Select Database Folder");
		btnRunPraat = new JButton("Run Praat");
		btnRunPraat.setEnabled(false);
		btnAttributeSelection = new JButton("Attribute Selection");
		btnAttributeSelection.setEnabled(false);
		lblStatus = new JLabel("");		
		fileChooser = new JFileChooser(DEFAULT_DB_PATH);
		
		//Set layout for mainPanel
		mainPanel.setLayout(new FlowLayout());
		//Add control to Panel
		mainPanel.add(btnSelectDBPath);
		mainPanel.add(btnRunPraat);
		mainPanel.add(btnAttributeSelection);
		mainPanel.add(lblStatus);
		
		//Add Panel to this Frame and Set info for this Frame
		this.setContentPane(mainPanel);
		this.setTitle("CEED Server");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Rearrange components to fit the Frame
		this.pack();
		
	}
}
