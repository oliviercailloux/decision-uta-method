package io.github.oliviercailloux.decision.arguer.ui;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheArguer;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheComputer;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheTools;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.nunes.Constraint;
import io.github.oliviercailloux.decision.arguer.nunes.NunesArguer;
import io.github.oliviercailloux.decision.arguer.nunes.NunesComputer;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;
import io.github.oliviercailloux.decision.arguer.nunes.NunesTools;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class ArguerUI {

	private ArguerUI() {
		// Used to print problem and its resolution
	}

	public void showNunesArgument(AlternativesComparison<NunesModel> altsComp, Set<Constraint> constraints) {
		showHeader("Nunes");

		LabreucheModel lmTMP = new LabreucheModel(altsComp.getPreferenceModel().getWeights());
		AlternativesComparison<LabreucheModel> laltcompTMP = new AlternativesComparison<>(altsComp.getX(),
				altsComp.getY(), lmTMP);

		showCriteriaWeights(laltcompTMP);
		showConstraints(constraints);
		showAlternatives(laltcompTMP);
		showAlternativesRanked(laltcompTMP, false);
		showStartExplanation(laltcompTMP);

		Set<Alternative> set = new LinkedHashSet<>();
		set.add(altsComp.getX());
		set.add(altsComp.getY());

		NunesComputer nc = new NunesComputer(altsComp, constraints, set);
		NunesOutput no = nc.getExplanation();
		NunesArguer arg = new NunesArguer();

		System.out.println(arg.argue(no));
	}

	public void showLabreucheArgument(AlternativesComparison<LabreucheModel> altsComp) {
		showHeader("Labreuche");
		showCriteriaWeights(altsComp);
		showAlternatives(altsComp);
		showAlternativesRanked(altsComp, true);
		showStartExplanation(altsComp);

		LabreucheComputer lc = new LabreucheComputer(altsComp);
		LabreucheOutput lo = lc.getExplanation();
		LabreucheArguer arg = new LabreucheArguer();

		System.out.println(arg.argue(lo));
	}

	public void showHeader(String modelName) {
		StringBuilder bld = new StringBuilder();

		bld.append("****************************************************************" + "\n"
				+ "*                                                              *" + "\n"
				+ "*      Recommender system based on " + modelName + " Model       *" + "\n"
				+ "*                                                              *" + "\n"
				+ "****************************************************************" + "\n");

		System.out.println(bld);
	}

	public void showCriteriaWeights(AlternativesComparison<LabreucheModel> altsComp) {
		StringBuilder bld = new StringBuilder();

		bld.append("\n    Criteria    <-   Weight : \n");

		for (Criterion c : altsComp.getPreferenceModel().getWeights().keySet()) {
			bld.append("\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = "
					+ altsComp.getPreferenceModel().getWeights().get(c));
		}

		System.out.println(bld.toString());
	}

	public void showConstraints(Set<Constraint> constraints) {
		StringBuilder bld = new StringBuilder();

		bld.append("\n    Constraints : \n");

		for (Constraint c : constraints) {
			bld.append("\n" + "	" + c.toString() + ", v(" + c.getCriterion().getName() + ") = " + c.getValuePref());
		}

		System.out.println(bld.toString());
	}

	public void showAlternatives(AlternativesComparison<LabreucheModel> altsComp) {
		StringBuilder bld = new StringBuilder();

		bld.append("\n \n Alternatives : ");

		bld.append("\n" + "	" + altsComp.getX().getName() + " " + " : "
				+ Utils.showVector(altsComp.getX().getEvaluations().values()));
		bld.append("\n" + "	" + altsComp.getY().getName() + " " + " : "
				+ Utils.showVector(altsComp.getY().getEvaluations().values()));

		System.out.println(bld.toString());
	}

	public void showAlternativesRanked(AlternativesComparison<LabreucheModel> altsComp, boolean flag) {
		StringBuilder bld = new StringBuilder();

		double scoreX;
		double scoreY;

		if (flag) {
			scoreX = LabreucheTools.score(altsComp.getX(), altsComp.getPreferenceModel().getWeights());
			scoreY = LabreucheTools.score(altsComp.getY(), altsComp.getPreferenceModel().getWeights());
		} else {

			Table<Alternative, Alternative, Double> tradeoffs = NunesTools.computeTO(
					ImmutableSet.of(altsComp.getX(), altsComp.getY()), altsComp.getPreferenceModel().getWeights());
			scoreX = NunesTools.score(altsComp.getX(), altsComp.getY(), altsComp.getCriteria(),
					altsComp.getPreferenceModel().getWeights(), tradeoffs);
			scoreY = NunesTools.score(altsComp.getY(), altsComp.getX(), altsComp.getCriteria(),
					altsComp.getPreferenceModel().getWeights(), tradeoffs);
		}

		bld.append("\n" + "			Alternatives ranked");
		bld.append("\n" + altsComp.getX().getName() + " = " + scoreX);
		bld.append("\n" + altsComp.getY().getName() + " = " + scoreY);

		System.out.println(bld.toString());
	}

	public void showStartExplanation(AlternativesComparison<LabreucheModel> altsComp) {
		StringBuilder bld = new StringBuilder();

		bld.append(
				"Explanation why " + altsComp.getX().getName() + " is better than " + altsComp.getY().getName() + " :");

		System.out.println(bld.toString());
	}
}
