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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;

/**
 * Created by gordon on 24/01/2016.
 */
public class MockIsoChronology implements StaticReplacementMock {
    @Override
    public String getMockedClassName() {
        return IsoChronology.class.getName();
    }

    public static LocalDate dateNow(IsoChronology instance) {
        return instance.dateNow(MockClock.systemDefaultZone());
    }

    public static LocalDate dateNow(IsoChronology instance, ZoneId zone) {
        return instance.dateNow(MockClock.system(zone));
    }

}
