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
	private static int nbAlt = 50;
	private static int nbCrit = 3;
	private static int maxSim = 1000;
		
	public static void main(String [] args) {
	    
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsLabreucheAnchor = new LinkedHashMap<>();	    
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsALL = new LinkedHashMap<>();
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsNOA = new LinkedHashMap<>();
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsIVT = new LinkedHashMap<>();
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsRMGAVG = new LinkedHashMap<>();
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsRMGCOMP = new LinkedHashMap<>();
	    
	    Map<Integer,Map<Integer,List<Integer>>> generalStatsSameResult = new LinkedHashMap<>();
	    
	    //Map<Integer,Map<Integer,List<Integer>>> generalStatNunesPattern
		
	    for(int c = 3; c <= nbCrit; c+=2) {
	    	
	    	Map<Integer,List<Integer>> globalStatLabreucheAnchor = new LinkedHashMap<>();
		    Map<Integer,List<Integer>> globalStatALL = new LinkedHashMap<>();
		    Map<Integer,List<Integer>> globalStatNOA = new LinkedHashMap<>();
		    Map<Integer,List<Integer>> globalStatIVT = new LinkedHashMap<>();
		    Map<Integer,List<Integer>> globalStatRMGAVG = new LinkedHashMap<>();
		    Map<Integer,List<Integer>> globalStatRMGCOMP = new LinkedHashMap<>();
		    
		    Map<Integer,List<Integer>> globalStatSameResult = new LinkedHashMap<>();
		    
		    //Map<Integer,List<Integer>> globalStatNunesPattern= new LinkedHashMap<>();
	    	
			for(int i = 2; i <= nbAlt; i+=2) {
				
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
				
				System.out.println("end i " + i);
			}
			
			generalStatsLabreucheAnchor.put(c, globalStatLabreucheAnchor);
			generalStatsSameResult.put(c, globalStatSameResult);
			//generalStatsNunesPattern.put(c,globalStatNunesPattern);
			generalStatsALL.put(c, globalStatALL);
			generalStatsNOA.put(c, globalStatNOA);
			generalStatsIVT.put(c, globalStatIVT);
			generalStatsRMGAVG.put(c, globalStatRMGAVG);
			generalStatsRMGCOMP.put(c, globalStatRMGCOMP);
			
			System.out.println("end c " + c);
	    }
		
		StringBuilder result = new StringBuilder();
		
		result.append(showGlobal("Pourcentage of sames alternatives find    : ", generalStatsSameResult, 0));
		result.append(showGlobal("Variance of anchor distribution           : ", generalStatsLabreucheAnchor,2));
		result.append(showGlobal("Standard deviation of anchor distribution : ", generalStatsLabreucheAnchor,1));
		result.append(showGlobal("Pourcentage of anchor ALL used            : ", generalStatsALL,0));
		result.append(showGlobal("Pourcentage of anchor NOA used            : ", generalStatsNOA,0));
		result.append(showGlobal("Pourcentage of anchor IVT used            : ", generalStatsIVT,0));
		result.append(showGlobal("Pourcentage of anchor RMGAVG used         : ", generalStatsRMGAVG,0));
		result.append(showGlobal("Pourcentage of anchor RMGCOMP used        : ", generalStatsRMGCOMP,0));
	
		System.out.println(result.toString());
	}
	
	private static String showDeviations(Map<Integer, List<Integer>> map) {
		StringBuilder result = new StringBuilder();

		//result.append(message + "	");
		
		for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
			if(!entry.getValue().isEmpty())
				result.append(new DecimalFormat("##.##").format(Stats.of(entry.getValue()).populationStandardDeviation()) +"	");
			else
				result.append("None" +"	");

		}
		
		result.append("\n");
		
		return result.toString();
	}

	private static String showVariances(Map<Integer, List<Integer>> map) {
		StringBuilder result = new StringBuilder();

		//result.append(message + "	");
		
		for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
			if(!entry.getValue().isEmpty())
				result.append(new DecimalFormat("##.##").format(Stats.of(entry.getValue()).populationVariance()) +"	");
			else
				result.append("None" +"	");

		}
		
		result.append("\n");
		
		return result.toString();
	}

	private static String showPourcents(Map<Integer, List<Integer>> map) {
		StringBuilder result = new StringBuilder();
		
		for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
			if(!entry.getValue().isEmpty())
				result.append(new DecimalFormat("##.##").format(Stats.meanOf(entry.getValue()) * 100)+"%"+ "	");
			else
				result.append(0 + "%" +"	");
		}
		
		result.append("\n");
		
		return result.toString();
	}
		
	private static String showGlobal(String message, Map<Integer,Map<Integer,List<Integer>>> map, int flag) {
		
		StringBuilder result = new StringBuilder();

		result.append(message + "\n");
		result.append("numbers alternatives  : ");
		
		for(int i = 2; i <= nbAlt; i+=2) {							
			result.append(i + "	");
		}
		
		result.append("\n");
				
		switch(flag) {
		
		case 0:
			for(Entry<Integer, Map<Integer,List<Integer>>> entry : map.entrySet()) {
				result.append("numbers critaria : " + entry.getKey() + "	" + showPourcents(entry.getValue()));
			}
			break;
			
		case 1:
			for(Entry<Integer, Map<Integer,List<Integer>>> entry : map.entrySet()) {
				result.append("numbers critaria : " + entry.getKey() + "	" + showDeviations(entry.getValue()));
			}
			break;
			
		case 2:
			for(Entry<Integer, Map<Integer,List<Integer>>> entry : map.entrySet()) {
				result.append("numbers critaria : " + entry.getKey() + "	" + showVariances(entry.getValue()));
			}
			break;
			
		default:
			throw new IllegalArgumentException();
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
