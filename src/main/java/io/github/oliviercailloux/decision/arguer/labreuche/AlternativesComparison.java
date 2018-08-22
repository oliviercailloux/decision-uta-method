package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class AlternativesComparison {

	private Map<Criterion, Double> weights;
	private Alternative x;
	private Alternative y;

	/**
	 * @param x
	 * @param y
	 * @param weights may be empty.
	 */
	public AlternativesComparison(Alternative x, Alternative y, Map<Criterion, Double> weights) {
		this.x = requireNonNull(x);
		this.y = y;
		this.weights = weights;
	}

	public Set<Criterion> getCriteriaAgainst() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) < evalY.get(crit));
	}

	public Set<Criterion> getCriteriaInFavor() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) > evalY.get(crit));
	}

	/**
	 * @return a map containing, for each criterion, the difference in evaluation on
	 *         that criterion: evaluation of x minus evaluation of y.
	 */
	public Map<Criterion, Double> getDelta() {
		Map<Criterion, Double> res = new LinkedHashMap<>();

		for (Criterion c : weights.keySet()) {
			res.put(c, x.getEvaluations().get(c) - y.getEvaluations().get(c));
		}

		return res;
	}

	public Map<Criterion, Double> getWeight() {
		return this.weights;
	}

	public Alternative getX() {
		return this.x;
	}

	public Alternative getY() {
		return this.y;
	}

	private Set<Criterion> getFilteredCriteria(Predicate<Criterion> predicate) {
		Stream<Criterion> allCriteriaStream = weights.keySet().stream().sequential();
		Stream<Criterion> criteriaFilteredStream = allCriteriaStream.filter(predicate);
		Set<Criterion> criteriaFiltered = criteriaFilteredStream.collect(Collectors.toCollection(LinkedHashSet::new));
		return criteriaFiltered;
	}

}
