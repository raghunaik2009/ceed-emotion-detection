package thesis.ceed.classifiers;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.DMNBtext;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
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
import weka.core.Instance;
import weka.core.Instances;

/*
 * return correct percentage if is training, predicted class if not.
 */
public class CeedClassifier {
	public static Double classify(int classifierCode, Instances trainingData, boolean isTraining, Instance instanceToClassify) {
		Classifier classifier;
		
		// 7 <-> 57; 21 <-> 58
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
			//classifier = new CeedMultilayerPerceptron();
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

		try {
			classifier.buildClassifier(trainingData);
			if (isTraining) {
				Evaluation eval = new Evaluation(trainingData);
				eval.crossValidateModel(classifier, trainingData, 10, new Random(1));
				return eval.pctCorrect();
			} else {
				return classifier.classifyInstance(instanceToClassify);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
