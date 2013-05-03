package thesis.ceed.client;

import java.io.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

public class RecordingWav {
	private static final int RECORDER_BPP = 16;//bit depth per sample
	private static final String FILE_EXT = ".wav";
	private static final String OUTPUT_FOLDER = "CEED";
	private static final String TEMP_FILE = "ceed_temp.raw";
	private static final int SAMPLE_RATE = 16000;
	private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
			
	private AudioRecord mAudioRecorder = null;
	private int bufferSize = 0;
	private Thread recordThread = null;
	public boolean isRecording = false;
	private String fileNameSaved = null;
	public String getFileNameSaved() {
		return fileNameSaved;
	}

	public void setFileNameSaved(String fileNameSaved) {
		this.fileNameSaved = fileNameSaved;
	}
	public RecordingWav(){
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS, AUDIO_ENCODING);
		fileNameSaved = getFileName();
	}
	
	public String getFileName(){
		//getting the path of the SD card
		String filePath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filePath,OUTPUT_FOLDER);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		return (file.getAbsolutePath()+"/" + System.currentTimeMillis() + FILE_EXT);		
	}
	
	public String getTempFileName(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,OUTPUT_FOLDER);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		File tempFile = new File(filepath,TEMP_FILE);
		
		if(tempFile.exists())
			tempFile.delete();
		
		return (file.getAbsolutePath() + "/" + TEMP_FILE);
	}
	
	
	public void startRecording(){
		//Initialize mAudioRecorder object with InputSource, Sample rate, number of channel, audio encoding and buffer size of output stream
		mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNELS, AUDIO_ENCODING, bufferSize );
		//Set to Recording state
		mAudioRecorder.startRecording();
		isRecording = true;
		recordThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				writeRecordedDataToRawFile();
			}
		});
		recordThread.start();		
	}//end of startRecording()
	
	public void writeRecordedDataToRawFile(){
		byte data[] = new byte[bufferSize];
		String fileName = getTempFileName();
		FileOutputStream fos = null;
		
		try{
			fos = new FileOutputStream(fileName);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		int read = 0;
		if(null!= fos){
			while(isRecording){
				read = mAudioRecorder.read(data, 0, bufferSize);
				if(AudioRecord.ERROR_INVALID_OPERATION != read){
					try{
						fos.write(data);						
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
			try{
				fos.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}//end of writeRecordedDataToRawFile()
	
	public void stopRecording(){
		if(null!= mAudioRecorder){
			isRecording = false;
			mAudioRecorder.stop();
			mAudioRecorder.release();
			
			mAudioRecorder = null;
			recordThread = null;
		}
		
		saveFromTempToWavFile(getTempFileName(), fileNameSaved);
		//deleteTempFile();
	}//end of stopRecording()
	
	public void saveFromTempToWavFile(String tempFile, String wavFile){
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		long totalAudioDataLen = 0;
		long totalDataLen = totalAudioDataLen + 36;
		long longSampleRate = SAMPLE_RATE;
		int numberOfChannel = 1;
		long byteRate = RECORDER_BPP*SAMPLE_RATE*numberOfChannel/8;
		
		byte[] data = new byte[bufferSize];
		
		try{
			fis = new FileInputStream(tempFile);
			fos = new FileOutputStream(wavFile);
			//get the size of audio data
			totalAudioDataLen = fis.getChannel().size();
			totalDataLen = totalAudioDataLen + 36;
			//create Wave File header
			createWavFile(fos, totalAudioDataLen, totalDataLen, longSampleRate, numberOfChannel, byteRate);
			//continue to write audio data after completing the header
			while(fis.read(data) != -1){
				fos.write(data);
			}
			
			fis.close();
			fos.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}//end of saveFromTempToWavFile
	
	public void createWavFile(FileOutputStream fos, long totalAudioDataLen, long totalDataLen, long sampleRate, int numberOfChannel, long byteRate){
		byte[] mWaveFileHeader = new byte[44];
		
		/*
	     WAV File Specification
	     FROM http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
	    The canonical WAVE format starts with the RIFF header:
	    0         4   ChunkID          Contains the letters "RIFF" in ASCII form
	                                   (0x52494646 big-endian form).
	    4         4   ChunkSize        36 + SubChunk2Size, or more precisely:
	                                   4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
	                                   This is the size of the rest of the chunk 
	                                   following this number.  This is the size of the 
	                                   entire file in bytes minus 8 bytes for the
	                                   two fields not included in this count:
	                                   ChunkID and ChunkSize.
	    8         4   Format           Contains the letters "WAVE"
	                                   (0x57415645 big-endian form).

	    The "WAVE" format consists of two subchunks: "fmt " and "data":
	    The "fmt " subchunk describes the sound data's format:
	    12        4   Subchunk1ID      Contains the letters "fmt "
	                                   (0x666d7420 big-endian form).
	    16        4   Subchunk1Size    16 for PCM.  This is the size of the
	                                   rest of the Subchunk which follows this number.
	    20        2   AudioFormat      PCM = 1 (i.e. Linear quantization)
	                                   Values other than 1 indicate some 
	                                   form of compression.
	    22        2   NumChannels      Mono = 1, Stereo = 2, etc.
	    24        4   SampleRate       8000, 44100, etc.
	    28        4   ByteRate         == SampleRate * NumChannels * BitsPerSample/8
	    32        2   BlockAlign       == NumChannels * BitsPerSample/8
	                                   The number of bytes for one sample including
	                                   all channels. I wonder what happens when
	                                   this number isn't an integer?
	    34        2   BitsPerSample    8 bits = 8, 16 bits = 16, etc.

	    The "data" subchunk contains the size of the data and the actual sound:
	    36        4   Subchunk2ID      Contains the letters "data"
	                                   (0x64617461 big-endian form).
	    40        4   Subchunk2Size    == NumSamples * NumChannels * BitsPerSample/8
	                                   This is the number of bytes in the data.
	                                   You can also think of this as the size
	                                   of the read of the subchunk following this 
	                                   number.
	    44        *   Data             The actual sound data. 
	    
	    */
		
		mWaveFileHeader[0] = 'R';																	
		mWaveFileHeader[1] = 'I';
		mWaveFileHeader[2] = 'F';
		mWaveFileHeader[3] = 'F';
		mWaveFileHeader[4] = (byte)(totalDataLen & 0xff);
		mWaveFileHeader[5] = (byte)((totalDataLen >> 8) & 0xff);
		mWaveFileHeader[6] = (byte)((totalDataLen >> 16) & 0xff);
		mWaveFileHeader[7] = (byte)((totalDataLen >> 24) & 0xff);
		mWaveFileHeader[8] = 'W';
		mWaveFileHeader[9] = 'A';
		mWaveFileHeader[10] = 'V';
		mWaveFileHeader[11] = 'E';
		mWaveFileHeader[12] = 'f';
		mWaveFileHeader[13] = 'm';
		mWaveFileHeader[14] = 't';
		mWaveFileHeader[15] = ' ';
		mWaveFileHeader[16] = 16;//size of 'fmt ' chunk
		mWaveFileHeader[17] = 0;
		mWaveFileHeader[18] = 0;
		mWaveFileHeader[19] = 0;
		mWaveFileHeader[20] = 1;
		mWaveFileHeader[21] = 0;
		mWaveFileHeader[22] = (byte) numberOfChannel;
		mWaveFileHeader[23] = 0;
		
		mWaveFileHeader[24] = (byte)(sampleRate & 0xff);
		mWaveFileHeader[25] = (byte)((sampleRate >> 8) & 0xff);
		mWaveFileHeader[26] = (byte)((sampleRate >> 16) & 0xff);
		mWaveFileHeader[27] = (byte)((sampleRate >> 24) & 0xff);
		
		mWaveFileHeader[28] = (byte)(byteRate & 0xff);
		mWaveFileHeader[29] = (byte)((byteRate >> 8) & 0xff);
		mWaveFileHeader[30] = (byte)((byteRate >> 16) & 0xff);
		mWaveFileHeader[31] = (byte)((byteRate >> 24) & 0xff);
		
		mWaveFileHeader[32] = (byte)(numberOfChannel*RECORDER_BPP/8);
		mWaveFileHeader[33] = 0;
		mWaveFileHeader[34] = RECORDER_BPP;
		
		mWaveFileHeader[35] = 0;
		
		mWaveFileHeader[36] = 'd';
		mWaveFileHeader[37] = 'a';
		mWaveFileHeader[38] = 't';
		mWaveFileHeader[39] = 'a';
		
		mWaveFileHeader[40] = (byte)(totalAudioDataLen & 0xff);
		mWaveFileHeader[41] = (byte)((totalAudioDataLen >> 8) & 0xff);
		mWaveFileHeader[42] = (byte)((totalAudioDataLen >> 16) & 0xff);
		mWaveFileHeader[43] = (byte)((totalAudioDataLen >> 24) & 0xff);
		
		try {
			
			fos.write(mWaveFileHeader, 0, 44);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of createWaveFile()
	
	public void deleteTempFile(){
		File file = new File(getTempFileName());
		file.delete();
	}
}


