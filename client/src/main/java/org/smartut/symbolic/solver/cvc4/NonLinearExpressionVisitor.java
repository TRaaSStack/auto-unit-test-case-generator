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
package org.smartut.symbolic.solver.cvc4;

import java.util.ArrayList;

import org.smartut.symbolic.expr.Expression;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.bv.IntegerBinaryExpression;
import org.smartut.symbolic.expr.bv.IntegerComparison;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.IntegerUnaryExpression;
import org.smartut.symbolic.expr.bv.IntegerVariable;
import org.smartut.symbolic.expr.bv.RealComparison;
import org.smartut.symbolic.expr.bv.RealToIntegerCast;
import org.smartut.symbolic.expr.bv.RealUnaryToIntegerExpression;
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.bv.StringBinaryToIntegerExpression;
import org.smartut.symbolic.expr.bv.StringMultipleComparison;
import org.smartut.symbolic.expr.bv.StringMultipleToIntegerExpression;
import org.smartut.symbolic.expr.bv.StringToIntegerCast;
import org.smartut.symbolic.expr.bv.StringUnaryToIntegerExpression;
import org.smartut.symbolic.expr.fp.IntegerToRealCast;
import org.smartut.symbolic.expr.fp.RealBinaryExpression;
import org.smartut.symbolic.expr.fp.RealConstant;
import org.smartut.symbolic.expr.fp.RealUnaryExpression;
import org.smartut.symbolic.expr.fp.RealVariable;
import org.smartut.symbolic.expr.reader.StringReaderExpr;
import org.smartut.symbolic.expr.ref.GetFieldExpression;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.ref.ReferenceVariable;
import org.smartut.symbolic.expr.str.IntegerToStringCast;
import org.smartut.symbolic.expr.str.RealToStringCast;
import org.smartut.symbolic.expr.str.StringBinaryExpression;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringMultipleExpression;
import org.smartut.symbolic.expr.str.StringUnaryExpression;
import org.smartut.symbolic.expr.str.StringVariable;
import org.smartut.symbolic.expr.token.HasMoreTokensExpr;
import org.smartut.symbolic.expr.token.NewTokenizerExpr;
import org.smartut.symbolic.expr.token.NextTokenizerExpr;
import org.smartut.symbolic.expr.token.StringNextTokenExpr;

final class NonLinearExpressionVisitor implements ExpressionVisitor<Boolean, Void> {

	@Override
	public Boolean visit(IntegerBinaryExpression n, Void arg) {
		Boolean left = n.getLeftOperand().accept(this, null);
		Boolean right = n.getRightOperand().accept(this, null);
		if (left || right)
			return true;

		switch (n.getOperator()) {
		case MUL:
		case DIV:
		case REM: {
			boolean isLeftSymbolic = n.getLeftOperand().containsSymbolicVariable();
			boolean isRightSymbolic = n.getRightOperand().containsSymbolicVariable();

			return isLeftSymbolic && isRightSymbolic;
		}
		default:
			return false;
		}
	}

	@Override
	public Boolean visit(IntegerComparison n, Void arg) {
		Boolean left_ret_val = n.getLeftOperant().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperant().accept(this, null);
		return right_ret_val;
	}

