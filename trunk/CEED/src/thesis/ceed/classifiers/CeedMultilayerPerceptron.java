package thesis.ceed.classifiers;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;

public class CeedMultilayerPerceptron extends Classifier implements WeightedInstancesHandler {

	/** for serialization */
	private static final long serialVersionUID = -5990607817048210779L;

	/**
	 * Main method for testing this class.
	 * 
	 * @param argv
	 *            should contain command line options (see setOptions)
	 */
	public static void main(String[] argv) {
		runClassifier(new CeedMultilayerPerceptron(), argv);
	}

	/**
	 * runs the classifier instance with the given options.
	 * 
	 * @param classifier
	 *            the classifier to run
	 * @param options
	 *            the command-line options
	 */
	public static void runClassifier(Classifier classifier, String[] options) {
		try {
			System.out.println(Evaluation.evaluateModel(classifier, options));
		} catch (Exception e) {
			if (( (e.getMessage() != null) && (e.getMessage().indexOf("General options") == -1) )
					|| (e.getMessage() == null))
				e.printStackTrace();
			else
				System.err.println(e.getMessage());
		}
	}

	//Input nodes or output nodes
	protected class NeuralEnd extends NeuralConnection {
		static final long serialVersionUID = 7305185603191183338L;

		// The attribute number for an input, the class label number for an output.
		private int m_link;

		// True if node is an input, False if it's an output.
		private boolean m_input;

		public NeuralEnd(String id) {
			super(id);

			m_link = 0;
			m_input = true;
		}

		// Reset the value and error for this unit, also reset all input units to this one.
		public void reset() {
			if (!Double.isNaN(m_unitValue) || !Double.isNaN(m_unitError)) {
				m_unitValue = Double.NaN;
				m_unitError = Double.NaN;
				m_weightsUpdated = false;
				for (int noa = 0; noa < m_numInputs; noa++) {
					m_inputList[noa].reset();
				}
			}
		}

		// Get the output value of this unit.
		public double outputValue(boolean calculate) {
			if (Double.isNaN(m_unitValue) && calculate) {
				if (m_input) {
					if (m_currentInstance.isMissing(m_link)) {
						m_unitValue = 0;
					} else {
						m_unitValue = m_currentInstance.value(m_link);
					}
				} else {
					// node is an output.
					m_unitValue = 0;
					for (int noa = 0; noa < m_numInputs; noa++) {
						m_unitValue += m_inputList[noa].outputValue(true);
					}
					// TODO: check output value
				}
			}
			return m_unitValue;
		}

		// Get the error value of this unit, which in this case is the difference between the predicted class, and the actual class.
		public double errorValue(boolean calculate) {
			if (!Double.isNaN(m_unitValue) && Double.isNaN(m_unitError) && calculate) {
				if (m_input) {
					m_unitError = 0;
					for (int noa = 0; noa < m_numOutputs; noa++) {
						m_unitError += m_outputList[noa].errorValue(true);
					}
				} else {
					// node is an output.
					// TODO: what does this mean?
					if (m_currentInstance.classValue() == m_link) {
						m_unitError = 1 - m_unitValue;
					} else {
						m_unitError = 0 - m_unitValue;
					}
					// TODO: check error value
				}
			}
			return m_unitError;
		}

		// Save the current weights of this unit.
		public void saveWeights() {
			for (int i = 0; i < m_numInputs; i++) {
				m_inputList[i].saveWeights();
			}
		}

		// Restore weights of this unit
		public void restoreWeights() {
			for (int i = 0; i < m_numInputs; i++) {
				m_inputList[i].restoreWeights();
			}
		}

		// Set link for this node
		public void setLink(boolean input, int val) throws Exception {
			m_input = input;

			if (input) {
				m_type = PURE_INPUT;
			} else {
				m_type = PURE_OUTPUT;
			}
			if (val < 0
					|| (input && val > m_instances.numAttributes())
					|| (!input && m_instances.classAttribute().isNominal() && val > m_instances
							.classAttribute().numValues())) {
				m_link = 0;
			} else {
				m_link = val;
			}
		}

