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

package org.smartut.symbolic.expr.bv;

import java.util.HashSet;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.symbolic.ConstraintTooLongException;
import org.smartut.symbolic.DSEStats;
import org.smartut.symbolic.expr.AbstractExpression;
import org.smartut.symbolic.expr.Expression;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.UnaryExpression;
import org.smartut.symbolic.expr.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * StringUnaryExpression class.
 * </p>
 * 
 * @author krusev
 */
public final class StringUnaryToIntegerExpression extends AbstractExpression<Long>
        implements IntegerValue, UnaryExpression<String> {

	private static final long serialVersionUID = -384874147850376188L;

	protected static final Logger log = LoggerFactory.getLogger(StringUnaryToIntegerExpression.class);

	// protected int conretIntValue;

	private final Operator op;

	private final Expression<String> expr;

	/**
	 * <p>
	 * Constructor for StringUnaryExpression.
	 * </p>
	 * 
	 * @param param
	 *            a {@link org.smartut.symbolic.expr.Expression} object.
	 * @param op2
	 *            a {@link org.smartut.symbolic.expr.Operator} object.
	 * @param con
	 *            a {@link java.lang.String} object.
	 */
	public StringUnaryToIntegerExpression(Expression<String> param, Operator op2, Long con) {
		super(con, 1 + param.getSize(), param.containsSymbolicVariable());
		this.expr = param;
		this.op = op2;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH) {
			DSEStats.getInstance().reportConstraintTooLong(getSize());
			throw new ConstraintTooLongException(getSize());
		}
	}

	/** {@inheritDoc} */
	@Override
	public Operator getOperator() {
		return op;
	}

	/** {@inheritDoc} */
	@Override
	public Expression<String> getOperand() {
		return expr;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return expr + "." + op.toString().trim() + "(" + ")";
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof StringUnaryToIntegerExpression) {
			StringUnaryToIntegerExpression other = (StringUnaryToIntegerExpression) obj;
			return this.op.equals(other.op) && this.expr.equals(other.expr);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.op.hashCode() + this.expr.hashCode();
	}

	@Override
	public Set<Variable<?>> getVariables() {
        Set<Variable<?>> variables = new HashSet<>(this.expr.getVariables());
		return variables;
	}

	@Override
	public Set<Object> getConstants() {
		return this.expr.getConstants();
	}
	
	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}
}
