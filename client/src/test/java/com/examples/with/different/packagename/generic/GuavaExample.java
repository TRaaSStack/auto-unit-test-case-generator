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
package com.examples.with.different.packagename.generic;

import java.util.HashMap;
import java.util.Map;

public class GuavaExample<R, C, V> {
	
	private R value;
	
	private GuavaExample(R value) {
		this.value = value;
	}
	
	public static <R extends Comparable, C extends Comparable, V> GuavaExample<R, C, V> create(R value) {
		return new GuavaExample<>(value);
	}
	
	public Map<C, V> row(R rowKey) {
	    Map<C,V> map = new HashMap<>();
	    if(rowKey == value)
	    	return map;
	    else
	    	return map;
	}
}
