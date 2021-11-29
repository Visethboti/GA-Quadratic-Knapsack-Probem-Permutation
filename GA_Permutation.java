import java.lang.Math;
import java.util.Random;


public class GA_Permutation {
	private static Random random;
	
	// problem
	private final int[][] qkValueWeight; // [0][] is Value // [1][] is Weight
	private final int[][] qkPairValue;
	private final int qkCapacity;
	private final int qkNumObjects;
	
	private int numGenerationsToRun;
	
	// Parameters
	private final int popsSize = 100;
	private final double parentSizePercentage = 0.5; 
	private final double offspringSizePercentage = 0.98;
	private final double elitismSizePercentage = 0.02;
	private final double mutationProbality = 0.005;
	private final int kTournamentSize = 3;
	
	// Parameters Size
	private final int parentSize = (int) (popsSize * parentSizePercentage);
	private final int offspringSize = (int) (popsSize * offspringSizePercentage);
	//private final int elitismSize = popsSize - offspringSize;
	private final int elitismSize = (int) (popsSize * elitismSizePercentage);
	
	// Initialization
	private char[][] populations; // populations[][0][] is Value // populations[][1][] is Weight
		
	// Fitness Calculation
	private int[] popFitness;
		
	// Parents SelectionEvent
	private int[] parentPool;
		
	// Crossover
	private char[][] offspringPool;
		
	// Elitism
	private char[][] elitismPool;
		
	// New generation
	private char[][] nextGenPopulation;
	
	// *** Constructor ***
	public GA_Permutation (int[][] qkValueWeight, int[][] qkPairValue, int qkCapacity, int numObjects) {
		this.qkValueWeight = qkValueWeight;
		this.qkPairValue = qkPairValue;
		this.qkCapacity = qkCapacity;
		this.qkNumObjects = numObjects;
		
		this.populations = new char[popsSize][numObjects];
		this.popFitness = new int[popsSize];
		this.parentPool = new int[parentSize];
		this.offspringPool = new char[offspringSize][numObjects];
		this.elitismPool = new char[elitismSize][numObjects];
		this.nextGenPopulation = new char[popsSize][numObjects];
	
		this.random = new Random();
	}
	
	// *** Run ***
	public void runGA(int numGenerationsToRun){
		this.numGenerationsToRun = numGenerationsToRun;
		
		
		/*
		int genCounter = 0;
		
		System.out.println("GA_Binary: Start Running GA");
		
		// Initialization
		System.out.println("GA_Binary: Start Initialization");
		initialization();
		System.out.println("GA_Binary: Start fitnessCalculation");
		fitnessCalculation();
		
		System.out.print("Gen:" + genCounter);
		printFitnessStat();
		
		System.out.println("GA_Binary: Start Generational");
		do {
			parentSelection();
			crossOver();
			elitism();
			replaceGeneration();
			fitnessCalculation();
			mutation();
			fitnessCalculation();
			
			genCounter++;
			System.out.print("Gen:" + genCounter);
			printFitnessStat();
		}while(genCounter < numGenerationsToRun);
		System.out.println("GA_Binary: Finished Generational");
		*/
	}
	
	// EA Methods
	private void initialization(){
		for(int i = 0; i < popsSize; i++) {
			// Only chromosome that fit in the knapsack capacity is allowed. The do while loop will run till one fit under capacity.
			do {
				for(int j = 0; j < qkNumObjects; j++) {
					if(random.nextBoolean()) {
						populations[i][j] = '1';
					}
					else {
						populations[i][j] = '0';
					}
				}
			} while(getFitness(populations[i]) == 0);
		}
	}
	
	private void fitnessCalculation(){
		for(int i = 0; i < popsSize; i++) {
			popFitness[i] = getFitness(populations[i]);
		}
	}
	
	private void parentSelection(){ // K-tournament Selection
		// clear Parent Pool
		for(int i = 0; i < parentSize; i++) {
			parentPool[i] = -1;
		}
		
		//int[] tournament = new int[kTournamentSize]; // to store the index to the chromosome randomly choosen from population
		int bestChromosomeIndex;
		int randomNum;
		for(int i = 0; i < parentSize; i++) { // number of parents need to choose
			bestChromosomeIndex = -1;
			for(int j = 0; j < kTournamentSize; j++) { // choose random chromosome from population ktournamentSize number of times and select the best fitness
				do {
					randomNum = random.nextInt(popsSize);
				} while(!notExistInArray(parentPool, randomNum));	
				
				if(bestChromosomeIndex == -1)
					bestChromosomeIndex = randomNum;
				else if(popFitness[randomNum] > popFitness[bestChromosomeIndex]) 
					bestChromosomeIndex = randomNum;
			}
			
			parentPool[i] = bestChromosomeIndex;
			//System.out.println("parent: "+ bestChromosomeIndex); // for debugging
		}
	}
	
