/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    MultilayerPerceptron.java
 *    Copyright (C) 2000-2012 University of Waikato, Hamilton, New Zealand
 */

import java.util.Random;
import java.util.StringTokenizer;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;

/**
 * <!-- globalinfo-start --> A Classifier that uses backpropagation to classify
 * instances.<br/>
 * This network can be built by hand, created by an algorithm or both. The
 * network can also be monitored and modified during training time. The nodes in
 * this network are all sigmoid (except for when the class is numeric in which
 * case the the output nodes become unthresholded linear units).
 * <p/>
 * <!-- globalinfo-end -->
 * 
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 * -L &lt;learning rate&gt;
 *  Learning Rate for the backpropagation algorithm.
 *  (Value should be between 0 - 1, Default = 0.3).
 * </pre>
 * 
 * <pre>
 * -M &lt;momentum&gt;
 *  Momentum Rate for the backpropagation algorithm.
 *  (Value should be between 0 - 1, Default = 0.2).
 * </pre>
 * 
 * <pre>
 * -N &lt;number of epochs&gt;
 *  Number of epochs to train through.
 *  (Default = 500).
 * </pre>
 * 
 * <pre>
 * -V &lt;percentage size of validation set&gt;
 *  Percentage size of validation set to use to terminate
 *  training (if this is non zero it can pre-empt num of epochs.
 *  (Value should be between 0 - 100, Default = 0).
 * </pre>
 * 
 * <pre>
 * -S &lt;seed&gt;
 *  The value used to seed the random number generator
 *  (Value should be &gt;= 0 and and a long, Default = 0).
 * </pre>
 * 
 * <pre>
 * -E &lt;threshold for number of consecutive errors&gt;
 *  The consecutive number of errors allowed for validation
 *  testing before the network terminates.
 *  (Value should be &gt; 0, Default = 20).
 * </pre>
 * 
 * <pre>
 * -A
 *  Autocreation of the network connections will NOT be done.
 *  (This will be ignored if -G is NOT set)
 * </pre>
 * 
 * <pre>
 * -B
 *  A NominalToBinary filter will NOT automatically be used.
 *  (Set this to not use a NominalToBinary filter).
 * </pre>
 * 
 * <pre>
 * -H &lt;comma separated numbers for nodes on each layer&gt;
 *  The hidden layers to be created for the network.
 *  (Value should be a list of comma separated Natural 
 *  numbers or the letters 'a' = (attribs + classes) / 2, 
 *  'i' = attribs, 'o' = classes, 't' = attribs .+ classes)
 *  for wildcard values, Default = a).
 * </pre>
 * 
 * <pre>
 * -I
 *  Normalizing the attributes will NOT be done.
 *  (Set this to not normalize the attributes).
 * </pre>
 * 
 * <pre>
 * -R
 *  Reseting the network will NOT be allowed.
 *  (Set this to not allow the network to reset).
 * </pre>
 * 
 * <pre>
 * -D
 *  Learning rate decay will occur.
 *  (Set this to cause the learning rate to decay).
 * </pre>
 * 
 * <!-- options-end -->
 * 
 * @author Malcolm Ware (mfw4@cs.waikato.ac.nz)
 * @version $Revision: 9444 $
 */
