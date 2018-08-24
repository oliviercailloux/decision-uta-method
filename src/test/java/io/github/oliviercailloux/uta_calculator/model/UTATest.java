package io.github.oliviercailloux.uta_calculator.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.view.MainBuyingNewCar;


public class UTATest {

	@Test
	public void utaLearningFromExamplesShouldReproduceThoseExample() {

		MainBuyingNewCar buyingNewCarExercice = new MainBuyingNewCar();
		List<Alternative> correctAlternatives = buyingNewCarExercice.getMainAlternatives();
		List<Alternative> utaAlternatives = buyingNewCarExercice.getAlternativeSorted();

		//"Does the buying new car work fines ?"
		assertEquals(correctAlternatives, utaAlternatives);
	}

}