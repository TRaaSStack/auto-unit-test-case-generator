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
package org.smartut.coverage.io.output;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.smartut.Properties;
import org.smartut.ga.archive.Archive;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;

/**
 * @author Jose Miguel Rojas
 */
public class OutputCoverageSuiteFitness extends TestSuiteFitnessFunction {

    private static final long serialVersionUID = -8345906214972153096L;

    //public final int numBranchlessMethods;
    public final int totalGoals;
    private final Set<OutputCoverageTestFitness> outputCoverageGoals = new LinkedHashSet<>();

    private final Set<OutputCoverageTestFitness> toRemoveGoals = new LinkedHashSet<>();
    private final Set<OutputCoverageTestFitness> removedGoals  = new LinkedHashSet<>();

    // Some stuff for debug output
    public int maxCoveredGoals = 0;
    public double bestFitness = Double.MAX_VALUE;

    public OutputCoverageSuiteFitness() {
        // Add observer
        TestCaseExecutor executor = TestCaseExecutor.getInstance();
        OutputObserver observer = new OutputObserver();
        executor.addObserver(observer);
        //TODO: where to remove observer?: executor.removeObserver(observer);

        determineCoverageGoals();

        totalGoals = outputCoverageGoals.size();
    }

    /**
     * Initialize the set of known coverage goals
     */
    private void determineCoverageGoals() {
        List<OutputCoverageTestFitness> goals = new OutputCoverageFactory().getCoverageGoals();
        for (OutputCoverageTestFitness goal : goals) {
            outputCoverageGoals.add(goal);
			if(Properties.TEST_ARCHIVE)
				Archive.getArchiveInstance().addTarget(goal);
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Execute all tests and count covered output goals
     */
    @Override
    public double getFitness(TestSuiteChromosome suite) {
        logger.trace("Calculating test suite fitness");
        double fitness = 0.0;

        List<ExecutionResult> results = runTestSuite(suite);

        boolean hasTimeoutOrTestException = false;
        for (ExecutionResult result : results) {
            if (result.hasTimeout() || result.hasTestException()) {
                hasTimeoutOrTestException = true;
                break;
            }
        }

        Set<TestFitnessFunction> setOfCoveredGoals = new LinkedHashSet<>();
        if (hasTimeoutOrTestException) {
            logger.info("Test suite has timed out, setting fitness to max value " + totalGoals);
            fitness = totalGoals;
        } else
            fitness = computeDistance(results, setOfCoveredGoals);

        int coveredGoals = setOfCoveredGoals.size() + removedGoals.size();

        if (totalGoals > 0)
            suite.setCoverage(this, (double) coveredGoals / (double) totalGoals);
        else
            suite.setCoverage(this, 1.0);

        suite.setNumOfCoveredGoals(this, coveredGoals);

        printStatusMessages(suite, coveredGoals, fitness);
        updateIndividual(suite, fitness);

        assert (coveredGoals <= totalGoals) : "Covered " + coveredGoals + " vs total goals " + totalGoals;
        assert (fitness >= 0.0);
        assert (fitness != 0.0 || coveredGoals == totalGoals) : "Fitness: " + fitness + ", "
                + "coverage: " + coveredGoals + "/" + totalGoals;
        assert (suite.getCoverage(this) <= 1.0) && (suite.getCoverage(this) >= 0.0) : "Wrong coverage value "
                + suite.getCoverage(this);

        return fitness;
    }

    @Override
    public boolean updateCoveredGoals() {
        if (!Properties.TEST_ARCHIVE) {
            return false;
        }

        for (OutputCoverageTestFitness goal : this.toRemoveGoals) {
            if (this.outputCoverageGoals.remove(goal)) {
                this.removedGoals.add(goal);
            } else {
                throw new IllegalStateException("goal to remove not found");
            }
        }

        this.toRemoveGoals.clear();
        logger.info("Current state of archive: " + Archive.getArchiveInstance().toString());

        return true;
    }

    public double computeDistance(List<ExecutionResult> results, Set<TestFitnessFunction> setOfCoveredGoals) {

        Map<OutputCoverageTestFitness, Double> mapDistances = new LinkedHashMap<>();
        for (OutputCoverageTestFitness testFitness : this.outputCoverageGoals) {
          mapDistances.put(testFitness, 1.0);
        }

        for (ExecutionResult result : results) {
            if (result.hasTimeout() || result.hasTestException()) {
                continue;
            }

            TestChromosome test = new TestChromosome();
            test.setTestCase(result.test);
            test.setLastExecutionResult(result);
            test.setChanged(false);

            for (final OutputCoverageTestFitness testFitness : this.outputCoverageGoals) {
                if (!mapDistances.containsKey(testFitness)) {
                    continue;
                }

                double distance = testFitness.getFitness(test, result); // archive is updated by the TestFitnessFunction class

                mapDistances.put(testFitness, Math.min(distance, mapDistances.get(testFitness)));

                if (distance == 0.0) {
                    mapDistances.remove(testFitness);
                    setOfCoveredGoals.add(testFitness); // helper to count the number of covered goals
                    this.toRemoveGoals.add(testFitness); // goal to not be considered by the next iteration of the evolutionary algorithm
                }
            }
        }

        return mapDistances.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Some useful debug information
     *
     * @param coveredGoals
     * @param fitness
     */
    private void printStatusMessages(TestSuiteChromosome suite,
            int coveredGoals, double fitness) {
        if (coveredGoals > maxCoveredGoals) {
            logger.info("(Output Goals) Best individual covers " + coveredGoals + "/"
                    + totalGoals + " output goals");
            maxCoveredGoals = coveredGoals;
            logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
                    + suite.totalLengthOfTestCases());

        }
        if (fitness < bestFitness) {
            logger.info("(Fitness) Best individual covers " + coveredGoals + "/"
                    + totalGoals + " output goals");
            bestFitness = fitness;
            logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
                    + suite.totalLengthOfTestCases());

        }
    }
}
