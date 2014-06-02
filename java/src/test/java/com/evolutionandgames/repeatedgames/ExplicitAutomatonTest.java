package com.evolutionandgames.repeatedgames;

import static org.junit.Assert.*;

import org.junit.Test;

import com.evolutionandgames.repeatedgames.fsa.AutomataConverter;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomaton;

import dk.brics.automaton.Automaton;

public class ExplicitAutomatonTest {

	@Test
	public void testParse() {
		String example = "[D/0 0 0 0]";
		//String example = "[D/DD0DC0CD0CC0]";
		ExplicitAutomaton result = ExplicitAutomaton.parse(example);
		//System.out.println(result.toString());
		assertEquals(example, result.toString());
		
		//String example1 = "[D/DD0DC0CD1CC0, C/DD1DC1CD1CC1]";
		String example1 = "[D/0 0 1 0, C/1 1 1 1]";
		ExplicitAutomaton result1 = ExplicitAutomaton.parse(example1);
		//System.out.println(result1.toString());
		assertEquals(example1, result1.toString());
		
		
		//String example3 = "[D/DD2DC0CD1CC0, C/DD1DC1CD0CC0, C/DD0DC2CD0CC2]";
		String example3 = "[D/2 0 1 0, C/1 1 0 0, C/0 2 0 2]";
		ExplicitAutomaton result3 = ExplicitAutomaton.parse(example3);
		//System.out.println(result3.toString());
		assertEquals(example3, result3.toString());
		
	}
	
	
	//equality & minimize
	@Test
	public void testMinimization(){
		//ExplicitAutomaton simpleAllD = ExplicitAutomaton.parse("[D/DD0DC0CD0CC0]");
		ExplicitAutomaton simpleAllD = ExplicitAutomaton.parse("[D/0 0 0 0]");
		//ExplicitAutomaton complicatedAllD = ExplicitAutomaton.parse("[D/DD1DC0CD0CC0, D/DD2DC0CD0CC0, D/DD0DC0CD0CC1]");
		ExplicitAutomaton complicatedAllD = ExplicitAutomaton.parse("[D/1 0 0 0, D/2 0 0 0, D/0 0 0 1]");
		assertEquals("[D/0 0 0 0]", complicatedAllD.minimize().toString());
		assertTrue(simpleAllD.equals(complicatedAllD));
	}

	//@Test
	public void testMinimization2(){
		//ExplicitAutomaton automaton = ExplicitAutomaton.parse("[D/DD0DC0CD1CC0, C/DD1DC1CD1CC1]");
		ExplicitAutomaton automaton = ExplicitAutomaton.parse("[D/0 0 1 0, C/1 1 1 1]");
		ExplicitAutomaton automatonMinimized = automaton.minimize();
		assertEquals(automaton, automatonMinimized);
		
	}
	
	@Test
	public void testDeadStates(){
		//ExplicitAutomaton automaton = ExplicitAutomaton.parse("[C/DD1DC2CD0CC0, D/DD0DC2CD1CC1, D/DD2DC2CD2CC2]");
		ExplicitAutomaton automaton = ExplicitAutomaton.parse("[C/1 2 0 0, D/0 2 1 1, D/2 2 2 2]");
		ExplicitAutomaton minimized = automaton.minimize();
		System.out.println(minimized);
	    Automaton bricsOriginal = AutomataConverter.convertToBrics(automaton);
	    Automaton bricsMinimized = AutomataConverter.convertToBrics(minimized);
	    System.out.println(bricsOriginal.equals(bricsMinimized));
	    boolean result = minimized.equals(automaton);
		assertTrue(result);
		
		
	}
	
	
	//play
	
	
	
	//mutations
	
}
