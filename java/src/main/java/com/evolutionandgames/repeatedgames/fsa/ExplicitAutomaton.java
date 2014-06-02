package com.evolutionandgames.repeatedgames.fsa;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.evolutionandgames.agentbased.Agent;
import com.evolutionandgames.repeatedgames.evolution.Action;
import com.evolutionandgames.repeatedgames.evolution.RepeatedStrategy;

import dk.brics.automaton.Automaton;

public class ExplicitAutomaton implements Agent, RepeatedStrategy {

	public static boolean MINIMIZE_BEFORE_PRINTING = false;
	
	

	public static final char COOPERATE_COOPERATE = 'C';
	public static final char COOPERATE_DEFECT = 'E';
	public static final char DEFECT_COOPERATE = 'O';
	public static final char DEFECT_DEFECT = 'D';

	private ArrayList<ExplicitState> states;
	private int currentState = 0;

	@Override
	public int hashCode() {
		return AutomataConverter.convertToBrics(this).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExplicitAutomaton other = (ExplicitAutomaton) obj;
		Automaton otherBrics = AutomataConverter.convertToBrics(other);
		return AutomataConverter.convertToBrics(this).equals(
				otherBrics);
	}

	@Override
	public String toString() {
		if (MINIMIZE_BEFORE_PRINTING) {
			return this.minimize().states.toString();
		}else{
			return this.states.toString();
		}
	}

	public int getSize() {
		return this.states.size();
	}

	public boolean isValid() {
		if (states == null || states.size() <= 0)
			return false;
		for (int i = 0; i < states.size(); i++) {
			if (!states.get(i).isValid(states.size())) {
				return false;
			}
		}
		return true;
	}

	public ExplicitAutomaton(ExplicitState initialState) {
		super();
		this.states = new ArrayList<ExplicitState>();
		states.add(initialState);
		this.currentState = 0;
	}

	public void addState(ExplicitState state) {
		this.states.add(state);
	}

	public void reset() {
		this.currentState = 0;
	}

	public Action currentAction() {
		if (states.get(currentState).isCooperate())
			return Action.COOPERATE;
		return Action.DEFECT;
	}

	public void next(Action focal, Action other) {
		if (focal == Action.COOPERATE && other == Action.COOPERATE) {
			this.currentState = states.get(currentState)
					.getNextFocalCooperateOtherCooperate();
		} else if (focal == Action.COOPERATE && other == Action.DEFECT) {
			this.currentState = states.get(currentState)
					.getNextFocalCooperateOtherDefect();
		} else if (focal == Action.DEFECT && other == Action.COOPERATE) {
			this.currentState = states.get(currentState)
					.getNextFocalDefectOtherCooperate();
		} else {
			// DEFECT DEFECT
			this.currentState = states.get(currentState)
					.getNextFocalDefectOtherDefect();
		}

	}

	public ExplicitState getState(int index) {
		return states.get(index);
	}

	/**
	 * Removes a state.
	 * 
	 * @param stateToRemove
	 */
	public void removeState(int stateToRemove) {
		states.remove(stateToRemove);
	}

	public ExplicitAutomaton copy() {
		ExplicitAutomaton copy = new ExplicitAutomaton(this.getState(0)
				.getCopy());
		for (int i = 1; i < this.getSize(); i++) {
			copy.addState(this.getState(i).getCopy());
		}
		return copy;
	}

	/**
	 * Minimizes the automata, returns a new instance
	 * 
	 * @return
	 */
	public ExplicitAutomaton minimize() {
		Automaton.setMinimization(Automaton.MINIMIZE_HOPCROFT);
		Automaton brics = AutomataConverter.convertToBrics(this);
		brics.minimize();

		return AutomataConverter.convertFromBrics(brics);
	}

	public static ExplicitAutomaton parse(String stringRepresentation) {
		// remove end and star delimiters
		stringRepresentation = StringUtils.removeStart(stringRepresentation,
				"[");
		stringRepresentation = StringUtils.removeEnd(stringRepresentation, "]");
		// split per state
		String[] arrayOfStateStrings = StringUtils.split(stringRepresentation,
				',');
		ExplicitState firsState = ExplicitState
				.stringToState(arrayOfStateStrings[0]);
		ExplicitAutomaton ans = new ExplicitAutomaton(firsState);
		for (int i = 1; i < arrayOfStateStrings.length; i++) {
			ans.addState(ExplicitState.stringToState(arrayOfStateStrings[i]));
		}
		if (!ans.isValid())
			throw new IllegalArgumentException("Produced a non-valid machine: "
					+ ans.toString());
		return ans;

	}

	public ArrayList<ExplicitState> getStates() {
		return states;
	}

}
