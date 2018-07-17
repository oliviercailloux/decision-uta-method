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
	
	static {System.loadLibrary("jniortools");}
	
	private List<Criterion> criteria;
	private List<Alternative> alternatives;
	private Map<Criterion,Double> weights;
	private Map<Criterion,Double> weightsReferences;
	private Map<Criterion,Double> deltas;
	private Map<Double,Alternative> scoreboard;
	private List<Criterion> positiveArguments;
	private List<Criterion> negativeArguments;
	private List<Criterion> nullArguments;
	public List<List<Criterion>> c_set;
	private Double epsilon;
	private Alternative best_choice;
	private Alternative second_choice;
	
	
	public LabreucheModel(List<Criterion> criteria, List<Alternative> alternatives, Map<Criterion,Double> weights){
		this.criteria = criteria;
		this.alternatives = alternatives;
		this.weights = weights;
		this.deltas = new LinkedHashMap<Criterion,Double>();
		this.weightsReferences = new LinkedHashMap<Criterion,Double>();
		this.scoreboard = new HashMap<Double,Alternative>();
		this.positiveArguments = new ArrayList<Criterion>();
		this.negativeArguments = new ArrayList<Criterion>();
		this.nullArguments = new ArrayList<Criterion>();
		this.c_set = new ArrayList<List<Criterion>>();
		this.epsilon = 0.2 / this.criteria.size();
		this.best_choice = null;
		this.second_choice = null;
		
		for(int i=0; i<criteria.size();i++) 
			this.weightsReferences.put(criteria.get(i),1.0/criteria.size());
	}
	
	
	/***************************************************************************************
	 *  						      													   *
	 *  								GETTERS & SETTERS							       *
	 *  							  													   *	
	 * *************************************************************************************/
	
	
	public List<Criterion> getCriteria(){ 					return this.criteria; 			}
	
	public List<Alternative> getAlternatives(){ 			return this.alternatives; 		}
		
	public Map<Criterion,Double> getWeights(){ 				return this.weights; 			}

	public Map<Criterion,Double> getWeightsReferences(){ 	return this.weightsReferences; 	}
	
	public Map<Criterion,Double> getDeltas(){				return this.deltas; 			}
	
	public Map<Double,Alternative> getScoreboard() { 		return this.scoreboard;			}
	
	public List<Criterion> getPositiveArguments() {			return this.positiveArguments;	}
	
	public List<Criterion> getNegativeArguments() {			return this.negativeArguments;	}
	
	public List<Criterion> getNullArguments() { 			return this.nullArguments;		}
	
	public List<List<Criterion>> getC_set() {				return this.c_set;				}
	
	public Double getEpsilon() { 							return this.epsilon;			}
	
	public Alternative getBestChoice() {					return this.best_choice;		}
	
	public Alternative getSecondChoice() {					return this.second_choice;		}
	
	
	
	
	/****************************************************************************************
	 *  						                   											*
	 *  		         					METHODS						    				*
	 *  					                	   											*	
	 ****************************************************************************************/
	
	
	/* * * * * * * * * * * * * * * * * * * *
	 *                                     *
	 *  Beginning's methods for resolution   *
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
		for(Alternative a : alternatives)
			this.scoreboard.put(score(a, this.weights),a);
	}

	
	// Build the  Delta, between 2 alternatives, needed for the anchor NOA, IVT.
	public void buildDelta(Alternative x, Alternative y){   // x better than y by the score function.
		if(!this.deltas.isEmpty())
			this.deltas.clear();
		
		Map<Criterion, Double> evalsX = x.getEvaluations();
		Map<Criterion,Double> evalsY = y.getEvaluations();
		
		// delta_i = x_i - y_i
		for(Criterion c : criteria)
			deltas.put(c,(evalsX.get(c) - evalsY.get(c)));	
	}
	
	
	// build the sets of positives, negatives and nulls criteria of x over y. 
	public void buildArgumentsSets(Alternative x, Alternative y){
		for(Map.Entry<Criterion,Double> w_i : weights.entrySet()) {
			if(x.getEvaluations().get(w_i.getKey()) > y.getEvaluations().get(w_i.getKey()))     // x_i > y_i
				this.positiveArguments.add(w_i.getKey());
			else
				if(x.getEvaluations().get(w_i.getKey()) < y.getEvaluations().get(w_i.getKey())) // x_i < y_i
					this.negativeArguments.add(w_i.getKey());
				else																			// x_i = y_i
					this.nullArguments.add(w_i.getKey());          
		}
	}
	
	
	
	
	
	
	/* * * * * * * * * * * * * * * * * * *
	 * 									 *
	 *    Methods used by NOA anchor    *
	 * 									 *
	 * * * * * * * * * * * * * * * * * * */
	
	
	// return all permutations of size "size" in list.
	public List<List<Criterion>> allPi(List<Criterion> list, int size){
		List<List<Criterion>> cycles = new ArrayList<List<Criterion>>();
		
		for(int i = 0; i < size; i++) {
			if(i == 0){
				List<Criterion> singleton; 
				
				for(int j = 0; j< list.size(); j++){
					singleton = new ArrayList<Criterion>();
					singleton.add(list.get(j));
					cycles.add(singleton);
				}				
			}else {
				List<List<Criterion>> copy_cycles = new ArrayList<List<Criterion>>(cycles);
				List<Criterion> new_cycle = new ArrayList<Criterion>();
				List<Criterion> list_light = new ArrayList<Criterion>(list);
				
				for(List<Criterion> cycle : copy_cycles){
					list_light.removeAll(cycle);
					
					if(list_light.size() >= 1){
						for(Criterion c : list_light){
							new_cycle = this.add(cycle,c);
							//if(!cycles.contains(new_cycle))
							cycles.add(new_cycle);
						}
						cycles.remove(cycle);
					}
					
					list_light.addAll(cycle);
				}
			}
		}
		
		return cycles;
	}
	
	
	public List<Criterion> add(List<Criterion> l, Criterion c){
		List<Criterion> new_l = new ArrayList<Criterion>(l);
		new_l.add(c);
		return new_l;
	}

	
	// return all permutations of list
	public List<List<Criterion>> allPi(List<Criterion> list){
		List<List<Criterion>> result = new ArrayList<List<Criterion>>();
		List<List<Criterion>> tmp;
	
		for(int i = 2; i<=list.size(); i++){
			tmp = allPi(list,i);
			
			for(List<Criterion> l : tmp)
				result.add(l);
		}
		
		return result;
	} 
	
	
	public Couple<Double,List<Criterion>> d_eu(List<Criterion> list, int flag) {
		Double first_part = 0.0;
		
		if(list.isEmpty()) 
			return new Couple<Double,List<Criterion>>(first_part,list);
		
		for(Criterion c : list)
			first_part += this.weights.get(c) * deltas.get(c);
				
		List<Criterion> best_min_pi = this.pi_min(list);	
		Map<Criterion,Double> pi_w = this.modified_w(best_min_pi);
		Double min_part = 0.0;
		
		for(Criterion c : best_min_pi)
			min_part += pi_w.get(c) * deltas.get(c);
		
		
		Double result = first_part - min_part;
		
		//System.out.println("Calling d_eu for "+ this.showCriteria(list) +" : "+ first_part +" - "+ min_part +" = " + result + " pi best : "+ this.showCriteria(best_min_pi) );
		
		if(flag == 0)
			return new Couple<Double,List<Criterion>>(first_part,best_min_pi);
		
		if(flag == 1)
			return new Couple<Double,List<Criterion>>(min_part,best_min_pi);
		
		return new Couple<Double,List<Criterion>>(result,best_min_pi);
	}
	
	
	// return the permutation that minimize Sum pi(i) delta_i,  for all i in "list"
	public List<Criterion> pi_min(List<Criterion> list){
		List<List<Criterion>> pis = this.allPi(list);
		Map<Criterion,Double> w_modified = new LinkedHashMap<Criterion,Double>();
		Double min_pi_value = Double.MAX_VALUE;
		List<Criterion> min_pi = null;
		Double sum = null;
		
		for(List<Criterion> pi : pis) {
			w_modified = this.modified_w(pi);
			
			sum = 0.0;
			
			for(Criterion c : list)
				sum += deltas.get(c) * w_modified.get(c);
			
			//System.out.println("Current Min part of "+ this.showCriteria(pi) + " = " + sum);
			
			if(sum < min_pi_value){
				min_pi_value = sum;
				min_pi = pi;
			}
			
			
			w_modified.clear();
			//w_modified = new LinkedHashMap<Criterion,Double>(this.weights);
		}
		
		//System.out.println("Calling pi_min for " + this.showCriteria(list) +"   pi_min returned : " + this.showCriteria(min_pi) + " = " + min_pi_value);
		return min_pi;
	}
	
	
	//returned weight vector modified by cycle
	public Map<Criterion,Double> modified_w(List<Criterion> cycle){
		Map<Criterion,Double> pi_w = new LinkedHashMap<Criterion,Double>(this.weights);
		Double tmp_pi = 0.0;

		tmp_pi = pi_w.get(cycle.get(cycle.size()- 1));
			
		for(int i= cycle.size() -1; i>= 0; i--) {        // weights vector modified by pi.
			if(i == 0)
				pi_w.put(cycle.get(0),tmp_pi);
			else
				pi_w.put(cycle.get(i),pi_w.get(cycle.get(i-1)));
		}	
		
		return pi_w;
	}
	
	
	// return all the subset of size "size" in set
	public List<List<Criterion>> allSubset(List<Criterion> set, int size){
		List<List<Criterion>> subsets= new ArrayList<List<Criterion>>();
		
		for(int i = 0; i< size; i++) {
			if(i == 0){
				List<Criterion> singleton; 
				
				for(int j = 0; j< set.size(); j++){
					singleton = new ArrayList<Criterion>();
					singleton.add(set.get(j));
					subsets.add(singleton);
				}	
			} else {
				List<List<Criterion>> copy_subsets = new ArrayList<List<Criterion>>(subsets);
				List<Criterion> new_subset = new ArrayList<Criterion>();
				List<Criterion> set_light = new ArrayList<Criterion>(set);
				
				for(List<Criterion> subset : copy_subsets){
					//System.out.println("######## changing subset ###########");
					int born_sup = set.indexOf(subset.get(subset.size()-1));
					
					//System.out.println(" subset " + this.showCriteria(subset));
					//System.out.println(" last element " + subset.get(subset.size()-1));
					//System.out.println(" born sup " + born_sup);
					
					//System.out.println(" before " + this.showCriteria(set_light));
					//System.out.println("set : " + this.showCriteria(set));
					//System.out.println(" sublist " + this.showCriteria(set.subList(0,born_sup + 1)));
					//set_light.removeAll(subset);
					set_light.removeAll(set.subList(0,born_sup + 1));
					//System.out.println(" after " + this.showCriteria(set_light));
					
					if(set_light.size() >= 1){
						for(Criterion c : set_light){
							new_subset= this.add(subset,c);
							subsets.add(new_subset);
							//System.out.println("adding subset " + this.showCriteria(new_subset));
						}
						subsets.remove(subset);
					}
					
					subsets.remove(subset);

					
					set_light.addAll(set.subList(0, born_sup + 1));
				}
			}	
		}
		
		return subsets;
	} 
	
	
	// return all the subset
	public List<List<Criterion>> allSubset(List<Criterion> set){
		List<List<Criterion>> result = new ArrayList<List<Criterion>>();
		List<List<Criterion>> tmp;
	
		for(int i = 2; i<=set.size(); i++){
			tmp = allSubset(set,i);
			
			for(List<Criterion> l : tmp)
				result.add(l);
		}
		
		return result;
	}
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * *
	 * 										   *
	 *  	Methods used by IVT anchors       *
	 *                                 		   *
	 * * * * * * * * * * * * * * * * * * * * * */
	
	
	// return true if the intersection of "list_l" and "l" is empty, and false otherwise 
	public boolean isCapEmpty(List<List<Criterion>> list_l, List<Criterion> l) {		
		if(list_l.isEmpty())
			return true;
		
		if(l.isEmpty())
			return true;
		
		List<Criterion> union_l = new ArrayList<Criterion>();
		
		for(List<Criterion> list : list_l) {
			for(Criterion c : list) {
				if(!union_l.contains(c))
					union_l.add(c);
			}
		}
		
		for(Criterion c : l) {
			if(union_l.contains(c))
				return false;
		}
		
		return true;
	}
	
	
	
	// return list sorted in a sense of lexi
	public List<List<Criterion>> sortLexi(List<List<Criterion>> list){
		List<List<Criterion>> sorted = new ArrayList<List<Criterion>>();
		int min_size = Integer.MAX_VALUE;
		
		List<List<Criterion>> tmp = new ArrayList<List<Criterion>>();
		//Map<List<Criterion>,Double> rankedSameSize = new HashMap<List<Criterion>,Double>();
		Map<Double,List<Criterion>> rankedSameSize = new HashMap<Double,List<Criterion>>();
		
		while(!list.isEmpty()) {
			//System.out.println("Size of list = "+ list.size());
			
			for(List<Criterion> l : list){
				if(l.size() < min_size)
					min_size = l.size();
			}
			
			//System.out.println("Size min find : "+ min_size);
			
			for(List<Criterion> min_size_l : list) {
				if(min_size_l.size() == min_size)
					tmp.add(min_size_l);
			}
			
			//System.out.println("Size of tmp = "+ tmp.size());
			
			for(List<Criterion> l2 : tmp) {
				//System.out.println("set "+ showCriteria(l2)+" d_eu = "+d_eu(l2));
				rankedSameSize.put(d_eu(l2,5).getLeft(),l2);
			}
			
			ArrayList<Double> keys = new ArrayList<Double>(rankedSameSize.keySet());
			Collections.sort(keys);
			Collections.reverse(keys);
			
			//System.out.println("Size of keys = "+ keys.size());
			
			for(int i=0; i < keys.size(); i++) {
				if(!sorted.contains(rankedSameSize.get(keys.get(i))))
					sorted.add(rankedSameSize.get(keys.get(i)));
			}
			
			for(List<Criterion> delete : tmp)
				list.remove(delete);
			
			//System.out.println("Size of List after remove = "+ list.size());
			rankedSameSize.clear();
			tmp.clear();
			min_size = Integer.MAX_VALUE;
		}
		
		return sorted;
	}


	//return the minimal set of permutation that inverse the decison between x and y.
	public List<List<Criterion>> algo_EU(List<List<Criterion>> a, List<List<Criterion>> b, int k){
		System.out.println(" \n #### Calling ALGO_EU : " + this.showSet(a) +"  and   k = "+ k );
		
		List<List<Criterion>> a_copy = new ArrayList<List<Criterion>>(a);
		List<List<Criterion>> f = new ArrayList<List<Criterion>>();
		
		for(int i=k; i < this.c_set.size(); i++) {				  											//L1
			System.out.println(k +" " + i +" T_i = "+ this.showCriteria(this.c_set.get(i))+ " i = "+i);
			System.out.println(k +" " + i +" Is "+ this.showSet(a)+" CAP "+ this.showCriteria(this.c_set.get(i))+ " empty  : "+ isCapEmpty(a,this.c_set.get(i)));
			
			if(isCapEmpty(a,this.c_set.get(i))){				  											//L2
				Double sum = 0.0;
				
				if(!a.isEmpty()) {
					for(List<Criterion> l : a)
						sum += this.d_eu(l,5).getLeft();
				}
				sum += this.d_eu(this.c_set.get(i),5).getLeft();
				
				Double hache = this.score(this.best_choice, this.weights) - this.score(this.second_choice, this.weights);
				System.out.println(k +" " + i +" sum better than hache? "+sum +" >= "+ hache);
				if(sum >= hache) {																			//L3
					System.out.println(k+" " + i +" Adding to F"+ this.showCriteria(this.c_set.get(i)));
					a_copy.add(this.c_set.get(i));
					f = new ArrayList<List<Criterion>>(a_copy);												//L4
				}else{																						//L5
					a_copy.add(this.c_set.get(i));
					System.out.println(k+" " + i +" Calling algo_eu"+this.showSet(a_copy)+" "+this.showSet(b)+" , "+ (i+1));
					f = algo_EU(a_copy,b,(i+1));																	//L6
					
				}
				System.out.println(k+" " + i +" Test for update :"+ !f.isEmpty() + " and [ "+ b.isEmpty()+" or "+ this.includeDiscri(f,b) +" ]");
				if(!f.isEmpty() && ( b.isEmpty() || this.includeDiscri(f,b) )) {							//L8
					System.out.println(k+" " + i +" UPDATE");
					b = new ArrayList<List<Criterion>>(f);													//L8		
				}
				System.out.println(k+" " + i +" A" + this.showSet(a)); //System.out.println(k+" " + i +" B" + this.showSet(b));//System.out.println(k+" " + i +" Test for return B :" + !b.isEmpty()+" and "+!this.includeDiscri(a_copy,b));
				if(!b.isEmpty() && !this.includeDiscri(a_copy,b)) {												//L10
					return b;		
				}
			}
			
			a_copy.clear();
		}
		List<List<Criterion>> empty = new ArrayList<List<Criterion>>();
		
		System.out.println("#### END ALGO EU : k = "+k);
		return empty;
	}
	
	
	//return true if a is include in b in a sense en include_discri
	public boolean includeDiscri(List<List<Criterion>> a, List<List<Criterion>> b) {
		boolean result = true;
		
		if(a.isEmpty())
			return true;
		
		if(b.isEmpty()){
			if(!a.isEmpty())
				return false;
			else
				return true;	
		}
		
		if(a.size() > b.size())
			return false;
		
		
		if(a.size() == b.size()) {
			boolean flag = true;
			for(int i = 0; i < a.size(); i++) {
				if(a.get(i).size() != a.get(i).size())
					flag = false;
			}
			
			if(flag) {
				int k = 0;
				
				do {
					//System.out.println(k);
					
					if(d_eu(a.get(k),5) == d_eu(b.get(k),5))
						k++;
					else
						break;
					//System.out.println(k);
				}while(k < a.size());
				
				if(d_eu(a.get(k),5).getLeft() > d_eu(b.get(k),5).getLeft())
					result = true;
				else
					result = false;
			}
			else
				result = false;
		}
		
		else {
			
			int t = Math.min(a.size(), b.size());
		
			int j = 0;
			int max_a = a.size() -1;
			int max_b = b.size() -1;
			
			do {
				if(a.get(max_a-j).size() == b.get(max_b-j).size())
					j++;
				else
					break;
			}while(j < t);
			
			
		
			if(j < t && a.get(max_a-j).size() < b.get(max_b-j).size())
				return true;
			else
				result = false;
		
			if(max_a < max_b) {
				 boolean flag = false;
				for(int i=0; i<=max_a;i++) {
					if(a.get(max_a-i).size() != b.get(max_b-i).size())
						flag = false;
				}
				
				if(flag)
					return true;
				else
					result = false;
			}
		}
		
		return result;
	}

	
	
	
	/* * * * * * * * * * * * * * *
	 *                           *
	 * 	Methods for building	 *	
	 * 	R* used in IVT anchor    *
	 *							 *
	 * * * * * * * * * * * * * * */
	
	
	
	// construct all the couple possible in list
	public List<Couple<Criterion,Criterion>> couples_of(List<Criterion> list){
		List<Couple<Criterion,Criterion>> cpl = new ArrayList<Couple<Criterion,Criterion>>();
		
		for(Criterion c1 : list) {
			for(Criterion c2 : list) {
				if(!c1.equals(c2)) {
					//System.out.println("Test ("+ c1.getName() + " , " + c2.getName() + " )");
					//System.out.println(this.deltas.get(c1)+ " < "+ this.deltas.get(c2)+  " && " + this.weights.get(c1)+ " < " + this.weights.get(c2));
					if(this.deltas.get(c1) < this.deltas.get(c2) && this.weights.get(c1) < this.weights.get(c2)) {
						//System.out.println("add  ( " + c1.getName()+" , "+ c2.getName()+ " )" );
						cpl.add(new Couple<Criterion,Criterion>(c1,c2));
					}
				}
			}
		}
		return cpl;
	}
	
	
	public List<Couple<Criterion,Criterion>> r_top(List<Couple<Criterion,Criterion>> list_c){
		boolean change_flag = false;						 // -> list_c have a new couple added, falg = true;
		List<Couple<Criterion,Criterion>> copy = new ArrayList<Couple<Criterion,Criterion>>(list_c);
		List<Couple<Criterion,Criterion>> light = null;							// -> used to avoid to check the same couples
		Couple<Criterion,Criterion> tmp = null;
		
		do {
			change_flag = false;
		
			// c1 = (a   b)
			for(Couple<Criterion,Criterion> c1 : copy) {
			light = new ArrayList<Couple<Criterion,Criterion>>(copy);
			light.remove(c1);
				
				// c2 = (c   d)
				for(Couple<Criterion,Criterion> c2 : light) {
					// (a   b) , (c   d) =>  b=c and a!=d
					if(c1.getRight().equals(c2.getLeft()) && !c1.getLeft().equals(c2.getRight())) {
						tmp  = new Couple<Criterion,Criterion>(c1.getLeft(),c2.getRight());
						if(!copy.contains(tmp)) {
							copy.add(tmp);
							change_flag = true;
						}
					}
					// (c   d) , (a   b) =>  a=d and b!=d 				
					if(c1.getLeft().equals(c2.getRight()) && !c1.getRight().equals(c2.getLeft())) {
						tmp  = new Couple<Criterion,Criterion>(c1.getRight(),c2.getLeft());
						if(!copy.contains(tmp)) {
							copy.add(tmp);
							change_flag = true;
						}
					}
				}
			}
			light.clear();
			
		}while(change_flag);
		
		return copy;
	}

	
	public List<Couple<Criterion,Criterion>> r_star(List<Couple<Criterion,Criterion>> cpls){
		List<List<Couple<Criterion,Criterion>>> subsets = this.allSubsetCouple(cpls);
		List<Couple<Criterion,Criterion>> result = new ArrayList<Couple<Criterion,Criterion>>();
		List<Couple<Criterion,Criterion>> tmp_top;
		
		for(List<Couple<Criterion,Criterion>> c_list : subsets) {
			tmp_top = this.r_top(c_list);
		
			if(tmp_top.equals(c_list)){
				for(Couple<Criterion,Criterion> c : c_list) {
					if(!result.contains(c))
						result.add(c);
				}
			}
		}
		return result;
	}
	
	
	public List<List<Couple<Criterion,Criterion>>> allSubsetCouple(List<Couple<Criterion,Criterion>> set, int size){
		List<List<Couple<Criterion,Criterion>>> subsets= new ArrayList<List<Couple<Criterion,Criterion>>>();
		for(int i = 0; i< size; i++) {
			if(i == 0){
				List<Couple<Criterion,Criterion>> singleton; 
				
				for(int j = 0; j< set.size(); j++){
					singleton = new ArrayList<Couple<Criterion,Criterion>>();
					singleton.add(set.get(j));
					subsets.add(singleton);
				}
			} 
			else {
				List<List<Couple<Criterion,Criterion>>> copy_subsets = new ArrayList<List<Couple<Criterion,Criterion>>>(subsets);
				List<Couple<Criterion,Criterion>> new_subset = new ArrayList<Couple<Criterion,Criterion>>();
				List<Couple<Criterion,Criterion>> set_light = new ArrayList<Couple<Criterion,Criterion>>(set);
				
				for(List<Couple<Criterion,Criterion>> subset : copy_subsets){
					int born_sup = set.indexOf(subset.get(subset.size()-1));
					set_light.removeAll(set.subList(0,born_sup + 1));
					
					if(set_light.size() >= 1){
						for(Couple<Criterion,Criterion> c : set_light){
							new_subset = this.addCouple(subset,c);
							subsets.add(new_subset);
						}
						subsets.remove(subset);
					}
					subsets.remove(subset);
					set_light.addAll(set.subList(0, born_sup + 1));
				}
			}	
		}
		
		return subsets;
	}
	
	
	public List<List<Couple<Criterion,Criterion>>> allSubsetCouple(List<Couple<Criterion,Criterion>> set){
		List<List<Couple<Criterion,Criterion>>> result = new ArrayList<List<Couple<Criterion,Criterion>>>();
		List<List<Couple<Criterion,Criterion>>> tmp;
	
		for(int i = 1; i<=set.size(); i++){
			tmp = allSubsetCouple(set,i);
			
			for(List<Couple<Criterion,Criterion>> l : tmp)
				result.add(l);
		}
		
		return result;
	}
	
	
	public List<Couple<Criterion,Criterion>> addCouple(List<Couple<Criterion,Criterion>> l, Couple<Criterion,Criterion> c){
		List<Couple<Criterion,Criterion>> new_l = new ArrayList<Couple<Criterion,Criterion>>(l);
		new_l.add(c);
		return new_l;
	}
	
	
	
	
		
	/* * * * * * * * * * * * * * * * * * *
	 * 									 *
	 * 	Used to show properly sets of    *
	 *  criteria or Couple of criteria   *
	 *                                   *
	 * * * * * * * * * * * * * * * * * * */
	
	
	
	public String displayAsVector(Alternative a) {
 
		String vectorPerf = "( ";
		
		for(Map.Entry<Criterion, Double>  perf : a.getEvaluations().entrySet())
			vectorPerf += " "+perf.getValue()+" ";
				
		return vectorPerf +" )";
	}

	
	public String showVector(Map<Criterion,Double> v) {
		String show2 = "( ";
		
		for(Map.Entry<Criterion, Double> c : v.entrySet()) {
			Double c_scaled = BigDecimal.valueOf(c.getValue()).setScale(3, RoundingMode.HALF_UP).doubleValue();
			show2 += c_scaled+" ";
		}
		show2 += " )";	
		
		return "  "+show2;
	}

	
	public String showCriteria(List<Criterion> c){
		String show = "{ ";
		
		for(int i = 0; i < c.size();i++){
			if(i == c.size()-1)
				show += c.get(i).getName();
			else
				show += c.get(i).getName()+", ";
		}
			
		return show +" }";
	}

	
	public String showSet(List<List<Criterion>> list) {
		String str = "{ ";
		
		for(List<Criterion> l : list)
			str += this.showCriteria(l) + " ";
		
		str += " }";
		
		return str;
	}
	

	public String showCouples(List<Couple<Criterion,Criterion>> cpls) {
		String string = "{ ";
		
		for(Couple<Criterion,Criterion> c : cpls)
			string += "("+((Criterion) c.getLeft()).getName() +","+ ((Criterion) c.getRight()).getName()+") ";
		
		string += " }";
		
		return string;
	}
	
	

	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                Methods On Process                                         *
	 *                                                                                           *
	 *********************************************************************************************/

	
	
	// empty! \o/
	
	
	
	/*********************************************************************************************
	 * 																							 *	
	 *  						Explanation methods : Anchors									 *
	 *  																						 *
	 * 						  Explain why "x" is better than "y"								 *		
	 *  																						 *
	 ********************************************************************************************/
	
	
	
	
	public String anchorALL(Alternative x, Alternative y){
		buildDelta(x,y);
		
		for(Map.Entry<Criterion,Double> d : deltas.entrySet()){
			if(d.getValue() < 0.0)
				return "not applicable";
		}
		
		return  x.getName()+" is preferred to "+y.getName()+" since "+x.getName()+" is better then "+y.getName()+" on ALL criteria.";
	}
	
	
	
	public String anchorNOA(Alternative x, Alternative y){
		//System.out.println("reference score : "+score(x,weightsReferences)+ " > "+score(y,weightsReferences)+" ?");
		
		if(score(x,this.weightsReferences) > score(y,this.weightsReferences))
			return "not applicable";
		
		System.out.println("\n \n" + "Delta " + x.getName() +" > "+y.getName() + " : " + this.showVector(deltas));
		
		Map<Double,Criterion> temp = new HashMap<Double,Criterion>();  				// ->  associate (w_i - 1/n) * Delta_i to the criterion c_i
		
		
		for(Map.Entry<Criterion, Double> w_i : this.weights.entrySet())
			temp.put( (w_i.getValue() - 1.0 /this.criteria.size()) * this.deltas.get(w_i.getKey()), w_i.getKey());
		
		//System.out.println("Bidule machin chouette : ");
		
		//for(Map.Entry<Double, Criterion> tp : temp.entrySet())
		//	System.out.println(tp.getValue() +" : "+ tp.getKey());
		
		// sorting the values
		ArrayList<Double> keys = new ArrayList<Double>(temp.keySet());
		//System.out.println("Print keys : "+ keys);
		Collections.sort(keys);
		//System.out.println("Print keys sorted : "+ keys);
		
		
		List<Criterion> C = new ArrayList<Criterion>();
		//Map<Criterion,Double> wcomposed = this.weights;
		Map<Criterion,Double> wcomposed = this.weightsReferences;
		
		int p = keys.size() - 1;
		
		//proposition 2 Section 5
		//System.out.println("********** Calcul du plus petit ensemble possible !!************");
		//System.out.println(score(x,wcomposed)+" > "+score(y,wcomposed));
		do {
			//System.out.println("Nouvelle iteration :   " );
			//System.out.println(" p = "+p);
			//System.out.println("changing : "+keys.get(p));
			
			wcomposed.put(temp.get(keys.get(p)), this.weights.get((temp.get(keys.get(p)))));
			
			//for(Map.Entry<Criterion, Double> tp : wcomposed.entrySet())
			//	System.out.println(tp.getKey().getId()+" : "+tp.getValue());
				
			C.add(temp.get(keys.get(p)));
			
			//System.out.println("v("+x.getName()+") = "+score(x,wcomposed)+" > "+score(y,wcomposed)+" = v("+y.getName()+")");
			
		 	p--;
		}
		while(score(x,wcomposed) < score(y,wcomposed));
				
		//System.out.println("C = "+ C.toString());
		
		List<Criterion> ConPA = new ArrayList<Criterion>();
		List<Criterion> ConNA = new ArrayList<Criterion>();
		
		for(Criterion c : C){
			if(positiveArguments.contains(c))
				ConPA.add(c);
			else
				ConNA.add(c);
		}
		
		String explanation = "Even though "+ y.getName() +" is better than "+ x.getName() +" on average, "+ x.getName()+ 
				" is preferred to "+ y.getName() +" since \n"+ x.getName() + " is better than "+ y.getName()+" on the criteria "+
				showCriteria(ConPA)+" that are important whereas \n"+ x.getName()+" is worse than "+ y.getName()+ " on the criteria "+
				showCriteria(ConNA)+" that are not important.";
		
		Double addSentence = 0.0;
		for(int i =0; i < criteria.size();i++) {
			if(!C.contains(criteria.get(i)))
				addSentence += deltas.get(criteria.get(i)) * weights.get(criteria.get(i));
		}
		
		if(addSentence > 0)
			explanation += "\n" + "Moroever, "+x.getName()+" is on average better than "+y.getName()+" on the other criteria.";
		
		return explanation;
	}  
	
	
	
	public String anchorIVT(Alternative x, Alternative y) {
		String explanation = "not applicable";				
		int size = 2;
		size = this.criteria.size();
		List<List<Criterion>> subsets = new ArrayList<List<Criterion>>();     
		List<List<Criterion>> big_a = new ArrayList<List<Criterion>>();       // -> first variable for algo_eu calls 
		List<List<Criterion>> big_b = new ArrayList<List<Criterion>>();       // -> second variable for algo_eu calls
		List<List<Criterion>> big_c = new ArrayList<List<Criterion>>();       // result of algo_eu
		Double d_eu = null;
		List<Criterion> pi = new ArrayList<Criterion>();
		
		System.out.println("Delta " + x.getName() +" > "+y.getName() + " : " + this.showVector(deltas) + "\n");
		
		do {
			d_eu = null;
			pi.clear();
			big_a.clear();
			big_b.clear();
			big_c.clear();
			subsets.clear();
			this.c_set.clear();
			
			//subsets = this.allSubset(this.criteria,size);
			
			subsets = this.allSubset(this.criteria);
			
			for(List<Criterion> subset : subsets) {
				System.out.println(this.showCriteria(subset));
				d_eu = this.d_eu(subset,5).getLeft();
				System.out.println(" E.T");
				pi = this.pi_min(subset);
				System.out.println("Subset : " + this.showCriteria(subset) + " d_eu = " + d_eu + " pi : "+ this.showCriteria(pi));
				if(d_eu > 0 && pi.containsAll(subset) && subset.containsAll(pi) && pi.containsAll(subset)) {
					this.c_set.add(subset);
					System.out.println("adding set to c_set : " + this.showCriteria(subset));
				}
				
				pi.clear();
			}
			
			
			this.c_set = this.sortLexi(this.c_set);
			
			System.out.println("C_set : ");
			for(List<Criterion> l : this.c_set)
				System.out.println(this.showCriteria(l)); // + " " + this.d_eu(l,0) + " "  + this.d_eu(l,1) + " " + this.d_eu(l,5) + " " + this.showCriteria(this.pi_min(l)));
			
			//System.out.println("@@@@@@@@@@@@  ALGO EU CALLING @@@@@@@@@@@@");
			big_c = this.algo_EU(big_a, big_b, 0);
			
			//System.out.println("Algo_eu return  : ");
			//for(List<Criterion> c : big_c)
				//System.out.println(this.showCriteria(c));
			
			size++;
		}while(big_c.isEmpty() && size <= this.criteria.size());
		
		if(big_c.isEmpty())
			return explanation;
		
		
		// Start to determine R*
		
		List<Couple<Criterion,Criterion>> cpls = new ArrayList<Couple<Criterion,Criterion>>();
		List<Couple<Criterion,Criterion>> r_s = new ArrayList<Couple<Criterion,Criterion>>();
		
		System.out.println("Minimal permutation : " + this.showSet(big_c) + "\n");
		
		for(List<Criterion> l : big_c) {
			r_s = this.couples_of(l);
			
			for(Couple<Criterion,Criterion> c : r_s) {
				if(!cpls.contains(c))
					cpls.add(c);
			}
			
			r_s.clear();
		}
				
		List<Couple<Criterion,Criterion>> r_star = this.r_star(cpls);

		// R* determined
		
		
		List<Criterion> k_ps  = new ArrayList<Criterion>();
		List<Criterion> k_prs = new ArrayList<Criterion>();
		List<Criterion> k_nw  = new ArrayList<Criterion>();
		List<Criterion> k_nrw = new ArrayList<Criterion>();
		List<Couple<Criterion,Criterion>> k_pn  = new ArrayList<Couple<Criterion,Criterion>>();
		
		for(Couple<Criterion,Criterion> c : r_star) { 						// c = (i,j) where i and j are criteria
			
			// j in S+ and i in S- and w_j > w_i
			if(this.positiveArguments.contains(c.getRight()) && this.negativeArguments.contains(c.getLeft()) && this.weights.get(c.getRight()) > this.weights.get(c.getLeft())) {
				
				// w_j >= 1/n  and w_i <= 1/n 
				if(this.weights.get(c.getRight()) >= (1.0 / this.criteria.size()) && this.weights.get(c.getLeft()) <= (1.0 / this.criteria.size())   ) {
					if(!k_ps.contains(c.getRight()))
						k_ps.add(c.getRight());  			// adding j
					if(!k_nw.contains(c.getLeft()))
						k_nw.add(c.getLeft());   			// adding i
				}
				else {
					// w_j >> w_i 
					if(this.weights.get(c.getRight()) > this.weights.get(c.getLeft()) + this.epsilon) {
						if(!k_pn.contains(c))
							k_pn.add(c);   					// adding (i,j)
					}
					else{
						// w_j >= 1/n
						if(this.weights.get(c.getRight()) >= (1.0 / this.criteria.size())) {
							if(!k_ps.contains(c.getRight()))
								k_ps.add(c.getRight());  	// adding j
						}
						else {
							// w_i <= 1/n
							if(this.weights.get(c.getLeft()) <= (1.0 / this.criteria.size())) {
								if(!k_nw.contains(c.getLeft()))
									k_nw.add(c.getLeft());   // adding i
							}
						}
					}
				}
			}
			
			// i and j in S+ and delta_j > delta_i and w_j > w_i
			if(this.positiveArguments.contains(c.getLeft()) && this.positiveArguments.contains(c.getRight()) && 
			   this.deltas.get(c.getRight()) > this.deltas.get(c.getLeft()) && this.weights.get(c.getRight()) > this.weights.get(c.getLeft())) {
				
				// w_j >= 1/n
				if(this.weights.get(c.getRight()) >= (1.0 / this.criteria.size()) ) {
					if(!k_ps.contains(c.getRight()))
						k_ps.add(c.getRight());			// adding j
				}
				else {
					if(!k_prs.contains(c.getRight()))
						k_prs.add(c.getRight());		// adding j
				}
			}
					
			if(this.negativeArguments.contains(c.getLeft()) && this.negativeArguments.contains(c.getRight()) && 
			   this.deltas.get(c.getRight()) > this.deltas.get(c.getLeft()) && this.weights.get(c.getRight()) > this.weights.get(c.getLeft())) {
				
				// w_j <= 1/n
				if(this.weights.get(c.getRight()) <= (1.0 / this.criteria.size()) ) {
					if(!k_nw.contains(c.getRight()))
						k_nw.add(c.getRight());			// adding j
				}
				else {
					if(!k_nrw.contains(c.getRight()))
						k_nrw.add(c.getRight());		// adding j
				}
			}
		}
		
		System.out.println("R* : " + this.showCouples(r_star)+ "\n");
		
		if(!k_ps.isEmpty())
			System.out.print("K_ps = " + this.showCriteria(k_ps) + "  ");
		
		if(!k_prs.isEmpty())
			System.out.print("K_prs = " + this.showCriteria(k_prs) + "  ");
		
		if(!k_nw.isEmpty())
			System.out.print("K_nw = " + this.showCriteria(k_nw) + "  ");
		
		if(!k_nrw.isEmpty())
			System.out.print("K_nrw = " + this.showCriteria(k_nrw) + "  ");
		
		if(!k_pn.isEmpty())
			System.out.print("K_pn = " + this.showCouples(k_pn) + " ");
		
		System.out.println("\n");
		
		explanation = x.getName() + " is preferred to " + y.getName() + " since " + x.getName() + " is better than " +
					  y.getName() + " on the criteria " + this.showCriteria(k_ps) + " that are important "+ "\n" +"and on the criteria " +
					  this.showCriteria(k_prs) + " that are relativily important, " +y.getName() + " is better than " + 
					  x.getName() + " on the criteria " + this.showCriteria(k_nw) + "\n" +"that are not important and on the criteria " +
					  this.showCriteria(k_nrw) + " that are not really important.";
		
		if(!k_pn.isEmpty()) {
			explanation += "And ";
			for(Couple<Criterion,Criterion> couple : k_pn){
				explanation += couple.getRight().getName() + " for wich " + x.getName() + " is better than " + y.getName() +
							" is more important than criterion " + couple.getLeft().getName() + " for wich " + x.getName() + 
							" is worse than " + y.getName() + " ";
			}
			explanation += ".";
		}
				
		return explanation;		
	}
	
	
	
	public String anchorRMG(Alternative x, Alternative y){
		String explanation = "";
		Double max_w = Double.MIN_VALUE;
		
		for(Map.Entry<Criterion,Double> c : this.weights.entrySet()) {
			Double v = Math.abs(c.getValue() - (1.0 / this.criteria.size()));	
			
			if( v > max_w)
				max_w = v;
		}
		
		//System.out.println("MAX : "+max_w  +" epsilon : "+this.epsilon);
		
		if( max_w <= this.epsilon ) {
			explanation = x.getName() +" is preferred to "+ y.getName() +" since "+ x.getName() +" is on average better than "+ y.getName() + 
						"\n" + " and all the criteria have almost the same weights.";	
		} else {
				if( max_w < this.epsilon * 2) {
					explanation = x.getName() +" is preferred to "+ y.getName() +" since the intensity of the preference "+ x.getName() +" over "+
								x.getName() +" on \n"+ this.showCriteria(this.positiveArguments) +" is significantly larger than the intensity of "+ 
								y.getName() +" over "+ x.getName() +" on \n"+ this.showCriteria(this.negativeArguments) +
								", and all the criteria have more or less the same weights.";
				} else {
						explanation = x.getName() +" is preferred to "+ y.getName() +" since the intensity of the preference "+ x.getName() +
									" over "+ x.getName() +" on "+ this.showCriteria(this.positiveArguments) +" is much larger than the intensity of "+ 
						  			y.getName() +" over "+ x.getName() +" on "+ this.showCriteria(this.negativeArguments) +".";
				}
		}
		
		return explanation;
	}
	
	
	
	public void resolved(){
		String display =  "****************************************************************";
		display += "\n" + "*                                                              *";
		display += "\n" + "*         Recommender system based on Labreuche Model          *";
		display += "\n" + "*                                                              *";
		display += "\n" + "****************************************************************" + "\n";
		
		display += "\n    Criteria    <-   Weight : \n";
		
		for(Criterion c : criteria)
			display += "\n" + "	" + c.getName() + "  <-  w_"+c.getId()+" = "+this.weights.get(c);

		display += "\n \n Alternatives : "; 
		
		for(Alternative a : alternatives)	
			display += "\n" + "	" + a.getName() + " " + " : " + displayAsVector(a);
		
		System.out.println(display);
		
		long start = System.currentTimeMillis();
		
		this.scoringAlternatives();
	
		display = "\n" + "Top of alternatives : ";
		
		ArrayList<Double> scores = new ArrayList<Double>(this.scoreboard.keySet());
		Collections.sort(scores);
		 
		for(int i = scores.size() - 1; i >= 0; i--)
			display += "\n "+ " " + this.scoreboard.get(scores.get(i)).getName()+ " : " + scores.get(i);
		
		Alternative x = this.scoreboard.get(scores.get(scores.size() - 1)); 
		Alternative y = this.scoreboard.get(scores.get(scores.size() - 2));
		
		this.best_choice = this.scoreboard.get(scores.get(scores.size() - 1)); 
		this.second_choice = this.scoreboard.get(scores.get(scores.size() - 2));
		
		this.buildDelta(x, y);
		
		//display += "\n \n" + "Delta " + x.getName() +" > "+y.getName() + " : " + this.showVector(deltas);
		
		System.out.println(display + "\n");	
		
		display = "Explanation why "+x.getName()+" is better than "+y.getName()+" :";

		this.buildArgumentsSets(x, y);
		
		String explanation = this.anchorALL(x, y);
		
		if(explanation == "not applicable") {
			//System.out.println("Anchor ALL not applicable");
			explanation = this.anchorNOA(x, y);
		}
		
		if(explanation == "not applicable") {
			//System.out.println("Anchor NOA not applicable");
			explanation = this.anchorIVT(x, y);
		}
				
		if(explanation == "not applicable") {
			//System.out.println("Anchor IVT not applicable");
			explanation = this.anchorRMG(x, y);
		}
		
		System.out.println(display);
		
		display = "\n" + explanation;
		long time = System.currentTimeMillis() - start;
		display += "\n \n" + "Problem solved in : " + time + " milliseconds";
		System.out.println(display);
	}


	
}