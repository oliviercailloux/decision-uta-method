package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Tools {

	private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

	static Double score(Alternative x, Map<Criterion, Double> w) {
		Double score = 0.0;

		// Sum w_i * x_i
		for (Criterion c : w.keySet())
			score += w.get(c) * x.getEvaluations().get(c);

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

			LOGGER.debug("Current Min part of " + Utils.showCriteria(pi) + " = " + sum);

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
					LOGGER.debug("######## changing subset ###########");

					int born_sup = set.indexOf(subset.get(subset.size() - 1));

					LOGGER.debug(" subset " + Utils.showCriteria(subset));
					LOGGER.debug(" last element " + subset.get(subset.size() - 1));
					LOGGER.debug(" born sup " + born_sup);

					LOGGER.debug(" before " + Utils.showCriteria(set_light));
					LOGGER.debug("set : " + Utils.showCriteria(set));
					LOGGER.debug(" sublist " + Utils.showCriteria(set.subList(0, born_sup + 1)));
					// set_light.removeAll(subset);

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

	// return true if a is include in b in a sense en include_discri
	static boolean includeDiscri(List<List<Criterion>> a, List<List<Criterion>> b, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
		boolean result = true;

		if (a.isEmpty())
			return true;

		if (b.isEmpty()) {
			if (!a.isEmpty())
				return false;
			return true;
		}

		if (a.size() > b.size())
			return false;

		if (a.size() == b.size()) {
			boolean flag = true;
			for (int i = 0; i < a.size(); i++) {
				if (a.get(i).size() != a.get(i).size())
					flag = false;
			}

			if (flag) {
				int k = 0;

				do {
					LOGGER.debug(k + "");

					if (d_eu(a.get(k), w, delta) == d_eu(b.get(k), w, delta))
						k++;
					else
						break;
					LOGGER.debug(k + "");
				} while (k < a.size());

				if (d_eu(a.get(k), w, delta).getLeft() > d_eu(b.get(k), w, delta).getLeft())
					result = true;
				else
					result = false;
			} else
				result = false;
		}

		else {

			int t = Math.min(a.size(), b.size());

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
			else
				result = false;

			if (max_a < max_b) {
				boolean flag = false;
				for (int i = 0; i <= max_a; i++) {
					if (a.get(max_a - i).size() != b.get(max_b - i).size())
						flag = false;
				}

				if (flag)
					return true;
				else
					result = false;
			}
		}

		return result;
	}

	/*
	 * * * * * * * * * * * Methods for building * R* used in IVT anchor * * * * * *
	 * * * * *
	 */

	static List<Couple<Criterion, Criterion>> couples_of(List<Criterion> l, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {

		List<Couple<Criterion, Criterion>> result = new ArrayList<>();

		for (Criterion c1 : l) {
			for (Criterion c2 : l) {
				if (!c1.equals(c2)) {
					if (delta.get(c1) < delta.get(c2) && w.get(c1) < w.get(c2)) {

						result.add(new Couple<>(c1, c2));
					}
				}
			}
		}
		return result;
	}

	static List<Couple<Criterion, Criterion>> r_top(List<Couple<Criterion, Criterion>> list_c) {
		boolean change_flag = false; // -> list_c have a new couple added, falg = true;

		List<Couple<Criterion, Criterion>> copy = new ArrayList<>(list_c);
		List<Couple<Criterion, Criterion>> light = new ArrayList<>(); // -> used to avoid to check the same couples
		Couple<Criterion, Criterion> tmp = null;

		do {
			change_flag = false;

			// c1 = (a b)
			for (Couple<Criterion, Criterion> c1 : copy) {
				light = new ArrayList<>(copy);
				light.remove(c1);

				// c2 = (c d)
				for (Couple<Criterion, Criterion> c2 : light) {
					// (a b) , (c d) => b=c and a!=d
					if (c1.getRight().equals(c2.getLeft()) && !c1.getLeft().equals(c2.getRight())) {
						tmp = new Couple<>(c1.getLeft(), c2.getRight());
						if (!copy.contains(tmp)) {
							copy.add(tmp);
							change_flag = true;
						}
					}
					// (c d) , (a b) => a=d and b!=d
					if (c1.getLeft().equals(c2.getRight()) && !c1.getRight().equals(c2.getLeft())) {
						tmp = new Couple<>(c1.getRight(), c2.getLeft());
						if (!copy.contains(tmp)) {
							copy.add(tmp);
							change_flag = true;
						}
					}
				}
			}
			light.clear();

		} while (change_flag);

		return copy;
	}

	static ImmutableGraph<Criterion> buildRStar(List<Couple<Criterion, Criterion>> cpls) {
		Set<List<Couple<Criterion, Criterion>>> subsets = allSubsetCouple(cpls);
		MutableGraph<Criterion> result = GraphBuilder.directed().build();

		List<Couple<Criterion, Criterion>> tmp_top;

		for (List<Couple<Criterion, Criterion>> c_list : subsets) {
			tmp_top = r_top(c_list);

			if (tmp_top.equals(c_list)) {
				for (Couple<Criterion, Criterion> c : c_list) {
					result.putEdge(c.getLeft(), c.getRight());
				}
			}
		}

		return ImmutableGraph.copyOf(result);
	}

	static Set<List<Couple<Criterion, Criterion>>> allSubsetCouple(List<Couple<Criterion, Criterion>> set, int size) {
		Set<List<Couple<Criterion, Criterion>>> subsets = new LinkedHashSet<>();
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				List<Couple<Criterion, Criterion>> singleton;

				for (int j = 0; j < set.size(); j++) {
					singleton = new ArrayList<>();
					singleton.add(set.get(j));
					subsets.add(singleton);
				}
			} else {
				List<List<Couple<Criterion, Criterion>>> copy_subsets = new ArrayList<>(subsets);
				List<Couple<Criterion, Criterion>> new_subset = new ArrayList<>();
				List<Couple<Criterion, Criterion>> set_light = new ArrayList<>(set);

				for (List<Couple<Criterion, Criterion>> subset : copy_subsets) {
					int born_sup = set.indexOf(subset.get(subset.size() - 1));
					set_light.removeAll(set.subList(0, born_sup + 1));

					if (set_light.size() >= 1) {
						for (Couple<Criterion, Criterion> c : set_light) {
							new_subset = addCouple(subset, c);
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

	static Set<List<Couple<Criterion, Criterion>>> allSubsetCouple(List<Couple<Criterion, Criterion>> cpls) {
		Set<List<Couple<Criterion, Criterion>>> result = new LinkedHashSet<>();

		Set<List<Couple<Criterion, Criterion>>> tmp;

		for (int i = 1; i <= cpls.size(); i++) {
			tmp = allSubsetCouple(cpls, i);

			for (List<Couple<Criterion, Criterion>> l : tmp)
				result.add(l);
		}

		return result;
	}

	static List<Couple<Criterion, Criterion>> addCouple(List<Couple<Criterion, Criterion>> l,
			Couple<Criterion, Criterion> c) {
		List<Couple<Criterion, Criterion>> new_l = new ArrayList<>(l);
		new_l.add(c);
		return new_l;
	}

}
