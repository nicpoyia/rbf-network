package cy.com.nicpoyia.rbf.compound_activity_data;

/**
 * Class representing a compound's properties and activity.
 * 
 * @author Nicolas Poyiadjis
 *
 */
public class CompoundData {
	// Number of attributes that should be contained within a compound sample
	public static final int numOfAttributes = 53;
	// Normalized attribute values
	double[] normalizedAttributeValues;
	// Normalized activity of the represented compound
	double activity;

	/**
	 * Constructs a compound data-set.
	 * 
	 * @param normalizedAttributeValues
	 *            Normalized values for all attributes in the right order.
	 * @param activity
	 *            Normalized activity of the represented compound
	 */
	public CompoundData(double[] normalizedAttributeValues, double activity) {
		// Check data-set's validity
		if (normalizedAttributeValues.length != numOfAttributes)
			throw new IllegalArgumentException("The attributes vector must have " + numOfAttributes + " values");
		// Store data-set's data
		this.normalizedAttributeValues = normalizedAttributeValues;
		this.activity = activity;
	}

	/**
	 * Generates a string representation of the attributes vector.
	 * 
	 * @return Attributes vector string
	 */
	public String getAttributesVectorString() {
		StringBuilder vectorString = new StringBuilder();
		for (int i = 0; i < numOfAttributes; i++) {
			vectorString.append(normalizedAttributeValues[i]);
			if (i < (numOfAttributes - 1))
				vectorString.append(", ");
		}
		return vectorString.toString();
	}

}
