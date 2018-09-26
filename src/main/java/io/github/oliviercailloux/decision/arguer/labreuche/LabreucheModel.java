package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheModel {

	/**
	 * @param labreuchOutput
	 *            equal null iff it is not initialized.
	 */

	private AlternativesComparison alternativesComparison;
	private double epsilon;
	private List<List<Criterion>> ivtPermutations;
	private List<List<Criterion>> setCIVT;
	private LabreucheOutput labreucheOutput;
	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheModel.class);

	public LabreucheModel(AlternativesComparison alternativesComparaison) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);

		this.ivtPermutations = new ArrayList<>();
		this.setCIVT = new ArrayList<>();
		this.epsilon = 0.2 / this.alternativesComparison.getWeight().keySet().size();
		this.labreucheOutput = null;
	}

	public LabreucheModel(AlternativesComparison alternativesComparaison, double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);
		this.epsilon = requireNonNull(epsilon);

		this.setCIVT = new ArrayList<>();
		this.labreucheOutput = null;
	}

	/***************************************************************************************
	 * * GETTERS & SETTERS * *
	 *************************************************************************************/

	public List<List<Criterion>> getSetCIVT() {
		return this.setCIVT;
	}

	public Double getEpsilon() {
		return this.epsilon;
	}

	public AlternativesComparison getAlternativesComparison() {
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
		StringBuilder bld = new StringBuilder();
		
		bld.append("\n CALLING ALGO( " + Utils.showSet(c) + " , " + Utils.showSet(b) + " , " + k + " )");
		LOGGER.debug(bld.toString());

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
			
			bld = new StringBuilder();
			bld.append(k + " " + i + " Current permutation : " + Utils.showCriteria(currentPerm) + " current c = "
					+ Utils.showSet(c) + " cap = " + LabreucheTools.isCapEmpty(c, currentPerm));
			LOGGER.debug(bld.toString());

			if (LabreucheTools.isCapEmpty(c, currentPerm)) {
				cPrime = new ArrayList<>(cCopy);
				cPrime.add(currentPerm);

				double sum = 0.0;

				if (!cPrime.isEmpty()) {
					for (List<Criterion> perm : cPrime) {
						sum += LabreucheTools
								.d_eu(perm, alternativesComparison.getWeight(), alternativesComparison.getDelta())
								.getLeft();
					}
				}

				double vXminusVY = LabreucheTools.score(alternativesComparison.getX(),
						alternativesComparison.getWeight())
						- LabreucheTools.score(alternativesComparison.getY(), alternativesComparison.getWeight());

				cCopy.add(currentPerm);

				if (sum < vXminusVY) {
					bld = new StringBuilder();
					bld.append("START BRANCHING : c = " + Utils.showSet(cCopy) + " b = " + Utils.showSet(bCopy)
							+ " " + (i + 1));
					LOGGER.debug(bld.toString());

					if (b == null) {
						cPrime = algo(cCopy, null, i + 1);
					} else {
						cPrime = algo(cCopy, bCopy, i + 1);
					}

					bld = new StringBuilder();
					bld.append("END BRANCHING : c = " + Utils.showSet(cCopy) + " b = " + Utils.showSet(bCopy) + " "
							+ (i + 1));
					LOGGER.debug(bld.toString());
				}
				
				LOGGER.debug(Utils.showSet(cPrime) + " inclu_discri " + Utils.showSet(bCopy));
				
				
				if (LabreucheTools.includeDiscri(cPrime, bCopy, alternativesComparison.getWeight(),
						alternativesComparison.getDelta())) {
					LOGGER.debug("UPDATE B!");
					bCopy = new ArrayList<>(cPrime);
				}

				LOGGER.debug(Utils.showSet(cCopy) + " incluDiscri " + Utils.showSet(bCopy));

				if (!LabreucheTools.includeDiscri(cCopy, bCopy, alternativesComparison.getWeight(),
						alternativesComparison.getDelta())) {
					LOGGER.debug("RETURN B \n");
					return bCopy;
				}
			}
			cCopy = new ArrayList<>(c);
		}

		LOGGER.debug("RETURN INVALID \n");
				
		return null;
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

		for (Double v : alternativesComparison.getDelta().values()) {
			if (v < 0.0) {
				LOGGER.info("ALL false");
				return false;
			}
		}

		LOGGER.info("ALL true");
		labreucheOutput = new ALLOutput(alternativesComparison);

		return true;
	}

	private boolean tryNOA() {
		Preconditions.checkState(labreucheOutput == null);

		if (LabreucheTools.score(alternativesComparison.getX(),
				alternativesComparison.getWeightReference()) >= LabreucheTools.score(alternativesComparison.getY(),
						alternativesComparison.getWeightReference())) {
			LOGGER.info("NOA false");
			return false;
		}

		Map<Double, Criterion> temp = new HashMap<>();
		Set<Criterion> setC = new LinkedHashSet<>();

		for (Map.Entry<Criterion, Double> w_i : alternativesComparison.getWeight().entrySet()) {
			temp.put((w_i.getValue() - 1.0 / alternativesComparison.getCriteria().size())
					* alternativesComparison.getDelta().get(w_i.getKey()), w_i.getKey());
		}

		ArrayList<Double> keys = new ArrayList<>(temp.keySet());
		Collections.sort(keys);

		Map<Criterion, Double> wcomposed = Maps.newHashMap(alternativesComparison.getWeightReference());
		int p = keys.size() - 1;
		double scoreX;
		double scoreY;

		do {
			wcomposed.put(temp.get(keys.get(p)), alternativesComparison.getWeight().get((temp.get(keys.get(p)))));
			setC.add(temp.get(keys.get(p)));

			scoreX = LabreucheTools.score(alternativesComparison.getX(), wcomposed);
			scoreY = LabreucheTools.score(alternativesComparison.getY(), wcomposed);

			p--;
		} while (scoreX < scoreY);

		LOGGER.info("NOA true");
		labreucheOutput = new NOAOutput(alternativesComparison, setC);

		return true;

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

		LOGGER.debug(
				"Delta " + alternativesComparison.getX().getName() + " > " + alternativesComparison.getY().getName()
						+ " : " + Utils.showVector(alternativesComparison.getDelta().values()) + "\n");

		do {
			pi.clear();
			bigA.clear();
			bigB.clear();
			subsets.clear();
			setCIVT.clear();

			subsets = LabreucheTools.allSubset(new ArrayList<>(alternativesComparison.getCriteria()));

			for (List<Criterion> subset : subsets) {
				Couple<Double, List<Criterion>> res = LabreucheTools.d_eu(subset, alternativesComparison.getWeight(),
						alternativesComparison.getDelta());
				dEU = res.getLeft().doubleValue();
				pi = res.getRight();

				if (dEU > 0 && pi.containsAll(subset) && subset.containsAll(pi)) {
					setCIVT.add(subset);
				}

				pi.clear();
			}

			setCIVT = LabreucheTools.sortLexi(setCIVT, alternativesComparison.getWeight(),
					alternativesComparison.getDelta());

			LOGGER.debug("\n setCIVT : ");
			for (List<Criterion> l : setCIVT)
				LOGGER.debug(Utils.showCriteria(l) + " "
						+ LabreucheTools.d_eu(l, alternativesComparison.getWeight(), alternativesComparison.getDelta())
						+ " " + Utils.showCriteria(LabreucheTools.pi_min(l, alternativesComparison.getWeight(),
								alternativesComparison.getDelta())));

			bigC = algo(bigA, null, 0);
			size++;

		} while (bigC == null && size <= alternativesComparison.getCriteria().size());

		ivtPermutations = bigC;

		if (ivtPermutations == null) {
			return false;
		}

		// Start to determine R*

		MutableGraph<Criterion> cpls = GraphBuilder.directed().build();
		ImmutableGraph<Criterion> rS;

		LOGGER.debug("Minimal permutation : " + Utils.showSet(ivtPermutations) + "\n");

		for (List<Criterion> l : ivtPermutations) {

			rS = LabreucheTools.couples_ofG(l, alternativesComparison.getWeight(), alternativesComparison.getDelta());

			for (EndpointPair<Criterion> cp : rS.edges()) {
				cpls.putEdge(cp.nodeU(), cp.nodeV());
			}
		}

		ImmutableGraph<Criterion> rStar = LabreucheTools.buildRStarG(ImmutableGraph.copyOf(cpls));

		// R* determined

		LOGGER.info("IVT true");
		labreucheOutput = new IVTOutput(alternativesComparison, rStar, epsilon);

		return true;
	}

	private boolean tryRMGAVG() {
		Preconditions.checkState(labreucheOutput == null);

		double maxW = getMaxW();

		if (maxW <= epsilon) {
			LOGGER.info("RMGAVG true");

			labreucheOutput = new RMGAVGOutput(alternativesComparison);

			return true;
		}

		LOGGER.info("RMGAVG false");

		return false;
	}

	private double getMaxW() {
		double maxW = Double.MIN_VALUE;

		for (Double v : alternativesComparison.getWeight().values()) {
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

		if (maxW > epsilon) {
			LOGGER.info("RMGCOMP true");

			labreucheOutput = new RMGCOMPOutput(alternativesComparison, epsilon);

			return true;
		}

		LOGGER.info("RMGCOMP false");

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

	public void showProblem() {
		
		StringBuilder bld = new StringBuilder();


		bld.append("****************************************************************"
				+ "\n" + "*                                                              *" 
				+ "\n" + "*         Recommender system based on Labreuche Model          *" 
				+ "\n" + "*                                                              *" 
				+ "\n" + "****************************************************************" + "\n");

		bld.append("\n    Criteria    <-   Weight : \n");

		for (Criterion c : alternativesComparison.getWeight().keySet())
			bld.append("\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = "
					+ alternativesComparison.getWeight().get(c));

		bld.append("\n \n Alternatives : ");

		bld.append("\n" + "	" + alternativesComparison.getX().getName() + " " + " : "
				+ Utils.showVector(alternativesComparison.getX().getEvaluations().values()));
		
		bld.append("\n" + "	" + alternativesComparison.getY().getName() + " " + " : "
				+ Utils.showVector(alternativesComparison.getY().getEvaluations().values()));

		bld.append("\n" + "			Alternatives ranked");
		bld.append("\n" + alternativesComparison.getX().getName() + " = "
				+ LabreucheTools.score(alternativesComparison.getX(), alternativesComparison.getWeight()));
		bld.append("\n" + alternativesComparison.getY().getName() + " = "
				+ LabreucheTools.score(alternativesComparison.getY(), alternativesComparison.getWeight()));

		bld.append("Explanation why " + alternativesComparison.getX().getName() + " is better than "
				+ alternativesComparison.getY().getName() + " :");

		LOGGER.info(bld.toString());
	}

	public void solvesProblem() {
		showProblem();

		LabreucheOutput lo = getExplanation();

		LabreucheArguer arg = new LabreucheArguer();
		String explanation = arg.argue(lo);

		LOGGER.info(explanation);
	}
}