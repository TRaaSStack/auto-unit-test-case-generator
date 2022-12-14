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
package org.smartut.symbolic.vm.wrappers;

import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class C_CharValue extends SymbolicFunction {

	private static final String CHAR_VALUE = "charValue";

	public C_CharValue(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_CHARACTER, CHAR_VALUE, Types.TO_CHAR);
	}

	@Override
	public Object executeFunction() {

		ReferenceConstant symb_character = this.getSymbReceiver();
		Character conc_character = (Character) this.getConcReceiver();
		char conc_char_value = this.getConcCharRetVal();

		IntegerValue symb_char_value = env.heap.getField(
				Types.JAVA_LANG_CHARACTER, SymbolicHeap.$CHAR_VALUE,
				conc_character, symb_character, conc_char_value);

		return symb_char_value;
	}

}