	private void crossOver(){
		int randomNum1, randomNum2, randomPoint;
		char[] newOffspring = new char[qkNumObjects];
		for(int i = 0; i < offspringSize; i++) { // For each num of off spring required
			do {
				randomNum1 = random.nextInt(parentSize); // Randomly pick two parent from the parentPool that is not the same
				do {
					randomNum2 = random.nextInt(parentSize);
				}while(randomNum1 == randomNum2);
				
				// One point crossover
				randomPoint = random.nextInt(qkNumObjects-1) + 1; // randomPoint between 1 to number of objects
				
				for(int j = 0; j < randomPoint; j++) {
					newOffspring[j] = populations[parentPool[randomNum1]][j];
				}
				for(int j = randomPoint; j < qkNumObjects; j++) {
					newOffspring[j] = populations[parentPool[randomNum2]][j];
				}
			}while(getFitness(newOffspring) == 0);
			
			// Add to offspring Pool
			for(int j = 0; j < qkNumObjects; j++) {
				offspringPool[i][j] = newOffspring[j];
			}
		}
	}
	
	private void elitism() {
		int[] elitismIndex = new int[elitismSize];
		for(int i = 0; i < elitismSize; i++) 
			elitismIndex[i] = -1;
		
		int indexHighestFitness = 0;
		int highestFitness = 0;
		boolean notExist = true;
		
		// get all the indexs
		for(int i = 0; i < elitismSize; i++){	
			for(int j = 0; j < popsSize; j++){
				if(!notExistInArray(elitismIndex, j)){
					notExist = false;
				}
				
				if(highestFitness < popFitness[j] && notExist){
					highestFitness = popFitness[j];
					indexHighestFitness = j;
				}
				notExist = true;
			}
			elitismIndex[i] = indexHighestFitness;
			indexHighestFitness = 0;
			highestFitness = 0;	
		}
		
		// put the index pop into elitismPool
		for(int i = 0; i < elitismSize; i++){
			for(int j = 0; j < qkNumObjects; j++){
					elitismPool[i][j] = populations[elitismIndex[i]][j];
			}
			//System.out.println("Elitism - " + elitismIndex[i]);// for debugging
		}
		
		return;
	}
	
	private void replaceGeneration(){
		int counter = 0;
		
		// Combine offspring and elitismPool to make the next generation
		for(int i = 0; i < offspringSize && counter < popsSize; i++){
			for(int j = 0; j < qkNumObjects; j++){
				populations[counter][j] = offspringPool[i][j];
			}
			counter++;
		}
		
		for(int i = 0; i < elitismSize && counter < popsSize; i++){
			for(int j = 0; j < qkNumObjects; j++){
				populations[counter][j] = elitismPool[i][j];
			}
			counter++;
		}
		
		return;
	}
	
	private void mutation(){
		double randomValue;
		int randomBit;
		int counter, currentCFitness;
		char[] currentChromosome = new char[qkNumObjects];
		for(int i = 0; i < popsSize; i++) {
			randomValue = random.nextDouble();
			if(randomValue <= mutationProbality) {
				// take current chromosome from population
				for(int j = 0; j < qkNumObjects; j++) {
					currentChromosome[j] = populations[i][j];
				}
				
				counter = 0;
				currentCFitness = 0;
				do {
					randomBit = random.nextInt(qkNumObjects);
					if(currentChromosome[randomBit] == '1')
						currentChromosome[randomBit] = '0';
					else
						currentChromosome[randomBit] = '1';
					counter++;
					currentCFitness = getFitness(currentChromosome);
				}while(counter < qkNumObjects && currentCFitness == 0);
					
				// Replace the pop with currentChromosome
				if(currentCFitness > 0) {
					for(int j = 0; j < qkNumObjects; j++) {
						populations[i][j] = currentChromosome[j];
					}
				}
			}
		}
	}
	
