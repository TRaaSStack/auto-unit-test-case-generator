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
package org.smartut.javaagent;

import org.smartut.Properties;
import org.smartut.TestUtil;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.instrumentation.testability.TestabilityTransformationClassLoader;
import org.smartut.testcase.execution.ExecutionTrace;
import org.smartut.testcase.execution.ExecutionTracer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class InstrumentingClassLoaderTest {

	@BeforeClass
	public static void initClass() {
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
	}

	@Test
	public void testClassWithStaticInitializationCallingGetPackage()
	        throws ClassNotFoundException {
		InstrumentingClassLoader instrumentingClassLoader = new InstrumentingClassLoader();
		Class<?> stat = Class.forName("com.examples.with.different.packagename.StatInitIssue",
		                              true, instrumentingClassLoader);
	}

	/*
	 * Tests the child-first/parent-last property of the classloader.
	 */
	@Ignore
	@Test
	public void testDependingInstrumentation() throws Exception {
		Class<?> originalClass = DependentClassLoaderTestSubject.class;
		Properties.TARGET_CLASS = originalClass.getName();
		Properties.PROJECT_PREFIX = originalClass.getPackage().getName();
		Properties.TARGET_CLASS_PREFIX = Properties.PROJECT_PREFIX;
		TestabilityTransformationClassLoader instrumentingClassLoader = new TestabilityTransformationClassLoader();
		Class<?> changedClass = instrumentingClassLoader.loadClass(ClassLoaderTestSubject.class.getName());
		Assert.assertEquals(instrumentingClassLoader, changedClass.getClassLoader());
		Object changed = changedClass.getConstructor().newInstance();
		ExecutionTracer.enable();
		ExecutionTracer.getExecutionTracer().clear();
		TestUtil.invokeMethod(changed, "trySomethingElse");
		ExecutionTrace execTrace = ExecutionTracer.getExecutionTracer().getTrace();
		execTrace = ExecutionTracer.getExecutionTracer().getTrace();
		Assert.assertFalse(execTrace.getTrueDistances().isEmpty());
		Assert.assertFalse(execTrace.getFalseDistances().isEmpty());
		ExecutionTracer.getExecutionTracer().clear();
	}

	@Ignore
	@Test
	public void testDirectInstrumentation() throws Exception {
		Class<?> originalClass = ClassLoaderTestSubject.class;
		Properties.TARGET_CLASS = originalClass.getName();
		Properties.PROJECT_PREFIX = originalClass.getPackage().getName();
		ClassLoaderTestSubject original = new ClassLoaderTestSubject();
		ExecutionTracer.enable();
		ExecutionTracer.getExecutionTracer().clear();
		original.assess(6);
		ExecutionTrace execTrace = ExecutionTracer.getExecutionTracer().getTrace();
		Assert.assertTrue(execTrace.getTrueDistances().isEmpty());
		Assert.assertTrue(execTrace.getFalseDistances().isEmpty());

		TestabilityTransformationClassLoader instrumentingClassLoader = new TestabilityTransformationClassLoader();
		Class<?> changedClass = instrumentingClassLoader.loadClass(ClassLoaderTestSubject.class.getName());
		Assert.assertEquals(instrumentingClassLoader, changedClass.getClassLoader());
		Assert.assertTrue(changedClass.hashCode() != originalClass.hashCode());
        Assert.assertNotEquals(changedClass, originalClass);
		Object changed = changedClass.getConstructor().newInstance();
		try {
			@SuppressWarnings("unused")
			ClassLoaderTestSubject casted = (ClassLoaderTestSubject) changed;
			Assert.fail();
		} catch (ClassCastException exc) {
			// expected
		}
		ExecutionTracer.getExecutionTracer().clear();
		TestUtil.invokeMethod(changed, "assess", 6);
		execTrace = ExecutionTracer.getExecutionTracer().getTrace();
		Assert.assertFalse(execTrace.getTrueDistances().isEmpty());
		Assert.assertFalse(execTrace.getFalseDistances().isEmpty());
		ExecutionTracer.getExecutionTracer().clear();
	}

	@Ignore
	@Test
	public void testInnerClasses() throws Exception {
		Class<? extends InnerClassesTestSubject> originalClass = InnerClassesTestSubject.class;

		Properties.TARGET_CLASS = originalClass.getName();
		Properties.PROJECT_PREFIX = originalClass.getPackage().getName();
		TestabilityTransformationClassLoader instrumentingClassLoader = new TestabilityTransformationClassLoader();

		Class<?> changedClass = instrumentingClassLoader.loadClass(InnerClassesTestSubject.class.getName());

		Assert.assertEquals(instrumentingClassLoader, changedClass.getClassLoader());
		Assert.assertTrue(changedClass.hashCode() != originalClass.hashCode());

		InnerClassesTestSubject original = originalClass.newInstance();
		Assert.assertEquals("abcd", original.toString());

		Object modified = changedClass.newInstance();
		Assert.assertEquals("abcd", modified.toString());
	}
}
