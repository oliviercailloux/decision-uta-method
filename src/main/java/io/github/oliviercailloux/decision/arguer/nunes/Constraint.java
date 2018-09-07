package io.github.oliviercailloux.decision.arguer.nunes;

import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Constraint {

	private Criterion criterion;
	private Double treshold;
	private boolean flag_min; // e qual 0 if we want a better value than threshold on the criterion;
	private Double value_pref;
	private boolean isHard; // isHardOrSoft equal 1 when it is a hard constraint
	private int id;

	public Constraint(int id, Criterion c, Double t, boolean f, Double vp, boolean ih) {
		this.id = id;
		this.criterion = c;
		this.treshold = t;
		this.flag_min = f;
		this.value_pref = vp;
		this.isHard = ih;
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

		if (id != other.id)
			return false;

		return true;
	}

	@Override
	public String toString() {
		String s = "";

		if (flag_min)
			s += criterion.getName() + " <= " + treshold;
		else
			s += criterion.getName() + " >= " + treshold;

		return s;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public Double getTreshold() {
		return treshold;
	}

	public boolean isFlag_min() {
		return flag_min;
	}

	public Double getValue_pref() {
		return value_pref;
	}

	public boolean isHard() {
		return isHard;
	}

	public int getId() {
		return id;
	}
}
