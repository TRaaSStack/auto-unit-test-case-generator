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
package org.smartut.ga.operators.mutation;

import java.io.Serializable;
import org.smartut.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MutationDistribution implements Serializable {

  private static final long serialVersionUID = -5800252656232641574L;

  /** Constant <code>logger</code> */
  protected static final Logger logger = LoggerFactory.getLogger(MutationDistribution.class);

  protected int sizeOfDistribution;

  /**
   * Check whether a particular chromosome is allowed to be mutated
   * 
   * @param index
   * @return true if mutation is allowed, false otherwise
   */
  public abstract boolean toMutate(int index);

  /**
   * Get mutation distribution defined in
   * {@link org.smartut.Properties.MutationProbabilityDistribution}
   * 
   * @param sizeOfDistribution
   * @return
   */
  public static MutationDistribution getMutationDistribution(int sizeOfDistribution) {
    switch (Properties.MUTATION_PROBABILITY_DISTRIBUTION) {
      case UNIFORM:
      default:
        logger.debug("Using uniform mutation distribution");
        return new UniformMutation(sizeOfDistribution);
      case BINOMIAL:
        logger.debug("Using binomial mutation distribution");
        return new BinomialMutation(sizeOfDistribution);
    }
  }
}
