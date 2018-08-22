package io.github.oliviercailloux.labreuchemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class AlternativeComparison {

	private Alternative x;
	private Alternative y;
	private Map<Criterion,Double> w;
	
	public AlternativeComparison(Alternative x, Alternative y, Map<Criterion,Double> w){
		this.x = x;
		this.y = y;
		this.w = w;
	}
	
	
	public Alternative getX() {
		return this.x;
	}
	
	public Alternative getY() {
		return this.y;
	}
	
	public Map<Criterion,Double> getW(){
		return this.w;
	}
	
	
	public Map<Criterion,Double> getDelta() {
		Map<Criterion,Double> res = new HashMap<>();
		
		for(Criterion c : w.keySet())
			res.put(c,x.getEvaluations().get(c) - y.getEvaluations().get(c));
		
		return res;
	}
	
	public Set<Criterion> getCriteriaInFavor(){
		Set<Criterion> res = new HashSet<>();
		Map<Criterion,Double> eval_x = this.x.getEvaluations();
		Map<Criterion,Double> eval_y = this.y.getEvaluations();
		
		for(Criterion c : w.keySet()) {
			if(eval_x.get(c) > eval_y.get(c))
				res.add(c);
		}
		return res;
	}
	
	public Set<Criterion> getCriteriaAgainst(){
		Set<Criterion> res = new HashSet<>();
		Map<Criterion,Double> eval_x = this.x.getEvaluations();
		Map<Criterion,Double> eval_y = this.y.getEvaluations();
		
		for(Criterion c : w.keySet()) {
			if(eval_x.get(c) < eval_y.get(c))
				res.add(c);
		}
		return res;
	}
	
	
}
