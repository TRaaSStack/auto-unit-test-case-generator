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
package org.smartut.seeding;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.TestFactory;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.examples.with.different.packagename.testcarver.ArrayConverterTestCase;
import com.examples.with.different.packagename.testcarver.DifficultClassTest;
import com.examples.with.different.packagename.testcarver.DifficultClassWithoutCarving;
import com.examples.with.different.packagename.testcarver.DifficultClassWithoutCarvingTest;

public class ObjectPoolSystemTest extends SystemTestBase {

	private double P_POOL = Properties.P_OBJECT_POOL;
	private boolean CARVE_POOL = Properties.CARVE_OBJECT_POOL;
	private String SELECTED_JUNIT = Properties.SELECTED_JUNIT;
	private TestFactory FACTORY = Properties.TEST_FACTORY;
	
	@Before
	public void initProperties() {
		Properties.SEARCH_BUDGET = 20000;
	}
	
	@After
	public void restoreProperties() {
		Properties.P_OBJECT_POOL = P_POOL;
		Properties.CARVE_OBJECT_POOL = CARVE_POOL;
		Properties.SELECTED_JUNIT = SELECTED_JUNIT;
		Properties.TEST_FACTORY = FACTORY;
	}
	
	@Ignore
	@Test
	public void testDifficultClassWithoutPoolFails() {
		SmartUt smartut = new SmartUt();

		String targetClass = DifficultClassWithoutCarving.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.0;
				
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		// Passes now....
		Assert.assertTrue("Did not expect optimal coverage: ", best.getCoverage() < 1d);		
	}
	
	@Ignore
	@Test
	public void testDifficultClassWithWrongPoolFails() {
		SmartUt smartut = new SmartUt();

		String targetClass = DifficultClassWithoutCarving.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.8;
		Properties.CARVE_OBJECT_POOL = true;
		Properties.SELECTED_JUNIT = ArrayConverterTestCase.class.getCanonicalName();
				
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertTrue("Did not expect optimal coverage: ", best.getCoverage() < 1d);		
	}
	
	@Test
	public void testDifficultClassWithPoolPasses() {
		SmartUt smartut = new SmartUt();

		String targetClass = DifficultClassWithoutCarving.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.8;
		Properties.CARVE_OBJECT_POOL = true;
		Properties.SELECTED_JUNIT = DifficultClassTest.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Expected optimal coverage: ", 1d, best.getCoverage(), 0.001);		
	}
	
	@Test
	public void testDifficultClassWithMultipleClassPoolPasses() {
		SmartUt smartut = new SmartUt();

		String targetClass = DifficultClassWithoutCarving.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.8;
		Properties.CARVE_OBJECT_POOL = true;
		Properties.SELECTED_JUNIT = DifficultClassWithoutCarvingTest.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Expected optimal coverage: ", 1d, best.getCoverage(), 0.001);		
	}


}
