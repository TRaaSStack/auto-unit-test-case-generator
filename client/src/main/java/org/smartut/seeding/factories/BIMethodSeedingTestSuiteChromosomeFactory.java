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

package org.smartut.seeding.factories;

import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.testcase.TestCase;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.Randomness;

/**
 * @author Thomas White
 */
public class BIMethodSeedingTestSuiteChromosomeFactory implements
		ChromosomeFactory<TestSuiteChromosome> {

	private static final long serialVersionUID = 1L;

	private final ChromosomeFactory<TestSuiteChromosome> defaultFactory;
	private final TestSuiteChromosome bestIndividual;

	/**
	 * <p>
	 * Constructor for JUnitTestSuiteChromosomeFactory.
	 * </p>
	 * 
	 * @param defaultFactory
	 *            a {@link org.smartut.ga.ChromosomeFactory} object.
	 */
	public BIMethodSeedingTestSuiteChromosomeFactory(
			ChromosomeFactory<TestSuiteChromosome> defaultFactory,
			TestSuiteChromosome bestIndividual) {
		this.defaultFactory = defaultFactory;
		this.bestIndividual = bestIndividual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.ga.ChromosomeFactory#getChromosome()
	 */
	/** {@inheritDoc} */
	@Override
	public TestSuiteChromosome getChromosome() {
		/*
		 * double P_delta = 0.1d; double P_clone = 0.1d; int MAX_CHANGES = 10;
		 */

		TestSuiteChromosome chromosome = defaultFactory.getChromosome();

		int numTests = chromosome.getTests().size();

		// reduce seed probablility by number of tests to be generated
		final double SEED_CHANCE = Properties.SEED_PROBABILITY / numTests;
		for (int i = 0; i < numTests; i++) {
			if (Randomness.nextDouble() < SEED_CHANCE) {
				int testSize = bestIndividual.getTests().size();
				TestCase test = bestIndividual.getTests().get(Randomness.nextInt(testSize));
				if (test != null) {
					List<TestCase> tests = chromosome.getTests();
					tests.remove(i);
					tests.add(i, test);
					chromosome.clearTests();
					for (TestCase t : tests){
						chromosome.addTest(t);
					}
				}
			}
			// chromosome.tests.add(test);
		}

		return chromosome;
	}

}
