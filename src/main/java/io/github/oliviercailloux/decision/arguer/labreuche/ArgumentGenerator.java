package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.oliviercailloux.uta_calculator.utils.NumbersGenerator;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;

public class ArgumentGenerator {
	
	Set<Alternative> alternatives;
	Map<Criterion,Double> weights;
	
	
	public ArgumentGenerator(int alternativesNumber, int criteriaNumber) {
		this.alternatives = new LinkedHashSet<>();
		this.weights = new LinkedHashMap<>();
		
		generateWeightVector(criteriaNumber);
		generateAlternatives(alternativesNumber);
	}
	
	public Map<Criterion, Double> getWeights(){
		return this.weights;
	}
	
	public Set<Alternative> getAlternatives(){
		return this.alternatives;
	}
	

	public void generateAlternatives(int alternativesNumber) {
		ProblemGenerator pg = new ProblemGenerator();
		pg.setCriteria(new ArrayList<>(weights.keySet()));
		pg.generateAlternatives(alternativesNumber);
		this.alternatives = new HashSet<>(pg.getAlternatives());
	}
	
	public void generateWeightVector(int criteriaNumber) {
		ProblemGenerator pg = new ProblemGenerator();
		pg.generateCriteria(criteriaNumber, 1, 10, 1);
		List<Criterion> criteria = pg.getCriteria();
		NumbersGenerator ng = new NumbersGenerator();
		List<Double> weightsGenerated = ng.generate(criteriaNumber, 1.0);
		
		for(int i = 0; i< criteria.size(); i++) {
			weights.put(criteria.get(i), weightsGenerated.get(i));
		}
	}
	
	public Set<Alternative> findBest(){
		Set<Alternative> bests = new LinkedHashSet<>();
		double bestCurrentScore = 0.0;
		double score = 0.0;
		
		for(Alternative alt : alternatives) {
			score = Tools.score(alt, weights);
			if(score > bestCurrentScore) {
				bests.clear();
				bestCurrentScore = score;
				bests.add(alt);
			}

			if(score == bestCurrentScore) {
				bests.add(alt);
			}
		}
		
		return bests;
	}
	

	public Alternative findUniqueBest() {
		Set<Alternative> bests = findBest();
		
		if(bests.size() == 1) {
			Iterator<Alternative> iter = bests.iterator();
			return iter.next();		
		}
		
		throw new IllegalArgumentException();
	}
	
	public LabreucheOutput compare(Alternative x, Alternative y) {
		AlternativesComparison altComp = new AlternativesComparison(x, y, weights);
		
		LabreucheModel lm = new LabreucheModel(altComp);
		
		return lm.getExplanation();
	}
	
	
	
}
