package io.github.oliviercailloux.uta_calculator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.oliviercailloux.uta_calculator.view.MainBuyingNewCar;

public class UTATest {

	// @Test
	public void utaLearningFromExamplesShouldReproduceThoseExample() {

		MainBuyingNewCar buyingNewCarExercice = new MainBuyingNewCar();
		List<Alternative> correctAlternatives = buyingNewCarExercice.getMainAlternatives();
		List<Alternative> utaAlternatives = buyingNewCarExercice.getAlternativeSorted();

		// "Does the buying new car work fines ?"
		assertEquals(correctAlternatives, utaAlternatives);
	}
}