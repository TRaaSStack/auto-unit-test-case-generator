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

package org.smartut.contracts;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.statements.FieldStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on ObjectContract / Randoop
 * 
 * @author Gordon Fraser
 */
public abstract class Contract {

	protected static final Logger logger = LoggerFactory.getLogger(Contract.class);

	protected static class Pair<T> {
		T object1;
		T object2;

		public Pair(T o1, T o2) {
			object1 = o1;
			object2 = o2;
		}
	}

	/**
	 * <p>
	 * getAllObjects
	 * </p>
	 * 
	 * @param scope
	 *            a {@link org.smartut.testcase.execution.Scope} object.
	 * @return a {@link java.util.Collection} object.
	 */
	protected Collection<Object> getAllObjects(Scope scope) {
		// TODO: Assignable classes and subclasses?
		final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
		return scope.getObjects(targetClass);
	}

	protected Collection<VariableReference> getAllVariables(Scope scope) {
		final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
		return scope.getElements(targetClass);
	}

	/**
	 * <p>
	 * getAllObjectPairs
	 * </p>
	 * 
	 * @param scope
	 *            a {@link org.smartut.testcase.execution.Scope} object.
	 * @return a {@link java.util.Collection} object.
	 */
	protected Collection<Pair<Object>> getAllObjectPairs(Scope scope) {
		Set<Pair<Object>> pairs = new HashSet<>();
		Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
		for (Object o1 : scope.getObjects(targetClass)) {
			for (Object o2 : scope.getObjects(o1.getClass())) {
				pairs.add(new Pair<>(o1, o2));
			}
		}
		return pairs;
	}

	protected Collection<Pair<VariableReference>> getAllVariablePairs(Scope scope) {
		Set<Pair<VariableReference>> pairs = new HashSet<>();
		final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
		List<VariableReference> objects = scope.getElements(targetClass);
		for (int i = 0; i < objects.size(); i++) {
			for (int j = i; j < objects.size(); j++) {
				pairs.add(new Pair<>(objects.get(i), objects.get(j)));
			}
		}
		//		for (VariableReference o1 : scope.getElements(Properties.getTargetClass())) {
		//			for (VariableReference o2 : scope.getElements(o1.getVariableClass())) {
		//				pairs.add(new Pair<VariableReference>(o1, o2));
		//			}
		//		}
		return pairs;
	}

	/**
	 * Check if this statement is related to the unit under test
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.Statement} object.
	 * @return a boolean.
	 */
	protected boolean isTargetStatement(Statement statement) {
		//if (statement.getReturnClass().equals(Properties.getTargetClass()))
		//	return true;
		if (statement instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) statement;
			final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
			if (targetClass.equals(ms.getMethod().getDeclaringClass()))
				return true;
		} else if (statement instanceof ConstructorStatement) {
			ConstructorStatement cs = (ConstructorStatement) statement;
			final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
			if (targetClass.equals(cs.getConstructor().getDeclaringClass()))
				return true;
		} else if (statement instanceof FieldStatement) {
			FieldStatement fs = (FieldStatement) statement;
			final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
			if (targetClass.equals(fs.getField().getDeclaringClass()))
				return true;
		}

		return false;
	}

	/**
	 * Run the test against this contract and determine whether it reports a
	 * failure
	 * 
	 * @param test
	 *            a {@link org.smartut.testcase.TestCase} object.
	 * @return a boolean.
	 */
	public boolean fails(TestCase test) {
		ContractChecker.setActive(false);
		TestCaseExecutor executor = TestCaseExecutor.getInstance();
		SingleContractChecker checker = new SingleContractChecker(this);
		executor.addObserver(checker);
		TestCaseExecutor.runTest(test);
		executor.removeObserver(checker);
		//ContractChecker.setActive(true);
		return !checker.isValid();
	}

	/**
	 * Check the contract on the current statement in the current scope
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.Statement} object.
	 * @param scope
	 *            a {@link org.smartut.testcase.execution.Scope} object.
	 * @param exception
	 *            a {@link java.lang.Throwable} object.
	 * @return a boolean.
	 */
	public abstract ContractViolation check(Statement statement, Scope scope,
	        Throwable exception);

	/**
	 * Add an assertion to the statement based on the contract. The assertion
	 * should fail on a contract violation, and pass if the contract is
	 * satisfied.
	 * 
	 * @param statement
	 * @param variables
	 * @param exception
	 */
	public abstract void addAssertionAndComments(Statement statement,
	        List<VariableReference> variables, Throwable exception);

	public void changeClassLoader(ClassLoader classLoader) {
		// No-op by default
	}
}
