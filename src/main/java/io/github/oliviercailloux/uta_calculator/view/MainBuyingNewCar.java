package io.github.oliviercailloux.uta_calculator.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.oliviercailloux.uta_calculator.model.CriterionWithScale;
import io.github.oliviercailloux.uta_calculator.model.PartialValueFunction;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;
import io.github.oliviercailloux.uta_calculator.model.UTAAlternative;
import io.github.oliviercailloux.uta_calculator.model.UTASTAR;
import io.github.oliviercailloux.uta_calculator.model.ValueFunction;
import io.github.oliviercailloux.uta_calculator.utils.ScaleGenerator;

public class MainBuyingNewCar {

	private ValueFunction vf;
	private ProblemGenerator problem;
	public UTASTAR utastar;

	public MainBuyingNewCar() {
		problem = generateBuyingNewCar();
		utastar = new UTASTAR(problem.getCriteria(), problem.getAlternatives());
		utastar.setPrint(false);
		vf = utastar.findValueFunction();
	}

	public static void main(String[] args) {
		MainBuyingNewCar main = new MainBuyingNewCar();

		if (main.utastar.isPrint()) {
			System.out.println("Information about the problem");
			System.out.println(main.problem);

			System.out.println("Displaying Partial Value Function");
			for (PartialValueFunction pvf : main.vf.getPartialValueFunctions()) {
				System.out.println(pvf.getCriterion().getName() + " : {" + pvf.getIntervals() + "}");
			}
			System.out.println();
		}

		System.out.println("Value of each alternative :");
		Map<UTAAlternative, Double> alternativeValues = main.getAlternativeValues(main.vf,
				main.problem.getAlternatives());
		for (Entry<UTAAlternative, Double> alternativeValue : alternativeValues.entrySet()) {
			System.out.println(alternativeValue.getKey().getName() + " : " + alternativeValue.getValue());
		}

		System.out.println();
		String orderStr = "";
		Map<UTAAlternative, Double> alternativesOrdered = sortByValue(alternativeValues);
		for (Entry<UTAAlternative, Double> alternativeValue : alternativesOrdered.entrySet()) {
			if (!orderStr.isEmpty()) {
				orderStr += " > ";
			}
			orderStr += alternativeValue.getKey().getName();
		}
		System.out.println(orderStr);

	}

	private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Object>() {
			@Override
			public int compare(Object o2, Object o1) {
				return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue())
						.compareTo(((Map.Entry<K, V>) (o2)).getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public List<UTAAlternative> getMainAlternatives() {
		List<UTAAlternative> alternatives = problem.getAlternatives();
		Collections.sort(alternatives, new Comparator<UTAAlternative>() {
			@Override
			public int compare(final UTAAlternative a1, final UTAAlternative a2) {
				return ((Integer) a1.getId()).compareTo(a2.getId());
			}
		});
		return alternatives;
	}

	public List<UTAAlternative> getAlternativeSorted() {
		List<UTAAlternative> alternatives = problem.getAlternatives();
		Collections.sort(alternatives, new Comparator<UTAAlternative>() {
			@Override
			public int compare(final UTAAlternative a1, final UTAAlternative a2) {
				return ((Double) vf.getValue(a1)).compareTo(vf.getValue(a2));
			}
		});
		return alternatives;
	}

	public Map<UTAAlternative, Double> getAlternativeValues(ValueFunction valuefunction,
			List<UTAAlternative> alternatives) {
		Map<UTAAlternative, Double> result = new HashMap<>();
		for (UTAAlternative alternative : alternatives) {
			result.put(alternative, valuefunction.getValue(alternative));
		}
		return result;
	}

	public ProblemGenerator generateBuyingNewCar() {
		ScaleGenerator scaleGenerator = new ScaleGenerator();

		List<CriterionWithScale> criteria = new ArrayList<>();
		CriterionWithScale price = new CriterionWithScale(1, "price", scaleGenerator.generate(15000.0, 25000.0, 3));
		criteria.add(price);
		CriterionWithScale comfort = new CriterionWithScale(2, "comfort", scaleGenerator.generate(1.0, 4.0, 4));
		criteria.add(comfort);
		CriterionWithScale safety = new CriterionWithScale(3, "safety", scaleGenerator.generate(1.0, 5.0, 3));
		criteria.add(safety);

		List<UTAAlternative> alternatives = new ArrayList<>();
		Map<CriterionWithScale, Double> evaluation = new HashMap<>();
		evaluation.put(price, 22000.0);
		evaluation.put(comfort, 4.0);
		evaluation.put(safety, 4.0);
		UTAAlternative ns = new UTAAlternative(1, "Nissan Sentra", evaluation);
		alternatives.add(ns);

		evaluation = new HashMap<>();
		evaluation.put(price, 25000.0);
		evaluation.put(comfort, 3.0);
		evaluation.put(safety, 2.0);
		UTAAlternative c4 = new UTAAlternative(2, "Citroen C4", evaluation);
		alternatives.add(c4);

		evaluation = new HashMap<>();
		evaluation.put(price, 15000.0);
		evaluation.put(comfort, 2.0);
		evaluation.put(safety, 3.0);
		UTAAlternative p208 = new UTAAlternative(3, "Peugeot 208 GT", evaluation);
		alternatives.add(p208);

		evaluation = new HashMap<>();
		evaluation.put(price, 21500.0);
		evaluation.put(comfort, 1.0);
		evaluation.put(safety, 3.0);
		UTAAlternative p308 = new UTAAlternative(4, "Peugeot 308 Berline", evaluation);
		alternatives.add(p308);

		ProblemGenerator pb = new ProblemGenerator(criteria, alternatives);
		return pb;
	}

}