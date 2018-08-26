package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

/**
 *
 * Immutable.
 */
public class AlternativesComparison {

	private ImmutableMap<Criterion, Double> weights;
	private Alternative x;
	private Alternative y;
	private Logger logger;

	/**
	 * The parameters must be such that x and y do not have the same evaluation.
	 *
	 * @param x
	 * @param y
	 * @param weights
	 */
	public AlternativesComparison(Alternative x, Alternative y, Map<Criterion, Double> weights) {
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.weights = ImmutableMap.copyOf(requireNonNull(weights));
		/** TODO check that value ≠. */
		assert !weights.isEmpty();
		this.logger = LoggerFactory.getLogger(LabreucheModel.class);
		
		(this).reorderXY();
	}

	/**
	 * @return the criteria in favor of y better than x.
	 */
	public ImmutableSet<Criterion> getNegativeCriteria() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) < evalY.get(crit));
	}

	/**
	 * @return the criteria in favor of x better than y.
	 */
	public ImmutableSet<Criterion> getPositiveCriteria() {
		Map<Criterion, Double> evalX = this.x.getEvaluations();
		Map<Criterion, Double> evalY = this.y.getEvaluations();

		return getFilteredCriteria((crit) -> evalX.get(crit) > evalY.get(crit));
	}

	/**
	 * @return a map containing, for each criterion, the difference in evaluation on
	 *         that criterion: evaluation of x minus evaluation of y.
	 */
	public ImmutableMap<Criterion, Double> getDelta() {
		Builder<Criterion, Double> builder = ImmutableMap.builder();

		for (Criterion c : weights.keySet()) {
			builder.put(c, x.getEvaluations().get(c) - y.getEvaluations().get(c));
		}

		return builder.build();
	}

	public ImmutableMap<Criterion, Double> getWeight() {
		return this.weights;
	}

	public Alternative getX() {
		return this.x;
	}

	public Alternative getY() {
		return this.y;
	}

	private ImmutableSet<Criterion> getFilteredCriteria(Predicate<Criterion> predicate) {
		Stream<Criterion> allCriteriaStream = weights.keySet().stream().sequential();
		Stream<Criterion> criteriaFilteredStream = allCriteriaStream.filter(predicate);
		ImmutableSet<Criterion> criteriaFiltered = criteriaFilteredStream.collect(ImmutableSet.toImmutableSet());
		return criteriaFiltered;
	}

	/**
	 * @return non empty.
	 */
	public ImmutableSet<Criterion> getCriteria() {
		return weights.keySet();
	}
	
	private void reorderXY() {
		double scoreX = Tools.score(x, weights);
		double scoreY = Tools.score(y, weights);
				
		if(scoreY > scoreX) {
			Alternative tmp = this.x;
			this.x = this.y;
			this.y = tmp;
		}
	}
	
	public ImmutableMap<Criterion, Double> getWeightReference(){
		Builder<Criterion,Double> weightsReference = ImmutableMap.builder();
		
		for(Criterion c : weights.keySet()) {
			weightsReference.put(c, 1.0/weights.keySet().size());
		}
		
		return weightsReference.build();
	}
	
 	public void showProblem() {

		String display = "****************************************************************";
		display += "\n" + "*                                                              *";
		display += "\n" + "*         Recommender system based on Labreuche Model          *";
		display += "\n" + "*                                                              *";
		display += "\n" + "****************************************************************" + "\n";

		display += "\n    Criteria    <-   Weight : \n";

		for (Criterion c : this.weights.keySet())
			display += "\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = " + this.weights.get(c);

		display += "\n \n Alternatives : ";

		display += "\n" + "	" + this.x.getName() + " " + " : " + Tools.displayAsVector(this.x);
		display += "\n" + "	" + this.y.getName() + " " + " : " + Tools.displayAsVector(this.y);
			
		
		display += "\n" + "			Alternatives ranked";
		display += "\n" + x.getName() + " = " + Tools.score(x, weights);
		display += "\n" + y.getName() + " = " + Tools.score(y, weights);

		display = "Explanation why " + this.x.getName() + " is better than " + this.y.getName() + " :";

		logger.info(display);
	}

	
}
