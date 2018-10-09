package io.github.oliviercailloux.decision.arguer.nunes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class NunesModel implements Comparator<EvaluatedAlternative> {

	private ImmutableMap<Criterion, Double> weights;
	private Set<Constraint> constraints;
	private final Comparator<Double> comparingScores;

	/**
	 * @param weights
	 *            not <code>null</code>, may be empty.
	 * @param constraints
	 *            not <code>null</code>, may be empty.
	 */
	public NunesModel(Map<Criterion, Double> weights, Set<Constraint> constraints) {
		requireNonNull(weights);
		this.weights = ImmutableMap.copyOf(weights);
		this.constraints = constraints;
		this.comparingScores = Comparator.comparingDouble(null);
	}

	public Set<Constraint> getConstraints() {
		return this.constraints;
	}

	public double getScore(EvaluatedAlternative a, EvaluatedAlternative b,
			Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradeoffs) {
		checkArgument(a.getEvaluations().keySet().equals(weights.keySet()));
		return NunesTools.score(a, b, weights.keySet(), weights, tradeoffs);
	}

	/**
	 * @return not <code>null</code>, may be empty.
	 */
	public ImmutableMap<Criterion, Double> getWeights() {
		return this.weights;
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
	public int compare(EvaluatedAlternative a, EvaluatedAlternative b) {
		Set<EvaluatedAlternative> set = new LinkedHashSet<>();
		set.add(a);
		set.add(b);
		Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradeoffs = NunesTools.computeTO(set, weights);

		double scoreA = NunesTools.score(a, b, weights.keySet(), weights, tradeoffs);
		double scoreB = NunesTools.score(a, b, weights.keySet(), weights, tradeoffs);

		return comparingScores.compare(scoreB, scoreA);
	}

}
