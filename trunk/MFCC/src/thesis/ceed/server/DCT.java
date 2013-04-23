package thesis.ceed.server;

public class DCT {
	public static double[] calculateDCT(double f[]){
        double cepc[] = new double[CommonProcesses.NO_CEPSTRA];
        
        for (int i = 0; i < cepc.length; i++){
            for (int j = 1; j <= CommonProcesses.NO_MEL_FILTER; j++){
                cepc[i] += f[j - 1] * Math.cos(Math.PI * i / CommonProcesses.NO_MEL_FILTER * (j - 0.5));
            }
        }        
        return cepc;
    }
}
