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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import thesis.ceed.server.Server;
import thesis.ceed.trainingprocess.ClassifierSelection;
import thesis.ceed.trainingprocess.FeatureExtraction;
import thesis.ceed.trainingprocess.FeatureSelection;
import thesis.ceed.utils.NotifyingThread;
import thesis.ceed.utils.ThreadCompleteListener;
import weka.core.Instance;
import weka.core.Instances;

public class TrainingPanel extends JPanel implements ThreadCompleteListener {

	private static final long serialVersionUID = -2489116449487103041L;
	
	private JLabel lblDbAddr, lblTestFileAddr;
	private JTextField txtDbAddr, txtTestFileAddr;
	private JButton btnOpenDbFolder, btnOpenTestFile, btnTrain, btnTest;
	private JFileChooser fcDatabase, fcFile;
	private JComboBox<Object> cbLanguage;
	private JPanel pnlTrain, pnlTest;
	private NotifyingThread threadTraining;
	private Object[] languages = new Object[] {"GER", "VIE"};
	
	private void setEnableTrain(boolean enable) {
		btnTrain.setEnabled(enable);
		btnOpenDbFolder.setEnabled(enable);
		cbLanguage.setEnabled(enable);
	}
	
	private void setEnableTest(boolean enable) {
		btnTest.setEnabled(enable);
		btnOpenTestFile.setEnabled(enable);
	}
	
	public TrainingPanel() {
		// Train Panel - pnlTrain
		lblDbAddr = new JLabel("Database folder path: ");
		
		txtDbAddr = new JTextField();
		//txtDbAddr.setText("E:\\E2.Doing\\BK\\TN\\wavZ");
		
		btnOpenDbFolder = new JButton("Open Database");
		btnOpenDbFolder.setToolTipText("Open database folder containing sounds to train");
		fcDatabase = new JFileChooser();
		fcDatabase.setCurrentDirectory(new File(Server.WORKING_DIR));
		//fcDatabase.setCurrentDirectory(new File("E:\\E2.Doing\\BK\\TN\\wavZ"));
		fcDatabase.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		btnOpenDbFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (fcDatabase.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					txtDbAddr.setText(fcDatabase.getSelectedFile().getPath());
				}
			}
		});
		
		btnTrain = new JButton("Train");
		btnTrain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File dbFolder = new File(txtDbAddr.getText());
				if (dbFolder.isDirectory()) {
					setEnableTrain(false);
					//FeatureExtraction.extractFeature(txtDbAddr.getText(), (String) cbLanguage.getSelectedItem());
					threadTraining = new NotifyingThread() {
						public void doRun() {
							FeatureExtraction.extractFeature(txtDbAddr.getText(), (String) cbLanguage.getSelectedItem());
							if ( ((String) cbLanguage.getSelectedItem()).equals("GER") ) {
								FeatureSelection.selectFeature(FeatureExtraction.FULL_ARFF_PATH_GER, "GER");
								ClassifierSelection.selectClassifier(FeatureSelection.SELECTED_ARFF_PATH_GER, "GER");
							} else if ( ((String) cbLanguage.getSelectedItem()).equals("VIE") ) {
								FeatureSelection.selectFeature(FeatureExtraction.FULL_ARFF_PATH_VIE, "VIE");
								ClassifierSelection.selectClassifier(FeatureSelection.SELECTED_ARFF_PATH_VIE, "VIE");
							}
						}
					};
					threadTraining.addListener(TrainingPanel.this);
					threadTraining.start();
				}
			}
		});
		
		cbLanguage = new JComboBox<Object>();
		cbLanguage.setModel(new DefaultComboBoxModel<Object>(languages));
		
		JPanel pnlDbAddr = new JPanel(new BorderLayout());
		pnlDbAddr.add(lblDbAddr, BorderLayout.WEST);
		pnlDbAddr.add(txtDbAddr, BorderLayout.CENTER);
		JPanel pnlTrainControl = new JPanel(new FlowLayout());
		pnlTrainControl.add(cbLanguage);
		pnlTrainControl.add(btnOpenDbFolder);
		pnlTrainControl.add(btnTrain);
		pnlTrain = new JPanel(new BorderLayout());
		pnlTrain.add(pnlDbAddr, BorderLayout.NORTH);
		pnlTrain.add(pnlTrainControl, BorderLayout.CENTER);
		
		// Test Panel - pnlTest
		lblTestFileAddr = new JLabel("File path: ");
		
		txtTestFileAddr = new JTextField();
		//txtTestFileAddr.setText("D:\\CEED\\GER\\Sound\\353904050257969_F.wav");
		
		btnOpenTestFile = new JButton("Open File");
		fcFile = new JFileChooser();
		fcFile.setCurrentDirectory(new File(Server.WORKING_DIR));
		fcFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		btnOpenTestFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (fcFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					txtTestFileAddr.setText(fcFile.getSelectedFile().getPath());
				}
			}
		});
		
		btnTest = new JButton("Run Test");
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEnableTest(false);
				processSound(txtTestFileAddr.getText(), (String) cbLanguage.getSelectedItem());
				setEnableTest(true);
			}
		});
		
		JPanel pnlTestFileAddr = new JPanel(new BorderLayout());
		pnlTestFileAddr.add(lblTestFileAddr, BorderLayout.WEST);
		pnlTestFileAddr.add(txtTestFileAddr, BorderLayout.CENTER);
		JPanel pnlTestControl = new JPanel(new FlowLayout());
		pnlTestControl.add(btnOpenTestFile);
		pnlTestControl.add(btnTest);
		pnlTest = new JPanel(new BorderLayout());
		pnlTest.add(pnlTestFileAddr, BorderLayout.NORTH);
		pnlTest.add(pnlTestControl, BorderLayout.CENTER);
		
		// Training Panel
		setLayout(new BorderLayout());
		add(pnlTrain, BorderLayout.NORTH);
		add(pnlTest, BorderLayout.CENTER);
	}
	
	private Boolean processSound(String filePath, String lang) {
		String filteredArffFilePath = FeatureExtraction.extractFeature(filePath, lang);
		
		// Classifier
		String emotion = null;
		int emoCodeInt = 0;
		try {
			Instance instance = new Instances(new BufferedReader(new FileReader(filteredArffFilePath))).firstInstance();
			Double emoCode = 0.0;
			if (lang.equals("GER"))
				emoCode = Server.clsGer.classifyInstance(instance);
			else if (lang.equals("VIE"))
				emoCode = Server.clsVie.classifyInstance(instance);
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
			
			ServerWindow.log("Test: " + filePath + " - " + emotion + "\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ServerWindow.log(e.getMessage() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			ServerWindow.log(e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			ServerWindow.log(e.getMessage() + "\n");
		}
		
		return true;
	}

	@Override
	public void notifyOfThreadComplete(Thread thread) {
		if (thread.getName().equals(threadTraining.getName()))
			setEnableTrain(true);
	}
}
