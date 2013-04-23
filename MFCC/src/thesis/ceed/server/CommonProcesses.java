package thesis.ceed.server;

public class CommonProcesses {
	//Frame size
		public static final int FRAMELENGHT = 512;
		//Frame overlapping size
		public static final int SHIFTINTERVAL = FRAMELENGHT/2;
		//All the frames of the input signal
		public static double FRAMES[][];
		//ALPHA of preEmphasis process
		public static final double ALPHA = 0.95;
		//sample rate 
		public static double SAMPLERATE = 16000.0;
		//lower freq
		public static double LOWER_FILTER_FREQ = 133.3334;
		//upper freq
		public static double UPPTER_FILTER_FREQ = 2840.0230;
		//size of FFT 
		public static int FFT_SIZE = 512;
		//number of MFCCs of each frame
		public static int NO_CEPSTRA = 13;
		//FFT object
		public static FFT FFT;
		//function to convert frequency from Hz to Mel
		public static int NO_MEL_FILTER = 23;
		public static double freqToMel(double freq){
			return 2595*log10(1+freq/700);
		}
		
		//function to convert frequency from Mel to Hz
		public static double inverseMel(double x){
	        double temp = Math.pow(10, x / 2595) - 1;
	        return 700 * (temp);
	    }
		
		//calculate log10
		public static double log10(double value){
	        return Math.log(value) / Math.log(10);
	    }
		
		//calculate center frequency of a filterbank
		private double centerFreq(int i){
	        double mel[] = new double[2];
	        mel[0] = freqToMel(LOWER_FILTER_FREQ);
	        mel[1] = freqToMel(SAMPLERATE / 2);
	        
	        // take inverse mel of:
	        double temp = mel[0] + ((mel[1] - mel[0]) / (NO_MEL_FILTER + 1)) * i;
	        return inverseMel(temp);
	    }
		
		//Pre-processing step
		public double[] preEmphasis(short inputSignal[]){
			double outputSignal[] = new double[inputSignal.length];
			for(int i = 1; i < inputSignal.length;i++){
				outputSignal[i] = inputSignal[i] - ALPHA*inputSignal[i - 1];
			}		
			return outputSignal;
		}
		//Step 1: Framing the input signal array
		public void framing(double inputSignal[]){
			double numFrames = (double) inputSignal.length/(double)(FRAMELENGHT - SHIFTINTERVAL);
			//Round the numbFrame
			if( (numFrames/(int)numFrames) != 1){
				numFrames = (int)numFrames + 1;
			}
			
			//fill up last frame with 0 as the default value of an element in newly initiated array is 0
			double paddedSignal[] = new double[(int)numFrames * FRAMELENGHT];
			for(int n = 0; n < inputSignal.length; n++){
				paddedSignal[n] = inputSignal[n];
			}
			
			FRAMES = new double[(int)numFrames][FRAMELENGHT];
			//cutting signal into frames with specified configuration of overlapping size btw frames
			for(int m = 0; m < numFrames; m++){
				for(int n = 0; n < FRAMELENGHT; n++){
					FRAMES[m][n] = paddedSignal[m*(FRAMELENGHT - SHIFTINTERVAL) + n];
				}
			}
		}//end of Framing
		
		//Step 1.1: applying Hamming window to the frames
		public void hammingWindow(){
			double hammingWin[] = new double[FRAMELENGHT];
			for(int i = 0; i < FRAMELENGHT; i ++){
				hammingWin[i] = 0.54 - 0.46*Math.cos( (2*Math.PI*i)/ (FRAMELENGHT - 1));
			}
			
			for(int j = 0; j< FRAMES.length;j++){
				for(int k = 0; k < FRAMELENGHT; k++){
					FRAMES[j][k]*=hammingWin[k];
				}
			}
		}//end of hammingwindow
		
		//Step 2: FFT transform and calculate energy in certain frequency in EACH frame
		public double[] magnitudeSpectrum(double frame[]){
			double magSpectrum[] = new double[frame.length];
			
			//FFT calculating for this frame
			FFT.computeFFT(frame);
			
			//calculate energy(magnitude spectrum)
			for(int i = 0; i<frame.length; i++){
				magSpectrum[i] = Math.pow(FFT.real[i]*FFT.real[i] + FFT.imag[i]*FFT.imag[i], 0.5);				
			}
			
			return magSpectrum;		
		}//end of step 2
		
		public int[] fftBinIndices(){
	        int cbin[] = new int[NO_MEL_FILTER + 2];
	        
	        cbin[0] = (int)Math.round(LOWER_FILTER_FREQ / SAMPLERATE * FFT_SIZE);
	        cbin[cbin.length - 1] = (int)(FFT_SIZE / 2);
	        
	        for (int i = 1; i <= NO_MEL_FILTER; i++){
	            double fc = centerFreq(i);

	            cbin[i] = (int)Math.round(fc / SAMPLERATE * FFT_SIZE);
	        }
	        
	        return cbin;
	    }//end of fftBinIndices
		
		public double[] melFilter(double bin[], int cbin[]){
	        double temp[] = new double[NO_MEL_FILTER + 2];

	        for (int k = 1; k <= NO_MEL_FILTER; k++){
	            double num1 = 0, num2 = 0;

	            for (int i = cbin[k - 1]; i <= cbin[k]; i++){
	                num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k-1] + 1)) * bin[i];
	            }

	            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++){
	                num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
	            }

	            temp[k] = num1 + num2;
	        }

	        double fbank[] = new double[NO_MEL_FILTER];
	        for (int i = 0; i < NO_MEL_FILTER; i++){
	            fbank[i] = temp[i + 1];
	        }
	        return fbank;
	    }
}
