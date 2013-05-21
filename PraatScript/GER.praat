comma$ = ", "

form 
	text fileSourcePath
endform

indexOfDot = index(fileSourcePath$, ".")
inputToWekaFileName$ = left$ (fileSourcePath$, indexOfDot-1)+"_filtered.arff"
lastSlashIndex = rindex(fileSourcePath$, "\")
fName$ = mid$(fileSourcePath$, lastSlashIndex + 1,indexOfDot - 1 - lastSlashIndex)
#xoa file cu
filedelete 'inputToWekaFileName$'

fileappend 'inputToWekaFileName$' % ARFF file for emotional speech database with numeric features'newline$'
fileappend 'inputToWekaFileName$' %'newline$'
fileappend 'inputToWekaFileName$' @relation emodb'newline$''newline$'

fileappend 'inputToWekaFileName$' @attribute pitchAcMeanAbsoluteSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchAcSlopeWithoutOctaveJumps numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchCcSlopeWithoutOctaveJumps numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchSpiSlopeWithoutOctaveJumps numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pitchShsSlopeWithoutOctaveJumps numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccStdevPeriod numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ppccJitterRap numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterRap numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pppJitterPpq5 numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmAcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmAcStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute harmGneStdev numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute freqOfMaximum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute ltasSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcValueAtFreq numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcFreqOfMaximum numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute pcLtasSlope numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute formantLpcQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute formantLpcMean numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensityMin numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute intensityQuantile numeric'newline$'
fileappend 'inputToWekaFileName$' @attribute emotion? { anger, boredom, disgust, fear, happiness, sadness, neutral }'newline$'
fileappend 'inputToWekaFileName$' %'newline$'

fileappend 'inputToWekaFileName$' @data'newline$'
fileappend 'inputToWekaFileName$' %'newline$'
fileappend 'inputToWekaFileName$' % 1 instances'newline$'
fileappend 'inputToWekaFileName$' %'newline$'

call featureExtractor 'fileSourcePath$'

procedure featureExtractor .fileName$
	objectName$ = fName$
	Read from file... '.fileName$'
	
	select Sound 'objectName$'
	To Pitch (ac)... 0 75 15 no 0.03 0.45 0.01 0.35 0.14 600
	pitchAcMeanAbsSlope = Get mean absolute slope... Hertz
	pitchAcSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchAcMeanAbsSlope''comma$''pitchAcSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To Pitch (cc)... 0 75 15 no 0.03 0.45 0.01 0.35 0.14 600
	pitchCcStdev = Get standard deviation... 0 0 Hertz
	pitchCcSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchCcStdev''comma$''pitchCcSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To Pitch (SPINET)... 0.005 0.04 70 5000 250 500 15
	pitchSpiStdev = Get standard deviation... 0 0 Hertz
	pitchSpiSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchSpiStdev''comma$''pitchSpiSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To Pitch (shs)... 0.01 50 15 1250 15 0.84 600 48
	pitchShsMin = Get minimum... 0 0 Hertz Parabolic
	pitchShsQuantile = Get quantile... 0 0 0.5 Hertz
	pitchShsMean = Get mean... 0 0 Hertz
	pitchShsStdev = Get standard deviation... 0 0 Hertz
	pitchShsSlopeWoOctaveJmps = Get slope without octave jumps
	fileappend 'inputToWekaFileName$' 'pitchShsMin''comma$''pitchShsQuantile''comma$''pitchShsMean''comma$''pitchShsStdev''comma$''pitchShsSlopeWoOctaveJmps''comma$'
	
	select Sound 'objectName$'
	To PointProcess (periodic, cc)... 75 600
	ppccStdevPeriod = Get stdev period... 0 0 0.0001 0.02 1.3
	ppccJitterRap = Get jitter (rap)... 0 0 0.0001 0.02 1.3
	fileappend 'inputToWekaFileName$' 'ppccStdevPeriod''comma$''ppccJitterRap''comma$'
	
	select Sound 'objectName$'
	To PointProcess (periodic, peaks)... 75 600 yes yes
	pppJitterRap = Get jitter (rap)... 0 0 0.0001 0.02 1.3
	pppJitterPpq5 = Get jitter (ppq5)... 0 0 0.0001 0.02 1.3
	fileappend 'inputToWekaFileName$' 'pppJitterRap''comma$''pppJitterPpq5''comma$'
	
	select Sound 'objectName$'
	To Harmonicity (ac)... 0.01 75 0.1 4.5
	harmAcMean = Get mean... 0 0
	harmAcStdev = Get standard deviation... 0 0
	fileappend 'inputToWekaFileName$' 'harmAcMean''comma$''harmAcStdev''comma$'
	
	select Sound 'objectName$'
	To Harmonicity (gne)... 500 4500 1000 80
	harmGneStdev = Get standard deviation... 0 0 0 0
	fileappend 'inputToWekaFileName$' 'harmGneStdev''comma$'
	
	select Sound 'objectName$'
	To Ltas... 100
	ltasMin = Get minimum... 0 0 None
	freqOfMaximum = Get frequency of maximum... 0 0 None
	ltasSlope = Get slope... 0 1000 1000 4000 energy
	fileappend 'inputToWekaFileName$' 'ltasMin''comma$''freqOfMaximum''comma$''ltasSlope''comma$'
	
	select Sound 'objectName$'
	To Ltas (pitch-corrected)... 75 600 5000 100 0.0001 0.02 1.3
	pcValueAtFreq = Get value at frequency... 1500 Nearest
	pcFreqOfMaximum = Get frequency of maximum... 0 0 None
	pcLtasSlope = Get slope... 0 1000 1000 4000 energy
	fileappend 'inputToWekaFileName$' 'pcValueAtFreq''comma$''pcFreqOfMaximum''comma$''pcLtasSlope''comma$'
	
	select Sound 'objectName$'
	To Formant (burg)... 0 5 5500 0.025 50
	formantLpcQuantile = Get quantile... 1 0 0 Hertz 0.5
	formantLpcMean = Get mean... 1 0 0 Hertz
	fileappend 'inputToWekaFileName$' 'formantLpcQuantile''comma$''formantLpcMean''comma$'
	
	select Sound 'objectName$'
	To Intensity... 100 0 yes
	intensityMin = Get minimum... 0 0 Parabolic
	intensityQuantile = Get quantile... 0 0 0.5
	fileappend 'inputToWekaFileName$' 'intensityMin''comma$''intensityQuantile''comma$'
	
	fileappend 'inputToWekaFileName$' ?
	fileappend 'inputToWekaFileName$' 'newline$'
endproc