/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * Copyright (C) 2021- SmartUt contributors
 *
 * SmartUt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * SmartUt is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SmartUt. If not, see <http://www.gnu.org/licenses/>.
 */
package org.smartut.coverage.dataflow;

import org.smartut.Properties.Criterion;
import org.smartut.coverage.FitnessFunctions;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluate fitness of a test suite with respect to all of its def-use pairs
 */
public class AllDefsCoverageSuiteFitness extends TestSuiteFitnessFunction {
	private static final long serialVersionUID = 1L;

	static List<? extends TestFitnessFunction> goals = FitnessFunctions.getFitnessFactory(Criterion.ALLDEFS).getCoverageGoals();

	/** Constant <code>totalGoals=goals.size()</code> */
	public static int totalGoals = goals.size();


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.ga.FitnessFunction#getFitness(org.
	 * smartut.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public double getFitness(TestSuiteChromosome suite) {
		logger.trace("Calculating defuse fitness");

		List<ExecutionResult> results = runTestSuite(suite);
		double fitness = 0.0;

		Set<TestFitnessFunction> coveredGoals = new HashSet<>();

		for (TestFitnessFunction goal : goals) {
			if (coveredGoals.contains(goal))
				continue;

			double goalFitness = 2.0;
			for (ExecutionResult result : results) {
				TestChromosome tc = new TestChromosome();
				tc.setTestCase(result.test);
				double resultFitness = goal.getFitness(tc, result);
				if (resultFitness < goalFitness)
					goalFitness = resultFitness;
				if (goalFitness == 0.0) {
					result.test.addCoveredGoal(goal);
					// System.out.println(goal.toString());
					// System.out.println(result.test.toCode());
					// System.out.println(resultFitness);
					coveredGoals.add(goal);
					break;
				}
			}
			fitness += goalFitness;
		}

		updateIndividual(suite, fitness);
		setSuiteCoverage(suite, coveredGoals);

		return fitness;
	}

	private void setSuiteCoverage(TestSuiteChromosome suite,
	        Set<TestFitnessFunction> coveredGoals) {

		if (goals.size() > 0)
			suite.setCoverage(this, coveredGoals.size() / (double) goals.size());
		else
			suite.setCoverage(this, 1.0);
		
		suite.setNumOfCoveredGoals(this, coveredGoals.size());
	}
}
