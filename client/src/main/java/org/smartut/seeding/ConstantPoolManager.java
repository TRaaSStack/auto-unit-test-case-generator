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

package org.smartut.seeding;

import org.smartut.Properties;
import org.smartut.utils.Randomness;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gordon Fraser
 * 
 */
public class ConstantPoolManager {

	private static ConstantPoolManager instance = new ConstantPoolManager();

	private ConstantPool[] pools;
	private double[] probabilities;

	private List<ConstantPool> customizedPools = new LinkedList<>();
	/*
	 * We treat it in a special way, for now, just for making experiments
	 * easier to run
	 */
	private static final int SUT_POOL_INDEX = 0;
	private static final int NON_SUT_POOL_INDEX = 1;
	private static final int DYNAMIC_POOL_INDEX = 2;

	private ConstantPoolManager() {
		init();
	}

	private void init() {
		if(!Properties.VARIABLE_POOL) {
			pools = new ConstantPool[]{new StaticConstantPool(), new StaticConstantPool(),
					new DynamicConstantPool()};
		} else {
			pools = new ConstantPool[]{new StaticConstantVariableProbabilityPool(), new StaticConstantVariableProbabilityPool(),
					new DynamicConstantVariableProbabilityPool()};
		}

		initDefaultProbabilities();
	}

	private void initDefaultProbabilities() {
		probabilities = new double[pools.length];
//		double p = 1d / probabilities.length;
		double p = (1d - Properties.DYNAMIC_POOL) / (probabilities.length - 1);
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] = p;
		}
		probabilities[DYNAMIC_POOL_INDEX] = Properties.DYNAMIC_POOL;
		normalizeProbabilities();
	}

	private void normalizeProbabilities() {
		double sum = 0d;
		for (double p : probabilities) {
			sum += p;
		}
		double delta = 1d / sum;
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] = probabilities[i] * delta;
		}
	}

	public static ConstantPoolManager getInstance() {
		return instance;
	}

	/*
	 * Note: the indexes are hard coded for now. We do it because maybe
	 * in the future we might want to extend this class, so still we need to
	 * use arrays 
	 */

	public void addSUTConstant(Object value) {
		pools[SUT_POOL_INDEX].add(value);
	}

	public void addNonSUTConstant(Object value) {
		pools[NON_SUT_POOL_INDEX].add(value);
	}

	public void addDynamicConstant(Object value) {
		pools[DYNAMIC_POOL_INDEX].add(value);
	}

	public ConstantPool getSUTConstantPool() {
		return pools[SUT_POOL_INDEX];
	}


	public ConstantPool getConstantPool() {
		double p = Randomness.nextDouble();
		double k = 0d;
		for (int i = 0; i < probabilities.length; i++) {
			k += probabilities[i];
			if (p < k) {
				return pools[i];
			}
		}
		/*
		 * This should not happen, but you never know with double computations...
		 */
		return pools[0];
	}
	
	public ConstantPool getDynamicConstantPool() {
		return pools[DYNAMIC_POOL_INDEX];
	}

	public void reset() {
		init();
	}

	public void addCustomizedPool(ConstantPool customizedPool, int index){
		customizedPools.add(index, customizedPool);
	}

	public ConstantPool getCustomizedPool(int index){
		if (customizedPools.size() > index){
			return customizedPools.get(index);
		}
		return null;
	}

}
