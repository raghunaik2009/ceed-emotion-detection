package thesis.ceed.server;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;
public class ReadWav {
	public static short[] readWav(String filePath){
		File fileRead = new File(filePath);
		int totalFrameRead = 0;
		short[] inputStreamShort;
		try{
			AudioInputStream ais = AudioSystem.getAudioInputStream(fileRead);
			int bytesPerFrame = ais.getFormat().getFrameSize();
			
			//buffer to read from audio stream
			int inputStreamBuffer = 1024*bytesPerFrame;
			
			byte[] inputStreamByte = new byte[inputStreamBuffer];
			int numFrameRead = 0;
			int numByteRead = 0;
			while((numByteRead = ais.read(inputStreamByte))!=-1){
				//Number of frame read
				numFrameRead = numByteRead/bytesPerFrame;
				totalFrameRead += numFrameRead;
			}
			
			//Short array (each element 16bit) to return 
			inputStreamShort = new short[inputStreamByte.length/2];
			//convert 2 bytes into 1 short element
			for(int i = 0; i<inputStreamShort.length; i++){
				inputStreamShort[i] = (short)( (inputStreamByte[2 * i + 1]<<8) + (inputStreamByte[2*i] >=0? inputStreamByte[2*i]: inputStreamByte[2*i]+256) );
			}
			return inputStreamShort;
			
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			//return null;
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//return null;
		}	
		inputStreamShort = new short[1];
		return inputStreamShort;
	}
	
	public static void frameSize(String filePath){
		File fileRead = new File(filePath);
		
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(fileRead);
			
			int bytesPerFrame = 
				    ais.getFormat().getFrameSize();
			System.out.println("Format info:"+ais.getFormat().toString());
			System.out.println("Frame rate:"+ais.getFormat().getFrameRate());
			System.out.println("Frame Size in Bytes:"+bytesPerFrame);			
			System.out.println("Sample rate:"+ais.getFormat().getSampleRate());
			System.out.println("Number of channel:"+ais.getFormat().getChannels());
			System.out.println("Sample size in Bits:"+ais.getFormat().getSampleSizeInBits());
					
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void showArray(short[] tempArray){
		for(int i = 0; i < tempArray.length; i++){
			System.out.println(tempArray[i]);
		}
	}
}
