package com.evolutionandgames.repeatedgames.simulations;

import java.io.File;
import java.io.IOException;

import com.evolutionandgames.agentbased.AgentBasedPayoffCalculator;
import com.evolutionandgames.agentbased.AgentBasedPopulationFactory;
import com.evolutionandgames.agentbased.AgentBasedSimulation;
import com.evolutionandgames.agentbased.AgentMutator;
import com.evolutionandgames.agentbased.extensive.AgentBasedWrightFisherProcessWithAssortment;
import com.evolutionandgames.agentbased.extensive.ExtensivePopulation;
import com.evolutionandgames.jevodyn.utils.PayoffToFitnessMapping;
import com.evolutionandgames.repeatedgames.evolution.RepeatedGame;
import com.evolutionandgames.repeatedgames.evolution.RepeatedGamePayoffCalculator;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomataFactory;
import com.evolutionandgames.repeatedgames.fsa.FiniteStateAutomatonMutator;
import com.evolutionandgames.repeatedgames.utils.ExtraMeasuresProcessor;
import com.evolutionandgames.repeatedgames.utils.RepeatedStrategyPopulationFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * EvilGreenbeard simulation.
 * 
 */
public class AgentBasedTimeSeriesSimulation {

	// things that configure a simulation and are persited to a json file
	private int reportEveryTimeSteps;
	private Long seed;
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
	private int populationSize;
	private String fileName;
	private int numberOfTimeSteps;
	private boolean reportExtraMeasures;
	
	// things to be build in the init method
	public transient AgentBasedPopulationFactory factory;
	private transient AgentMutator mutator;
	private transient RepeatedGame repeatedGame;
	private transient AgentBasedPayoffCalculator payoffCalculator;
	private transient AgentBasedWrightFisherProcessWithAssortment process;
	private transient AgentBasedSimulation simulation;
	private transient ExtensivePopulation population;

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
				this.repeatedGame, this.mistakeProbability,
				this.reportExtraMeasures);

		this.process = new AgentBasedWrightFisherProcessWithAssortment(
				population, payoffCalculator, mapping, intensityOfSelection,
				mutator, r);
		this.simulation = new AgentBasedSimulation(this.process);
	}

	private static AgentBasedTimeSeriesSimulation loadFromFile(String string)
			throws IOException {
		File file = new File(string);
		Gson gson = new Gson();
		String json = Files.toString(file, Charsets.UTF_8);
		AgentBasedTimeSeriesSimulation sim = gson.fromJson(json,
				AgentBasedTimeSeriesSimulation.class);
		sim.init();
		return sim;
	}

	public static String exampleJson() {
		AgentBasedTimeSeriesSimulation app = new AgentBasedTimeSeriesSimulation();
		app.reportEveryTimeSteps = 1;
		app.seed = System.currentTimeMillis();
		app.mutationProbability = 0.001;
		app.addingStatesProbability = 1.0 / 3.0;
		app.removingStatesProbability = 1.0 / 3.0;
		app.changingStateProbability = 1.0 / 3.0;
		app.r = 0.0;
		app.intensityOfSelection = 1.0;
		app.mapping = PayoffToFitnessMapping.LINEAR;
		app.continuationProbability = 0.95;
		app.mistakeProbability = 0.001;
		app.reward = 2.0;
		app.sucker = 1.0;
		app.temptation = 1.0;
		app.punishment = 1.0;
		app.populationSize = 100;
		app.numberOfTimeSteps = 100000000;
		app.reportExtraMeasures = false;
		app.fileName = "salida.txt";
		String json = new GsonBuilder().setPrettyPrinting().create()
				.toJson(app);
		return json;
	}

	public static void runApp(String filename) throws IOException {
		AgentBasedTimeSeriesSimulation app = AgentBasedTimeSeriesSimulation
				.loadFromFile(filename);
		if (app.reportExtraMeasures) {
			app.simulation.simulateTimeSeries(app.numberOfTimeSteps,
					app.reportEveryTimeSteps, app.seed, app.fileName,
					new ExtraMeasuresProcessor());
		} else {
			app.simulation.simulateTimeSeries(app.numberOfTimeSteps,
					app.reportEveryTimeSteps, app.seed, app.fileName);
		}
	}

}
