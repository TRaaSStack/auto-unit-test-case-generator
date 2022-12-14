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

import org.smartut.classpath.ClassPathHandler;
import org.junit.*;

import org.smartut.Properties;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.mock.MockFramework;

import com.examples.with.different.packagename.mock.java.lang.MemoryCheck;

public class MockRuntimeLoadingTest {

	private static final boolean DEFAULT_JVM = RuntimeSettings.mockJVMNonDeterminism;
	private static final boolean DEFAULT_REPLACE_CALLS = Properties.REPLACE_CALLS;

	@BeforeClass
	public static void init(){
		String cp = System.getProperty("user.dir") + "/target/test-classes";
		ClassPathHandler.getInstance().addElementToTargetProjectClassPath(cp);
	}

	@After
	public void tearDown(){
		RuntimeSettings.mockJVMNonDeterminism = DEFAULT_JVM;
		Properties.REPLACE_CALLS = DEFAULT_REPLACE_CALLS;
	}
	
	@Test
	public void testReplacementMethod() throws Exception{
		RuntimeSettings.mockJVMNonDeterminism  = true;
		Properties.REPLACE_CALLS = true;
		
		InstrumentingClassLoader cl = new InstrumentingClassLoader();
		MockFramework.enable();
		Class<?> clazz = cl.loadClass(MemoryCheck.class.getCanonicalName());
		
		Object mc = clazz.newInstance();
		String expected = "500"; //this is hard coded in the mock
		Assert.assertEquals(expected, mc.toString());
	}
	
}
