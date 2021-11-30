import java.io.*;
import java.util.Scanner;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	// problem
	private static int[][] qkValueWeight; // [0][] is Value // [1][] is Weight
	private static int[][] qkPairValue;
	private static int qkCapacity;
	private static int numObjects;
	
	private static final String problemFilName = "test.txt";
	
	public static void main(String[] args) {
		System.out.println("Main: Starting readProblem()");
		readProblem();
		printProblemStat();
		printProblem();
		
		// Run GA
		long start = System.currentTimeMillis();
		
		GA_Permutation ga_Permutation = new GA_Permutation(qkValueWeight, qkPairValue, qkCapacity, numObjects);
		ga_Permutation.runGA(100);
		
		long finished = System.currentTimeMillis();
		double timeElapsed = (finished - start) / (double)1000;
		System.out.println("It took " + timeElapsed + " seconds");
		
		String[] gaResult = ga_Permutation.getFitnessStat();
		String[] outputData = {problemFilName, gaResult[0], gaResult[1], gaResult[2], Double.toString(timeElapsed)};
		
		printResultToCSV(outputData);
	}
	
	private static void printResultToCSV(String[] outputData) {
		try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File("test.csv"), true))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append(outputData[0]);
		      sb.append(',');
		      sb.append(outputData[1]);
		      sb.append(',');
		      sb.append(outputData[2]);
		      sb.append(',');
		      sb.append(outputData[3]);
		      sb.append(',');
		      sb.append(outputData[4]);
		      sb.append('\n');

		      writer.write(sb.toString());

		      System.out.println("Permutation's result written to csv file.");
		      
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void readProblem() {
		try {
			File file  = new File("..\\problem_instances\\" + problemFilName);
			Scanner scanner = new Scanner(file);
			
			String fileName = scanner.nextLine();
			numObjects = scanner.nextInt(); // size of object in this problem instance
			
			// init problem
			qkValueWeight = new int[2][numObjects];
			qkPairValue = new int[numObjects][numObjects];
			
			// read in value
			for(int i = 0; i < numObjects; i++) {
				qkValueWeight[0][i] = scanner.nextInt();
			}
			
			// init array
			for(int i = 0; i < numObjects; i++) {
				for(int j = 0; j < numObjects; j++) {
					qkPairValue[i][j] = -1;
				}
			}
			
			// read in pair value
			for(int i = 0; i < numObjects-1; i++) {
				for(int j = i+1; j < numObjects; j++) {
					qkPairValue[i][j] = scanner.nextInt();
				}
			}
			
			// empty line and constraint
			scanner.nextInt();
			
			// read in capacity
			qkCapacity = scanner.nextInt();
			
			// read in weight
			for(int i = 0; i < numObjects; i++) {
				qkValueWeight[1][i] = scanner.nextInt();
			}
			
			// close scanner
			scanner.close();
		} catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
	
	public static void printProblem() {
		printProblemStat();
		System.out.println("*** Value and Weight ***");
		print(qkValueWeight);
		System.out.println("*** Pair Value ***");
		print(qkPairValue);
	}
	
	public static void printProblemStat() {
		System.out.println("*** The Problem ***");
		System.out.print("Number of Objects = " + numObjects);
		System.out.println(" Capacity = " + qkCapacity);
	}
	
	public static void print(int[][] twoDArray) {
		int size1 = twoDArray.length;
		int size2 = twoDArray[0].length;
		for(int i = 0; i < size1; i++){
			for(int j = 0; j < size2; j++){
				System.out.print(twoDArray[i][j] + " ");
			}
			System.out.println("");
		}
	}
}