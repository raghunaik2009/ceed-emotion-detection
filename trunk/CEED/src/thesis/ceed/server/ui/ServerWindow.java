package thesis.ceed.server.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class ServerWindow extends JFrame {
	
	private static final long serialVersionUID = -2627869533995160107L;
	
	private JTabbedPane tabPane;
	private TrainingPanel trnPanel;
	private StatisticsPanel statPanel;
	private XPanel xPanel;
	private static JTextArea taResult;
	private JScrollPane spResult;
	
	public static void log(String text) {
		taResult.append(text);
	}
	
	public ServerWindow(){
		xPanel = new XPanel();
		trnPanel = new TrainingPanel();
		statPanel = new StatisticsPanel();
		
		tabPane = new JTabbedPane();
		tabPane.addTab("X", null, xPanel, "X Panel");
		tabPane.addTab("Training", null, trnPanel, "Training Panel");
		tabPane.addTab("Statistics", null, statPanel, "Statistics Panel");
		tabPane.setSelectedIndex(0);
		
		taResult = new JTextArea();
		spResult = new JScrollPane(taResult);
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(tabPane, BorderLayout.NORTH);
		container.add(spResult, BorderLayout.CENTER);
		setTitle("CEED Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Rearrange components to fit the Frame
		pack();
		setVisible(true);
	}
	/*public static void main(String[] argv) {
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
	}*/
}
