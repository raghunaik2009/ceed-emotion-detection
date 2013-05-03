package thesis.ceed.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

import thesis.ceed.recognitionprocess.FeatureExtraction;
import thesis.ceed.recognitionprocess.FeatureSelection;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
public class ServerProcessThread extends Thread{
	//socket to interact with client
	private Socket socket;
	//IMEI number of client
	private String clientIMEI;
	private int clientIndex;
	private String currentSoundPathOnServer = null;
	private BufferedReader inStream;
	private OutputStreamWriter outStream;
	private static char RECEIVE_SOUND_TOKEN = '#';
	
	
	public ServerProcessThread(Socket socket, int clientNumber){
		this.socket = socket;
		this.clientIndex = clientNumber;
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
		String filePath = null;
		
		//Pattern:  #IMEI##SoundName###FileSize####data
		int IMEIPos = clientData.indexOf("#") + 1;
		int namePos = clientData.indexOf("##") + 2;//2 is the lenght of ##
		int fileSizePos = clientData.indexOf("###") + 3;//3 is the lenght of ###
		int dataPos = clientData.indexOf("####") + 4;//4 is the lenght of ####
		//Get sound info from client data stream
		clientIMEI = clientData.substring(IMEIPos, namePos);
		String soundName = clientData.substring(namePos, dataPos); 
		int fileSize = Integer.parseInt(clientData.substring(fileSizePos, dataPos ));
		String soundData = clientData.substring(dataPos);
		
		//Create directory if not existed to each client using IMEI number
		File newDir = new File("D:\\"+clientIMEI);
		if(!newDir.exists())
			newDir.mkdir();
		//Save sound file into corresponding client
		File savedFile = new File(newDir.getAbsolutePath()+soundName +".wav");
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
	
	private Boolean processDB(){
		return true;
	}
	
	private Boolean processSound(){
		Boolean isSuccess = false;
		Attempt newAttempt = new Attempt(clientIMEI, currentSoundPathOnServer, "", new Date());
		String arffFilePath = FeatureExtraction.extractFeature(currentSoundPathOnServer);
		FeatureSelection.selectFeature(arffFilePath);
		//TODO: Classifier
		//TODO: update Attempt
		//TODO: save Attempt to DB
		sendResult(newAttempt.getEmotion());
		return true;
	}
	
	
}
