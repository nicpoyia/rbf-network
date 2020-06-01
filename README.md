# RBF network with moving centers in Java

Package contains implementation of RBF and demo on Selwood data set.

RBF parameters have been selected after testing with the data set. 

### How to run
* Build the project using gradle
* Run Launcher class for the demo
* You can modify input parameters in resources directory

### Input parameters
* numHiddenLayerNeurons
* numInputNeurons
* numOutputNeurons
* useBias
* biasValue
* learningRates
* sigmas
* maxIterations
* dataFile
* centresFile
* resultsFile
* weightsFile

### Generated files
* centreVectors.txt (Initial centers used)
* weights.txt
* results.txt
