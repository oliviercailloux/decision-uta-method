package io.github.oliviercailloux.decision.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProblemGenerator {

	// Attributes
	private Random random;
	private List<Criterion> criteria;
	private List<EvaluatedAlternative> alternatives;

	// Constructors
	public ProblemGenerator(List<Criterion> criteria, List<EvaluatedAlternative> alternatives) {
		this.criteria = criteria;
		this.alternatives = alternatives;
		random = new Random();
	}

	public ProblemGenerator() {
		this.criteria = new ArrayList<>();
		this.alternatives = new ArrayList<>();
		random = new Random();
	}

	// Getters and Setters
	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criterion> criteria) {
		this.criteria = criteria;
	}

	public List<EvaluatedAlternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<EvaluatedAlternative> alternatives) {
		this.alternatives = alternatives;
	}

	// Methods
	public void generateCriteria(int number) {
		for (int i = 0; i < number; i++) {
			int id = i + 1;
			Criterion criterion = new Criterion(id, "c" + id);
			criteria.add(criterion);
		}
	}

	public void generateAlternatives(int number) {
		for (int i = 0; i < number; i++) {
			int id = i + 1;
			Map<Criterion, Double> evaluations = new HashMap<>();
			for (Criterion criterion : criteria) {
				double randomValue = random.nextDouble();
				evaluations.put(criterion, randomValue);
			}
			EvaluatedAlternative alternative = new EvaluatedAlternative(id, "a" + id, evaluations);
			alternatives.add(alternative);
		}
	}

	@Override
	public String toString() {
		String result = "";
		result += "Criteria : \n";

		for (int i = 0; i < criteria.size(); i++) {
			result += criteria.get(i).getName() + " \n";
		}

		result += "\nAlternatives : \n";
		for (EvaluatedAlternative alternative : alternatives) {
			result += alternative.getName() + " --> ";
			for (int i = 0; i < criteria.size(); i++) {
				result += alternative.getEvaluations().get(criteria.get(i)) + " ";
			}
			result += "\n";
		}

		return result;
	}

}
