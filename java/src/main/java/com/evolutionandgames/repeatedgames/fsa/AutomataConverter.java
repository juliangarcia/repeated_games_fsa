package com.evolutionandgames.repeatedgames.fsa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

public class AutomataConverter {
	
	public static Automaton convertToBrics(ExplicitAutomaton explicitAutomaton) {
		dk.brics.automaton.State[] stateArray = new dk.brics.automaton.State[explicitAutomaton.getSize()];
		for (int i = 0; i < stateArray.length; i++) {
			stateArray[i] = new State();
			stateArray[i].setAccept(explicitAutomaton.getState(i).isCooperate());
		}
		for (int i = 0; i < stateArray.length; i++) {
			stateArray[i].addTransition(new Transition(
					ExplicitAutomaton.COOPERATE_COOPERATE,
					stateArray[explicitAutomaton.getState(i).getNextFocalCooperateOtherCooperate()]));
			stateArray[i].addTransition(new Transition(
					ExplicitAutomaton.COOPERATE_DEFECT, stateArray[explicitAutomaton.getState(i).getNextFocalCooperateOtherDefect()]));
			stateArray[i].addTransition(new Transition(
					ExplicitAutomaton.DEFECT_COOPERATE, stateArray[explicitAutomaton.getState(i).getNextFocalDefectOtherCooperate()]));
			stateArray[i].addTransition(new Transition(
					ExplicitAutomaton.DEFECT_DEFECT, stateArray[explicitAutomaton.getState(i).getNextFocalDefectOtherDefect()]));
		}
		dk.brics.automaton.Automaton automaton = new Automaton();
		automaton.setInitialState(stateArray[0]);
		automaton.setDeterministic(true);
		return automaton;
	}
	
	
	/**
	 * Given a state and an array of states, returns the position of the state
	 * in the array
	 * 
	 * @param array
	 * @param state
	 * @return
	 */
	private static int index(State[] array, State state) {
		for (int i = 0; i < array.length; i++) {
			if (state.equals(array[i]))
				return i;
		}
		throw new IllegalArgumentException("Array does not contain such state");
	}
	
	
	
	/**
	 * Converts from Brics to Explicit
	 * 
	 * @param bricsAutomaton
	 * @return
	 */
	public static ExplicitAutomaton convertFromBrics(Automaton bricsAutomaton) {

		/*
		 * First I get the Set of States of the brics automaton and build an
		 * array from it, makin sure that the initial state is at position zero
		 * of the array, and is only included once
		 */
		Set<State> bricsStatesSet = bricsAutomaton.getStates();
		State[] bricsStatesArray = new State[bricsStatesSet.size()];
		bricsStatesArray[0] = bricsAutomaton.getInitialState();
		int i = 1;
		for (Iterator<State> iterator = bricsStatesSet.iterator(); iterator
				.hasNext();) {
			State state = (State) iterator.next();
			if (!state.equals(bricsStatesArray[0])) {
				bricsStatesArray[i] = state;
				i++;
			}
		}

		// then I build the initial state, with the respective automata to
		// return
		ExplicitAutomaton ans = new ExplicitAutomaton(createExplicitState(
				bricsStatesArray[0], bricsStatesArray));
		// then I go one by one for the non-initial states, i.e., starting at
		// position 1 of the array
		for (int j = 1; j < bricsStatesArray.length; j++) {
			ans.addState(createExplicitState(bricsStatesArray[j],
					bricsStatesArray));
		}
		// finally I restore dead transitions
		restoreDeadTransitions(ans);
		// and check for validity
		if (!ans.isValid())
			throw new IllegalArgumentException("Produced a non-valid machine: "
					+ ans.toString());

		return ans;
	}

	/**
	 * Restore dead states, deleted by the minimizaiton algorithm.
	 * A dead state is a state label defect, with all its transitions pointing to itself.
	 * @param ans
	 */
	private static void restoreDeadTransitions(ExplicitAutomaton ans) {
		ArrayList<ExplicitState> deadStates = listOfDeadStates(ans);
		if (deadStates.isEmpty()){
			//nothing to do
			return;
		}
		//if there are deadStates
		
		//first if it is a singleton defect labeled, do repair in situ
		if (ans.getSize() ==1 && !ans.getState(0).isCooperate()) {
			ExplicitState explicitState = deadStates.get(0);
			if (explicitState.getNextFocalCooperateOtherCooperate() == -1 ) {
				explicitState.setNextFocalCooperateOtherCooperate(0);
			}
			if (explicitState.getNextFocalCooperateOtherDefect() == -1 ) {
				explicitState.setNextFocalCooperateOtherDefect(0);
			}
			if (explicitState.getNextFocalDefectOtherCooperate() == -1 ) {
				explicitState.setNextFocalDefectOtherCooperate(0);
			}
			if (explicitState.getNextFocalDefectOtherDefect() == -1 ) {
				explicitState.setNextFocalDefectOtherDefect(0);
			}
			return;
		}
		//otherwise go all over states
		int lastIndex = ans.getSize();
		ans.addState(new ExplicitState(false, lastIndex, lastIndex, lastIndex, lastIndex));
		for (Iterator<ExplicitState> iterator = deadStates.iterator(); iterator.hasNext();) {
			ExplicitState explicitState = (ExplicitState) iterator.next();
			if (explicitState.getNextFocalCooperateOtherCooperate() == -1 ) {
				explicitState.setNextFocalCooperateOtherCooperate(lastIndex);
			}
			if (explicitState.getNextFocalCooperateOtherDefect() == -1 ) {
				explicitState.setNextFocalCooperateOtherDefect(lastIndex);
			}
			if (explicitState.getNextFocalDefectOtherCooperate() == -1 ) {
				explicitState.setNextFocalDefectOtherCooperate(lastIndex);
			}
			if (explicitState.getNextFocalDefectOtherDefect() == -1 ) {
				explicitState.setNextFocalDefectOtherDefect(lastIndex);
			}
		}
		//done
	}

