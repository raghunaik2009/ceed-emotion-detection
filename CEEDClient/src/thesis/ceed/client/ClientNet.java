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
import thesis.ceed.utils.*;

public class ClientNet {
	public static final String SERVER_IP = "192.168.137.1";
	public static final int SERVER_PORT = 7010;
	
	static Socket clientSocket;
	static BufferedReader fromServer;
	//static DataOutputStream outToServer;
	static OutputStreamWriter outToServer;
	public static void connect() {
		new Thread() {
			public void run() {
				try {
					clientSocket = new Socket(SERVER_IP, SERVER_PORT);
					outToServer = new OutputStreamWriter(new DataOutputStream(clientSocket.getOutputStream()));
					fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static void send(File file, String lang) throws IOException {
		int fileSize = (int)file.length();
		
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bufferFileInputStream = new BufferedInputStream(fis);
		
		byte[] data = new byte[fileSize];
		bufferFileInputStream.read(data, 0, fileSize);
		fis.close();

		String encoded = Base64.encode(data);
		String imei = CEEDClient.telephony.getDeviceId();
		String time = file.getName();
		time = time.substring(0, time.lastIndexOf(".wav"));
		String toServer = lang + "#" + imei + "##" + time + "###" + fileSize + "####" + encoded;
		outToServer.write(toServer);
		outToServer.write(13);
		outToServer.write(10);
		outToServer.flush();
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
