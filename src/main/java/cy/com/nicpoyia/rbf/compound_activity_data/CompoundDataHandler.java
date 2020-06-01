package cy.com.nicpoyia.rbf.compound_activity_data;

import cy.com.nicpoyia.rbf.network.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Class responsible for compound data-sets handling (normalization of values,
 * training and testing data separation)
 *
 * @author Nicolas Poyiadjis
 */
public class CompoundDataHandler {
    // Number of given data-sets
    int dataSetsCount;
    // Original attribute values for each data-set
    List<Double[]> dataSetsAttributeValues;
    // Represented character for each data-set
    List<Double> dataSetsRepresentedActivities;
    // Normalized attribute values per attribute
    List<Double[]> normalizedDataSetsAttributeValues;
    // Normalized data-sets
    List<CompoundData> dataSets;
    // Training data-sets
    private List<CompoundData> trainingDataSets;
    // Testing data-sets
    private List<CompoundData> testingDataSets;

    /**
     * Constructs a data-set handler.
     *
     * @param configuration Configuration
     */
    public CompoundDataHandler(Configuration configuration) throws Exception {
        // Initialize instance attributes and structures
        dataSetsCount = 0;
        dataSetsAttributeValues = new ArrayList<Double[]>();
        dataSetsRepresentedActivities = new ArrayList<Double>();
        normalizedDataSetsAttributeValues = new ArrayList<Double[]>();
        dataSets = new ArrayList<CompoundData>();
        trainingDataSets = new ArrayList<CompoundData>();
        testingDataSets = new ArrayList<CompoundData>();
        // Read data-sets from file
        readDataSetsFile(configuration.dataFile);
        try {
            // Normalize attribute values between [0,1]
            normilizeAllAttributeValues();
            // Generate normalized data-sets
            generateDataSets();
        } catch (Exception e) {
            System.err.println("Error while processing data-sets");
            e.printStackTrace();
        }
        // Separate training and testing data-sets
        separateTrainingTestingDataSets();
        // Select centres and save them into a file
        selectCentres(configuration.numHiddenLayerNeurons, configuration.centresFile);
    }

    /**
     * Training data-sets getter method.
     *
     * @return Training data-sets
     */
    public List<CompoundData> getTrainingDataSets() {
        return trainingDataSets;
    }

    /**
     * Testing data-sets getter method.
     *
     * @return Testing data-sets
     */
    public List<CompoundData> getTestingDataSets() {
        return testingDataSets;
    }

    /**
     * Generates a list of the input values for each data-set.
     *
     * @param trainingTesting true: Training Data-sets / false: Testing data-sets
     * @return List of data-set inputs.
     */
    public List<double[]> getInputValuesDataSets(boolean trainingTesting) {
        List<CompoundData> dataSets = trainingDataSets;
        if (!trainingTesting)
            dataSets = testingDataSets;
        List<double[]> inputValues = new ArrayList<double[]>();
        for (int i = 0; i < dataSets.size(); i++)
            inputValues.add(dataSets.get(i).normalizedAttributeValues);
        return inputValues;
    }

    /**
     * Generates a list of the target output values for each data-set.
     *
     * @param trainingTesting true: Training Data-sets / false: Testing data-sets
     * @return List of data-set target outputs.
     */
    public double[] getTargetOutputValuesDataSets(boolean trainingTesting) {
        List<CompoundData> dataSets = trainingDataSets;
        if (!trainingTesting)
            dataSets = testingDataSets;
        double[] targetOutputValues = new double[dataSetsCount];
        for (int i = 0; i < dataSets.size(); i++)
            targetOutputValues[i] = dataSets.get(i).activity;
        return targetOutputValues;
    }

    /**
     * Reads all data-sets among with the represented character from the
     * data-sets file.
     *
     * @param dataSetsfilename Data-sets filename
     */
    private void readDataSetsFile(String dataSetsfilename) throws Exception {
        FileInputStream dataSetsFileInputStream = null;
        try {
            dataSetsFileInputStream = new FileInputStream(dataSetsfilename);
        } catch (FileNotFoundException e) {
            throw new Exception("Dataset file not found");
        }
        BufferedReader dataSetsFileReader = new BufferedReader(new InputStreamReader(dataSetsFileInputStream));
        String nextDataSetLine = null;
        // Skip the first three lines, that contain meta-data
        dataSetsFileReader.readLine();
        dataSetsFileReader.readLine();
        dataSetsFileReader.readLine();
        nextDataSetLine = dataSetsFileReader.readLine();
        while ((nextDataSetLine != null) && (!nextDataSetLine.isEmpty())) {
            if (!readDataSet(nextDataSetLine))
                throw new Exception("Invalid data-set line " + dataSetsCount + ": " + nextDataSetLine);
            dataSetsCount++;
            nextDataSetLine = dataSetsFileReader.readLine();
        }
        dataSetsFileReader.close();
    }

    /**
     * Decodes information from a data-set line.
     *
     * @param dataSetLine Data-set line as a string
     * @return Whether data-set line has been successfully read
     */
    private boolean readDataSet(String dataSetLine) {
        if (dataSetLine == null)
            return false;
        // Get data-set line's elements
        String[] lineElements = dataSetLine.split(",");
        if (lineElements.length != (CompoundData.numOfAttributes + 2))
            return false;
        // Get compound's activity
        String representedActivityString = lineElements[1];
        if (representedActivityString.equals("<-1*"))
            representedActivityString = "-1";
        Double representedActivity = Double.parseDouble(representedActivityString);
        // Get data-set's attribute values
        Double[] attributeValues = new Double[CompoundData.numOfAttributes];
        for (int i = 0; i < CompoundData.numOfAttributes; i++) {
            String nextAttributeValueString = lineElements[i + 2];
            if (nextAttributeValueString.length() == 0)
                return false;
            Double nextAttributeValue = null;
            try {
                nextAttributeValue = Double.parseDouble(nextAttributeValueString);
            } catch (NumberFormatException e) {
                System.out.println(nextAttributeValueString);
                return false;
            }
            attributeValues[i] = nextAttributeValue;
        }
        // Store decoded data-set information
        dataSetsAttributeValues.add(attributeValues);
        dataSetsRepresentedActivities.add(representedActivity);
        return true;
    }

