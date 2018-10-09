package io.github.oliviercailloux.decision.model;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class EvaluatedAlternative {

	private int id;
	private String name;
	private ImmutableMap<Criterion, Double> evaluations;
	
	/***
	 * 
	 * @param id
	 * 				not <code>null</code>
	 * @param name
	 * 				not <code>null</code>
	 * @param evaluations 
	 * 				not <code>empty</code>
	 */
	public EvaluatedAlternative(int id, String name, Map<Criterion, Double> evaluations) {
		this.id = requireNonNull(id);
		this.name = requireNonNull(name);
		checkArgument(!evaluations.isEmpty());
		this.evaluations = ImmutableMap.copyOf(evaluations);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ImmutableMap<Criterion, Double> getEvaluations() {
		return evaluations;
	}

	@Override
	public String toString() {
		ToStringHelper stringHelper = MoreObjects.toStringHelper(this);

		stringHelper.addValue(id);

		if (!name.equals("c" + id)) {
			stringHelper.addValue(name);
		}

		if (!evaluations.isEmpty()) {
			stringHelper.addValue(evaluations);
		}

		return stringHelper.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((evaluations == null) ? 0 : evaluations.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		EvaluatedAlternative other = (EvaluatedAlternative) obj;

		if (evaluations == null) {
			if (other.evaluations != null)
				return false;

		} else if (!evaluations.equals(other.evaluations))
			return false;

		if (id != other.id)
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		return true;
	}
}
