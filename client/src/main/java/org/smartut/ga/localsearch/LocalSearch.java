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
package org.smartut.ga.localsearch;

import org.smartut.ga.Chromosome;

/**
 * This class applies local search on an specific Chromosome.
 * The local search is applied with respect to a given 
 * local search objective for the chromosome type
 * 
 * @author galeotti
 *
 * @param <T> the chromosome type
 */
public interface LocalSearch<T extends Chromosome<T>> {

	boolean doSearch(T individual, LocalSearchObjective<T> objective);
	
}
