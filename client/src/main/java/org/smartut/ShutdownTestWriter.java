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
package org.smartut;

import org.smartut.ga.Chromosome;
import org.smartut.ga.stoppingconditions.StoppingConditionImpl;
import org.smartut.utils.LoggingUtils;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * <p>
 * ShutdownTestWriter class.
 * </p>
 * 
 * @author Gordon Fraser
 */
@SuppressWarnings("restriction")
public class ShutdownTestWriter<T extends Chromosome<T>> extends StoppingConditionImpl<T> implements SignalHandler {

	private static final long serialVersionUID = -5703624299360241009L;

	private static boolean interrupted = false;

	public ShutdownTestWriter() {
		// empty constructor
	}

	public ShutdownTestWriter(ShutdownTestWriter<?> that) {
		// empty copy constructor
	}

	@Override
	public ShutdownTestWriter<T> clone() {
		return new ShutdownTestWriter<>(this);
	}

	/* (non-Javadoc)
	 * @see sun.misc.SignalHandler#handle(sun.misc.Signal)
	 */
	/** {@inheritDoc} */
	@Override
	public void handle(Signal arg0) {
		LoggingUtils.getSmartUtLogger().info("\n* User requested search stop");

		// If this is the second Ctrl+C the user _really_ wants to stop...
		if (interrupted)
			System.exit(0);
		interrupted = true;
	}

	/**
	 * <p>
	 * isInterrupted
	 * </p>
	 * 
	 * @return a boolean.
	 */
	public static boolean isInterrupted() {
		return interrupted;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.stoppingconditions.StoppingCondition#isFinished()
	 */
	/** {@inheritDoc} */
	@Override
	public boolean isFinished() {
		return interrupted;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.stoppingconditions.StoppingCondition#reset()
	 */
	/** {@inheritDoc} */
	@Override
	public void reset() {
		// interrupted = false;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.stoppingconditions.StoppingCondition#setLimit(int)
	 */
	/** {@inheritDoc} */
	@Override
	public void setLimit(long limit) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.stoppingconditions.StoppingCondition#getLimit()
	 */
	/** {@inheritDoc} */
	@Override
	public long getLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.stoppingconditions.StoppingCondition#getCurrentValue()
	 */
	/** {@inheritDoc} */
	@Override
	public long getCurrentValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public void forceCurrentValue(long value) {
		// TODO Auto-generated method stub
		// TODO ?
	}

}
