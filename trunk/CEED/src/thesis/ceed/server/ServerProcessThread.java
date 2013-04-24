package thesis.ceed.server;

import java.net.Socket;

public class ServerProcessThread extends Thread{
	//socket to interact with client
	private Socket socket;
	//IMEI number of client
	private String ClientIMEI;
	private int clientIndex;
	
	public ServerProcessThread(Socket socket, int clientNumber){
		this.socket = socket;
		this.clientIndex = clientNumber;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		getIOStreams();
		processIOStreams();
	}
	
	public void getIOStreams(){
		
	}
	
	public void processIOStreams(){
		
	}
	
	private String receiveSound(){
		String filePath = null;
		return filePath;
	}
	
	private Boolean sendResult(){
		return true;
	}
	
	private Boolean processDB(){
		return true;
	}
	
	private Boolean processSound(){
		return true;
	}
}
