package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.exception.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheTools {

	private LabreucheTools() {
		throw new IllegalStateException("Labreuche Tools class");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheTools.class);

	/**
	 * @param x
	 * @param w
	 * @return the score of the alternative x.
	 */
	public static double score(Alternative x, Map<Criterion, Double> w) {
		double score = 0.0;

		// Sum w_i * x_i
		for (Entry<Criterion, Double> c : w.entrySet()) {
			score += w.get(c.getKey()) * x.getEvaluations().get(c.getKey());
		}

		return score;
	}

	/* * * * * * * * * * * * Methods used by NOA anchor * * * * * * */

	/**
	 * @param subset
	 * @param size
	 * @return all permutations of size size in list.
	 */
	static List<List<Criterion>> allPi(List<Criterion> subset, int size) {
		List<List<Criterion>> cycles = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			if (i == 0) {
				List<Criterion> singleton;

				for (Criterion c : subset) {
					singleton = new ArrayList<>();
					singleton.add(c);
					cycles.add(singleton);
				}
			} else {
				List<List<Criterion>> copyCycles = new ArrayList<>(cycles);
				List<Criterion> newCycle;
				List<Criterion> listLight = new ArrayList<>(subset);

				for (List<Criterion> cycle : copyCycles) {
					listLight.removeAll(cycle);

					if (!listLight.isEmpty()) {
						for (Criterion c : listLight) {
							newCycle = add(cycle, c);
							cycles.add(newCycle);
						}
						cycles.remove(cycle);
					}

					listLight.addAll(cycle);
				}
			}
		}

		return cycles;
	}

	static List<Criterion> add(List<Criterion> l, Criterion c) {
		List<Criterion> newList = new ArrayList<>(l);
		newList.add(c);
		return newList;
	}

	/**
	 * @param subset
	 * @return all permutations possible in subset
	 */
	static List<List<Criterion>> allPi(List<Criterion> subset) {
		List<List<Criterion>> result = new ArrayList<>();
		List<List<Criterion>> tmp;

		for (int i = 2; i <= subset.size(); i++) {
			tmp = allPi(subset, i);

			for (List<Criterion> l : tmp)
				result.add(l);
		}

		return result;
	}

	static Couple<Double, List<Criterion>> dEU(List<Criterion> subset, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {

		double firstPart = 0.0;

		if (subset.isEmpty()) {
			return new Couple<>(firstPart, subset);
		}

		for (Criterion c : subset) {
			firstPart += w.get(c) * delta.get(c);
		}

		List<Criterion> bestMinPi = minimalPi(subset, w, delta);

		if (bestMinPi == null) {
			throw new NullArgumentException();
		}

		Map<Criterion, Double> piW = modifiedW(bestMinPi, w);

		double secondPart = 0.0;

		for (Criterion c : bestMinPi) {
			secondPart += piW.get(c) * delta.get(c);
		}

		double result = firstPart - secondPart;

		if (LOGGER.isDebugEnabled()) {
			StringBuilder bld = new StringBuilder();
			bld.append("Calling dEU for " + Utils.showCriteria(subset) + " : " + firstPart + " - " + secondPart + " = "
					+ result + " pi best : " + Utils.showCriteria(bestMinPi));
			LOGGER.debug(bld.toString());
		}

		return new Couple<>(result, bestMinPi);
	}

	/**
	 * @param subset
	 * @param w
	 * @param delta
	 * @return the permutation that minimize Sum pi(i) * delta_i, for all i in
	 *         subset
	 */
	static List<Criterion> minimalPi(List<Criterion> subset, Map<Criterion, Double> w, Map<Criterion, Double> delta) {

		List<List<Criterion>> pis = allPi(subset);
		Map<Criterion, Double> wModified;
		Double minPiValue = Double.MAX_VALUE;
		List<Criterion> minPi = null;
		Double sum = null;

		for (List<Criterion> pi : pis) {
			wModified = modifiedW(pi, w);
			sum = 0.0;

			for (Criterion c : subset) {
				sum += delta.get(c) * wModified.get(c);
			}

			if (sum < minPiValue) {
				minPiValue = sum;
				minPi = pi;
			}

			wModified.clear();
		}

		if (LOGGER.isDebugEnabled()) {
			StringBuilder bld = new StringBuilder();
			bld.append("Calling minimalPi for " + Utils.showCriteria(subset) + " minimalPi returned : "
					+ Utils.showCriteria(minPi) + " = " + minPiValue);
			LOGGER.debug(bld.toString());
		}

		return minPi;
	}

	/**
	 * @param permutation
	 * @param w
	 * @return the w vector modified by the permutation
	 */
	static Map<Criterion, Double> modifiedW(List<Criterion> permutation, Map<Criterion, Double> w) {
		Map<Criterion, Double> piW = new LinkedHashMap<>(w);
		double tmpPi = piW.get(permutation.get(permutation.size() - 1));

		for (int i = permutation.size() - 1; i >= 0; i--) { // weights vector modified by pi.
			if (i == 0)
				piW.put(permutation.get(0), tmpPi);
			else
				piW.put(permutation.get(i), piW.get(permutation.get(i - 1)));
		}

		return piW;
	}

	/**
	 * @param set
	 * @param size
	 * @return all the subset of size size in set
	 */
	static List<List<Criterion>> allSubset(List<Criterion> set, int size) {

		List<List<Criterion>> subsets = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			if (i == 0) {
				List<Criterion> singleton;

				for (Criterion c : set) {
					singleton = new ArrayList<>();
					singleton.add(c);
					subsets.add(singleton);
				}
			} else {
				List<List<Criterion>> copySubsets = new ArrayList<>(subsets);
				List<Criterion> newSubset;
				List<Criterion> setLight = new ArrayList<>(set);

				for (List<Criterion> subset : copySubsets) {

					int bornSup = set.indexOf(subset.get(subset.size() - 1));

					setLight.removeAll(set.subList(0, bornSup + 1));

					if (!setLight.isEmpty()) {
						for (Criterion c : setLight) {
							newSubset = add(subset, c);
							subsets.add(newSubset);
						}
						subsets.remove(subset);
					}

					subsets.remove(subset);

					setLight.addAll(set.subList(0, bornSup + 1));
				}
			}
		}

		return subsets;
	}

	/**
	 * @param set
	 * @return all the subset of set
	 */
	static List<List<Criterion>> allSubset(List<Criterion> set) {
		List<List<Criterion>> result = new ArrayList<>();
		List<List<Criterion>> tmp;

		for (int i = 2; i <= set.size(); i++) {
			tmp = allSubset(set, i);

			for (List<Criterion> l : tmp)
				result.add(l);
		}

		return result;
	}

	/* * * * * * * * * * * * Methods used by IVT anchors * * * * * * * * */

	/**
	 * @param lists
	 * @param l
	 * @return true if the intersection of lists and l is empty, and false otherwise
	 */
	static boolean isCapEmpty(List<List<Criterion>> lists, List<Criterion> l) {
		if (lists.isEmpty()) {
			return true;
		}

		if (l.isEmpty()) {
			return true;
		}

		List<Criterion> unionL = new ArrayList<>();

		for (List<Criterion> list : lists) {
			for (Criterion c : list) {
				if (!unionL.contains(c))
					unionL.add(c);
			}
		}

		for (Criterion c : l) {
			if (unionL.contains(c))
				return false;
		}

		return true;
	}

	/**
	 * @param list
	 * @param w
	 * @param delta
	 * @return list sorted in a sense of >_lexi
	 */
	static List<List<Criterion>> sortLexi(List<List<Criterion>> lists, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
		List<List<Criterion>> sorted = new ArrayList<>();
		int minSize = Integer.MAX_VALUE;

		List<List<Criterion>> tmp = new ArrayList<>();
		Map<Double, List<Criterion>> rankedSameSize = new LinkedHashMap<>();

		while (!lists.isEmpty()) {

			for (List<Criterion> l : lists) {
				if (l.size() < minSize)
					minSize = l.size();
			}

			for (List<Criterion> minSizeL : lists) {
				if (minSizeL.size() == minSize)
					tmp.add(minSizeL);
			}

			for (List<Criterion> l2 : tmp) {
				rankedSameSize.put(dEU(l2, w, delta).getLeft(), l2);
			}

			ArrayList<Double> keys = new ArrayList<>(rankedSameSize.keySet());
			Collections.sort(keys);
			Collections.reverse(keys);

			for (int i = 0; i < keys.size(); i++) {
				if (!sorted.contains(rankedSameSize.get(keys.get(i)))) {
					sorted.add(rankedSameSize.get(keys.get(i)));
				}
			}

			for (List<Criterion> delete : tmp) {
				lists.remove(delete);
			}

			rankedSameSize.clear();
			tmp.clear();
			minSize = Integer.MAX_VALUE;
		}

		return sorted;
	}

	/**
	 * @param a
	 * @param b
	 * @param weights
	 * @param deltas
	 * @return true if a is include in b in a sense of include_discri
	 */
	static boolean includeDiscri(List<List<Criterion>> a, List<List<Criterion>> b, Map<Criterion, Double> weights,
			Map<Criterion, Double> deltas) {

		if (a == null && b == null) {
			return false;
		}

		if (b == null) {
			return true;
		}

		if (a == null) {
			return false;
		}

		if (a.isEmpty() && b.isEmpty()) {
			return false;
		}

		if (!a.isEmpty() && b.isEmpty()) {
			return false;
		}

		if (!b.isEmpty() && a.isEmpty()) {
			return true;
		}

		if (a.size() > b.size())
			return false;

		if (a.size() == b.size()) {
			boolean flag = true;
			for (int i = 0; i < a.size(); i++) {
				if (a.get(i).size() != b.get(i).size())
					flag = false;
			}

			if (flag) {
				int k = 0;

				do {
					if (dEU(a.get(k), weights, deltas).getLeft() == dEU(b.get(k), weights, deltas).getLeft())
						k++;
					else
						break;
				} while (k < a.size());

				if (dEU(a.get(k), weights, deltas).getLeft() > dEU(b.get(k), weights, deltas).getLeft()) {
					return true;
				}
			}
		}

		else {

			int t = Math.min(a.size() - 1, b.size() - 1);

			int j = 0;
			int maxA = a.size() - 1;
			int maxB = b.size() - 1;

			do {
				if (a.get(maxA - j).size() == b.get(maxB - j).size())
					j++;
				else
					break;
			} while (j < t);

			if (j < t && a.get(maxA - j).size() < b.get(maxB - j).size())
				return true;

			if (maxA < maxB) {
				boolean flag = false;

				for (int i = 0; i <= maxA; i++) {
					if (a.get(maxA - i).size() != b.get(maxB - i).size())
						flag = false;
				}

				if (flag) {
					return true;
				}
			}
		}

		return false;
	}

	/* * * * * * * * * * Methods for build R Star used in IVT anchor * * * * * */

	/**
	 * @param l
	 * @param w
	 * @param delta
	 * @return a graph representing couples of criteria (C1,C2) in l as C1 is more
	 *         important than C2
	 */
	static ImmutableGraph<Criterion> couplesOfG(List<Criterion> l, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {

		MutableGraph<Criterion> result = GraphBuilder.directed().build();

		for (Criterion c1 : l) {
			for (Criterion c2 : l) {
				if (!c1.equals(c2) && delta.get(c1) < delta.get(c2) && w.get(c1) < w.get(c2)) {
					result.putEdge(c1, c2);
				}
			}
		}
		return ImmutableGraph.copyOf(result);
	}

	static ImmutableGraph<Criterion> rTopG(Graph<Criterion> graph) {

		boolean changeFlag = false;
		MutableGraph<Criterion> light;
		MutableGraph<Criterion> copy = Graphs.copyOf(graph);

		do {
			changeFlag = false;

			// c1 = (a b)
			for (EndpointPair<Criterion> c1 : copy.edges()) {
				light = Graphs.copyOf(copy);
				light.removeEdge(c1.nodeU(), c1.nodeV());

				// c2 = (c d)
				for (EndpointPair<Criterion> c2 : light.edges()) {
					// (a b) , (c d) => b=c and a!=d
					if (c1.nodeV().equals(c2.nodeU()) && !c1.nodeU().equals(c2.nodeV())
							&& !copy.hasEdgeConnecting(c1.nodeU(), c2.nodeV())) {
						copy.putEdge(c1.nodeU(), c2.nodeV());
						changeFlag = true;
					}

					// (c d) , (a b) => a=d and b!=d
					if (c1.nodeU().equals(c2.nodeV()) && !c1.nodeV().equals(c2.nodeU())
							&& !copy.hasEdgeConnecting(c1.nodeV(), c2.nodeU())) {
						copy.putEdge(c1.nodeV(), c2.nodeU());
						changeFlag = true;
					}
				}
			}

		} while (changeFlag);

		return ImmutableGraph.copyOf(copy);
	}

	static ImmutableGraph<Criterion> buildRStarG(Graph<Criterion> cpls) {

		Set<ImmutableGraph<Criterion>> subsets = allSubsetCoupleG(cpls);
		MutableGraph<Criterion> result = GraphBuilder.directed().build();
		ImmutableGraph<Criterion> tmpTop;

		for (ImmutableGraph<Criterion> cList : subsets) {
			tmpTop = rTopG(cList);

			if (tmpTop.equals(cList)) {
				for (EndpointPair<Criterion> c : cList.edges()) {
					result.putEdge(c.nodeU(), c.nodeV());
				}
			}
		}

		return ImmutableGraph.copyOf(result);
	}

	static Set<ImmutableGraph<Criterion>> allSubsetCoupleG(Graph<Criterion> set, int size) {

		Set<MutableGraph<Criterion>> subsets = new LinkedHashSet<>();

		for (int i = 0; i < size; i++) {
			if (i == 0) {
				MutableGraph<Criterion> singleton;

				for (EndpointPair<Criterion> edge : set.edges()) {
					singleton = GraphBuilder.directed().build();
					singleton.putEdge(edge.nodeU(), edge.nodeV());
					subsets.add(singleton);
				}

			} else {

				Set<MutableGraph<Criterion>> copySubsets = new LinkedHashSet<>(subsets);
				MutableGraph<Criterion> newSubset;
				MutableGraph<Criterion> setLight = Graphs.copyOf(set);

				for (MutableGraph<Criterion> subset : copySubsets) {

					for (EndpointPair<Criterion> edge : subset.edges()) {
						setLight.removeEdge(edge.nodeU(), edge.nodeV());
					}

					if (!setLight.edges().isEmpty()) {
						for (EndpointPair<Criterion> c : setLight.edges()) {
							newSubset = addCoupleG(subset, c);
							subsets.add(newSubset);
						}
						subsets.remove(subset);
					}
					subsets.remove(subset);

					for (EndpointPair<Criterion> edge : subset.edges())
						setLight.putEdge(edge.nodeU(), edge.nodeV());
				}
			}
		}

		Set<ImmutableGraph<Criterion>> result = new LinkedHashSet<>();

		for (MutableGraph<Criterion> g : subsets) {
			result.add(ImmutableGraph.copyOf(g));
		}

		return result;
	}

	static Set<ImmutableGraph<Criterion>> allSubsetCoupleG(Graph<Criterion> cpls) {

		Set<ImmutableGraph<Criterion>> result = new LinkedHashSet<>();
		Set<ImmutableGraph<Criterion>> tmp;

		for (int i = 1; i <= cpls.edges().size(); i++) {
			tmp = allSubsetCoupleG(cpls, i);

			for (ImmutableGraph<Criterion> g : tmp)
				result.add(g);
		}

		return result;
	}

	static MutableGraph<Criterion> addCoupleG(Graph<Criterion> graph, EndpointPair<Criterion> edge) {

		MutableGraph<Criterion> newGraph = Graphs.copyOf(graph);
		newGraph.putEdge(edge.nodeU(), edge.nodeV());

		return newGraph;
	}

}
