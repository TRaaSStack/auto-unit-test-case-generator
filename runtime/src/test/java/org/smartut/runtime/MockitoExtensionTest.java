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
package org.smartut.runtime;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MockitoExtensionTest {

    public static class OverrideToString{
        @Override
        public String toString(){
            return "foo";
        }
    }

    @Test
    public void testConfirmDoReturnChain(){
        String a = "a";
        String b = "b";

        OverrideToString obj = mock(OverrideToString.class);
        doReturn(a).doReturn(b).when(obj).toString();

        assertEquals(a, obj.toString());
        assertEquals(b, obj.toString());
        assertEquals(b, obj.toString());
        assertEquals(b, obj.toString());
    }

    @Test
    public void testDoReturnMultiple(){
        String a = "a";
        String b = "b";

        OverrideToString obj = mock(OverrideToString.class);
        MockitoExtension.doReturn(a,b).when(obj).toString();

        assertEquals(a, obj.toString());
        assertEquals(b, obj.toString());
        assertEquals(b, obj.toString());
        assertEquals(b, obj.toString());
    }

    @Test
    public void testDoReturnMultipleWithMockitoAPI(){
        String a = "a";
        String b = "b";

        OverrideToString obj = mock(OverrideToString.class);
        doReturn(a,b).when(obj).toString();

        assertEquals(a, obj.toString());
        assertEquals(b, obj.toString());
        assertEquals(b, obj.toString());
        assertEquals(b, obj.toString());
    }

}