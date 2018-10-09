package io.github.oliviercailloux.uta_calculator.model;

import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class CriterionWithScale {

	// Attributes
	private int id;
	private String name;
	private List<Double> scale;

	// Constructors
	public CriterionWithScale(int id, String name, List<Double> scale) {
		this.id = id;
		this.name = name;
		this.scale = scale;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Double> getScale() {
		return scale;
	}

	public void setScale(List<Double> scale) {
		this.scale = scale;
	}

	// Methods
	public double getMinValue() {
		return scale.get(0);
	}

	public double getMaxValue() {
		return scale.get(scale.size() - 1);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		CriterionWithScale other = (CriterionWithScale) obj;

		return id == other.id;
	}

	@Override
	public String toString() {
		ToStringHelper stringHelper = MoreObjects.toStringHelper(this);

		stringHelper.addValue(id);

		if (!name.equals("c" + id)) {
			stringHelper.addValue(name);
		}

		if (!scale.isEmpty()) {
			stringHelper.addValue(scale);
		}

		return stringHelper.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}