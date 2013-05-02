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

public class TrainingPanel extends JPanel {

	private static final long serialVersionUID = -2489116449487103041L;
	
	private static final String fullArffFilePath = ".\\Training\\DE-full.arff";
	private static final String selectedArffFilePath = ".\\Training\\DE.arff";
	//private static final String selectedAttFilePath = ".\\Training\\DE.ftr";
	//private static final String selectedClsFilePath = ".\\Training\\DE.cls";

	private JTextField txtDbAddr;
	private JButton btnOpenDbFolder, btnTrain;
	private JTextArea taResult;
	private JPanel dbAddrPanel, trnPanel;
	private JFileChooser fileChooser;
	
	public TrainingPanel() {
		dbAddrPanel = new JPanel(new FlowLayout());
		
		txtDbAddr = new JTextField();
		btnOpenDbFolder = new JButton("Open Database");
		btnOpenDbFolder.setToolTipText("Open database folder containing sounds to train");
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		btnOpenDbFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					txtDbAddr.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		
		dbAddrPanel.add(btnOpenDbFolder);
		dbAddrPanel.add(txtDbAddr);
		
		trnPanel = new JPanel(new BorderLayout());
		
		taResult = new JTextArea();
		btnTrain = new JButton("Train");
		btnTrain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File dbFolder = new File(txtDbAddr.getText());
				if (dbFolder.isDirectory()) {
					new Thread() {
						public void run() {
							FeatureExtraction.extractFeature(txtDbAddr.getText());
							notify();
						}
					}.start();
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						taResult.setText(taResult.getText() + "Feature Extraction interrupted.\n");
					}
					taResult.setText(taResult.getText() + "Feature Extraction completed.\n");
					
					new Thread() {
						public void run() {
							FeatureSelection.selectFeature(fullArffFilePath);
							notify();
						}
					}.start();
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						taResult.setText(taResult.getText() + "Feature Selection interrupted.\n");
					}
					taResult.setText(taResult.getText() + "Feature Selection completed.\n");
					
					new Thread() {
						public void run() {
							ClassifierSelection.selectClassifier(selectedArffFilePath);
							notify();
						}
					}.start();
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						taResult.setText(taResult.getText() + "Classifier Selection interrupted.\n");
					}
					taResult.setText(taResult.getText() + "Classifier Selection completed.\n");
				}
			}
		});
		
		trnPanel.add(btnTrain, BorderLayout.NORTH);
		trnPanel.add(taResult, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(dbAddrPanel, BorderLayout.NORTH);
		add(trnPanel, BorderLayout.CENTER);
	}
}
