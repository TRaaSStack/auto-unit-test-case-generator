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
package com.examples.with.different.packagename;

/**
 * @author Jose Miguel Rojas
 */
public class Compositional {
    public void foo(int x) {
        if (x > 0)
            bar(x);
        else
            baz(x);
    }

    public void bar(int x) {
        if (x < 500)
            baz(x * 2);
        else
            baz(x);
    }

    public void baz(int x) {
        if (x > 0 && x < 100)
            throw new IllegalArgumentException();
    }
}
