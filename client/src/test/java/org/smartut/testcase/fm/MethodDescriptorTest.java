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
package org.smartut.testcase.fm;

import org.smartut.utils.generic.GenericClass;
import org.junit.Test;

import java.awt.*;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Created by foo on 20/12/15.
 */
public class MethodDescriptorTest {

    @Test
    public void testMatcher() throws Exception{

        Class<?> klass = Graphics2D.class;
        Method m = klass.getDeclaredMethod("getRenderingHint",RenderingHints.Key.class);

        MethodDescriptor md = new MethodDescriptor(m, new GenericClass(m.getReturnType()));

        String res = md.getInputParameterMatchers();
        assertTrue(res, res.contains("any("));
        assertTrue(res, res.contains("RenderingHints"));
        assertTrue(res, res.contains("Key"));

        assertFalse(res, res.contains("$"));
    }
}