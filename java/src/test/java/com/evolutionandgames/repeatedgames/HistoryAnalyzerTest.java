package com.evolutionandgames.repeatedgames;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.evolutionandgames.jevodyn.utils.Random;
import com.evolutionandgames.repeatedgames.evolution.Action;
import com.evolutionandgames.repeatedgames.evolution.HistoryAnalyzer;
import com.evolutionandgames.repeatedgames.evolution.RepeatedGame;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomataFactory;
import com.evolutionandgames.repeatedgames.fsa.ExplicitAutomaton;

public class HistoryAnalyzerTest {

	private double reward = 3.0;
	private double sucker = 0.0;
	private double temptation = 5.0;
	private double punishment = 1.0;
	private double continuationProbability = 0.9999;

	// @Test
	public void test() {
		Random.seed();
		RepeatedGame repeatedGame = new RepeatedGame(reward, sucker,
				temptation, punishment, continuationProbability);
		ExplicitAutomaton allc = ExplicitAutomataFactory.allC();
		ExplicitAutomaton alld = ExplicitAutomataFactory.allD();
		for (int i = 0; i < 10; i++) {
			repeatedGame.playOnce(alld, allc);
			String encoded = HistoryAnalyzer.encodeHistories(
					repeatedGame.getActionsPlayerOne(),
					repeatedGame.getActionsPlayerTwo());
			System.out.println(encoded);

		}
		assertTrue(true);

	}

	public List<Action> randomPlay() {
		ArrayList<Action> ans = new ArrayList<Action>();
		if (Random.nextBoolean()) {
			ans.add(Action.COOPERATE);
		} else {
			ans.add(Action.DEFECT);
		}
		while (Random.bernoulliTrial(continuationProbability)) {
			if (Random.nextBoolean()) {
				ans.add(Action.COOPERATE);
			} else {
				ans.add(Action.DEFECT);
			}
		}
		return ans;
	}

	public List<Action> randomPlay(int size) {
		ArrayList<Action> ans = new ArrayList<Action>();
		for (int i = 0; i < size; i++) {
			if (Random.nextBoolean()) {
				ans.add(Action.COOPERATE);
			} else {
				ans.add(Action.DEFECT);
			}
		}
		return ans;
	}

	// @Test
	public void test2() {
		Random.seed();
		List<Action> player1 = randomPlay();
		List<Action> player2 = randomPlay(player1.size());
		String encoded = HistoryAnalyzer.encodeHistories(player1, player2);
		System.out.println(player1);
		System.out.println(player2);
		System.out.println(encoded);
		System.out.println("Leniency: "
				+ HistoryAnalyzer.computeLeniencyScore(encoded));
		System.out.println("Forgiveness: "
				+ HistoryAnalyzer.computeForgivenessScore(encoded));

	}

	@Test
	public void testStrategiesTwiceAndPunisher() {
		Random.seed();
		RepeatedGame repeatedGame = new RepeatedGame(reward, sucker,
				temptation, punishment, continuationProbability);
		ExplicitAutomaton player1 = ExplicitAutomataFactory.punisherForOneRound();
		ExplicitAutomaton player2 =ExplicitAutomataFactory.punisherForOneRound();
		System.out.println(player1);
		System.out.println("vs");
		System.out.println(player2);
		repeatedGame.playOnce(player1, player2, 0.01);
		String encoded = HistoryAnalyzer.encodeHistories(
				repeatedGame.getActionsPlayerOne(),
				repeatedGame.getActionsPlayerTwo());
		//System.out.println(punisher);
		//System.out.println(twice);
		//System.out.println(repeatedGame.getActionsPlayerOne());
		//System.out.println(repeatedGame.getActionsPlayerTwo());
		System.out.println(encoded);
		System.out.println("Leniency: "
				+ HistoryAnalyzer.computeLeniencyScore(encoded));
		System.out.println("Forgiveness: "
				+ HistoryAnalyzer.computeForgivenessScore(encoded));

	}

}
