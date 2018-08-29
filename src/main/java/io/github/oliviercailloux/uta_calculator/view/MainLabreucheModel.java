package io.github.oliviercailloux.uta_calculator.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class MainLabreucheModel {

	public LabreucheModel lm;

	public MainLabreucheModel() {
		this.lm = generateProblems(5);
	}

	public static void main(String[] args) {
		MainLabreucheModel main = new MainLabreucheModel();

		System.out.println("Starting problmes!");

		main.lm.getExplanation();
		
		System.out.println(main.lm.arguer());
		
		// main.lm5;
		// main.lm6;
		// main.lm9;
		// main.lm10;
		// main.lm13;
		// main.lm14;
		// main.lm15;
		// main.lm16;
		// main.lm17;
		// main.lm18;
	}

	public LabreucheModel generateProblems(int example) {
		LabreucheModel labmod = null;
		
		if(example == 5) {
			// example 5 for NOA -> validate!
			Double[] w5 = { 0.41, 0.06, 0.24, 0.29 };
			Double[] x5 = { 0.42, 0.66, 0.66, 0.57 };
			Double[] y5 = { 0.54, 0.04, 0.89, 0.76 };

			Set<Criterion> criteriaEx5 = new LinkedHashSet<>();

			for (int i = 0; i < w5.length; i++)
				criteriaEx5.add(new Criterion(i + 1, "c" + (i + 1), new ArrayList<Double>()));

			Map<Criterion, Double> x_perfEx5 = new LinkedHashMap<>();
			Map<Criterion, Double> y_perfEx5 = new LinkedHashMap<>();
			Map<Criterion, Double> weightsEx5 = new LinkedHashMap<>();

			int i = 0;
			for (Criterion c : criteriaEx5) {
				x_perfEx5.put(c, x5[i]);
				y_perfEx5.put(c, y5[i]);
				weightsEx5.put(c, w5[i]);
				i++;
			}

			Alternative x_5 = new Alternative(1, "X", x_perfEx5);
			Alternative y_5 = new Alternative(2, "Y", y_perfEx5);
			AlternativesComparison alts = new AlternativesComparison(x_5, y_5, weightsEx5);
		
			labmod = new LabreucheModel(alts);
		}
		/*
		 * else { //example 6 for NOA -> validate!
		 * Double[] w6 = {0.18, 0.11, 0.12, 0.24, 0.35}; 
		 * Double[] x6 = {0.95, 0.67, 0.64, 0.27, 0.39}; 
		 * Double[] y6 = {0.30, 0.37, 0.41, 0.94, 0.49};
		 * 
		 * // example 9 for IVT Double[] w9 = {0.06, 0.11, 0.21, 0.29, 0.33}; Double[]
		 * x9 = {0.89, 0.03, 0.07, 0.32, 0.38}; Double[] y9 = {0.36, 0.76, 0.60, 0.25,
		 * 0.75};
		 * 
		 * // example 10 for IVT Double[] w10 = {0.13, 0.04, 0.12, 0.10, 0.07, 0.19,
		 * 0.15, 0.03, 0.01, 0.16}; Double[] x10 = {0.61, 0.28, 0.08, 0.02, 0.81, 0.15,
		 * 0.16, 0.38, 0.24, 0.75}; Double[] y10 = {0.45, 0.64, 0.86, 0.76, 0.87, 0.54,
		 * 0.17, 0.04, 0.55, 0.05};
		 * 
		 * //Section 8.4 of the paper Double[] x = {0.50 , 0.06, 0.03, 0.95, 0.87, 0.20,
		 * 0.95}; Double[] y = {0.99 , 0.35, 0.31, 0.51, 0.62, 0.57, 0.52};
		 * 
		 * Double[] w13 = {0.06 , 0.11, 0.19, 0.11, 0.31, 0.08, 0.14}; // example 13 NOA
		 * -> valid! Double[] w14 = {0.14 , 0.05, 0.17, 0.23, 0.17, 0.11, 0.13}; //
		 * example 14 NOA -> valid! Double[] w15 = {0.11 , 0.14, 0.13, 0.02, 0.27, 0.25,
		 * 0.08}; // example 15 IVT -> valid! Double[] w16 = {0.24 , 0.20, 0.25, 0.06,
		 * 0.02, 0.19, 0.04}; // example 16 IVT -> valid! Double[] w17 = {0.16 , 0.14,
		 * 0.15, 0.10, 0.16, 0.15, 0.14}; // example 17 RMG -> valid! Double[] w18 =
		 * {0.12 , 0.16, 0.15, 0.16, 0.15, 0.14, 0.12}; // example 18 RMG -> with
		 * epsilon too small we don't have the same result
		 * 
		 * // criteria vectors building List<Criterion> criteriaEx6 = new ArrayList<>();
		 * List<Criterion> criteriaEx9 = new ArrayList<>(); List<Criterion> criteriaEx10
		 * = new ArrayList<>(); List<Criterion> criteriaEx13 = new ArrayList<>();
		 * List<Criterion> criteriaEx14 = new ArrayList<>(); List<Criterion>
		 * criteriaEx15 = new ArrayList<>(); List<Criterion> criteriaEx16 = new
		 * ArrayList<>(); List<Criterion> criteriaEx17 = new ArrayList<>();
		 * List<Criterion> criteriaEx18 = new ArrayList<>();
		 * 
		 * 
		 * for(int i=0; i< w6.length; i++) { criteriaEx6.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); criteriaEx9.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); }
		 * 
		 * for(int i=0; i< w10.length; i++) criteriaEx10.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>()));
		 * 
		 * for(int i=0; i< w13.length; i++) { criteriaEx13.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); criteriaEx14.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); criteriaEx15.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); criteriaEx16.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); criteriaEx17.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); criteriaEx18.add(new
		 * Criterion(i+1,"c"+(i+1),new ArrayList<Double>())); }
		 * 
		 * // alternatives building Map<Criterion,Double> x_perfEx6 = new
		 * LinkedHashMap<>(); Map<Criterion,Double> x_perfEx9 = new LinkedHashMap<>();
		 * Map<Criterion,Double> x_perfEx10 = new LinkedHashMap<>();
		 * Map<Criterion,Double> x_perf = new LinkedHashMap<>();
		 * 
		 * Map<Criterion,Double> y_perfEx6 = new LinkedHashMap<>();
		 * Map<Criterion,Double> y_perfEx9 = new LinkedHashMap<>();
		 * Map<Criterion,Double> y_perfEx10 = new LinkedHashMap<>();
		 * Map<Criterion,Double> y_perf = new LinkedHashMap<>();
		 * 
		 * 
		 * 
		 * 
		 * for(int i=0; i<x6.length;i++) { x_perfEx6.put(criteriaEx6.get(i),x6[i]);
		 * y_perfEx6.put(criteriaEx6.get(i),y6[i]);
		 * 
		 * x_perfEx9.put(criteriaEx9.get(i),y9[i]);
		 * y_perfEx9.put(criteriaEx9.get(i),x9[i]); }
		 * 
		 * for(int i=0; i<x10.length;i++) { x_perfEx10.put(criteriaEx10.get(i),x10[i]);
		 * y_perfEx10.put(criteriaEx10.get(i),y10[i]); }
		 * 
		 * for(int i=0; i<x.length;i++) { x_perf.put(criteriaEx13.get(i),x[i]);
		 * y_perf.put(criteriaEx13.get(i),y[i]); }
		 * 
		 * 
		 * 
		 * Alternative x_6 = new Alternative(1,"X",x_perfEx6); Alternative y_6 = new
		 * Alternative(2,"Y",y_perfEx6);
		 * 
		 * Alternative x_9 = new Alternative(1,"X",x_perfEx9); Alternative y_9 = new
		 * Alternative(2,"Y",y_perfEx9);
		 * 
		 * Alternative x_10 = new Alternative(1,"X",x_perfEx10); Alternative y_10 = new
		 * Alternative(2,"Y",y_perfEx10);
		 * 
		 * Alternative x_end = new Alternative(1,"X",x_perf); Alternative y_end = new
		 * Alternative(2,"Y",y_perf);
		 * 
		 * 
		 * 
		 * // weights vectors building Map<Criterion, Double> weightsEx6 = new
		 * LinkedHashMap<>(); Map<Criterion, Double> weightsEx9 = new LinkedHashMap<>();
		 * Map<Criterion, Double> weightsEx10 = new LinkedHashMap<>(); Map<Criterion,
		 * Double> weightsEx13 = new LinkedHashMap<>(); Map<Criterion, Double>
		 * weightsEx14 = new LinkedHashMap<>(); Map<Criterion, Double> weightsEx15 = new
		 * LinkedHashMap<>(); Map<Criterion, Double> weightsEx16 = new
		 * LinkedHashMap<>(); Map<Criterion, Double> weightsEx17 = new
		 * LinkedHashMap<>(); Map<Criterion, Double> weightsEx18 = new
		 * LinkedHashMap<>();
		 * 
		 * 
		 * 
		 * for(int i = 0; i<criteriaEx6.size();i++) { weightsEx6.put(criteriaEx6.get(i),
		 * w6[i]); weightsEx9.put(criteriaEx9.get(i), w9[i]); }
		 * 
		 * for(int i = 0; i<criteriaEx10.size();i++)
		 * weightsEx10.put(criteriaEx10.get(i), w10[i]);
		 * 
		 * for(int i = 0; i<criteriaEx13.size();i++) {
		 * weightsEx13.put(criteriaEx13.get(i), w13[i]);
		 * weightsEx14.put(criteriaEx13.get(i), w14[i]);
		 * weightsEx15.put(criteriaEx13.get(i), w15[i]);
		 * weightsEx16.put(criteriaEx13.get(i), w16[i]);
		 * weightsEx17.put(criteriaEx13.get(i), w17[i]);
		 * weightsEx18.put(criteriaEx13.get(i), w18[i]); }
		 * 
		 * // problems generated lm6 = new LabreucheModel(x_6,y_6,weightsEx6); lm9 = new
		 * LabreucheModel(x_9,y_9,weightsEx9); lm10 = new
		 * LabreucheModel(x_10,y_10,weightsEx10); lm13 = new
		 * LabreucheModel(x_end,y_end,weightsEx13); lm14 = new
		 * LabreucheModel(x_end,y_end,weightsEx14); lm15 = new
		 * LabreucheModel(x_end,y_end,weightsEx15); lm16 = new
		 * LabreucheModel(x_end,y_end,weightsEx16); lm17 = new
		 * LabreucheModel(x_end,y_end,weightsEx17); lm18 = new
		 * LabreucheModel(x_end,y_end,weightsEx18); }
		 */
		
		return labmod;
	}
}
