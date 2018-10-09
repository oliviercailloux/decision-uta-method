package io.github.oliviercailloux.decision.model;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import static java.util.Objects.requireNonNull;


public class Criterion {

	private int id;
	private String name;

	/***
	 * 
	 * @param id  
	 * 				not <code>null</code>.
	 * @param name
	 * 				not <code>null</code>.
	 */
	public Criterion(int id, String name) {
		this.id = requireNonNull(id);
		this.name = requireNonNull(name);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Criterion other = (Criterion) obj;

		return id == other.id;
	}

	@Override
	public String toString() {
		ToStringHelper stringHelper = MoreObjects.toStringHelper(this);

		stringHelper.addValue(id);

		if (!name.equals("c" + id)) {
			stringHelper.addValue(name);
		}

		return stringHelper.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
