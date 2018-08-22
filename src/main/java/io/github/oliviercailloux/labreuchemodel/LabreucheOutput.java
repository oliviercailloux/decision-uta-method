package io.github.oliviercailloux.labreuchemodel;

import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public interface LabreucheOutput {
	
	abstract String argue();
	
	abstract Boolean isApplicable();
}
