package com.evolutionandgames.repeatedgames.fsa;

import java.util.ArrayList;
import java.util.Iterator;

import com.evolutionandgames.agentbased.Agent;
import com.evolutionandgames.agentbased.AgentMutator;
import com.evolutionandgames.jevodyn.utils.Random;

public class FiniteStateAutomatonMutator implements AgentMutator {

	private double mutationProbabilityPerState;

	// Default values as in the other paper
	private double addingStatesProbability = 0.30;
	private double removingStatesProbability = 0.3;
	private double changingStateProbability = 0.4;

	private double[] distrubutionOfEvents = { addingStatesProbability,
			removingStatesProbability, changingStateProbability };

	private enum MutationEvent {
		ADD, REMOVE, CHANGE
	}

	public Agent mutate(Agent agent) {
		ExplicitAutomaton automaton = ((ExplicitAutomaton) agent).copy();
		// for every state roll a dice, and build list with types of operation
		ArrayList<MutationEvent> eventsToApply = buildMutationChain(automaton
				.getSize());
		for (Iterator<MutationEvent> iterator = eventsToApply.iterator(); iterator.hasNext();) {
			automaton = applyMutation(automaton, iterator.next());
		}
		//if (!automaton.isValid()) throw new IllegalArgumentException("Chain of mutations " + eventsToApply + " resulted in  invalid " + automaton);
		return automaton;
	}

	private ExplicitAutomaton applyMutation(ExplicitAutomaton automaton, MutationEvent mutationEvent) {
		switch (mutationEvent) {
		case ADD:
			//add a random state
			automaton.addState(ExplicitState.randomState(automaton.getSize()+1));
			//make it reachable by making a random state (but the last one) point to the last state
			int targetState = Random.nextInt(automaton.getSize()-1);
			int targetLink = Random.nextInt(4);
			if (targetLink ==0) {
				automaton.getState(targetState).setNextFocalCooperateOtherCooperate(automaton.getSize()-1); 
			}else if (targetLink==1) {
				automaton.getState(targetState).setNextFocalCooperateOtherDefect(automaton.getSize()-1);
			}else if (targetLink ==2) {
				automaton.getState(targetState).setNextFocalDefectOtherCooperate(automaton.getSize()-1);
			}else {
				automaton.getState(targetState).setNextFocalDefectOtherDefect(automaton.getSize()-1);
			}
			break;
		case REMOVE:
			//does nothing if the size is one, or zero.
			if (automaton.getSize()>1){
				int stateToRemove = Random.nextInt(automaton.getSize());
				automaton.removeState(stateToRemove);
				//repair broken links, by pointing them to a random state
				for (int i = 0; i < automaton.getSize(); i++) {
					if (automaton.getState(i).getNextFocalCooperateOtherCooperate() == stateToRemove){
						automaton.getState(i).setNextFocalCooperateOtherCooperate(Random.nextInt(automaton.getSize()));
					}
					if (automaton.getState(i).getNextFocalCooperateOtherDefect() == stateToRemove){
						automaton.getState(i).setNextFocalCooperateOtherDefect(Random.nextInt(automaton.getSize()));
					}
					if (automaton.getState(i).getNextFocalDefectOtherCooperate() == stateToRemove){
						automaton.getState(i).setNextFocalDefectOtherCooperate(Random.nextInt(automaton.getSize()));
					}
					if (automaton.getState(i).getNextFocalDefectOtherDefect() == stateToRemove){
						automaton.getState(i).setNextFocalDefectOtherDefect(Random.nextInt(automaton.getSize()));
					}
					//reorder those that occurred after the state to remove
					if (automaton.getState(i).getNextFocalCooperateOtherCooperate() > stateToRemove){
						automaton.getState(i).setNextFocalCooperateOtherCooperate(automaton.getState(i).getNextFocalCooperateOtherCooperate()-1);
					}
					if (automaton.getState(i).getNextFocalCooperateOtherDefect() > stateToRemove){
						automaton.getState(i).setNextFocalCooperateOtherDefect(automaton.getState(i).getNextFocalCooperateOtherDefect()-1);
					}
					if (automaton.getState(i).getNextFocalDefectOtherCooperate() > stateToRemove){
						automaton.getState(i).setNextFocalDefectOtherCooperate(automaton.getState(i).getNextFocalDefectOtherCooperate()-1);
					}
					if (automaton.getState(i).getNextFocalDefectOtherDefect() > stateToRemove){
						automaton.getState(i).setNextFocalDefectOtherDefect(automaton.getState(i).getNextFocalDefectOtherDefect()-1);
					}
					
					
					
					
				}
			}
			break;
		case CHANGE:
			int stateToChange = Random.nextInt(automaton.getSize());
			//you can change the action (0), or  nextCC (1), nextCD (2), nextDC (3) or next DD (4) 
			int typeOfChange = Random.nextInt(5);
			if (typeOfChange ==0){
				automaton.getState(stateToChange).flip();
			}else if (typeOfChange ==1) {
				automaton.getState(stateToChange).setNextFocalCooperateOtherCooperate(Random.nextInt(automaton.getSize()));
			} else if (typeOfChange == 2){
				automaton.getState(stateToChange).setNextFocalCooperateOtherDefect(Random.nextInt(automaton.getSize()));
			}else if (typeOfChange ==3){
				automaton.getState(stateToChange).setNextFocalDefectOtherCooperate(Random.nextInt(automaton.getSize()));
			}else{
				automaton.getState(stateToChange).setNextFocalDefectOtherDefect(Random.nextInt(automaton.getSize()));
			}
			break;
	}
	return automaton;
	}

