package cy.com.nicpoyia.rbf.network;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

/**
 * Class representing RBF configuration parameters.
 *
 * @author Nicolas Poyiadjis
 */
public class Configuration {
    // Default parameters file name
    private static final String defaultParametersFilename = "parameters.txt";
    // Attributes representing parameters
    public int numHiddenLayerNeurons;
    public int numInputNeurons;
    public int numOutputNeurons;
    public boolean useBias;
    public double biasValue;
    public double[] learningRates;
    public double[] sigmas;
    public long maxIterations;
    public String dataFile;
    public String centresFile;
    public String resultsFile;
    public String weightsFile;

    /**
     * Constructs a configuration instance using a file containing all required
     * parameters.
     *
     * @param parametersFile Parameters file name
     */
    public Configuration(String parametersFile) throws Exception {
        URL parametersFilePath = Thread.currentThread().getContextClassLoader().getResource(parametersFile);
        if (parametersFilePath == null) {
            throw new Exception("Parameters file not found");
        }
        FileInputStream parametersFileInputStream = new FileInputStream(parametersFilePath.getFile());
        BufferedReader parametersFileReader = new BufferedReader(new InputStreamReader(parametersFileInputStream));
        String nextParameterLine = null;
        while ((nextParameterLine = parametersFileReader.readLine()) != null) {
            if (!readParameter(nextParameterLine))
                throw new Exception("Invalid parameter line: " + nextParameterLine);
        }
        parametersFileReader.close();
    }

    /**
     * Constructs a configuration instance using the default parameters file.
     */
    public Configuration() throws Exception {
        this(defaultParametersFilename);
    }

    /**
     * Reads a parameter and stores it into the appropriate instance attribute.
     *
     * @param parameterLine Line containing parameter name and value
     * @return Whether parameter has been successfully read
     */
    private boolean readParameter(String parameterLine) {
        String[] parameterLineElements = parameterLine.split(" ");
        String parameterName = parameterLineElements[0];
        String parameterValueString = parameterLineElements[1];
        if (parameterName.equals("numHiddenLayerNeurons"))
            numHiddenLayerNeurons = Integer.parseInt(parameterValueString);
        else if (parameterName.equals("numInputNeurons"))
            numInputNeurons = Integer.parseInt(parameterValueString);
        else if (parameterName.equals("numOutputNeurons"))
            numOutputNeurons = Integer.parseInt(parameterValueString);
        else if (parameterName.equals("useBias"))
            useBias = Boolean.parseBoolean(parameterValueString);
        else if (parameterName.equals("biasValue"))
            biasValue = Double.parseDouble(parameterValueString);
        else if (parameterName.equals("learningRates")) {
            try {
                learningRates = new double[3];
                String[] learningRatesStrings = parameterValueString.split(",");
                for (int i = 0; i < 3; i++)
                    learningRates[i] = Double.parseDouble(learningRatesStrings[i]);
            } catch (Exception e) {
                return false;
            }
        } else if (parameterName.equals("sigmas")) {
            try {
                sigmas = new double[numHiddenLayerNeurons];
                String[] sigmasStrings = parameterValueString.split(",");
                for (int i = 0; i < numHiddenLayerNeurons; i++)
                    sigmas[i] = Double.parseDouble(sigmasStrings[i]);
            } catch (Exception e) {
                return false;
            }
        } else if (parameterName.equals("maxIterations"))
            maxIterations = Long.parseLong(parameterValueString);
        else if (parameterName.equals("dataFile"))
            dataFile = parameterValueString;
        else if (parameterName.equals("centresFile"))
            centresFile = parameterValueString;
        else if (parameterName.equals("resultsFile"))
            resultsFile = parameterValueString;
        else if (parameterName.equals("weightsFile"))
            weightsFile = parameterValueString;
        else
            return false;
        return true;
    }

    /**
     * Configuration Test Driver
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        System.out.println("numHiddenLayerNeurons: " + conf.numHiddenLayerNeurons);
        System.out.println("numInputNeurons: " + conf.numInputNeurons);
        System.out.println("numOutputNeurons: " + conf.numOutputNeurons);
        System.out.println("useBias: " + conf.useBias);
        System.out.println("biasValue: " + conf.biasValue);
        System.out.println("learningRates: " + Arrays.toString(conf.learningRates));
        System.out.println("sigmas: " + Arrays.toString(conf.sigmas));
        System.out.println("maxIterations: " + conf.maxIterations);
        System.out.println("dataFile: " + conf.dataFile);
        System.out.println("centresFile: " + conf.centresFile);
        System.out.println("resultsFile: " + conf.resultsFile);
        System.out.println("weightsFile: " + conf.weightsFile);
    }

}
