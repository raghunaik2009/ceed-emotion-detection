package thesis.ceed.recognitionprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import thesis.ceed.server.Server;
import thesis.ceed.server.ui.ServerWindow;
import weka.core.Instances;

public class FeatureExtraction {
	private static String command;
	private static final String PRAAT_PATH = Server.WORKING_DIR + "praatcon.exe";
	private static final String SCRIPT_FOR_FILE_PATH = Server.WORKING_DIR + "file.praat";
	private static final String SCRIPT_FOR_FOLDER_PATH = Server.WORKING_DIR + "folder.praat";
	//private static String SCRIPT_FOR_FOLDER_PATH = Server.WORKING_DIR + "folder-";
	private static final String FULL_ARFF_EXTENSION = "_full" + Server.ARFF_EXTENSION;
	private static final String FILTERED_ARFF_EXTENSION = "_filtered" + Server.ARFF_EXTENSION;
	
	public static String FULL_ARFF_PATH_GER = Server.WORKING_DIR + "GER\\GER" + FULL_ARFF_EXTENSION;
	public static String FULL_ARFF_PATH_VIE = Server.WORKING_DIR + "VIE\\VIE" + FULL_ARFF_EXTENSION;
	
	public static String extractFeature(String path, String lang) {		
		//File instance for testing whether receiving a file or folder
		File testPath = new File(path);	
		
		try {
			//command = "D:\\praatcon.exe D:\\Eg_FeatureExtraction.praat \""+path+"\\ " + isTraining;
			if (testPath.isDirectory()) {
				//command = PRAAT_PATH + " " + SCRIPT_FOR_FOLDER_PATH + lang + PRAAT_EXTENSION + " \"" + path + "\\";
				command = PRAAT_PATH + " " + SCRIPT_FOR_FOLDER_PATH + " \"" + path + "\\ " + lang;
				Process p = Runtime.getRuntime().exec(command);
				p.waitFor();
				
				ServerWindow.log("Feature Extraction with " + lang + " speech database completed.\n");
				return (Server.WORKING_DIR + lang + "\\" + lang + FULL_ARFF_EXTENSION);
			} else if (testPath.isFile()) {
				int indexOfDot = path.indexOf('.');
				String filteredArffFilePath = path.substring(0, indexOfDot) + FILTERED_ARFF_EXTENSION;
				String langPraatPath = Server.WORKING_DIR + lang + "\\" + lang + Server.PRAAT_EXTENSION;
				if (new File(langPraatPath).exists()) {
					command = PRAAT_PATH + " " + langPraatPath + " \"" + path;
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
				} else {
					String fullArffFilePath = path.substring(0, indexOfDot) + Server.ARFF_EXTENSION;
					command = PRAAT_PATH + " " + SCRIPT_FOR_FILE_PATH + " \"" + path;
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
					
					//Reconstruct list of selected attr of this database from .att file
					String selectedAttFilePath = null;
					if (lang.equals("GER"))
						selectedAttFilePath = FeatureSelection.SELECTED_ATT_PATH_GER;
					else if (lang.equals("VIE"))
						selectedAttFilePath = FeatureSelection.SELECTED_ATT_PATH_VIE;
					FileReader fr = new FileReader(selectedAttFilePath);
					BufferedReader attFileReader = new BufferedReader(fr);
					ArrayList<Integer> indexes = new ArrayList<Integer>();
					String line = attFileReader.readLine();
					while(line != null){
						int temp = Integer.parseInt(line);
						indexes.add(temp);
						line = attFileReader.readLine();
					}
					attFileReader.close();
					
					//filter full attr. file with newly created array of selected attr.
					Instances fullInstances = new Instances(new BufferedReader(new FileReader(fullArffFilePath)));
					for (int i = fullInstances.numAttributes() - 2; i >= 0; i--) {
						if (!indexes.contains(Integer.valueOf(i)))
							fullInstances.deleteAttributeAt(i);
					}
					
					BufferedWriter writer = new BufferedWriter(new FileWriter(filteredArffFilePath));
					writer.write(fullInstances.toString());
					writer.flush();
					writer.close();
				}
				
				ServerWindow.log("Feature Extraction with speech file completed.\n");
				return filteredArffFilePath;
			}
		} catch (IOException e) {
			e.printStackTrace();
			ServerWindow.log("Feature Extraction: " + e.getMessage() + "\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
			ServerWindow.log("Feature Extraction: " + e.getMessage() + "\n");
		}
		return null;
	}
}
