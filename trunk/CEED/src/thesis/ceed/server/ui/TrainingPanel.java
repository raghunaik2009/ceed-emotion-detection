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
import javax.swing.JTextArea;
import javax.swing.JTextField;

import thesis.ceed.recognitionprocess.ClassifierSelection;
import thesis.ceed.recognitionprocess.FeatureExtraction;
import thesis.ceed.recognitionprocess.FeatureSelection;
import thesis.ceed.server.Server;
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
	private static JTextArea taResult;
	private JPanel pnlTrain, pnlTest;
	private NotifyingThread threadFeatureExtraction, threadFeatureSelection, threadClassifierSelection;
	private Object[] languages = new Object[] {"GER", "VIE"};
	
	public static void outText(String text) {
		taResult.append(text);
	}
	
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
		txtDbAddr.setText("E:\\E2.Doing\\Samsung\\wav");
		
		btnOpenDbFolder = new JButton("Open Database");
		btnOpenDbFolder.setToolTipText("Open database folder containing sounds to train");
		fcDatabase = new JFileChooser();
		//fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fcDatabase.setCurrentDirectory(new File("E:\\E2.Doing\\Samsung\\wav"));
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
					threadFeatureExtraction = new NotifyingThread() {
						public void doRun() {
							FeatureExtraction.extractFeature(txtDbAddr.getText(), (String) cbLanguage.getSelectedItem());
						}
					};
					threadFeatureSelection = new NotifyingThread() {
						public void doRun() {
							if ( ((String) cbLanguage.getSelectedItem()).equals("GER") )
								FeatureSelection.selectFeature(FeatureExtraction.FULL_ARFF_PATH_GER, "GER");
							else if ( ((String) cbLanguage.getSelectedItem()).equals("VIE") )
								FeatureSelection.selectFeature(FeatureExtraction.FULL_ARFF_PATH_VIE, "VIE");
						}
					};
					threadClassifierSelection = new NotifyingThread() {
						public void doRun() {
							if ( ((String) cbLanguage.getSelectedItem()).equals("GER") )
								ClassifierSelection.selectClassifier(FeatureSelection.SELECTED_ARFF_PATH_GER, "GER");
							else if ( ((String) cbLanguage.getSelectedItem()).equals("VIE") )
								ClassifierSelection.selectClassifier(FeatureSelection.SELECTED_ARFF_PATH_VIE, "VIE");
						}
					};
					threadFeatureExtraction.addListener(TrainingPanel.this);
					threadFeatureSelection.addListener(TrainingPanel.this);
					threadClassifierSelection.addListener(TrainingPanel.this);
					threadFeatureExtraction.start();
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
		pnlTrain.add(pnlTrainControl, BorderLayout.SOUTH);
		
		// Test Panel - pnlTest
		lblTestFileAddr = new JLabel("File path: ");
		
		txtTestFileAddr = new JTextField();
		txtTestFileAddr.setText("D:\\CEED\\GER\\Sound\\353904050257969_F.wav");
		
		btnOpenTestFile = new JButton("Open File");
		fcFile = new JFileChooser();
		fcFile.setCurrentDirectory(new File("D:\\CEED\\GER\\Sound"));
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
		pnlTest.add(pnlTestControl, BorderLayout.SOUTH);
		
		// Result Panel - pnlResult
		taResult = new JTextArea();
		
		// Training Panel
		JPanel panels = new JPanel(new BorderLayout());
		panels.add(pnlTrain, BorderLayout.NORTH);
		panels.add(pnlTest, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(panels, BorderLayout.NORTH);
		add(taResult, BorderLayout.CENTER);
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
			
			outText("Test: " + filePath + " - " + emotion + "\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			outText(e.getMessage() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			outText(e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			outText(e.getMessage() + "\n");
		}
		
		return true;
	}

	@Override
	public void notifyOfThreadComplete(Thread thread) {
		if (thread.getName().equals(threadFeatureExtraction.getName())) {
			threadFeatureSelection.start();
		} else if (thread.getName().equals(threadFeatureSelection.getName())) {
			threadClassifierSelection.start();
		} else if (thread.getName().equals(threadClassifierSelection.getName())) {
			setEnableTrain(true);
		}
	}
}