	// GA Untilities Methods
	private int getFitness(char[] chromosome) {
		int totalWieght = 0;
		int totalValue = 0;
		for(int i = 0; i < qkNumObjects; i++) {
			if(chromosome[i] == '1') {
				totalValue += qkValueWeight[0][i];
				totalWieght += qkValueWeight[1][i];
				
				// Add up the pair value with the other object if they are choosen too
				for(int j = i+1; j < qkNumObjects; j++) {
					if(chromosome[j] == '1')
						totalValue += qkPairValue[i][j];
				}
			}
		}
		
		if(totalWieght > qkCapacity) 
			return 0;
		else
			return totalValue;
	}
	
	// *** Untilities Methods ***
	
	// Print Methods
	private void printPopulations(){
		System.out.println("*** Populations ***");
		for(int i = 0; i < popsSize; i++) {
			System.out.print(i+"-");
			System.out.print(populations[i]);
			System.out.println(" Fitness: " + popFitness[i]);
		}
	}
	
	private void printParentPool(){
		System.out.println("*** Parent Pool ***");
		for(int i = 0; i < parentSize; i++) {
			System.out.print(i+"-Pop:" + parentPool[i] + "-");
			System.out.print(populations[parentPool[i]]);
			System.out.println(" Fitness: " + popFitness[parentPool[i]]);
		}
	}
	
	private void printOffspringPool(){
		System.out.println("*** Offspring Pool ***");
		for(int i = 0; i < offspringSize; i++) {
			System.out.print(i+"-");
			System.out.print(offspringPool[i]);
			System.out.println(" Fitness: " + getFitness(offspringPool[i]));
		}
	}
	
	private void printElitismPool(){
		System.out.println("*** Elitism Pool ***");
		for(int i = 0; i < elitismSize; i++) {
			System.out.print(i+"-");
			System.out.print(elitismPool[i]);
			System.out.println(" Fitness: " + getFitness(elitismPool[i]));
		}
	}
	
	private void printFitnessStat(){
		double sum = 0, avgFitness = 0, standardDeviation = 0;
		int highestFitness = 0, highestFitessIndex = 0, lowestFitness = popFitness[0], lowestFitessIndex = 0;
		
		
		for(int i = 0; i < popsSize; i++){
			if(highestFitness < popFitness[i]){
				highestFitessIndex = i;
				highestFitness = popFitness[i];
			}
			if(lowestFitness > popFitness[i]){
				lowestFitessIndex = i;
				lowestFitness = popFitness[i];
			}
			sum+=popFitness[i];
		}
		avgFitness = (double) sum / popsSize;
		
		// Standard Deviation Calculation
		for(int i = 0; i < popsSize; i++) {
			standardDeviation += Math.pow((popFitness[i] - avgFitness), 2); // pow of 2
		}
		standardDeviation = Math.sqrt(standardDeviation/popsSize);
		
		//Print the stats
		System.out.println("====== EA Stats ====== ");
		System.out.println("AvgFitness= " + avgFitness + "| Highest= " + highestFitness + "| Lowest= " + lowestFitness + "| StanDev= " + standardDeviation);
	}
	
	public String[] getFitnessStat(){
		double sum = 0, avgFitness = 0, standardDeviation = 0;
		int highestFitness = 0, highestFitessIndex = 0, lowestFitness = popFitness[0], lowestFitessIndex = 0;
		
		for(int i = 0; i < popsSize; i++){
			if(highestFitness < popFitness[i]){
				highestFitessIndex = i;
				highestFitness = popFitness[i];
			}
			if(lowestFitness > popFitness[i]){
				lowestFitessIndex = i;
				lowestFitness = popFitness[i];
			}
			sum+=popFitness[i];
		}
		avgFitness = (double) sum / popsSize;
		
		// Standard Deviation Calculation
		for(int i = 0; i < popsSize; i++) {
			standardDeviation += Math.pow((popFitness[i] - avgFitness), 2); // pow of 2
		}
		standardDeviation = Math.sqrt(standardDeviation/popsSize);
		
		//Print the stats
		System.out.println("====== EA Stats ====== ");
		System.out.println("AvgFitness= " + avgFitness + "| Highest= " + highestFitness + "| Lowest= " + lowestFitness + "| StanDev= " + standardDeviation);
		
		String[] result = {Integer.toString(highestFitness), Double.toString(avgFitness), Double.toString(standardDeviation)};
		return result;
	}
	
	private boolean notExistInArray(int[] array, int value) {
		for(int i = 0; i < array.length; i++) {
			if(value == array[i])
				return false;
		}
		return true;
	}
}



