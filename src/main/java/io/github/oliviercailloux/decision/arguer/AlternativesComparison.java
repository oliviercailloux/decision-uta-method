package io.github.oliviercailloux.decision.arguer;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

/**
 * Immutable.
 *
 * @param M
 *            the kind of preference model used in this comparison.
 */
public class AlternativesComparison<M extends Comparator<EvaluatedAlternative>> {

	private M preferenceModel;
	private EvaluatedAlternative x;
	private EvaluatedAlternative y;

	/**
	 * <code>x</code> must be strictly better than <code>y</code> according to the
	 * given preference model.
	 *
	 * @param x
	 *            not <code>null</code>, must be in the domain of the preference
	 *            model.
	 * @param y
	 *            not <code>null</code>, must be in the domain of the preference
	 *            model, must be evaluated on the same criteria than x, must be
	 *            evaluated on some criteria.
	 * @param preferenceModel
	 *            not <code>null</code>, must be immutable (no defensive copy is done).
	 */
	public AlternativesComparison(EvaluatedAlternative x, EvaluatedAlternative y, M preferenceModel) {
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.preferenceModel = requireNonNull(preferenceModel);
		checkArgument(x.getEvaluations().keySet().equals(y.getEvaluations().keySet()));
		checkArgument(x.getEvaluations().keySet().size() >= 1);
		checkArgument(preferenceModel.compare(x, y) > 0);
	}

	public EvaluatedAlternative getX() {
		return this.x;
	}

	public EvaluatedAlternative getY() {
		return this.y;
	}

	public M getPreferenceModel() {
		return preferenceModel;
	}

	/**
	 * @return non empty.
	 */
	public ImmutableSet<Criterion> getCriteria() {
		return ImmutableSet.copyOf(x.getEvaluations().keySet());
	}

	/**
	 * @return the criteria in favor of y better than x.
	 */
	public ImmutableSet<Criterion> getNegativeCriteria() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria(crit -> evalX.get(crit) < evalY.get(crit));
	}

	/**
	 * @return the criteria in favor of x better than y.
	 */
	public ImmutableSet<Criterion> getPositiveCriteria() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria(crit -> evalX.get(crit) > evalY.get(crit));
	}

	private ImmutableSet<Criterion> getFilteredCriteria(Predicate<Criterion> predicate) {
		Stream<Criterion> allCriteriaStream = x.getEvaluations().keySet().stream().sequential();
		Stream<Criterion> criteriaFilteredStream = allCriteriaStream.filter(predicate);

		return criteriaFilteredStream.collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		AlternativesComparison<?> other = (AlternativesComparison<?>) obj;

		if (!other.getX().equals(x))
			return false;

		if (!other.getY().equals(y))
			return false;

		return other.getPreferenceModel().equals(preferenceModel);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x,y,preferenceModel);
	}

}
