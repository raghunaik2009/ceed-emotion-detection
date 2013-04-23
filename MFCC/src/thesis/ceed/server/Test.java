package thesis.ceed.server;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//MFCC testMFCC = new MFCC();
		ReadWav.showArray(ReadWav.readWav("D:\\Internship\\Emotion Detection\\Database\\example\\03a01Fa.wav"));
		//testMFCC.processMFCC(ReadWav.readWav("D:\\Internship\\Emotion Detection\\Database\\example\\03a01Fa.wav"));
		MEDC testMEDC = new MEDC();
		testMEDC.processMEDC(ReadWav.readWav("D:\\Internship\\Emotion Detection\\Database\\example\\03a01Fa.wav"));
	}

}