		public int getLink() {
			return m_link;
		}
	}

	/**
	 * a ZeroR model in case no model can be built from the data or the network
	 * predicts all zeros for the classes
	 */
	private Classifier m_ZeroR;

	private Instances m_instances;

	private Instance m_currentInstance;

	// Ranges of all the attributes.
	private double[] m_attributeRanges;

	// Base values of all the attributes.
	private double[] m_attributeBases;

	private NeuralEnd[] m_outputs;

	private NeuralEnd[] m_inputs;

	// Actual nodes
	private NeuralConnection[] m_neuralNodes;

	// Number of class labels
	private int m_numClasses = 0;

	// Number of attributes (doesn't include class attribute).
	private int m_numAttributes = 0;

	// Next id number available for default naming.
	private int m_nextId;

	// The number used to seed the random number generator.
	private int m_randomSeed;

	// The actual random number generator.
	private Random m_random;

	// The learning rate for the network.
	private double m_learningRate;

	// The momentum for the network.
	private double m_momentum;

	// A flag to state that a nominal to binary filter should be used on nominal attributes.
	private boolean m_useNomToBin;

	// The actual filter.
	private NominalToBinary m_nominalToBinaryFilter;

	// Should input values be normalized? This will normalize the attributes.
	// This could help improve performance of the network.
	// This is not reliant on the class being numeric. This will also
	// normalize nominal attributes as well (after they have been run
	// through the nominal to binary filter if that is in use) so that the
	// nominal values are between -1 and 1.
	private boolean m_normalizeAttributes;

	// The percentage size of the validation set (The training will continue until it is observed that
	//	the error on the validation set has been consistently getting worse, or if the training time is reached).
	// If this is set to zero no validation set will be used and instead the network will train for the specified number of epochs.
	private int m_valSizeInPercentage;

	// Number of epochs to train through.
	private int m_numEpochs;

	// How many times in a row the validation set error can't get better before training is terminated.
	private int m_driftThreshold;

	public CeedMultilayerPerceptron() {
		m_instances = null;
		m_currentInstance = null;
		m_outputs = new NeuralEnd[0];
		m_inputs = new NeuralEnd[0];
		m_numAttributes = 0;
		m_numClasses = 0;
		m_neuralNodes = new NeuralConnection[0];
		m_nextId = 0;
		m_random = null;
		m_randomSeed = 0;
		m_learningRate = .3;
		m_momentum = .2;
		m_useNomToBin = true;
		m_nominalToBinaryFilter = new NominalToBinary();
		m_normalizeAttributes = true;
		m_valSizeInPercentage = 0;
		m_driftThreshold = 20;
		m_numEpochs = 500;
	}

	// Add a node to the node list
	private void addNode(NeuralConnection n) {
		NeuralConnection[] temp = new NeuralConnection[m_neuralNodes.length + 1];
		for (int noa = 0; noa < m_neuralNodes.length; noa++) {
			temp[noa] = m_neuralNodes[noa];
		}

		temp[temp.length - 1] = n;
		m_neuralNodes = temp;
	}

	// Normalize attributes
	private Instances normalizeAttributes(Instances inst) throws Exception {
		if (inst != null) {
			// x bounds
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			double value;
			m_attributeRanges = new double[inst.numAttributes()];
			m_attributeBases = new double[inst.numAttributes()];
			for (int noa = 0; noa < inst.numAttributes(); noa++) {
				min = Double.POSITIVE_INFINITY;
				max = Double.NEGATIVE_INFINITY;
				for (int i = 0; i < inst.numInstances(); i++) {
					if (!inst.instance(i).isMissing(noa)) {
						value = inst.instance(i).value(noa);
						if (value < min) {
							min = value;
						}
						if (value > max) {
							max = value;
						}
					}
				}
				m_attributeRanges[noa] = (max - min) / 2;
				m_attributeBases[noa] = (max + min) / 2;
				
				// TODO: use this for what?
				if (noa != inst.classIndex() && m_normalizeAttributes) {
					for (int i = 0; i < inst.numInstances(); i++) {
						if (m_attributeRanges[noa] != 0) {
							inst.instance(i).setValue(
									noa,
									(inst.instance(i).value(noa) - m_attributeBases[noa]) / m_attributeRanges[noa]);
						} else {
							inst.instance(i).setValue(
									noa,
									inst.instance(i).value(noa) - m_attributeBases[noa]);
						}
					}
				}
			}
		}
		return inst;
	}

