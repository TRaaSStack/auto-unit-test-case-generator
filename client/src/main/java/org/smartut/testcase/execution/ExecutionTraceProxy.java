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

package org.smartut.testcase.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smartut.coverage.dataflow.DefUse;
import org.smartut.setup.CallContext;
import org.smartut.testcase.execution.ExecutionTraceImpl.BranchEval;

/**
 * <p>
 * ExecutionTraceProxy class.
 * </p>
 * 
 * @author gordon
 */
public class ExecutionTraceProxy implements ExecutionTrace, Cloneable {

	private ExecutionTraceImpl trace;

	/**
	 * <p>
	 * Constructor for ExecutionTraceProxy.
	 * </p>
	 */
	public ExecutionTraceProxy() {
		this.trace = new ExecutionTraceImpl();
	}

	/**
	 * <p>
	 * Constructor for ExecutionTraceProxy.
	 * </p>
	 * 
	 * @param trace
	 *            a {@link org.smartut.testcase.execution.ExecutionTraceImpl}
	 *            object.
	 */
	public ExecutionTraceProxy(ExecutionTraceImpl trace) {
		this.trace = trace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#branchPassed(int, int, double,
	 * double)
	 */
	/** {@inheritDoc} */
	@Override
	public void branchPassed(int branch, int bytecode_id, double true_distance, double false_distance) {
		copyOnWrite();
		trace.branchPassed(branch, bytecode_id, true_distance, false_distance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#clear()
	 */
	/** {@inheritDoc} */
	@Override
	public void clear() {
		copyOnWrite();
		trace.clear();
	}

	/**
	 * <p>
	 * copyOnWrite
	 * </p>
	 */
	public void copyOnWrite() {
		if (trace.getProxyCount() > 1) {
			trace.removeProxy();
			trace = trace.clone();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.IExecutionTrace#definitionPassed(java.lang.Object,
	 * int)
	 */
	/** {@inheritDoc} */
	@Override
	public void definitionPassed(Object object, Object caller, int defID) {
		copyOnWrite();
		trace.definitionPassed(object, caller, defID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.IExecutionTrace#enteredMethod(java.lang.String,
	 * java.lang.String, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void enteredMethod(String className, String methodName, Object caller) {
		copyOnWrite();
		trace.enteredMethod(className, methodName, caller);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#exitMethod(java.lang.String,
	 * java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public void exitMethod(String classname, String methodname) {
		copyOnWrite();
		trace.exitMethod(classname, methodname);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#finishCalls()
	 */
	/** {@inheritDoc} */
	@Override
	public void finishCalls() {
		copyOnWrite();
		trace.finishCalls();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getCoverageData()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<String, Map<String, Map<Integer, Integer>>> getCoverageData() {
		return trace.getCoverageData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getCoveredFalseBranches()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<Integer> getCoveredFalseBranches() {
		return trace.getCoveredFalseBranches();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.ExecutionTrace#getCoveredLines(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public Set<Integer> getCoveredLines(String className) {
		return trace.getCoveredLines(className);
	}

	@Override
	public Set<Integer> getCoveredLines() {
		return trace.getCoveredLines();
	}

	@Override
	public Set<Integer> getAllCoveredLines() {
		return trace.getAllCoveredLines();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getCoveredMethods()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<String> getCoveredMethods() {
		return trace.getCoveredMethods();
	}

	@Override
	public Set<String> getCoveredBranchlessMethods() {
		return trace.getCoveredBranchlessMethods();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getCoveredPredicates()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<Integer> getCoveredPredicates() {
		return trace.getCoveredPredicates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getCoveredTrueBranches()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<Integer> getCoveredTrueBranches() {
		return trace.getCoveredTrueBranches();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getCoveredDefinitions()
	 */
	@Override
	public Set<Integer> getCoveredDefinitions() {
		return trace.getCoveredDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getDefinitionExecutionCount()
	 */
	@Override
	public Map<Integer, Integer> getDefinitionExecutionCount() {
		return trace.getDefinitionExecutionCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getDefinitionData()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<String, HashMap<Integer, HashMap<Integer, Integer>>> getDefinitionData() {
		return trace.getDefinitionData();
	}

	@Override
	public Map<String, HashMap<Integer, HashMap<Integer, Object>>> getDefinitionDataObjects() {
		return trace.getDefinitionDataObjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTraceInterface#getExplicitException()
	 */
	/** {@inheritDoc} */
	@Override
	public Throwable getExplicitException() {
		return trace.getExplicitException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getFalseDistance(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double getFalseDistance(int branchId) {
		return trace.getFalseDistance(branchId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getFalseDistances()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Integer, Double> getFalseDistances() {
		return trace.getFalseDistances();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getMethodCalls()
	 */
	/** {@inheritDoc} */
	@Override
	public List<MethodCall> getMethodCalls() {
		return trace.getMethodCalls();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getMethodExecutionCount()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<String, Integer> getMethodExecutionCount() {
		return trace.getMethodExecutionCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getMutationDistance(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double getMutationDistance(int mutationId) {
		return trace.getMutationDistance(mutationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getMutationDistances()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Integer, Double> getMutationDistances() {
		return trace.getMutationDistances();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getPassedDefinitions(java.lang.
	 * String)
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Integer, HashMap<Integer, Integer>> getPassedDefinitions(String variableName) {
		return trace.getPassedDefinitions(variableName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getPassedUses(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Integer, HashMap<Integer, Integer>> getPassedUses(String variableName) {
		return trace.getPassedUses(variableName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getPredicateExecutionCount()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Integer, Integer> getPredicateExecutionCount() {
		return trace.getPredicateExecutionCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getReturnData()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<String, Map<String, Map<Integer, Integer>>> getReturnData() {
		return trace.getReturnData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getTouchedMutants()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<Integer> getTouchedMutants() {
		return trace.getTouchedMutants();
	}

	@Override
	public Set<Integer> getInfectedMutants() {
		return trace.getInfectedMutants();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#getTraceForObject(int)
	 */
	/** {@inheritDoc} */
	@Override
	public ExecutionTrace getTraceForObject(int objectId) {
		return trace.getTraceForObject(objectId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#getTraceInDUCounterRange(org.
	 * smartut.coverage.dataflow.DefUse, boolean, int, int)
	 */
	/** {@inheritDoc} */
	@Override
	public ExecutionTrace getTraceInDUCounterRange(DefUse targetDU, boolean wantToCoverTargetDU, int duCounterStart,
			int duCounterEnd) {
		return trace.getTraceInDUCounterRange(targetDU, wantToCoverTargetDU, duCounterStart, duCounterEnd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getTrueDistance(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double getTrueDistance(int branchId) {
		return trace.getTrueDistance(branchId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getTrueDistances()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Integer, Double> getTrueDistances() {
		return trace.getTrueDistances();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getUseData()
	 */
	/** {@inheritDoc} */
	@Override
	public Map<String, HashMap<Integer, HashMap<Integer, Integer>>> getUseData() {
		return trace.getUseData();
	}

	@Override
	public Map<String, HashMap<Integer, HashMap<Integer, Object>>> getUseDataObjects() {
		return trace.getUseDataObjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#hasFalseDistance(int)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean hasFalseDistance(int predicateId) {
		return trace.hasFalseDistance(predicateId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#hasTrueDistance(int)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean hasTrueDistance(int predicateId) {
		return trace.hasTrueDistance(predicateId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#lazyClone()
	 */
	/** {@inheritDoc} */
	@Override
	public ExecutionTrace lazyClone() {
		ExecutionTraceProxy copy = new ExecutionTraceProxy(trace);
		trace.addProxy();
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#linePassed(java.lang.String,
	 * java.lang.String, int)
	 */
	/** {@inheritDoc} */
	@Override
	public void linePassed(String className, String methodName, int line) {
		copyOnWrite();
		trace.linePassed(className, methodName, line);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#mutationPassed(int, double)
	 */
	/** {@inheritDoc} */
	@Override
	public void mutationPassed(int mutationId, double distance) {
		copyOnWrite();
		trace.mutationPassed(mutationId, distance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#returnValue(java.lang.String,
	 * java.lang.String, int)
	 */
	/** {@inheritDoc} */
	@Override
	public void returnValue(String className, String methodName, int value) {
		copyOnWrite();
		trace.returnValue(className, methodName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.ExecutionTraceInterface#setExplicitException(java.
	 * lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void setExplicitException(Throwable explicitException) {
		copyOnWrite();
		trace.setExplicitException(explicitException);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#toDefUseTraceInformation()
	 */
	/** {@inheritDoc} */
	@Override
	public String toDefUseTraceInformation() {
		return trace.toDefUseTraceInformation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.IExecutionTrace#toDefUseTraceInformation(java.lang.
	 * String)
	 */
	/** {@inheritDoc} */
	@Override
	public String toDefUseTraceInformation(String targetVar) {
		return trace.toDefUseTraceInformation(targetVar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.IExecutionTrace#toDefUseTraceInformation(java.lang.
	 * String, int)
	 */
	/** {@inheritDoc} */
	@Override
	public String toDefUseTraceInformation(String var, int objectId) {
		return trace.toDefUseTraceInformation(var, objectId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.IExecutionTrace#usePassed(java.lang.Object,
	 * int)
	 */
	/** {@inheritDoc} */
	@Override
	public void usePassed(Object object, Object caller, int useID) {
		copyOnWrite();
		trace.usePassed(object, caller, useID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#wasMutationTouched(int)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean wasMutationTouched(int mutationId) {
		return trace.wasMutationTouched(mutationId);
	}

	/** {@inheritDoc} */
	@Override
	public List<BranchEval> getBranchesTrace() {
		return trace.getBranchesTrace();
	}

	/** {@inheritDoc} */
	@Override
	public Map<Integer, Double> getFalseDistancesSum() {
		return trace.getTrueDistancesSum();
	}

	/** {@inheritDoc} */
	@Override
	public Map<Integer, Double> getTrueDistancesSum() {
		return trace.getTrueDistancesSum();
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, HashMap<Integer, HashMap<Integer, Integer>>> getPassedUses() {
		return trace.getPassedUses();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getPassedDefIDs()
	 */
	@Override
	public Set<Integer> getPassedDefIDs() {
		return trace.getPassedDefIDs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getPassedUseIDs()
	 */
	@Override
	public Set<Integer> getPassedUseIDs() {
		return trace.getPassedUseIDs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getTrueDistancesContext()
	 */
	@Override
	public Map<Integer, Map<CallContext, Double>> getTrueDistancesContext() {
		return trace.getTrueDistancesContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getFalseDistancesContext()
	 */
	@Override
	public Map<Integer, Map<CallContext, Double>> getFalseDistancesContext() {
		return trace.getFalseDistancesContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.ExecutionTrace#getPredicateContextExecutionCount()
	 */
	@Override
	public Map<Integer, Map<CallContext, Integer>> getPredicateContextExecutionCount() {
		return trace.getPredicateContextExecutionCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionTrace#getMethodContextCount()
	 */
	@Override
	public Map<String, Map<CallContext, Integer>> getMethodContextCount() {
		return trace.getMethodContextCount();
	}

	@Override
	public void putStaticPassed(String classNameWithDots, String fieldName) {
		trace.putStaticPassed(classNameWithDots, fieldName);
	}

	@Override
	public void getStaticPassed(String classNameWithDots, String fieldName) {
		trace.getStaticPassed(classNameWithDots, fieldName);
	}

	@Override
	public Set<String> getClassesWithStaticWrites() {
		return trace.getClassesWithStaticWrites();
	}

	@Override
	public void classInitialized(String classNameWithDots) {
		trace.classInitialized(classNameWithDots);
	}

	@Override
	public Set<String> getClassesWithStaticReads() {
		return trace.getClassesWithStaticReads();
	}

	@Override
	public List<String> getInitializedClasses() {
		return trace.getInitializedClasses();
	}
}
