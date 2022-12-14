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
package org.smartut;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.smartut.Properties.Criterion;
import org.smartut.Properties.StatisticsBackend;
import org.smartut.Properties.StoppingCondition;
import org.smartut.coverage.exception.ExceptionCoverageFactory;
import org.smartut.coverage.line.LineCoverageSuiteFitness;
import org.smartut.ga.archive.Archive;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.result.TestGenerationResult;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.instrumentation.RuntimeInstrumentation;
import org.smartut.runtime.mock.MockFramework;
import org.smartut.statistics.OutputVariable;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.statistics.backend.DebugStatisticsBackend;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.execution.reset.ClassReInitializer;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.Randomness;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import static org.junit.Assert.assertTrue;

/**
 * @author Andrea Arcuri
 * 
 */
public class SystemTestBase {

	public static final String ALREADY_SETUP = "systemtest.alreadysetup";

	private static java.util.Properties currentProperties;

	protected static Criterion[] standardCriteria = Properties.CRITERION;

	static {
		String s = System.getProperty(ALREADY_SETUP);
		if (s == null) {
			System.setProperty(ALREADY_SETUP, ALREADY_SETUP);
			runSetup();
		}
	}

	/**
	 * Needed to keep track of how ofter a test was re-executed.
	 * Note: re-execution only works if in surefire/failsafe we use "rerunFailingTestsCount"
	 */
	private static final Map<String, Integer> executionCounter = new ConcurrentHashMap<>();


	@Rule
	public TestName name = new TestName();


	@Before
	public void checkIfValidName(){
		String name = this.getClass().getName();
		assertTrue("Invalid name for system test: "+name, name.endsWith("SystemTest"));
	}

	@After
	public void resetStaticVariables() {
		RuntimeInstrumentation.setAvoidInstrumentingShadedClasses(false);
		RuntimeSettings.applyUIDTransformation = false;
		TestCaseExecutor.getInstance().newObservers();
		TestGenerationContext.getInstance().resetContext();
		ClassReInitializer.resetSingleton();
		System.setProperties(currentProperties);
		Properties.getInstance().resetToDefaults();
		ExceptionCoverageFactory.getGoals().clear();
		Archive.getArchiveInstance().reset();
	}

	@Before
	public void setDefaultPropertiesForTestCases() {
		
		Properties.getInstance().resetToDefaults();

		Properties.IS_RUNNING_A_SYSTEM_TEST = true;
		RuntimeInstrumentation.setAvoidInstrumentingShadedClasses(true);

		Properties.SHOW_PROGRESS = false;
		Properties.SERIALIZE_RESULT = false;
		Properties.JUNIT_TESTS = false;
		Properties.PLOT = false;
		Properties.CLASS_PREFIX = "";

		Properties.STOPPING_CONDITION = StoppingCondition.MAXSTATEMENTS;
		Properties.SEARCH_BUDGET = 30000;

		Properties.GLOBAL_TIMEOUT = 120;
		Properties.MINIMIZATION_TIMEOUT = 8;
		Properties.EXTRA_TIMEOUT = 2;

		Properties.ENABLE_ASSERTS_FOR_SMARTUT = true;
		Properties.CLIENT_ON_THREAD = true;
		Properties.SANDBOX = false;
		Properties.ERROR_BRANCHES = false;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		Properties.NEW_STATISTICS = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
		TestGenerationContext.getInstance().resetContext();
		ClassReInitializer.resetSingleton();

		//change seed every month
		long seed = new GregorianCalendar().get(Calendar.MONTH);
//		long seed = getSeed();
		Randomness.setSeed(seed);

		currentProperties = (java.util.Properties) System.getProperties().clone();
		
		MockFramework.enable();
	}


	/**
	 * Choose seed based on current month. If a test is re-run, then look at the
	 * next month, and so on in a %12 ring
	 * @return
     */
	private long getSeed(){

		String id = this.getClass().getName() + "#" + name.getMethodName();
		Integer counter = executionCounter.computeIfAbsent(id, c -> 0);

		int month = (counter + new GregorianCalendar().get(Calendar.MONTH)) % 12;

		executionCounter.put(id, counter+1);

		return month;
	}


	protected GeneticAlgorithm<?>  do100percentLineTestOnStandardCriteria(Class<?> target){
		SmartUt smartut = new SmartUt();

		String targetClass = target.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.CRITERION = standardCriteria;


		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		double cov = best.getCoverageInstanceOf(LineCoverageSuiteFitness.class);

		Assert.assertEquals("Non-optimal coverage: ", 1d, cov, 0.001);

		return ga;
	}


