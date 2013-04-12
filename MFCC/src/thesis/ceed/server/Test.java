package thesis.ceed.server;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadWav.showArray(ReadWav.readWav("D:\\Internship\\Emotion Detection\\Database\\example\\03a01Fa.wav"));
		MFCC.processMFCC(ReadWav.readWav("D:\\Internship\\Emotion Detection\\Database\\example\\03a01Fa.wav"));
	}

}
