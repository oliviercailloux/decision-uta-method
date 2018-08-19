package io.github.oliviercailloux.labreuchemodel;

import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class AllOutput implements LabreucheOutput {
	
	public Alternative best_choice;
	public Alternative second_choice;
	public Map<Criterion,Double> deltas;

	
	public AllOutput(Alternative x, Alternative y, Map<Criterion,Double> d ) {
		this.best_choice = x;
		this.second_choice = y;
		this.deltas = d;
	}
	
	@Override
	public Anchor anchor() {
		
		return null;
	}

	@Override
	public String argue() {
		
		return this.best_choice.getName()+" is preferred to "+this.second_choice.getName()+" since "
			  +this.best_choice.getName()+" is better then " +this.second_choice.getName()+" on ALL criteria.";
	}

	@Override
	public Boolean isApplicable() {
		
		for(Map.Entry<Criterion,Double> d : deltas.entrySet()){
			if(d.getValue() < 0.0)
				return false;
		}
		
		return true;
	}

	
}
