package thesis.ceed.server;

import java.io.IOException;
import java.net.*;
import java.util.Date;

import thesis.ceed.server.ui.ServerWindow;

public class Server {
	private static final String IPADDRESS = "192.168.173.1";
	private static final int PORT = 7010;
	private static ServerSocket serverSocket = null;
	//private ArrayList<Socket> clientList; 
	public static int numberOfClient = 0;
	
	public static String getIPAddress() {
		return IPADDRESS;
	}
	
	public static int getPort() {
		return PORT;
	}
	
	public Server() {
		//clientList = new ArrayList<Socket>();
		
		new Thread() {
			public void run() {
				try {
					//Step 1: Create a ServerSocket to listen from Client
					serverSocket = new ServerSocket(PORT);
					System.out.println("Server Socket Created");
					while(true){
					//Step 2: wait for connection from a client
						//clientList.add(serverSocket.accept());
						//Socket newClientSocket = serverSocket.accept();
						numberOfClient++;
						if (!serverSocket.isClosed())
							new ServerProcessThread(serverSocket.accept(), numberOfClient).start();
							System.out.print("A client connected!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (serverSocket != null)
							serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public static void stopServer() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv) {
		ServerDbHelper dbHelper = new ServerDbHelper();
		dbHelper.createDatabase();
		dbHelper.createTables();
		dbHelper.addUser("3333");
		dbHelper.addUser("1111");
		dbHelper.addUser("2222");
		dbHelper.addUser("4444");
		dbHelper.addAttempt(new Attempt("1111", "aaaa", "Anger", String.valueOf(new Date(90, 11, 20).getTime())));
		dbHelper.addAttempt(new Attempt("2222", "bbbb", "Neutral", String.valueOf(new Date(99, 12, 23).getTime())));
		dbHelper.addAttempt(new Attempt("3333", "cccc", "Boredom", String.valueOf(new Date(109, 1, 25).getTime())));
		dbHelper.addAttempt(new Attempt("4444", "dddd", "Fear", String.valueOf(new Date(113, 7, 20).getTime())));
		
		new Server();
		new ServerWindow();
	}
}
