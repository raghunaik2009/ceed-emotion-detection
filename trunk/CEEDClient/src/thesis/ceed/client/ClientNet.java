package thesis.ceed.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Base64;

public class ClientNet {
	public static final String SERVER_IP = "192.168.173.1";
	public static final int SERVER_PORT = 7010;
	
	static Socket clientSocket;
	static BufferedReader fromServer;
	//static DataOutputStream outToServer;
	static OutputStreamWriter outToServer;
	public static void connect() throws UnknownHostException, IOException {
		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	clientSocket = new Socket(SERVER_IP, SERVER_PORT);
		    		outToServer = new OutputStreamWriter(new DataOutputStream(clientSocket.getOutputStream()));
		    		fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		});
		thread.start();		
	}
	
	public static void send(File file) throws IOException {
		int fileSize = (int)file.length();
		
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bufferFileInputStream = new BufferedInputStream(fis);
		
		byte[] data = new byte[fileSize];
		bufferFileInputStream.read(data, 0, fileSize);
		//fis.read(data);
		//fis.close();
		String encoded = Base64.encodeToString(data, 0);
		String imei = CEEDClient.telephony.getDeviceId();
		String time = String.valueOf(System.currentTimeMillis());
		String toServer = "#" + imei + "##" + time + "###" + fileSize + "####" + encoded + "\n";
		outToServer.write(toServer);
		fis.close();
	}
	
	public static String receiveResult() {
		String emotion = null;
		try {
			emotion = fromServer.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return emotion;
	}
	
	public static void disconnect() throws IOException {
		fromServer.close();
		outToServer.close();
		clientSocket.close();
	}
}
