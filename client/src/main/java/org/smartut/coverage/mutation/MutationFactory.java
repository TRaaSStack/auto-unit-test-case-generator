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

package org.smartut.coverage.mutation;

import java.util.ArrayList;
import java.util.List;

import org.smartut.Properties;
import org.smartut.TestGenerationContext;
import org.smartut.instrumentation.mutation.InsertUnaryOperator;
import org.smartut.instrumentation.mutation.ReplaceArithmeticOperator;
import org.smartut.instrumentation.mutation.ReplaceConstant;
import org.smartut.instrumentation.mutation.ReplaceVariable;
import org.smartut.rmi.ClientServices;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testsuite.AbstractFitnessFactory;

/**
 * <p>
 * MutationFactory class.
 * </p>
 * 
 * @author fraser
 */
public class MutationFactory extends AbstractFitnessFactory<MutationTestFitness> {

	private boolean strong = true;

	protected List<MutationTestFitness> goals = null;

	/**
	 * <p>
	 * Constructor for MutationFactory.
	 * </p>
	 */
	public MutationFactory() {
	}

	/**
	 * <p>
	 * Constructor for MutationFactory.
	 * </p>
	 * 
	 * @param strongMutation
	 *            a boolean.
	 */
	public MutationFactory(boolean strongMutation) {
		this.strong = strongMutation;
	}

	/* (non-Javadoc)
	 * @see org.smartut.coverage.TestFitnessFactory#getCoverageGoals()
	 */
	/** {@inheritDoc} */
	@Override
	public List<MutationTestFitness> getCoverageGoals() {
		return getCoverageGoals(null);
	}

	/**
	 * <p>
	 * getCoverageGoals
	 * </p>
	 * 
	 * @param targetMethod
	 *            a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	public List<MutationTestFitness> getCoverageGoals(String targetMethod) {
		if (goals != null)
			return goals;

		goals = new ArrayList<>();

		for (Mutation m : getMutantsLimitedPerClass()) {
			if (targetMethod != null && !m.getMethodName().endsWith(targetMethod))
				continue;

			// We need to return all mutants to make coverage values and bitstrings consistent 
			//if (MutationTimeoutStoppingCondition.isDisabled(m))
			//	continue;
			if (strong)
				goals.add(new StrongMutationTestFitness(m));
			else
				goals.add(new WeakMutationTestFitness(m));
		}
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Mutants, goals.size());

		return goals;
	}
	
	/**
	 * Try to remove mutants per mutation operator until the number of mutants
	 * is acceptable wrt the class limit
	 */
	private List<Mutation> getMutantsLimitedPerClass() {
		List<Mutation> mutants = MutationPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getMutants();
		String[] operators = { ReplaceVariable.NAME, InsertUnaryOperator.NAME, ReplaceConstant.NAME, ReplaceArithmeticOperator.NAME };
		if(mutants.size() > Properties.MAX_MUTANTS_PER_CLASS) {
			for(String op : operators) {
				mutants.removeIf(u -> u.getMutationName().startsWith(op));
				if(mutants.size() < Properties.MAX_MUTANTS_PER_CLASS)
					break;
			}
		}
		return mutants;
	}
}
