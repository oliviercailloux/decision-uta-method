package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;
import io.github.oliviercailloux.decision.model.ProblemGenerator;
import io.github.oliviercailloux.uta_calculator.utils.NumbersGenerator;

public class ArgumentGenerator {

	private Set<EvaluatedAlternative> alternatives;
	private Map<Criterion, Double> weights;
	private ProblemGenerator problemGenerator;

	public ArgumentGenerator(Set<EvaluatedAlternative> alternatives, LabreucheModel model) {
		this.alternatives = alternatives;
		this.weights = model.getWeights();
		this.problemGenerator = new ProblemGenerator(new ArrayList<>(weights.keySet()), new ArrayList<>(alternatives));
	}

	public ArgumentGenerator(int alternativesNumber, int criteriaNumber) {
		this.problemGenerator = new ProblemGenerator();
		this.alternatives = new LinkedHashSet<>();
		this.weights = new LinkedHashMap<>();

		generateWeightVector(criteriaNumber);
		generateAlternatives(alternativesNumber);
	}

	public Map<Criterion, Double> getWeights() {
		return this.weights;
	}

	public void setAlternatives(Set<EvaluatedAlternative> alternatives) {
		this.alternatives = alternatives;
	}

	public Set<EvaluatedAlternative> getAlternatives() {
		return this.alternatives;
	}

	public void generateAlternatives(int alternativesNumber) {
		problemGenerator.setCriteria(new ArrayList<>(weights.keySet()));
		problemGenerator.generateAlternatives(alternativesNumber);
		this.alternatives = new LinkedHashSet<>(problemGenerator.getAlternatives());
	}

	public void generateWeightVector(int criteriaNumber) {
		problemGenerator.generateCriteria(criteriaNumber);
		List<Criterion> criteria = problemGenerator.getCriteria();
		NumbersGenerator ng = new NumbersGenerator();
		List<Double> weightsGenerated = ng.generate(criteriaNumber, 1.0);

		for (int i = 0; i < criteria.size(); i++) {
			weights.put(criteria.get(i), weightsGenerated.get(i));
		}
	}

	public Set<EvaluatedAlternative> findBest() {
		Set<EvaluatedAlternative> bests = new LinkedHashSet<>();
		double bestCurrentScore = 0.0;
		double score;

		for (EvaluatedAlternative alt : alternatives) {
			score = LabreucheTools.score(alt, getWeights());
			if (score > bestCurrentScore) {
				bests.clear();
				bestCurrentScore = score;
				bests.add(alt);
			}

			if (score == bestCurrentScore) {
				bests.add(alt);
			}
		}

		return bests;
	}

	public EvaluatedAlternative findUniqueBest() {
		Set<EvaluatedAlternative> bests = findBest();

		if (bests.size() == 1) {
			Iterator<EvaluatedAlternative> iter = bests.iterator();
			return iter.next();
		}

		throw new IllegalArgumentException();
	}

	public LabreucheOutput compare(EvaluatedAlternative x, EvaluatedAlternative y) {
		LabreucheModel lm = new LabreucheModel(getWeights());

		AlternativesComparison<LabreucheModel> altComp = new AlternativesComparison<>(x, y, lm);

		LabreucheComputer lc = new LabreucheComputer(altComp);

		return lc.getExplanation();
	}

}
