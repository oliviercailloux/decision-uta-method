package io.github.oliviercailloux.decision.arguer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

/**
 *
 * Immutable.
 */
public class AlternativesComparison {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlternativesComparison.class);
	private ImmutableMap<Criterion, Double> weights;
	private Alternative x;
	private Alternative y;

	/**
	 * The parameters must be such that x and y do not have the same evaluation.
	 *
	 * @param x
	 * @param y
	 * @param weights
	 */
	public AlternativesComparison(Alternative x, Alternative y, Map<Criterion, Double> weights) {
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		checkNotNull(weights);
		this.weights = ImmutableMap.copyOf(requireNonNull(weights));
		assert !weights.isEmpty();

		LOGGER.info("Checking is X better than Y");
		(this).isXbetterY();

		LOGGER.info("Weights vector normalized");
		(this).WeightsSumInOne();
	}

	private void WeightsSumInOne() {
		double sum_weights = 0.0;

		for (Map.Entry<Criterion, Double> entry : weights.entrySet()) {
			sum_weights += entry.getValue().doubleValue();
		}

		Builder<Criterion, Double> builder = ImmutableMap.builder();

		for (Map.Entry<Criterion, Double> entry : weights.entrySet()) {
			builder.put(entry.getKey(), entry.getValue().doubleValue() / sum_weights);
		}

		weights = builder.build();
	}

	/**
	 * @return the criteria in favor of y better than x.
	 */
	public ImmutableSet<Criterion> getNegativeCriteria() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) < evalY.get(crit));
	}

	/**
	 * @return the criteria in favor of x better than y.
	 */
	public ImmutableSet<Criterion> getPositiveCriteria() {
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

	/**
	 * @return non empty.
	 */
	public ImmutableSet<Criterion> getCriteria() {
		return weights.keySet();
	}

	private void isXbetterY() {
		double scoreX = Tools.score(x, weights);
		double scoreY = Tools.score(y, weights);

		if (scoreY > scoreX) {
			LOGGER.info("Y better than X => switch in AlternativesComparison");
			Alternative tmp = this.x;
			this.x = this.y;
			this.y = tmp;
		}
	}

	public ImmutableMap<Criterion, Double> getWeightReference() {
		Builder<Criterion, Double> weightsReference = ImmutableMap.builder();

		for (Criterion c : weights.keySet()) {
			weightsReference.put(c, 1.0 / weights.keySet().size());
		}

		return weightsReference.build();
	}

}
