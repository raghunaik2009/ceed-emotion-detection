package thesis.ceed.server;

public class MEDC {
	CommonProcesses CP_MEDC = new CommonProcesses();
	FFT FFT_MEDC;
	public double[] calculcateLogMeanEnergy(double[] fbank){
		double[] resultLogMean = new double[CommonProcesses.NO_MEL_FILTER - 1];
		for(int i = 0; i < fbank.length - 1;i++)
			resultLogMean[i] = (fbank[i+1] - fbank[i])/(Math.log(fbank[i+1]) - Math.log(fbank[i]));
		
		return resultLogMean;
			
	}
	
	public double[] calculateFirstDifference(double[] logMeanEnergy){
		double[] resultFirstDif = new double[logMeanEnergy.length - 1];
		for(int i = 0;i<logMeanEnergy.length - 1; i++)
			resultFirstDif[i] = logMeanEnergy[i+1] - logMeanEnergy[i];		
		return resultFirstDif;		
	}
	
	public double[] calculateSecondDifference(double[] firstDif){
		double[] resultSecondDif = new double[firstDif.length - 1];
		for(int i = 0; i < firstDif.length - 1;i++)
			resultSecondDif[i] = firstDif[i+1] - firstDif[i];		
		return resultSecondDif;
	}
	
	public double[] combineDiffs(double[] firstDif, double[] secondDif){
		double combinedDif [] = new double[firstDif.length];
		
		combinedDif[0] = (firstDif[0]*firstDif[0])+(secondDif[0]*secondDif[0]);
		for(int i = 1; i < secondDif.length; i ++)
			combinedDif[i] = (firstDif[i] * firstDif[i]) + (secondDif[i]*secondDif[i]);
		
		return combinedDif;
	}
	public double[][] processMEDC(short[] inputSignal){
		
		//PreEmphasis
		double outputSignal[] = CP_MEDC.preEmphasis(inputSignal);
		//Framing the sound
		CP_MEDC.framing(outputSignal);
		//Initialize FRAMES
		double MEDC[][] = new double[CP_MEDC.FRAMES.length][CommonProcesses.NO_MEL_FILTER - 1];
		//Applying Hamming window
		CP_MEDC.hammingWindow();
		//Calculate MEDC for each frame in FRAMES[]
		for(int i = 0; i< CP_MEDC.FRAMES.length; i ++){
			FFT_MEDC = new FFT();
			//calculate energy in term of magnitudeSpectrum
			double bin[] = CP_MEDC.magnitudeSpectrum(CP_MEDC.FRAMES[i]);
			//calculate FFT bin indices 
			int cbin[] = CP_MEDC.fftBinIndices();
			//get filterbank
			double fbank[] = CP_MEDC.melFilter(bin, cbin);
			//calculate logMeanEnergy
			double logMeanEnergy[] = calculcateLogMeanEnergy(fbank);
			//calculate First Dif
			double firstDif[] = calculateFirstDifference(logMeanEnergy);
			//calculate Second Dif
			double secondDif[] = calculateSecondDifference(firstDif);
			//MEDC of a frame
			double combinedDiffs [] = combineDiffs(firstDif, secondDif);
			for(int j = 0; j < CommonProcesses.NO_MEL_FILTER - 2; j++){
				MEDC[i][j] = combinedDiffs[j];
			}
		}
		return MEDC;
	}
}
