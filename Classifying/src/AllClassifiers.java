import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.DMNBtext;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
//import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.ClassificationViaClustering;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.END;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Grading;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiBoostAB;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.meta.MultiScheme;
import weka.classifiers.meta.OrdinalClassClassifier;
import weka.classifiers.meta.RacedIncrementalLogitBoost;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.meta.RotationForest;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.StackingC;
import weka.classifiers.meta.Vote;
import weka.classifiers.meta.nestedDichotomies.ClassBalancedND;
import weka.classifiers.meta.nestedDichotomies.DataNearBalancedND;
import weka.classifiers.meta.nestedDichotomies.ND;
import weka.classifiers.misc.HyperPipes;
import weka.classifiers.misc.VFI;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.DTNB;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.NNge;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.Ridor;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.FT;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.J48graft;
import weka.classifiers.trees.LADTree;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;


public class AllClassifiers extends JFrame implements ThreadCompleteListener {
	private JButton m_OpenBtn = new JButton("Open file...");
	private JFileChooser m_FileChooser = new JFileChooser();
	private JButton m_RunBtn = new JButton("Run");
	private JTextArea m_Txt = new JTextArea();
	private JScrollPane m_Pane = new JScrollPane(m_Txt);
	private Instances trainingData;
	private NotifyingThread one, two, three;
	StringBuilder builder = new StringBuilder();
	TreeMap<Double, Classifier> classifyingResult = new TreeMap<Double, Classifier>();
	
	private static final long serialVersionUID = 1L;

