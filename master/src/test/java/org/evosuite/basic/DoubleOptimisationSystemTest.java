/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.basic;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.DoubleExample;
import com.examples.with.different.packagename.DoubleExample2;

public class DoubleOptimisationSystemTest extends SystemTestBase {
	
	private double seedConstants = Properties.PRIMITIVE_POOL;
	
	@After
	public void resetSeedConstants() {
		Properties.PRIMITIVE_POOL = seedConstants;
	}
	
	@Test
	public void testDoubleSUT() {
		EvoSuite evosuite = new EvoSuite();

		String targetClass = DoubleExample.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.PRIMITIVE_POOL = 0.0;
		Properties.SEARCH_BUDGET = 50000;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = evosuite.parseCommandLine(command);

		Assert.assertTrue(result != null);

		GeneticAlgorithm<?, DEFAULT_VALUE_XXX> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testDoubleSUTExact() {
		EvoSuite evosuite = new EvoSuite();

		String targetClass = DoubleExample2.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		//Properties.PRIMITIVE_POOL = 0.0;
		// TODO: Optimising exact doubles without seeding takes _long_
		//Properties.SEARCH_BUDGET = 30000;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = evosuite.parseCommandLine(command);

		Assert.assertTrue(result != null);

		GeneticAlgorithm<?, DEFAULT_VALUE_XXX> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
}
