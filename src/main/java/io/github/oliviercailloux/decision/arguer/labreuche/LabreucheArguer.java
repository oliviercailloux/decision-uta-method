package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.decision.model.Criterion;

public class LabreucheArguer {

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
			throw new IllegalStateException();
		}
	}

	private String argueALL(ALLOutput output) {

		StringBuilder bld = new StringBuilder();
		AlternativesComparison<LabreucheModel> alcoALL = output.getAlternativesComparison();

		bld.append(alcoALL.getX().getName() + " is preferred to " + alcoALL.getY().getName() + " since "
				+ alcoALL.getX().getName() + " is better then " + alcoALL.getY().getName() + " on ALL criteria.");

		return bld.toString();
	}

	private String argueNOA(NOAOutput output) {

		StringBuilder bld = new StringBuilder();
		AlternativesComparison<LabreucheModel> alcoNOA = output.getAlternativesComparison();
		Set<Criterion> cOnPA = output.getPositiveNoaCriteria();
		Set<Criterion> cOnNA = output.getNegativeNoaCriteria();

		bld.append("Even though " + alcoNOA.getY().getName() + " is better than " + alcoNOA.getX().getName()
				+ " on average, " + alcoNOA.getX().getName() + " is preferred to " + alcoNOA.getY().getName()
				+ " since \n" + alcoNOA.getX().getName() + " is better than " + alcoNOA.getY().getName()
				+ " on the criteria " + Utils.showCriteria(cOnPA) + " that are important whereas "
				+ alcoNOA.getX().getName() + " is worse than " + alcoNOA.getY().getName() + " on the criteria "
				+ Utils.showCriteria(cOnNA) + " that are not important.");

		Double addSentence = 0.0;

		for (Criterion c : alcoNOA.getCriteria()) {
			if (!output.getNoaCriteria().contains(c)) {
				addSentence += alcoNOA.getPreferenceModel().getDelta(alcoNOA.getX(), alcoNOA.getY()).get(c)
						* alcoNOA.getPreferenceModel().getWeights().get(c);
			}
		}

		if (addSentence > 0) {
			bld.append("\n" + "Moroever, " + alcoNOA.getX().getName() + " is on average better than "
					+ alcoNOA.getY().getName() + " on the other criteria.");
		}

		return bld.toString();
	}

	private String argueIVT(IVTOutput output) {

		StringBuilder bld = new StringBuilder();
		AlternativesComparison<LabreucheModel> alcoIVT = output.getAlternativesComparison();
		ImmutableSet<Criterion> kNRW = output.getNegativeRelativelyWeak();
		ImmutableSet<Criterion> kNW = output.getNegativeWeak();
		ImmutableSet<Criterion> kPRS = output.getPositiveRelativelyStrong();
		ImmutableSet<Criterion> kPS = output.getPositiveStrong();
		ImmutableGraph<Criterion> kPN = output.getPositiveNegative();

		bld.append(alcoIVT.getX().getName() + " is preferred to " + alcoIVT.getY().getName() + " since "
				+ alcoIVT.getX().getName() + " is better than " + alcoIVT.getY().getName() + " on the criteria "
				+ Utils.showCriteria(kPS) + " that are important and on the criteria " + Utils.showCriteria(kPRS)
				+ " that are relativily important, " + alcoIVT.getY().getName() + " is better than "
				+ alcoIVT.getX().getName() + " on the criteria " + Utils.showCriteria(kNW)
				+ " that are not important and on the criteria " + Utils.showCriteria(kNRW)
				+ " that are not really important.");

		if (!kPN.edges().isEmpty()) {
			bld.append("\n" + "And ");

			for (EndpointPair<Criterion> couple : kPN.edges()) {

				bld.append(couple.nodeV().getName() + " for wich " + alcoIVT.getX().getName() + " is better than "
						+ alcoIVT.getY().getName() + " is more important than criterion " + couple.nodeU().getName()
						+ " for wich " + alcoIVT.getX().getName() + " is worse than " + alcoIVT.getY().getName() + " ");
			}

			bld.replace(bld.length(), bld.length(), ".");
		}

		return bld.toString();

	}

	private String argueRMGAVG(RMGAVGOutput output) {

		StringBuilder bld = new StringBuilder();
		AlternativesComparison<LabreucheModel> alcoRMGAVG = output.getAlternativesComparison();

		bld.append(alcoRMGAVG.getX().getName() + " is preferred to " + alcoRMGAVG.getY().getName() + " since "
				+ alcoRMGAVG.getX().getName() + " is on average better than " + alcoRMGAVG.getY().getName()
				+ " and all the criteria have almost the same weights.");

		return bld.toString();
	}

	private String argueRMGCOMP(RMGCOMPOutput output) {

		StringBuilder bld = new StringBuilder();
		AlternativesComparison<LabreucheModel> alcoRMGCOMP = output.getAlternativesComparison();
		double maxW = output.getMaxW();
		double epsilonPrime = output.getAlternativesComparison().getPreferenceModel().getEpsilonWPrime();
		double epsilon = output.getAlternativesComparison().getPreferenceModel().getEpsilonW();

		if (maxW > epsilon && maxW <= epsilonPrime) {
			bld.append(alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
					+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
					+ alcoRMGCOMP.getY().getName() + " on \n" + Utils.showCriteria(alcoRMGCOMP.getPositiveCriteria())
					+ " is significantly larger than the intensity of " + alcoRMGCOMP.getY().getName() + " over "
					+ alcoRMGCOMP.getX().getName() + " on \n" + Utils.showCriteria(alcoRMGCOMP.getNegativeCriteria())
					+ ", and all the criteria have more or less the same weights.");

			return bld.toString();
		}

		bld.append(alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
				+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
				+ alcoRMGCOMP.getY().getName() + " on " + Utils.showCriteria(alcoRMGCOMP.getPositiveCriteria())
				+ " is much larger than the intensity of " + alcoRMGCOMP.getY().getName() + " over "
				+ alcoRMGCOMP.getX().getName() + " on " + Utils.showCriteria(alcoRMGCOMP.getNegativeCriteria()) + ".");

		return bld.toString();
	}

}
