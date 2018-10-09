package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.Objects;

import io.github.oliviercailloux.decision.model.Criterion;

public class Constraint {

	/**
	 * {@value} flagMin = equal 0 if we want a better value than threshold on the
	 * criterion; {@value} valuePref = equal 1 or -1 when it is a hard constraint.
	 */

	private Criterion criterion;
	private double treshold;
	private boolean flagMin;
	private double valuePref;
	private int id;

	public Constraint(int id, Criterion criterion, double treshold, boolean flagMin, double valuePref) {
		this.id = id;
		this.criterion = criterion;
		this.treshold = treshold;
		this.flagMin = flagMin;
		this.valuePref = valuePref;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;

		Constraint other = (Constraint) o;

		return id == other.id;
	}

	@Override
	public String toString() {
		String s = "";

		if (flagMin)
			s += criterion.getName() + " <= " + treshold;
		else
			s += criterion.getName() + " >= " + treshold;

		return s;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public Double getTreshold() {
		return treshold;
	}

	public boolean isFlagMin() {
		return flagMin;
	}

	public Double getValuePref() {
		return valuePref;
	}

	public int getId() {
		return id;
	}
}
