package io.github.oliviercailloux.decision.arguer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.google.common.collect.Table;
import com.google.common.math.Stats;

import io.github.oliviercailloux.decision.arguer.labreuche.ArgumentGenerator;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheComputer;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.nunes.NunesTools;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Experimentations {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Experimentations.class);
	
		
	public static void main(String [] args) {
		
		int nbAlt = 20;
		int nbCrit = 3;
		int maxSim = 10000;
	    
	    Map<Integer,List<Integer>> globalStatLabreucheAnchor = new LinkedHashMap<>();
	    //Map<Integer,List<Integer>> globalStatNunesPattern= new LinkedHashMap<>();
	    Map<Integer,List<Integer>> globalStatSameResult = new LinkedHashMap<>();
	    
	    Map<Integer,List<Integer>> globalStatALL = new LinkedHashMap<>();
	    Map<Integer,List<Integer>> globalStatNOA = new LinkedHashMap<>();
	    Map<Integer,List<Integer>> globalStatIVT = new LinkedHashMap<>();
	    Map<Integer,List<Integer>> globalStatRMGAVG = new LinkedHashMap<>();
	    Map<Integer,List<Integer>> globalStatRMGCOMP = new LinkedHashMap<>();
		
		for(int i = 2; i <= nbAlt; i++) {
			List<Integer> statsLabreucheAnchor = new ArrayList<>();
			//List<Integer> statsNunesPattern = new ArrayList<>();
			List<Integer> statsSameResult = new ArrayList<>();
			    
			List<Integer> statsALL = new ArrayList<>();
			List<Integer> statsNOA = new ArrayList<>();
			List<Integer> statsIVT = new ArrayList<>();
			List<Integer> statsRMGAVG = new ArrayList<>();
			List<Integer> statsRMGCOMP = new ArrayList<>();
			    
			for(int j = 0; j < maxSim; j++) {
				ArgumentGenerator ag = new ArgumentGenerator(i, nbCrit);
				Alternative bestL;
				Alternative bestN;
				
				try {
					bestL = ag.findUniqueBest();

				} catch (IllegalArgumentException e3) {
					StringBuilder bld = new StringBuilder();
					bld.append(" BUG : " + e3.getMessage());
					LOGGER.debug(bld.toString());

					Iterator<Alternative> itr = ag.findBest().iterator();

					bestL = itr.next();
				}
				
				try {
					bestN = uniqueBestNunes(ag.getAlternatives(),ag.getWeights());
				} catch (IllegalArgumentException e3) {
					StringBuilder bld = new StringBuilder();
					bld.append(" BUG : " + e3.getMessage());
					LOGGER.debug(bld.toString());

					Iterator<Alternative> itr = bestsNunes(ag.getAlternatives(), ag.getWeights()).iterator();
					
					bestN = itr.next();
				}
				
				updateSame(statsSameResult, bestL, bestN);
				
				if(!bestL.equals(bestN)) {
					LabreucheOutput lbo = ag.compare(bestL, bestN);
					addStatLabreuche(statsLabreucheAnchor, lbo);
					update(statsALL, lbo, Anchor.ALL);
					update(statsNOA, lbo, Anchor.NOA);
					update(statsIVT, lbo, Anchor.IVT);
					update(statsRMGAVG, lbo, Anchor.RMGAVG);
					update(statsRMGCOMP, lbo, Anchor.RMGCOMP);
				}
				
			}

			globalStatLabreucheAnchor.put(i, statsLabreucheAnchor);
			globalStatSameResult.put(i, statsSameResult);
			//globalStatNunesPattern.put(i, statsNunesPattern);
			globalStatALL.put(i, statsALL);
			globalStatNOA.put(i, statsNOA);
			globalStatIVT.put(i, statsIVT);
			globalStatRMGAVG.put(i, statsRMGAVG);
			globalStatRMGCOMP.put(i, statsRMGCOMP);
			
			System.out.println(".");
		}
		
		StringBuilder result = new StringBuilder();
		result.append("Number of alternatives                    : " + "	");
																
		for(int i = 2; i <= nbAlt; i++) {							
			result.append(i + "	");
		}
		
		result.append("\n");
		
		result.append(showPourcents("Pourcentage of sames alternatives find    : ", globalStatSameResult));
		result.append(showVariances("Variance of anchor distribution           : ", globalStatLabreucheAnchor));
		result.append(showDeviations("Standard deviation of anchor distribution : ", globalStatLabreucheAnchor));
		result.append(showPourcents("Pourcentage of anchor ALL used            : ", globalStatALL));
		result.append(showPourcents("Pourcentage of anchor NOA used            : ", globalStatNOA));
		result.append(showPourcents("Pourcentage of anchor IVT used            : ", globalStatIVT));
		result.append(showPourcents("Pourcentage of anchor RMGAVG used         : ", globalStatRMGAVG));
		result.append(showPourcents("Pourcentage of anchor RMGCOMP used        : ", globalStatRMGCOMP));
	
		System.out.println(result.toString());
	}
	
	private static Object showDeviations(String message, Map<Integer, List<Integer>> map) {
		StringBuilder result = new StringBuilder();

		result.append(message + "	");
		
		for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
			if(!entry.getValue().isEmpty())
				result.append(new DecimalFormat("##.##").format(Stats.of(entry.getValue()).populationStandardDeviation()) +"	");
			else
				result.append("None" +"	");

		}
		
		result.append("\n");
		
		return result.toString();
	}

	private static Object showVariances(String message, Map<Integer, List<Integer>> map) {
		StringBuilder result = new StringBuilder();

		result.append(message + "	");
		
		for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
			if(!entry.getValue().isEmpty())
				result.append(new DecimalFormat("##.##").format(Stats.of(entry.getValue()).populationVariance()) +"	");
			else
				result.append("None" +"	");

		}
		
		result.append("\n");
		
		return result.toString();
	}

	private static String showPourcents(String message, Map<Integer, List<Integer>> map) {
		StringBuilder result = new StringBuilder();

		result.append(message + "	");
		
		for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
			if(!entry.getValue().isEmpty())
				result.append(new DecimalFormat("##.##").format(Stats.meanOf(entry.getValue()) * 100)+"%"+ "	");
			else
				result.append(0 + "%" +"	");

		}
		
		result.append("\n");
		
		return result.toString();
	}
	
	private static void update(List<Integer> stats, LabreucheOutput lbo, Anchor anchor) {
		if(lbo.getAnchor().equals(anchor)) {
			stats.add(1);
		}else {
			stats.add(0);
		}
	}

	private static void updateSame(List<Integer> statsSameResult, Alternative bestL, Alternative bestN) {
		
		if(bestL.equals(bestN)) {
			statsSameResult.add(1);
		}else {
			statsSameResult.add(0);
		}
	}

	private static void addStatLabreuche(List<Integer> stats, LabreucheOutput lbo) {
		switch(lbo.getAnchor()) {
		
			case ALL: 
				stats.add(1);
				break;
			
			case NOA:
				stats.add(2);
				break;
			
			case IVT:
				stats.add(3);
				break;
				
			case RMGAVG:
				stats.add(4);
				break;
				
			case RMGCOMP:
				stats.add(5);
				break;
				
			default :
				throw new IllegalStateException();
		}
	}	
		
	private static Set<Alternative> bestsNunes(Set<Alternative> set, Map<Criterion,Double> weights) {
		Map<Alternative,Integer> scoreboard = new LinkedHashMap<>();
		
		Table<Alternative,Alternative,Double> tradeoffs = NunesTools.computeTO(set, weights);
		
		for(Alternative a : set) {
			scoreboard.put(a, 0);
		}
				
		for(Alternative a : set) {
			for(Alternative b : set) {
				if(!a.equals(b)) {
					double scoreA = NunesTools.score(a, b, weights.keySet(), weights, tradeoffs);
					double scoreB = NunesTools.score(b, a, weights.keySet(), weights, tradeoffs);
					
					if(scoreA < scoreB) {
						int tmp = scoreboard.get(a);
						scoreboard.put(a, tmp+1);
					}
				}
			}
		}
		
		int max = Collections.max(scoreboard.values());		
		
		Set<Alternative> bests = new LinkedHashSet<>();
		
		for(Entry<Alternative,Integer> entry : scoreboard.entrySet()) {
			if(entry.getValue() == max) {
				bests.add(entry.getKey());
			}
		}
		
		return bests;
	}
	
	private static Alternative uniqueBestNunes(Set<Alternative> set, Map<Criterion,Double> weights) {
		Set<Alternative> bests = bestsNunes(set, weights);
		
		if(bests.size() == 1) {
			Iterator<Alternative> iter = bests.iterator();
			return iter.next();	 
		}
		
		throw new IllegalArgumentException();
	}

}
