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

package org.smartut.coverage;

import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.coverage.ambiguity.AmbiguityCoverageSuiteFitness;
import org.smartut.coverage.rho.RhoCoverageSuiteFitness;
import org.smartut.TestGenerationContext;
import org.smartut.rmi.ClientServices;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.*;

/**
 * @author Gordon Fraser
 *
 */
public class CoverageCriteriaAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(CoverageCriteriaAnalyzer.class);

    private static Map<String, StringBuffer> coverageBitString = new TreeMap<>();

    private static boolean isMutationCriterion(Properties.Criterion criterion) {
        switch (criterion) {
            case MUTATION:
            case WEAKMUTATION:
            case STRONGMUTATION:
            case ONLYMUTATION:
                return true;
            default:
                return false;
        }
    }

    private static void reinstrument(TestSuiteChromosome testSuite, Properties.Criterion criterion) {

        if (ArrayUtil.contains(Properties.SECONDARY_OBJECTIVE, Properties.SecondaryObjective.IBRANCH)) {
            ExecutionTracer.enableContext();
        }
        if (!ExecutionTracer.isTraceCallsEnabled()) {
            ExecutionTracer.enableTraceCalls();
        }

        testSuite.setChanged(true);
        for (TestChromosome test : testSuite.getTestChromosomes()) {
            test.setChanged(true);
            test.clearCachedResults(); // clears last execution result and last mutation result
        }

        Properties.Criterion[] oldCriterion = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
        Properties.CRITERION = new Properties.Criterion[]{criterion};

        logger.info("Re-instrumenting for criterion: " + criterion);
        TestGenerationContext.getInstance().resetContext();

        // Need to load class explicitly in case there are no test cases.
        // If there are tests, then this is redundant
        Properties.getInitializedTargetClass();

        // TODO: Now all existing test cases have reflection objects pointing to the wrong classloader
        logger.info("Changing classloader of test suite for criterion: " + criterion);

        for (TestChromosome test : testSuite.getTestChromosomes()) {
            DefaultTestCase dtest = (DefaultTestCase) test.getTestCase();
            dtest.changeClassLoader(TestGenerationContext.getInstance().getClassLoaderForSUT());
        }
        Properties.CRITERION = oldCriterion;
    }

    public static void analyzeCriteria(TestSuiteChromosome testSuite, String criteria) {

        // If coverage of target criteria is not already measured
        if (!Properties.COVERAGE) {
            for (Criterion c : Properties.CRITERION) {
                // Analyse coverage for enabled criteria
                // LoggingUtils.getSmartUtLogger().info("  - " + c.name());
                logger.debug("Measuring coverage of target criterion {}", c);
                analyzeCoverage(testSuite, c.name());
            }
        }

        boolean reinstrumented = false;
        for (String extraCriterion : Arrays.asList(criteria.toUpperCase().split(","))) {
            if (extraCriterion.equals("CBRANCH")) {
                Properties.INSTRUMENT_METHOD_CALLS = true;
            }
            // Analyse coverage for extra criteria
            if (!ArrayUtil.contains(Properties.CRITERION, extraCriterion)) {
                logger.debug("Measuring additional coverage of target criterion {}", extraCriterion);
                reinstrumented = true;
                analyzeCoverage(testSuite, extraCriterion);
            }
        }

        // If reinstrumentation happened, we might need to restore the original instrumentation
        // otherwise things like the MutationPool may not be up to date
        if (reinstrumented) {
            TestGenerationContext.getInstance().resetContext();
            Properties.getInitializedTargetClass();
        }
    }

    private static void analyzeCoverage(TestSuiteChromosome testSuite, String criterion) {
        try {
            Properties.Criterion crit = Properties.Criterion.valueOf(criterion.toUpperCase());
            analyzeCoverage(testSuite, crit);
        } catch (IllegalArgumentException e) {
            LoggingUtils.getSmartUtLogger().info("* Unknown coverage criterion: " + criterion);
        }
    }

    public static RuntimeVariable getCoverageVariable(Properties.Criterion criterion) {
        switch (criterion) {
            case ALLDEFS:
                return RuntimeVariable.AllDefCoverage;
            case BRANCH:
                return RuntimeVariable.BranchCoverage;
            case CBRANCH:
                return RuntimeVariable.CBranchCoverage;
            case EXCEPTION:
                return RuntimeVariable.ExceptionCoverage;
            case DEFUSE:
                return RuntimeVariable.DefUseCoverage;
            case STATEMENT:
                return RuntimeVariable.StatementCoverage;
            case RHO:
                return RuntimeVariable.RhoScore;
            case AMBIGUITY:
                return RuntimeVariable.AmbiguityScore;
            case STRONGMUTATION:
            case MUTATION:
                return RuntimeVariable.MutationScore;
            case ONLYMUTATION:
                return RuntimeVariable.OnlyMutationScore;
            case WEAKMUTATION:
                return RuntimeVariable.WeakMutationScore;
            case ONLYBRANCH:
                return RuntimeVariable.OnlyBranchCoverage;
            case METHODTRACE:
                return RuntimeVariable.MethodTraceCoverage;
            case METHOD:
                return RuntimeVariable.MethodCoverage;
            case METHODNOEXCEPTION:
                return RuntimeVariable.MethodNoExceptionCoverage;
            case ONLYLINE:
            case LINE:
                return RuntimeVariable.LineCoverage;
            case OUTPUT:
                return RuntimeVariable.OutputCoverage;
            case INPUT:
                return RuntimeVariable.InputCoverage;
            case IBRANCH:
                return RuntimeVariable.IBranchCoverage;
            case TRYCATCH:
                return RuntimeVariable.TryCatchCoverage;
            default:
                throw new RuntimeException("Criterion not supported: " + criterion);

        }
    }

    public static void analyzeCoverage(TestSuiteChromosome testSuite) {

        LoggingUtils.getSmartUtLogger().info("* Going to analyze the coverage criteria");

        Properties.Criterion[] criteria = Properties.CRITERION;

        /*
            As we analyze exactly the same criteria used during the search, we should do not
            need to re-instrument and re-run the tests
         */
        boolean recalculate = false;

        for (Properties.Criterion pc : criteria) {
            LoggingUtils.getSmartUtLogger().info("* Coverage analysis for criterion " + pc);

            analyzeCoverage(testSuite, pc, recalculate);
        }
    }

    public static void analyzeCoverage(TestSuiteChromosome testSuite, Properties.Criterion criterion) {
        analyzeCoverage(testSuite,criterion,true);
    }

    private static void analyzeCoverage(TestSuiteChromosome testSuite, Properties.Criterion criterion, boolean recalculate) {

        TestSuiteChromosome testSuiteCopy = testSuite.clone();

        TestFitnessFactory<? extends TestFitnessFunction> factory = FitnessFunctions.getFitnessFactory(criterion);

        if(recalculate) {
            reinstrument(testSuiteCopy, criterion);

            for (TestChromosome test : testSuiteCopy.getTestChromosomes()) {
                test.getTestCase().clearCoveredGoals();
                test.clearCachedResults();

                // independently of mutation being a main or secondary criteria,
                // test cases have to be 'changed'. with this, isCovered() will
                // re-execute test cases and it will be able to find the covered goals
                if (isMutationCriterion(criterion)) {
                    test.setChanged(true);
                }
            }
        }

        List<? extends TestFitnessFunction> goals = factory.getCoverageGoals();
        Collections.sort(goals);

        StringBuffer buffer = new StringBuffer(goals.size());
        int covered = 0;

        for (TestFitnessFunction goal : goals) {
            if (goal.isCoveredBy(testSuiteCopy)) {
                logger.debug("Goal {} is covered", goal);
                covered++;
                buffer.append("1");
            } else {
                logger.debug("Goal {} is not covered", goal);
                buffer.append("0");
                if (Properties.PRINT_MISSED_GOALS)
                    LoggingUtils.getSmartUtLogger().info(" - Missed goal {}", goal.toString());
            }
        }

        coverageBitString.put(criterion.name(), buffer);
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.CoverageBitString,
                coverageBitString.size() == 0 ? "0" : coverageBitString.values().toString().replace("[", "").replace("]", "").replace(", ", ""));

        RuntimeVariable bitStringVariable = getBitStringVariable(criterion);
        if (bitStringVariable != null) {
            String goalBitString = buffer.toString();
            ClientServices.getInstance().getClientNode().trackOutputVariable(bitStringVariable, goalBitString);
        }

        if (goals.isEmpty()) {
            if (criterion == Properties.Criterion.MUTATION
                    || criterion == Properties.Criterion.STRONGMUTATION) {
                ClientServices.getInstance().getClientNode().trackOutputVariable(
                        RuntimeVariable.MutationScore, 1.0);
            }
            LoggingUtils.getSmartUtLogger().info("* Coverage of criterion " + criterion + ": 100% (no goals)");
            ClientServices.getInstance().getClientNode().trackOutputVariable(getCoverageVariable(criterion), 1.0);
        } else {

            ClientServices.getInstance().getClientNode().trackOutputVariable(
                    getCoverageVariable(criterion), (double) covered / (double) goals.size());

            if (criterion == Properties.Criterion.MUTATION
                    || criterion == Properties.Criterion.STRONGMUTATION) {
                ClientServices.getInstance().getClientNode().trackOutputVariable(
                        RuntimeVariable.MutationScore, (double) covered / (double) goals.size());
            }

            LoggingUtils.getSmartUtLogger().info("* Coverage of criterion "
                    + criterion
                    + ": "
                    + NumberFormat.getPercentInstance().format((double) covered / (double) goals.size()));

            LoggingUtils.getSmartUtLogger().info("* Total number of goals: " + goals.size());
            LoggingUtils.getSmartUtLogger().info("* Number of covered goals: " + covered);
        }

        // FIXME it works, but needs a better way of handling this
        if (criterion == Properties.Criterion.RHO) {
            RhoCoverageSuiteFitness rho = new RhoCoverageSuiteFitness();
            ClientServices.getInstance().getClientNode().trackOutputVariable(
                    RuntimeVariable.RhoScore, Math.abs(0.5 - rho.getFitness(testSuite)));
        } else if (criterion == Properties.Criterion.AMBIGUITY) {
            AmbiguityCoverageSuiteFitness ag = new AmbiguityCoverageSuiteFitness();
            ClientServices.getInstance().getClientNode().trackOutputVariable(
                    RuntimeVariable.AmbiguityScore, ag.getFitness(testSuite));
        }
    }

    public static RuntimeVariable getBitStringVariable(Properties.Criterion criterion) {
        switch (criterion) {
            case EXCEPTION:
                return RuntimeVariable.ExceptionCoverageBitString;
            case DEFUSE:
                return RuntimeVariable.DefUseCoverageBitString;
            case ALLDEFS:
                return RuntimeVariable.AllDefCoverageBitString;
            case BRANCH:
                return RuntimeVariable.BranchCoverageBitString;
            case CBRANCH:
                return RuntimeVariable.CBranchCoverageBitString;
            case IBRANCH:
                return RuntimeVariable.IBranchCoverageBitString;
            case ONLYBRANCH:
                return RuntimeVariable.OnlyBranchCoverageBitString;
            case MUTATION:
            case STRONGMUTATION:
                return RuntimeVariable.MutationCoverageBitString;
            case WEAKMUTATION:
                return RuntimeVariable.WeakMutationCoverageBitString;
            case ONLYMUTATION:
                return RuntimeVariable.OnlyMutationCoverageBitString;
            case METHODTRACE:
                return RuntimeVariable.MethodTraceCoverageBitString;
            case METHOD:
                return RuntimeVariable.MethodCoverageBitString;
            case METHODNOEXCEPTION:
                return RuntimeVariable.MethodNoExceptionCoverageBitString;
            case OUTPUT:
                return RuntimeVariable.OutputCoverageBitString;
            case INPUT:
                return RuntimeVariable.InputCoverageBitString;
            case STATEMENT:
                return RuntimeVariable.StatementCoverageBitString;
            case LINE:
            case ONLYLINE:
                return RuntimeVariable.LineCoverageBitString;
            case TRYCATCH:
                return null;
            default:
                logger.debug("Criterion not supported: " + criterion);
                return null;
        }
    }
}
