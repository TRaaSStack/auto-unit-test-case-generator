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
package org.smartut.symbolic.vm.math;

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.bv.RealUnaryToIntegerExpression;
import org.smartut.symbolic.expr.fp.RealValue;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicEnvironment;

public abstract class Round {

	private static final String ROUND = "round";

	public static class Round_D extends SymbolicFunction {

		public Round_D(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ROUND, Types.D2L_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			long res = this.getConcLongRetVal();
			RealValue realExpression = this.getSymbRealArgument(0);
			IntegerValue roundExpr;
			if (realExpression.containsSymbolicVariable()) {
				Operator op = Operator.ROUND;
				roundExpr = new RealUnaryToIntegerExpression(realExpression,
						op, res);
			} else {
				roundExpr = this.getSymbIntegerRetVal();
			}
			return roundExpr;
		}

	}

	public static class Round_F extends SymbolicFunction {

		public Round_F(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ROUND, Types.F2I_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			int res = this.getConcIntRetVal();
			RealValue realExpression = this.getSymbRealArgument(0);
			IntegerValue roundExpr;
			if (realExpression.containsSymbolicVariable()) {
				Operator op = Operator.ROUND;
				roundExpr = new RealUnaryToIntegerExpression(realExpression,
						op, (long) res);
			} else {
				roundExpr = this.getSymbIntegerRetVal();
			}
			return roundExpr;
		}

	}

}
