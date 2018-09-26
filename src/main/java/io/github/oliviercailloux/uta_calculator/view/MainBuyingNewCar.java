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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.PartialValueFunction;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;
import io.github.oliviercailloux.uta_calculator.model.UTASTAR;
import io.github.oliviercailloux.uta_calculator.model.ValueFunction;
import io.github.oliviercailloux.uta_calculator.utils.ScaleGenerator;

public class MainBuyingNewCar {

	private ValueFunction vf;
	private ProblemGenerator problem;
	public UTASTAR utastar;
	private static final Logger LOGGER = LoggerFactory.getLogger(MainBuyingNewCar.class);

	public MainBuyingNewCar() {
		problem = generateBuyingNewCar();
		utastar = new UTASTAR(problem.getCriteria(), problem.getAlternatives());
		utastar.setPrint(false);
		vf = utastar.findValueFunction();
	}

	public static void main(String[] args) {
		MainBuyingNewCar main = new MainBuyingNewCar();

		if (main.utastar.isPrint()) {
			LOGGER.info("Information about the problem");
			LOGGER.info(main.problem.toString());

			LOGGER.info("Displaying Partial Value Function");
			for (PartialValueFunction pvf : main.vf.getPartialValueFunctions()) {
				LOGGER.info(pvf.getCriterion().getName() + " : {" + pvf.getIntervals() + "}");
			}
			LOGGER.info("");
		}

		LOGGER.info("Value of each alternative :");
		Map<Alternative, Double> alternativeValues = main.getAlternativeValues(main.vf, main.problem.getAlternatives());
		for (Entry<Alternative, Double> alternativeValue : alternativeValues.entrySet()) {
			LOGGER.info(alternativeValue.getKey().getName() + " : " + alternativeValue.getValue());
		}

		LOGGER.info("");
		StringBuilder orderStr = new StringBuilder();
		Map<Alternative, Double> alternativesOrdered = sortByValue(alternativeValues);
		for (Entry<Alternative, Double> alternativeValue : alternativesOrdered.entrySet()) {
			if (orderStr.length() > 0) {
				orderStr.append(" > ");
			}
			orderStr.append(alternativeValue.getKey().getName());
		}
		LOGGER.info(orderStr.toString());

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

	public List<Alternative> getMainAlternatives() {
		List<Alternative> alternatives = problem.getAlternatives();
		Collections.sort(alternatives, new Comparator<Alternative>() {
			@Override
			public int compare(final Alternative a1, final Alternative a2) {
				return ((Integer) a1.getId()).compareTo(a2.getId());
			}
		});
		return alternatives;
	}

	public List<Alternative> getAlternativeSorted() {
		List<Alternative> alternatives = problem.getAlternatives();
		Collections.sort(alternatives, new Comparator<Alternative>() {
			@Override
			public int compare(final Alternative a1, final Alternative a2) {
				return ((Double) vf.getValue(a1)).compareTo(vf.getValue(a2));
			}
		});
		return alternatives;
	}

	public Map<Alternative, Double> getAlternativeValues(ValueFunction valuefunction, List<Alternative> alternatives) {
		Map<Alternative, Double> result = new HashMap<>();
		for (Alternative alternative : alternatives) {
			result.put(alternative, valuefunction.getValue(alternative));
		}
		return result;
	}

	public ProblemGenerator generateBuyingNewCar() {
		ScaleGenerator scaleGenerator = new ScaleGenerator();

		List<Criterion> criteria = new ArrayList<>();
		Criterion price = new Criterion(1, "price", scaleGenerator.generate(15000.0, 25000.0, 3));
		criteria.add(price);
		Criterion comfort = new Criterion(2, "comfort", scaleGenerator.generate(1.0, 4.0, 4));
		criteria.add(comfort);
		Criterion safety = new Criterion(3, "safety", scaleGenerator.generate(1.0, 5.0, 3));
		criteria.add(safety);

		List<Alternative> alternatives = new ArrayList<>();
		Map<Criterion, Double> evaluation = new HashMap<>();
		evaluation.put(price, 22000.0);
		evaluation.put(comfort, 4.0);
		evaluation.put(safety, 4.0);
		Alternative ns = new Alternative(1, "Nissan Sentra", evaluation);
		alternatives.add(ns);

		evaluation = new HashMap<>();
		evaluation.put(price, 25000.0);
		evaluation.put(comfort, 3.0);
		evaluation.put(safety, 2.0);
		Alternative c4 = new Alternative(2, "Citroen C4", evaluation);
		alternatives.add(c4);

		evaluation = new HashMap<>();
		evaluation.put(price, 15000.0);
		evaluation.put(comfort, 2.0);
		evaluation.put(safety, 3.0);
		Alternative p208 = new Alternative(3, "Peugeot 208 GT", evaluation);
		alternatives.add(p208);

		evaluation = new HashMap<>();
		evaluation.put(price, 21500.0);
		evaluation.put(comfort, 1.0);
		evaluation.put(safety, 3.0);
		Alternative p308 = new Alternative(4, "Peugeot 308 Berline", evaluation);
		alternatives.add(p308);

		return new ProblemGenerator(criteria, alternatives);
	}

}