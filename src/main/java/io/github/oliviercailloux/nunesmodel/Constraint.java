package io.github.oliviercailloux.nunesmodel;

import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Constrain {
	
	public Criterion criterion;
	public Double treshold;
	public boolean flag_min;     //flag_min equal 0 if we want a better value than threshold on the criterion;
	
	
	public Constrain(Criterion c, Double t, boolean f) {
		this.criterion = c;
		this.treshold = t;
		this.flag_min = f;
	}
	
	
	
	@Override
	public String toString() {
		String s = "";
		
		if(flag_min)
			s += criterion.getName() +" <= "+ treshold;
		else
			s += criterion.getName() +" >= "+ treshold;
		
		return s;
	}
}
