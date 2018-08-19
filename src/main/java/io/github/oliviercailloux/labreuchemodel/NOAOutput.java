package io.github.oliviercailloux.labreuchemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NOAOutput implements LabreucheOutput{

	public Set<Criterion> criteria;
	public Map<Criterion,Double> weights;
	public Map<Criterion,Double> weightsReferences;
	public Map<Criterion,Double> deltas;
	public List<Criterion> positiveArguments;
	public Alternative best_choice;
	public Alternative second_choice;
	public List<Criterion> C;
	public List<Criterion> ConPA;
	public List<Criterion> ConNA;
	
	
	public NOAOutput(Alternative x, Alternative y, Map<Criterion,Double> w, Map<Criterion,Double> w_ref,List<Criterion> pa, Map<Criterion,Double> d) {
		this.best_choice = x;
		this.second_choice = y;
		this.weights = w;
		this.weightsReferences = w_ref;
		this.positiveArguments = pa;
		this.criteria = w.keySet();
		this.deltas = d;
		this.C = new ArrayList<>();
		this.ConPA = new ArrayList<>();
		this.ConNA = new ArrayList<>();
	}
	
	
	@Override
	public Anchor anchor() {
		
		return null;
	}

	@Override
	public String argue() {
		String explanation = "Even though "+ this.second_choice.getName() +" is better than "+ this.best_choice.getName() +" on average, "+ this.best_choice.getName()+ 
				" is preferred to "+ this.second_choice.getName() +" since \n"+ this.best_choice.getName() + " is better than "+ this.second_choice.getName()+" on the criteria "+
				Tools.showCriteria(ConPA)+" that are important whereas \n"+ this.best_choice.getName()+" is worse than "+ this.second_choice.getName()+ " on the criteria "+
				Tools.showCriteria(ConNA)+" that are not important.";
		
		Double addSentence = 0.0;
		for(Criterion c : this.criteria) {
			if(!C.contains(c))
				addSentence += deltas.get(c) * weights.get(c);
		}
		
		if(addSentence > 0)
			explanation += "\n" + "Moroever, "+this.best_choice.getName()+" is on average better than "+this.second_choice.getName()+" on the other criteria.";
		
		return explanation;
	}

	@Override
	public Boolean isApplicable() {
		
		if(Tools.score(this.best_choice,this.weightsReferences) > Tools.score(this.second_choice,this.weightsReferences))
			return false;
				
		System.out.println("\n \n" + "Delta " + this.best_choice.getName() +" > "+this.second_choice.getName() + " : " + Tools.showVector(deltas));
				
		Map<Double,Criterion> temp = new HashMap<>(); 
					
		for(Map.Entry<Criterion, Double> w_i : this.weights.entrySet())
			temp.put( (w_i.getValue() - 1.0 /this.criteria.size()) * this.deltas.get(w_i.getKey()), w_i.getKey());
				
		ArrayList<Double> keys = new ArrayList<>(temp.keySet());
		Collections.sort(keys);
				
		Map<Criterion,Double> wcomposed = this.weightsReferences;	
		int p = keys.size() - 1;
				
		do {
			wcomposed.put(temp.get(keys.get(p)), this.weights.get((temp.get(keys.get(p)))));
			this.C.add(temp.get(keys.get(p)));
			p--;
		}
		while(Tools.score(this.best_choice,wcomposed) < Tools.score(this.second_choice,wcomposed));
				
		for(Criterion c : C){
			if(positiveArguments.contains(c))
				this.ConPA.add(c);
			else
				this.ConNA.add(c);
		}
				
		return true;	
	}
}
