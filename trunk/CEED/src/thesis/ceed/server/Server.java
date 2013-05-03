package thesis.ceed.server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
public class Server {
	private static final String IPADDRESS = "192.168.1.111";
	private static final int PORT = 65;
	private ServerSocket serverSocket = null;
	//private ArrayList<Socket> clientList; 
	public static int numberOfClient = 0;
	public static String getIPAddress() {
		return IPADDRESS;
	}
	public static int getPort() {
		return PORT;
	}
	
	public Server(){
		//clientList = new ArrayList<Socket>();
		try {
			//Step 1: Create a ServerSocket to listen from Client
			ServerSocket serverSocket = new ServerSocket(getPort());
			
			while(true){
			//Step 2: wait for connection from a client
				//clientList.add(serverSocket.accept());
				//Socket newClientSocket = serverSocket.accept();
				numberOfClient++;
				new ServerProcessThread(serverSocket.accept(), numberOfClient).start();
				serverSocket.close();
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
		
	}
	 
	
	
	
	
	
	
}
