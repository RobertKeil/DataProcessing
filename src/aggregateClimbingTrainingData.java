import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class aggregateClimbingTrainingData {

	public static void main(String[] args) throws Exception {
		
		String activity = "on table";
		
		reduceDataset(50, 6, activity);

	}

	/**
	 * Reduces the new training data into a list of features to be used to train the model. 
	 * @param sampleRate The sample rate of the input file is important to be able to identify seconds in the file
	 * @param numberOfSeconds The amount of seconds that one row in the output file should represent
	 * @param start The start time of the activity in ms
	 * @param end The end time of the activity in ms
	 * @param actvitiy The activity between the stard and end times
	 * @return outputFileName the name of the output file
	 * @throws Exception
	 * @author Mats
	 */
public static String reduceDataset (int sampleRate, int numberOfSeconds, String activity) throws Exception {
		
		int rowsPerAggregation = sampleRate*numberOfSeconds;
		
		String fileName= "assets/0newTrainingData/original_onDesk.csv";
		String outputFileName = "assets/0newTrainingData/" + activity + ".csv";
		
		double[][] recordsForStatistics = new double [3][rowsPerAggregation];
		PearsonsCorrelation calculateCorrelation = new PearsonsCorrelation();
		
		int counterRows = 0;
		int counterAggregations=0;
		
		Long timestamp;
		
		FileInputStream file= new FileInputStream(new File(fileName));
		BufferedReader brFile = new BufferedReader(new InputStreamReader(file));
		PrintWriter reducedFile = new PrintWriter (outputFileName);
		String[] tempDataArr;
		
		//print Header
		reducedFile.println("MeanX" + "," + "StdDevX" + "," + "IntqrX" + "," +
							"MeanY" + "," + "StdDevY" + "," + "IntqrY" + "," +
							"MeanZ" + "," + "StdDevZ" + "," + "IntqrZ" + "," +
							"CorrelationXY" + "," + "CorrelationXZ" + "," + "CorrelationYZ" + ","  +
							"Timestamp" + "," + "Posture"); 
		
		//read two times in the beginning to omit header line
		String tempData = brFile.readLine();
		tempData = brFile.readLine();
		tempData = brFile.readLine();
		
		while (tempData != null) {		
			tempDataArr=tempData.split(",");
			timestamp = Long.valueOf(tempDataArr[0]);
				
				tempDataArr=tempData.split(",");
					
				recordsForStatistics[0][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[1]);
				recordsForStatistics[1][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[2]);
				recordsForStatistics[2][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[3]);
				
				if (counterRows%rowsPerAggregation==0 && counterRows!=0){
					DescriptiveStatistics x = new DescriptiveStatistics(recordsForStatistics[0]);
					DescriptiveStatistics y = new DescriptiveStatistics(recordsForStatistics[1]);
					DescriptiveStatistics z = new DescriptiveStatistics(recordsForStatistics[2]);
					
					double intQrtRangeX = x.getPercentile(75)-x.getPercentile(25);
					double intQrtRangeY = y.getPercentile(75)-y.getPercentile(25);
					double intQrtRangeZ = z.getPercentile(75)-z.getPercentile(25);
					
					reducedFile.println(x.getMean() + "," + x.getStandardDeviation() + "," + intQrtRangeX + "," +  
										y.getMean() + "," + y.getStandardDeviation() + "," + intQrtRangeY + "," +
										z.getMean() + "," + z.getStandardDeviation() + "," + intQrtRangeZ + "," +
										calculateCorrelation.correlation(recordsForStatistics[0], recordsForStatistics[1]) + "," +
										calculateCorrelation.correlation(recordsForStatistics[0], recordsForStatistics[2]) + "," +
										calculateCorrelation.correlation(recordsForStatistics[1], recordsForStatistics[2]) + "," +
										timestamp + "," + '"' + activity + '"');
					
					counterAggregations++;
					System.out.println(counterAggregations);
				}
				counterRows++;
				tempData = brFile.readLine();					
		}
		brFile.close();
		reducedFile.close();
		return outputFileName;
	}
}