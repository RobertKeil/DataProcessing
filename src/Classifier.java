import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;



public class Classifier {
	
	private static final String TRAIN_FILE = "assets/Reduced50HzSubject1Day0.arff";
	private static final String MODEL_FILE = "assets/knn.model";
	
	private static final String OUTPUT_FILE = "assets/output.arff";
	
	
	public static void main(String[] args) throws Exception {
		
	    BufferedReader reader = new BufferedReader(new FileReader(TRAIN_FILE));
	    
	    Instances data = new Instances(reader);
		reader.close();
		// Set class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
	    IBk classifier = new IBk(1);        // new instance of the classifier, J48 is decision tree, or IBk(k) for kNN
		classifier.buildClassifier(data);   // build classifier
		
		// Save classifier to file; serialize model
		SerializationHelper.write(MODEL_FILE, classifier);
		System.out.println("Done");
		
		// Options for evaluation
//		String[] options = new String[4];
//		 options[0] = "-t";
//		 options[1] = "assets/Reduced50HzPostureData.arff";
//		 options[2] = "-split-percentage";
//		 options[3] = "70";
		
		Evaluation eTest = new Evaluation(data);
		eTest.evaluateModel(classifier, data);
		System.out.println(classifier);
		System.out.println(eTest.toSummaryString());
	}   
	
	
	/**
	 * This method saves the preprocessed data into an ARFF file, which is needed for the Weka classifiers
	 * @param processed The array of preprocessed data
	 * @return processedArff A double[][] array equal to processed
	 * @author Mats
	 */
	static void createArff(double[][] processed) throws Exception {
	
		double[][] processedArff = processed;
		
		// Creates ARFF file for the instances to be saved to
		PrintStream output = new PrintStream(new File(OUTPUT_FILE));
	             
	
	     // Create the layout for the file
	    output.println("@RELATION features");
		output.println();
		output.println("@ATTRIBUTE xMean NUMERIC");
		output.println("@ATTRIBUTE xStdDev NUMERIC");
		output.println("@ATTRIBUTE xIntqr NUMERIC");
		output.println("@ATTRIBUTE yMean NUMERIC");
		output.println("@ATTRIBUTE yStdDev NUMERIC");
		output.println("@ATTRIBUTE yIntqr NUMERIC");
		output.println("@ATTRIBUTE zMean NUMERIC");
		output.println("@ATTRIBUTE zStdDev NUMERIC");
		output.println("@ATTRIBUTE zIntqr NUMERIC");
		output.println("@ATTRIBUTE CorrXY NUMERIC");
		output.println("@ATTRIBUTE CorrXZ NUMERIC");
		output.println("@ATTRIBUTE CorrYZ NUMERIC");
		output.println("@ATTRIBUTE timeStamp NUMERIC");
		output.println("@ATTRIBUTE class {1.0,2.0,3.0,4.0,5.0,6.0}");
		output.println();
		output.println("@DATA");
	
		// Write data points from array into file
		for(int i = 0;i<=processedArff.length-1;i++){
			for(int ii = 0; ii<processedArff[1].length; ii++) {
				
				output.print(processedArff[i][ii]);
				if(ii<processedArff[1].length-1) {
					output.print(",");
					
				}
				
				if(ii==processedArff[1].length-1) {
					output.print(",?");
				}
			}
			output.println();   
		}	
		output.close();
	}
}
