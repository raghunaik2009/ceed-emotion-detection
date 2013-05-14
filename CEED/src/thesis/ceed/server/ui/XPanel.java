package thesis.ceed.server.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import thesis.ceed.server.Server;

public class XPanel extends JPanel {

	private static final long serialVersionUID = -6248094217134328164L;
	
	private static JTextArea taResult;
	private JButton btnStart, btnStop;
	
	public static void outText(String text) {
		taResult.append(text);
	}
	
	public XPanel() {
		btnStart = new JButton("Start server");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Server.startServer();
			}
		});
		btnStop = new JButton("Stop server");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Server.stopServer();
			}
		});
		JPanel pnlButtons = new JPanel(new FlowLayout());
		pnlButtons.add(btnStart);
		pnlButtons.add(btnStop);
		
		taResult = new JTextArea();
		
		setLayout(new BorderLayout());
		add(pnlButtons, BorderLayout.NORTH);
		add(taResult, BorderLayout.CENTER);
	}

}
