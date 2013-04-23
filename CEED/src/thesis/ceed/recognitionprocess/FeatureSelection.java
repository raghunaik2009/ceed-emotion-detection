package thesis.ceed.recognitionprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.filters.Filter;


public class FeatureSelection {
	public Boolean selectFeature(String arffFilePath){
		Instances newData = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("C:\\training.arff"));
			Instances data = new Instances(reader);
			reader.close();
			
			weka.filters.supervised.attribute.AttributeSelection attributeSelection = new weka.filters.supervised.attribute.AttributeSelection();
			CfsSubsetEval eval  = new CfsSubsetEval();
			BestFirst search = new BestFirst();
			
			attributeSelection.setEvaluator(eval);
			attributeSelection.setSearch(search);
			
			//attributeSelection.SelectAttributes(data);
			attributeSelection.setInputFormat(data);
			newData = Filter.useFilter(data, attributeSelection);
			System.out.println(newData);
			BufferedWriter writer = new BufferedWriter( new FileWriter("C:\\test\\selectedFeature.arff"));
			writer.write(newData.toString());
			writer.flush();
			writer.close();			
			
			
			//int[] index = attributeSelection.selectedAttributes();
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
