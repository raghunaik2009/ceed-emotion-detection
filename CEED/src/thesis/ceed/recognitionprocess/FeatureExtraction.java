package thesis.ceed.recognitionprocess;
import java.io.IOException;


public class FeatureExtraction {
	private static String command;
	private static String PRAAT_PATH = "D:\\CEED\\praat.exe";
	private static String SCRIPT_FILE_PATH = "D:\\CEED\\Eg_FeatureExtraction.praat";
	private static String TRAINING_ARFF_FOLDER_PATH = "D:\\CEED\\Training";
	private static String TEST_ARFF_FOLDER_PATH = "D:\\CEED\\GER-Arff";
	public static String extractFeature(String path){
		
		int isTraining;
		
		if(path.endsWith(".wav")) isTraining = 0;
		else isTraining = 1;
		try {
			//command = "D:\\praatcon.exe D:\\Eg_FeatureExtraction.praat \""+path+"\\ " + isTraining;
			command = PRAAT_PATH +" "+ SCRIPT_FILE_PATH+ " \""+path+"\\ " + isTraining;
			Process p = Runtime.getRuntime().exec(command);
			//p.getOutputStream()
			p.waitFor();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(isTraining == 1) return TRAINING_ARFF_FOLDER_PATH+"\\GER-full.arff";
		else return TEST_ARFF_FOLDER_PATH+"\\"+path;
	}
}
