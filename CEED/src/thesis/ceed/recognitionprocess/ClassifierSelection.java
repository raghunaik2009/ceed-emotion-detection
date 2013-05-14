package thesis.ceed.recognitionprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import thesis.ceed.classifiers.CeedClassifier;
import thesis.ceed.server.Server;
import thesis.ceed.server.ui.TrainingPanel;
import weka.core.Instances;

public class ClassifierSelection {
	public static final String CLASSIFIER_PATH_GER = Server.WORKING_DIR + "GER\\GER" + Server.CLS_EXTENSION;
	public static final String CLASSIFIER_PATH_VIE = Server.WORKING_DIR + "VIE\\VIE" + Server.CLS_EXTENSION;
	
	public static boolean selectClassifier(String selectedAttArffFilePath, String lang) {
		TreeMap<Double, Integer> classifyingResult = new TreeMap<Double, Integer>();
		try {
			Instances trainingData = new Instances(new BufferedReader(new FileReader(selectedAttArffFilePath)));
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			for (int i = 0; i < 63; i++) {
				Double pctCorrect = CeedClassifier.evaluate(i, trainingData);
				if (pctCorrect != null) {
					classifyingResult.put(pctCorrect, i);
					TrainingPanel.outText("Classifier Selection with language " + lang + ": " + CeedClassifier.select(i).getClass().getSimpleName()
							+ " - " + pctCorrect.toString() + "%\n");
				}
			}
			
			Integer selectedClsIndex = classifyingResult.lastEntry().getValue();
			TrainingPanel.outText("Classifier Selection with language " + lang + ": "
					+ CeedClassifier.select(selectedClsIndex).getClass().getSimpleName() + " selected.\n");
			String clsPath = Server.WORKING_DIR + lang + "\\" + lang + Server.CLS_EXTENSION;
			BufferedWriter writer = new BufferedWriter(new FileWriter(clsPath));
			writer.write(selectedClsIndex.toString() + "\n");
			writer.flush();
			writer.close();
			
			TrainingPanel.outText("Classifier Selection with language " + lang + " completed.\n");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			TrainingPanel.outText(e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			TrainingPanel.outText(e.getMessage());
			return false;
		}
	}
}
