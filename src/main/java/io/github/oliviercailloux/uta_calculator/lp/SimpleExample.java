package io.github.oliviercailloux.uta_calculator.lp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class SimpleExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExample.class);

	static {
		System.loadLibrary("jniortools");
	}

	private static void runLinearProgrammingExample(String solverType, boolean printModel) {
		MPSolver solver = new MPSolver("LinearProgrammingExample",
				MPSolver.OptimizationProblemType.valueOf(solverType));
		double infinity = MPSolver.infinity();

		// x1, x2 and x3 are continuous non-negative variables.
		MPVariable x1 = solver.makeNumVar(0.0, infinity, "x1");
		MPVariable x2 = solver.makeNumVar(0.0, infinity, "x2");
		MPVariable x3 = solver.makeNumVar(0.0, infinity, "x3");

		// Maximize 10 * x1 + 6 * x2 + 4 * x3.
		MPObjective objective = solver.objective();
		objective.setCoefficient(x1, 10);
		objective.setCoefficient(x2, 6);
		objective.setCoefficient(x3, 4);
		objective.setMaximization();

		// x1 + x2 + x3 <= 100.
		MPConstraint c0 = solver.makeConstraint(-infinity, 100.0);
		c0.setCoefficient(x1, 1);
		c0.setCoefficient(x2, 1);
		c0.setCoefficient(x3, 1);

		// 10 * x1 + 4 * x2 + 5 * x3 <= 600.
		MPConstraint c1 = solver.makeConstraint(-infinity, 600.0);
		c1.setCoefficient(x1, 10);
		c1.setCoefficient(x2, 4);
		c1.setCoefficient(x3, 5);

		// 2 * x1 + 2 * x2 + 6 * x3 <= 300.
		MPConstraint c2 = solver.makeConstraint(-infinity, 300.0);
		c2.setCoefficient(x1, 2);
		c2.setCoefficient(x2, 2);
		c2.setCoefficient(x3, 6);

		LOGGER.info("Number of variables = " + solver.numVariables());
		LOGGER.info("Number of constraints = " + solver.numConstraints());

		if (printModel) {
			String model = solver.exportModelAsLpFormat(false);
			LOGGER.info(model);
		}

		final MPSolver.ResultStatus resultStatus = solver.solve();

		// Check that the problem has an optimal solution.
		if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
			LOGGER.debug("The problem does not have an optimal solution!");
			return;
		}

		// Verify that the solution satisfies all constraints (when using solvers
		// others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
		if (!solver.verifySolution(/* tolerance= */1e-7, /* logErrors= */true)) {
			LOGGER.debug("The solution returned by the solver violated the" + " problem constraints by at least 1e-7");
			return;
		}

		LOGGER.info("Problem solved in " + solver.wallTime() + " milliseconds");

		// The objective value of the solution.
		LOGGER.info("Optimal objective value = " + solver.objective().value());

		// The value of each variable in the solution.
		LOGGER.info("x1 = " + x1.solutionValue());
		LOGGER.info("x2 = " + x2.solutionValue());
		LOGGER.info("x3 = " + x3.solutionValue());

	}

	public static void main(String[] args) {
		runLinearProgrammingExample("GLOP_LINEAR_PROGRAMMING", true);
	}

}