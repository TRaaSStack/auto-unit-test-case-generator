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
package org.smartut.junit.rules;

import java.util.Arrays;

public class InitializeClasses extends BaseRule {

	private String[] classNames;

	public InitializeClasses(String... classesToInitialize) {
		classNames = Arrays.copyOf(classesToInitialize, classesToInitialize.length);
	}

	@Override
	protected void before() {
		org.smartut.runtime.agent.InstrumentingAgent.activate();
		for (final String className : classNames) {
			org.smartut.runtime.Runtime.getInstance().resetRuntime();
			ClassLoader classLoader = getClass().getClassLoader();
			try {
				Class.forName(className, true, classLoader);
			} catch (ExceptionInInitializerError ex) {
				System.err.println("Could not initialize " + className);
			} catch (Throwable t) {
			}
		}
		org.smartut.runtime.agent.InstrumentingAgent.deactivate();
	}

	@Override
	protected void after() {
	}
}
