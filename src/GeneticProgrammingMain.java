import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import utilities.GeneticOperators;
import utilities.Settings;
import data.GeneticProgrammingTree;
import data.OutputData;
import data.TrainingData;

/**
 * Main GP 
 * @author bash1664
 *
 */

public class GeneticProgrammingMain {

	/**
	 * Main Run() 
	 * @param args
	 */
	public static void main(String[] args) {
		GeneticProgrammingMain gpMain = new GeneticProgrammingMain();
		
		try {
            gpMain.process();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred.");
        }
	}
	
	public void process() throws Exception {
	    // Initialize 
	    ArrayList<TrainingData> trainingData = TrainingData.getTrainingData();
	    OutputData output = new OutputData();
	    
        ArrayList<GeneticProgrammingTree> initialPopulation = getInitialPopulation(trainingData);
        
        // Sort population according to fitness, in descending order
        Collections.sort(initialPopulation);
        
        // Get current time in milliseconds
        long startTime = getCurrentTime();
        output.setStartTime(startTime);
        
        GeneticProgrammingTree currentMaxFitnessTree = getCurrentMaxFitnessTree(initialPopulation);
        output.incrementGenerationCount();
        output.addFittestTreeInGeneration(currentMaxFitnessTree);
        output.addPopulationSizeInGeneration(initialPopulation.size());
       
        // Generate the best fit solution
        ArrayList<GeneticProgrammingTree> population = initialPopulation;
        while (!done(startTime, currentMaxFitnessTree) && output.getGenerationCount() < Settings.getMaxGeneration()) {  
            output.setCurrentTime(getCurrentTime());
            output.displayResults();
            output.displayPopulation(population);
            
            ArrayList<GeneticProgrammingTree> nextGenPopulation = GeneticOperators.selection(population);
            
            ArrayList<GeneticProgrammingTree> children = GeneticOperators.crossoverTrees(population);
            
            GeneticOperators.mutateTrees(children);
            
            nextGenPopulation.addAll(children);
            
            output.incrementGenerationCount();
            output.addPopulationSizeInGeneration(nextGenPopulation.size());
                
            performFitnesEvaluation(nextGenPopulation);
                
            Collections.sort(nextGenPopulation);
            
            currentMaxFitnessTree = getCurrentMaxFitnessTree(nextGenPopulation);
            output.addFittestTreeInGeneration(currentMaxFitnessTree);
                 
            population = nextGenPopulation;
        }
        
        output.setCurrentTime(getCurrentTime());
        output.displayPopulation(population);
        output.displayFinalResults();
	}

    private void performFitnesEvaluation(ArrayList<GeneticProgrammingTree> nextGenPopulation)throws Exception {
        for (GeneticProgrammingTree gpTree : nextGenPopulation) {
            double fitness = GeneticProgrammingTree.evaluateFitness(TrainingData.getTrainingData(), gpTree);
            gpTree.setFitness(fitness);
        }
    }

    private GeneticProgrammingTree getCurrentMaxFitnessTree(ArrayList<GeneticProgrammingTree> population) {
        GeneticProgrammingTree currentMaxFitnessTree = population.get(0);
        return currentMaxFitnessTree;
    }

    private boolean done(long startTime, GeneticProgrammingTree gpTree) throws Exception {
        if (bestSolutionFound(gpTree) && !(executionTimeExceeded(startTime))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean bestSolutionFound(GeneticProgrammingTree currentMaxFitnessTree) throws Exception {
        return currentMaxFitnessTree.getFitness() <= Settings.getFitnessThreshold();
    }
	
    private boolean executionTimeExceeded(long startTime) throws Exception {
        long currentTime = new Date().getTime();
        long runDuration = currentTime - startTime;
        
        if (Settings.trace()) {
            System.out.println("[Trace] StartTime   : " + startTime);
            System.out.println("[Trace] CurrentTime : " + currentTime);
            System.out.println("[Trace] Run Duration: " + runDuration);
        }
        
        if (runDuration > Settings.maxExecutionTime()) {
            return true;
        } else {
            return false;
        }  
    }

    private long getCurrentTime() {
        return new Date().getTime();
    }

    private ArrayList<GeneticProgrammingTree> getInitialPopulation(ArrayList<TrainingData> trainingData) {
        int size = 0;
        
        try {
            Properties settings = Settings.getSettings();
            
            String prop = settings.getProperty(Settings.PROP_POPULATION_SIZE);
            
            size = Integer.parseInt(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ArrayList<GeneticProgrammingTree> population = new ArrayList<GeneticProgrammingTree>(size);
        try {
            for (int i = 0; i < size; i++) {
                GeneticProgrammingTree gpTree = GeneticProgrammingTree.createGeneticProgrammingTree(trainingData); 
                
                population.add(gpTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return population;
    }

}