public class MultilayerPerceptron extends Classifier implements OptionHandler,
		WeightedInstancesHandler, Randomizable {

	/** for serialization */
	private static final long serialVersionUID = -5990607817048210779L;

	/**
	 * Main method for testing this class.
	 * 
	 * @param argv
	 *            should contain command line options (see setOptions)
	 */
	public static void main(String[] argv) {
		runClassifier(new MultilayerPerceptron(), argv);
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
			if (((e.getMessage() != null) && (e.getMessage().indexOf(
					"General options") == -1))
					|| (e.getMessage() == null))
				e.printStackTrace();
			else
				System.err.println(e.getMessage());
		}
	}

	/**
	 * This inner class is used to connect the nodes in the network up to the
	 * data that they are classifying, Note that objects of this class are only
	 * suitable to go on the attribute side or class side of the network and not
	 * both.
	 */
	protected class NeuralEnd extends NeuralConnection {

		/** for serialization */
		static final long serialVersionUID = 7305185603191183338L;

		/**
		 * the value that represents the instance value this node represents.
		 * For an input it is the attribute number, for an output, if nominal it
		 * is the class value.
		 */
		private int m_link;

		/** True if node is an input, False if it's an output. */
		private boolean m_input;

		/**
		 * Constructor
		 */
		public NeuralEnd(String id) {
			super(id);

			m_link = 0;
			m_input = true;

		}

		/**
		 * Call this to get the output value of this unit.
		 * 
		 * @param calculate
		 *            True if the value should be calculated if it hasn't been
		 *            already.
		 * @return The output value, or NaN, if the value has not been
		 *         calculated.
		 */
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
				}
			}
			return m_unitValue;
		}

		/**
		 * Call this to get the error value of this unit, which in this case is
		 * the difference between the predicted class, and the actual class.
		 * 
		 * @param calculate
		 *            True if the value should be calculated if it hasn't been
		 *            already.
		 * @return The error value, or NaN, if the value has not been
		 *         calculated.
		 */
		public double errorValue(boolean calculate) {
			if (!Double.isNaN(m_unitValue) && Double.isNaN(m_unitError) && calculate) {
				if (m_input) {
					m_unitError = 0;
					for (int noa = 0; noa < m_numOutputs; noa++) {
						m_unitError += m_outputList[noa].errorValue(true);
					}
				} else {
					if (m_currentInstance.classIsMissing()) {
						m_unitError = .1;
					} else if (m_instances.classAttribute().isNominal()) {
						if (m_currentInstance.classValue() == m_link) {
							m_unitError = 1 - m_unitValue;
						} else {
							m_unitError = 0 - m_unitValue;
						}
					}
				}
			}
			return m_unitError;
		}

		/**
		 * Call this to reset the value and error for this unit, ready for the
		 * next run. This will also call the reset function of all units that
		 * are connected as inputs to this one. This is also the time that the
		 * update for the listeners will be performed.
		 */
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

		/**
		 * Call this to have the connection save the current weights.
		 */
		public void saveWeights() {
			for (int i = 0; i < m_numInputs; i++) {
				m_inputList[i].saveWeights();
			}
		}

		/**
		 * Call this to have the connection restore from the saved weights.
		 */
		public void restoreWeights() {
			for (int i = 0; i < m_numInputs; i++) {
				m_inputList[i].restoreWeights();
			}
		}

		/**
		 * Call this function to set What this end unit represents.
		 * 
		 * @param input
		 *            True if this unit is used for entering an attribute, False
		 *            if it's used for determining a class value.
		 * @param val
		 *            The attribute number or class type that this unit
		 *            represents. (for nominal attributes).
		 */
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

		/**
		 * @return link for this node.
		 */
		public int getLink() {
			return m_link;
		}
	}

	/**
	 * a ZeroR model in case no model can be built from the data or the network
	 * predicts all zeros for the classes
	 */
	private Classifier m_ZeroR;

	/** Whether to use the default ZeroR model */
	private boolean m_useDefaultModel = false;

	/** The training instances. */
	private Instances m_instances;

	/** The current instance running through the network. */
	private Instance m_currentInstance;

	/** The ranges for all the attributes. */
	private double[] m_attributeRanges;

	/** The base values for all the attributes. */
	private double[] m_attributeBases;

	/** The output units.(only feeds the errors, does no calculation) */
	private NeuralEnd[] m_outputs;

	/** The input units.(only feeds the inputs, does no calculation) */
	private NeuralEnd[] m_inputs;

	/** All the nodes that actually comprise the logical neural net. */
	private NeuralConnection[] m_neuralNodes;

	/** The number of classes. */
	private int m_numClasses = 0;

	/** The number of attributes. */
	private int m_numAttributes = 0; // note the number doesn't include the class.

	/** The next id number available for default naming. */
	private int m_nextId;

	/** The number of epochs to train through. */
	private int m_numEpochs;

	/** a flag to state that the network should be accepted the way it is. */
	private boolean m_accepted;

	/** An int to say how big the validation set should be. */
	private int m_valSize;

	/** The number to to use to quit on validation testing. */
	private int m_driftThreshold;

	/** The number used to seed the random number generator. */
	private int m_randomSeed;

	/** The actual random number generator. */
	private Random m_random;

	/** A flag to state that a nominal to binary filter should be used. */
	private boolean m_useNomToBin;

	/** The actual filter. */
	private NominalToBinary m_nominalToBinaryFilter;

	/** The string that defines the hidden layers */
	private String m_hiddenLayers;

	/** This flag states that the user wants the input values normalized. */
	private boolean m_normalizeAttributes;

	/** This flag states that the user wants the learning rate to decay. */
	private boolean m_decay;

	/** This is the learning rate for the network. */
	private double m_learningRate;

	/** This is the momentum for the network. */
	private double m_momentum;

	/**
	 * This flag states that the user wants the network to restart if it is
	 * found to be generating infinity or NaN for the error value. This would
	 * restart the network with the current options except that the learning
	 * rate would be smaller than before, (perhaps half of its current value).
	 * This option will not be available if the gui is chosen (if the gui is
	 * open the user can fix the network themselves, it is an architectural
	 * minefield for the network to be reset with the gui open).
	 */
	private boolean m_reset;

	/**
	 * this is a sigmoid unit.
	 */
	private SigmoidUnit m_sigmoidUnit;

	/**
	 * This is a linear unit.
	 */
	private LinearUnit m_linearUnit;

	/**
	 * The constructor.
	 */
	public MultilayerPerceptron() {
		m_instances = null;
		m_currentInstance = null;
		
		m_outputs = new NeuralEnd[0];
		m_inputs = new NeuralEnd[0];
		m_numAttributes = 0;
		m_numClasses = 0;
		m_neuralNodes = new NeuralConnection[0];
		m_nextId = 0;
		m_accepted = false;
		m_random = null;
		m_nominalToBinaryFilter = new NominalToBinary();
		m_sigmoidUnit = new SigmoidUnit();
		m_linearUnit = new LinearUnit();
		// setting all the options to their defaults. To completely change these
		// defaults they will also need to be changed down the bottom in the
		// set options function (the text info in the accompanying functions
		// should also be changed to reflect the new defaults
		m_normalizeAttributes = true;
		m_useNomToBin = true;
		m_driftThreshold = 20;
		m_numEpochs = 500;
		m_valSize = 0;
		m_randomSeed = 0;
		m_hiddenLayers = "a";
		m_learningRate = .3;
		m_momentum = .2;
		m_reset = true;
		m_decay = false;
	}

	/**
	 * @param d
	 *            True if the learning rate should decay.
	 */
	public void setDecay(boolean d) {
		m_decay = d;
	}

	/**
	 * @return the flag for having the learning rate decay.
	 */
	public boolean getDecay() {
		return m_decay;
	}

	/**
	 * This sets the network up to be able to reset itself with the current
	 * settings and the learning rate at half of what it is currently. This will
	 * only happen if the network creates NaN or infinite errors. Also this will
	 * continue to happen until the network is trained properly. The learning
	 * rate will also get set back to it's original value at the end of this.
	 * This can only be set to true if the GUI is not brought up.
	 * 
	 * @param r
	 *            True if the network should restart with it's current options
	 *            and set the learning rate to half what it currently is.
	 */
	public void setReset(boolean r) {
		m_reset = r;
	}

	/**
	 * @return The flag for reseting the network.
	 */
	public boolean getReset() {
		return m_reset;
	}

	/**
	 * @param a
	 *            True if the attributes should be normalized (even nominal
	 *            attributes will get normalized here) (range goes between -1 -
	 *            1).
	 */
	public void setNormalizeAttributes(boolean a) {
		m_normalizeAttributes = a;
	}

	/**
	 * @return The flag for normalizing attributes.
	 */
	public boolean getNormalizeAttributes() {
		return m_normalizeAttributes;
	}

	/**
	 * @param f
	 *            True if a nominalToBinary filter should be used on the data.
	 */
	public void setNominalToBinaryFilter(boolean f) {
		m_useNomToBin = f;
	}

	/**
	 * @return The flag for nominal to binary filter use.
	 */
	public boolean getNominalToBinaryFilter() {
		return m_useNomToBin;
	}

	/**
	 * This seeds the random number generator, that is used when a random number
	 * is needed for the network.
	 * 
	 * @param l
	 *            The seed.
	 */
	public void setSeed(int l) {
		if (l >= 0) {
			m_randomSeed = l;
		}
	}

	/**
	 * @return The seed for the random number generator.
	 */
	public int getSeed() {
		return m_randomSeed;
	}

	/**
	 * This sets the threshold to use for when validation testing is being done.
	 * It works by ending testing once the error on the validation set has
	 * consecutively increased a certain number of times.
	 * 
	 * @param t
	 *            The threshold to use for this.
	 */
	public void setValidationThreshold(int t) {
		if (t > 0) {
			m_driftThreshold = t;
		}
	}

	/**
	 * @return The threshold used for validation testing.
	 */
	public int getValidationThreshold() {
		return m_driftThreshold;
	}

	/**
	 * The learning rate can be set using this command. NOTE That this is a
	 * static variable so it affect all networks that are running. Must be
	 * greater than 0 and no more than 1.
	 * 
	 * @param l
	 *            The New learning rate.
	 */
	public void setLearningRate(double l) {
		if (l > 0 && l <= 1) {
			m_learningRate = l;
		}
	}

	/**
	 * @return The learning rate for the nodes.
	 */
	public double getLearningRate() {
		return m_learningRate;
	}

	/**
	 * The momentum can be set using this command. THE same conditions apply to
	 * this as to the learning rate.
	 * 
	 * @param m
	 *            The new Momentum.
	 */
	public void setMomentum(double m) {
		if (m >= 0 && m <= 1) {
			m_momentum = m;
		}
	}

	/**
	 * @return The momentum for the nodes.
	 */
	public double getMomentum() {
		return m_momentum;
	}

	/**
	 * This will set what the hidden layers are made up of when auto build is
	 * enabled. Note to have no hidden units, just put a single 0, Any more 0's
	 * will indicate that the string is badly formed and make it unaccepted.
	 * Negative numbers, and floats will do the same. There are also some
	 * wildcards. These are 'a' = (number of attributes + number of classes) /
	 * 2, 'i' = number of attributes, 'o' = number of classes, and 't' = number
	 * of attributes + number of classes.
	 * 
	 * @param h
	 *            A string with a comma separated list of numbers. Each number
	 *            is the number of nodes to be on a hidden layer.
	 */
	public void setHiddenLayers(String h) {
		String tmp = "";
		StringTokenizer tok = new StringTokenizer(h, ",");
		if (tok.countTokens() == 0) {
			return;
		}
		double dval;
		int val;
		String c;
		boolean first = true;
		while (tok.hasMoreTokens()) {
			c = tok.nextToken().trim();

			if (c.equals("a") || c.equals("i") || c.equals("o") || c.equals("t")) {
				tmp += c;
			} else {
				dval = Double.valueOf(c).doubleValue();
				val = (int) dval;

				if ((val == dval
						&& (val != 0 || (tok.countTokens() == 0 && first)) && val >= 0)) {
					tmp += val;
				} else {
					return;
				}
			}

			first = false;
			if (tok.hasMoreTokens()) {
				tmp += ", ";
			}
		}
		m_hiddenLayers = tmp;
	}

	/**
	 * @return A string representing the hidden layers, each number is the
	 *         number of nodes on a hidden layer.
	 */
	public String getHiddenLayers() {
		return m_hiddenLayers;
	}

	/**
	 * This will set the size of the validation set.
	 * 
	 * @param a
	 *            The size of the validation set, as a percentage of the whole.
	 */
	public void setValidationSetSize(int a) {
		if (a < 0 || a > 99) {
			return;
		}
		m_valSize = a;
	}

	/**
	 * @return The percentage size of the validation set.
	 */
	public int getValidationSetSize() {
		return m_valSize;
	}

	/**
	 * Set the number of training epochs to perform. Must be greater than 0.
	 * 
	 * @param n
	 *            The number of epochs to train through.
	 */
	public void setTrainingTime(int n) {
		if (n > 0) {
			m_numEpochs = n;
		}
	}

	/**
	 * @return The number of epochs to train through.
	 */
	public int getTrainingTime() {
		return m_numEpochs;
	}

	/**
	 * Call this function to place a node into the network list.
	 * 
	 * @param n
	 *            The node to place in the list.
	 */
	private void addNode(NeuralConnection n) {

		NeuralConnection[] temp1 = new NeuralConnection[m_neuralNodes.length + 1];
		for (int noa = 0; noa < m_neuralNodes.length; noa++) {
			temp1[noa] = m_neuralNodes[noa];
		}

		temp1[temp1.length - 1] = n;
		m_neuralNodes = temp1;
	}

	/**
	 * This function sets what the m_numeric flag to represent the passed class
	 * it also performs the normalization of the attributes if applicable and
	 * sets up the info to normalize the class. (note that regardless of the
	 * options it will fill an array with the range and base, set to normalize
	 * all attributes and the class to be between -1 and 1)
	 * 
	 * @param inst
	 *            the instances.
	 * @return The modified instances. This needs to be done. If the attributes
	 *         are normalized then deep copies will be made of all the instances
	 *         which will need to be passed back out.
	 */
	private Instances setClassType(Instances inst) throws Exception {
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

	/**
	 * A function used to stop the code that called build classifier from
	 * continuing on before the user has finished the decision tree.
	 * 
	 * @param tf
	 *            True to stop the thread, False to release the thread that is
	 *            waiting there (if one).
	 */
	public synchronized void blocker(boolean tf) {
		if (tf) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		} else {
			notifyAll();
		}
	}

	/**
	 * this will reset all the nodes in the network.
	 */
	private void resetNetwork() {
		for (int noc = 0; noc < m_numClasses; noc++) {
			m_outputs[noc].reset();
		}
	}

	/**
	 * This will cause the output values of all the nodes to be calculated. Note
	 * that the m_currentInstance is used to calculate these values.
	 */
	private void calculateOutputs() {
		for (int noc = 0; noc < m_numClasses; noc++) {
			// get the values.
			m_outputs[noc].outputValue(true);
		}
	}

	/**
	 * This will cause the error values to be calculated for all nodes. Note
	 * that the m_currentInstance is used to calculate these values. Also the
	 * output values should have been calculated first.
	 * 
	 * @return The squared error.
	 */
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

	/**
	 * This will cause the weight values to be updated based on the learning
	 * rate, momentum and the errors that have been calculated for each node.
	 * 
	 * @param l
	 *            The learning rate to update with.
	 * @param m
	 *            The momentum to update with.
	 */
	private void updateNetworkWeights(double l, double m) {
		for (int noc = 0; noc < m_numClasses; noc++) {
			// update weights
			m_outputs[noc].updateWeights(l, m);
		}
	}

	/**
	 * This creates the required input units.
	 */
	private void setupInputs() throws Exception {
		m_inputs = new NeuralEnd[m_numAttributes];
		int now = 0;
		for (int noa = 0; noa < m_numAttributes + 1; noa++) {
			if (m_instances.classIndex() != noa) {
				m_inputs[noa - now] = new NeuralEnd(m_instances.attribute(noa).name());
				m_inputs[noa - now].setLink(true, noa);
			} else {
				now = 1;
			}
		}

	}

	/**
	 * This creates the required output units.
	 */
	private void setupOutputs() throws Exception {
		m_outputs = new NeuralEnd[m_numClasses];
		for (int noa = 0; noa < m_numClasses; noa++) {
			m_outputs[noa] = new NeuralEnd(m_instances.classAttribute().value(noa));

			m_outputs[noa].setLink(false, noa);
			NeuralNode temp = new NeuralNode(String.valueOf(m_nextId),
					m_random, m_sigmoidUnit);
			m_nextId++;
			addNode(temp);
			NeuralConnection.connect(temp, m_outputs[noa]);
		}
	}

	/**
	 * Call this function to automatically generate the hidden units
	 */
	private void setupHiddenLayer() {
		StringTokenizer tok = new StringTokenizer(m_hiddenLayers, ",");
		int val = 0; // num of nodes in a layer
		int prev = 0; // used to remember the previous layer
		int num = tok.countTokens(); // number of layers
		String c;
		for (int noa = 0; noa < num; noa++) {
			// note that I am using the Double to get the value rather than the
			// Integer class, because for some reason the Double implementation
			// can
			// handle leading white space and the integer version can't!?!
			c = tok.nextToken().trim();
			if (c.equals("a")) {
				val = (m_numAttributes + m_numClasses) / 2;
			} else if (c.equals("i")) {
				val = m_numAttributes;
			} else if (c.equals("o")) {
				val = m_numClasses;
			} else if (c.equals("t")) {
				val = m_numAttributes + m_numClasses;
			} else {
				val = Double.valueOf(c).intValue();
			}
			for (int nob = 0; nob < val; nob++) {
				NeuralNode temp = new NeuralNode(String.valueOf(m_nextId),
						m_random, m_sigmoidUnit);
				m_nextId++;
				addNode(temp);
				if (noa > 0) {
					// then do connections
					for (int noc = m_neuralNodes.length - nob - 1 - prev; noc < m_neuralNodes.length
							- nob - 1; noc++) {
						NeuralConnection.connect(m_neuralNodes[noc], temp);
					}
				}
			}
			prev = val;
		}
		tok = new StringTokenizer(m_hiddenLayers, ",");
		c = tok.nextToken();
		if (c.equals("a")) {
			val = (m_numAttributes + m_numClasses) / 2;
		} else if (c.equals("i")) {
			val = m_numAttributes;
		} else if (c.equals("o")) {
			val = m_numClasses;
		} else if (c.equals("t")) {
			val = m_numAttributes + m_numClasses;
		} else {
			val = Double.valueOf(c).intValue();
		}

		if (val == 0) {
			for (int noa = 0; noa < m_numAttributes; noa++) {
				for (int nob = 0; nob < m_numClasses; nob++) {
					NeuralConnection.connect(m_inputs[noa], m_neuralNodes[nob]);
				}
			}
		} else {
			for (int noa = 0; noa < m_numAttributes; noa++) {
				for (int nob = m_numClasses; nob < m_numClasses + val; nob++) {
					NeuralConnection.connect(m_inputs[noa], m_neuralNodes[nob]);
				}
			}
			for (int noa = m_neuralNodes.length - prev; noa < m_neuralNodes.length; noa++) {
				for (int nob = 0; nob < m_numClasses; nob++) {
					NeuralConnection.connect(m_neuralNodes[noa],
							m_neuralNodes[nob]);
				}
			}
		}

	}

	/**
	 * This will go through all the nodes and check if they are connected to a
	 * pure output unit. If so they will be set to be linear units. If not they
	 * will be set to be sigmoid units.
	 */
	private void setEndsToLinear() {
		for (int noa = 0; noa < m_neuralNodes.length; noa++) {
			if ((m_neuralNodes[noa].getType() & NeuralConnection.OUTPUT) == NeuralConnection.OUTPUT) {
				((NeuralNode) m_neuralNodes[noa]).setMethod(m_linearUnit);
			} else {
				((NeuralNode) m_neuralNodes[noa]).setMethod(m_sigmoidUnit);
			}
		}
	}

	/**
	 * Returns default capabilities of the classifier.
	 * 
	 * @return the capabilities of this classifier
	 */
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.DATE_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.NUMERIC_CLASS);
		result.enable(Capability.DATE_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);

		return result;
	}

	/**
	 * Call this function to build and train a neural network for the training
	 * data provided.
	 * 
	 * @param i
	 *            The training data.
	 * @throws Exception
	 *             if can't build classification properly.
	 */
	public void buildClassifier(Instances i) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(i);

		// remove instances with missing class
		i = new Instances(i);
		i.deleteWithMissingClass();

		m_ZeroR = new weka.classifiers.rules.ZeroR();
		m_ZeroR.buildClassifier(i);
		// only class? -> use ZeroR model
		if (i.numAttributes() == 1) {
			System.err
					.println("Cannot build model (only class attribute present in data!), "
							+ "using ZeroR model instead!");
			m_useDefaultModel = true;
			return;
		} else {
			m_useDefaultModel = false;
		}

		m_instances = null;
		m_currentInstance = null;

		m_outputs = new NeuralEnd[0];
		m_inputs = new NeuralEnd[0];
		m_numAttributes = 0;
		m_numClasses = 0;
		m_neuralNodes = new NeuralConnection[0];

		m_nextId = 0;
		m_accepted = false;
		m_instances = new Instances(i);
		m_random = new Random(m_randomSeed);
		m_instances.randomize(m_random);

		if (m_useNomToBin) {
			m_nominalToBinaryFilter = new NominalToBinary();
			m_nominalToBinaryFilter.setInputFormat(m_instances);
			m_instances = Filter
					.useFilter(m_instances, m_nominalToBinaryFilter);
		}
		m_numAttributes = m_instances.numAttributes() - 1;
		m_numClasses = m_instances.numClasses();

		setClassType(m_instances);

		// this sets up the validation set.
		Instances valSet = null;
		// numinval is needed later
		int numInVal = (int) (m_valSize / 100.0 * m_instances.numInstances());
		if (m_valSize > 0) {
			if (numInVal == 0) {
				numInVal = 1;
			}
			valSet = new Instances(m_instances, 0, numInVal);
		}
		// /////////

		setupInputs();

		setupOutputs();
		setupHiddenLayer();

		// For silly situations in which the network gets accepted before
		// training commences
		if (m_numeric) {
			setEndsToLinear();
		}
		if (m_accepted) {
			m_instances = new Instances(m_instances, 0);
			m_currentInstance = null;
			return;
		}

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
		if (m_valSize != 0) {
			for (int noa = 0; noa < valSet.numInstances(); noa++) {
				if (!valSet.instance(noa).classIsMissing()) {
					totalValWeight += valSet.instance(noa).weight();
				}
			}
		}
		
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
					tempRate = m_learningRate * m_currentInstance.weight();
					if (m_decay) {
						tempRate /= noa;
					}

					right += (calculateErrors() / m_instances.numClasses())
							* m_currentInstance.weight();
					updateNetworkWeights(tempRate, m_momentum);

				}

			}
			right /= totalWeight;
			if (Double.isInfinite(right) || Double.isNaN(right)) {
				if (!m_reset) {
					m_instances = null;
					throw new Exception(
							"Network cannot train. Try restarting with a"
									+ " smaller learning rate.");
				} else {
					// reset the network if possible
					if (m_learningRate <= Utils.SMALL)
						throw new IllegalStateException(
								"Learning rate got too small ("
										+ m_learningRate + " <= " + Utils.SMALL
										+ ")!");
					m_learningRate /= 2;
					buildClassifier(i);
					m_learningRate = origRate;
					m_instances = new Instances(m_instances, 0);
					m_currentInstance = null;
					return;
				}
			}

			// //////////////////////do validation testing if applicable
			if (m_valSize != 0) {
				right = 0;
				for (int nob = 0; nob < valSet.numInstances(); nob++) {
					m_currentInstance = valSet.instance(nob);
					if (!m_currentInstance.classIsMissing()) {
						// this is where the network updating occurs, for the
						// validation set
						resetNetwork();
						calculateOutputs();
						right += (calculateErrors() / valSet.numClasses())
								* m_currentInstance.weight();
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

	/**
	 * Call this function to predict the class of an instance once a
	 * classification model has been built with the buildClassifier call.
	 * 
	 * @param i
	 *            The instance to classify.
	 * @return A double array filled with the probabilities of each class type.
	 * @throws Exception
	 *             if can't classify instance.
	 */
	public double[] distributionForInstance(Instance i) throws Exception {

		// default model?
		if (m_useDefaultModel) {
			return m_ZeroR.distributionForInstance(i);
		}

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
						m_currentInstance
								.setValue(
										noa,
										(m_currentInstance.value(noa) - m_attributeBases[noa])
												/ m_attributeRanges[noa]);
					} else {
						m_currentInstance.setValue(noa,
								m_currentInstance.value(noa)
										- m_attributeBases[noa]);
					}
				}
			}
		}
		resetNetwork();

		// since all the output values are needed.
		// They are calculated manually here and the values collected.
		double[] theArray = new double[m_numClasses];
		for (int noa = 0; noa < m_numClasses; noa++) {
			theArray[noa] = m_outputs[noa].outputValue(true);
		}
		if (m_instances.classAttribute().isNumeric()) {
			return theArray;
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
	}

	/**
	 * Parses a given list of options.
	 * <p/>
	 * 
	 * <!-- options-start --> Valid options are:
	 * <p/>
	 * 
	 * <pre>
	 * -L &lt;learning rate&gt;
	 *  Learning Rate for the backpropagation algorithm.
	 *  (Value should be between 0 - 1, Default = 0.3).
	 * </pre>
	 * 
	 * <pre>
	 * -M &lt;momentum&gt;
	 *  Momentum Rate for the backpropagation algorithm.
	 *  (Value should be between 0 - 1, Default = 0.2).
	 * </pre>
	 * 
	 * <pre>
	 * -N &lt;number of epochs&gt;
	 *  Number of epochs to train through.
	 *  (Default = 500).
	 * </pre>
	 * 
	 * <pre>
	 * -V &lt;percentage size of validation set&gt;
	 *  Percentage size of validation set to use to terminate
	 *  training (if this is non zero it can pre-empt num of epochs.
	 *  (Value should be between 0 - 100, Default = 0).
	 * </pre>
	 * 
	 * <pre>
	 * -S &lt;seed&gt;
	 *  The value used to seed the random number generator
	 *  (Value should be &gt;= 0 and and a long, Default = 0).
	 * </pre>
	 * 
	 * <pre>
	 * -E &lt;threshold for number of consequetive errors&gt;
	 *  The consequetive number of errors allowed for validation
	 *  testing before the netwrok terminates.
	 *  (Value should be &gt; 0, Default = 20).
	 * </pre>
	 * 
	 * <pre>
	 * -B
	 *  A NominalToBinary filter will NOT automatically be used.
	 *  (Set this to not use a NominalToBinary filter).
	 * </pre>
	 * 
	 * <pre>
	 * -H &lt;comma separated numbers for nodes on each layer&gt;
	 *  The hidden layers to be created for the network.
	 *  (Value should be a list of comma separated Natural 
	 *  numbers or the letters 'a' = (attribs + classes) / 2, 
	 *  'i' = attribs, 'o' = classes, 't' = attribs .+ classes)
	 *  for wildcard values, Default = a).
	 * </pre>
	 * 
	 * <pre>
	 * -I
	 *  Normalizing the attributes will NOT be done.
	 *  (Set this to not normalize the attributes).
	 * </pre>
	 * 
	 * <pre>
	 * -R
	 *  Reseting the network will NOT be allowed.
	 *  (Set this to not allow the network to reset).
	 * </pre>
	 * 
	 * <pre>
	 * -D
	 *  Learning rate decay will occur.
	 *  (Set this to cause the learning rate to decay).
	 * </pre>
	 * 
	 * <!-- options-end -->
	 * 
	 * @param options
	 *            the list of options as an array of strings
	 * @throws Exception
	 *             if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {
		// the defaults can be found here!!!!
		String learningString = Utils.getOption('L', options);
		if (learningString.length() != 0) {
			setLearningRate((new Double(learningString)).doubleValue());
		} else {
			setLearningRate(0.3);
		}
		String momentumString = Utils.getOption('M', options);
		if (momentumString.length() != 0) {
			setMomentum((new Double(momentumString)).doubleValue());
		} else {
			setMomentum(0.2);
		}
		String epochsString = Utils.getOption('N', options);
		if (epochsString.length() != 0) {
			setTrainingTime(Integer.parseInt(epochsString));
		} else {
			setTrainingTime(500);
		}
		String valSizeString = Utils.getOption('V', options);
		if (valSizeString.length() != 0) {
			setValidationSetSize(Integer.parseInt(valSizeString));
		} else {
			setValidationSetSize(0);
		}
		String seedString = Utils.getOption('S', options);
		if (seedString.length() != 0) {
			setSeed(Integer.parseInt(seedString));
		} else {
			setSeed(0);
		}
		String thresholdString = Utils.getOption('E', options);
		if (thresholdString.length() != 0) {
			setValidationThreshold(Integer.parseInt(thresholdString));
		} else {
			setValidationThreshold(20);
		}
		String hiddenLayers = Utils.getOption('H', options);
		if (hiddenLayers.length() != 0) {
			setHiddenLayers(hiddenLayers);
		} else {
			setHiddenLayers("a");
		}
		if (Utils.getFlag('B', options)) {
			setNominalToBinaryFilter(false);
		} else {
			setNominalToBinaryFilter(true);
		}
		if (Utils.getFlag('I', options)) {
			setNormalizeAttributes(false);
		} else {
			setNormalizeAttributes(true);
		}
		if (Utils.getFlag('R', options)) {
			setReset(false);
		} else {
			setReset(true);
		}
		if (Utils.getFlag('D', options)) {
			setDecay(true);
		} else {
			setDecay(false);
		}

		Utils.checkForRemainingOptions(options);
	}

	/**
	 * Gets the current settings of NeuralNet.
	 * 
	 * @return an array of strings suitable for passing to setOptions()
	 */
	public String[] getOptions() {

		String[] options = new String[21];
		int current = 0;
		options[current++] = "-L";
		options[current++] = "" + getLearningRate();
		options[current++] = "-M";
		options[current++] = "" + getMomentum();
		options[current++] = "-N";
		options[current++] = "" + getTrainingTime();
		options[current++] = "-V";
		options[current++] = "" + getValidationSetSize();
		options[current++] = "-S";
		options[current++] = "" + getSeed();
		options[current++] = "-E";
		options[current++] = "" + getValidationThreshold();
		options[current++] = "-H";
		options[current++] = getHiddenLayers();
		if (!getNominalToBinaryFilter()) {
			options[current++] = "-B";
		}
		if (!getNormalizeAttributes()) {
			options[current++] = "-I";
		}
		if (!getReset()) {
			options[current++] = "-R";
		}
		if (getDecay()) {
			options[current++] = "-D";
		}

		while (current < options.length) {
			options[current++] = "";
		}
		return options;
	}

	/**
	 * @return string describing the model.
	 */
	public String toString() {
		// only ZeroR model?
		if (m_useDefaultModel) {
			StringBuffer buf = new StringBuffer();
			buf.append(this.getClass().getName().replaceAll(".*\\.", "") + "\n");
			buf.append(this.getClass().getName().replaceAll(".*\\.", "")
					.replaceAll(".", "=")
					+ "\n\n");
			buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
			buf.append(m_ZeroR.toString());
			return buf.toString();
		}

		StringBuffer model = new StringBuffer(m_neuralNodes.length * 100);
		// just a rough size guess
		NeuralNode con;
		double[] weights;
		NeuralConnection[] inputs;
		for (int noa = 0; noa < m_neuralNodes.length; noa++) {
			con = (NeuralNode) m_neuralNodes[noa]; // this would need a change
													// for items other than
													// nodes!!!
			weights = con.getWeights();
			inputs = con.getInputs();
			if (con.getMethod() instanceof SigmoidUnit) {
				model.append("Sigmoid ");
			} else if (con.getMethod() instanceof LinearUnit) {
				model.append("Linear ");
			}
			model.append("Node " + con.getId() + "\n    Inputs    Weights\n");
			model.append("    Threshold    " + weights[0] + "\n");
			for (int nob = 1; nob < con.getNumInputs() + 1; nob++) {
				if ((inputs[nob - 1].getType() & NeuralConnection.PURE_INPUT) == NeuralConnection.PURE_INPUT) {
					model.append("    Attrib "
							+ m_instances.attribute(
									((NeuralEnd) inputs[nob - 1]).getLink())
									.name() + "    " + weights[nob] + "\n");
				} else {
					model.append("    Node " + inputs[nob - 1].getId() + "    "
							+ weights[nob] + "\n");
				}
			}
		}
		// now put in the ends
		for (int noa = 0; noa < m_outputs.length; noa++) {
			inputs = m_outputs[noa].getInputs();
			model.append("Class "
					+ m_instances.classAttribute().value(
							m_outputs[noa].getLink()) + "\n    Input\n");
			for (int nob = 0; nob < m_outputs[noa].getNumInputs(); nob++) {
				if ((inputs[nob].getType() & NeuralConnection.PURE_INPUT) == NeuralConnection.PURE_INPUT) {
					model.append("    Attrib "
							+ m_instances.attribute(
									((NeuralEnd) inputs[nob]).getLink()).name()
							+ "\n");
				} else {
					model.append("    Node " + inputs[nob].getId() + "\n");
				}
			}
		}
		return model.toString();
	}

	/**
	 * This will return a string describing the classifier.
	 * 
	 * @return The string.
	 */
	public String globalInfo() {
		return "A Classifier that uses backpropagation to classify instances.\n"
				+ "This network can be built by hand, created by an algorithm or both. "
				+ "The network can also be monitored and modified during training time. "
				+ "The nodes in this network are all sigmoid (except for when the class "
				+ "is numeric in which case the the output nodes become unthresholded "
				+ "linear units).";
	}

	/**
	 * @return a string to describe the learning rate option.
	 */
	public String learningRateTipText() {
		return "The amount the" + " weights are updated.";
	}

	/**
	 * @return a string to describe the momentum option.
	 */
	public String momentumTipText() {
		return "Momentum applied to the weights during updating.";
	}

	/**
	 * @return a string to describe the random seed option.
	 */
	public String seedTipText() {
		return "Seed used to initialise the random number generator."
				+ "Random numbers are used for setting the initial weights of the"
				+ " connections betweem nodes, and also for shuffling the training data.";
	}

	/**
	 * @return a string to describe the validation threshold option.
	 */
	public String validationThresholdTipText() {
		return "Used to terminate validation testing."
				+ "The value here dictates how many times in a row the validation set"
				+ " error can get worse before training is terminated.";
	}

	/**
	 * @return a string to describe the validation size option.
	 */
	public String validationSetSizeTipText() {
		return "The percentage size of the validation set."
				+ "(The training will continue until it is observed that"
				+ " the error on the validation set has been consistently getting"
				+ " worse, or if the training time is reached).\n"
				+ "If This is set to zero no validation set will be used and instead"
				+ " the network will train for the specified number of epochs.";
	}

	/**
	 * @return a string to describe the learning rate option.
	 */
	public String trainingTimeTipText() {
		return "The number of epochs to train through."
				+ " If the validation set is non-zero then it can terminate the network"
				+ " early";
	}

	/**
	 * @return a string to describe the nominal to binary option.
	 */
	public String nominalToBinaryFilterTipText() {
		return "This will preprocess the instances with the filter."
				+ " This could help improve performance if there are nominal attributes"
				+ " in the data.";
	}

	/**
	 * @return a string to describe the hidden layers in the network.
	 */
	public String hiddenLayersTipText() {
		return "This defines the hidden layers of the neural network."
				+ " This is a list of positive whole numbers. 1 for each hidden layer."
				+ " Comma separated. To have no hidden layers put a single 0 here."
				+ " This will only be used if autobuild is set. There are also wildcard"
				+ " values 'a' = (attribs + classes) / 2, 'i' = attribs, 'o' = classes"
				+ " , 't' = attribs + classes.";
	}

	/**
	 * @return a string to describe the nominal to binary option.
	 */
	public String normalizeAttributesTipText() {
		return "This will normalize the attributes."
				+ " This could help improve performance of the network."
				+ " This is not reliant on the class being numeric. This will also"
				+ " normalize nominal attributes as well (after they have been run"
				+ " through the nominal to binary filter if that is in use) so that the"
				+ " nominal values are between -1 and 1";
	}

	/**
	 * @return a string to describe the Reset option.
	 */
	public String resetTipText() {
		return "This will allow the network to reset with a lower learning rate."
				+ " If the network diverges from the answer this will automatically"
				+ " reset the network with a lower learning rate and begin training"
				+ " again. This option is only available if the gui is not set. Note"
				+ " that if the network diverges but isn't allowed to reset it will"
				+ " fail the training process and return an error message.";
	}

	/**
	 * @return a string to describe the Decay option.
	 */
	public String decayTipText() {
		return "This will cause the learning rate to decrease."
				+ " This will divide the starting learning rate by the epoch number, to"
				+ " determine what the current learning rate should be. This may help"
				+ " to stop the network from diverging from the target output, as well"
				+ " as improve general performance. Note that the decaying learning"
				+ " rate will not be shown in the gui, only the original learning rate"
				+ ". If the learning rate is changed in the gui, this is treated as the"
				+ " starting learning rate.";
	}
}