package cy.com.nicpoyia.rbf.compound_activity_data;

import cy.com.nicpoyia.rbf.network.Configuration;

import java.util.Arrays;
import java.util.List;


/**
 * Test Driver class for CompoundDataHandler class.
 * 
 * @author Nicolas Poyiadjis
 *
 */
public class CompoundDataHandlerTest {

	/**
	 * Test Driver
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CompoundDataHandler compoundDataHandler = new CompoundDataHandler(new Configuration());
		int dataSetCounter = 0;
		for (int i = 0; i < compoundDataHandler.dataSetsCount; i++) {
			CompoundData nextDataSet = compoundDataHandler.dataSets.get(i);
			if (nextDataSet == null)
				System.out.println("DataSet at index " + i + " has a null pointer");
			dataSetCounter++;
		}
		System.out.println(dataSetCounter + " data-sets have been properly read and processed");
		// Display compound training data-set
		List<CompoundData> trainingSets = compoundDataHandler.getTrainingDataSets();
		for (int i = 0; i < trainingSets.size(); i++) {
			CompoundData nextDataSet = trainingSets.get(i);
			double nextDataSetRepresentedActivity = nextDataSet.activity;
			String nextDataSetAttributeValues = Arrays.toString(nextDataSet.normalizedAttributeValues);
			System.out.println("Training data-set " + i + ":");
			System.out.println("\t" + nextDataSetRepresentedActivity + " ---> " + nextDataSetAttributeValues);
		}
		System.out.println();
		// Display compound training data-set
		List<CompoundData> testingSets = compoundDataHandler.getTestingDataSets();
		for (int i = 0; i < testingSets.size(); i++) {
			CompoundData nextDataSet = testingSets.get(i);
			double nextDataSetRepresentedActivity = nextDataSet.activity;
			String nextDataSetAttributeValues = Arrays.toString(nextDataSet.normalizedAttributeValues);
			System.out.println("Testing data-set " + i + ":");
			System.out.println("\t" + nextDataSetRepresentedActivity + " ---> " + nextDataSetAttributeValues);
		}
	}

}
