package thesis.ceed.server.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import thesis.ceed.server.Server;

public class XPanel extends JPanel {

	private static final long serialVersionUID = -6248094217134328164L;
	
	private JButton btnStart, btnStop;
	
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
		
		setLayout(new FlowLayout());
		add(btnStart);
		add(btnStop);
	}

}
