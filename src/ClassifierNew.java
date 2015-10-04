import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * The method in this class creates and saves a classification model into a file. Uses a classifier from the WEKA library. 
 * Model is based on previously collected and labeled data. This data was aggregated and preprocessed 
 * in the aggregateCollectedData class.
 * @author Mats
*/
public class ClassifierNew {
	
	private static final String TRAIN_FILE = "assets/0newTrainingData/ARFF/all.arff";
	// private static final String TEST_FILE = "assets/subject"+subject+"allSamplewoUnknownTEST.arff";
	private static final String MODEL_FILE = "assets/0newTrainingData/Models/model.nb";	
	
	public static void main(String[] args) throws Exception {
		
		// Read training data file and save it into an Instances object for WEKA
	    BufferedReader reader = new BufferedReader(new FileReader(TRAIN_FILE));	    
	    Instances trainData = new Instances(reader);
		
		// Read test data file and save it into an Instances object for WEKA
//	    reader = new BufferedReader(new FileReader(TEST_FILE));	    
//	    Instances testData = new Instances(reader);
//		reader.close();
		
		// Set class attributes, which is the last attribute
		trainData.setClassIndex(trainData.numAttributes() - 1);
//		testData.setClassIndex(trainData.numAttributes() - 1);
		
		
		// Declare and build the kNN classifier IBk(x), with x as k
	    IBk knnClassifier = new IBk(3);
		knnClassifier.buildClassifier(trainData);
		
		
		// Declare and build the Decision Tree classifier J48()
	    J48 j48Classifier = new J48();
		j48Classifier.buildClassifier(trainData);		
		
		
		// Declare and build the Naive Bayes classifier
	    NaiveBayes nbClassifier = new NaiveBayes();
		nbClassifier.buildClassifier(trainData);	
		
		// Evaluation of classifiers
//		Evaluation eTestKnn = new Evaluation(trainData);
//		Evaluation eTestNb = new Evaluation(trainData);
//		Evaluation eTestJ48 = new Evaluation(trainData);
//		eTestKnn.evaluateModel(knnClassifier, testData);
//		eTestNb.evaluateModel(nbClassifier, testData);
//		eTestJ48.evaluateModel(j48Classifier, testData);
//		System.out.println("Percentage of correctly classified instances:");
//		System.out.println("kNN(3): " + eTestKnn.pctCorrect());
//		System.out.println("NB: " + eTestNb.pctCorrect());
//		System.out.println("J48: " + eTestJ48.pctCorrect());
		
		// Save classifier to file
		SerializationHelper.write(MODEL_FILE, nbClassifier);
		System.out.println("Model written to file.");
		
	}   
}
