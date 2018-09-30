package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Table;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.Model;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.nunes.NunesTools;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;
import io.github.oliviercailloux.uta_calculator.utils.NumbersGenerator;

public class ArgumentGenerator {

	private Set<Alternative> alternatives;
	private Map<Criterion, Double> weights;
	private ProblemGenerator problemGenerator;
	private LabreucheModel model;

	public ArgumentGenerator(Set<Alternative> alternatives, LabreucheModel model) {
		this.alternatives = alternatives;
		this.weights = model.getWeights();
		this.model = model;
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

	public void setAlternatives(Set<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	public Set<Alternative> getAlternatives() {
		return this.alternatives;
	}

	public void generateAlternatives(int alternativesNumber) {
		problemGenerator.setCriteria(new ArrayList<>(weights.keySet()));
		problemGenerator.generateAlternatives(alternativesNumber);
		this.alternatives = new LinkedHashSet<>(problemGenerator.getAlternatives());
	}

	public void generateWeightVector(int criteriaNumber) {
		problemGenerator.generateCriteria(criteriaNumber, 0, 10, 2);
		List<Criterion> criteria = problemGenerator.getCriteria();
		NumbersGenerator ng = new NumbersGenerator();
		List<Double> weightsGenerated = ng.generate(criteriaNumber, 1.0);

		for (int i = 0; i < criteria.size(); i++) {
			weights.put(criteria.get(i), weightsGenerated.get(i));
		}
	}

	public Set<Alternative> findBest() {
		Set<Alternative> bests = new LinkedHashSet<>();
		double bestCurrentScore = 0.0;
		double score;

		if(model != null) {
			
			for (Alternative alt : alternatives) {
				score = LabreucheTools.score(alt, model.getWeights());
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

		if(model != null) {
			
			Map<Alternative, Integer> scoreboard = new LinkedHashMap<>();
			Table<Alternative, Alternative, Double> tradeoffs = NunesTools.computeTO(alternatives, weights);
			double scoreA;
			double scoreB;
			int scoreTMP;

			for (Alternative a : alternatives) {
				scoreTMP = 0;
				for (Alternative b : alternatives) {
					if (!a.equals(b)) {
						scoreA = NunesTools.score(a, b, weights.keySet(), weights, tradeoffs);
						scoreB = NunesTools.score(b, a, weights.keySet(), weights, tradeoffs);
						if (scoreA < scoreB) {
							scoreTMP++;
						}
					}
				}
				scoreboard.put(a, scoreTMP);
			}

			List<Integer> listScoreSorted = new ArrayList<>(scoreboard.values());
			Collections.sort(listScoreSorted);
			int bestScore = listScoreSorted.get(0);

			for (Entry<Alternative, Integer> entry : scoreboard.entrySet()) {
				if (entry.getValue() == bestScore) {
					bests.add(entry.getKey());
				}
			}

			return bests;

		}
		
		throw new IllegalStateException();
	}

	public Alternative findUniqueBest() {
		Set<Alternative> bests = findBest();

		if (bests.size() == 1) {
			Iterator<Alternative> iter = bests.iterator();
			return iter.next();
		}

		throw new IllegalArgumentException();
	}

	public LabreucheOutput compare(Alternative x, Alternative y) {
		LabreucheModel lm = new LabreucheModel(getWeights());

		AlternativesComparison<LabreucheModel> altComp = new AlternativesComparison<>(x, y, lm);

		LabreucheComputer lc = new LabreucheComputer(altComp);

		return lc.getExplanation();
	}

}
