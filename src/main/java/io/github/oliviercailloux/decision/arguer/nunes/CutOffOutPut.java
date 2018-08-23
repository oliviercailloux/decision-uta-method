package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class CutOffOutPut implements NunesOutPut {

	public List<Constraint> constraints;
	public Alternative rejected;
	public Alternative x;
	public List<Criterion> reject;
	public Map<Constraint,Double> values_constrain;
	
	public CutOffOutPut(Alternative x, Alternative y, List<Constraint> c){
		this.constraints = c;
		this.x = x;
		this.rejected = y;	
		this.values_constrain = new HashMap<>();
	}


	@Override
	public String argue() {
		
		return x.getName() +" is rejected because she doesn't satisfied the constrain on the criteria "+ Tools.showCriteria(reject);
	}


	@Override
	public Boolean isApplicable() {
		
		computeValuesConstraints();

		//checking hard constraint not respected
		
		for(Constraint c : values_constrain.keySet()) {
			if(values_constrain.get(c) == 1)
				reject.add(c.getCriterion());
		}
		
		if(!reject.isEmpty())
			return true;
		
		// checking soft constraint
		
		
		
		
		
		
		return false;
	}
	
	public boolean isSatisfied(Alternative alt, Constraint c) {
		if(c.isFlag_min()) {
			if(alt.getEvaluations().get(c.getCriterion()) > c.getTreshold())
				return false;
		}
		else {	if(alt.getEvaluations().get(c.getCriterion()) < c.getTreshold()) 
				return false;
		}
		
		return true;
	}
	
	public void computeValuesConstraints(){
		for(Constraint c : constraints) {
			if(c.isHard()) {
				if(isSatisfied(rejected, c))
					values_constrain.put(c, -1.0);
				else
					values_constrain.put(c, 1.0);
			}
			else
				values_constrain.put(c, c.getValue_pref());
		}
	} 
	

	public boolean lpv(Alternative alt, Constraint c) {
		if((isSatisfied(alt, c) && c.getValue_pref() < 0.0) || (!isSatisfied(alt, c) && c.getValue_pref() > 0.0))
			return true;
		
		return false;
	}
	
	public Constraint strongestConstraint() {
		Map<Double,Constraint> cons = new HashMap<>();
		
		for(Constraint c : constraints) {
				
			
			
		}
		
		return null;
	}
	
	
}