	protected GeneticAlgorithm<?>  do100percentLineTest(Class<?> target){
		SmartUt smartut = new SmartUt();

		String targetClass = target.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.LINE};

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);

		return ga;
	}

	protected GeneticAlgorithm<?>  doNonOptimalLineTest(Class<?> target){
		SmartUt smartut = new SmartUt();

		String targetClass = target.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.LINE};

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertNotEquals("Unexpected optimal coverage: ", 1d, best.getCoverage(), 0.001);

		return ga;
	}



	protected OutputVariable getOutputVariable(RuntimeVariable rv){
		if(!Properties.OUTPUT_VARIABLES.contains(rv.toString())){
			throw new IllegalStateException("Properties.OUTPUT_VARIABLES needs to contain "+rv.toString());
		}
		Map<String, OutputVariable<?>> map = DebugStatisticsBackend.getLatestWritten();
		Assert.assertNotNull(map);
		OutputVariable out = map.get(rv.toString());
		return out;
	}


	protected void checkUnstable() throws IllegalStateException{
		OutputVariable unstable = getOutputVariable(RuntimeVariable.HadUnstableTests);
		Assert.assertNotNull(unstable);
		Assert.assertEquals(Boolean.FALSE, unstable.getValue());
	}
	
	/*
	 * this static variable is a safety net to be sure it is called only once. 
	 * static variables are shared and not re-initialized
	 * during a sequence of test cases.
	 */
	private static boolean hasBeenAlreadyRun = false;

	private static void runSetup() {
		if (hasBeenAlreadyRun) {
			return;
		}

		deleteEvoDirs();

		System.out.println("*** SystemTest: runSetup() ***");

		String master = getMasterTestsTarget();
		String runtime = getRuntimeTestsTarget();
		String client = getClientTestsTarget();
		String external = getExternalTarget();

		SmartUt smartut = new SmartUt();
		String[] command = new String[] { "-setup", master,runtime, client, external };

		Object result = smartut.parseCommandLine(command);
		Assert.assertNull(result);
		File evoProp = new File(Properties.OUTPUT_DIR + File.separator
		        + "smartut.properties");
		assertTrue("It was not created: " + evoProp.getAbsolutePath(),
		                  evoProp.exists());

		hasBeenAlreadyRun = true;
	}

    //FIXME: these will change once com.examples goes to its own module

	private static String getExternalTarget(){
		String target = System.getProperty("user.dir") + File.separator + "external";

		checkFile(target);
		return target;
	}

	private static String getMasterTestsTarget() {
		String target = System.getProperty("user.dir") + File.separator + "target"
		        + File.separator + "test-classes";

		checkFile(target);
		return target;
	}

	private static String getRuntimeTestsTarget() {
		String target = 
				System.getProperty("user.dir") + 
				File.separator +".." + 
				File.separator +"runtime" +		
				File.separator + "target"
		        + File.separator + "test-classes";

		checkFile(target);
		return target;
	}

    private static String getClientTestsTarget() {
        String target =
                System.getProperty("user.dir") +
                        File.separator +".." +
                        File.separator +"client" +
                        File.separator + "target"
                        + File.separator + "test-classes";

        checkFile(target);
        return target;
    }

	private static void checkFile(String target) {
		File targetDir = new File(target);
		try {
			assertTrue("Target directory does not exist: "
			                          + targetDir.getCanonicalPath(), targetDir.exists());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		assertTrue(targetDir.isDirectory());
	}

	private static void deleteEvoDirs() {

		System.out.println("*** SystemTest: deleteEvoDirs() ***");

		try {
			org.apache.commons.io.FileUtils.deleteDirectory(new File("smartut-files"));
			org.apache.commons.io.FileUtils.deleteDirectory(new File("smartut-report"));
			org.apache.commons.io.FileUtils.deleteDirectory(new File("smartut-tests"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		hasBeenAlreadyRun = false;
	}
	

	protected GeneticAlgorithm<?> getGAFromResult(Object result) {
		assert(result instanceof List);
		List<List<TestGenerationResult>> results = (List<List<TestGenerationResult>>)result;
		assert(results.size() == 1);
		//return results.iterator().next().getGeneticAlgorithm();
		return results.get(0).get(0).getGeneticAlgorithm();
	}
}
