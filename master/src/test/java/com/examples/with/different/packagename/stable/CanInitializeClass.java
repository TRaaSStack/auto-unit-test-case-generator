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
package com.examples.with.different.packagename.stable;

public class CanInitializeClass {

	private static int counter;
	static {
		counter=100500+10;
	}
	private final int value;
	public CanInitializeClass() {
		value = counter;
	}
	public boolean isValue(int value) {
		if (value==this.value) {
			return true;
		} else {
			return false;
		}
	}
	public static int useNotInitializedClass() {
		int value = CannotInitializeClass.init();
		return value;
	}
}
