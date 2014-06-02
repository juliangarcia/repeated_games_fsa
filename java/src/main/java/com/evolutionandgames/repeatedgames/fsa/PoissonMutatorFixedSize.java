package com.evolutionandgames.repeatedgames.fsa;

import com.evolutionandgames.agentbased.Agent;
import com.evolutionandgames.agentbased.AgentMutator;
import com.evolutionandgames.jevodyn.utils.Random;

public class PoissonMutatorFixedSize extends PoissonMutatorInheritedSize
		implements AgentMutator {

	private int meanSize;

	public PoissonMutatorFixedSize(int meanSize) {
		super();
		this.meanSize = meanSize;
	}

	@Override
	public Agent mutate(Agent agent) {
		int mutantSize = Random.simulatePoissonDistribution((double)meanSize);
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
	
	
	
}
