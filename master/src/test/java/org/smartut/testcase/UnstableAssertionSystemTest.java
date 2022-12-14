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
package org.smartut.testcase;

import java.util.Map;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.statistics.OutputVariable;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.statistics.backend.DebugStatisticsBackend;
//import org.smartut.testsuite.SearchStatistics;
import org.smartut.testsuite.TestSuiteChromosome;
//import org.smartut.utils.ReportGenerator.StatisticEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.staticfield.UnstableAssertion;

public class UnstableAssertionSystemTest extends SystemTestBase {

	//private final boolean DEFAULT_JUNIT_CHECK_ON_SEPARATE_PROCESS = Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS;
	private final boolean DEFAULT_RESET_STATIC_FIELDS = Properties.RESET_STATIC_FIELDS;
	private final Properties.JUnitCheckValues DEFAULT_JUNIT_CHECK = Properties.JUNIT_CHECK;
	private final boolean DEFAULT_JUNIT_TESTS = Properties.JUNIT_TESTS;
	private final boolean DEFAULT_SANDBOX = Properties.SANDBOX;

	@Before
	public void saveProperties() {
		// Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = true;
		Properties.RESET_STATIC_FIELDS = true;
		Properties.JUNIT_CHECK = Properties.JUnitCheckValues.TRUE;
		Properties.JUNIT_TESTS = true;
		Properties.SANDBOX = true;
	}

	@After
	public void restoreProperties() {
		// Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = DEFAULT_JUNIT_CHECK_ON_SEPARATE_PROCESS;
		Properties.RESET_STATIC_FIELDS = DEFAULT_RESET_STATIC_FIELDS;
		Properties.JUNIT_CHECK = DEFAULT_JUNIT_CHECK;
		Properties.JUNIT_TESTS = DEFAULT_JUNIT_TESTS;
		Properties.SANDBOX = DEFAULT_SANDBOX;
	}

	@Test
	public void test() {
		SmartUt smartut = new SmartUt();

		String targetClass = UnstableAssertion.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.OUTPUT_VARIABLES=""+RuntimeVariable.HadUnstableTests;
		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		double best_fitness = best.getFitness();
        Assert.assertEquals("Optimal coverage was not achieved ", 0.0, best_fitness, 0.0);

		Map<String, OutputVariable<?>> map = DebugStatisticsBackend.getLatestWritten();
		Assert.assertNotNull(map);
		OutputVariable<?> unstable = map.get(RuntimeVariable.HadUnstableTests.toString());
		Assert.assertNotNull(unstable);
		Assert.assertEquals(Boolean.FALSE, unstable.getValue());
	}

}
