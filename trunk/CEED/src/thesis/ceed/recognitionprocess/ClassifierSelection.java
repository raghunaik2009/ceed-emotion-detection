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
	public static final String CLASSIFIER_STORE = "DE_CLSFR.ceed";
	
	public static boolean selectClassifier(String selectedAttArffFilePath) {
		TreeMap<Double, Integer> classifyingResult = new TreeMap<Double, Integer>();
		try {
			Instances trainingData = new Instances(new BufferedReader(new FileReader(selectedAttArffFilePath)));
			for (int i = 0; i < 63; i++) {
				Double pctCorrect = CeedClassifier.classify(i, trainingData, true, null);
				if (pctCorrect != null) {
					classifyingResult.put(pctCorrect, i);
				}
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(CLASSIFIER_STORE));
			writer.write(classifyingResult.lastEntry().getValue());
			writer.flush();
			writer.close();
			
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
