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
package org.smartut.runtime.mock.java.time.chrono;

import org.smartut.runtime.mock.StaticReplacementMock;
import org.smartut.runtime.mock.java.time.MockClock;

import java.time.Clock;
import java.time.ZoneId;
import java.time.chrono.ThaiBuddhistDate;
import java.time.temporal.TemporalAccessor;

/**
 * Created by gordon on 24/01/2016.
 */
public class MockThaiBuddhistDate implements StaticReplacementMock {
    @Override
    public String getMockedClassName() {
        return ThaiBuddhistDate.class.getName();
    }

    public static ThaiBuddhistDate now() {
        return now(MockClock.systemDefaultZone());
    }

    public static ThaiBuddhistDate now(ZoneId zone) {
        return now(MockClock.system(zone));
    }

    public static ThaiBuddhistDate now(Clock clock) {
        return ThaiBuddhistDate.now(clock);
    }

    public static ThaiBuddhistDate of(int prolepticYear, int month, int dayOfMonth) {
        return ThaiBuddhistDate.of(prolepticYear, month, dayOfMonth);
    }

    public static ThaiBuddhistDate from(TemporalAccessor temporal) {
        return ThaiBuddhistDate.from(temporal);
    }

}
