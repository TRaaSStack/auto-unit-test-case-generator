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
package org.smartut.coverage.dataflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smartut.testsuite.AbstractFitnessFactory;

/**
 * <p>
 * AllDefsCoverageFactory class.
 * </p>
 * 
 * @author Andre Mis
 */
public class AllDefsCoverageFactory extends
        AbstractFitnessFactory<AllDefsCoverageTestFitness> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.coverage.TestFitnessFactory#getCoverageGoals()
	 */
	/** {@inheritDoc} */
	@Override
	public List<AllDefsCoverageTestFitness> getCoverageGoals() {

		List<AllDefsCoverageTestFitness> goals = new ArrayList<>();

		Set<Definition> defs = DefUseCoverageFactory.getRegisteredDefinitions();

		for (Definition def : defs) {
			Map<Use, DefUseCoverageTestFitness> uses = DefUseCoverageFactory.getRegisteredGoalsForDefinition(def);

			goals.add(createGoal(def, uses));
		}

		return goals;
	}

	private static AllDefsCoverageTestFitness createGoal(Definition def,
	        Map<Use, DefUseCoverageTestFitness> uses) {

		return new AllDefsCoverageTestFitness(def, uses);
	}

}
