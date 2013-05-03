package thesis.ceed.recognitionprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import weka.core.Instances;

public class FeatureExtraction {
	private static String command;
	private static String PRAAT_PATH = "D:\\CEED\\praatcon.exe";
	private static String SCRIPT_FOR_FILE_PATH = "D:\\CEED\\file.praat";
	private static String SCRIPT_FOR_FOLDER_PATH = "D:\\CEED\\folder.praat";
	public static String FULL_ARFF_PATH = "D:\\CEED\\GER\\GER-full.arff";	
	
	public static String extractFeature(String path){
		
		//File instance for testing whether receiving a file or folder
		File testPath = new File(path);	
		
		try {
			//command = "D:\\praatcon.exe D:\\Eg_FeatureExtraction.praat \""+path+"\\ " + isTraining;
			if(testPath.isDirectory()){
				command = PRAAT_PATH + " " + SCRIPT_FOR_FOLDER_PATH+ " \""+path+"\\";
				Process p = Runtime.getRuntime().exec(command);
				//p.getOutputStream()
				p.waitFor();
				return FULL_ARFF_PATH;
			}
			if(testPath.isFile()){
				int indexOfDot = path.indexOf('.');
				String fullArffFilePath = path.substring(0, indexOfDot) + ".arff";
				command = PRAAT_PATH + " " + SCRIPT_FOR_FILE_PATH+ " \""+path;
				Process p = Runtime.getRuntime().exec(command);
				//p.getOutputStream()
				p.waitFor();
				
				//Reconstruct list of selected attr of this database from .att file
				FileReader fr = new  FileReader(FeatureSelection.SELECTED_ATT_PATH);
				BufferedReader attFileReader = new BufferedReader(fr);
				ArrayList<Integer> index = new ArrayList<Integer>();
				/*//int numberOfAtt = 0;
				while(attFileReader.readLine() != null){
					int temp = Integer.parseInt(attFileReader.readLine());
					//numberOfAtt++;
					index.add(temp);
				}
				attFileReader.close();*/
				
				
				index.add(14);
				index.add(15);
				index.add(20);
				index.add(22);
				index.add(27);
				index.add(29);
				index.add(30);
				index.add(32);
				index.add(33);
				index.add(34);
				index.add(36);
				index.add(43);
				index.add(46);
				index.add(58);
				index.add(59);
				index.add(67);
				index.add(68);
				index.add(71);
				index.add(74);
				index.add(77);
				index.add(79);
				index.add(82);
				index.add(87);
				index.add(89);
				index.add(93);
				index.add(94);
				index.add(97);
				index.add(99);
				index.add(102);
				
				//filter full attr. file with newly created array of selected attr.
				Instances instance = new Instances(new BufferedReader(new FileReader(fullArffFilePath)));
				for(int i = instance.numAttributes() - 2; i >=0 ; i --){
					if(!index.contains(Integer.valueOf(i)))
						instance.deleteAttributeAt(i);
				}
				
				String filteredArffFilePath = path.substring(0, indexOfDot) +"_filtered.arff";
				BufferedWriter writer = new BufferedWriter(new FileWriter(filteredArffFilePath));
				writer.write(instance.toString());
				writer.flush();
				writer.close();	
				
				return filteredArffFilePath;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error while extracting sound features";
	}
}
