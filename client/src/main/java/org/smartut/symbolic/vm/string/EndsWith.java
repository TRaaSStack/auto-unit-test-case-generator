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
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class EndsWith extends SymbolicFunction {

	private static final String ENDS_WITH = "endsWith";

	public EndsWith(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_STRING, ENDS_WITH,
				Types.STR_TO_BOOL_DESCRIPTOR);
	}

	@Override
	public Object executeFunction() {

		String conc_left = (String) this.getConcReceiver();
		ReferenceConstant symb_left = this.getSymbReceiver();

		StringValue left_expr = env.heap.getField(Types.JAVA_LANG_STRING,
				SymbolicHeap.$STRING_VALUE, conc_left, symb_left, conc_left);

		String conc_right = (String) this.getConcArgument(0);
		ReferenceConstant symb_right = (ReferenceConstant) this
				.getSymbArgument(0);

		StringValue right_expr = env.heap.getField(Types.JAVA_LANG_STRING,
				SymbolicHeap.$STRING_VALUE, conc_right, symb_right, conc_right);

		boolean res = this.getConcBooleanRetVal();

		if (left_expr.containsSymbolicVariable()
				|| right_expr.containsSymbolicVariable()) {
			int conV = res ? 1 : 0;
			StringBinaryComparison strBExpr = new StringBinaryComparison(left_expr,
					Operator.ENDSWITH, right_expr, (long) conV);
			return strBExpr;
		} else {
			return this.getSymbIntegerRetVal();
		}
	}
}
