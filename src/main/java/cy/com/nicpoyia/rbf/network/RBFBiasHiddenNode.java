package cy.com.nicpoyia.rbf.network;

/**
 * Class representing an RBF bias hidden node.
 * 
 * @author Nicolas Poyiadjis
 *
 */
public class RBFBiasHiddenNode extends RBFHiddenNode {

	/**
	 * Constructs an RBF bias hidden node.
	 * 
	 * @param outputCoefficients
	 *            Initial coefficient of each output connection from the bias
	 *            hidden node
	 */
	public RBFBiasHiddenNode(double[] outputCoefficients) {
		super(outputCoefficients, null, 0);
	}

	/**
	 * Calculates the bias hidden node's weighted output vector.
	 * 
	 * @param input
	 *            Input vector
	 * @return Weighted output vector
	 */
	@Override
	public double[] getOutputVector(double[] input) {
		double[] ouputVector = new double[outputCoefficients.length];
		System.arraycopy(outputCoefficients, 0, ouputVector, 0, outputCoefficients.length);
		return ouputVector;
	}

	/**
	 * Updates the bias node's output coefficient, centre and sigma.
	 * 
	 * @param input
	 *            Input vector
	 * @param outputErrors
	 *            Error of each network output
	 * @param learningRates
	 *            Coefficient learning rates
	 */
	@Override
	public void updateNodeParemeters(double[] input, double[] outputErrors, double[] learningRates) {
		// Calculate update value
		double[] coefficientUpdateVector = coefficientUpdateVector(input, outputErrors, learningRates[0]);
		// Update bias node's parameter
		for (int i = 0; i < outputCoefficients.length; i++)
			outputCoefficients[i] += coefficientUpdateVector[i];
	}

	/**
	 * Calculates the update value of the bias node's output coefficient.
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
		double[] coefficientUpdateVector = new double[outputErrors.length];
		for (int i = 0; i < outputErrors.length; i++)
			coefficientUpdateVector[i] = learningRate * outputErrors[i];
		return coefficientUpdateVector;
	}

}
