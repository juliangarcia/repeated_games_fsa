package com.evolutionandgames.repeatedgames.simulations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.evolutionandgames.agentbased.Agent;
import com.evolutionandgames.agentbased.AgentMutator;
import com.evolutionandgames.jevodyn.utils.Random;
import com.evolutionandgames.repeatedgames.evolution.RepeatedGame;
import com.evolutionandgames.repeatedgames.evolution.RepeatedStrategy;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomataFactory;
import com.evolutionandgames.repeatedgames.fsa.FiniteStateAutomatonMutator;
import com.evolutionandgames.repeatedgames.fsa.PoissonMutatorFixedSize;
import com.evolutionandgames.repeatedgames.fsa.PoissonMutatorInheritedSize;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FudenbergMaskinSimulation {

	private static final double PER_STATE_LOCAL_MUTATION_RATE = 0.01;
	enum MutationScheme {LOCAL, SEMILOCAL, GLOBAL};

	public static String exampleJson() {
		String json = new GsonBuilder().setPrettyPrinting().create()
				.toJson(exampleObject());
		return json;
	}
	
	public static FudenbergMaskinSimulation exampleObject(){
		FudenbergMaskinSimulation app = new FudenbergMaskinSimulation();
		app.seed = System.currentTimeMillis();
		app.continuationProbability = 0.95;
		app.mistakeProbability = 0.001;
		app.reward = 3.0;
		app.sucker = 0.0;
		app.temptation = 5.0;
		app.punishment = 1.0;
		app.numberOfTimeSteps = 10000;
		app.fileName = "salida.txt";
		app.q_bar = 0.01;
		app.mutationScheme = MutationScheme.GLOBAL;
		app.meanNumberOfStates = 5;
		app.roundRobinSize = 100;
		return app;
	}

	// read from json
	private double q_bar;
	private Long seed;
	private double continuationProbability;
	private double mistakeProbability;
	private double reward;
	private double sucker;
	private double temptation;
	private double punishment;
	private String fileName;
	private int numberOfTimeSteps;
	private MutationScheme mutationScheme;
	private int roundRobinSize;
	private int meanNumberOfStates;

	// things to build in the init method
	private transient AgentMutator mutator;
	private transient RepeatedGame repeatedGame;

	public void init() {
		if (this.mutationScheme == MutationScheme.LOCAL) {
			mutator = new FiniteStateAutomatonMutator(
					PER_STATE_LOCAL_MUTATION_RATE);
		} else if (this.mutationScheme == MutationScheme.SEMILOCAL) {
			mutator = new PoissonMutatorInheritedSize();
		}
		else{
			mutator = new PoissonMutatorFixedSize(meanNumberOfStates);
		}

		Random.seed(this.seed);
		this.repeatedGame = new RepeatedGame(this.reward, this.sucker,
				this.temptation, this.punishment, this.continuationProbability);
	}

	public static FudenbergMaskinSimulation loadFromFile(String string)
			throws IOException {
		File file = new File(string);
		Gson gson = new Gson();
		String json = Files.toString(file, Charsets.UTF_8);
		FudenbergMaskinSimulation sim = gson.fromJson(json,
				FudenbergMaskinSimulation.class);
		sim.init();
		return sim;
	}

	public static void runApp(FudenbergMaskinSimulation app) throws IOException {
		

		RepeatedStrategy resident = ExplicitAutomataFactory.allD();
		
		ICsvListWriter listWriter = null;
        String[] header = FudenbergMaskinSimulation.buildHeader();
        CellProcessor[] processors = FudenbergMaskinSimulation.getProcessors();
        int lastCheckIn = 0; 
        try {
            listWriter = new CsvListWriter(new FileWriter(app.fileName),
                    CsvPreference.STANDARD_PREFERENCE);
            // write the header
            listWriter.writeHeader(header);
            // write the initial zero step content
            //listWriter.write(FudenbergMaskinSimulation.currentStateRow(0, resident, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),processors);
       
        
        RepeatedStrategy mutant = (RepeatedStrategy) app.mutator.mutate((Agent) resident);
        
        double expectedPayoffResidentAgainstResident = 0.0;
		double expectedPayoffResidentAgainstMutant = 0.0;
		double expectedPayoffMutantAgainstResident = 0.0;
		double expectedPayoffMutantAgainstMutant = 0.0;
		double mutantFrequency = 0.0; 
		double fitnessResident = 0.0;
		double fitnessMutant = 0.0;
        
        
		for (int timeStep = 0; timeStep < app.numberOfTimeSteps; timeStep++) {
			// make sure mutant is a different guy
			while (mutant.equals(resident)) {
				mutant = (RepeatedStrategy) app.mutator.mutate((Agent) resident);
			}

			// sample the mutant frequency from U(0, q_bar)
			mutantFrequency = Random.nextDouble() * app.q_bar;

			// expected payoffs
			expectedPayoffResidentAgainstResident = 0.0;
			expectedPayoffResidentAgainstMutant = 0.0;
			expectedPayoffMutantAgainstResident = 0.0;
			expectedPayoffMutantAgainstMutant = 0.0;
			// many times compute 4 quantities and sum
			for (int i = 0; i < app.roundRobinSize; i++) {
				double[] payoffs = app.repeatedGame.playOnce(resident,
						resident, app.mistakeProbability);
				expectedPayoffResidentAgainstResident = expectedPayoffResidentAgainstResident
						+ payoffs[0];
				payoffs = app.repeatedGame.playOnce(resident, mutant,
						app.mistakeProbability);
				expectedPayoffResidentAgainstMutant = expectedPayoffResidentAgainstMutant
						+ payoffs[0];
				expectedPayoffMutantAgainstResident = expectedPayoffMutantAgainstResident
						+ payoffs[1];
				payoffs = app.repeatedGame.playOnce(mutant, mutant,
						app.mistakeProbability);
				expectedPayoffMutantAgainstMutant = expectedPayoffMutantAgainstMutant
						+ payoffs[0];
			}
			
			//divide by round robin
			expectedPayoffResidentAgainstResident = expectedPayoffResidentAgainstResident/(double)app.roundRobinSize;
			expectedPayoffResidentAgainstMutant = expectedPayoffResidentAgainstMutant/(double)app.roundRobinSize;
			expectedPayoffMutantAgainstResident = expectedPayoffMutantAgainstResident/(double)app.roundRobinSize;
			expectedPayoffMutantAgainstMutant = expectedPayoffMutantAgainstMutant/(double)app.roundRobinSize;
			
			
			
			// now the fitness is considering the mutant frequency
			fitnessResident = mutantFrequency
					* expectedPayoffResidentAgainstMutant
					+ (1.0 - mutantFrequency)
					* expectedPayoffResidentAgainstResident;
			fitnessMutant = mutantFrequency
					* expectedPayoffMutantAgainstMutant
					+ (1.0 - mutantFrequency)
					* expectedPayoffMutantAgainstResident;

			// this is the evolution step
			if (fitnessMutant >= fitnessResident) {
				listWriter.write(FudenbergMaskinSimulation.currentStateRow(lastCheckIn, timeStep, resident, mutantFrequency, fitnessMutant, fitnessResident, expectedPayoffResidentAgainstResident, expectedPayoffResidentAgainstMutant, expectedPayoffMutantAgainstResident, expectedPayoffMutantAgainstMutant),processors);
				resident = mutant;
				lastCheckIn = timeStep;
			}
			mutant = (RepeatedStrategy) app.mutator.mutate((Agent) resident);
		}
		//write the last resident
		listWriter.write(FudenbergMaskinSimulation.currentStateRow(lastCheckIn, app.numberOfTimeSteps, resident, mutantFrequency, fitnessMutant, fitnessResident, expectedPayoffResidentAgainstResident, expectedPayoffResidentAgainstMutant, expectedPayoffMutantAgainstResident, expectedPayoffMutantAgainstMutant),processors);
		
		
        } catch(Exception e){
        	System.out.println(e);
        	e.printStackTrace();
        	
        }finally {
        	// close files no matter what
			if (listWriter != null) {
				listWriter.close();
			}
		}
	}

	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // resident
				new NotNull(), // checkIn
				new NotNull(), // checkOut
				new NotNull(), // mutant frequency
				new NotNull(), // payoff mutant
				new NotNull(), // payoff resident
				new NotNull(),//"payoffNewResidentVsNewResident"
				new NotNull(),//"payoffOldResidentVsNewResident"
				new NotNull(),//"payoffnewResidentVsOldResident"
				new NotNull() //"payoffOldResidentVsOldResident"
		};
		return processors;
	}
	
	/**
     * Helper method to write the csv file
     * 
     * @return
     */
    private static String[] buildHeader() {
        // build the header
        final String[] header = { "resident", "checkInTime", "checkOutTime", "mutantFrequency" , "fitnessMutant", "fitnessdResident", "payoffMutantAgainstMutant", "payoffResidentAgainstMutant", "payoffMutantAgainstResident", "payoffResidentAgainstResident"};
        return header;
    }
    
    /**
     * Turns the current population into a list to be written in the csv file
     * 
     * @param simulation
     * @param extraColumnProcessor
     * @return
     */
    private static List<Object> currentStateRow(int checkInTime, int checkOutTime, RepeatedStrategy resident, double mutantFrequency, double fitnessMutant, double fitnessResident, double expectedPayoffResidentAgainstResident, double expectedPayoffResidentAgainstMutant, double expectedPayoffMutantAgainstResident, double expectedPayoffMutantAgainstMutant) {
        ArrayList<Object> ans = new ArrayList<Object>();
        ans.add(resident);
        ans.add(checkInTime);
        ans.add(checkOutTime);
        ans.add(mutantFrequency);
        ans.add(fitnessMutant);
        ans.add(fitnessResident);
        //"payoffNewResidentVsNewResident", "payoffOldResidentVsNewResident", "payoffnewResidentVsOldResident", "payoffOldResidentVsOldResident"
        ans.add(expectedPayoffMutantAgainstMutant);
        ans.add(expectedPayoffResidentAgainstMutant);
        ans.add(expectedPayoffMutantAgainstResident);
        ans.add(expectedPayoffResidentAgainstResident);
        return ans;
    }
	
	

}
