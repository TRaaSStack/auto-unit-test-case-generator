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
package org.smartut.testcase.factories;

import org.smartut.Properties;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedLengthTestChromosomeFactory implements
        ChromosomeFactory<TestChromosome> {

	private static final long serialVersionUID = -3860201346772188495L;

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(FixedLengthTestChromosomeFactory.class);

	/**
	 * Constructor
	 */
	public FixedLengthTestChromosomeFactory() {
	}

	/**
	 * Creates and returns a random test case. Note that the {@code size} argument only serves as a
	 * hint for this method; it will try to generate a test case of <em>at least</em> the given
	 * {@code size}, but it may fail to do so, in which case a test case with fewer statements
	 * is returned.
	 *
	 * @param size the minimum intended size of the test case
	 * @return a random test case
	 */
	private TestCase getRandomTestCase(int size) {
		TestCase test = new DefaultTestCase();
		TestFactory testFactory = TestFactory.getInstance();

		// Then add random stuff
		for (int num = 0; test.size() < size && num < Properties.MAX_ATTEMPTS; num++) {
			testFactory.insertRandomStatement(test, test.size() - 1);
		}

		//logger.debug("Randomized test case:" + test.toCode());

		return test;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Generate a random chromosome
	 */
	@Override
	public TestChromosome getChromosome() {
		TestChromosome c = new TestChromosome();
		c.setTestCase(getRandomTestCase(Properties.CHROMOSOME_LENGTH));
		return c;
	}

}
