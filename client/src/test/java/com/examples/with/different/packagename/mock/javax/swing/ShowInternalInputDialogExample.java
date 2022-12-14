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
package com.examples.with.different.packagename.mock.javax.swing;

import javax.swing.JOptionPane;

public class ShowInternalInputDialogExample {

	public int showInternalInputDialogs() {
		int count = 0;

		final String string0 = JOptionPane.showInternalInputDialog(null, "message0");
		if (string0 == null) {
			count++;
		}

		String string2 = JOptionPane.showInternalInputDialog(null, "message0", "title0", JOptionPane.ERROR_MESSAGE);
		if (string2 == null) {
			count++;
		}

		Object object0 = JOptionPane.showInternalInputDialog(null, "message0", "title0", JOptionPane.ERROR_MESSAGE,
				null, new Object[] { "val0", "val1" }, "val0");
		if (object0 == null) {
			count++;
		}

		return count;

	}
}
