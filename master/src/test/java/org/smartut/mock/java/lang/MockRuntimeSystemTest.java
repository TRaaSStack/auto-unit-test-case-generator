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
package org.smartut.mock.java.lang;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.mock.java.lang.HookWithBranch;
import com.examples.with.different.packagename.mock.java.lang.MemorySum;

public class MockRuntimeSystemTest extends SystemTestBase {

	@Test
	public void testMockMemoryCheck(){
		String targetClass = MemorySum.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;		
		Properties.JUNIT_TESTS = true;
		Properties.JUNIT_CHECK = Properties.JUnitCheckValues.TRUE;
		Properties.REPLACE_CALLS = true;
		Properties.OUTPUT_VARIABLES=""+RuntimeVariable.HadUnstableTests;
		
		SmartUt smartut = new SmartUt();
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Assert.assertNotNull(best);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);

		checkUnstable();
	}

	@Test
	public void testCheckShutdownHook(){
		String targetClass = HookWithBranch.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;		
		Properties.JUNIT_TESTS = true;
		Properties.JUNIT_CHECK = Properties.JUnitCheckValues.TRUE;
		Properties.REPLACE_CALLS = true;
		Properties.OUTPUT_VARIABLES=""+RuntimeVariable.HadUnstableTests;
		Properties.MINIMIZE=true;
		Properties.CRITERION = new Properties.Criterion[] { Properties.Criterion.BRANCH };

		SmartUt smartut = new SmartUt();
		String[] command = new String[] { "-generateSuite", "-class", targetClass};
		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Assert.assertNotNull(best);

		Assert.assertTrue("Non-optimal coverage: ", best.getCoverage() >= 0.8);

		checkUnstable();
	}

}