	public AllClassifiers() {
		m_OpenBtn.setToolTipText("Open a set of instances from a file");
		m_RunBtn.setToolTipText("Run all classifiers");
		m_RunBtn.setEnabled(false);
		//m_Txt.setEditable(false);
		m_Pane.setAutoscrolls(true);
		m_FileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
		m_FileChooser.setFileFilter(new FileNameExtensionFilter("Arff data files", "arff"));
		
		m_OpenBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (m_FileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						trainingData = new Instances(new BufferedReader(new FileReader(m_FileChooser.getSelectedFile())));
						
						AttributeSelection attsel = new AttributeSelection();
					    CfsSubsetEval eval = new CfsSubsetEval();
					    BestFirst search = new BestFirst();
					    search.setOptions(new String[] {"-D", "2", "-N", "5"});
					    attsel.setEvaluator(eval);
					    attsel.setSearch(search);
					    attsel.SelectAttributes(trainingData);
					    int[] selectedAtt = attsel.selectedAttributes();
					    ArrayList<Integer> indices = new ArrayList<Integer>();
					    for (int i = 0; i < selectedAtt.length; i++) {
					    	indices.add(Integer.valueOf(selectedAtt[i]));
					    }
					    m_Txt.setText(indices.toString());

					    for (int i = trainingData.numAttributes() - 2; i >= 0 ; i--) {
					    	if (!indices.contains(Integer.valueOf(i))) {
					    		trainingData.deleteAttributeAt(i);
					    	}
					    }
					    
					    m_RunBtn.setEnabled(true);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		m_RunBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setText("");
				m_OpenBtn.setEnabled(false);
				m_RunBtn.setEnabled(false);
				/*one = new NotifyingThread() {
					public void doRun() {
						for (int i = 0; i < 21; i++) {
							threadRun(i, builder, classifyingResult);
							setText(builder.toString());
						}
					}
				};
				two = new NotifyingThread() {
					public void doRun() {
						for (int i = 21; i < 42; i++) {
							threadRun(i, builder, classifyingResult);
							setText(builder.toString());
						}
					}
				};
				three = new NotifyingThread() {
					public void doRun() {
						for (int i = 42; i < 63; i++) {
							threadRun(i, builder, classifyingResult);
							setText(builder.toString());
						}
					}
				};
				one.addListener(AllClassifiers.this);
				two.addListener(AllClassifiers.this);
				three.addListener(AllClassifiers.this);
				one.start();
				two.start();
				three.start();*/
				threadRun(57, builder, classifyingResult);
				setText(builder.toString());
			}
		});
		
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(m_OpenBtn);
		buttons.add(m_RunBtn);
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(buttons, BorderLayout.NORTH);
		container.add(m_Pane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 500);
		setTitle("All classifiers");
		setVisible(true);
	}
	
	private synchronized void setText(String text) {
		m_Txt.setText(text);
	}
	
	private void enableButtons() {
		m_OpenBtn.setEnabled(true);
		m_RunBtn.setEnabled(true);
	}
	
	private void threadRun(int classifierCode, StringBuilder builder, TreeMap<Double, Classifier> classifyingResult) {
		Object[] result = classify(classifierCode);
		synchronized (this) {
			builder.append( ((Classifier)result[0]).getClass().getSimpleName() + ":\t\t" + result[1] + "\n" );
			classifyingResult.put((Double)result[1], (Classifier)result[0]);
		}
	}
	
	private Object[] classify(int classifierCode) {
		Classifier classifier;
		
		switch (classifierCode) {
		case 0:
			classifier = new BayesNet();
			break;
		case 1:
			classifier = new NaiveBayes();
			break;
		case 2:
			classifier = new DMNBtext();
			break;
		case 3:
			classifier = new NaiveBayesSimple();
			break;
		case 4:
			classifier = new NaiveBayesUpdateable();
			break;
			
		case 5:
			classifier = new LibSVM();
			break;
		case 6:
			classifier = new Logistic();
			break;
		case 57:
			classifier = new MultilayerPerceptron();
			break;
		case 8:
			classifier = new RBFNetwork();
			break;
		case 9:
			classifier = new SimpleLogistic();
			break;
		case 10:
			classifier = new SMO();
			break;
			
		case 11:
			classifier = new IB1();
			break;
		case 12:
			classifier = new IBk();
			break;
		case 13:
			classifier = new KStar();
			break;
		case 14:
			classifier = new LWL();
			break;
			
		case 15:
			classifier = new AdaBoostM1();
			break;
		case 16:
			classifier = new AttributeSelectedClassifier();
			break;
		case 17:
			classifier = new Bagging();
			break;
		case 18:
			classifier = new ClassificationViaClustering();
			break;
		case 19:
			classifier = new ClassificationViaRegression();
			break;
		case 20:
			classifier = new CVParameterSelection();
			break;
		case 58:
			classifier = new Dagging();
			break;
		case 22:
			classifier = new Decorate();
			break;
		case 23:
			classifier = new END();
			break;
		case 24:
			classifier = new FilteredClassifier();
			break;
		case 25:
			classifier = new Grading();
			break;
		case 26:
			classifier = new LogitBoost();
			break;
		case 27:
			classifier = new MultiBoostAB();
			break;
		case 28:
			classifier = new MultiClassClassifier();
			break;
		case 29:
			classifier = new MultiScheme();
			break;
		case 30:
			classifier = new ClassBalancedND();
			break;
		case 31:
			classifier = new DataNearBalancedND();
			break;
		case 32:
			classifier = new ND();
			break;
		case 33:
			classifier = new OrdinalClassClassifier();
			break;
		case 34:
			classifier = new RacedIncrementalLogitBoost();
			break;
		case 35:
			classifier = new RandomCommittee();
			break;
		case 36:
			classifier = new RandomSubSpace();
			break;
		case 37:
			classifier = new RotationForest();
			break;
		case 38:
			classifier = new Stacking();
			break;
		case 39:
			classifier = new StackingC();
			break;
		case 40:
			classifier = new Vote();
			break;
			
		case 41:
			classifier = new HyperPipes();
			break;
		case 42:
			classifier = new VFI();
			break;
		
		case 43:
			classifier = new ConjunctiveRule();
			break;
		case 44:
			classifier = new DecisionTable();
			break;
		case 45:
			classifier = new DTNB();
			break;
		case 46:
			classifier = new JRip();
			break;
		case 47:
			classifier = new NNge();
			break;
		case 48:
			classifier = new OneR();
			break;
		case 49:
			classifier = new PART();
			break;
		case 50:
			classifier = new Ridor();
			break;
			
		case 51:
			classifier = new BFTree();
			break;
		case 52:
			classifier = new DecisionStump();
			break;
		case 53:
			classifier = new FT();
			break;
		case 54:
			classifier = new J48();
			break;
		case 55:
			classifier = new J48graft();
			break;
		case 56:
			classifier = new LADTree();
			break;
		case 7:
			classifier = new LMT();
			break;
		case 21:
			classifier = new NBTree();
			break;
		case 59:
			classifier = new RandomForest();
			break;
		case 60:
			classifier = new RandomTree();
			break;
		case 61:
			classifier = new REPTree();
			break;
		case 62:
			classifier = new SimpleCart();
			break;
		
		default:
			classifier = new ZeroR();
			break;
		}
		
		//String result = classifier.getClass().getName() + ":\t\t\t\t";
		Double pctCorrect = Double.valueOf(0);
		try {
			classifier.buildClassifier(trainingData);
			
			Evaluation eval = new Evaluation(trainingData);
			eval.crossValidateModel(classifier, trainingData, 10, new Random(1));
			//result += String.valueOf(eval.pctCorrect());
			pctCorrect = eval.pctCorrect();
		} catch (Exception e) {
			//result += "error";
			e.printStackTrace();
		}
		
		Object[] result = new Object[2];
		result[0] = classifier;
		result[1] = pctCorrect;
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AllClassifiers();
	}

	@Override
	public synchronized void notifyOfThreadComplete(Thread thread) {
		if ( (thread.getName().equals(one.getName()) && !two.isAlive() && !three.isAlive()) ||
				(thread.getName().equals(two.getName()) && !one.isAlive() && !three.isAlive()) ||
				(thread.getName().equals(three.getName()) && !one.isAlive() && !two.isAlive())
			) {
			Entry<Double, Classifier> lastEntry = classifyingResult.lastEntry();
			builder.append("---------------\n" + lastEntry.getValue().getClass().getSimpleName() + "\t" + lastEntry.getKey() + "\n");
			setText(builder.toString());
			//enableButtons();
		}
	}

}
