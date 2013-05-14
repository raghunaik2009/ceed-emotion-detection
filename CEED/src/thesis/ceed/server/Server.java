package thesis.ceed.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

import thesis.ceed.classifiers.CeedClassifier;
import thesis.ceed.recognitionprocess.ClassifierSelection;
import thesis.ceed.recognitionprocess.FeatureSelection;
import thesis.ceed.server.ui.ServerWindow;
import thesis.ceed.server.ui.XPanel;
import thesis.ceed.utils.NotifyingThread;
import thesis.ceed.utils.ThreadCompleteListener;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class Server implements ThreadCompleteListener {
	private static final int PORT = 7010;

	public static final String WORKING_DIR = "D:\\CEED\\";
	public static final String PRAAT_EXTENSION = ".praat";
	public static final String ARFF_EXTENSION = ".arff";
	public static final String ATT_EXTENSION = ".att";
	public static final String CLS_EXTENSION = ".cls";

	public static ServerSocket serverSocket = null;
	public static int numberOfClient = 0;
	public static Classifier clsVie, clsGer;

	// private ArrayList<Socket> clientList;
	private static NotifyingThread threadCreateGer, threadCreateVie, threadCreateSocket;

	public static int getPort() {
		return PORT;
	}

	public Server() {
		// clientList = new ArrayList<Socket>();
		threadCreateGer = new NotifyingThread() {
			public void doRun() {
				createCls("GER");
			}
		};
		threadCreateVie = new NotifyingThread() {
			public void doRun() {
				createCls("VIE");
			}
		};
		threadCreateSocket = new NotifyingThread() {
			public void doRun() {
				try {
					// Step 1: Create a ServerSocket to listen from Client
					Server.serverSocket = new ServerSocket(Server.getPort());
					//System.out.println("Server Socket Created");
					XPanel.outText("Server socket created.\n");
					while (true) {
						// Step 2: wait for connection from a client
						// clientList.add(serverSocket.accept());
						// Socket newClientSocket = serverSocket.accept();
						// numberOfClient++;
						if (!Server.serverSocket.isClosed()) {
							// new ServerProcessThread(Server.serverSocket.accept(), numberOfClient).start();
							new ServerProcessThread(Server.serverSocket.accept()).start();
							//System.out.print("A client connected!");
							XPanel.outText("A client connected!\n");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					XPanel.outText("Create Socket: " + e.getMessage() + "\n");
				} finally {
					try {
						if (Server.serverSocket != null)
							Server.serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
						XPanel.outText("Create Socket: " + e.getMessage() + "\n");
					}
				}
			}
		};

		threadCreateGer.setPriority(Thread.MAX_PRIORITY);
		threadCreateVie.setPriority(Thread.MAX_PRIORITY);

		threadCreateGer.addListener(Server.this);
		threadCreateVie.addListener(Server.this);
	}

	public static void startServer() {
		threadCreateGer.start();
		threadCreateVie.start();
	}

	private void createCls(String lang) {
		String selectedArff = null, selectedCls = null;

		if (lang.equals("GER")) {
			selectedArff = FeatureSelection.SELECTED_ARFF_PATH_GER;
			selectedCls = ClassifierSelection.CLASSIFIER_PATH_GER;
		} else if (lang.equals("VIE")) {
			selectedArff = FeatureSelection.SELECTED_ARFF_PATH_VIE;
			selectedCls = ClassifierSelection.CLASSIFIER_PATH_VIE;
		}

		try {
			Instances trainingData = new Instances(new BufferedReader(
					new FileReader(selectedArff)));
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			FileReader fr = new FileReader(selectedCls);
			BufferedReader clsFileReader = new BufferedReader(fr);
			int clsCode = Integer.parseInt(clsFileReader.readLine());
			clsFileReader.close();
			if (lang.equals("GER")) {
				Server.clsGer = CeedClassifier.select(clsCode);
				Server.clsGer.buildClassifier(trainingData);
				XPanel.outText("GER classifier created.\n");
			} else if (lang.equals("VIE")) {
				Server.clsVie = CeedClassifier.select(clsCode);
				Server.clsVie.buildClassifier(trainingData);
				XPanel.outText("VIE classifier created.\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			XPanel.outText(e.getMessage() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			XPanel.outText(e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			XPanel.outText(e.getMessage() + "\n");
		}
	}

	public static void stopServer() {
		try {
			serverSocket.close();
			XPanel.outText("Close Socket done.\n");
		} catch (IOException e) {
			e.printStackTrace();
			XPanel.outText("Close Socket: " + e.getMessage() + "\n");
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

	@Override
	public void notifyOfThreadComplete(Thread thread) {
		if ( (thread.getName().equals(threadCreateGer.getName()) && !threadCreateVie.isAlive())
				|| (thread.getName().equals(threadCreateVie.getName()) && !threadCreateGer.isAlive()) )
			threadCreateSocket.start();
	}

}