	// Reset all the nodes in the network.
	private void resetNetwork() {
		for (int noc = 0; noc < m_numClasses; noc++) {
			m_outputs[noc].reset();
		}
	}

	// Calculate output values of output NeuralEnd by calculating output values of all nodes.
	private void calculateOutputs() {
		for (int noc = 0; noc < m_numClasses; noc++) {
			m_outputs[noc].outputValue(true);
		}
	}

	// Calculate error values of all nodes.
	// Return the total squared error of all output units.
	// Output values should have been calculated first.
	private double calculateErrors() throws Exception {
		double ret = 0, temp = 0;
		for (int noc = 0; noc < m_numAttributes; noc++) {
			// get the errors.
			m_inputs[noc].errorValue(true);
		}
		
		for (int noc = 0; noc < m_numClasses; noc++) {
			temp = m_outputs[noc].errorValue(false);
			ret += temp * temp;
		}
		return ret;
	}

	// Update weights of all units in the network by updating in the direction from input to output
	private void updateNetworkWeights(double l, double m) {
		for (int noc = 0; noc < m_numClasses; noc++) {
			// update weights
			m_outputs[noc].updateWeights(l, m);
		}
	}

	// The last attribute is the class attribute.
	private void setupInputs() throws Exception {
		m_inputs = new NeuralEnd[m_numAttributes];
		for (int noa = 0; noa < m_numAttributes; noa++) {
			m_inputs[noa] = new NeuralEnd(m_instances.attribute(noa).name());
			m_inputs[noa].setLink(true, noa);
		}
	}

	// Each output NeuralEnd connects with one NeuralNode.
	private void setupOutputs() throws Exception {
		m_outputs = new NeuralEnd[m_numClasses];
		for (int noa = 0; noa < m_numClasses; noa++) {
			m_outputs[noa] = new NeuralEnd(m_instances.classAttribute().value(noa));
			m_outputs[noa].setLink(false, noa);
			NeuralNode temp = new NeuralNode(String.valueOf(m_nextId), m_random);
			m_nextId++;
			addNode(temp);
			NeuralConnection.connect(temp, m_outputs[noa]);
		}
	}

	// 1 hidden layer.
	private void setupHiddenLayer() {
		int val = 0; // number of nodes in the hidden layer
		
		//val = (m_numAttributes + m_numClasses) / 2;
		//val = m_numAttributes;
		//val = m_numClasses;
		val = m_numAttributes + m_numClasses;
		
		for (int nob = 0; nob < val; nob++) {
			NeuralNode temp = new NeuralNode(String.valueOf(m_nextId), m_random);
			m_nextId++;
			addNode(temp);
		}

		for (int noa = 0; noa < m_numAttributes; noa++) {
			for (int nob = m_numClasses; nob < m_numClasses + val; nob++) {
				NeuralConnection.connect(m_inputs[noa], m_neuralNodes[nob]);
			}
		}
		for (int noa = m_neuralNodes.length - val; noa < m_neuralNodes.length; noa++) {
			for (int nob = 0; nob < m_numClasses; nob++) {
				NeuralConnection.connect(m_neuralNodes[noa], m_neuralNodes[nob]);
			}
		}
	}

