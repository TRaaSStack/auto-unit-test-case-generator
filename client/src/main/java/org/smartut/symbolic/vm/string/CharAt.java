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
package org.smartut.symbolic.vm.string;

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.bv.StringBinaryToIntegerExpression;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class CharAt extends SymbolicFunction {

	private static final String CHAR_AT = "charAt";

	public CharAt(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_STRING, CHAR_AT,
				Types.INT_TO_CHAR_DESCRIPTOR);
	}

	@Override
	public Object executeFunction() {

		String conc_str = (String) this.getConcReceiver();
		ReferenceConstant symb_str = this.getSymbReceiver();
		StringValue string_expr = env.heap.getField(Types.JAVA_LANG_STRING,
				SymbolicHeap.$STRING_VALUE, conc_str, symb_str, conc_str);

		IntegerValue index_expr = this.getSymbIntegerArgument(0);
		char res = this.getConcCharRetVal();


		if (string_expr.containsSymbolicVariable()
				|| index_expr.containsSymbolicVariable()) {

			StringBinaryToIntegerExpression strBExpr = new StringBinaryToIntegerExpression(
					string_expr, Operator.CHARAT, index_expr, (long) res);

			return strBExpr;

		} else {
			return this.getSymbIntegerRetVal();
		}
	}
}
