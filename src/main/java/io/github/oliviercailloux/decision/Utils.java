package io.github.oliviercailloux.decision;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import io.github.oliviercailloux.decision.model.Criterion;

public class Utils {

	private Utils() {
		throw new IllegalStateException();
	}

	public static String showVector(Collection<Double> collect) {
		StringBuilder bld = new StringBuilder();

		bld.append("( ");

		for (Double c : collect) {
			Double cScaled = BigDecimal.valueOf(c).setScale(3, RoundingMode.HALF_UP).doubleValue();
			bld.append(cScaled + " ");
		}
		bld.append(")");

		return bld.toString();
	}

	public static String showCriteria(Collection<Criterion> collect) {
		StringBuilder bld = new StringBuilder();

		bld.append("{ ");

		for (Criterion c : collect) {
			bld.append(c.getName() + " ");
		}

		bld.append("}");

		return bld.toString();
	}

	public static String showSet(List<List<Criterion>> collections) {
		if (collections == null) {
			return "{ / }";
		}

		StringBuilder bld = new StringBuilder();

		bld.append("{ ");

		for (Collection<Criterion> l : collections) {
			bld.append(showCriteria(l) + " ");
		}

		bld.append("}");

		return bld.toString();
	}

}
