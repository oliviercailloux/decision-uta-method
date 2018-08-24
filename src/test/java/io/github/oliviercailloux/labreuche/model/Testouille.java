package io.github.oliviercailloux.labreuche.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;

public class Testouille {

	public static void main(String[] args) {

		ProblemGenerator pg = new ProblemGenerator();

		pg.generateCriteria(10, 0, 10, 1);

		Set<Couple<Criterion, Criterion>> couples = generateCouples(6, pg.getCriteria());

		System.out.println("Les couples :");

		for (Couple<Criterion, Criterion> c : couples)
			System.out.println(c.getLeft().getName() + " , " + c.getRight().getName());

		MutableGraph<Criterion> graph = GraphBuilder.directed().build();

		for (Criterion c : pg.getCriteria())
			graph.addNode(c);

		for (Couple<Criterion, Criterion> cp : couples)
			graph.putEdge(cp.getLeft(), cp.getRight());

		ImmutableGraph<Criterion> immutableGraph = ImmutableGraph.copyOf(graph);

		System.out.println("\n" + "Les arcs :");

		for (EndpointPair<Criterion> ep : immutableGraph.edges())
			System.out.println(ep.nodeU().getName() + " , " + ep.nodeV().getName());
	}

	private static Set<Couple<Criterion, Criterion>> generateCouples(int number, List<Criterion> criteria) {
		Set<Couple<Criterion, Criterion>> res = new LinkedHashSet<>();
		for (int i = 0; i < number; i++) {
			int alea1 = (int) (Math.random() * criteria.size());
			int alea2 = (int) (Math.random() * criteria.size()) + 1;
			if (alea2 >= criteria.size() - 1)
				alea2 = 0;
			Couple<Criterion, Criterion> c = new Couple<>(criteria.get(alea1), criteria.get(alea2));
			res.add(c);
		}

		return res;
	}
}