	// Build and train
	public void buildClassifier(Instances i) throws Exception {
		// remove instances with missing class
		i = new Instances(i);
		i.deleteWithMissingClass();

		m_ZeroR = new weka.classifiers.rules.ZeroR();
		m_ZeroR.buildClassifier(i);
		
		m_instances = new Instances(i);
		m_currentInstance = null;
		m_outputs = new NeuralEnd[0];
		m_inputs = new NeuralEnd[0];
		m_numAttributes = m_instances.numAttributes() - 1;
		m_numClasses = m_instances.numClasses();
		m_neuralNodes = new NeuralConnection[0];
		m_nextId = 0;
		m_random = new Random(m_randomSeed);
		m_instances.randomize(m_random);
		if (m_useNomToBin) {
			m_nominalToBinaryFilter = new NominalToBinary();
			m_nominalToBinaryFilter.setInputFormat(m_instances);
			m_instances = Filter.useFilter(m_instances, m_nominalToBinaryFilter);
			// TODO: check instances after being filtered to binary form
		}

		normalizeAttributes(m_instances);

		// this sets up the validation set.
		Instances valSet = null;
		// numInVal is needed later
		int numInVal = (int) (m_valSizeInPercentage / 100.0 * m_instances.numInstances());
		if (m_valSizeInPercentage > 0) {
			if (numInVal == 0) {
				numInVal = 1;
			}
			valSet = new Instances(m_instances, 0, numInVal);
		}

		setupInputs();
		setupOutputs();
		setupHiddenLayer();

		// connections done.
		double right = 0;
		double driftOff = 0;
		double lastRight = Double.POSITIVE_INFINITY;
		double bestError = Double.POSITIVE_INFINITY;
		double tempRate;
		double totalWeight = 0;
		double totalValWeight = 0;
		double origRate = m_learningRate; // only used for when reset

		// ensure that at least 1 instance is trained through.
		if (numInVal == m_instances.numInstances()) {
			numInVal--;
		}
		if (numInVal < 0) {
			numInVal = 0;
		}
		for (int noa = numInVal; noa < m_instances.numInstances(); noa++) {
			if (!m_instances.instance(noa).classIsMissing()) {
				totalWeight += m_instances.instance(noa).weight();
			}
		}
		if (m_valSizeInPercentage != 0) {
			for (int noa = 0; noa < valSet.numInstances(); noa++) {
				if (!valSet.instance(noa).classIsMissing()) {
					totalValWeight += valSet.instance(noa).weight();
				}
			}
		}
		
		// A flag to state that the network should be accepted or not yet.
		boolean m_accepted = false;
		for (int noa = 1; noa < m_numEpochs + 1; noa++) {
			right = 0;
			for (int nob = numInVal; nob < m_instances.numInstances(); nob++) {
				m_currentInstance = m_instances.instance(nob);
				if (!m_currentInstance.classIsMissing()) {
					// this is where the network updating (and training occurs,
					// for the
					// training set
					resetNetwork();
					calculateOutputs();
					// TODO: what are here?
					tempRate = m_learningRate * m_currentInstance.weight();
					right += calculateErrors() / m_instances.numClasses() * m_currentInstance.weight();
					updateNetworkWeights(tempRate, m_momentum);
				}
			}
			right /= totalWeight;
			if (Double.isInfinite(right) || Double.isNaN(right)) {
				// reset the network if possible
				if (m_learningRate <= Utils.SMALL)
					throw new IllegalStateException("Learning rate got too small (" + m_learningRate + " <= " + Utils.SMALL + ")!");
				m_learningRate /= 2;
				buildClassifier(i);
				m_learningRate = origRate;
				m_instances = new Instances(m_instances, 0);
				m_currentInstance = null;
				return;
			}

			// do validation testing if applicable
			if (m_valSizeInPercentage != 0) {
				right = 0;
				for (int nob = 0; nob < valSet.numInstances(); nob++) {
					m_currentInstance = valSet.instance(nob);
					if (!m_currentInstance.classIsMissing()) {
						// this is where the network updating occurs, for the
						// validation set
						resetNetwork();
						calculateOutputs();
						right += calculateErrors() / valSet.numClasses() * m_currentInstance.weight();
						// note 'right' could be calculated here just using
						// the calculate output values. This would be faster.
						// be less modular
					}
				}

				if (right < lastRight) {
					if (right < bestError) {
						bestError = right;
						// save the network weights at this point
						for (int noc = 0; noc < m_numClasses; noc++) {
							m_outputs[noc].saveWeights();
						}
						driftOff = 0;
					}
				} else {
					driftOff++;
				}
				lastRight = right;
				if (driftOff > m_driftThreshold || noa + 1 >= m_numEpochs) {
					for (int noc = 0; noc < m_numClasses; noc++) {
						m_outputs[noc].restoreWeights();
					}
					m_accepted = true;
				}
				right /= totalValWeight;
			}
			if (m_accepted) {
				m_instances = new Instances(m_instances, 0);
				m_currentInstance = null;
				return;
			}
		}
		m_instances = new Instances(m_instances, 0);
		m_currentInstance = null;
	}
	
