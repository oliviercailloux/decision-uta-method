package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NOAOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private Set<Criterion> setC;

	public NOAOutput(AlternativesComparison alternativesComparison, Set<Criterion> setC) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.setC = requireNonNull(setC);
	}
	
	
	@Override
	public Anchor getAnchor() {
		return Anchor.NOA;
	}
	
	
	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}


	public Set<Criterion> getSetC() {
		return this.setC;
	}

	
	public Set<Criterion> getConNA() {
		Set<Criterion> ConNA = new TreeSet<>();
		
		for (Criterion c : setC) {
			if (this.alternativesComparison.getCriteriaAgainst().contains(c)) {
				ConNA.add(c);
			}
		}
		
		return ConNA;
	}

	
	public Set<Criterion> getConPA() {
		Set<Criterion> ConPA = new TreeSet<>();
		
		for (Criterion c : setC) {
			if (this.alternativesComparison.getCriteriaInFavor().contains(c)) {
				ConPA.add(c);
			}
		}

		return ConPA;
	}
}