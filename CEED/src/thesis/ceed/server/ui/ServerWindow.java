package thesis.ceed.server.ui;

import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import thesis.ceed.server.Attempt;
import thesis.ceed.server.ServerDbHelper;

public class ServerWindow extends JFrame {
	
	private static final long serialVersionUID = -2627869533995160107L;
	
	/*private static String DEFAULT_DB_PATH = "D:\\EMODB";
	private JPanel mainPanel;
	private JButton btnSelectDBPath;
	private JButton btnRunPraat;
	private JButton btnAttributeSelection;
	private JLabel lblStatus;*/
	private JTabbedPane tabPane = new JTabbedPane();
	private StatisticsPanel statPanel;
	
	public ServerWindow(){
		statPanel = new StatisticsPanel();
		
		
		
		
		
		
		
		
		//Init components
/*		mainPanel = new JPanel();
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
		this.setContentPane(mainPanel);*/
		this.setContentPane(statPanel);
		this.setTitle("CEED Server");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Rearrange components to fit the Frame
		this.pack();
		this.setVisible(true);
	}
	
	public static void main(String[] argv) {
		ServerDbHelper dbHelper = new ServerDbHelper();
		dbHelper.createDatabase();
		dbHelper.createTables();
		dbHelper.addUser("3333");
		dbHelper.addUser("1111");
		dbHelper.addUser("2222");
		dbHelper.addUser("4444");
		dbHelper.addAttempt(new Attempt("1111", "aaaa", "Anger", String.valueOf(new Date(90, 11, 20).getTime())));
		dbHelper.addAttempt(new Attempt("2222", "bbbb", "Neutral", String.valueOf(new Date(99, 12, 23).getTime())));
		dbHelper.addAttempt(new Attempt("3333", "cccc", "Boredom", String.valueOf(new Date(109, 1, 25).getTime())));
		dbHelper.addAttempt(new Attempt("4444", "dddd", "Fear", String.valueOf(new Date(113, 7, 20).getTime())));
		
		new ServerWindow();
	}
}
