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
package org.smartut.runtime.mock.javax.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.TestGenerationContext;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.coverage.branch.BranchCoverageSuiteFitness;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.util.JOptionPaneInputs;
import org.smartut.runtime.util.JOptionPaneInputs.GUIAction;
import org.smartut.symbolic.TestCaseBuilder;
import org.smartut.testcase.ConstantInliner;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.examples.with.different.packagename.mock.javax.swing.AskUser;

public class MockJOptionPaneTest {

	private static final String TARGET_CLASS = AskUser.class.getCanonicalName();
	private static final boolean DEFAULT_MOCK_GUI = RuntimeSettings.mockGUI;
	private static final boolean DEFAULT_REPLACE_GUI = Properties.REPLACE_GUI;

	@BeforeClass
	public static void init() {
		String cp = System.getProperty("user.dir") + "/target/test-classes";
		ClassPathHandler.getInstance().addElementToTargetProjectClassPath(cp);
	}

	@Before
	public void setUp() {
		Properties.CRITERION = new Properties.Criterion[] { Criterion.BRANCH };
		Properties.TARGET_CLASS = TARGET_CLASS;
		Properties.REPLACE_GUI = true;
		RuntimeSettings.mockGUI = true;
		TestGenerationContext.getInstance().resetContext();
	}

	@After
	public void tearDown() {
		RuntimeSettings.mockGUI = DEFAULT_MOCK_GUI;
		Properties.REPLACE_GUI = DEFAULT_REPLACE_GUI;
		TestGenerationContext.getInstance().resetContext();
	}

	@Test
	public void testNoDialogBeforeExecution() {
		boolean hasStringDialog0 = JOptionPaneInputs.getInstance().hasDialog(GUIAction.STRING_INPUT);
		assertFalse(hasStringDialog0);
	}

	@Test
	public void testShowInputDialogWasFound() throws Exception {

		boolean hasStringDialog0 = JOptionPaneInputs.getInstance().hasDialog(GUIAction.STRING_INPUT);
		assertFalse(hasStringDialog0);

		InstrumentingClassLoader cl = new InstrumentingClassLoader();
		TestCase t1 = buildTestCase0(cl);
		TestCaseExecutor.getInstance().execute(t1);

		boolean hasStringDialog = JOptionPaneInputs.getInstance().hasDialog(GUIAction.STRING_INPUT);
		assertTrue(hasStringDialog);
	}

	@Test
	public void testCoveredGoals() throws Exception {

		Properties.TIMEOUT = Integer.MAX_VALUE;

		InstrumentingClassLoader cl = TestGenerationContext.getInstance().getClassLoaderForSUT();
		TestCase t0 = buildTestCase0(cl);
		TestCase t1 = buildTestCase1(cl);

		TestSuiteChromosome suite = new TestSuiteChromosome();
		suite.addTest(t0);
		suite.addTest(t1);

		BranchCoverageSuiteFitness ff = new BranchCoverageSuiteFitness(cl);
		ff.getFitness(suite);

		Set<TestFitnessFunction> coveredGoals = suite.getCoveredGoals();
		Assert.assertEquals(3, coveredGoals.size());

	}

	private static TestCase buildTestCase0(InstrumentingClassLoader cl)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		TestCaseBuilder builder = new TestCaseBuilder();

		Class<?> clazz = cl.loadClass(TARGET_CLASS);
		Constructor<?> constructor = clazz.getConstructor();
		VariableReference showMessageDialogExample0 = builder.appendConstructor(constructor);

		Method showConfirmDialogsMethod = clazz.getMethod("ask");
		builder.appendMethod(showMessageDialogExample0, showConfirmDialogsMethod);

		return builder.getDefaultTestCase();
	}

	private static TestCase buildTestCase1(InstrumentingClassLoader cl)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		TestCaseBuilder builder = new TestCaseBuilder();

		VariableReference string0 = builder.appendStringPrimitive("input0");

		Class<?> jOptionPaneInputsClass = JOptionPaneInputs.class;
		final String ENQUEUE_INPUT_STRING = "enqueueInputString";
		Method enqueueStringMethod = jOptionPaneInputsClass.getMethod(ENQUEUE_INPUT_STRING, String.class);
		builder.appendMethod(null, enqueueStringMethod, string0);

		Class<?> clazz = cl.loadClass(TARGET_CLASS);
		Constructor<?> constructor = clazz.getConstructor();
		VariableReference showMessageDialogExample0 = builder.appendConstructor(constructor);

		Method showConfirmDialogsMethod = clazz.getMethod("ask");
		builder.appendMethod(showMessageDialogExample0, showConfirmDialogsMethod);

		return builder.getDefaultTestCase();
	}

	@Test
	public void testInlinerBug() throws Exception {

		Properties.TIMEOUT = Integer.MAX_VALUE;

		InstrumentingClassLoader cl = new InstrumentingClassLoader();
		TestCase t0 = buildTestCase0(cl);
		TestCase t1 = buildTestCase1(cl);

		TestSuiteChromosome suite = new TestSuiteChromosome();
		suite.addTest(t0);
		suite.addTest(t1);

		System.out.println(suite.toString());
		
		BranchCoverageSuiteFitness ff = new BranchCoverageSuiteFitness(cl);
		ff.getFitness(suite);

		ConstantInliner inliner = new ConstantInliner();
		inliner.inline(suite);

		System.out.println(suite.toString());

		List<ExecutionResult> execResults = suite.getLastExecutionResults();
		assertEquals(2, execResults.size());
		
		ExecutionResult r1 = execResults.get(0);
		ExecutionResult r2 = execResults.get(1);
		
		r1.calledReflection();
		r2.calledReflection();
	}

}
