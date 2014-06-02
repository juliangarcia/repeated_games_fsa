package com.evolutionandgames.repeatedgames.simulations;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.evolutionandgames.agentbased.Agent;
import com.evolutionandgames.agentbased.AgentBasedPayoffCalculator;
import com.evolutionandgames.agentbased.AgentBasedPopulation;
import com.evolutionandgames.agentbased.AgentBasedPopulationFactory;
import com.evolutionandgames.agentbased.AgentBasedSimulation;
import com.evolutionandgames.agentbased.AgentMutator;
import com.evolutionandgames.agentbased.extensive.AgentBasedWrightFisherProcessWithAssortment;
import com.evolutionandgames.agentbased.extensive.ExtensivePopulation;
import com.evolutionandgames.jevodyn.utils.PayoffToFitnessMapping;
import com.evolutionandgames.repeatedgames.evolution.RepeatedGame;
import com.evolutionandgames.repeatedgames.evolution.RepeatedGamePayoffCalculator;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomataFactory;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomaton;
import com.evolutionandgames.repeatedgames.fsa.FiniteStateAutomatonMutator;
import com.evolutionandgames.repeatedgames.utils.MapBasedValueComparator;
import com.evolutionandgames.repeatedgames.utils.RepeatedStrategyPopulationFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * EvilGreenbeard simulation.
 * 
 */
public class AgentBasedStationaritySimulation {

	// things that configure a simulation and are persited to a json file
	private int samplesPerEstimate;
	private int reportEveryTimeSteps;
	private Long seed;
	private int burningTimePerEstimate;
	private int numberOfEstimates;
	private double mutationProbability;
	double addingStatesProbability;
	double removingStatesProbability;
	double changingStateProbability;
	private double r;
	private double intensityOfSelection;
	private PayoffToFitnessMapping mapping;
	private double continuationProbability;
	private double mistakeProbability;
	private double reward;
	private double sucker;
	private double temptation;
	private double punishment;
	private String outputFile;
	private int populationSize;
	
	// things to be build in the init method
	public transient AgentBasedPopulationFactory factory;
	private transient AgentMutator mutator;
	private transient RepeatedGame repeatedGame;
	private transient AgentBasedPayoffCalculator payoffCalculator;
	private transient AgentBasedWrightFisherProcessWithAssortment process;
	private transient AgentBasedSimulation simulation;
	private transient ExtensivePopulation population;
	private int numberOfStrategiesToReport;

	public void init() {

		// FSA
		this.factory = new RepeatedStrategyPopulationFactory(populationSize,
				ExplicitAutomataFactory.allD());
		this.mutator = new FiniteStateAutomatonMutator(mutationProbability,
				addingStatesProbability, removingStatesProbability,
				changingStateProbability);

		this.population = (ExtensivePopulation) factory.createPopulation();
		this.repeatedGame = new RepeatedGame(this.reward, this.sucker,
				this.temptation, this.punishment, this.continuationProbability);

		this.payoffCalculator = new RepeatedGamePayoffCalculator(
				this.repeatedGame, this.mistakeProbability, false);
		this.process = new AgentBasedWrightFisherProcessWithAssortment(
				population, payoffCalculator, mapping, intensityOfSelection,
				mutator, r);
		this.simulation = new AgentBasedSimulation(this.process);
	}

	private static AgentBasedStationaritySimulation loadFromFile(String string)
			throws IOException {
		File file = new File(string);
		Gson gson = new Gson();
		String json = Files.toString(file, Charsets.UTF_8);
		AgentBasedStationaritySimulation sim = gson.fromJson(json,
				AgentBasedStationaritySimulation.class);
		sim.init();
		return sim;
	}

	public static String exampleJson() {
		AgentBasedStationaritySimulation app = new AgentBasedStationaritySimulation();
		app.samplesPerEstimate = 1000;
		app.reportEveryTimeSteps = 1000;
		app.seed = System.currentTimeMillis();
		app.burningTimePerEstimate = 1000;
		app.numberOfEstimates = 10;
		app.mutationProbability = 0.00001;
		app.addingStatesProbability = 1.0 / 3.0;
		app.removingStatesProbability = 1.0 / 3.0;
		app.changingStateProbability = 1.0 / 3.0;
		app.r = 0.0;
		app.intensityOfSelection = 0.0;
		app.mapping = PayoffToFitnessMapping.LINEAR;
		app.continuationProbability = 2;
		app.mistakeProbability = 0.001;
		app.reward = 3.0;
		app.sucker = 1.0;
		app.temptation = 4.0;
		app.punishment = 2.0;
		app.outputFile = "salida_test.csv";
		app.populationSize = 100;
		app.numberOfStrategiesToReport = 20;
		String json = new GsonBuilder().setPrettyPrinting().create()
				.toJson(app);
		return json;

	}

	public static void runApp(String filename) throws IOException {
		ExplicitAutomaton.MINIMIZE_BEFORE_PRINTING = false;
		AgentBasedStationaritySimulation app = AgentBasedStationaritySimulation
				.loadFromFile(filename);
		Map<Agent, Double> stat = app.simulation
				.estimateStationaryDistribution(app.burningTimePerEstimate,
						app.samplesPerEstimate, app.numberOfEstimates,
						app.reportEveryTimeSteps, app.seed, app.factory);
		ExplicitAutomaton.MINIMIZE_BEFORE_PRINTING = true;
		String resultado = buildString(stat, app.numberOfStrategiesToReport);
		File file = new File(app.outputFile);
		String json = new Gson().toJson(app);
		String result = json + "\n" + resultado;
		Files.write(result, file, Charsets.UTF_8);

	}

	public static String buildString(Map<Agent, Double> stat,
			int numberOfStrategiesToReport) {
		StringBuffer buffer = new StringBuffer();
		// order the map by frequency
		TreeMap<Agent, Double> orderedMap = new TreeMap<Agent, Double>(
				new MapBasedValueComparator(stat));
		orderedMap.putAll(stat);
		int i = 0;
		for (Map.Entry<Agent, Double> entry : orderedMap.entrySet()) {
			buffer.append(entry.getKey().toString() + "," + entry.getValue()
					+ "\n");
			i++;
			if (i >= numberOfStrategiesToReport)
				break;
		}
		return buffer.toString();
	}

	public int getSamplesPerEstimate() {
		return samplesPerEstimate;
	}

	public int getReportEveryTimeSteps() {
		return reportEveryTimeSteps;
	}

	public Long getSeed() {
		return seed;
	}

	public int getBurningTimePerEstimate() {
		return burningTimePerEstimate;
	}

	public int getNumberOfEstimates() {
		return numberOfEstimates;
	}

	public double getMutationProbability() {
		return mutationProbability;
	}

	public double getR() {
		return r;
	}

	public double getIntensityOfSelection() {
		return intensityOfSelection;
	}

	public PayoffToFitnessMapping getMapping() {
		return mapping;
	}

	public double getContinuationProbability() {
		return continuationProbability;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public AgentBasedPopulationFactory getFactory() {
		return factory;
	}

	public AgentMutator getMutator() {
		return mutator;
	}

	public AgentBasedPayoffCalculator getPayoffCalculator() {
		return payoffCalculator;
	}

	public AgentBasedWrightFisherProcessWithAssortment getProcess() {
		return process;
	}

	public AgentBasedSimulation getSimulation() {
		return simulation;
	}

	public AgentBasedPopulation getPopulation() {
		return population;
	}

}
