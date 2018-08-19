package io.github.oliviercailloux.labreuchemodel;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGAVGOutput implements LabreucheOutput {
	
	private List<Criterion> criteria;
	private Map<Criterion,Double> weights;
	private List<Criterion> positiveArguments;
	private List<Criterion> negativeArguments;
	private Double epsilon;
	private Alternative best_choice;
	private Alternative second_choice;
	public Double max_w;
	
	public RMGAVGOutput(Alternative x, Alternative y, Map<Criterion,Double> w) {
		this.best_choice = x;
		this.second_choice = y;
		this.weights = w;
		this.criteria = new ArrayList<>();
		for(Criterion c : w.keySet())
			this.criteria.add(c);
		this.max_w = null;
		
	}

	@Override
	public Anchor anchor() {
		
		return null;
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

}
