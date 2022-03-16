/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and SmartUt
 * contributors
 *
 * This file is part of SmartUt.
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
package com.examples.with.different.packagename.concolic;


public class TestCase10 {

	/**
	 * @param args
	 */
	public static void test(double double0, double double1, double double3,
			double double4) {

		MathDouble mathDouble0 = new MathDouble();
		double double2 = (double) mathDouble0.castToLong(double0);
		mathDouble0.unreach();
		int int0 = mathDouble0.castToInt(double3);
		char char0 = mathDouble0.castToChar(double3);
		long long0 = mathDouble0.castToLong(double4);
		double double5 = mathDouble0.substract(int0, int0);
		if (double5 == double4) {
			mathDouble0.castToFloat(double5);
		}

	}

}