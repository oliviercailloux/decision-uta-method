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
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.nunes.NunesTools;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class Statistics {

	private static final Logger LOGGER = LoggerFactory.getLogger(Statistics.class);
	private static int nbAlt = 50;
	private static int stepCriteria = 2;
	private static int stepAlternatives =  5;
	private static int nbCrit = 10;
	private static int maxSim = 1000;

	public static void main(String[] args) {
		
		Table<Integer,Integer, Multiset<Anchor>> statsAnchor = HashBasedTable.create();
		Table<Integer, Integer, Integer> statsSameResult = HashBasedTable.create();

		// Table<Integer,Integer, Multiset<Pattern>> statsPattern = new
		// HashBasedTable.create();
		
		System.out.println("Starting simulation");

		for (int c = 2; c <= nbCrit; c += stepCriteria) {

			System.out.println("Starting with c = " + c);
			
			for (int a = 5; a <= nbAlt; a += stepAlternatives) {

				Multiset<Anchor> statAnchor = HashMultiset.create();
				//Multiset<Pattern> statPattern = HashMultiset.create();

				int cptSame = 0;
				
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
						
						bestN = uniqueBestNunes(ag.getAlternatives(), ag.getWeights());
					
					} catch (IllegalArgumentException e3) {
					
						StringBuilder bld = new StringBuilder();
						bld.append(" BUG : " + e3.getMessage());
						LOGGER.debug(bld.toString());
						Iterator<EvaluatedAlternative> itr = bestsNunes(ag.getAlternatives(), ag.getWeights())
								.iterator();
						bestN = itr.next();
					}
					
					if (!bestL.equals(bestN)) {
						LabreucheOutput lbo = ag.compare(bestL, bestN);
						statAnchor.add(lbo.getAnchor());
					}
					else { cptSame++; }
				}

				statsAnchor.put(a, c, statAnchor);
				statsSameResult.put(a, c, cptSame);
				System.out.println("\n Ending with a = " + a);
			}			

			System.out.println("Ending with c = " + c);
		}

		StringBuilder result = new StringBuilder();
		
		result.append(showStatsSameResult("Pourcentage of sames alternatives find    : ", statsSameResult));
		result.append(showGlobal("Pourcentage of anchor ALL used            : ", statsAnchor, Anchor.ALL, statsSameResult));
		result.append(showGlobal("Pourcentage of anchor NOA used            : ", statsAnchor, Anchor.NOA, statsSameResult));
		result.append(showGlobal("Pourcentage of anchor IVT used            : ", statsAnchor, Anchor.IVT, statsSameResult));
		result.append(showGlobal("Pourcentage of anchor RMGAVG used         : ", statsAnchor, Anchor.RMGAVG, statsSameResult));
		result.append(showGlobal("Pourcentage of anchor RMGCOMP used        : ", statsAnchor, Anchor.RMGCOMP, statsSameResult));

		System.out.println(result.toString());
	}

	private static String showStatsSameResult(String message, Table<Integer, Integer, Integer> table) {
		StringBuilder result = new StringBuilder();

		result.append(message + "\n");
		result.append("numbers alternatives  : ");

		for (int i = 5; i <= nbAlt; i += stepAlternatives) {
			result.append(i + "	");
		}

		result.append("\n");
		
		for (Entry<Integer, Map<Integer, Integer>> entry : table.columnMap().entrySet()) {
			result.append("numbers critaria : " + entry.getKey() + "	" + showLine(entry.getValue()));
		}

		result.append("\n");

		return result.toString();
	}
	
	private static String showLine(Map<Integer, Integer> map) {
		StringBuilder result = new StringBuilder();

		for (Entry<Integer, Integer> entry : map.entrySet()) {
			result.append(new DecimalFormat("##.##").format((entry.getValue() * 100.0) / maxSim) + "	");
		}

		result.append("\n");

		return result.toString();
	}

	private static String showPourcents(Map<Integer, Multiset<Anchor>> map, Anchor anchor, Map<Integer, Integer> sameResult) {
		StringBuilder result = new StringBuilder();

		for (Entry<Integer, Multiset<Anchor>> entry : map.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				int occurence = entry.getValue().count(anchor);
				result.append(new DecimalFormat("##.##").format(occurence / (  maxSim - sameResult.get(entry.getKey()) * 1.0) * 100) + "	");
			}else {
				result.append(0 + "	");
			}
		}

		result.append("\n");

		return result.toString();
	}
	
	private static String showGlobal(String message, Table<Integer,Integer,Multiset<Anchor>> table, Anchor anchor, Table<Integer, Integer, Integer> statsSameResult) {

		StringBuilder result = new StringBuilder();

		result.append(message + "\n");
		result.append("numbers alternatives  : ");

		for (int i = 5; i <= nbAlt; i += stepAlternatives) {
			result.append(i + "	");
		}

		result.append("\n");
		
		for (Entry<Integer, Map<Integer, Multiset<Anchor>>> entry : table.columnMap().entrySet()) {
			result.append("numbers critaria : " + entry.getKey() + "	" + showPourcents(entry.getValue(),anchor, statsSameResult.column(entry.getKey())));
		}

		result.append("\n");

		return result.toString();
	}

	private static Set<EvaluatedAlternative> bestsNunes(Set<EvaluatedAlternative> set, Map<Criterion, Double> weights) {
		Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradeoffs = NunesTools.computeTO(set, weights);

		Iterator<EvaluatedAlternative> itr = set.iterator();
		EvaluatedAlternative currentBest = itr.next();
		
		Set<EvaluatedAlternative> bests = new LinkedHashSet<>();
		bests.add(currentBest);
		
		while(itr.hasNext()) {
			EvaluatedAlternative next = itr.next();
			double scoreBest = NunesTools.score(currentBest, next, weights.keySet(), weights, tradeoffs);
			double scoreNext = NunesTools.score(next, currentBest, weights.keySet(), weights, tradeoffs);

			if(scoreNext == scoreBest) {
				bests.add(next);
			}
			
			if(scoreNext < scoreBest) {
				bests.clear();
				currentBest = next;
				bests.add(next);
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
