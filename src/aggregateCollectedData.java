import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class aggregateCollectedData {

	public static void main(String[] args) throws Exception {
		
		reduceDataset(50);

	}

public static String reduceDataset (int rowsPerAggregation) throws Exception {

		String fileName= "C:\\Users\\rober_000\\Dropbox\\2. Semester\\Team Projekt\\PostureData.csv";
		String outputFileName = "C:\\Users\\rober_000\\Dropbox\\2. Semester\\Team Projekt\\PostureDataReduced.csv";
		
		String timestamp = "";
		double[][] recordsForStatistics = new double [3][rowsPerAggregation];
		PearsonsCorrelation calculateCorrelation = new PearsonsCorrelation();
		String[]labels = new String[rowsPerAggregation];
		
		int counterRows = 0;
		int counterAggregations=0;
		
		FileInputStream file= new FileInputStream(new File(fileName));
		BufferedReader brFile = new BufferedReader(new InputStreamReader(file));
		PrintWriter reducedFile = new PrintWriter (outputFileName);
		String[] tempDataArr;
		
		//print Header
		reducedFile.println("Timestamp" + ";" + "MeanX" + ";" + "MeanY" + ";" +"MeanZ" + ";" +"StndrdDevX" + ";" +"StndrdDevY" + ";" +"StndrdDevZ" + ";" 
		+ "CorrelationXY" + ";" +"CorrelationXZ" + ";" +"CorrelationYZ" + ";" 
		+ "IntQrtRangeX" + ";" + "IntQrtRangY" + ";" + "IntQrtRangeZ" +";" +  "posture"); 
		
		//read two times in the beginning to omit header line
		String tempData = brFile.readLine();		
		tempData = brFile.readLine();
		
		while (tempData != null) {		
				
				tempDataArr=tempData.split(";");	
				timestamp = tempDataArr[0];
				recordsForStatistics[0][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[1]);
				recordsForStatistics[1][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[2]);
				recordsForStatistics[2][counterRows%rowsPerAggregation]= 	Double.parseDouble(tempDataArr[3]);
				labels[counterRows%rowsPerAggregation] =  tempDataArr[4];

				if (counterRows%rowsPerAggregation==0 && counterRows!=0){
					DescriptiveStatistics x = new DescriptiveStatistics(recordsForStatistics[0]);
					DescriptiveStatistics y = new DescriptiveStatistics(recordsForStatistics[1]);
					DescriptiveStatistics z = new DescriptiveStatistics(recordsForStatistics[2]);
					
					double intQrtRangeX = x.getPercentile(75)-x.getPercentile(25);
					double intQrtRangeY = y.getPercentile(75)-y.getPercentile(25);
					double intQrtRangeZ = z.getPercentile(75)-z.getPercentile(25);
					
					reducedFile.println(timestamp + ";" + x.getMean() + ";" + y.getMean() + ";" + z.getMean() + ";" 
							+ x.getStandardDeviation() + ";" + y.getStandardDeviation() + ";" + z.getStandardDeviation() + ";" 
							+ calculateCorrelation.correlation(recordsForStatistics[0], recordsForStatistics[1]) + ";" 
							+ calculateCorrelation.correlation(recordsForStatistics[0], recordsForStatistics[2]) + ";"
							+ calculateCorrelation.correlation(recordsForStatistics[1], recordsForStatistics[2]) + ";" 
							+ intQrtRangeX + ";" + intQrtRangeY + ";" + intQrtRangeZ + ";" + labels[0]);
					
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