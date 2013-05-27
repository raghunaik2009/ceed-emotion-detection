package thesis.ceed.trainingprocess;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

import thesis.ceed.classifiers.CeedClassifier;
import thesis.ceed.server.Server;
import thesis.ceed.server.ui.ServerWindow;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class ClassifierSelection {
	public static final String CLASSIFIER_PATH_GER = Server.WORKING_DIR + "GER\\GER" + Server.CLS_EXTENSION;
	public static final String CLASSIFIER_PATH_VIE = Server.WORKING_DIR + "VIE\\VIE" + Server.CLS_EXTENSION;
	
	public static boolean selectClassifier(String selectedAttArffFilePath, String lang) {
		TreeMap<Double, Classifier> classifyingResult = new TreeMap<Double, Classifier>();
		try {
			Instances trainingData = new Instances(new BufferedReader(new FileReader(selectedAttArffFilePath)));
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			for (int i = 0; i < 64; i++) {
				Classifier cls = CeedClassifier.select(i);
				Double pctCorrect = CeedClassifier.evaluate(cls, trainingData);
				if (pctCorrect != null) {
					classifyingResult.put(pctCorrect, cls);
					ServerWindow.log("Classifier Selection with language " + lang + ": " + cls.getClass().getSimpleName()
							+ " - " + pctCorrect.toString() + "%\n");
				}
			}
			
			Classifier selectedCls = classifyingResult.lastEntry().getValue();
			ServerWindow.log("Classifier Selection with language " + lang + ": "
					+ selectedCls.getClass().getSimpleName() + " selected.\n");
			String clsPath = Server.WORKING_DIR + lang + "\\" + lang + Server.CLS_EXTENSION;
			ObjectOutputStream outputCls = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(clsPath)));
			try {
				outputCls.writeObject(selectedCls);
			} finally {
				outputCls.close();
			}
			
			ServerWindow.log("Classifier Selection with language " + lang + " completed.\n");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ServerWindow.log(e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			ServerWindow.log(e.getMessage());
			return false;
		}
	}
}
