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
package org.smartut.papers;

import com.examples.with.different.packagename.papers.pafm.PAFM;
import com.examples.with.different.packagename.papers.pafm.PAFM_old;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.junit.Test;

public class PAFM_SystemTest extends SystemTestBase {

    @Test
    public void testPAFM_old(){
        Properties.SEARCH_BUDGET = 100_000;
        Properties.P_FUNCTIONAL_MOCKING = 0.8;
        Properties.P_REFLECTION_ON_PRIVATE = 0.5;
        // PAFM is activated by time. The test uses statements as budget.
        // Therefore, we activate PAFM from the start.
        Properties.REFLECTION_START_PERCENT = 0.0;
        Properties.FUNCTIONAL_MOCKING_PERCENT = 0.0;
        do100percentLineTest(PAFM_old.class);
    }


    @Test
    public void testPAFM(){
        Properties.SEARCH_BUDGET = 100_000;
        Properties.P_FUNCTIONAL_MOCKING = 0.8;
        Properties.P_REFLECTION_ON_PRIVATE = 0.5;
        // PAFM is activated by time. The test uses statements as budget.
        // Therefore, we activate PAFM from the start.
        Properties.REFLECTION_START_PERCENT = 0.0;
        Properties.FUNCTIONAL_MOCKING_PERCENT = 0.0;
        do100percentLineTest(PAFM.class);
    }
}
