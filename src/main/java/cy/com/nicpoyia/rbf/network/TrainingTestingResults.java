package cy.com.nicpoyia.rbf.network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Results representing progress of the training and testing procedures.
 *
 * @author Nicolas Poyiadjis
 */
public class TrainingTestingResults {
    // Results output file name
    private String resultsFilename;
    // Number of iterations executed so far
    private int numOfIterations;

    /**
     * Number of Iterations getter
     *
     * @return Number of iterations
     */
    public int getNumOfIterations() {
        return numOfIterations;
    }

    /**
     * Constructs an instance that handles the training and testing results.
     * @param resultsFile
     */
    public TrainingTestingResults(String resultsFile) {
        this.resultsFilename = resultsFile;
        this.numOfIterations = 0;
        // Clear the results file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(resultsFilename);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores iteration's results. Should be called once iteration is complete.
     *
     * @param trainingError   Iteration's training error
     * @param testingError    Iteration's testing error
     */
    public void iterationCompleted(double trainingError, double testingError) {
        numOfIterations++;
        // Print errors into the output errors file
        PrintWriter errorsWriter = null;
        try {
            errorsWriter = new PrintWriter(new FileOutputStream(resultsFilename, true));
            errorsWriter.println((numOfIterations - 1) + " " + trainingError + " " + testingError);
            errorsWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test Driver
     *
     * @param args
     */
    public static void main(String[] args) {
        TrainingTestingResults results = new TrainingTestingResults("results.txt");
        try {
            results.iterationCompleted(0.55, 0.65);
            results.iterationCompleted(0.5, 0.6);
            results.iterationCompleted(0.47, 0.57);
            results.iterationCompleted(0.45, 0.55);
            results.iterationCompleted(0.44, 0.54);
            results.iterationCompleted(0.436, 0.536);
            results.iterationCompleted(0.433, 0.533);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
