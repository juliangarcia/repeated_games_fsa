package com.evolutionandgames.repeatedgames;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.evolutionandgames.jevodyn.utils.Random;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomaton;
import com.evolutionandgames.repeatedgames.fsa.PoissonMutatorInheritedSize;

public class PoissonMutatorTest {

	@Test
	public void testParse() {
		Random.seed();
		
		PoissonMutatorInheritedSize mutator = new PoissonMutatorInheritedSize();
		
		String example = "[D/0 0 0 0]";
		ExplicitAutomaton parent = ExplicitAutomaton.parse(example);
		ExplicitAutomaton mutant =(ExplicitAutomaton) mutator.mutate(parent);
		System.out.println(example);
		System.out.println(mutant.toString());
		
		
		
		String example1 = "[D/0 0 1 0, C/1 1 1 1]";
		ExplicitAutomaton parent1 = ExplicitAutomaton.parse(example1);
		ExplicitAutomaton mutant1 =(ExplicitAutomaton) mutator.mutate(parent1);
		System.out.println(example1);
		System.out.println(mutant1.toString());
		
		String example2 = "[D/2 0 1 0, C/1 1 0 0, C/0 2 0 2]";
		ExplicitAutomaton parent2 = ExplicitAutomaton.parse(example2);
		ExplicitAutomaton mutant2 =(ExplicitAutomaton) mutator.mutate(parent2);
		System.out.println(example2);
		System.out.println(mutant2.toString());
		assertTrue(true);
		
	}
	
	
}
