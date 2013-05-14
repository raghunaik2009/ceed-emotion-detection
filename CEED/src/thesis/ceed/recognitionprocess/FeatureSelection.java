package thesis.ceed.recognitionprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import thesis.ceed.server.Server;
import thesis.ceed.server.ui.TrainingPanel;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.filters.Filter;

public class FeatureSelection {
	public static final String SELECTED_ARFF_PATH_GER = Server.WORKING_DIR + "GER\\GER.arff";
	public static final String SELECTED_ATT_PATH_GER = Server.WORKING_DIR + "GER\\GER" + Server.ATT_EXTENSION;
	public static final String SELECTED_ARFF_PATH_VIE = Server.WORKING_DIR + "VIE\\VIE.arff";
	public static final String SELECTED_ATT_PATH_VIE = Server.WORKING_DIR + "VIE\\VIE" + Server.ATT_EXTENSION;
	
	public static Boolean selectFeature(String arffFilePath, String lang) {
		String selectedArffFilePath = Server.WORKING_DIR + lang + "\\" + lang + Server.ARFF_EXTENSION,
				selectedAttFilePath = Server.WORKING_DIR + lang + "\\" + lang + Server.ATT_EXTENSION;
		Instances filteredData = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(arffFilePath));
			Instances data = new Instances(reader);
			reader.close();
			
			weka.filters.supervised.attribute.AttributeSelection attributeSelection = new weka.filters.supervised.attribute.AttributeSelection();
			CfsSubsetEval eval = new CfsSubsetEval();
			BestFirst search = new BestFirst();
			attributeSelection.setEvaluator(eval);
			attributeSelection.setSearch(search);	
			attributeSelection.setInputFormat(data);
			filteredData = Filter.useFilter(data, attributeSelection);			
			//System.out.println(filteredData);
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedArffFilePath));
			writer.write(filteredData.toString());
			writer.flush();
			writer.close();
			TrainingPanel.outText("Feature Selection: Arff file with selected features of the language " + lang + " created.\n");
			
			AttributeSelection attset = new AttributeSelection();
			attset.setEvaluator(eval);
			attset.setSearch(search);
			attset.SelectAttributes(data);		
			int[] indexes = attset.selectedAttributes();
			BufferedWriter attFileWriter = new BufferedWriter(new FileWriter(selectedAttFilePath));
			for (int i = 0; i < indexes.length; i++){
				attFileWriter.write(String.valueOf(indexes[i]));
				attFileWriter.write("\n");
			}
			attFileWriter.flush();
			attFileWriter.close();
			TrainingPanel.outText("Feature Selection: File containing list of indexes of selected features of the language " + lang + " created.\n"
								+ "Feature Selection completed.\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			TrainingPanel.outText(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			TrainingPanel.outText(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			TrainingPanel.outText(e.getMessage());
		}
		if (filteredData.numAttributes() != 0) return true;
		else return false;
	}
}
