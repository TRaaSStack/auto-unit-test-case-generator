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
package org.smartut.symbolic.vm.regex;

import java.util.regex.Matcher;

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.ExpressionFactory;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class Matcher_Matches extends SymbolicFunction {

	private static final String MATCHES = "matches";

	public Matcher_Matches(SymbolicEnvironment env) {
		super(env, Types.JAVA_UTIL_REGEX_MATCHER, MATCHES, Types.TO_BOOLEAN);
	}

	@Override
	public Object executeFunction() {
		Matcher conc_matcher = (Matcher) this.getConcReceiver();
		ReferenceConstant symb_matcher = this
				.getSymbReceiver();
		boolean res = this.getConcBooleanRetVal();

		String conc_regex = conc_matcher.pattern().pattern();
		StringValue symb_input = (StringValue) env.heap.getField(
				Types.JAVA_UTIL_REGEX_MATCHER, SymbolicHeap.$MATCHER_INPUT,
				conc_matcher, symb_matcher);

		if (symb_input != null && symb_input.containsSymbolicVariable()) {
			int concrete_value = res ? 1 : 0;
			StringConstant symb_regex = ExpressionFactory
					.buildNewStringConstant(conc_regex);
			StringBinaryComparison strComp = new StringBinaryComparison(symb_regex,
					Operator.PATTERNMATCHES, symb_input, (long) concrete_value);

			return strComp;
		} else {
			return this.getSymbIntegerRetVal();
		}
	}
}
