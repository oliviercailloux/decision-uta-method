package io.github.oliviercailloux.decision.arguer.nunes;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Table;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheComputer;
import io.github.oliviercailloux.decision.arguer.nunes.output.CriticalAttributeOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.CutOffOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DecisiveOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.Pattern;
import io.github.oliviercailloux.decision.arguer.nunes.output.TradeOffOutput;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class NunesComputer {

	private Set<Constraint> constraints;
	private AlternativesComparison<NunesModel> alternativesComparison;
	private Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradoffs;
	private NunesOutput nunesOutput;
	private Set<EvaluatedAlternative> alternatives;
	private Set<EvaluatedAlternative> alternativesRejected;
	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheComputer.class);

	public NunesComputer(AlternativesComparison<NunesModel> alternativesComparison, Set<Constraint> constraints,
			Set<EvaluatedAlternative> alternatives) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.constraints = new LinkedHashSet<>((requireNonNull(constraints)));
		this.alternatives = requireNonNull(alternatives);
		this.nunesOutput = null;
		this.alternativesRejected = new LinkedHashSet<>();

		this.tradoffs = NunesTools.computeTO(alternatives, alternativesComparison.getPreferenceModel().getWeights());
	}

	public Set<EvaluatedAlternative> getAlternativesRejected() {
		return this.alternativesRejected;
	}

	public Set<EvaluatedAlternative> getAlternatives() {
		return this.alternatives;
	}

	public Set<Constraint> getConstraints() {
		return this.constraints;
	}

	public Table<EvaluatedAlternative, EvaluatedAlternative, Double> getTradOffs() {
		return this.tradoffs;
	}

	public AlternativesComparison<NunesModel> getAlternativesComparison() {
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

		if(tryCRIT()) {
			assert nunesOutput != null;
			return nunesOutput;
		}
		
		if (tryDOM()) {
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
	
	public CriticalAttributeOutput getCRITExplanation() {
		return (CriticalAttributeOutput) getCheckedExplanation(Pattern.CRIT);
	}

	public DominationOutput getDOMExplanation() {
		return (DominationOutput) getCheckedExplanation(Pattern.DOM);
	}
	
	public CutOffOutput getCUTOFFExplanation() {
		return (CutOffOutput) getCheckedExplanation(Pattern.CUTOFF);
	}
	

	public DecisiveOutput getDECISIVEExplanation() {
		return (DecisiveOutput) getCheckedExplanation(Pattern.DECISIVE);
	}

	public TradeOffOutput getTRADEOFFExplanation() {
		return (TradeOffOutput) getCheckedExplanation(Pattern.TRADEOFF);
	}

	private boolean tryCRIT() {
		Preconditions.checkState(nunesOutput == null);

		int count = 0;
		Criterion critical = null;

		for (Entry<Criterion, Double> entry : alternativesComparison.getPreferenceModel()
				.getDelta(alternativesComparison.getX(), alternativesComparison.getY()).entrySet()) {
			if (entry.getValue() < 0.0) {
				count++;
				critical = entry.getKey();
			}

			if (entry.getValue() < 0.0) {
				LOGGER.info("CRIT false");
				return false;
			}
		}

		if (count == 1) {
			LOGGER.info("DOM (critical) true");
			nunesOutput = new CriticalAttributeOutput(alternativesComparison, critical);
			return true;
		}

		LOGGER.info("CRIT false");
		return false;
	}
	
	private boolean tryDOM() {
		Preconditions.checkState(nunesOutput == null);

		for (Entry<Criterion, Double> entry : alternativesComparison.getPreferenceModel()
				.getDelta(alternativesComparison.getX(), alternativesComparison.getY()).entrySet()) {
			if (entry.getValue() < 0.0) {
				LOGGER.info("DOM false");
				return false;
			}
		}

		LOGGER.info("DOM true");
		nunesOutput = new DominationOutput(alternativesComparison);

		return true;
	}

	private boolean tryCUTOFF() {
		//TODO
		return false;
	}
	
	private boolean tryMINREQP() {
		// TODO
		return false;
	}
	
	private boolean tryMINREQM() {
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
}
