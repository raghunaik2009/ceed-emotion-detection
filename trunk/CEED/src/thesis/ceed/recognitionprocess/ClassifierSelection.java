package thesis.ceed.recognitionprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import thesis.ceed.classifiers.CeedClassifier;
import weka.core.Instances;

public class ClassifierSelection {
	public static final String CLASSIFIER_PATH = "D:\\CEED\\GER\\GER.cls";
	
	public static boolean selectClassifier(String selectedAttArffFilePath) {
		TreeMap<Double, Integer> classifyingResult = new TreeMap<Double, Integer>();
		try {
			Instances trainingData = new Instances(new BufferedReader(new FileReader(selectedAttArffFilePath)));
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			for (int i = 0; i < 63; i++) {
				Double pctCorrect = CeedClassifier.classify(i, trainingData, true, null);
				if (pctCorrect != null) {
					classifyingResult.put(pctCorrect, i);
				}
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(CLASSIFIER_PATH));
			writer.write(classifyingResult.lastEntry().getValue().toString() + "\n");
			writer.flush();
			writer.close();
			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
