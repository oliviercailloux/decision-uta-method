package io.github.oliviercailloux.decision.arguer.labreuche;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

/**
 *
 * Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class LabreucheModel implements Comparator<EvaluatedAlternative> {

	public static double getEpsilonDefaultValue(Map<Criterion, Double> weights) {
		requireNonNull(weights);
		if (weights.isEmpty()) {
			return 0;
		}
		return 0.2 / weights.size();
	}

	public static double getEpsilonWDefaultValue(Map<Criterion, Double> weights) {
		requireNonNull(weights);
		if (weights.isEmpty()) {
			return 0;
		}
		return 0.15 / weights.size();
	}

	public static double getEpsilonWPrimeDefaultValue(Map<Criterion, Double> weights) {
		requireNonNull(weights);
		if (weights.isEmpty()) {
			return 0;
		}
		return 0.3 / weights.size();
	}

	private ImmutableMap<Criterion, Double> weights;
	private double epsilon;
	private double epsilonW;
	private double epsilonWPrime;

	private final Comparator<EvaluatedAlternative> comparingScores;

	/**
	 * @param weights
	 *            not <code>null</code>, may be empty.
	 * @param epsilon
	 *            a finite non-negative value.
	 * @param epsilonW
	 *            a finite non-negative value.
	 * @param epsilonWPrime
	 *            a finite non-negative value greater than or equal to epsilonW.
	 */
	public LabreucheModel(Map<Criterion, Double> weights, double epsilon, double epsilonW, double epsilonWPrime) {
		requireNonNull(weights);
		checkArgument(Double.isFinite(epsilon) && epsilon >= 0d);
		checkArgument(Double.isFinite(epsilonW) && epsilonW >= 0d);
		checkArgument(Double.isFinite(epsilonWPrime) && epsilonWPrime >= epsilonW);
		this.weights = ImmutableMap.copyOf(weights);
		this.epsilon = epsilon;
		this.epsilonW = epsilonW;
		comparingScores = Comparator.comparingDouble(this::getScore);
	}

	/**
	 * @param weights
	 *            not <code>null</code>, may be empty.
	 */
	public LabreucheModel(Map<Criterion, Double> weights) {
		this(weights, getEpsilonDefaultValue(weights), getEpsilonWDefaultValue(weights),
				getEpsilonWPrimeDefaultValue(weights));
	}

	/**
	 * @return not <code>null</code>, may be empty.
	 */
	public ImmutableMap<Criterion, Double> getWeights() {
		return this.weights;
	}

	/**
	 * @return a finite non-negative value.
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @return a finite non-negative value.
	 */
	public double getEpsilonW() {
		return epsilonW;
	}

	/**
	 * @return a finite non-negative value greater than or equal to epsilonW.
	 */
	public double getEpsilonWPrime() {
		return epsilonWPrime;
	}

	@Override
	public int compare(EvaluatedAlternative a1, EvaluatedAlternative a2) {
		return comparingScores.compare(a1, a2);
	}

	public double getScore(EvaluatedAlternative a) {
		checkArgument(a.getEvaluations().keySet().equals(weights.keySet()));
		return weights.entrySet().stream().mapToDouble((e) -> e.getValue() * a.getEvaluations().get(e.getKey())).sum();
	}

	/**
	 * @return a map containing, for each criterion, the difference in evaluation on
	 *         that criterion: evaluation of x minus evaluation of y.
	 */
	public ImmutableMap<Criterion, Double> getDelta(EvaluatedAlternative x, EvaluatedAlternative y) {
		checkArgument(x.getEvaluations().keySet().equals(weights.keySet()));
		checkArgument(y.getEvaluations().keySet().equals(weights.keySet()));

		return weights.keySet().stream()
				.collect(toImmutableMap((c) -> c, (c) -> x.getEvaluations().get(c) - y.getEvaluations().get(c)));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		LabreucheModel other = (LabreucheModel) obj;

		if (other.getEpsilon() != epsilon)
			return false;

		if (other.getEpsilonW() != epsilonW)
			return false;

		if (other.getEpsilonWPrime() != epsilonWPrime)
			return false;

		return other.getWeights().equals(weights);
	}

	@Override
	public int hashCode() {
		return Objects.hash(weights, epsilon, epsilonW, epsilonWPrime);
	}
}
