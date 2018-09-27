package io.github.oliviercailloux.decision.arguer;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheTools;
import io.github.oliviercailloux.decision.arguer.nunes.NunesTools;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

/**
 * Immutable.
 *
 * @param M
 *            the kind of preference model used in this comparison.
 */
public class AlternativesComparison<M extends Comparator<Alternative>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlternativesComparison.class);
	private M preferenceModel;
	private Alternative x;
	private Alternative y;

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
	 *            must be immutable (no defensive copy is done).
	 */
	public AlternativesComparison(Alternative x, Alternative y, M preferenceModel) {
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.preferenceModel= requireNonNull(preferenceModel);
		checkArgument(x.getEvaluations().keySet().equals(y.getEvaluations().keySet()));
		checkArgument(x.getEvaluations().keySet().size() >= 1);

		LOGGER.info("Checking is X better than Y");
		checkArgument(preferenceModel.compare(x, y) > 0);
	}

	public Alternative getX() {
		return this.x;
	}

	public Alternative getY() {
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

	private double score(Alternative a, Alternative b, Model modelType) {

		switch (modelType) {

		case LABREUCHE:
			return LabreucheTools.score(a, preferenceModel);

		case NUNES:
			Set<Alternative> alts = new LinkedHashSet<>();
			alts.add(x);
			alts.add(y);

			return NunesTools.score(a, b, this.getCriteria(), weights, NunesTools.computeTO(alts, weights));

		default:
			throw new IllegalArgumentException("Model type undifined");
		}
	}

}
