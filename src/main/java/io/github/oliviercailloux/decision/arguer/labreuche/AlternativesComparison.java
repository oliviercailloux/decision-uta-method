package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

/**
 *
 * Immutable.
 */
public class AlternativesComparison {

	private ImmutableMap<Criterion, Double> weights;
	private Alternative x;
	private Alternative y;

	/**
	 * @param x
	 * @param y
	 * @param weights may be empty.
	 */
	public AlternativesComparison(Alternative x, Alternative y, Map<Criterion, Double> weights) {
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.weights = ImmutableMap.copyOf(requireNonNull(weights));
	}

	public ImmutableSet<Criterion> getCriteriaAgainst() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) < evalY.get(crit));
	}

	public ImmutableSet<Criterion> getCriteriaInFavor() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) > evalY.get(crit));
	}

	/**
	 * @return a map containing, for each criterion, the difference in evaluation on
	 *         that criterion: evaluation of x minus evaluation of y.
	 */
	public ImmutableMap<Criterion, Double> getDelta() {
		Builder<Criterion, Double> builder = ImmutableMap.builder();

		for (Criterion c : weights.keySet()) {
			builder.put(c, x.getEvaluations().get(c) - y.getEvaluations().get(c));
		}

		return builder.build();
	}

	public ImmutableMap<Criterion, Double> getWeight() {
		return this.weights;
	}

	public Alternative getX() {
		return this.x;
	}

	public Alternative getY() {
		return this.y;
	}

	private ImmutableSet<Criterion> getFilteredCriteria(Predicate<Criterion> predicate) {
		Stream<Criterion> allCriteriaStream = weights.keySet().stream().sequential();
		Stream<Criterion> criteriaFilteredStream = allCriteriaStream.filter(predicate);
		ImmutableSet<Criterion> criteriaFiltered = criteriaFilteredStream.collect(ImmutableSet.toImmutableSet());
		return criteriaFiltered;
	}

	public ImmutableSet<Criterion> getCriteria() {
		return weights.keySet();
	}

}
