package thesis.ceed.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Base64;

public class ClientNet {
	public static final String SERVER_IP = "192.168.1.2";
	public static final int SERVER_PORT = 7010;
	
	static Socket clientSocket;
	static BufferedReader fromServer;
	static DataOutputStream outToServer;
	
	public static void connect() throws UnknownHostException, IOException {
		clientSocket = new Socket(SERVER_IP, SERVER_PORT);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public static void send(File file) throws IOException {
		byte[] data = new byte[1048576];
		FileInputStream fis = new FileInputStream(file);
		fis.read(data);
		fis.close();
		String encoded = Base64.encodeToString(data, 0);
		String imei = CEEDClient.telephony.getDeviceId();
		String time = String.valueOf(System.currentTimeMillis());
		String toServer = "#" + imei + "##" + time + "###" + encoded.length() + "####" + encoded + "\n";
		outToServer.writeBytes(toServer);
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
