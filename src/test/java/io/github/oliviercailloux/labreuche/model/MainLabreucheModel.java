package io.github.oliviercailloux.labreuche.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.LabreucheModel;

public class MainLabreucheModel {

	public static void main(String[] args) {
		
		//example  5 for NOA  ->  validate!
		/*
		Double[] w = {0.41 , 0.06, 0.24, 0.29};
		Double[] x = {0.42 , 0.66, 0.66, 0.57};
		Double[] y = {0.54 , 0.04, 0.89, 0.76};
		*/
			
		//Double[] y = {1.0 , 1.0, 1.0, 1.0};    //  -> for checking the anchor ALL  ->  validate!
		
		//example 6 for NOA   -> validate!
		/*
		Double[] w = {0.18 , 0.11, 0.12, 0.24, 0.35};
		Double[] x = {0.95 , 0.67, 0.64, 0.27, 0.39};
		Double[] y = {0.30 , 0.37, 0.41, 0.94, 0.49};	
		*/
		
		// example 9 for IVT
		
		/*
		Double[] w = {0.06, 0.11, 0.21, 0.29, 0.33};
		Double[] x = {0.89, 0.03, 0.07, 0.32, 0.38};
		Double[] y = {0.36, 0.76, 0.60, 0.25, 0.75};
		*/
		
		// example 10 for IVT
		/*
		Double[] w = {0.13 , 0.04, 0.12, 0.10, 0.07, 0.19, 0.15, 0.03, 0.01, 0.16};
		Double[] x = {0.61 , 0.28, 0.08, 0.02, 0.81, 0.15, 0.16, 0.38, 0.24, 0.75};
		Double[] y = {0.45 , 0.64, 0.86, 0.76, 0.87, 0.54, 0.17, 0.04, 0.55, 0.05};
		*/
		
		
		//Section 8.4 of the paper
		
		Double[] x = {0.50 , 0.06, 0.03, 0.95, 0.87, 0.20, 0.95};
		Double[] y = {0.99 , 0.35, 0.31, 0.51, 0.62, 0.57, 0.52};
		
		
		//Double[] w = {0.06 , 0.11, 0.19, 0.11, 0.31, 0.08, 0.14}; // example 13   NOA  ->  validate!
		//Double[] w = {0.14 , 0.05, 0.17, 0.23, 0.17, 0.11, 0.13}; // example 14   NOA	-> validate!
		//Double[] w = {0.11 , 0.14, 0.13, 0.02, 0.27, 0.25, 0.08}; // example 15   IVT   -> validate!
		Double[] w = {0.24 , 0.20, 0.25, 0.06, 0.02, 0.19, 0.04}; // example 16   IVT -> validate!
		//Double[] w = {0.16 , 0.14, 0.15, 0.10, 0.16, 0.15, 0.14}; // example 17   RMG   -> validate!
		//Double[] w = {0.12 , 0.16, 0.15, 0.16, 0.15, 0.14, 0.12}; // example 18   RMG  -> with epsilon too small we don't have the same result
		
		
		// criteria building
		List<Criterion> criteria = new ArrayList<Criterion>();
		
		for(int i=0; i< w.length; i++)
			criteria.add(new Criterion(i+1,"c"+(i+1),new ArrayList<Double>()));
		
		// alternatives building
		List<Alternative> alts = new ArrayList<Alternative>();
		Map<Criterion,Double> perf1 = new LinkedHashMap<Criterion,Double>();
		Map<Criterion,Double> perf2 = new LinkedHashMap<Criterion,Double>();
		//Map<Criterion,Double> perf3 = new LinkedHashMap<Criterion,Double>();
		
		for(int i=0; i<x.length;i++) {
			perf1.put(criteria.get(i),x[i]);
			perf2.put(criteria.get(i),y[i]);
			//perf3.put(criteria.get(i),z[i]);
		}
		
		Alternative hixe = new Alternative(1,"X",perf1);			alts.add(hixe);
		Alternative igrek = new Alternative(2,"Y",perf2); 		alts.add(igrek);
		//Alternative zede = new Alternative(3,"Z",perf3);			alts.add(zede);
		
		//weights vector building
		Map<Criterion, Double> weights = new LinkedHashMap<Criterion,Double>();
		for(int i = 0; i<criteria.size();i++)
			weights.put(criteria.get(i), w[i]);
		
		
		LabreucheModel lm = new LabreucheModel(criteria,alts,weights);
				
		
		lm.resolved();
		
		/*
		lm.buildDelta(igrek, hixe);
		
		
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(criteria.get(0));
		crit.add(criteria.get(2));
		crit.add(criteria.get(4));
		
		System.out.println(lm.showCriteria(crit));
		
		System.out.println("D_eu total :");
		System.out.println(lm.d_eu(crit,5));
		
		System.out.println("pi_best" + lm.pi_min(crit));
		
		System.out.println("D_eu first part "+ lm.d_eu(crit,0) + " min part : " + lm.d_eu(crit, 1));
		
		System.out.println("V(x,y) = "+ (lm.score(igrek, weights) - lm.score(hixe,weights)));
		
		System.out.println();
		
		System.out.println(lm.showCriteria(lm.pi_min(crit)));
		*/
		
	}

}
