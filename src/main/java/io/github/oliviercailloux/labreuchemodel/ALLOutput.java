package io.github.oliviercailloux.labreuchemodel;

import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class ALLOutput implements LabreucheOutput {
	
	private Alternative best_choice;
	private Alternative second_choice;
	private Map<Criterion,Double> deltas;
	private Anchor anchor;

	
	public ALLOutput(AlternativeComparison alt) {
		this.best_choice = alt.getX();
		this.second_choice = alt.getX();
		this.deltas = alt.getDelta();
		this.anchor = Anchor.ALL;
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




	public Alternative getBest_choice() {
		return best_choice;
	}




	public Alternative getSecond_choice() {
		return second_choice;
	}




	public Map<Criterion, Double> getDeltas() {
		return deltas;
	}


	public Anchor getAnchor() {
		return anchor;
	}

	
}
