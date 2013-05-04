package thesis.ceed.server.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import thesis.ceed.classifiers.CeedClassifier;
import thesis.ceed.recognitionprocess.ClassifierSelection;
import thesis.ceed.recognitionprocess.FeatureExtraction;
import thesis.ceed.recognitionprocess.FeatureSelection;
import thesis.ceed.server.Attempt;
import thesis.ceed.server.ServerProcessThread;
import thesis.ceed.utils.NotifyingThread;
import thesis.ceed.utils.ThreadCompleteListener;
import weka.core.Instance;
import weka.core.Instances;

public class TrainingPanel extends JPanel implements ThreadCompleteListener {

	private static final long serialVersionUID = -2489116449487103041L;
	
	private JTextField txtDbAddr;
	private JButton btnOpenDbFolder, btnTrain, btnRun;
	private JTextArea taResult;
	private JPanel dbAddrPanel, testPanel, trnPanel;
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
		fileChooser.setCurrentDirectory(new File("E:\\E2.Doing\\Samsung\\wav"));
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
		
		testPanel = new JPanel(new BorderLayout());
		
		final JTextField testTxtDbAddr = new JTextField();
		JButton testBtnOpenDbFolder = new JButton("Open File");
		//btnOpenDbFolder.setToolTipText("Open database folder containing sounds to train");
		final JFileChooser testFileChooser = new JFileChooser();
		//fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		testFileChooser.setCurrentDirectory(new File("D:\\CEED\\GER\\Sound"));
		testFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		testBtnOpenDbFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (testFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					testTxtDbAddr.setText(testFileChooser.getSelectedFile().getPath());
				}
			}
		});
		
		testPanel.add(testBtnOpenDbFolder, BorderLayout.WEST);
		testPanel.add(testTxtDbAddr, BorderLayout.CENTER);
		
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
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				processSound(testTxtDbAddr.getText());
			}
		});
		
		pnl.add(btnTrain);
		pnl.add(btnRun);
		JPanel lowerPanel = new JPanel(new BorderLayout());
		lowerPanel.add(testPanel, BorderLayout.NORTH);
		lowerPanel.add(taResult, BorderLayout.CENTER);
		trnPanel.add(pnl, BorderLayout.NORTH);
		trnPanel.add(lowerPanel, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(dbAddrPanel, BorderLayout.NORTH);
		add(trnPanel, BorderLayout.CENTER);
	}
	
	private Boolean processSound(String filePath){
		//Attempt newAttempt = new Attempt(clientIMEI, filePath, "", recordTime);
		String filteredArffFilePath = FeatureExtraction.extractFeature(filePath);
		
		// Classifier
		String emotion = null;
		int emoCodeInt = 0;
		try {
			FileReader fr = new  FileReader(ClassifierSelection.CLASSIFIER_PATH);
			BufferedReader clsFileReader = new BufferedReader(fr);
			int clsCode = Integer.parseInt(clsFileReader.readLine());
			clsFileReader.close();
			Instances trainingData = new Instances(new BufferedReader(new FileReader(FeatureSelection.SELECTED_ARFF_PATH)));
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			Instance instance = (new Instances(new BufferedReader(new FileReader(filteredArffFilePath)))).firstInstance();
			Double emoCode = CeedClassifier.classify(clsCode, trainingData, false, instance);
			emoCodeInt = emoCode.intValue();
			switch (emoCodeInt) {
			case 0:
				emotion = "Anger";
				break;
			case 1:
				emotion = "Boredom";
				break;
			case 2:
				emotion = "Disgust";
				break;
			case 3:
				emotion = "Fear";
				break;
			case 4:
				emotion = "Happiness";
				break;
			case 5:
				emotion = "Sadness";
				break;
			case 6:
				emotion = "Neutral";
				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Update Attempt
		//newAttempt.setEmotion(emotion);
		
		//save Attempt to DB
		//dbHelper.addAttempt(newAttempt);
		
		//sendResult(String.valueOf(emoCodeInt) + "\n");
		
		taResult.setText(taResult.getText() + filePath + ": " + emotion);
		return true;
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
