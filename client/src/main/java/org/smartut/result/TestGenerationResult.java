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
package org.smartut.result;

import java.io.Serializable;
import java.util.Set;

import org.smartut.ga.FitnessFunction;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testcase.TestCase;

public interface TestGenerationResult extends Serializable {

	enum Status { SUCCESS, TIMEOUT, ERROR };
	
	/** Did test generation succeed? */
    Status getTestGenerationStatus();
	
	/** If there was an error, this contains the error message */
    String getErrorMessage();
	
	/** The entire GA in its final state */
    GeneticAlgorithm<?> getGeneticAlgorithm();
	
	/** Map from test method to ContractViolation */
    Set<Failure> getContractViolations(String name);
	
	/** Class that was tested */
    String getClassUnderTest();
	
	/** Target coverage criterion used to create this test suite */
    String[] getTargetCriterion();
	
	/** Coverage level of the target criterion */
    double getTargetCoverage(FitnessFunction<?> function);
	
	/** Map from test method to SmartUt test case */
    TestCase getTestCase(String name);

	/** Map from test method to SmartUt test case */
    String getTestCode(String name);
	
	/** JUnit test suite source code */
    String getTestSuiteCode();
	
	/** Lines covered by test */
    Set<Integer> getCoveredLines(String name);
	
	Set<BranchInfo> getCoveredBranches(String name);

	Set<MutationInfo> getCoveredMutants(String name);

	Set<MutationInfo> getExceptionMutants();

	/** Lines covered by final test suite */
    Set<Integer> getCoveredLines();

	/** Branches covered by final test suite */
    Set<BranchInfo> getCoveredBranches();

	/** Mutants detected by final test suite */
    Set<MutationInfo> getCoveredMutants();

	/** Lines not covered by final test suite */
    Set<Integer> getUncoveredLines();

	/** Branches not covered by final test suite */
    Set<BranchInfo> getUncoveredBranches();

	/** Mutants not detected by final test suite */
    Set<MutationInfo> getUncoveredMutants();

	/** Comment for that test */
    String getComment(String name);

}
