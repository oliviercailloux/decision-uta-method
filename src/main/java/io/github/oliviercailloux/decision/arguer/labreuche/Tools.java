package io.github.oliviercailloux.decision.arguer.labreuche;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Tools {

	public static Double score(Alternative x, Map<Criterion, Double> w) {
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
	public static List<List<Criterion>> allPi(List<Criterion> subset, int size) {
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

	public static List<Criterion> add(List<Criterion> l, Criterion c) {
		List<Criterion> new_l = new ArrayList<>(l);
		new_l.add(c);
		return new_l;
	}

	// return all permutations of list
	public static List<List<Criterion>> allPi(List<Criterion> subset) {
		List<List<Criterion>> result = new ArrayList<>();
		List<List<Criterion>> tmp;

		for (int i = 2; i <= subset.size(); i++) {
			tmp = allPi(subset, i);

			for (List<Criterion> l : tmp)
				result.add(l);
		}

		return result;
	}

	public static Couple<Double, List<Criterion>> d_eu(List<Criterion> subset, int flag, Map<Criterion, Double> w,
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

		// System.out.println("Calling d_eu for "+ this.showCriteria(list) +" : "+
		// first_part +" - "+ min_part +" = " + result + " pi best : "+
		// this.showCriteria(best_min_pi) );

		if (flag == 0)
			return new Couple<>(first_part, best_min_pi);

		if (flag == 1)
			return new Couple<>(min_part, best_min_pi);

		return new Couple<>(result, best_min_pi);
	}

	// return the permutation that minimize Sum pi(i) delta_i, for all i in "list"
	public static List<Criterion> pi_min(List<Criterion> subset, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
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

			// System.out.println("Current Min part of "+ this.showCriteria(pi) + " = " +
			// sum);

			if (sum < min_pi_value) {
				min_pi_value = sum;
				min_pi = pi;
			}

			w_modified.clear();
			// w_modified = new LinkedHashMap<Criterion,Double>(this.weights);
		}

		// System.out.println("Calling pi_min for " + this.showCriteria(list) +" pi_min
		// returned : " + this.showCriteria(min_pi) + " = " + min_pi_value);
		return min_pi;
	}

	// returned weight vector modified by cycle
	public static Map<Criterion, Double> modified_w(List<Criterion> cycle, Map<Criterion, Double> w) {
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
	public static List<List<Criterion>> allSubset(List<Criterion> set, int size) {
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
					// System.out.println("######## changing subset ###########");
					int born_sup = set.indexOf(subset.get(subset.size() - 1));

					// System.out.println(" subset " + this.showCriteria(subset));
					// System.out.println(" last element " + subset.get(subset.size()-1));
					// System.out.println(" born sup " + born_sup);

					// System.out.println(" before " + this.showCriteria(set_light));
					// System.out.println("set : " + this.showCriteria(set));
					// System.out.println(" sublist " + this.showCriteria(set.subList(0,born_sup +
					// 1)));
					// set_light.removeAll(subset);
					set_light.removeAll(set.subList(0, born_sup + 1));
					// System.out.println(" after " + this.showCriteria(set_light));

					if (set_light.size() >= 1) {
						for (Criterion c : set_light) {
							new_subset = add(subset, c);
							subsets.add(new_subset);
							// System.out.println("adding subset " + this.showCriteria(new_subset));
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
	public static List<List<Criterion>> allSubset(List<Criterion> immutableSet) {
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
	 * * * * * * * * * * * * * * * * * * * * * * * Methods used by IVT anchors * * *
	 * * * * * * * * * * * * * * * * * * * *
	 */

	// return true if the intersection of "list_l" and "l" is empty, and false
	// otherwise
	public static boolean isCapEmpty(List<List<Criterion>> list_l, List<Criterion> l) {
		if (list_l.isEmpty())
			return true;

		if (l.isEmpty())
			return true;

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
	public static List<List<Criterion>> sortLexi(List<List<Criterion>> list, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
		List<List<Criterion>> sorted = new ArrayList<>();
		int min_size = Integer.MAX_VALUE;

		List<List<Criterion>> tmp = new ArrayList<>();
		// Map<List<Criterion>,Double> rankedSameSize = new
		// HashMap<List<Criterion>,Double>();
		Map<Double, List<Criterion>> rankedSameSize = new HashMap<>();

		while (!list.isEmpty()) {
			// System.out.println("Size of list = "+ list.size());

			for (List<Criterion> l : list) {
				if (l.size() < min_size)
					min_size = l.size();
			}

			// System.out.println("Size min find : "+ min_size);

			for (List<Criterion> min_size_l : list) {
				if (min_size_l.size() == min_size)
					tmp.add(min_size_l);
			}

			// System.out.println("Size of tmp = "+ tmp.size());

			for (List<Criterion> l2 : tmp) {
				// System.out.println("set "+ showCriteria(l2)+" d_eu = "+d_eu(l2));
				rankedSameSize.put(d_eu(l2, 5, w, delta).getLeft(), l2);
			}

			ArrayList<Double> keys = new ArrayList<>(rankedSameSize.keySet());
			Collections.sort(keys);
			Collections.reverse(keys);

			// System.out.println("Size of keys = "+ keys.size());

			for (int i = 0; i < keys.size(); i++) {
				if (!sorted.contains(rankedSameSize.get(keys.get(i))))
					sorted.add(rankedSameSize.get(keys.get(i)));
			}

			for (List<Criterion> delete : tmp)
				list.remove(delete);

			// System.out.println("Size of List after remove = "+ list.size());
			rankedSameSize.clear();
			tmp.clear();
			min_size = Integer.MAX_VALUE;
		}

		return sorted;
	}

	// return true if a is include in b in a sense en include_discri
	public static boolean includeDiscri(List<List<Criterion>> a, List<List<Criterion>> b, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
		boolean result = true;

		if (a.isEmpty())
			return true;

		if (b.isEmpty()) {
			if (!a.isEmpty())
				return false;
			else
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
					// System.out.println(k);

					if (d_eu(a.get(k), 5, w, delta) == d_eu(b.get(k), 5, w, delta))
						k++;
					else
						break;
					// System.out.println(k);
				} while (k < a.size());

				if (d_eu(a.get(k), 5, w, delta).getLeft() > d_eu(b.get(k), 5, w, delta).getLeft())
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
	 * * * * * * * * * * * * * * * * Methods for building * R* used in IVT anchor *
	 * * * * * * * * * * * * * * *
	 */

	public static List<Couple<Criterion, Criterion>> couples_of(List<Criterion> l, Map<Criterion, Double> w,
			Map<Criterion, Double> delta) {
		List<Couple<Criterion, Criterion>> cpl = new ArrayList<>();

		for (Criterion c1 : l) {
			for (Criterion c2 : l) {
				if (!c1.equals(c2)) {
					if (delta.get(c1) < delta.get(c2) && w.get(c1) < w.get(c2)) {
						cpl.add(new Couple<>(c1, c2));
					}
				}
			}
		}
		return cpl;
	}

	public static List<Couple<Criterion, Criterion>> r_top(List<Couple<Criterion, Criterion>> list_c) {
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

	public static List<Couple<Criterion, Criterion>> r_star(List<Couple<Criterion, Criterion>> cpls) {
		List<List<Couple<Criterion, Criterion>>> subsets = allSubsetCouple(cpls);
		List<Couple<Criterion, Criterion>> result = new ArrayList<>();
		List<Couple<Criterion, Criterion>> tmp_top;

		for (List<Couple<Criterion, Criterion>> c_list : subsets) {
			tmp_top = r_top(c_list);

			if (tmp_top.equals(c_list)) {
				for (Couple<Criterion, Criterion> c : c_list) {
					if (!result.contains(c))
						result.add(c);
				}
			}
		}
		return result;
	}

	public static List<List<Couple<Criterion, Criterion>>> allSubsetCouple(List<Couple<Criterion, Criterion>> set,
			int size) {
		List<List<Couple<Criterion, Criterion>>> subsets = new ArrayList<>();
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

	public static List<List<Couple<Criterion, Criterion>>> allSubsetCouple(List<Couple<Criterion, Criterion>> set) {
		List<List<Couple<Criterion, Criterion>>> result = new ArrayList<>();
		List<List<Couple<Criterion, Criterion>>> tmp;

		for (int i = 1; i <= set.size(); i++) {
			tmp = allSubsetCouple(set, i);

			for (List<Couple<Criterion, Criterion>> l : tmp)
				result.add(l);
		}

		return result;
	}

	public static List<Couple<Criterion, Criterion>> addCouple(List<Couple<Criterion, Criterion>> l,
			Couple<Criterion, Criterion> c) {
		List<Couple<Criterion, Criterion>> new_l = new ArrayList<>(l);
		new_l.add(c);
		return new_l;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * Used to show properly sets of *
	 * criteria or Couple of criteria * * * * * * * * * * * * * * * * * * *
	 */

	public static String displayAsVector(Alternative a) {

		String vectorPerf = "( ";

		for (Map.Entry<Criterion, Double> perf : a.getEvaluations().entrySet())
			vectorPerf += " " + perf.getValue() + " ";

		return vectorPerf + " )";
	}

	public static String showVector(Map<Criterion, Double> v) {
		String show2 = "( ";

		for (Map.Entry<Criterion, Double> c : v.entrySet()) {
			Double c_scaled = BigDecimal.valueOf(c.getValue()).setScale(3, RoundingMode.HALF_UP).doubleValue();
			show2 += c_scaled + " ";
		}
		show2 += " )";

		return "  " + show2;
	}

	public static String showCriteria(List<Criterion> c) {
		String show = "{ ";

		for (int i = 0; i < c.size(); i++) {
			if (i == c.size() - 1)
				show += c.get(i).getName();
			else
				show += c.get(i).getName() + ", ";
		}

		return show + " }";
	}

	public static String showCriteria(Set<Criterion> s) {
		String show = "{ ";

		for (Criterion c : s) {
			show += c.getName() + " ";
		}

		return show + " }";
	}

	public static String showSet(List<List<Criterion>> big_a) {
		String str = "{ ";

		for (List<Criterion> l : big_a)
			str += showCriteria(l) + " ";

		str += " }";

		return str;
	}

	public static String showSet(Set<Set<Criterion>> big_a) {
		String str = "{ ";

		for (Set<Criterion> l : big_a)
			str += showCriteria(l) + " ";

		str += " }";

		return str;
	}

	public static String showCouples(List<Couple<Criterion, Criterion>> cpls) {
		String string = "{ ";

		for (Couple<Criterion, Criterion> c : cpls)
			string += "(" + c.getLeft().getName() + "," + c.getRight().getName() + ") ";

		string += " }";

		return string;
	}

}
