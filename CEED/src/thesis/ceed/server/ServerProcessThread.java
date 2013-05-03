package thesis.ceed.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import thesis.ceed.classifiers.CeedClassifier;
import thesis.ceed.recognitionprocess.ClassifierSelection;
import thesis.ceed.recognitionprocess.FeatureExtraction;
import thesis.ceed.recognitionprocess.FeatureSelection;
import weka.core.Instance;
import weka.core.Instances;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class ServerProcessThread extends Thread {
	//socket to interact with client
	private Socket socket;
	//IMEI number of client
	private String clientIMEI;
	private String recordTime;
	private int clientIndex;
	private String currentSoundPathOnServer = null;
	private BufferedReader inStream;
	private OutputStreamWriter outStream;
	private ServerDbHelper dbHelper;
	
	public ServerProcessThread(Socket socket, int clientNumber){
		this.socket = socket;
		this.clientIndex = clientNumber;
		dbHelper = new ServerDbHelper();
	}
	
	@Override
	public void run() {
		super.run();
		getIOStreams();
		processIOStreams();
	}
	
	public void getIOStreams(){
		try {			
			//Use bufferedReader to make use of its readLine(): String method because we use String analysis as protocol
			inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outStream = new OutputStreamWriter(new DataOutputStream(socket.getOutputStream()));
		} catch (IOException e) {	
			e.printStackTrace();
		}		
	}
	
	public void processIOStreams(){
		try {
			String dataFromClient = inStream.readLine();
			currentSoundPathOnServer = receiveSound(dataFromClient);
			processSound();			
		} catch (IOException e) {	
			e.printStackTrace();
		}
	}
	
	private String receiveSound(String clientData){
		//Pattern:  #IMEI##Time###fileSize####data\n
		int IMEIPos = clientData.indexOf("#") + 1;
		int timePos = clientData.indexOf("##") + 2;//2 is the length of ##
		int fileSizePos = clientData.indexOf("###") + 3;//3 is the length of ###
		int dataPos = clientData.indexOf("####") + 4;//4 is the length of ####
		//Get sound info from client data stream
		clientIMEI = clientData.substring(IMEIPos, timePos -2);
		recordTime = clientData.substring(timePos, fileSizePos -3); 
		int fileSize = Integer.parseInt(clientData.substring(fileSizePos, dataPos-4));
		String soundData = clientData.substring(dataPos);
		
		//Create directory if not existed to each client using IMEI number
		File newDir = new File("D:\\CEED\\" + "GER\\Sound\\");
		if(!newDir.exists())
			newDir.mkdirs();
		//Save sound file into corresponding client
		File savedFile = new File(newDir.getAbsolutePath() + clientIMEI + "_" + recordTime +".wav");
		byte[] temp = new byte[fileSize];
		//decode data back from String to byte[]
		temp = Base64.decode(soundData);
		
		try {
			FileOutputStream fos = new FileOutputStream(savedFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);			
			bos.write(temp, 0, fileSize);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return savedFile.getAbsolutePath();
	}
	
	private Boolean sendResult(String emoString){
		String emoToClient = null;
		emoToClient += emoString;
		try {
			outStream.write(emoToClient);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	private Boolean processSound(){
		Attempt newAttempt = new Attempt(clientIMEI, currentSoundPathOnServer, "", recordTime);
		String filteredArffFilePath = FeatureExtraction.extractFeature(currentSoundPathOnServer);
		
		// Classifier
		String emotion = null;
		int emoCodeInt = 0;
		try {
			FileReader fr = new  FileReader(ClassifierSelection.CLASSIFIER_PATH);
			BufferedReader clsFileReader = new BufferedReader(fr);
			int clsCode = Integer.parseInt(clsFileReader.readLine());
			clsFileReader.close();
			Instances trainingData = new Instances(new BufferedReader(new FileReader(FeatureSelection.SELECTED_ARFF_PATH)));
			Instance instance = (new Instances(new BufferedReader(new FileReader(filteredArffFilePath)))).firstInstance();
			Double emoCode = CeedClassifier.classify(clsCode, trainingData, false, instance);
			emoCodeInt = emoCode.intValue();
			switch (emoCodeInt) {
			case 0:
				emotion = "Anger";
				break;
			case 1:
				emotion = "Boredom";
				break;
			case 2:
				emotion = "Disgust";
				break;
			case 3:
				emotion = "Fear";
				break;
			case 4:
				emotion = "Happiness";
				break;
			case 5:
				emotion = "Sadness";
				break;
			case 6:
				emotion = "Neutral";
				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Update Attempt
		newAttempt.setEmotion(emotion);
		
		//save Attempt to DB
		dbHelper.addAttempt(newAttempt);
		
		sendResult(String.valueOf(emoCodeInt) + "\n");
		return true;
	}	
}
