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
package com.examples.with.different.packagename.coverage;

public class MethodReturnsPrimitive {

	public boolean testBoolean(int x) {
		return (x > 0);
	}
	
    public int testInt(int x, int y) {
        return x + y;
    }

	public byte testByte(byte x, byte y) {
		return (byte) (x + y);
	}
	
	public long testLong(long x, long y) {
		return x - y;
	}
	
	public char testChar(int x, int y) {
		if (x == y) 
			return 'a';
		else if (x > y)
			return '1';
		else 
			return ' ';
	}
}
