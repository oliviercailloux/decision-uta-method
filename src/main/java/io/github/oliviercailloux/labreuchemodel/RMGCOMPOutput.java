package io.github.oliviercailloux.labreuchemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGCOMPOutput implements LabreucheOutput {

	private List<Criterion> criteria;
	private Map<Criterion,Double> weights;
	private Set<Criterion> positiveArguments;
	private Set<Criterion> negativeArguments;
	private Double epsilon;
	private Alternative best_choice;
	private Alternative second_choice;
	private Double max_w;
	private Anchor anchor;
	
	public RMGCOMPOutput(AlternativeComparison alt) {
		this.best_choice = alt.getX();
		this.second_choice = alt.getY();
		this.weights = alt.getW();
		this.criteria = new ArrayList<>();
		for(Criterion c : this.weights.keySet())
			this.criteria.add(c);
		this.positiveArguments = alt.getCriteriaInFavor();
		this.negativeArguments = alt.getCriteriaAgainst();
		this.max_w = null;
		this.anchor = Anchor.RMGCOMP;
	}

	@Override
	public String argue() {
		if( max_w > this.epsilon ) {
			if( max_w <= this.epsilon * 2) {
				return this.best_choice.getName() +" is preferred to "+ this.second_choice.getName() +" since the intensity of the preference "+ 
			           this.best_choice.getName() +" over "+ this.second_choice.getName() +" on \n"+ Tools.showCriteria(this.positiveArguments) +
			           " is significantly larger than the intensity of "+ this.second_choice.getName() +" over "+ this.best_choice.getName() +
			           " on \n"+ Tools.showCriteria(this.negativeArguments) + ", and all the criteria have more or less the same weights.";
			}
		}	
		
		return this.best_choice.getName() +" is preferred to "+ this.second_choice.getName() +" since the intensity of the preference "+ 
			   this.best_choice.getName() +" over "+ this.best_choice.getName() +" on "+ Tools.showCriteria(this.positiveArguments) + 
			   " is much larger than the intensity of "+ this.second_choice.getName() +" over "+ this.best_choice.getName() +" on "+ 
			   Tools.showCriteria(this.negativeArguments) +".";
	}

	@Override
	public Boolean isApplicable() {
		max_w = Double.MIN_VALUE;
		
		for(Map.Entry<Criterion,Double> c : this.weights.entrySet()) {
			Double v = Math.abs(c.getValue() - (1.0 / this.criteria.size()));	
			
			if( v > max_w)
				max_w = v;
		}
		
		if( max_w > this.epsilon )
			return true;
			
		return false;		
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public Map<Criterion, Double> getWeights() {
		return weights;
	}

	public Set<Criterion> getPositiveArguments() {
		return positiveArguments;
	}

	public Set<Criterion> getNegativeArguments() {
		return negativeArguments;
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
