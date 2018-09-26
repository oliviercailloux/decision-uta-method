package io.github.oliviercailloux.uta_calculator.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScaleGenerator {

	// Constructors
	public ScaleGenerator() {
	}

	// Methods
	public List<Double> generate(double minValue, double maxValue, int cuts) {

		List<Double> scale = new ArrayList<>();

		double subrangeLength = (maxValue - minValue) / (cuts - 1);
		double currentStart = minValue;

		for (int i = 0; i < cuts; ++i) {
			if (i == 0) {
				scale.add(minValue);
			} else if (i == (cuts - 1)) {
				scale.add(maxValue);
			} else {
				scale.add(currentStart);
			}
			currentStart += subrangeLength;
		}

		Collections.sort(scale);
		return scale;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		double minValue;
		double maxValue;
		int cuts;

		if (args.length == 0) {
			minValue = 10.0;
			maxValue = 20.0;
			cuts = 4;
		} else if (args.length == 3) {
			try {
				minValue = Double.parseDouble(args[0]);
			} catch (NumberFormatException e) {
				throw new Exception(args[0] + " is not an double. This should be the minValue");
			}
			try {
				maxValue = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				throw new Exception(args[1] + " is not a double. This should be the maxValue");
			}
			try {
				cuts = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				throw new Exception(args[2] + " is not an integer. This should be the number of cuts");
			}

		} else {
			throw new Exception("Please insert 0 or 3 arguments");
		}

		ScaleGenerator scaleGenerator = new ScaleGenerator();

		String result = "Generating a scale of " + cuts + " cuts between [" + minValue + "," + maxValue + "] : "
				+ scaleGenerator.generate(minValue, maxValue, cuts);
		System.out.println(result);
	}

}