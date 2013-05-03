package thesis.ceed.server.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import thesis.ceed.recognitionprocess.ClassifierSelection;
import thesis.ceed.recognitionprocess.FeatureExtraction;
import thesis.ceed.recognitionprocess.FeatureSelection;
import thesis.ceed.utils.NotifyingThread;
import thesis.ceed.utils.ThreadCompleteListener;

public class TrainingPanel extends JPanel implements ThreadCompleteListener {

	private static final long serialVersionUID = -2489116449487103041L;
	
	private JTextField txtDbAddr;
	private JButton btnOpenDbFolder, btnTrain;
	private JTextArea taResult;
	private JPanel dbAddrPanel, trnPanel;
	private JFileChooser fileChooser;
	private NotifyingThread one, two, three;
	
	private void setText(String text) {
		taResult.setText(text);
	}
	
	private void setEnableButton(boolean enable) {
		btnTrain.setEnabled(enable);
		btnOpenDbFolder.setEnabled(enable);
	}
	
	public TrainingPanel() {
		dbAddrPanel = new JPanel(new BorderLayout());
		
		txtDbAddr = new JTextField();
		btnOpenDbFolder = new JButton("Open Database");
		btnOpenDbFolder.setToolTipText("Open database folder containing sounds to train");
		fileChooser = new JFileChooser();
		//fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setCurrentDirectory(new File("D:\\Software\\EmoDB\\wav"));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		btnOpenDbFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					txtDbAddr.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		
		dbAddrPanel.add(btnOpenDbFolder, BorderLayout.WEST);
		dbAddrPanel.add(txtDbAddr, BorderLayout.CENTER);
		
		trnPanel = new JPanel(new BorderLayout());
		JPanel pnl = new JPanel(new FlowLayout());
		
		taResult = new JTextArea();
		btnTrain = new JButton("Train");
		btnTrain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEnableButton(false);
				File dbFolder = new File(txtDbAddr.getText());
				if (dbFolder.isDirectory()) {
					one = new NotifyingThread() {
						public void doRun() {
							FeatureExtraction.extractFeature(txtDbAddr.getText());
						}
					};
					two = new NotifyingThread() {
						public void doRun() {
							FeatureSelection.selectFeature(FeatureExtraction.FULL_ARFF_PATH);
						}
					};
					three = new NotifyingThread() {
						public void doRun() {
							ClassifierSelection.selectClassifier(FeatureSelection.SELECTED_ARFF_PATH);
						}
					};
					one.addListener(TrainingPanel.this);
					two.addListener(TrainingPanel.this);
					three.addListener(TrainingPanel.this);
					three.start();
				}
			}
		});
		
		pnl.add(btnTrain);
		trnPanel.add(pnl, BorderLayout.NORTH);
		trnPanel.add(taResult, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(dbAddrPanel, BorderLayout.NORTH);
		add(trnPanel, BorderLayout.CENTER);
	}

	@Override
	public void notifyOfThreadComplete(Thread thread) {
		if (thread.getName().equals(one.getName())) {
			setText(taResult.getText() + "Feature Extraction completed.\n");
			two.start();
		} else if (thread.getName().equals(two.getName())) {
			setText(taResult.getText() + "Feature Selection completed.\n");
			three.start();
		} else if (thread.getName().equals(three.getName())) {
			setText(taResult.getText() + "Classifier Selection completed.\n");
			setEnableButton(true);
		}
	}
}
