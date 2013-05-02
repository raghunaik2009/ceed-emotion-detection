comma$ = ", "

form 
	text sourcePath
	boolean isTraining 0
endform

if isTraining = 0
	inputToWekaFileName$ = "test.arff"
elsif isTraining = 1
	inputToWekaFileName$ = "training.arff"
endif

#xoa file cu
filedelete 'inputToWekaFileName$'

#khoi tao trung voi cau truc arff
fileappend 'inputToWekaFileName$' % ARFF file for emotional speech database with numeric features'newline$'
fileappend 'inputToWekaFileName$' %'newline$'
fileappend 'inputToWekaFileName$' @relation emodb'newline$''newline$'

#khai bao cac attribute
#1-5: raw parameters
fileappend 'inputToWekaFileName$' @attribute energy numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute power numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute eia numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pia numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensity numeric'newline$'

#10-13: pitch (global F0 statistics?) (how about quantile, mean absolute slope, slope without octave jumps?)
fileappend 'inputToWekaFileName$' @attribute pitchMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchStdev numeric'newline$'

#70-76: pitch Ac
fileappend 'inputToWekaFileName$' @attribute pitchAcMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcMeanAbsoluteSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcSlopeWithoutOctaveJumps numeric'newline$'

#77-83: pitch Cc
fileappend 'inputToWekaFileName$' @attribute pitchCcMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcMeanAbsoluteSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcSlopeWithoutOctaveJumps numeric'newline$'

#96-103: pitch SPINET (linear fit?)
fileappend 'inputToWekaFileName$' @attribute pitchSpiMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiMeanAbsoluteSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiSlopeWithoutOctaveJumps numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute pitchSpiLinearFit numeric'newline$'

#104-111: pitch Shs (outer view port?)
fileappend 'inputToWekaFileName$' @attribute pitchShsMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsMeanAbsoluteSlope numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute outerViewPort numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsSlopeWithoutOctaveJumps numeric'newline$'

#84-95: PointProcess, periodic, cc
fileappend 'inputToWekaFileName$' @attribute ppccNumberOfPts numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccLowIndex numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccHighIndex numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccNearestIndex numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccNumberOfPeriods numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccMeanPeriod numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccStdevPeriod numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccJitterLocal numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccJitterLocalAbs numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccJitterRap numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccJitterPpq5 numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccJitterDdp numeric'newline$'

#PointProcess, periodic, peaks
fileappend 'inputToWekaFileName$' @attribute pppNumberOfPts numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppLowIndex numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppHighIndex numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppNearestIndex numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppNumberOfPeriods numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppMeanPeriod numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppStdevPeriod numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterLocal numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterLocalAbs numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterRap numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterPpq5 numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterDdp numeric'newline$'

#30-37: harmonicity Cc
fileappend 'inputToWekaFileName$' @attribute harmCcMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmCcMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmCcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmCcStdev numeric'newline$'

#34-37: harmonicity Ac
fileappend 'inputToWekaFileName$' @attribute harmAcMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmAcMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmAcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmAcStdev numeric'newline$'

#6-9 & 14-18: harmonicity Gne
#fileappend 'inputToWekaFileName$' @attribute lowestX numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute highestX numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute lowestY numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute highestY numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute numberOfRows numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute numberOfCols numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute rowDistance numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute colDistance numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmGneSum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmGneMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmGneStdev numeric'newline$'

#38-53: Ltas (local peak height 1?)
#fileappend 'inputToWekaFileName$' @attribute ltasLowestFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ltasHighestFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute numberOfBins numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute binWidth numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute binNumberFromFreq numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute valueAtFreq numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute valueInBin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute freqOfMinimum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute freqOfMaximum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute localPeakHeight numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute localPeakHeight1 numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasStdev numeric'newline$'

#54-69: Ltas pitch-corrected (local peak height 1?)
#fileappend 'inputToWekaFileName$' @attribute pcLtasLowestFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute pcLtasHighestFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute pcNumberOfBins numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute pcBinWidth numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute pcBinNumberFromFreq numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcValueAtFreq numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcValueInBin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLtasMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcFreqOfMinimum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLtasMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcFreqOfMaximum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLtasMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLtasSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLocalPeakHeight numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute pcLocalPeakHeight1 numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLtasStdev numeric'newline$'

