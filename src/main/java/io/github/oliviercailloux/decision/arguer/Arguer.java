package io.github.oliviercailloux.decision.arguer;

import static java.util.Objects.requireNonNull;

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
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutPut;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Arguer {

	private LabreucheOutput labreucheOutput;
	private NunesOutPut nunesOutput;

	public Arguer(LabreucheOutput labreucheOutput) {
		this.labreucheOutput = requireNonNull(labreucheOutput);
		this.nunesOutput = null;
	}

	public Arguer(NunesOutPut nunesOutput) {
		this.nunesOutput = nunesOutput;
		this.labreucheOutput = null;
	}

	public LabreucheOutput getLabreucheOutput() {
		return this.labreucheOutput;
	}

	public String argue() {
		String explanation = "";

		if (nunesOutput != null) {
			//
		}

		if (labreucheOutput != null) {

			switch (labreucheOutput.getAnchor()) {
			case ALL:
				ALLOutput phiALL = (ALLOutput) labreucheOutput;

				AlternativesComparison alcoALL = phiALL.getAlternativesComparison();

				explanation = alcoALL.getX().getName() + " is preferred to " + alcoALL.getY().getName() + " since "
						+ alcoALL.getX().getName() + " is better then " + alcoALL.getY().getName()
						+ " on ALL criteria.";

				return explanation;

			case NOA:
				NOAOutput phiNOA = (NOAOutput) labreucheOutput;

				AlternativesComparison alcoNOA = phiNOA.getAlternativesComparison();
				Set<Criterion> ConPA = phiNOA.getPositiveNoaCriteria();
				Set<Criterion> ConNA = phiNOA.getNegativeNoaCriteria();

				explanation = "Even though " + alcoNOA.getY().getName() + " is better than " + alcoNOA.getX().getName()
						+ " on average, " + alcoNOA.getX().getName() + " is preferred to " + alcoNOA.getY().getName()
						+ " since \n" + alcoNOA.getX().getName() + " is better than " + alcoNOA.getY().getName()
						+ " on the criteria " + Utils.showCriteria(ConPA) + " that are important whereas \n"
						+ alcoNOA.getX().getName() + " is worse than " + alcoNOA.getY().getName() + " on the criteria "
						+ Utils.showCriteria(ConNA) + " that are not important.";

				Double addSentence = 0.0;

				for (Criterion c : alcoNOA.getCriteria()) {
					if (!phiNOA.getNoaCriteria().contains(c)) {
						addSentence += alcoNOA.getDelta().get(c) * alcoNOA.getWeight().get(c);
					}
				}

				if (addSentence > 0) {
					explanation += "\n" + "Moroever, " + alcoNOA.getX().getName() + " is on average better than "
							+ alcoNOA.getY().getName() + " on the other criteria.";
				}

				return explanation;

			case IVT:
				IVTOutput phiIVT = (IVTOutput) labreucheOutput;

				AlternativesComparison alcoIVT = phiIVT.getAlternativesComparison();

				ImmutableSet<Criterion> k_nrw = phiIVT.getNegativeRelativelyWeak();
				ImmutableSet<Criterion> k_nw = phiIVT.getNegativeWeak();
				ImmutableSet<Criterion> k_prs = phiIVT.getPositiveRelativelyStrong();
				ImmutableSet<Criterion> k_ps = phiIVT.getPositiveStrong();
				ImmutableGraph<Criterion> k_pn = phiIVT.getPositiveNegative();

				explanation = alcoIVT.getX().getName() + " is preferred to " + alcoIVT.getY().getName() + " since "
						+ alcoIVT.getX().getName() + " is better than " + alcoIVT.getY().getName() + " on the criteria "
						+ Utils.showCriteria(k_ps) + " that are important " + "\n" + "and on the criteria "
						+ Utils.showCriteria(k_prs) + " that are relativily important, " + alcoIVT.getY().getName()
						+ " is better than " + alcoIVT.getX().getName() + " on the criteria " + Utils.showCriteria(k_nw)
						+ "\n" + "that are not important and on the criteria " + Utils.showCriteria(k_nrw)
						+ " that are not really important.";

				if (!k_pn.edges().isEmpty()) {
					explanation += "And ";

					for (EndpointPair<Criterion> couple : k_pn.edges()) {

						explanation += couple.nodeV().getName() + " for wich " + alcoIVT.getX().getName()
								+ " is better than " + alcoIVT.getY().getName() + " is more important than criterion "
								+ couple.nodeU().getName() + " for wich " + alcoIVT.getX().getName() + " is worse than "
								+ alcoIVT.getY().getName() + " ";
					}

					explanation += ".";
				}

				return explanation;

			case RMGAVG:
				RMGAVGOutput phiRMGAVG = (RMGAVGOutput) labreucheOutput;

				AlternativesComparison alcoRMGAVG = phiRMGAVG.getAlternativesComparison();

				explanation = alcoRMGAVG.getX().getName() + " is preferred to " + alcoRMGAVG.getY().getName()
						+ " since " + alcoRMGAVG.getX().getName() + " is on average better than "
						+ alcoRMGAVG.getY().getName() + "\n" + " and all the criteria have almost the same weights.";

				return explanation;

			case RMGCOMP:
				RMGCOMPOutput phiRMGCOMP = (RMGCOMPOutput) labreucheOutput;

				AlternativesComparison alcoRMGCOMP = phiRMGCOMP.getAlternativesComparison();
				double maxW = phiRMGCOMP.getMaxW();
				double eps = phiRMGCOMP.getEpsilon();

				if (maxW > eps) {
					if (maxW <= eps * 2) {
						explanation = alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
								+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
								+ alcoRMGCOMP.getY().getName() + " on \n"
								+ Utils.showCriteria(alcoRMGCOMP.getPositiveCriteria())
								+ " is significantly larger than the intensity of " + alcoRMGCOMP.getY().getName()
								+ " over " + alcoRMGCOMP.getX().getName() + " on \n"
								+ Utils.showCriteria(alcoRMGCOMP.getNegativeCriteria())
								+ ", and all the criteria have more or less the same weights.";

						return explanation;
					}
				}

				explanation = alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
						+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
						+ alcoRMGCOMP.getX().getName() + " on " + Utils.showCriteria(alcoRMGCOMP.getPositiveCriteria())
						+ " is much larger than the intensity of " + alcoRMGCOMP.getY().getName() + " over "
						+ alcoRMGCOMP.getX().getName() + " on " + Utils.showCriteria(alcoRMGCOMP.getNegativeCriteria())
						+ ".";

				return explanation;

			default:
				return "No possible argumentation found";
			}
		}

		return explanation;
	}
}
