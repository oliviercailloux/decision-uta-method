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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Table;
import com.google.common.math.Stats;

import io.github.oliviercailloux.decision.arguer.labreuche.ArgumentGenerator;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheComputer;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.nunes.NunesTools;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class StatisticComputeTime {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Statistics.class);
	private static int nbAlt = 50;
	private static int stepCriteria = 2;
	private static int stepAlternatives = 5;
	private static int nbCrit = 6;
	private static int maxSim = 1000;

	public static void main(String[] args) {
			
		Table<Integer, Integer, Set<Long>> statsComputeTimeIVT = HashBasedTable.create();
			
		System.out.println("Starting simulation");

		for (int c = 2; c <= nbCrit; c += stepCriteria) {

			System.out.println("Starting with c = " + c);
				
			for (int a = 5; a <= nbAlt; a += stepAlternatives) {
					
				Set<Long> timesCompute = new LinkedHashSet<>();
					
				for (int j = 0; j < maxSim; j++) {
					System.out.print(".");
						
					ArgumentGenerator ag = new ArgumentGenerator(a, c);
					EvaluatedAlternative bestL;
					EvaluatedAlternative bestN;

					try {
							
						bestL = ag.findUniqueBest();
							
					} catch (IllegalArgumentException e3) {
							
						StringBuilder bld = new StringBuilder();
						bld.append(" BUG : " + e3.getMessage());
						LOGGER.debug(bld.toString());
						Iterator<EvaluatedAlternative> itr = ag.findBest().iterator();
						bestL = itr.next();
					}
						
					try {
							
						bestN = uniqueBestNunes(ag.getAlternatives(), ag.getWeights	());
						
					} catch (IllegalArgumentException e3) {
						
						StringBuilder bld = new StringBuilder();
						bld.append(" BUG : " + e3.getMessage());
						LOGGER.debug(bld.toString());
						Iterator<EvaluatedAlternative> itr = bestsNunes(ag.getAlternatives(), ag.getWeights())
								.iterator();
						bestN = itr.next();
					}
						
					if (!bestL.equals(bestN)) {
						LabreucheModel lm = new LabreucheModel(ag.getWeights());
						AlternativesComparison<LabreucheModel> altsComp = new AlternativesComparison<>(bestL, bestN, lm);
						LabreucheComputer lc = new LabreucheComputer(altsComp);
							
						if(lc.tryALLExplanation()){
							// do nothing
						}else {
							if(lc.tryNOAExplanation()) {
								//do nothing
							} else {
								long start = System.currentTimeMillis();
								if(lc.tryIVTExplanation()) {
									long time = System.currentTimeMillis() - start;
									timesCompute.add(time);
								}
							}
						}
					}
				}
				
				System.out.println("\n" +timesCompute.toString());
				
				statsComputeTimeIVT.put(a, c, timesCompute);
				
				System.out.println("\n Ending with i = " + a);
			}			

			System.out.println("Ending with c = " + c);
		}

		StringBuilder result = new StringBuilder();
			
		result.append(showStats("Average compute time of anchor IVT : ", statsComputeTimeIVT));

		System.out.println(result.toString());
	}

	private static Object showStats(String message, Table<Integer, Integer, Set<Long>> table) {
		StringBuilder result = new StringBuilder();

		result.append(message + "\n");
		result.append("numbers alternatives  : ");

		for (int i = 5; i <= nbAlt; i += stepAlternatives) {
			result.append(i + "	");
		}
		
		result.append("\n");
			
		for (Entry<Integer, Map<Integer, Set<Long>>> entry : table.columnMap().entrySet()) {
			result.append("numbers critaria : " + entry.getKey() + "	" + showLine(entry.getValue()));
		}

		result.append("\n");

		return result.toString();
	}
		
	private static String showLine(Map<Integer, Set<Long>> map) {
		StringBuilder result = new StringBuilder();

		for (Entry<Integer, Set<Long>> entry : map.entrySet()) {
			result.append(new DecimalFormat("##.##").format(Stats.meanOf(entry.getValue())) + "	");
		}
		
		result.append("\n");
		
		return result.toString();
	}

		

	private static Set<EvaluatedAlternative> bestsNunes(Set<EvaluatedAlternative> set, Map<Criterion, Double> weights) {
		Map<EvaluatedAlternative, Integer> scoreboard = new LinkedHashMap<>();
		
		Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradeoffs = NunesTools.computeTO(set, weights);
		
		for (EvaluatedAlternative a : set) {
			scoreboard.put(a, 0);
		}

		for (EvaluatedAlternative a : set) {
			for (EvaluatedAlternative b : set) {
				if (!a.equals(b)) {
					double scoreA = NunesTools.score(a, b, weights.keySet(), weights, tradeoffs);
					double scoreB = NunesTools.score(b, a, weights.keySet(), weights, tradeoffs);

					if (scoreA < scoreB) {
						int tmp = scoreboard.get(a);
						scoreboard.put(a, tmp + 1);
					}
				}
			}
		}

		int max = Collections.max(scoreboard.values());
		
		Set<EvaluatedAlternative> bests = new LinkedHashSet<>();

		for (Entry<EvaluatedAlternative, Integer> entry : scoreboard.entrySet()) {
			if (entry.getValue() == max) {
				bests.add(entry.getKey());
			}
		}
		
		return bests;
	}

	private static EvaluatedAlternative uniqueBestNunes(Set<EvaluatedAlternative> set, Map<Criterion, Double> weights) {
		Set<EvaluatedAlternative> bests = bestsNunes(set, weights);
		
		if (bests.size() == 1) {
			Iterator<EvaluatedAlternative> iter = bests.iterator();
			return iter.next();
		}

		throw new IllegalArgumentException();
	}
}
