package io.github.oliviercailloux.uta_calculator.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {

	// Attributes
	private DescriptiveStatistics stats;
	private KendallsCorrelation kendallsCorrelation;
	private static final Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

	// Constructors
	public Statistics() {
		stats = new DescriptiveStatistics();
		kendallsCorrelation = new KendallsCorrelation();
	}

	// Method
	public double getMean(List<Double> input) {
		setStat(input);
		return stats.getMean();
	}

	public double getStd(List<Double> input) {
		setStat(input);
		return stats.getStandardDeviation();
	}

	public double getKendalTau(List<Double> input1, List<Double> input2) {
		kendallsCorrelation = new KendallsCorrelation();
		return kendallsCorrelation.correlation(convertToArray(input1), convertToArray(input2));
	}

	private void setStat(List<Double> input) {
		stats = new DescriptiveStatistics();
		for (int i = 0; i < input.size(); i++) {
			stats.addValue(input.get(i));
		}
	}

	private double[] convertToArray(List<Double> arraylist) {
		double[] array = new double[arraylist.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = arraylist.get(i);
		}
		return array;
	}

	public static void main(String[] args) {
		Statistics statistics = new Statistics();
		List<Double> list = new ArrayList<>();
		for (double i = 0; i < 10; i++) {
			list.add(i);
		}
		LOGGER.info("List: " + list);
		LOGGER.info("Mean: " + statistics.getMean(list));
		LOGGER.info("Std: " + statistics.getStd(list));
	}

}