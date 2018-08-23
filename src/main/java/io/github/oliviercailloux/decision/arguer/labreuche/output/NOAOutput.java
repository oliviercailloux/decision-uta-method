package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

/**
 *
 * Immutable.
 */
public class NOAOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private ImmutableSet<Criterion> noaCriteria;

	/**
	 * @param noaCriteria may not be empty.
	 */
	public NOAOutput(AlternativesComparison alternativesComparison, Set<Criterion> noaCriteria) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.noaCriteria = ImmutableSet.copyOf(requireNonNull(noaCriteria));
		checkArgument(!noaCriteria.isEmpty());
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.NOA;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public ImmutableSet<Criterion> getNoaCriteria() {
		return this.noaCriteria;
	}

	/**
	 * @return the criteria among noaCriteria that are in favor of y against x.
	 */
	public ImmutableSet<Criterion> getNegativeNoaCriteria() {
		return ImmutableSet.copyOf(Sets.intersection(noaCriteria, alternativesComparison.getNegativeCriteria()));
	}

	/**
	 * @return the criteria among noaCriteria that are in favor of x against y.
	 */
	public Set<Criterion> getPositiveNoaCriteria() {
		return ImmutableSet.copyOf(Sets.intersection(noaCriteria, alternativesComparison.getPositiveCriteria()));
	}
}