#112-117: formants (with ffFreqRow & ffFreqBark)
#fileappend 'inputToWekaFileName$' @attribute ffLowestFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffHighestFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffNumberOfFreq numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffFreqDistance numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffFreqFromRow numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffFreqHertz numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffFreqBark numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute ffFreqMel numeric'newline$'

#24-29: formant LPC (number of LPC Koefficiencies?)
#fileappend 'inputToWekaFileName$' @attribute formantLpcMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute formantLpcMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute formantLpcQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute formantLpcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute formantLpcStd numeric'newline$'
#fileappend 'inputToWekaFileName$' @attribute numberOfLpcKoefficiencies numeric'newline$'

#MFCC
fileappend 'inputToWekaFileName$' @attribute mfcc numeric'newline$'

#19-23: intensity statistics
fileappend 'inputToWekaFileName$' @attribute intensityMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensityMax numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensityQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensityMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensityStdev numeric'newline$'

fileappend 'inputToWekaFileName$' @attribute emotion? { anger, boredom, disgust, fear, happiness, sadness, neutral }'newline$'
fileappend 'inputToWekaFileName$' 'newline$'

Create Strings as file list... list 'sourcePath$'*.wav
numberOfFiles = Get number of strings
#bat dau phan data
fileappend 'inputToWekaFileName$' @data'newline$'
fileappend 'inputToWekaFileName$' %'newline$'
fileappend 'inputToWekaFileName$' % 'numberOfFiles' instances'newline$'
fileappend 'inputToWekaFileName$' %'newline$'

#duyet qua cac file trong list va trich xuat dac trung
for ifile to numberOfFiles
	select Strings list
	fileName$ = Get string... ifile	
	call featureExtractor 'fileName$'
endfor

