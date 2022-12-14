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
package org.smartut.graphs.ccg;

import org.smartut.graphs.ccfg.CCFGNode;
public class ClassCallNode extends CCFGNode {

	private String method;
	
	/**
	 * <p>Getter for the field <code>method</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * <p>Constructor for ClassCallNode.</p>
	 *
	 * @param method a {@link java.lang.String} object.
	 */
	public ClassCallNode(String method) {
		this.method = method;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassCallNode other = (ClassCallNode) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return method;
	}

}
