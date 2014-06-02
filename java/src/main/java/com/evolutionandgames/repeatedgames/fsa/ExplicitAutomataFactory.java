package com.evolutionandgames.repeatedgames.fsa;

import com.evolutionandgames.repeatedgames.evolution.Action;


public class ExplicitAutomataFactory {
	
	public static ExplicitAutomaton allD(){
		ExplicitState state = new ExplicitState(false, 0, 0, 0, 0);
		ExplicitAutomaton automaton = new ExplicitAutomaton(state);
		return automaton;
	}
	
	public static ExplicitAutomaton allC(){
		ExplicitState state = new ExplicitState(true, 0, 0, 0, 0);
		ExplicitAutomaton automaton = new ExplicitAutomaton(state);
		return automaton;
	}
	
	public static ExplicitAutomaton simpleStrategy(Action action){
		if (action == Action.COOPERATE) {
			return allC();
		}
		return allD();
	}

	
	public static ExplicitAutomaton punisherForOneRound(){
		//DD DC CD CC
		ExplicitState state1 = new ExplicitState(true, 1, 0, 1, 0);
		ExplicitState state2 = new ExplicitState(false, 0, 0, 0, 0);
		ExplicitAutomaton automaton = new ExplicitAutomaton(state1);
		automaton.addState(state2);
		return automaton;
	}
	
	
	public static ExplicitAutomaton onceTwiceGrim(){
		//DD DC CD CC
		ExplicitState state1 = new ExplicitState(true, 1, 0, 1, 0);
		ExplicitState state2 = new ExplicitState(true, 2, 0, 2, 0);
		ExplicitState state3 = new ExplicitState(false, 2, 2, 2, 2);
		ExplicitAutomaton automaton = new ExplicitAutomaton(state1);
		automaton.addState(state2);
		automaton.addState(state3);
		return automaton;
	}
	
	
}
