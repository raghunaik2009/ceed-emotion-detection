import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

public class ClassifyingTest {
	private final static String TRAINING_FILE_NAME = "C:\\Users\\sev_user\\Desktop\\training.arff";
	private final static String UNLABELED_FILE_NAME = "C:\\Users\\sev_user\\Desktop\\unlabeled.arff";
	private final static String TEST_FILE_NAME = "C:\\Users\\sev_user\\Desktop\\labeled.arff";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Instances training = new Instances(new BufferedReader(new FileReader(
				TRAINING_FILE_NAME)));
		// setting class attribute
		training.setClassIndex(training.numAttributes() - 1);

		String[] options = new String[1];
		options[0] = "-U"; // unpruned tree
		ZeroR tree = new ZeroR(); // new instance of tree
		tree.setOptions(options); // set the options
		tree.buildClassifier(training); // build classifier

		/*// load unlabeled data
		Instances unlabeled = new Instances(new BufferedReader(new FileReader(
				UNLABELED_FILE_NAME)));
		// set class attribute
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

		// create copy
		Instances labeled = new Instances(unlabeled);

		// label instances
		for (int i = 0; i < unlabeled.numInstances(); i++) {
			double clsLabel = tree.classifyInstance(unlabeled.instance(i));
			labeled.instance(i).setClassValue(clsLabel);
		}
		// save labeled data
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				TEST_FILE_NAME));
		writer.write(labeled.toString());
		writer.newLine();
		writer.flush();
		writer.close();*/

		// load test data
		Instances test = new Instances(new BufferedReader(new FileReader(
				TEST_FILE_NAME)));
		// set class attribute
		test.setClassIndex(test.numAttributes() - 1);

		// Test the model
		Evaluation eval = new Evaluation(training);
		eval.evaluateModel(tree, test);

		// Print the result à la Weka explorer:
		String strSummary = eval.toSummaryString();
		System.out.println(strSummary);

		// Get the confusion matrix
		double[][] cmMatrix = eval.confusionMatrix();
		for (int i = 0; i < cmMatrix.length; i++) {
			for (int j = 0; j < cmMatrix[0].length; j++) {
				System.out.print(cmMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}

}
