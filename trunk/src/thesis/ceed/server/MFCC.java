package thesis.ceed.server;

public class MFCC {
	
	CommonProcesses CP_MFCC = new CommonProcesses();
	FFT FFT_MFCC;
	
	private static double[] nonLinearTransformation(double fbank[]){
        double f[] = new double[fbank.length];
        final double FLOOR = -50;
        
        for (int i = 0; i < fbank.length; i++){
            f[i] = Math.log(fbank[i]);
            
            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) f[i] = FLOOR;
        }
        
        return f;
    }
	
	public double[][] processMFCC(short inputSignal[]){
        double MFCC[][];

        // Pre-Emphasis
        double outputSignal[] = CP_MFCC.preEmphasis(inputSignal);
        
        // Frame Blocking
        CP_MFCC.framing(outputSignal);

        // Initializes the MFCC array
        MFCC = new double[CP_MFCC.FRAMES.length][CP_MFCC.NO_CEPSTRA];

        // apply Hamming Window to ALL frames
        CP_MFCC.hammingWindow();
        
        //
        // Below computations are all based on individual frames with Hamming Window already applied to them
        //
        for (int k = 0; k < CP_MFCC.FRAMES.length; k++){
            FFT_MFCC = new FFT();
            
            // Magnitude Spectrum
            double bin[] = CP_MFCC.magnitudeSpectrum(CP_MFCC.FRAMES[k]);

            // Mel Filtering
            int cbin[] = CP_MFCC.fftBinIndices();
            // get Mel Filterbank
            double fbank[] = CP_MFCC.melFilter(bin, cbin);

            // Non-linear transformation (log calculation)
            double f[] = nonLinearTransformation(fbank);

            // Cepstral coefficients
            double cepc[] = DCT.calculateDCT(f);

            // Add resulting MFCC to array
            for (int i = 0; i < CP_MFCC.NO_CEPSTRA; i++){
                MFCC[k][i] = cepc[i];
            }
        }

        return MFCC;
    }
	
}
