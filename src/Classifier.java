import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.SerializationHelper;



public class Classifier {
	
	private static final String TRAIN_FILE = "assets/train.arff";
	private static final String MODEL_FILE = "assets/knn.model";
	
	private static final String OUTPUT_FILE = "assets/output.arff";
	
	public static void main(String[] args) throws Exception {
		
	    BufferedReader reader = new BufferedReader(new FileReader(TRAIN_FILE));
	    
	    Instances data = new Instances(reader);
		reader.close();
		// Set class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
	    IBk classifier = new IBk(5);        // new instance of a KNN classifier, 5 indicates value for k
		classifier.buildClassifier(data);   // build classifier
		
		// Save classifier to file; serialize model
		SerializationHelper.write(MODEL_FILE, classifier);
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