	@Override
	public Boolean visit(IntegerConstant n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(IntegerUnaryExpression n, Void arg) {
		Boolean ret_val = n.getOperand().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(IntegerVariable n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(RealComparison n, Void arg) {
		Boolean left_ret_val = n.getLeftOperant().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperant().accept(this, null);
		return right_ret_val;
	}

	@Override
	public Boolean visit(RealToIntegerCast n, Void arg) {
		Boolean ret_val = n.getArgument().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(RealUnaryToIntegerExpression n, Void arg) {
		Boolean ret_val = n.getOperand().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(StringBinaryComparison n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		return right_ret_val;
	}

	@Override
	public Boolean visit(StringBinaryToIntegerExpression n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		return right_ret_val;
	}

	@Override
	public Boolean visit(StringMultipleComparison n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		if (right_ret_val) {
			return true;
		}

		ArrayList<Expression<?>> exprs = n.getOther();
		for (Expression<?> expression : exprs) {
			Boolean isNonLinear = expression.accept(this, null);
			if (isNonLinear) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean visit(StringMultipleToIntegerExpression n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		if (right_ret_val) {
			return true;
		}

		ArrayList<Expression<?>> exprs = n.getOther();
		for (Expression<?> expression : exprs) {
			Boolean isNonLinear = expression.accept(this, null);
			if (isNonLinear) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean visit(StringToIntegerCast n, Void arg) {
		Boolean ret_val = n.getArgument().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(StringUnaryToIntegerExpression n, Void arg) {
		Boolean ret_val = n.getOperand().accept(this, null);
		return ret_val;

	}

	@Override
	public Boolean visit(IntegerToRealCast n, Void arg) {
		Boolean ret_val = n.getArgument().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(RealBinaryExpression n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		if (right_ret_val) {
			return true;
		}

		switch (n.getOperator()) {
		case MUL:
		case DIV: {
			boolean isLeftSymbolic = n.getLeftOperand().containsSymbolicVariable();
			boolean isRightSymbolic = n.getRightOperand().containsSymbolicVariable();

			return isLeftSymbolic && isRightSymbolic;
		}
		default:
			return false;
		}
	}

	@Override
	public Boolean visit(RealConstant n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(RealUnaryExpression n, Void arg) {
		Boolean ret_val = n.getOperand().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(RealVariable n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(IntegerToStringCast n, Void arg) {
		Boolean ret_val = n.getArgument().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(RealToStringCast n, Void arg) {
		Boolean ret_val = n.getArgument().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(StringBinaryExpression n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		return right_ret_val;
	}

	@Override
	public Boolean visit(StringConstant n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(StringMultipleExpression n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(this, null);
		if (left_ret_val) {
			return true;
		}
		Boolean right_ret_val = n.getRightOperand().accept(this, null);
		if (right_ret_val) {
			return true;
		}

		ArrayList<Expression<?>> exprs = n.getOther();
		for (Expression<?> expression : exprs) {
			Boolean isNonLinear = expression.accept(this, null);
			if (isNonLinear) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean visit(StringUnaryExpression n, Void arg) {
		Boolean ret_val = n.getOperand().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(StringVariable n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(HasMoreTokensExpr n, Void arg) {
		Boolean ret_val = n.getTokenizerExpr().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(NewTokenizerExpr n, Void arg) {
		Boolean string_ret_val = n.getString().accept(this, null);
		if (string_ret_val) {
			return true;
		}
		Boolean delimVal_ret_val = n.getDelimiter().accept(this, null);
		return delimVal_ret_val;
	}

	@Override
	public Boolean visit(NextTokenizerExpr n, Void arg) {
		Boolean ret_val = n.getTokenizerExpr().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(StringNextTokenExpr n, Void arg) {
		Boolean ret_val = n.getTokenizerExpr().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(StringReaderExpr n, Void arg) {
		Boolean ret_val = n.getString().accept(this, null);
		return ret_val;
	}

	@Override
	public Boolean visit(ReferenceConstant referenceConstant, Void arg) {
		throw new UnsupportedOperationException(
				"Removal of Non-Linear expressions for ReferenceConstant is not yet implemented!");
	}

	@Override
	public Boolean visit(ReferenceVariable r, Void arg) {
		throw new UnsupportedOperationException(
				"Removal of Non-Linear expressions for ReferenceVariable is not yet implemented!");
	}

	@Override
	public Boolean visit(GetFieldExpression r, Void arg) {
		throw new UnsupportedOperationException(
				"Removal of Non-Linear expressions for GetFieldExpression is not yet implemented!");
	}

}
