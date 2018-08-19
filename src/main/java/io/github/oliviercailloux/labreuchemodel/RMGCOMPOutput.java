package io.github.oliviercailloux.labreuchemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGCOMPOutput implements LabreucheOutput {

	private List<Criterion> criteria;
	private Map<Criterion,Double> weights;
	private List<Criterion> positiveArguments;
	private List<Criterion> negativeArguments;
	private Double epsilon;
	private Alternative best_choice;
	private Alternative second_choice;
	public Double max_w;
	
	public RMGCOMPOutput(Alternative x, Alternative y, Map<Criterion,Double> w,List<Criterion> pa, List<Criterion> na) {
		this.best_choice = x;
		this.second_choice = y;
		this.weights = w;
		this.criteria = new ArrayList<>();
		for(Criterion c : w.keySet())
			this.criteria.add(c);
		this.positiveArguments = pa;
		this.negativeArguments = na;
		this.max_w = null;
		
	}
	
	
	@Override
	public Anchor anchor() {
		
		return null;
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

	
}
