package io.github.oliviercailloux.decision.arguer;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.CutOffOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Arguer {

	public String argue(LabreucheOutput output) {
		switch (output.getAnchor()) {
		case ALL:
			return argueALL((ALLOutput) output);

		case NOA:
			return argueNOA((NOAOutput) output);

		case IVT:
			return argueIVT((IVTOutput) output);

		case RMGAVG:
			return argueRMGAVG((RMGAVGOutput) output);

		case RMGCOMP:
			return argueRMGCOMP((RMGCOMPOutput) output);

		default:
			return "No possible argumentation found";
		}

	}

	private String argueALL(ALLOutput output) {

		AlternativesComparison alcoALL = output.getAlternativesComparison();

		String explanation = alcoALL.getX().getName() + " is preferred to " + alcoALL.getY().getName() + " since "
				+ alcoALL.getX().getName() + " is better then " + alcoALL.getY().getName() + " on ALL criteria.";

		return explanation;
	}

	private String argueNOA(NOAOutput output) {

		AlternativesComparison alcoNOA = output.getAlternativesComparison();
		Set<Criterion> ConPA = output.getPositiveNoaCriteria();
		Set<Criterion> ConNA = output.getNegativeNoaCriteria();

		String explanation = "Even though " + alcoNOA.getY().getName() + " is better than " + alcoNOA.getX().getName()
				+ " on average, " + alcoNOA.getX().getName() + " is preferred to " + alcoNOA.getY().getName()
				+ " since \n" + alcoNOA.getX().getName() + " is better than " + alcoNOA.getY().getName()
				+ " on the criteria " + Utils.showCriteria(ConPA) + " that are important whereas \n"
				+ alcoNOA.getX().getName() + " is worse than " + alcoNOA.getY().getName() + " on the criteria "
				+ Utils.showCriteria(ConNA) + " that are not important.";

		Double addSentence = 0.0;

		for (Criterion c : alcoNOA.getCriteria()) {
			if (!output.getNoaCriteria().contains(c)) {
				addSentence += alcoNOA.getDelta().get(c) * alcoNOA.getWeight().get(c);
			}
		}

		if (addSentence > 0) {
			explanation += "\n" + "Moroever, " + alcoNOA.getX().getName() + " is on average better than "
					+ alcoNOA.getY().getName() + " on the other criteria.";
		}

		return explanation;

	}

	private String argueIVT(IVTOutput output) {

		AlternativesComparison alcoIVT = output.getAlternativesComparison();

		ImmutableSet<Criterion> k_nrw = output.getNegativeRelativelyWeak();
		ImmutableSet<Criterion> k_nw = output.getNegativeWeak();
		ImmutableSet<Criterion> k_prs = output.getPositiveRelativelyStrong();
		ImmutableSet<Criterion> k_ps = output.getPositiveStrong();
		ImmutableGraph<Criterion> k_pn = output.getPositiveNegative();

		String explanation = alcoIVT.getX().getName() + " is preferred to " + alcoIVT.getY().getName() + " since "
				+ alcoIVT.getX().getName() + " is better than " + alcoIVT.getY().getName() + " on the criteria "
				+ Utils.showCriteria(k_ps) + " that are important " + "\n" + "and on the criteria "
				+ Utils.showCriteria(k_prs) + " that are relativily important, " + alcoIVT.getY().getName()
				+ " is better than " + alcoIVT.getX().getName() + " on the criteria " + Utils.showCriteria(k_nw) + "\n"
				+ "that are not important and on the criteria " + Utils.showCriteria(k_nrw)
				+ " that are not really important.";

		if (!k_pn.edges().isEmpty()) {
			explanation += "And ";

			for (EndpointPair<Criterion> couple : k_pn.edges()) {

				explanation += couple.nodeV().getName() + " for wich " + alcoIVT.getX().getName() + " is better than "
						+ alcoIVT.getY().getName() + " is more important than criterion " + couple.nodeU().getName()
						+ " for wich " + alcoIVT.getX().getName() + " is worse than " + alcoIVT.getY().getName() + " ";
			}

			explanation += ".";
		}

		return explanation;

	}

	private String argueRMGAVG(RMGAVGOutput output) {

		AlternativesComparison alcoRMGAVG = output.getAlternativesComparison();

		String explanation = alcoRMGAVG.getX().getName() + " is preferred to " + alcoRMGAVG.getY().getName() + " since "
				+ alcoRMGAVG.getX().getName() + " is on average better than " + alcoRMGAVG.getY().getName() + "\n"
				+ " and all the criteria have almost the same weights.";

		return explanation;
	}

	private String argueRMGCOMP(RMGCOMPOutput output) {

		String explanation = "";

		AlternativesComparison alcoRMGCOMP = output.getAlternativesComparison();
		double maxW = output.getMaxW();
		double eps = output.getEpsilon();

		if (maxW > eps) {
			if (maxW <= eps * 2) {
				explanation = alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
						+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
						+ alcoRMGCOMP.getY().getName() + " on \n"
						+ Utils.showCriteria(alcoRMGCOMP.getPositiveCriteria())
						+ " is significantly larger than the intensity of " + alcoRMGCOMP.getY().getName() + " over "
						+ alcoRMGCOMP.getX().getName() + " on \n"
						+ Utils.showCriteria(alcoRMGCOMP.getNegativeCriteria())
						+ ", and all the criteria have more or less the same weights.";

				return explanation;
			}
		}

		explanation = alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
				+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
				+ alcoRMGCOMP.getX().getName() + " on " + Utils.showCriteria(alcoRMGCOMP.getPositiveCriteria())
				+ " is much larger than the intensity of " + alcoRMGCOMP.getY().getName() + " over "
				+ alcoRMGCOMP.getX().getName() + " on " + Utils.showCriteria(alcoRMGCOMP.getNegativeCriteria()) + ".";

		return explanation;

	}

	public String argue(NunesOutput output) {
		String explanation = "";

		switch (output.getPattern()) {

		case DOMINATION:
			return argueDomination((DominationOutput) output);

		case CUTOFF:
			return argueCutOff((CutOffOutput) output);

		case MINREQM:
			return explanation;

		case MINREQP:
			return explanation;

		case DECISIVE:
			return explanation;

		case PROSCONS:
			return explanation;

		default:
			return "No possible argumentation found";

		}
	}

	private String argueDomination(DominationOutput output) {
		String explanation = "";

		AlternativesComparison alcoDOM = output.getAlternativesComparison();

		if (output.getCritical() != null) {
			explanation = alcoDOM.getX().getName() + " is recommended because it got the best value on "
					+ output.getCritical().getName();
		}

		explanation = "There is no reason to choose " + alcoDOM.getY().getName() + ", as " + alcoDOM.getX().getName()
				+ "is better on all criteria.";

		return explanation;
	}

	private String argueCutOff(CutOffOutput output) {

		AlternativesComparison alcoCutoff = output.getAlternativesComparison();

		String explanation = alcoCutoff.getX().getName()
				+ " is rejected because she doesn't satisfied the constrain on the criteria "
				+ output.getConstraint().getCriterion().getName();

		return explanation;
	}
}
