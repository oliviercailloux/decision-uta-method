package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheComputer {

	/**
	 * @param labreuchOutput
	 *            equal null iff it is not initialized.
	 */

	private AlternativesComparison<LabreucheModel> alternativesComparison;
	private List<List<Criterion>> ivtPermutations;
	private List<List<Criterion>> setCIVT;
	private LabreucheOutput labreucheOutput;
	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheComputer.class);

	public LabreucheComputer(AlternativesComparison<LabreucheModel> alternativesComparaison) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);
		this.ivtPermutations = new ArrayList<>();
		this.setCIVT = new ArrayList<>();
		this.labreucheOutput = null;
	}

	/***************************************************************************************
	 * * GETTERS & SETTERS * *
	 *************************************************************************************/

	public List<List<Criterion>> getSetCIVT() {
		return this.setCIVT;
	}

	public AlternativesComparison<LabreucheModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public LabreucheOutput getLabreucheOutput() {
		return this.labreucheOutput;
	}

	public List<List<Criterion>> getIVTPermutations() {
		return this.ivtPermutations;
	}

	/****************************************************************************************
	 * * METHODS * *
	 ****************************************************************************************/

	/**
	 * @return the minimal set of permutation that inverse the decision between x
	 *         and y.
	 */
	private List<List<Criterion>> algo(List<List<Criterion>> c, List<List<Criterion>> b, int k) {

		LOGGER.debug("CALLING ALGO( {} , {} , {} )", c, b, k);

		List<List<Criterion>> cCopy = new ArrayList<>(c);
		List<List<Criterion>> bCopy;

		if (b == null) {
			bCopy = null;
		} else {
			bCopy = new ArrayList<>(b);
		}

		List<List<Criterion>> cPrime;
		List<Criterion> currentPerm;

		for (int i = k; i < setCIVT.size(); i++) {
			currentPerm = setCIVT.get(i);

			LOGGER.debug("{} {} Current permutation : {} current c = {} cap = {} ", k, i, currentPerm, c,
					LabreucheTools.isCapEmpty(c, currentPerm));

			if (LabreucheTools.isCapEmpty(c, currentPerm)) {
				cPrime = new ArrayList<>(cCopy);
				cPrime.add(currentPerm);

				double sum = 0.0;

				if (!cPrime.isEmpty()) {
					for (List<Criterion> perm : cPrime) {
						sum += LabreucheTools
								.dEU(perm, alternativesComparison.getPreferenceModel().getWeights(), getDelta())
								.getLeft();
					}
				}

				double vXminusVY = LabreucheTools.score(alternativesComparison.getX(),
						alternativesComparison.getPreferenceModel().getWeights())
						- LabreucheTools.score(alternativesComparison.getY(),
								alternativesComparison.getPreferenceModel().getWeights());

				cCopy.add(currentPerm);

				if (sum < vXminusVY) {

					LOGGER.debug("START BRANCHING : i = {} c = {}  b = {}", (i + 1), cCopy, bCopy);

					if (b == null) {
						cPrime = algo(cCopy, null, i + 1);
					} else {
						cPrime = algo(cCopy, bCopy, i + 1);
					}

					LOGGER.debug("END BRANCHING : i = {} c = {} b = {}", (i + 1), cCopy, bCopy);
				}

				LOGGER.debug("{} inclu_discri {}", Utils.showSet(cPrime), Utils.showSet(bCopy));

				if (LabreucheTools.includeDiscri(cPrime, bCopy,
						alternativesComparison.getPreferenceModel().getWeights(), getDelta())) {
					LOGGER.debug("UPDATE B!");
					bCopy = new ArrayList<>(cPrime);
				}

				LOGGER.debug("{} incluDiscri {}", cCopy, bCopy);

				if (!LabreucheTools.includeDiscri(cCopy, bCopy,
						alternativesComparison.getPreferenceModel().getWeights(), getDelta())) {
					LOGGER.debug("RETURN B!");
					return bCopy;
				}
			}
			cCopy = new ArrayList<>(c);
		}

		LOGGER.debug("RETURN INVALID");

		return null;
	}

	private Map<Criterion, Double> getDelta() {
		Alternative x = alternativesComparison.getX();
		Alternative y = alternativesComparison.getY();
		LabreucheModel lm = alternativesComparison.getPreferenceModel();

		return lm.getDelta(x, y);
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * Resolution problem * * * * * * * * *
	 * * * * * * * * * * * *
	 */

	public LabreucheOutput getExplanation() {
		if (labreucheOutput != null) {
			return labreucheOutput;
		}
		return computeExplanation();
	}

	/**
	 * @return the right explanation of the problem given in alternativesComparison.
	 * @throws IllegalException
	 *             if none anchors applicable.
	 * @precondition : labreucheOutput == null.
	 */
	private LabreucheOutput computeExplanation() {
		Preconditions.checkState(labreucheOutput == null);

		if (tryALL()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryNOA()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryIVT()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryRMGAVG()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryRMGCOMP()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		throw new IllegalStateException();
	}

	private boolean tryALL() {
		Preconditions.checkState(labreucheOutput == null);

		for (Double v : getDelta().values()) {
			if (v < 0.0) {
				LOGGER.debug("ALL false");
				return false;
			}
		}

		LOGGER.debug("ALL true");
		labreucheOutput = new ALLOutput(alternativesComparison);

		return true;
	}

	private boolean tryNOA() {
		Preconditions.checkState(labreucheOutput == null);

		if (LabreucheTools.score(alternativesComparison.getX(), getWeightReference()) >= LabreucheTools
				.score(alternativesComparison.getY(), getWeightReference())) {
			LOGGER.debug("NOA false");
			return false;
		}

		Map<Double, Criterion> temp = new HashMap<>();
		Set<Criterion> setC = new LinkedHashSet<>();

		for (Map.Entry<Criterion, Double> w_i : alternativesComparison.getPreferenceModel().getWeights().entrySet()) {
			temp.put(
					(w_i.getValue() - 1.0 / alternativesComparison.getCriteria().size()) * getDelta().get(w_i.getKey()),
					w_i.getKey());
		}

		ArrayList<Double> keys = new ArrayList<>(temp.keySet());
		Collections.sort(keys);

		Map<Criterion, Double> wcomposed = Maps.newHashMap(getWeightReference());
		int p = keys.size() - 1;
		double scoreX;
		double scoreY;

		do {
			wcomposed.put(temp.get(keys.get(p)),
					alternativesComparison.getPreferenceModel().getWeights().get((temp.get(keys.get(p)))));
			setC.add(temp.get(keys.get(p)));

			scoreX = LabreucheTools.score(alternativesComparison.getX(), wcomposed);
			scoreY = LabreucheTools.score(alternativesComparison.getY(), wcomposed);

			p--;
		} while (scoreX < scoreY);

		LOGGER.debug("NOA true");
		labreucheOutput = new NOAOutput(alternativesComparison, setC);

		return true;

	}

	private Map<Criterion, Double> getWeightReference() {
		Map<Criterion, Double> vectorRef = new LinkedHashMap<>();
		Set<Criterion> criteria = alternativesComparison.getCriteria();

		for (Criterion c : criteria) {
			vectorRef.put(c, 1.0 / criteria.size());
		}

		return vectorRef;
	}

	private boolean tryIVT() {
		Preconditions.checkState(labreucheOutput == null);

		int size = 2;
		List<List<Criterion>> subsets = new ArrayList<>();
		List<List<Criterion>> bigA = new ArrayList<>(); // -> first variable for algo() calls
		List<List<Criterion>> bigB = new ArrayList<>(); // -> second variable for algo() calls
		List<List<Criterion>> bigC; // result of algo()
		double dEU;
		List<Criterion> pi = new ArrayList<>();

		LOGGER.debug("Delta {} > {} : {}", alternativesComparison.getX().getName(),
				alternativesComparison.getY().getName(), getDelta().values());

		do {
			pi.clear();
			bigA.clear();
			bigB.clear();
			subsets.clear();
			setCIVT.clear();

			subsets = LabreucheTools.allSubset(new ArrayList<>(alternativesComparison.getCriteria()));

			for (List<Criterion> subset : subsets) {
				Couple<Double, List<Criterion>> res = LabreucheTools.dEU(subset,
						alternativesComparison.getPreferenceModel().getWeights(), getDelta());
				dEU = res.getLeft().doubleValue();
				pi = res.getRight();

				if (dEU > 0 && pi.containsAll(subset) && subset.containsAll(pi)) {
					setCIVT.add(subset);
				}

				pi.clear();
			}

			setCIVT = LabreucheTools.sortLexi(setCIVT, alternativesComparison.getPreferenceModel().getWeights(),
					getDelta());

			LOGGER.debug("setCIVT : ");

			for (List<Criterion> l : setCIVT) {
				LOGGER.debug("pi = {}  dEU = {}  minPI = {}", l,
						LabreucheTools.dEU(l, alternativesComparison.getPreferenceModel().getWeights(), getDelta()),
						LabreucheTools.minimalPi(l, alternativesComparison.getPreferenceModel().getWeights(),
								getDelta()));
			}

			bigC = algo(bigA, null, 0);
			size++;

		} while (bigC == null && size <= alternativesComparison.getCriteria().size());

		ivtPermutations = bigC;

		if (ivtPermutations == null) {
			LOGGER.debug("IVT false");
			return false;
		}

		// Start to determine R*

		MutableGraph<Criterion> cpls = GraphBuilder.directed().build();
		ImmutableGraph<Criterion> rS;

		LOGGER.debug("Minimals permutations : {}", ivtPermutations);

		for (List<Criterion> l : ivtPermutations) {

			rS = LabreucheTools.couplesOfG(l, alternativesComparison.getPreferenceModel().getWeights(), getDelta());

			for (EndpointPair<Criterion> cp : rS.edges()) {
				cpls.putEdge(cp.nodeU(), cp.nodeV());
			}
		}

		ImmutableGraph<Criterion> rStar = LabreucheTools.buildRStarG(ImmutableGraph.copyOf(cpls));

		// R* determined

		LOGGER.debug("IVT true");
		labreucheOutput = new IVTOutput(alternativesComparison, rStar);

		return true;
	}

	private boolean tryRMGAVG() {
		Preconditions.checkState(labreucheOutput == null);

		double maxW = getMaxW();

		if (maxW <= alternativesComparison.getPreferenceModel().getEpsilon()) {
			LOGGER.debug("RMGAVG true");

			labreucheOutput = new RMGAVGOutput(alternativesComparison);

			return true;
		}

		LOGGER.debug("RMGAVG false");

		return false;
	}

	private double getMaxW() {
		double maxW = Double.MIN_VALUE;

		for (Double v : alternativesComparison.getPreferenceModel().getWeights().values()) {
			double value = Math.abs(v - (1.0 / alternativesComparison.getCriteria().size()));

			if (value > maxW) {
				maxW = value;
			}
		}
		return maxW;
	}

	private boolean tryRMGCOMP() {
		Preconditions.checkState(labreucheOutput == null);

		double maxW = getMaxW();

		if (maxW > alternativesComparison.getPreferenceModel().getEpsilon()) {
			LOGGER.debug("RMGCOMP true");

			labreucheOutput = new RMGCOMPOutput(alternativesComparison);

			return true;
		}

		LOGGER.debug("RMGCOMP false");

		return false;
	}

	public boolean isApplicable(Anchor anchor) {
		getExplanation();

		return labreucheOutput.getAnchor() == anchor;
	}

	/**
	 * @param anchor
	 *            : the type of Anchor explanation claimed
	 * @throws IllegalArgumentException
	 *             if anchor type is not the same a labreucheOutput value.
	 */
	private LabreucheOutput getCheckedExplanation(Anchor ancrage) {
		getExplanation();
		Preconditions.checkState(labreucheOutput.getAnchor() == ancrage, "This anchor is not the one applicable");
		return labreucheOutput;
	}

	public ALLOutput getALLExplanation() {
		return (ALLOutput) getCheckedExplanation(Anchor.ALL);
	}

	public NOAOutput getNOAExplanation() {
		return (NOAOutput) getCheckedExplanation(Anchor.NOA);
	}

	public IVTOutput getIVTExplanation() {
		return (IVTOutput) getCheckedExplanation(Anchor.IVT);
	}

	public RMGAVGOutput getRMGAVGExplanation() {
		return (RMGAVGOutput) getCheckedExplanation(Anchor.RMGAVG);
	}

	public RMGCOMPOutput getRMGCOMPExplanation() {
		return (RMGCOMPOutput) getCheckedExplanation(Anchor.RMGCOMP);
	}

}