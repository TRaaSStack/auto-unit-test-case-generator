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
package org.smartut.coverage.mutation;

import java.util.Arrays;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.Properties.Criterion;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.mutation.MutationPropagation;
import com.examples.with.different.packagename.mutation.SimpleMutationExample1;
import com.examples.with.different.packagename.mutation.SimpleMutationExample2;

public class MutationSystemTest extends SystemTestBase {

	private Properties.Criterion[] oldCriteria = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length); 
	private Properties.StoppingCondition oldStoppingCondition = Properties.STOPPING_CONDITION; 
	private double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	
	@Before
	public void beforeTest() {
		oldCriteria = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
		oldStoppingCondition = Properties.STOPPING_CONDITION;
		oldPrimitivePool = Properties.PRIMITIVE_POOL;
		//Properties.MINIMIZE = false;
	}
	
	@After
	public void restoreProperties() {
		Properties.CRITERION = oldCriteria;
		Properties.STOPPING_CONDITION = oldStoppingCondition;
		Properties.PRIMITIVE_POOL = oldPrimitivePool;
	}

	@Test
	public void testWeakMutationSimpleExampleWithArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.WEAKMUTATION };

		String targetClass = SimpleMutationExample1.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(12, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testWeakMutationSimpleExampleWithoutArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.WEAKMUTATION };

		String targetClass = SimpleMutationExample1.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(12, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStrongMutationSimpleExampleWithArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.STRONGMUTATION };

		String targetClass = SimpleMutationExample1.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(12, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStrongMutationSimpleExampleWithoutArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.STRONGMUTATION };

		String targetClass = SimpleMutationExample1.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(12, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testWeakMutationSimpleExampleWithArchive2() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.WEAKMUTATION };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(22, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testWeakMutationSimpleExampleWithoutArchive2() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.WEAKMUTATION };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(22, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStrongMutationSimpleExampleWithArchive2() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.STRONGMUTATION };
        Properties.SEARCH_BUDGET = 50000;

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(22, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStrongMutationSimpleExampleWithoutArchive2() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.STRONGMUTATION };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(22, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testWeakMutationPropagationExampleWithArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.WEAKMUTATION };

		String targetClass = MutationPropagation.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(24, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testWeakMutationPropagationExampleWithoutArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.WEAKMUTATION };

		String targetClass = MutationPropagation.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(24, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStrongMutationPropagationExampleWithArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.STRONGMUTATION };

		String targetClass = MutationPropagation.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(24, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStrongMutationPropagationExampleWithoutArchive() {
		SmartUt smartut = new SmartUt();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.STRONGMUTATION };

		String targetClass = MutationPropagation.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoals());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(24, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
}