procedure featureExtractor .fileName$
	objectName$ = left$ (.fileName$, 7)
	Read from file... 'sourcePath$''.fileName$'
	energy = Get energy... 0 0
	power = Get power... 0 0
	eia = Get energy in air
	pia = Get power in air
	intensity = Get intensity (dB)
	fileappend 'inputToWekaFileName$' 'energy''comma$''power''comma$''eia''comma$''pia''comma$''intensity''comma$'

	select Sound 'objectName$'
	To Pitch... 0 75 600
	pitchMin = Get minimum... 0 0 Hertz Parabolic
	pitchMax = Get maximum... 0 0 Hertz Parabolic
	pitchMean = Get mean... 0 0 Hertz
	pitchStdev = Get standard deviation... 0 0 Hertz
	fileappend 'inputToWekaFileName$' 'pitchMin''comma$''pitchMax''comma$''pitchMean''comma$''pitchStdev''comma$'

	select Sound 'objectName$'
	To Pitch (ac)... 0 75 15 no 0.03 0.45 0.01 0.35 0.14 600
	pitchAcMin = Get minimum... 0 0 Hertz Parabolic
	pitchAcMax = Get maximum... 0 0 Hertz Parabolic
	pitchAcQuantile = Get quantile... 0 0 0.5 Hertz
	pitchAcMean = Get mean... 0 0 Hertz
	pitchAcStdev = Get standard deviation... 0 0 Hertz
	pitchAcMeanAbsSlope = Get mean absolute slope... Hertz
	pitchAcSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchAcMin''comma$''pitchAcMax''comma$''pitchAcQuantile''comma$''pitchAcMean''comma$''pitchAcStdev''comma$''pitchAcMeanAbsSlope''comma$''pitchAcSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To Pitch (cc)... 0 75 15 no 0.03 0.45 0.01 0.35 0.14 600
	pitchCcMin = Get minimum... 0 0 Hertz Parabolic
	pitchCcMax = Get maximum... 0 0 Hertz Parabolic
	pitchCcQuantile = Get quantile... 0 0 0.5 Hertz
	pitchCcMean = Get mean... 0 0 Hertz
	pitchCcStdev = Get standard deviation... 0 0 Hertz
	pitchCcMeanAbsSlope = Get mean absolute slope... Hertz
	pitchCcSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchCcMin''comma$''pitchCcMax''comma$''pitchCcQuantile''comma$''pitchCcMean''comma$''pitchCcStdev''comma$''pitchCcMeanAbsSlope''comma$''pitchCcSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To Pitch (SPINET)... 0.005 0.04 70 5000 250 500 15
	pitchSpiMin = Get minimum... 0 0 Hertz Parabolic
	pitchSpiMax = Get maximum... 0 0 Hertz Parabolic
	pitchSpiQuantile = Get quantile... 0 0 0.5 Hertz
	pitchSpiMean = Get mean... 0 0 Hertz
	pitchSpiStdev = Get standard deviation... 0 0 Hertz
	pitchSpiMeanAbsSlope = Get mean absolute slope... Hertz
	pitchSpiSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchSpiMin''comma$''pitchSpiMax''comma$''pitchSpiQuantile''comma$''pitchSpiMean''comma$''pitchSpiStdev''comma$''pitchSpiMeanAbsSlope''comma$''pitchSpiSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To Pitch (shs)... 0.01 50 15 1250 15 0.84 600 48
	pitchShsMin = Get minimum... 0 0 Hertz Parabolic
	pitchShsMax = Get maximum... 0 0 Hertz Parabolic
	pitchShsQuantile = Get quantile... 0 0 0.5 Hertz
	pitchShsMean = Get mean... 0 0 Hertz
	pitchShsStdev = Get standard deviation... 0 0 Hertz
	pitchShsMeanAbsSlope = Get mean absolute slope... Hertz
	pitchShsSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchShsMin''comma$''pitchShsMax''comma$''pitchShsQuantile''comma$''pitchShsMean''comma$''pitchShsStdev''comma$''pitchShsMeanAbsSlope''comma$''pitchShsSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To PointProcess (periodic, cc)... 75 600
	ppccNumberOfPts = Get number of points
	ppccLowIndex = Get low index... 0.5
	ppccHighIndex = Get high index... 0.5
	ppccNearestIndex = Get nearest index... 0.5
	ppccNumberOfPeriods = Get number of periods... 0 0 0.0001 0.02 1.3
	ppccMeanPeriod = Get mean period... 0 0 0.0001 0.02 1.3
	ppccStdevPeriod = Get stdev period... 0 0 0.0001 0.02 1.3
	ppccJitterLocal = Get jitter (local)... 0 0 0.0001 0.02 1.3
	ppccJitterLocalAbs = Get jitter (local, absolute)... 0 0 0.0001 0.02 1.3
	ppccJitterRap = Get jitter (rap)... 0 0 0.0001 0.02 1.3
	ppccJitterPpq5 = Get jitter (ppq5)... 0 0 0.0001 0.02 1.3
	ppccJitterDdp = Get jitter (ddp)... 0 0 0.0001 0.02 1.3
	fileappend 'inputToWekaFileName$' 'ppccNumberOfPts''comma$''ppccLowIndex''comma$''ppccHighIndex''comma$''ppccNearestIndex''comma$''ppccNumberOfPeriods''comma$''ppccMeanPeriod''comma$''ppccStdevPeriod''comma$''ppccJitterLocal''comma$''ppccJitterLocalAbs''comma$''ppccJitterRap''comma$''ppccJitterPpq5''comma$''ppccJitterDdp''comma$'
	
	select Sound 'objectName$'
	To PointProcess (periodic, peaks)... 75 600 yes yes
	pppNumberOfPts = Get number of points
	pppLowIndex = Get low index... 0.5
	pppHighIndex = Get high index... 0.5
	pppNearestIndex = Get nearest index... 0.5
	pppNumberOfPeriods = Get number of periods... 0 0 0.0001 0.02 1.3
	pppMeanPeriod = Get mean period... 0 0 0.0001 0.02 1.3
	pppStdevPeriod = Get stdev period... 0 0 0.0001 0.02 1.3
	pppJitterLocal = Get jitter (local)... 0 0 0.0001 0.02 1.3
	pppJitterLocalAbs = Get jitter (local, absolute)... 0 0 0.0001 0.02 1.3
	pppJitterRap = Get jitter (rap)... 0 0 0.0001 0.02 1.3
	pppJitterPpq5 = Get jitter (ppq5)... 0 0 0.0001 0.02 1.3
	pppJitterDdp = Get jitter (ddp)... 0 0 0.0001 0.02 1.3
	fileappend 'inputToWekaFileName$' 'pppNumberOfPts''comma$''pppLowIndex''comma$''pppHighIndex''comma$''pppNearestIndex''comma$''pppNumberOfPeriods''comma$''pppMeanPeriod''comma$''pppStdevPeriod''comma$''pppJitterLocal''comma$''pppJitterLocalAbs''comma$''pppJitterRap''comma$''pppJitterPpq5''comma$''pppJitterDdp''comma$'

	select Sound 'objectName$'
	To Harmonicity (cc)... 0.01 75 0.1 1
	harmCcMin = Get minimum... 0 0 Parabolic
	harmCcMax = Get maximum... 0 0 Parabolic
	harmCcMean = Get mean... 0 0
	harmCcStdev = Get standard deviation... 0 0
	fileappend 'inputToWekaFileName$' 'harmCcMin''comma$''harmCcMax''comma$''harmCcMean''comma$''harmCcStdev''comma$'

	select Sound 'objectName$'
	To Harmonicity (ac)... 0.01 75 0.1 4.5
	harmAcMin = Get minimum... 0 0 Parabolic
	harmAcMax = Get maximum... 0 0 Parabolic
	harmAcMean = Get mean... 0 0
	harmAcStdev = Get standard deviation... 0 0
	fileappend 'inputToWekaFileName$' 'harmAcMin''comma$''harmAcMax''comma$''harmAcMean''comma$''harmAcStdev''comma$'

	select Sound 'objectName$'
	To Harmonicity (gne)... 500 4500 1000 80
	#lowestX = Get lowest x
	#highestX = Get highest x
	#lowestY = Get lowest y
	#highestY = Get highest y
	#numberOfRows = Get number of rows
	#numberOfCols = Get number of columns
	#rowDistance = Get row distance
	#colDistance = Get column distance
	harmGneSum = Get sum
	harmGneMean = Get mean... 0 0 0 0
	harmGneStdev = Get standard deviation... 0 0 0 0
	fileappend 'inputToWekaFileName$' 'harmGneSum''comma$''harmGneMean''comma$''harmGneStdev''comma$'
	#'lowestX''comma$''highestX''comma$''lowestY''comma$''highestY''comma$''numberOfRows''comma$''numberOfCols''comma$''rowDistance''comma$''colDistance''comma$'
	
	select Sound 'objectName$'
	To Ltas... 100
	#ltasLowestFreq = Get lowest frequency
	#ltasHighestFreq = Get highest frequency
	#numberOfBins = Get number of bins
	#binWidth = Get bin width
	#binNumberFromFreq = Get bin number from frequency... 2000
	valueAtFreq = Get value at frequency... 1500 Nearest
	valueInBin = Get value in bin... 50
	ltasMin = Get minimum... 0 0 None
	freqOfMinimum = Get frequency of minimum... 0 0 None
	ltasMax = Get maximum... 0 0 None
	freqOfMaximum = Get frequency of maximum... 0 0 None
	ltasMean = Get mean... 0 0 energy
	ltasSlope = Get slope... 0 1000 1000 4000 energy
	localPeakHeight = Get local peak height... 1700 4200 2400 3200 energy
	ltasStdev = Get standard deviation... 0 0 energy
	fileappend 'inputToWekaFileName$' 'valueAtFreq''comma$''valueInBin''comma$''ltasMin''comma$''freqOfMinimum''comma$''ltasMax''comma$''freqOfMaximum''comma$''ltasMean''comma$''ltasSlope''comma$''localPeakHeight''comma$''ltasStdev''comma$'
	#'ltasLowestFreq''comma$''ltasHighestFreq''comma$''numberOfBins''comma$''binWidth''comma$''binNumberFromFreq''comma$'

	select Sound 'objectName$'
	To Ltas (pitch-corrected)... 75 600 5000 100 0.0001 0.02 1.3
	#pcLtasLowestFreq = Get lowest frequency
	#pcLtasHighestFreq = Get highest frequency
	#pcNumberOfBins = Get number of bins
	#pcBinWidth = Get bin width
	#pcBinNumberFromFreq = Get bin number from frequency... 2000
	pcValueAtFreq = Get value at frequency... 1500 Nearest
	pcValueInBin = Get value in bin... 50
	pcLtasMin = Get minimum... 0 0 None
	pcFreqOfMinimum = Get frequency of minimum... 0 0 None
	pcLtasMax = Get maximum... 0 0 None
	pcFreqOfMaximum = Get frequency of maximum... 0 0 None
	pcLtasMean = Get mean... 0 0 energy
	pcLtasSlope = Get slope... 0 1000 1000 4000 energy
	pcLocalPeakHeight = Get local peak height... 1700 4200 2400 3200 energy
	pcLtasStdev = Get standard deviation... 0 0 energy
	fileappend 'inputToWekaFileName$' 'pcValueAtFreq''comma$''pcValueInBin''comma$''pcLtasMin''comma$''pcFreqOfMinimum''comma$''pcLtasMax''comma$''pcFreqOfMaximum''comma$''pcLtasMean''comma$''pcLtasSlope''comma$''pcLocalPeakHeight''comma$''pcLtasStdev''comma$'
	#'pcLtasLowestFreq''comma$''pcLtasHighestFreq''comma$''pcNumberOfBins''comma$''pcBinWidth''comma$''pcBinNumberFromFreq''comma$'

	#select Sound 'objectName$'
	#To FormantFilter... 0.015 0.005 100 50 0 1.1 75 600
	#ffLowestFreq = Get lowest frequency
	#ffHighestFreq = Get highest frequency
	#ffNumberOfFreq = Get number of frequencies
	#ffFreqDistance = Get frequency distance
	#ffFreqFromRow = Get frequency from row... 1
	#ffFreqHertz = Get frequency in Hertz... 10 Bark
	#ffFreqBark = Get frequency in Bark... 93.17 mel
	#ffFreqMel = Get frequency in mel... 1000 Hertz
	#fileappend 'inputToWekaFileName$' 'ffLowestFreq''comma$''ffHighestFreq''comma$''ffNumberOfFreq''comma$''ffFreqDistance''comma$''ffFreqFromRow''comma$''ffFreqHertz''comma$''ffFreqBark''comma$''ffFreqMel''comma$'
	
	select Sound 'objectName$'
	To Formant (burg)... 0 5 5500 0.025 50
	#formantLpcMin = Get minimum... 1 0 0 Hertz Parabolic
	formantLpcMax = Get maximum... 1 0 0 Hertz Parabolic
	formantLpcQuantile = Get quantile... 1 0 0 Hertz 0.5
	formantLpcMean = Get mean... 1 0 0 Hertz
	formantLpcStdev = Get standard deviation... 1 0 0 Hertz
	fileappend 'inputToWekaFileName$' 'formantLpcMax''comma$''formantLpcQuantile''comma$''formantLpcMean''comma$''formantLpcStdev''comma$'
	
	select Sound 'objectName$'
	To MFCC... 12 0.015 0.005 100 100 0
	mfcc = Get value... 0.1 1
	fileappend 'inputToWekaFileName$' 'mfcc''comma$'
	
	select Sound 'objectName$'
	To Intensity... 100 0 yes
	intensityMin = Get minimum... 0 0 Parabolic
	intensityMax = Get maximum... 0 0 Parabolic
	intensityQuantile = Get quantile... 0 0 0.5
	intensityMean = Get mean... 0 0 energy
	intensityStdev = Get standard deviation... 0 0
	fileappend 'inputToWekaFileName$' 'intensityMin''comma$''intensityMax''comma$''intensityQuantile''comma$''intensityMean''comma$''intensityStdev''comma$'

	#kiem tra filename de dua ra cam xuc
	emoChar$ = mid$(fileName$, 6, 1)
	if emoChar$ = "W"
		fileappend 'inputToWekaFileName$' anger
	elsif emoChar$ = "L"
		fileappend 'inputToWekaFileName$' boredom
	elsif emoChar$ = "E"
		fileappend 'inputToWekaFileName$' disgust
	elsif emoChar$ = "A"
		fileappend 'inputToWekaFileName$' fear
	elsif emoChar$ = "F"
		fileappend 'inputToWekaFileName$' happiness
	elsif emoChar$ = "T"
		fileappend 'inputToWekaFileName$' sadness
	elsif emoChar$ = "N"
		fileappend 'inputToWekaFileName$' neutral
	endif
	fileappend 'inputToWekaFileName$' 'newline$'
endproc