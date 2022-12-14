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
package org.smartut.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.smartut.ProgressMonitor;
import org.smartut.Properties;
import org.smartut.Properties.Algorithm;
import org.smartut.TestGenerationContext;
import org.smartut.coverage.FitnessFunctions;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.graphs.cfg.CFGMethodAdapter;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.rmi.ClientServices;
import org.smartut.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxFitnessEvaluationsStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxGenerationStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxTestsStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.StoppingCondition;
import org.smartut.ga.stoppingconditions.ZeroFitnessStoppingCondition;
import org.smartut.setup.TestCluster;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.utils.LoggingUtils;

import static java.util.stream.Collectors.toCollection;

/**
 * This is the abstract superclass of all techniques to generate a set of tests
 * for a target class, which does not necessarily require the use of a GA.
 * 
 * Postprocessing is not done as part of the test generation strategy.
 * 
 * @author gordon
 *
 */
public abstract class TestGenerationStrategy {

	/**
	 * Generate a set of tests; assume that all analyses are already completed
	 * @return
	 */
	public abstract TestSuiteChromosome generateTests();
	
	/** There should only be one */
	protected final ProgressMonitor<TestSuiteChromosome> progressMonitor = new ProgressMonitor<>();

	/** There should only be one */
	protected ZeroFitnessStoppingCondition<TestSuiteChromosome> zeroFitness =
			new ZeroFitnessStoppingCondition<>();
	
	/** There should only be one */
	protected StoppingCondition<TestSuiteChromosome> globalTime =
			new GlobalTimeStoppingCondition<>();

    protected void sendExecutionStatistics() {
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Statements_Executed, MaxStatementsStoppingCondition.getNumExecutedStatements());
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Tests_Executed, MaxTestsStoppingCondition.getNumExecutedTests());
    }
    
    /**
     * Convert criterion names to test suite fitness functions
     * @return
     */
	protected List<TestSuiteFitnessFunction> getFitnessFunctions() {
	    List<TestSuiteFitnessFunction> ffs = new ArrayList<>();
	    for (int i = 0; i < Properties.CRITERION.length; i++) {
	    	TestSuiteFitnessFunction newFunction = FitnessFunctions.getFitnessFunction(Properties.CRITERION[i]);
	    	
	    	// If this is compositional fitness, we need to make sure
	    	// that all functions are consistently minimization or 
	    	// maximization functions
	    	if(Properties.ALGORITHM != Algorithm.NSGAII && Properties.ALGORITHM != Algorithm.SPEA2) {
	    		for(TestSuiteFitnessFunction oldFunction : ffs) {			
	    			if(oldFunction.isMaximizationFunction() != newFunction.isMaximizationFunction()) {
	    				StringBuffer sb = new StringBuffer();
	    				sb.append("* Invalid combination of fitness functions: ");
	    				sb.append(oldFunction.toString());
	    				if(oldFunction.isMaximizationFunction())
	    					sb.append(" is a maximization function ");
	    				else
	    					sb.append(" is a minimization function ");
	    				sb.append(" but ");
	    				sb.append(newFunction.toString());
	    				if(newFunction.isMaximizationFunction())
	    					sb.append(" is a maximization function ");
	    				else
	    					sb.append(" is a minimization function ");
	    				LoggingUtils.getSmartUtLogger().info(sb.toString());
	    				throw new RuntimeException("Invalid combination of fitness functions");
	    			}
	    		}
	    	}
	        ffs.add(newFunction);

	    }

		return ffs;
	}
	
	/**
	 * Convert criterion names to factories for test case fitness functions
	 * @return
	 */
	public static List<TestFitnessFactory<? extends TestFitnessFunction>> getFitnessFactories() {
		return Arrays.stream(Properties.CRITERION)
				.map(FitnessFunctions::getFitnessFactory)
				.collect(toCollection(ArrayList::new));
	}
	
	/**
	 * Check if the budget has been used up. The GA will do this check
	 * on its own, but other strategies (e.g. random) may depend on this function.
	 * 
	 * @param chromosome
	 * @param stoppingCondition
	 * @return
	 */
	protected boolean isFinished(TestSuiteChromosome chromosome,
								 StoppingCondition<TestSuiteChromosome> stoppingCondition) {
		if (stoppingCondition.isFinished())
			return true;

		if (Properties.STOP_ZERO) {
			if (chromosome.getFitness() == 0.0)
				return true;
		}

		if (!(stoppingCondition instanceof MaxTimeStoppingCondition)) {
			return globalTime.isFinished();
		}

		return false;
	}
	
	/**
	 * Convert property to actual stopping condition
	 * @return
	 */
	protected StoppingCondition<TestSuiteChromosome> getStoppingCondition() {
		switch (Properties.STOPPING_CONDITION) {
		case MAXGENERATIONS:
			return new MaxGenerationStoppingCondition<>();
		case MAXFITNESSEVALUATIONS:
			return new MaxFitnessEvaluationsStoppingCondition<>();
		case MAXTIME:
			return new MaxTimeStoppingCondition<>();
		case MAXTESTS:
			return new MaxTestsStoppingCondition<>();
		case MAXSTATEMENTS:
			return new MaxStatementsStoppingCondition<>();
		default:
			return new MaxGenerationStoppingCondition<>();
		}
	}

	protected boolean canGenerateTestsForSUT() {
		if (TestCluster.getInstance().getNumTestCalls() == 0) {
			final InstrumentingClassLoader cl = TestGenerationContext.getInstance().getClassLoaderForSUT();
			final int numMethods = CFGMethodAdapter.getNumMethods(cl);
			return !(Properties.P_REFLECTION_ON_PRIVATE <= 0.0) && numMethods != 0;
		}
		return true;
	}
}
