package io.github.oliviercailloux.decision;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Utils {

	public static String showVector(Collection<Double> collect) {
		String show2 = "( ";

		for (Double c : collect) {
			Double c_scaled = BigDecimal.valueOf(c).setScale(3, RoundingMode.HALF_UP).doubleValue();
			show2 += c_scaled + " ";
		}
		show2 += " )";

		return show2;
	}

	public static String showCriteria(Collection<Criterion> collect) {
		String show = "{ ";

		for (Criterion c : collect) {
			show += c.getName() + " ";
		}

		return show + " }";
	}

	public static String showSet(List<List<Criterion>> big_a) {
		if (big_a == null) {
			return "{ / }";
		}

		String str = "{ ";

		for (Collection<Criterion> l : big_a)
			str += showCriteria(l) + " ";

		str += " }";

		return str;
	}

}
