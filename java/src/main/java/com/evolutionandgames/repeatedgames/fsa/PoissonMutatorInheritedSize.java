package com.evolutionandgames.repeatedgames.fsa;

import com.evolutionandgames.agentbased.Agent;
import com.evolutionandgames.agentbased.AgentMutator;
import com.evolutionandgames.jevodyn.utils.Random;

public class PoissonMutatorInheritedSize implements AgentMutator {

	public Agent mutate(Agent agent) {
		int size = ((ExplicitAutomaton) agent).getSize();
		int mutantSize = Random.simulatePoissonDistribution((double)size);
		ExplicitAutomaton mutant = null;
		if (Random.nextBoolean()) {
			mutant = ExplicitAutomataFactory.allC();
		}else{
			mutant = ExplicitAutomataFactory.allD();
		}
		for (int i = 0; i < mutantSize-1; i++) {
			addState(mutant);
		}
		return mutant;
	}
	
	
	protected void addState(ExplicitAutomaton automaton){
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
	}

}
