package thesis.ceed.recognitionprocess;
import java.io.IOException;


public class FeatureExtraction {
	public String extractFeature(String path){
		String command;
		int isTraining;
		
		if(path.endsWith(".wav")) isTraining = 0;
		else isTraining = 1;
		try {
			command = "C:\\praatcon.exe C:\\Eg_FeatureExtraction.praat \""+path+"\\" + isTraining;
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
		if(isTraining == 1) return "C:\\test.arff";
		else return "C:\\training.afrr";
	}
}
