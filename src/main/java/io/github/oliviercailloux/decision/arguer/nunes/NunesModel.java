package io.github.oliviercailloux.decision.arguer.nunes;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Table;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.nunes.output.DecisiveOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.Pattern;
import io.github.oliviercailloux.decision.arguer.nunes.output.TradeOffOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NunesModel {

	private Set<Constraint> constraints;
	private AlternativesComparison alternativesComparison;
	private Table<Alternative, Alternative, Double> tradoffs;
	private NunesOutput nunesOutput;
	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheModel.class);

	public NunesModel(AlternativesComparison alternativesComparison, Set<Constraint> constraints) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.constraints = new LinkedHashSet<>((requireNonNull(constraints)));
		this.nunesOutput = null;
		this.tradoffs = NunesTools.computeTO(alternativesComparison);
	}

	public Set<Constraint> getConstraints() {
		return this.constraints;
	}

	public Table<Alternative, Alternative, Double> getTradOffs() {
		return this.tradoffs;
	}

	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public NunesOutput getNunesOutput() {
		return this.nunesOutput;
	}

	public NunesOutput getExplanation() {
		if (nunesOutput != null) {
			return nunesOutput;
		}
		return computeExplanation();
	}

	private NunesOutput computeExplanation() {
		Preconditions.checkState(nunesOutput == null);

		if (tryDOMINATION()) {
			assert nunesOutput != null;
			return nunesOutput;
		}

		if (tryMINREQP()) {
			assert nunesOutput != null;
			return nunesOutput;
		}

		if (tryDECISIVE()) {
			assert nunesOutput != null;
			return nunesOutput;
		}

		if (tryTRADEOFF()) {
			assert nunesOutput != null;
			return nunesOutput;
		}

		throw new IllegalStateException();
	}

	public boolean isApplicable(Pattern pattern) {
		getExplanation();

		return nunesOutput.getPattern() == pattern;
	}

	/**
	 * @param anchor
	 *            : the type of Anchor explanation claimed
	 * @throws IllegalArgumentException
	 *             if anchor type is not the same a labreucheOutput value.
	 */
	private NunesOutput getCheckedExplanation(Pattern pattern) {
		getExplanation();
		Preconditions.checkState(nunesOutput.getPattern() == pattern, "This pattern is not the one applicable");
		return nunesOutput;
	}

	public DominationOutput getDOMINATIONExplanation() {
		return (DominationOutput) getCheckedExplanation(Pattern.DOMINATION);
	}

	public DecisiveOutput getDECISIVEExplanation() {
		return (DecisiveOutput) getCheckedExplanation(Pattern.DECISIVE);
	}

	public TradeOffOutput getTRADEOFFExplanation() {
		return (TradeOffOutput) getCheckedExplanation(Pattern.TRADEOFF);
	}

	private boolean tryDOMINATION() {
		Preconditions.checkState(nunesOutput == null);

		int count = 0;
		Criterion critical = null;

		for (Entry<Criterion, Double> entry : alternativesComparison.getDelta().entrySet()) {
			if (entry.getValue() > 0.0) {
				count++;
				critical = entry.getKey();

			}

			if (entry.getValue() < 0.0) {
				LOGGER.info("DOM false");
				return false;
			}
		}

		if (count == 1) {
			LOGGER.info("DOM (critical) true");
			nunesOutput = new DominationOutput(alternativesComparison, critical);
			return true;
		}

		LOGGER.info("DOM true");
		nunesOutput = new DominationOutput(alternativesComparison);

		return true;
	}

	private boolean tryMINREQP() {
		// TODO
		return false;
	}

	private boolean tryDECISIVE() {
		// TODO
		return false;
	}

	private boolean tryTRADEOFF() {
		// TODO
		return false;
	}

	public void showProblem(boolean print) {

		StringBuilder bld = new StringBuilder();

		bld.append("****************************************************************" + "\n"
				+ "*                                                              *" + "\n"
				+ "*         Recommender system based on Nunes and al Model       *" + "\n"
				+ "*                                                              *" + "\n"
				+ "****************************************************************" + "\n");

		bld.append("\n    Criteria    <-   Weight : \n");

		for (Criterion c : alternativesComparison.getWeight().keySet()) {
			bld.append("\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = "
					+ alternativesComparison.getWeight().get(c));
		}

		bld.append("\n    Constrains : \n");

		for (Constraint c : constraints) {
			bld.append("\n" + "	" + c.toString() + ", v(" + c.getCriterion().getName() + ") = " + c.getValuePref());
		}

		bld.append("\n \n Alternatives : ");

		bld.append("\n" + "	" + alternativesComparison.getX().getName() + " " + " : "
				+ Utils.showVector(alternativesComparison.getX().getEvaluations().values()));
		bld.append("\n" + "	" + alternativesComparison.getY().getName() + " " + " : "
				+ Utils.showVector(alternativesComparison.getY().getEvaluations().values()));

		bld.append("\n" + "			Alternatives ranked");
		bld.append("\n" + alternativesComparison.getX().getName() + " = "
				+ NunesTools.score(alternativesComparison.getX(), alternativesComparison.getY(),
						alternativesComparison.getCriteria(), alternativesComparison.getWeight(), tradoffs));
		bld.append("\n" + alternativesComparison.getY().getName() + " = "
				+ NunesTools.score(alternativesComparison.getY(), alternativesComparison.getX(),
						alternativesComparison.getCriteria(), alternativesComparison.getWeight(), tradoffs));

		bld.append("Explanation why " + alternativesComparison.getX().getName() + " is better than "
				+ alternativesComparison.getY().getName() + " :");

		if(print) {
			LOGGER.info(bld.toString());
		}
	}

	public void resolved() {
		showProblem(true);

		NunesOutput no = getExplanation();

		NunesArguer arg = new NunesArguer();
		String explanation = arg.argue(no);

		LOGGER.info(explanation);
	}

}
