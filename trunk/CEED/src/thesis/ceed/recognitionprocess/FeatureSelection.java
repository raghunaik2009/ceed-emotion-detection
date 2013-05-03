package thesis.ceed.recognitionprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;


public class FeatureSelection {
	public static final String SELECTED_ARFF_PATH = "D:\\CEED\\Training\\GER.arff";
	public static final String SELECTED_ATT_PATH = "D:\\CEED\\Training\\GER.att";
	public static Boolean selectFeature(String arffFilePath){
		Instances newData = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(arffFilePath));
			Instances data = new Instances(reader);
			reader.close();
			
			weka.filters.supervised.attribute.AttributeSelection attributeSelection = new weka.filters.supervised.attribute.AttributeSelection();
			CfsSubsetEval eval  = new CfsSubsetEval();
			BestFirst search = new BestFirst();
			attributeSelection.setEvaluator(eval);
			attributeSelection.setSearch(search);	
			attributeSelection.setInputFormat(data);
			newData = Filter.useFilter(data, attributeSelection);			
			System.out.println(newData);
			BufferedWriter writer = new BufferedWriter(new FileWriter(SELECTED_ARFF_PATH));
			writer.write(newData.toString());
			writer.flush();
			writer.close();		
			
			AttributeSelection attset = new AttributeSelection();
			attset.setEvaluator(eval);
			attset.setSearch(search);
			attset.SelectAttributes(data);		
			int[] index = attset.selectedAttributes();
			BufferedWriter attFileWriter = new BufferedWriter(new FileWriter(SELECTED_ATT_PATH));
			for(int i = 0; i<index.length; i++){
				attFileWriter.write(String.valueOf(index[i]));
				attFileWriter.write("\n");
			}
			//attFileWriter.write(Utils.arrayToString(index));
			attFileWriter.flush();
			attFileWriter.close();
			//System.out.println(Utils.arrayToString(index));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(newData.numAttributes() != 0) return true;
		else return false;
	}
}
