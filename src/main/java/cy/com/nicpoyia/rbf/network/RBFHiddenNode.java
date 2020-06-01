package cy.com.nicpoyia.rbf.network;

/**
 * Class representing an RBF hidden node.
 * 
 * @author Nicolas Poyiadjis
 *
 */
public class RBFHiddenNode {
	// Coefficient of each output connection from the hidden node
	public double[] outputCoefficients;
	// Centre of the hidden node
	public double[] centre;
	// Gaussian width
	public double sigma;

	/**
	 * Constructs an RBF hidden node.
	 * 
	 * @param outputCoefficients
	 *            Initial coefficient of each output connection from the hidden
	 *            node
	 * @param centre
	 *            Initial centre of the hidden node
	 * @param sigma
	 *            Initial gaussian width
	 */
	public RBFHiddenNode(double[] outputCoefficients, double[] centre, double sigma) {
		this.outputCoefficients = outputCoefficients;
		this.centre = centre;
		this.sigma = sigma;
	}

	/**
	 * Calculates the hidden node's weighted output vector.
	 * 
	 * @param input
	 *            Input vector
	 * @return Weighted output vector
	 */
	public double[] getOutputVector(double[] input) {
		double[] ouputVector = new double[outputCoefficients.length];
		double inputDistance = calculateSquaredInputDistance(input);
		double basisFunctionValue = calculateBasisFunctionValue(inputDistance);
		for (int i = 0; i < outputCoefficients.length; i++)
			ouputVector[i] = outputCoefficients[i] * basisFunctionValue;
		return ouputVector;
	}

	/**
	 * Updates the node's output coefficient, centre and sigma.
	 * 
	 * @param input
	 *            Input vector
	 * @param outputErrors
	 *            Error of each network output
	 * @param learningRates
	 *            Coefficient learning rates
	 */
	public void updateNodeParemeters(double[] input, double[] outputErrors, double[] learningRates) {
		// Calculate update values
		double[] coefficientUpdateVector = coefficientUpdateVector(input, outputErrors, learningRates[0]);
		double[] centreUpdateVector = centreUpdateVector(input, outputErrors, learningRates[1]);
		double sigmaUpdateValue = sigmaUpdateValue(input, outputErrors, learningRates[2]);
		// Update node's parameters
		for (int i = 0; i < outputCoefficients.length; i++)
			outputCoefficients[i] += coefficientUpdateVector[i];
		for (int i = 0; i < centre.length; i++)
			centre[i] += centreUpdateVector[i];
		sigma += sigmaUpdateValue;
	}

	/**
	 * Calculates the squared euclidean distance between an input vector and the
	 * current hidden node centre.
	 * 
	 * @param input
	 *            Input vector
	 * @return Squared euclidean distance
	 */
	private double calculateSquaredInputDistance(double[] input) {
		double inputSquaredDistance = 0.0;
		for (int i = 0; i < input.length; i++) {
			inputSquaredDistance += ((input[i] - centre[i]) * (input[i] - centre[i]));
		}
		return inputSquaredDistance;
	}

	/**
	 * Calculates the euclidean distance between an input vector and the current
	 * hidden node centre.
	 * 
	 * @param input
	 *            Input vector
	 * @return Euclidean distance
	 */
	private double calculateInputDistance(double[] input) {
		return Math.sqrt(calculateSquaredInputDistance(input));
	}

	/**
	 * Calculates the basis function value.
	 * 
	 * @param inputDistance
	 *            Distance between input and centre
	 * @return Basis function value
	 */
	private double calculateBasisFunctionValue(double inputDistance) {
		return Math.exp((-(inputDistance * inputDistance)) / (2 * sigma * sigma));
	}

	/**
	 * Calculates the update value of the node's output coefficient.
	 * 
	 * @param input
	 *            Input vector
	 * @param outputErrors
	 *            Error of each network output
	 * @param learningRate
	 *            Coefficient learning rate
	 * @return Coefficient update value
	 */
	private double[] coefficientUpdateVector(double[] input, double[] outputErrors, double learningRate) {
		double distanceBasisValue = calculateBasisFunctionValue(calculateSquaredInputDistance(input));
		double[] coefficientUpdateVector = new double[outputErrors.length];
		for (int i = 0; i < outputErrors.length; i++)
			coefficientUpdateVector[i] = learningRate * outputErrors[i] * distanceBasisValue;
		return coefficientUpdateVector;
	}

	/**
	 * Calculates the update vector of the node's centre.
	 * 
	 * @param input
	 *            Input vector
	 * @param outputErrors
	 *            Error of each network output
	 * @param learningRate
	 *            Coefficient learning rate
	 */
	private double[] centreUpdateVector(double[] input, double[] outputErrors, double learningRate) {
		double[] centreUpdateVector = new double[centre.length];
		double distanceBasisValue = calculateBasisFunctionValue(calculateSquaredInputDistance(input));
		for (int i = 0; i < centre.length; i++) {
			double coordinateInputSpecialDistance = (input[i] - centre[i]) / (sigma * sigma);
			centreUpdateVector[i] = 0.0;
			for (int j = 0; j < outputErrors.length; j++)
				centreUpdateVector[i] += (learningRate * outputErrors[j] * outputCoefficients[j] * distanceBasisValue
						* coordinateInputSpecialDistance);
		}
		return centreUpdateVector;
	}

	/**
	 * Calculates the update value of the node's sigma.
	 * 
	 * @param input
	 *            Input vector
	 * @param outputErrors
	 *            Error of each network output
	 * @param learningRate
	 *            Coefficient learning rate
	 */
	private double sigmaUpdateValue(double[] input, double[] outputErrors, double learningRate) {
		double distanceBasisValue = calculateBasisFunctionValue(calculateSquaredInputDistance(input));
		double coordinateInputSpecialDistance = (calculateSquaredInputDistance(input)) / (sigma * sigma * sigma);
		double sigmaUpdateValue = 0.0;
		for (int i = 0; i < outputErrors.length; i++)
			sigmaUpdateValue += learningRate * outputErrors[i] * outputCoefficients[i] * distanceBasisValue
					* coordinateInputSpecialDistance;
		return sigmaUpdateValue;
	}

}
