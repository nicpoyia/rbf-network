package cy.com.nicpoyia.rbf;


import cy.com.nicpoyia.rbf.network.RBFNetwork;

/**
 * RBF network demo launcher.
 * 
 * @author Nicolas Poyiadjis
 *
 */
public class Launcher {

	/**
	 * Launcher method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		RBFNetwork rbfNetwork = new RBFNetwork();
		rbfNetwork.executeTrainingAndTesting();
	}

}
