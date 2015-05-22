import java.io.BufferedReader;
import java.io.FileReader;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.SerializationHelper;

/** Description of Classifier
 * Creates and saves a classification model into a file. Uses a classifier from the WEKA library. 
 * Model is based on previously collected and labeled data. This data was aggregated and preprocessed 
 * in the aggregateCollectedData class.
 * @author Mats Schade
*/
public class Classifier {
	
	private static final String TRAIN_FILE = "assets/Reduced50HzSubject1Day0.arff";
	private static final String MODEL_FILE = "assets/knn.model";	
	
	public static void main(String[] args) throws Exception {
		
		// Read training data file and save it into an Instances object for WEKA
	    BufferedReader reader = new BufferedReader(new FileReader(TRAIN_FILE));	    
	    Instances data = new Instances(reader);
		reader.close();
		
		// Set class attribute, which is the last attribute
		data.setClassIndex(data.numAttributes() - 1);
		
		// Declare and build the kNN classifier IBk(x), with x as k
	    IBk classifier = new IBk(3);
		classifier.buildClassifier(data);
		
		// Save classifier to file
		SerializationHelper.write(MODEL_FILE, classifier);
		System.out.println("Model written to file.");
		
		// Evaluation of classifier
		Evaluation eTest = new Evaluation(data);
		eTest.evaluateModel(classifier, data);
		System.out.println(classifier);
		System.out.println(eTest.toSummaryString());
	}   
}
