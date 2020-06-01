package cy.com.nicpoyia.rbf.network;

import cy.com.nicpoyia.rbf.compound_activity_data.CompoundDataHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Class representing an RBF network.
 *
 * @author Nicolas Poyiadjis
 */
public class RBFNetwork {
    // Configuration
    private Configuration configuration;
    // RBF network parameters
    private int numHiddenLayerNeurons;
    private int numInputNeurons;
    private int numOutputNeurons;
    private boolean useBias;
    private double biasValue;
    private double[] learningRates;
    private double[] sigmas;
    private double[][] centres;
    private long maxIterations;
    // Network structure
    private RBFHiddenNode[] hiddenLayer;
    // Training input and target output values
    private List<double[]> trainingInputs;
    private double[] trainingTargetOutputs;
    // Testing input and target output values
    private List<double[]> testingInputs;
    private double[] testingTargetOutputs;
    // Results from training & testing
    private TrainingTestingResults results;

    /**
     * Constructs an RBF network.
     */
    public RBFNetwork() throws Exception {
        // Load configuration
        this.configuration = new Configuration();
        // Set RBF network parameters
        this.numHiddenLayerNeurons = configuration.numHiddenLayerNeurons;
        this.numInputNeurons = configuration.numInputNeurons;
        this.useBias = configuration.useBias;
        this.biasValue = configuration.biasValue;
        this.numOutputNeurons = configuration.numOutputNeurons;
        this.learningRates = configuration.learningRates;
        this.sigmas = configuration.sigmas;
        this.maxIterations = configuration.maxIterations;
        // Load data
        // Data-sets handler
        CompoundDataHandler dataSetsHandler = new CompoundDataHandler(configuration);
        this.trainingInputs = dataSetsHandler.getInputValuesDataSets(true);
        this.trainingTargetOutputs = dataSetsHandler.getTargetOutputValuesDataSets(true);
        this.testingInputs = dataSetsHandler.getInputValuesDataSets(false);
        this.testingTargetOutputs = dataSetsHandler.getTargetOutputValuesDataSets(false);
        // Read centres
        try {
            this.centres = loadCentresFromFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // Create RBF network structure
        createNetworkStructure();
        // Setup results handler
        this.results = new TrainingTestingResults(configuration.resultsFile);
    }

    /**
     * Executes training and testing for the specified number of iterations.
     */
    public void executeTrainingAndTesting() {
        for (int i = 0; i < maxIterations; i++)
            passEpoch();
        saveWeights();
    }

    /**
     * Passes an epoch (training and testing).
     */
    private void passEpoch() {
        // Pass epoch (training & testing)
        double trainingError = trainRBFNetwork();
        double testingError = testRBFNetwork();
        // Store results about training and testing error
        try {
            results.iterationCompleted(trainingError, testingError);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Trains the RBF network for all training data.
     *
     * @return Epoch training error
     */
    private double trainRBFNetwork() {
        List<double[]> patternErrorVectors = new ArrayList<double[]>();
        for (int i = 0; i < trainingInputs.size(); i++) {
            // Give input and get output
            double[] nextInputVector = trainingInputs.get(i);
            double[] nextTargetOutputVector = {trainingTargetOutputs[i]};
            double[] nextOutputVector = getOutputVector(nextInputVector);
            // Calculate error
            double[] nextErrorVector = calculateErrorVector(nextOutputVector, nextTargetOutputVector);
            patternErrorVectors.add(nextErrorVector);
            // Update hidden nodes parameters
            updateHiddenNodesParemeters(nextInputVector, nextErrorVector);
        }
        return calculateTotalEpochError(patternErrorVectors);
    }

    /**
     * Tests the RBF network for all testing data.
     *
     * @return Epoch testing error
     */
    private double testRBFNetwork() {
        List<double[]> patternErrorVectors = new ArrayList<double[]>();
        for (int i = 0; i < testingInputs.size(); i++) {
            // Give input and get output
            double[] nextInputVector = testingInputs.get(i);
            double[] nextTargetOutputVector = {testingTargetOutputs[i]};
            double[] nextOutputVector = getOutputVector(nextInputVector);
            // Calculate error
            double[] nextErrorVector = calculateErrorVector(nextOutputVector, nextTargetOutputVector);
            patternErrorVectors.add(nextErrorVector);
        }
        return calculateTotalEpochError(patternErrorVectors);
    }

    /**
     * Creates the RBF network nodes structure.
     */
    private void createNetworkStructure() {
        // Hidden nodes
        hiddenLayer = new RBFHiddenNode[numHiddenLayerNeurons + 1];
        for (int i = 0; i < numHiddenLayerNeurons; i++) {
            // Initialize coefficients to small random values [-1,1]
            double[] initialNodeCoefficients = new double[numOutputNeurons];
            Random randomGenerator = new Random();
            for (int j = 0; j < numOutputNeurons; j++) {
                double absCoefficient = randomGenerator.nextDouble();
                boolean weightSign = randomGenerator.nextBoolean();
                if (weightSign)
                    initialNodeCoefficients[j] = absCoefficient;
                else
                    initialNodeCoefficients[j] = -absCoefficient;
            }
            // Use specified node centre
            double[] nodeCentre = centres[i];
            // Initialize hidden node
            hiddenLayer[i] = new RBFHiddenNode(initialNodeCoefficients, nodeCentre, sigmas[i]);
        }
        // Hidden bias node
        if (useBias) {
            double[] initialBiasCoefficients = new double[numOutputNeurons];
            for (int i = 0; i < numOutputNeurons; i++)
                initialBiasCoefficients[i] = -biasValue;
            // Initialize bias hidden node
            hiddenLayer[numHiddenLayerNeurons] = new RBFBiasHiddenNode(initialBiasCoefficients);
        }
    }

    /**
     * Loads hidden nodes centres from the centres file.
     *
     * @return Centre vectors of all hidden nodes
     * @throws Exception If centres could not be successfully loaded
     */
    private double[][] loadCentresFromFile() throws Exception {
        double[][] centres = new double[numHiddenLayerNeurons][numInputNeurons];
        int centresLoaded = 0;
        try {
            FileInputStream centresFileInputStream = new FileInputStream(configuration.centresFile);
            @SuppressWarnings("resource")
            BufferedReader centresFileReader = new BufferedReader(new InputStreamReader(centresFileInputStream));
            String nextCentreLine = null;
            while ((nextCentreLine = centresFileReader.readLine()) != null) {
                String[] nextCentreVectorStrings = nextCentreLine.split(", ");
                for (int i = 0; i < numInputNeurons; i++) {
                    try {
                        centres[centresLoaded][i] = Double.parseDouble(nextCentreVectorStrings[i]);
                    } catch (NumberFormatException e) {
                        throw new Exception("Error while loading centre " + centresLoaded);
                    }
                }
                centresLoaded++;
            }
            centresFileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (centresLoaded != numHiddenLayerNeurons)
            throw new Exception(centresLoaded + " centres loaded - " + numHiddenLayerNeurons + " centres required!");
        return centres;
    }

    /**
     * Generates the RBF nerwork's output vector.
     *
     * @param input Input vector
     * @return Output vector
     */
    private double[] getOutputVector(double[] input) {
        double[][] hiddenNodesOutputs = new double[numHiddenLayerNeurons][numOutputNeurons];
        for (int i = 0; i < numHiddenLayerNeurons; i++)
            hiddenNodesOutputs[i] = hiddenLayer[i].getOutputVector(input);
        double[] outputVector = new double[numOutputNeurons];
        for (int i = 0; i < numOutputNeurons; i++) {
            outputVector[i] = 0.0;
            for (int j = 0; j < numHiddenLayerNeurons; j++)
                outputVector[i] += hiddenNodesOutputs[j][i];
        }
        return outputVector;
    }

    /**
     * Calculates the RBF nerwork's error vector.
     *
     * @param outputVector       Output vector
     * @param targetOutputVector Target output vector
     * @return Error vector
     */
    private double[] calculateErrorVector(double[] outputVector, double[] targetOutputVector) {
        double[] errorVector = new double[numOutputNeurons];
        for (int i = 0; i < numOutputNeurons; i++)
            errorVector[i] = targetOutputVector[i] - outputVector[i];
        return errorVector;
    }

    /**
     * Calculates the total epoch error.
     *
     * @param patternErrorVectors Error vectors for all input patterns
     * @return Total epoch error
     */
    private double calculateTotalEpochError(List<double[]> patternErrorVectors) {
        double totalSquaredSum = 0.0;
        for (double[] patternErrorVector : patternErrorVectors) {
			for (double v : patternErrorVector) totalSquaredSum += (v * v);
        }
		return 0.5 * totalSquaredSum;
    }

    /**
     * Updates the hidden nodes parameters, according to the error of each
     * network output.
     *
     * @param inputVector Input vector
     * @param errorVector Error vector
     */
    private void updateHiddenNodesParemeters(double[] inputVector, double[] errorVector) {
        for (int i = 0; i < numHiddenLayerNeurons; i++)
            hiddenLayer[i].updateNodeParemeters(inputVector, errorVector, learningRates);
        if (useBias)
            hiddenLayer[numHiddenLayerNeurons].updateNodeParemeters(inputVector, errorVector, learningRates);
    }

    /**
     * Saves all RBF network's weights into a text file.
     */
    private void saveWeights() {
        String weightsFilename = configuration.weightsFile;
        PrintWriter weightsWriter = null;
        try {
            weightsWriter = new PrintWriter(new FileOutputStream(weightsFilename));
            for (RBFHiddenNode nextHiddenNode : hiddenLayer) {
                if (!(nextHiddenNode instanceof RBFBiasHiddenNode)) {
                    double[] outputCoefficients = nextHiddenNode.outputCoefficients;
                    double[] centre = nextHiddenNode.centre;
                    double sigma = nextHiddenNode.sigma;
                    weightsWriter.println("Output coefficients: " + Arrays.toString(outputCoefficients));
                    weightsWriter.println("Centre: " + Arrays.toString(centre));
                    weightsWriter.println("Sigma: " + sigma);
                    weightsWriter.println();
                }
            }
            weightsWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
