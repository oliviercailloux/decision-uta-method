package io.github.oliviercailloux.labreuchemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheModel {
	
	private List<Criterion> criteria;
	private Map<Criterion,Double> weights;
	private Map<Double,Alternative> scoreboard;
	private List<List<Criterion>> c_set;
	private Double epsilon;
	private Alternative best_choice;
	private Alternative second_choice;
	private ALLOutput phi_all;
	private NOAOutput phi_noa;
	private IVTOutput phi_ivt;
	private RMGAVGOutput phi_rmgavg;
	private RMGCOMPOutput phi_rmgcomp;
	private LabreucheOutput lop;
	private AlternativeComparison comparison;
	
	public LabreucheModel(AlternativeComparison alt){
		this.comparison = alt;
		this.weights = alt.getW();
		this.best_choice = alt.getX();
		this.second_choice = alt.getY();
		this.criteria = new ArrayList<>();
		
		for(Criterion c : weights.keySet())
			this.criteria.add(c);
				
		this.scoreboard = new HashMap<>();
		this.c_set = new ArrayList<>();
		this.epsilon = 0.2 / this.criteria.size();
		
		
	}
	
	
	/***************************************************************************************
	 *  						      													   *
	 *  								GETTERS & SETTERS							       *
	 *  							  													   *	
	 * *************************************************************************************/
	
	
	public List<Criterion> getCriteria(){ 		
		return this.criteria; 	
	}
			
	public Map<Criterion,Double> getWeights(){ 		
		return this.weights; 
	}
	

	public Map<Double,Alternative> getScoreboard() { 
		return this.scoreboard;		
	}
	
	public List<List<Criterion>> getC_set() {		
		return this.c_set;			
	}
	
	public Double getEpsilon() { 				
		return this.epsilon;	
	}
	
	public Alternative getBestChoice() {		
		return this.best_choice;		
	}
	
	public Alternative getSecondChoice() {		
		return this.second_choice;		
	}
	
		
	
	/****************************************************************************************
	 *  						                   											*
	 *  		         					METHODS						    				*
	 *  					                	   											*	
	 ****************************************************************************************/
	
	
	/* * * * * * * * * * * * * * * * * * * *
	 *                                     *
	 *  Beginning's methods for resolution *
	 *                                     *
	 * * * * * * * * * * * * * * * * * * * */
	
	
	//give v(x)
 	public Double score(Alternative x, Map<Criterion,Double> w){
		Double score = 0.0;
		
		//Sum w_i * x_i
		for(Criterion c : w.keySet())
			score +=  w.get(c) * x.getEvaluations().get(c) ;
		
		return score;
	}
	

 	// associate the score for all the alternatives and stock them on the scoreboard.
	public void scoringAlternatives(){
		this.scoreboard.put(score(best_choice, this.weights),best_choice);
		this.scoreboard.put(score(second_choice, this.weights),second_choice);
	}


	/* * * * * * * * * * * * * * * * * * * *
	 * 									   *
	 *  	   Resolution problem		   *
	 * 									   *
	 * * * * * * * * * * * * * * * * * * * */
	
	public void startproblem(boolean show){
		
		String display =  "****************************************************************";
		display += "\n" + "*                                                              *";
		display += "\n" + "*         Recommender system based on Labreuche Model          *";
		display += "\n" + "*                                                              *";
		display += "\n" + "****************************************************************" + "\n";
		
		display += "\n    Criteria    <-   Weight : \n";
		
		for(Criterion c : criteria)
			display += "\n" + "	" + c.getName() + "  <-  w_"+c.getId()+" = "+this.weights.get(c);

		display += "\n \n Alternatives : "; 
		
		display += "\n" + "	" + best_choice.getName() + " " + " : " + Tools.displayAsVector(best_choice);
		display += "\n" + "	" + second_choice.getName() + " " + " : " + Tools.displayAsVector(second_choice);
		
		if(show)
			System.out.println(display);
		
		//long start = System.currentTimeMillis();
		
		this.scoringAlternatives();
	
		display = "\n" + "Top of alternatives : ";
		
		ArrayList<Double> scores = new ArrayList<>(this.scoreboard.keySet());
		Collections.sort(scores);
		 
		for(int i = scores.size() - 1; i >= 0; i--)
			display += "\n "+ " " + this.scoreboard.get(scores.get(i)).getName()+ " : " + scores.get(i);
		
		Alternative x = this.scoreboard.get(scores.get(scores.size() - 1)); 
		Alternative y = this.scoreboard.get(scores.get(scores.size() - 2));
		
		this.best_choice = this.scoreboard.get(scores.get(scores.size() - 1)); 
		this.second_choice = this.scoreboard.get(scores.get(scores.size() - 2));
		
		if(show)
			System.out.println(display + "\n");
		
		display = "Explanation why "+x.getName()+" is better than "+y.getName()+" :";
		
		//getExplanation();
		
		//String explanation = arguer();
		
		
		//display = "\n" + explanation;
		//long time = System.currentTimeMillis() - start;
		//display += "\n \n" + "Problem solved in : " + time + " milliseconds";
		
		if(show)
			System.out.println(display);
	}
	
	
	public LabreucheOutput getExplanation() {	
		
		if(phi_all.isApplicable())
			lop = phi_all;
		else {
			System.out.println("noa");
			if(phi_noa.isApplicable())
				lop = phi_noa;
			else {
				System.out.println("ivt");
				if(phi_ivt.isApplicable())
					lop = phi_ivt;
				else {
					System.out.println("rmg");
					if(phi_rmgavg.isApplicable())
						lop = phi_rmgavg;
					else {
						lop = phi_rmgcomp;
					}
				}
			}
		}
		
		return lop;
	}
	
	
	public boolean isAnchorALLApplicable() {
		phi_all = new ALLOutput(this.comparison);
		
		return phi_all.isApplicable();
	}
	
	public boolean isAnchorNOAApplicable() {
		phi_noa = new NOAOutput(comparison);
		
		return phi_noa.isApplicable();
	}
	
	public boolean isAnchorIVTApplicable() {
		phi_ivt = new IVTOutput(comparison);
		
		return phi_ivt.isApplicable();
	}
	
	public boolean isAnchorRMGAVGApplicable(){
		phi_rmgavg = new RMGAVGOutput(comparison);
		
		return phi_rmgavg.isApplicable();
	}
	
	public boolean isAnchorRMGCOMPApplicable() {
		phi_rmgcomp = new RMGCOMPOutput(comparison);
		
		return phi_rmgcomp.isApplicable();
	}
	
	
	public ALLOutput getALLExplanation() {
		return phi_all;
	}
	
	public NOAOutput getNOAExplanation(){
		return phi_noa;
	}
	
	public IVTOutput getIVTExplanation() {
		return phi_ivt;
	}
	
	public RMGAVGOutput getRMGAVGExplanation(){
		return phi_rmgavg;
	}
	
	public RMGCOMPOutput getRMGCOMPExplanation() {
		return phi_rmgcomp;
	}
	
	public String arguer() {
		return lop.argue();
	}
	
}