    /**
     * Normalizes the attribute value of all data-sets, by using min-max
     * normilization.
     *
     * @throws Exception In case of normalization error
     */
    private void normilizeAllAttributeValues() throws Exception {
        for (int i = 0; i < CompoundData.numOfAttributes; i++) {
            Double[] attributeValues = new Double[dataSetsCount];
            for (int j = 0; j < dataSetsCount; j++) {
                attributeValues[j] = dataSetsAttributeValues.get(j)[i];
            }
            Double[] normalizedAttributeValues = null;
            try {
                normalizedAttributeValues = normilizeAttributeValues(attributeValues);
            } catch (Exception e) {
                throw new Exception("Error while normilizing values of attribute " + i + " (" + e.getMessage() + ")");
            }
            normalizedDataSetsAttributeValues.add(normalizedAttributeValues);
        }
    }

    /**
     * Normalizes the attribute value of a single data-set, by using min-max
     * normalization, between [0,1].
     *
     * @param attributeValues All data-sets' values for a single attribute
     * @return Normalized attribute values in corresponding order.
     * @throws Exception IllegalArgumentException | NullPointerException
     */
    private Double[] normilizeAttributeValues(Double[] attributeValues) throws Exception {
        if (attributeValues == null)
            throw new IllegalArgumentException("attributeValues should not be null");
        // Detect maximum and minimum values
        Double minValue = Double.MAX_VALUE;
        Double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < dataSetsCount; i++) {
            Double nextValue = attributeValues[i];
            if (nextValue == null)
                throw new NullPointerException("Attribute value should not be null");
            if (nextValue < minValue)
                minValue = nextValue;
            if (nextValue > maxValue)
                maxValue = nextValue;
        }
        // Normalize values by using min-max normalization
        Double minMaxVariance = maxValue - minValue;
        Double[] normalizedValues = new Double[dataSetsCount];
        for (int i = 0; i < dataSetsCount; i++) {
            normalizedValues[i] = (attributeValues[i] - minValue) / minMaxVariance;
        }
        return normalizedValues;
    }

    /**
     * Generates and stores the DataSet instances, by using the normalized
     * attribute values and activities of the data-sets given.
     */
    private void generateDataSets() throws Exception {
        for (int i = 0; i < dataSetsCount; i++) {
            // Generate the DataSet instance
            Double representedActivity = dataSetsRepresentedActivities.get(i);
            double normalizedAttributeValues[] = new double[CompoundData.numOfAttributes];
            for (int j = 0; j < CompoundData.numOfAttributes; j++) {
                normalizedAttributeValues[j] = normalizedDataSetsAttributeValues.get(j)[i];
            }
            CompoundData nextDataSet = null;
            try {
                nextDataSet = new CompoundData(normalizedAttributeValues, representedActivity);
            } catch (IllegalArgumentException e) {
                throw new Exception("Error while intantiating DataSet " + i + " (" + e.getMessage() + ")");
            }
            // Store the data-set
            dataSets.add(nextDataSet);
        }
    }

    /**
     * Separates the given data-sets into training and testing ones, by using a
     * class balancing technique.
     */
    private void separateTrainingTestingDataSets() {
        // Randomly distribute samples to training and testing data-sets
        for (int i = 0; i < dataSetsCount; i += 3) {
            int[] nextGroup = new int[]{i, i + 1, i + 2};
            for (int j = 0; j < nextGroup.length; j++) {
                if ((i + j) < dataSetsCount) {
                    if (j <= 1)
                        trainingDataSets.add(dataSets.get(i + j));
                    else
                        testingDataSets.add(dataSets.get(i + j));
                }
            }
        }
    }

    /**
     * Selects the initial centres to be used and saves them into a file.
     *
     * @param numOfCentres Number of centres to select
     * @param centresFile  Filename to save centres into
     */
    private void selectCentres(int numOfCentres, String centresFile) {
        int trainingSetCentresCount = (int) Math.round(((double) numOfCentres) * (2.0 / 3));
        int testingSetCentresCount = numOfCentres - trainingSetCentresCount;
        List<CompoundData> centres = new ArrayList<CompoundData>();
        int selectionStep = dataSetsCount / (trainingSetCentresCount + testingSetCentresCount);
        // Normally select centres from training and testing data-sets
        for (int i = 0; i < trainingDataSets.size(); i += selectionStep)
            centres.add(trainingDataSets.get(i));
        for (int i = 0; i < testingDataSets.size(); i += selectionStep)
            centres.add(testingDataSets.get(i));
        // If less centres selected, fill in centres rendomly
        Random randomGenerator = new Random();
        for (int i = centres.size(); i < numOfCentres; i++) {
            int randomIndex = (int) Math.round(randomGenerator.nextDouble() * trainingDataSets.size());
            centres.add(trainingDataSets.get(randomIndex));
        }
        centres = centres.subList(0, numOfCentres);
        // Save centres into the centres file
        PrintWriter centresWriter = null;
        try {
            centresWriter = new PrintWriter(new FileOutputStream(centresFile));
            for (CompoundData nextCentre : centres) {
                centresWriter.println(nextCentre.getAttributesVectorString());
            }
            centresWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