	/**
	 * Build a chain of mutation events to be applied to the automata. One event
	 * is added per state with probability mutationProbabilityPerState.
	 * Therefore the expected size of a chain is size of the automata times the
	 * probability of mutation per state.
	 * 
	 * @param size
	 * @return
	 */
	private ArrayList<MutationEvent> buildMutationChain(int size) {
		ArrayList<MutationEvent> mutationEvents = new ArrayList<FiniteStateAutomatonMutator.MutationEvent>();
		for (int i = 0; i < size; i++) {
			if (Random.bernoulliTrial(mutationProbabilityPerState)) {
				int eventType = Random
						.simulateDiscreteDistribution(distrubutionOfEvents);
				switch (eventType) {
				case 0:
					mutationEvents.add(MutationEvent.ADD);
					break;
				case 1:
					mutationEvents.add(MutationEvent.REMOVE);
					break;
				case 2:
					mutationEvents.add(MutationEvent.CHANGE);
					break;
				}
			}
		}
		return mutationEvents;
	}

	/**
	 * Constructor with default chain of mutations
	 * @param mutationProbabilityPerState
	 */
	public FiniteStateAutomatonMutator(double mutationProbabilityPerState) {
		super();
		this.mutationProbabilityPerState = mutationProbabilityPerState;
	}
	
	/**
	 * Constructor with specific mutation chain probabilities
	 * @param mutationProbabilityPerState
	 * @param addingStatesProbability
	 * @param removingStatesProbability
	 * @param changingStateProbability
	 */
	public FiniteStateAutomatonMutator(double mutationProbabilityPerState, double addingStatesProbability, double removingStatesProbability, double changingStateProbability ) {
		super();
		this.addingStatesProbability = addingStatesProbability;
		this.removingStatesProbability = removingStatesProbability;
		this.changingStateProbability = changingStateProbability;
		this.mutationProbabilityPerState = mutationProbabilityPerState;
		this.distrubutionOfEvents = new double[3];
		distrubutionOfEvents[0] = this.addingStatesProbability;
		distrubutionOfEvents[1] = this.removingStatesProbability;
		distrubutionOfEvents[2] = this.changingStateProbability;
	}
	
	
	
}