	// Predict the class of an instance once a classification model has been built with the buildClassifier call.
	// return a double array filled with the probabilities of each class label.
	public double[] distributionForInstance(Instance i) throws Exception {
		if (m_useNomToBin) {
			m_nominalToBinaryFilter.input(i);
			m_currentInstance = m_nominalToBinaryFilter.output();
		} else {
			m_currentInstance = i;
		}

		if (m_normalizeAttributes) {
			for (int noa = 0; noa < m_instances.numAttributes(); noa++) {
				if (noa != m_instances.classIndex()) {
					if (m_attributeRanges[noa] != 0) {
						m_currentInstance.setValue(noa,
								(m_currentInstance.value(noa) - m_attributeBases[noa]) / m_attributeRanges[noa]);
					} else {
						m_currentInstance.setValue(noa,
								m_currentInstance.value(noa) - m_attributeBases[noa]);
					}
				}
			}
		}
		resetNetwork();

		// since all the output values are needed, they are calculated manually here and the values collected.
		double[] theArray = new double[m_numClasses];
		for (int noa = 0; noa < m_numClasses; noa++) {
			theArray[noa] = m_outputs[noa].outputValue(true);
		}

		// now normalize the array
		double count = 0;
		for (int noa = 0; noa < m_numClasses; noa++) {
			count += theArray[noa];
		}
		if (count <= 0) {
			return m_ZeroR.distributionForInstance(i);
		}
		for (int noa = 0; noa < m_numClasses; noa++) {
			theArray[noa] /= count;
		}
		return theArray;
		// TODO: check theArray
	}

	// Describe the model.
	public String toString() {
		StringBuffer model = new StringBuffer(m_neuralNodes.length * 100);
		// just a rough size guess
		NeuralNode con;
		double[] weights;
		NeuralConnection[] inputs;
		for (int noa = 0; noa < m_neuralNodes.length; noa++) {
			con = (NeuralNode) m_neuralNodes[noa]; // this would need a change for items other than nodes!!!
			weights = con.getWeights();
			inputs = con.getInputs();
			model.append("Sigmoid ");
			model.append("Node " + con.getId() + "\n    Inputs    Weights\n");
			model.append("    Threshold    " + weights[0] + "\n");
			for (int nob = 1; nob < con.getNumInputs() + 1; nob++) {
				model.append("    Attrib "
						+ m_instances.attribute(
								((NeuralEnd) inputs[nob - 1]).getLink())
								.name() + "    " + weights[nob] + "\n");
			}
		}
		// now put in the ends
		for (int noa = 0; noa < m_outputs.length; noa++) {
			inputs = m_outputs[noa].getInputs();
			model.append("Class "
					+ m_instances.classAttribute().value(
							m_outputs[noa].getLink()) + "\n    Input\n");
			for (int nob = 0; nob < m_outputs[noa].getNumInputs(); nob++) {
				model.append("    Node " + inputs[nob].getId() + "\n");
			}
		}
		return model.toString();
	}
}
