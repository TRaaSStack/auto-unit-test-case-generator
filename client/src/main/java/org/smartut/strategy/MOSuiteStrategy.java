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

import org.smartut.ClientProcess;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.TestSuiteChromosomeFactoryMock;
import org.smartut.ga.TestSuiteFitnessFunctionMock;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.result.TestGenerationResultBuilder;
import org.smartut.rmi.ClientServices;
import org.smartut.rmi.service.ClientState;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testcase.factories.RandomLengthTestFactory;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;

import java.util.ArrayList;
import java.util.List;

/**
 * Test generation with MOSA
 * 
 * @author Annibale,Fitsum
 *
 */
public class MOSuiteStrategy extends TestGenerationStrategy {

	@Override	
	public TestSuiteChromosome generateTests() {
		// Currently only LIPS uses its own Archive
		if (Properties.ALGORITHM == Properties.Algorithm.LIPS) {
			Properties.TEST_ARCHIVE = false;
		}

		// Set up search algorithm
		PropertiesSuiteGAFactory algorithmFactory = new PropertiesSuiteGAFactory();

		GeneticAlgorithm<TestSuiteChromosome> algorithm = algorithmFactory.getSearchAlgorithm();
		
		// Override chromosome factory
		// TODO handle this better by introducing generics
		ChromosomeFactory<TestSuiteChromosome> factory =
				new TestSuiteChromosomeFactoryMock(new RandomLengthTestFactory());
		algorithm.setChromosomeFactory(factory);
		
		if(Properties.SERIALIZE_GA || Properties.CLIENT_ON_THREAD)
			TestGenerationResultBuilder.getInstance().setGeneticAlgorithm(algorithm);

		long startTime = System.currentTimeMillis() / 1000;

		// What's the search target
		List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
		List<FitnessFunction<TestSuiteChromosome>> fitnessFunctions = new ArrayList<>();

		for (TestFitnessFactory<? extends TestFitnessFunction> f : goalFactories) {
			for (TestFitnessFunction goal : f.getCoverageGoals()) {
				FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
				fitnessFunctions.add(mock);
			}
		}

		algorithm.addFitnessFunctions(fitnessFunctions);

		// if (Properties.SHOW_PROGRESS && !logger.isInfoEnabled())
		algorithm.addListener(progressMonitor); // FIXME progressMonitor may cause
		// client hang if SmartUt is
		// executed with -prefix!
		
//		List<TestFitnessFunction> goals = getGoals(true);
		LoggingUtils.getSmartUtLogger().info("* " + ClientProcess.getPrettyPrintIdentifier() + "Total number of test goals for {}: {}",
				Properties.ALGORITHM.name(), fitnessFunctions.size());
		
//		ga.setChromosomeFactory(getChromosomeFactory(fitnessFunctions.get(0))); // FIXME: just one fitness function?

//		if (Properties.SHOW_PROGRESS && !logger.isInfoEnabled())
//			ga.addListener(progressMonitor); // FIXME progressMonitor may cause

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE) || 
				ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS) || 
				ArrayUtil.contains(Properties.CRITERION, Criterion.STATEMENT) || 
				ArrayUtil.contains(Properties.CRITERION, Criterion.RHO) || 
				ArrayUtil.contains(Properties.CRITERION, Criterion.BRANCH) ||
				ArrayUtil.contains(Properties.CRITERION, Criterion.AMBIGUITY))
			ExecutionTracer.enableTraceCalls();

		algorithm.resetStoppingConditions();
		
		TestSuiteChromosome testSuite = null;

		if (!(Properties.STOP_ZERO && fitnessFunctions.isEmpty()) || ArrayUtil.contains(Properties.CRITERION, Criterion.EXCEPTION)) {
			// Perform search
			LoggingUtils.getSmartUtLogger().info("* " + ClientProcess.getPrettyPrintIdentifier() + "Using seed {}", Randomness.getSeed());
			LoggingUtils.getSmartUtLogger().info("* " + ClientProcess.getPrettyPrintIdentifier() + "Starting evolution");
			ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

			algorithm.generateSolution();

			testSuite = algorithm.getBestIndividual();
			if (testSuite.getTestChromosomes().isEmpty()) {
				LoggingUtils.getSmartUtLogger().warn(ClientProcess.getPrettyPrintIdentifier() + "Could not generate any test case");
			}
		} else {
			zeroFitness.setFinished();
			testSuite = new TestSuiteChromosome();
			for (FitnessFunction<TestSuiteChromosome> ff : testSuite.getFitnessValues().keySet()) {
				testSuite.setCoverage(ff, 1.0);
			}
		}

		long endTime = System.currentTimeMillis() / 1000;

//		goals = getGoals(false); //recalculated now after the search, eg to handle exception fitness
//        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());
        
		// Newline after progress bar
		if (Properties.SHOW_PROGRESS)
			LoggingUtils.getSmartUtLogger().info("");
		
		String text = " statements, best individual has fitness: ";
		LoggingUtils.getSmartUtLogger().info("* " + ClientProcess.getPrettyPrintIdentifier() + "Search finished after "
				+ (endTime - startTime)
				+ "s and "
				+ algorithm.getAge()
				+ " generations, "
				+ MaxStatementsStoppingCondition.getNumExecutedStatements()
				+ text
				+ testSuite.getFitness());
		// Search is finished, send statistics
		sendExecutionStatistics();

		// We send the info about the total number of coverage goals/targets only after 
		// the end of the search. This is because the number of coverage targets may vary
		// when the criterion Properties.Criterion.EXCEPTION is used (exception coverage
		// goal are dynamically added when the generated tests trigger some exceptions
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, algorithm.getFitnessFunctions().size());
		
		return testSuite;
	}
	
}
