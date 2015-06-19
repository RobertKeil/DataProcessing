import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class aggregateCollectedData {

	public static void main(String[] args) throws Exception {
		
		for (int i = 0; i<=12; i++){
			
			int subject = 7;
			int partNumber = i; 
//			int sampleRate = findOutSampleRate(1, subject, partNumber);
			reduceDataset(50, 6, subject, partNumber);
		}

	}

	/**
	 * Reduces the dataset (I.e. a predefined number of rows are aggregated to one new row) and print a new file. 
	 * Furthermore, statistical figures are created. 
	 * @param sampleRate The sample rate of the input file is important to be able to identify seconds in the file
	 * @param numberOfSeconds The amount of seconds that one row in the output file should represent
	 * @param subject Number of subject (i.e. test person) 
	 * @param partNumber which partnumber
	 * @return outputFileName the name of the output file
	 * @throws Exception
	 * @author Robert
	 * @author Mats
	 */
public static String reduceDataset (int sampleRate, int numberOfSeconds, int subject, int partNumber) throws Exception {
		
		int rowsPerAggregation = sampleRate*numberOfSeconds;
		
		String fileName= "assets/Subject" + subject 
				+ "/Subject" + subject + "_SensorAccelerometerData_" + partNumber + ".csv";
		String fileNameSubstring = fileName.split("/")[fileName.split("/").length-1];
		String outputFileName = fileName.replace(fileNameSubstring, "Reduced" + sampleRate + "Hz" + fileNameSubstring);
		
		DateFormat format = new SimpleDateFormat("dd.MM.yy kk:mm:ss.SSS", Locale.GERMANY);
		Date timestampDate;
		
		double[][] recordsForStatistics = new double [3][rowsPerAggregation];
		PearsonsCorrelation calculateCorrelation = new PearsonsCorrelation();
		String posture, environment, devicePosition, activity;
		
		int counterRows = 0;
		int counterAggregations=0;
		
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
				timestampDate = format.parse(tempDataArr[1]);
				recordsForStatistics[0][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[2]);
				recordsForStatistics[1][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[3]);
				recordsForStatistics[2][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[4]);
				environment = tempDataArr[5];
				posture =  tempDataArr[6];
				devicePosition =  tempDataArr[7];
				activity =  tempDataArr[8];
				
				if (counterRows%rowsPerAggregation==0 && counterRows!=0){
					DescriptiveStatistics x = new DescriptiveStatistics(recordsForStatistics[0]);
					DescriptiveStatistics y = new DescriptiveStatistics(recordsForStatistics[1]);
					DescriptiveStatistics z = new DescriptiveStatistics(recordsForStatistics[2]);
					
					// Calculate signal magnitude area
					double[] xValues = x.getValues();
					double[] yValues = y.getValues();
					double[] zValues = z.getValues();
					double signalMagnitudeArea = 0;
					
					for(int i = 0; i < xValues.length; i++) {
						signalMagnitudeArea =+ Math.abs(xValues[i]) + Math.abs(yValues[i]) + Math.abs(zValues[i]);
					}
					
					double intQrtRangeX = x.getPercentile(75)-x.getPercentile(25);
					double intQrtRangeY = y.getPercentile(75)-y.getPercentile(25);
					double intQrtRangeZ = z.getPercentile(75)-z.getPercentile(25);
					
					reducedFile.println(x.getMean() + "," + x.getStandardDeviation() + "," + intQrtRangeX + "," +  
										y.getMean() + "," + y.getStandardDeviation() + "," + intQrtRangeY + "," +
										z.getMean() + "," + z.getStandardDeviation() + "," + intQrtRangeZ + "," +
										calculateCorrelation.correlation(recordsForStatistics[0], recordsForStatistics[1]) + "," +
										calculateCorrelation.correlation(recordsForStatistics[0], recordsForStatistics[2]) + "," +
										calculateCorrelation.correlation(recordsForStatistics[1], recordsForStatistics[2]) + "," +
										timestampDate.getTime() + "," + '"' + posture + '"');
					
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

	/**
	 * This method finds out the sample rate that was used to create the accelerometer data file. 
	 * This is necessary because it turned out that not all mobile devices were able to fulfill a sample rate of 50 Hz.
	 * @param seconds how long should the time window be for the sample rate (e.g. 1 --> result shows entries per second, 10 --> result shows entry per 10 seconds
	 * @param subject Number of subject (i.e. test person) 
	 * @param partNumber which partnumber
	 * @return sampleRate 
	 * @throws Exception
	 * @author Robert
	 */
	static int findOutSampleRate(int seconds, int subject, int partNumber) throws Exception{
		
		String fileName= "assets/Subject" + subject 
				+ "/Subject" + subject + "_SensorAccelerometerData_" + partNumber + ".csv";
		
		long firstTime;
		long currentTime;
		int sampleRate=0;
		
		DateFormat format = new SimpleDateFormat("dd.MM.yy kk:mm:ss.SSS", Locale.GERMANY);
		Date timestampDate;
		
		
		FileInputStream file= new FileInputStream(new File(fileName));
		BufferedReader brFile = new BufferedReader(new InputStreamReader(file));
		
		String tempData = brFile.readLine();
		tempData = brFile.readLine();
		tempData = brFile.readLine();
		
		timestampDate = format.parse(tempData.split(",")[1]);
		firstTime=timestampDate.getTime();
		currentTime=firstTime;
		
		//count how many records in the defined time window 
		while (tempData != null && currentTime-firstTime<seconds*1000) {
			sampleRate++;
			
			timestampDate = format.parse(tempData.split(",")[1]);
			currentTime=timestampDate.getTime();
			tempData = brFile.readLine();
		}
		brFile.close();
		System.out.println("Sample Rate is " + sampleRate);
		return sampleRate;
	}

}