	private static ArrayList<ExplicitState> listOfDeadStates(ExplicitAutomaton automaton) {
		ArrayList<ExplicitState> ans = new ArrayList<ExplicitState>();
		for (int i = 0; i < automaton.getSize(); i++) {
			if(automaton.getState(i).isDead()) ans.add(automaton.getState(i));
		}
		return ans;
	}


	private static ExplicitState createExplicitState(State bricsState,
			State[] bricsStatesArray) {
		ExplicitState explicitState = new ExplicitState(bricsState.isAccept(),-1, -1, -1, -1);
		Set<Transition> transitionsSet = bricsState.getTransitions();
		for (Iterator<Transition> iterator = transitionsSet.iterator(); iterator
				.hasNext();) {
			Transition transition = (Transition) iterator.next();
			if (transition.getMin() == ExplicitAutomaton.COOPERATE_COOPERATE
					&& transition.getMax() == ExplicitAutomaton.COOPERATE_COOPERATE) {
				explicitState.setNextFocalCooperateOtherCooperate(index(
						bricsStatesArray, transition.getDest()));
			} else if (transition.getMin() == ExplicitAutomaton.COOPERATE_DEFECT
					&& transition.getMax() == ExplicitAutomaton.COOPERATE_DEFECT) {
				explicitState.setNextFocalCooperateOtherDefect(index(
						bricsStatesArray, transition.getDest()));
			} else if (transition.getMin() == ExplicitAutomaton.DEFECT_COOPERATE
					&& transition.getMax() == ExplicitAutomaton.DEFECT_COOPERATE) {
				explicitState.setNextFocalDefectOtherCooperate(index(
						bricsStatesArray, transition.getDest()));
			} else if (transition.getMin() == ExplicitAutomaton.DEFECT_DEFECT
					&& transition.getMax() == ExplicitAutomaton.DEFECT_DEFECT) {
				explicitState.setNextFocalDefectOtherDefect(index(
						bricsStatesArray, transition.getDest()));

				// now all possible intervals
			} else if (transition.getMin() == ExplicitAutomaton.COOPERATE_COOPERATE
					&& transition.getMax() == ExplicitAutomaton.DEFECT_COOPERATE) {
				int index = index(bricsStatesArray, transition.getDest());
				explicitState.setNextFocalCooperateOtherCooperate(index);
				explicitState.setNextFocalDefectOtherDefect(index);
				explicitState.setNextFocalCooperateOtherDefect(index);
				explicitState.setNextFocalDefectOtherCooperate(index);
			}
			else if (transition.getMin() == ExplicitAutomaton.COOPERATE_COOPERATE
					&& transition.getMax() == ExplicitAutomaton.DEFECT_DEFECT) {
				int index = index(bricsStatesArray, transition.getDest());
				explicitState.setNextFocalCooperateOtherCooperate(index);
				explicitState.setNextFocalDefectOtherDefect(index);
			}else if (transition.getMin() == ExplicitAutomaton.COOPERATE_COOPERATE && 
					transition.getMax() == ExplicitAutomaton.COOPERATE_DEFECT) {
				int index = index(bricsStatesArray, transition.getDest());
				explicitState.setNextFocalCooperateOtherCooperate(index);
				explicitState.setNextFocalDefectOtherDefect(index);
				explicitState.setNextFocalCooperateOtherDefect(index);
			}else if (transition.getMin() == ExplicitAutomaton.DEFECT_DEFECT && 
					transition.getMax() == ExplicitAutomaton.COOPERATE_DEFECT) {
				int index = index(bricsStatesArray, transition.getDest());
				explicitState.setNextFocalDefectOtherDefect(index);
				explicitState.setNextFocalCooperateOtherDefect(index);
			}
			else if (transition.getMin() == ExplicitAutomaton.DEFECT_DEFECT && 
					transition.getMax() == ExplicitAutomaton.DEFECT_COOPERATE) {
				int index = index(bricsStatesArray, transition.getDest());
				explicitState.setNextFocalDefectOtherDefect(index);
				explicitState.setNextFocalCooperateOtherDefect(index);
				explicitState.setNextFocalDefectOtherCooperate(index);
			}
			else if (transition.getMin() == ExplicitAutomaton.COOPERATE_DEFECT && 
					transition.getMax() == ExplicitAutomaton.DEFECT_COOPERATE) {
				int index = index(bricsStatesArray, transition.getDest());
				explicitState.setNextFocalCooperateOtherDefect(index);
				explicitState.setNextFocalDefectOtherCooperate(index);
			}
			else {
				throw new IllegalArgumentException("Wrong machine!!");
			}
		}
		return explicitState;
	}

}
