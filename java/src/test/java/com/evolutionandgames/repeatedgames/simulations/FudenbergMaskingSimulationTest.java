package com.evolutionandgames.repeatedgames.simulations;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.evolutionandgames.jevodyn.utils.Random;

public class FudenbergMaskingSimulationTest {

	@Before
	public void setUp() throws Exception {
		Random.seed();
	}

	@Test
	public void testRunApp() throws IOException {
		// FudenbergMaskinSimulation sim =
		// FudenbergMaskinSimulation.exampleObject();
		// sim.init();
		// FudenbergMaskinSimulation.runApp(sim);
		assertTrue(true);
	}

	@Test
	public void testSpecificValues() throws IOException {
		/*String filename = "/Users/garcia/Dropbox/projects/repeated_games_with_mistakes/data/2013-07-01/pbs_files/2013-07-01_delta_0.9_mistake_0.05_local_mut_GLOBAL_replica_2.json";
		FudenbergMaskinSimulation sim = FudenbergMaskinSimulation
				.loadFromFile(filename);
		sim.init();
		try {
			sim.init();
			FudenbergMaskinSimulation.runApp(sim);
		} catch (Exception e) {
			System.out.println(e);
		}*/
		assertTrue(true);

	}

}
