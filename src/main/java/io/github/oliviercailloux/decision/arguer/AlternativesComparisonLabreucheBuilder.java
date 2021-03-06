package io.github.oliviercailloux.decision.arguer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class AlternativesComparisonLabreucheBuilder {

	private List<Double> x;
	private List<Double> y;
	private List<Double> w;

	public AlternativesComparisonLabreucheBuilder() {
		x = new ArrayList<>();
		y = new ArrayList<>();
		w = new ArrayList<>();
	}

	public AlternativesComparisonLabreucheBuilder setX(List<Double> x) {
		checkNotNull(x);
		this.x.clear();
		this.x.addAll(x);
		return this;
	}

	public AlternativesComparisonLabreucheBuilder setY(List<Double> y) {
		checkNotNull(y);
		this.y.clear();
		this.y.addAll(y);
		return this;
	}

	public AlternativesComparisonLabreucheBuilder setW(List<Double> weights) {
		checkNotNull(weights);
		w.clear();
		w.addAll(weights);
		return this;
	}

	public EvaluatedAlternative getX() {
		checkState(!x.isEmpty());

		final Set<Criterion> criteria = getCriteria(x.size());

		final ImmutableMap<Criterion, Double> evals = asMap(criteria, x);

		return new EvaluatedAlternative(1, "X", evals);
	}

	public EvaluatedAlternative getY() {
		checkState(!y.isEmpty());

		final Set<Criterion> criteria = getCriteria(y.size());

		final ImmutableMap<Criterion, Double> evals = asMap(criteria, y);

		return new EvaluatedAlternative(2, "Y", evals);
	}

	public ImmutableMap<Criterion, Double> getWeights() {
		checkState(!w.isEmpty());

		final Set<Criterion> criteria = getCriteria(w.size());

		return asMap(criteria, w);
	}

	public AlternativesComparison<LabreucheModel> build() {
		final int size = x.size();
		checkState(size == y.size());
		checkState(size == w.size());
		checkState(size >= 1);

		final LabreucheModel model = new LabreucheModel(getWeights());
		return new AlternativesComparison<>(getX(), getY(), model);
	}

	private Set<Criterion> getCriteria(int size) {
		final Builder<Criterion> builder = ImmutableSet.builderWithExpectedSize(size);
		for (int i = 1; i <= size; ++i) {
			builder.add(new Criterion(i, "c" + i));
		}
		return builder.build();
	}

	private ImmutableMap<Criterion, Double> asMap(Set<Criterion> criteria, List<Double> values) {
		assert criteria.size() == values.size();

		final ImmutableMap.Builder<Criterion, Double> builder = ImmutableMap.builderWithExpectedSize(criteria.size());
		final Iterator<Double> iterator = values.iterator();

		for (Criterion criterion : criteria) {
			builder.put(criterion, iterator.next());
		}

		return builder.build();
	}
}
