package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

	public static double score(Alternative x, Map<Criterion, Double> w) {
		double score = 0.0;

		// Sum w_i * x_i
		for (Criterion c : w.keySet()) {
			score += w.get(c) * x.getEvaluations().get(c);
		}

		return score;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * Methods used by NOA anchor * * * * * *
	 * * * * * * * * * * * * * *
	 */

	// return all permutations of size "size" in list.
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
				List<List<Criterion>> copy_cycles = new ArrayList<>(cycles);
				List<Criterion> new_cycle = new ArrayList<>();
				List<Criterion> list_light = new ArrayList<>(subset);

				for (List<Criterion> cycle : copy_cycles) {
					list_light.removeAll(cycle);

					if (list_light.size() >= 1) {
						for (Criterion c : list_light) {
							new_cycle = add(cycle, c);
							// if(!cycles.contains(new_cycle))
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

	static List<Criterion> add(List<Criterion> l, Criterion c) {
		List<Criterion> new_l = new ArrayList<>(l);
		new_l.add(c);
		return new_l;
	}

	// return all permutations of list
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

	static Couple<Double, List<Criterion>> d_eu(List<Criterion> subset, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {

		double first_part = 0.0;

		if (subset.isEmpty())
			return new Couple<>(first_part, subset);

		for (Criterion c : subset)
			first_part += w.get(c) * delta.get(c);

		List<Criterion> best_min_pi = pi_min(subset, w, delta);

		if (best_min_pi == null) {
			throw new NullArgumentException();
		}

		Map<Criterion, Double> pi_w = modified_w(best_min_pi, w);

		Double min_part = 0.0;

		for (Criterion c : best_min_pi)
			min_part += pi_w.get(c) * delta.get(c);

		Double result = first_part - min_part;

		LOGGER.debug("Calling d_eu for " + Utils.showCriteria(subset) + " : " + first_part + " - " + min_part + " = "
				+ result + " pi best : " + Utils.showCriteria(best_min_pi));

		return new Couple<>(result, best_min_pi);
	}

	// return the permutation that minimize Sum pi(i) delta_i, for all i in "list"
	static List<Criterion> pi_min(List<Criterion> subset, Map<Criterion, Double> w, Map<Criterion, Double> delta) {
		List<List<Criterion>> pis = allPi(subset);
		Map<Criterion, Double> w_modified = new LinkedHashMap<>();
		Double min_pi_value = Double.MAX_VALUE;
		List<Criterion> min_pi = null;
		Double sum = null;

		for (List<Criterion> pi : pis) {
			w_modified = modified_w(pi, w);

			sum = 0.0;

			for (Criterion c : subset)
				sum += delta.get(c) * w_modified.get(c);

			if (sum < min_pi_value) {
				min_pi_value = sum;
				min_pi = pi;
			}

			w_modified.clear();
			// w_modified = new LinkedHashMap<Criterion,Double>(this.weights);
		}

		LOGGER.debug("Calling pi_min for " + Utils.showCriteria(subset) + " pi_min returned : "
				+ Utils.showCriteria(min_pi) + " = " + min_pi_value);

		return min_pi;
	}

	// returned weight vector modified by cycle
	static Map<Criterion, Double> modified_w(List<Criterion> cycle, Map<Criterion, Double> w) {
		Map<Criterion, Double> pi_w = new LinkedHashMap<>(w);
		Double tmp_pi = 0.0;

		tmp_pi = pi_w.get(cycle.get(cycle.size() - 1));

		for (int i = cycle.size() - 1; i >= 0; i--) { // weights vector modified by pi.
			if (i == 0)
				pi_w.put(cycle.get(0), tmp_pi);
			else
				pi_w.put(cycle.get(i), pi_w.get(cycle.get(i - 1)));
		}

		return pi_w;
	}

	// return all the subset of size "size" in set
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
				List<List<Criterion>> copy_subsets = new ArrayList<>(subsets);
				List<Criterion> new_subset = new ArrayList<>();
				List<Criterion> set_light = new ArrayList<>(set);

				for (List<Criterion> subset : copy_subsets) {

					int born_sup = set.indexOf(subset.get(subset.size() - 1));

					set_light.removeAll(set.subList(0, born_sup + 1));

					LOGGER.debug(" after " + Utils.showCriteria(set_light));

					if (set_light.size() >= 1) {
						for (Criterion c : set_light) {
							new_subset = add(subset, c);
							subsets.add(new_subset);

							LOGGER.debug("adding subset " + Utils.showCriteria(new_subset));
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
	static List<List<Criterion>> allSubset(List<Criterion> immutableSet) {
		List<List<Criterion>> result = new ArrayList<>();
		List<List<Criterion>> tmp;

		for (int i = 2; i <= immutableSet.size(); i++) {
			tmp = allSubset(immutableSet, i);

			for (List<Criterion> l : tmp)
				result.add(l);
		}

		return result;
	}

	/*
	 * * * * * * * * * * * * * * Methods used by IVT anchors * * * * * * * * * * * *
	 * * * *
	 */

	// return true if the intersection of "list_l" and "l" is empty, and false
	// otherwise
	static boolean isCapEmpty(List<List<Criterion>> list_l, List<Criterion> l) {
		if (list_l.isEmpty()) {
			return true;
		}

		if (l.isEmpty()) {
			return true;
		}

		List<Criterion> union_l = new ArrayList<>();

		for (List<Criterion> list : list_l) {
			for (Criterion c : list) {
				if (!union_l.contains(c))
					union_l.add(c);
			}
		}

		for (Criterion c : l) {
			if (union_l.contains(c))
				return false;
		}

		return true;
	}

	// return list sorted in a sense of lexi
	static List<List<Criterion>> sortLexi(List<List<Criterion>> list, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
		List<List<Criterion>> sorted = new ArrayList<>();
		int min_size = Integer.MAX_VALUE;

		List<List<Criterion>> tmp = new ArrayList<>();
		// Map<List<Criterion>,Double> rankedSameSize = new
		// HashMap<List<Criterion>,Double>();
		Map<Double, List<Criterion>> rankedSameSize = new LinkedHashMap<>();

		while (!list.isEmpty()) {
			LOGGER.debug("Size of list = " + list.size());

			for (List<Criterion> l : list) {
				if (l.size() < min_size)
					min_size = l.size();
			}

			LOGGER.debug("Size min find : " + min_size);

			for (List<Criterion> min_size_l : list) {
				if (min_size_l.size() == min_size)
					tmp.add(min_size_l);
			}

			LOGGER.debug("Size of tmp = " + tmp.size());

			for (List<Criterion> l2 : tmp) {

				LOGGER.debug("set " + Utils.showCriteria(l2) + " d_eu = " + d_eu(l2, w, delta));

				rankedSameSize.put(d_eu(l2, w, delta).getLeft(), l2);
			}

			ArrayList<Double> keys = new ArrayList<>(rankedSameSize.keySet());
			Collections.sort(keys);
			Collections.reverse(keys);

			LOGGER.debug("Size of keys = " + keys.size());

			for (int i = 0; i < keys.size(); i++) {
				if (!sorted.contains(rankedSameSize.get(keys.get(i))))
					sorted.add(rankedSameSize.get(keys.get(i)));
			}

			for (List<Criterion> delete : tmp)
				list.remove(delete);

			LOGGER.debug("Size of List after remove = " + list.size());

			rankedSameSize.clear();
			tmp.clear();
			min_size = Integer.MAX_VALUE;
		}

		return sorted;
	}

	/**
	 * to do : redefine this method
	 */
	// return true if a is include in b in a sense en include_discri
	static boolean includeDiscri(List<List<Criterion>> a, List<List<Criterion>> b, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {

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
					LOGGER.debug(k + "");

					if (d_eu(a.get(k), w, delta).getLeft() == d_eu(b.get(k), w, delta).getLeft())
						k++;
					else
						break;
					LOGGER.debug(k + "");
				} while (k < a.size());

				if (d_eu(a.get(k), w, delta).getLeft() > d_eu(b.get(k), w, delta).getLeft()) {
					return true;
				}
			}
		}

		else {

			int t = Math.min(a.size() - 1, b.size() - 1);

			int j = 0;
			int max_a = a.size() - 1;
			int max_b = b.size() - 1;

			do {
				if (a.get(max_a - j).size() == b.get(max_b - j).size())
					j++;
				else
					break;
			} while (j < t);

			if (j < t && a.get(max_a - j).size() < b.get(max_b - j).size())
				return true;

			if (max_a < max_b) {
				boolean flag = false;

				for (int i = 0; i <= max_a; i++) {
					if (a.get(max_a - i).size() != b.get(max_b - i).size())
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

	static ImmutableGraph<Criterion> couples_ofG(List<Criterion> l, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {

		MutableGraph<Criterion> result = GraphBuilder.directed().build();

		for (Criterion c1 : l) {
			for (Criterion c2 : l) {
				if (!c1.equals(c2)) {
					if (delta.get(c1) < delta.get(c2) && w.get(c1) < w.get(c2)) {
						result.putEdge(c1, c2);
					}
				}
			}
		}
		return ImmutableGraph.copyOf(result);
	}

	static ImmutableGraph<Criterion> r_topG(Graph<Criterion> graph) {
		boolean change_flag = false; // -> list_c have a new couple added, falg = true;

		MutableGraph<Criterion> copy = Graphs.copyOf(graph);
		MutableGraph<Criterion> light; // -> used to avoid to check the same couples

		do {
			change_flag = false;

			// c1 = (a b)
			for (EndpointPair<Criterion> c1 : copy.edges()) {
				light = Graphs.copyOf(copy);
				light.removeEdge(c1.nodeU(), c1.nodeV());

				// c2 = (c d)
				for (EndpointPair<Criterion> c2 : light.edges()) {
					// (a b) , (c d) => b=c and a!=d
					if (c1.nodeV().equals(c2.nodeU()) && !c1.nodeU().equals(c2.nodeV())) {
						if (!copy.hasEdgeConnecting(c1.nodeU(), c2.nodeV())) {
							copy.putEdge(c1.nodeU(), c2.nodeV());
							change_flag = true;
						}
					}
					// (c d) , (a b) => a=d and b!=d
					if (c1.nodeU().equals(c2.nodeV()) && !c1.nodeV().equals(c2.nodeU())) {
						if (!copy.hasEdgeConnecting(c1.nodeV(), c2.nodeU())) {
							copy.putEdge(c1.nodeV(), c2.nodeU());
							change_flag = true;
						}
					}
				}
			}

		} while (change_flag);

		return ImmutableGraph.copyOf(copy);
	}

	static ImmutableGraph<Criterion> buildRStarG(Graph<Criterion> cpls) {
		Set<ImmutableGraph<Criterion>> subsets = allSubsetCoupleG(cpls);
		MutableGraph<Criterion> result = GraphBuilder.directed().build();

		ImmutableGraph<Criterion> tmp_top;

		for (ImmutableGraph<Criterion> c_list : subsets) {
			tmp_top = r_topG(c_list);

			if (tmp_top.equals(c_list)) {
				for (EndpointPair<Criterion> c : c_list.edges()) {
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

				Set<MutableGraph<Criterion>> copy_subsets = new LinkedHashSet<>(subsets);
				MutableGraph<Criterion> new_subset = GraphBuilder.directed().build();
				MutableGraph<Criterion> set_light = Graphs.copyOf(set);

				for (MutableGraph<Criterion> subset : copy_subsets) {

					for (EndpointPair<Criterion> edge : subset.edges()) {
						set_light.removeEdge(edge.nodeU(), edge.nodeV());
					}

					if (set_light.edges().size() >= 1) {
						for (EndpointPair<Criterion> c : set_light.edges()) {
							new_subset = addCoupleG(subset, c);
							subsets.add(new_subset);
						}
						subsets.remove(subset);
					}
					subsets.remove(subset);

					for (EndpointPair<Criterion> edge : subset.edges())
						set_light.putEdge(edge.nodeU(), edge.nodeV());
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
		MutableGraph<Criterion> new_graph = Graphs.copyOf(graph);
		new_graph.putEdge(edge.nodeU(), edge.nodeV());

		return new_graph;
	}

}
