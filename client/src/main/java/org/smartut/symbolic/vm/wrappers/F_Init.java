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

import org.smartut.symbolic.expr.fp.RealValue;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class F_Init extends SymbolicFunction {

	public F_Init(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_FLOAT, Types.INIT, Types.F_TO_VOID);
	}

	@Override
	public Object executeFunction() {

		RealValue fp32 = this.getSymbRealArgument(0);
		ReferenceConstant symb_float = this.getSymbReceiver();
		env.heap.putField(Types.JAVA_LANG_FLOAT, SymbolicHeap.$FLOAT_VALUE,
				null/* conc_float */, symb_float, fp32);
		// return void
		return null;
	}

}
