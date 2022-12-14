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

public final class C_ValueOf extends SymbolicFunction {

	private static final String VALUE_OF = "valueOf";

	public C_ValueOf(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_CHARACTER, VALUE_OF, Types.C_TO_CHARACTER);
	}

	@Override
	public Object executeFunction() {

		IntegerValue int_value = this.getSymbIntegerArgument(0);
		ReferenceConstant symb_character = (ReferenceConstant) this
				.getSymbRetVal();
		Character conc_character = (Character) this.getConcRetVal();

		env.heap.putField(Types.JAVA_LANG_CHARACTER, SymbolicHeap.$CHAR_VALUE,
				conc_character, symb_character, int_value);

		return symb_character;
	}

}
