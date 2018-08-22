package io.github.oliviercailloux.labreuchemodel;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGAVGOutput implements LabreucheOutput {
	
	private List<Criterion> criteria;
	private Map<Criterion,Double> weights;
	private Double epsilon;
	private Alternative best_choice;
	private Alternative second_choice;
	private Double max_w;
	private Anchor anchor;
	
	public RMGAVGOutput(AlternativeComparison alt) {
		this.best_choice = alt.getX();
		this.second_choice = alt.getY();
		this.weights = alt.getW();
		this.epsilon = 0.2 / this.criteria.size();
		this.criteria = new ArrayList<>();
		for(Criterion c : this.weights.keySet())
			this.criteria.add(c);
		this.max_w = null;
		this.anchor = Anchor.RMGAVG;	
	}

	@Override
	public String argue() {
		return this.best_choice.getName() +" is preferred to "+ this.second_choice.getName() +" since "
			 + this.best_choice.getName() +" is on average better than "+ this.second_choice.getName() + 
			"\n" + " and all the criteria have almost the same weights.";
	}

	@Override
	public Boolean isApplicable() {
		
		max_w = Double.MIN_VALUE;
		
		for(Map.Entry<Criterion,Double> c : this.weights.entrySet()) {
			Double v = Math.abs(c.getValue() - (1.0 / this.criteria.size()));	
			
			if( v > max_w)
				max_w = v;
		}
				
		if( max_w <= this.epsilon )
			return true;	
		
		return false;
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public Map<Criterion, Double> getWeights() {
		return weights;
	}

	public Double getEpsilon() {
		return epsilon;
	}

	public Alternative getBest_choice() {
		return best_choice;
	}

	public Alternative getSecond_choice() {
		return second_choice;
	}

	public Double getMax_w() {
		return max_w;
	}

	public Anchor getAnchor() {
		return anchor;
	}

}
