package com.evolutionandgames.repeatedgames;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.evolutionandgames.agentbased.extensive.ExtensivePopulationImpl;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomaton;
import com.evolutionandgames.repeatedgames.simulations.AgentBasedPayoffSimulation;
import com.evolutionandgames.repeatedgames.simulations.AgentBasedStationaritySimulation;
import com.evolutionandgames.repeatedgames.simulations.AgentBasedTimeSeriesSimulation;
import com.evolutionandgames.repeatedgames.simulations.FudenbergMaskinSimulation;


/**
 * Hello world!
 *
 */
public class App 
{

	@Parameter(names = { "-file", "-f" }, description = "Name of the json file")
	private String file;
	
	@Parameter(names = "-json", description = "Show json")
	private boolean json = false;

	
	enum SimulationType {
		TIME, STAT, PAYOFF, FUDENBERG
	}
	
	@Parameter(names = { "-type", "-t" }, description = "Type of simulation TIME, PAYOFF, STAT or FUDENBERG", required = true)
	private SimulationType type;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		App app = new App();
		//Parsing
		JCommander commander = new JCommander(app);
		try {
			commander.parse(args);
		} catch (ParameterException e) {
			System.out.println(e.getMessage());
			commander.usage();
			return;
		}
		//Done with parsing, get to business.
		ExtensivePopulationImpl.FORMAT = "%s:%d"; // very compact printing format due to lenght 
		switch (app.type) {
		case TIME:
			if(app.json){
				System.out.println(AgentBasedTimeSeriesSimulation.exampleJson());
			}else{
				ExplicitAutomaton.MINIMIZE_BEFORE_PRINTING = true;
				AgentBasedTimeSeriesSimulation.runApp(app.file);
			}
			break;
		case STAT:
			if(app.json){
				System.out.println(AgentBasedStationaritySimulation.exampleJson());
			}else{
				ExplicitAutomaton.MINIMIZE_BEFORE_PRINTING = true; 
				AgentBasedStationaritySimulation.runApp(app.file);
			}
			break;
		case PAYOFF:
			if(app.json){
				System.out.println(AgentBasedPayoffSimulation.exampleJson());
			}else{
				AgentBasedPayoffSimulation.runApp(app.file);
			}
			break;
		case FUDENBERG:
			if(app.json){
				System.out.println(FudenbergMaskinSimulation.exampleJson());
			}else{
				FudenbergMaskinSimulation.runApp(FudenbergMaskinSimulation
						.loadFromFile(app.file));
			}
			break;
		default:
			break;
		}
	}
}
