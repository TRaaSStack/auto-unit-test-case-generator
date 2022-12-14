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
package org.smartut.runtime.util;

/**
 * Created by Andrea Arcuri on 09/06/15.
 */
public class Inputs {

    /**
     *  Check for NPE but using IAE with proper error message
     *
     * @param inputs
     * @throws IllegalArgumentException
     */
    public static void checkNull(Object... inputs) throws IllegalArgumentException{
        if(inputs == null){
            throw new IllegalArgumentException("No inputs to check");
        }
        for(int i=0; i<inputs.length; i++){
            Object obj = inputs[i];
            if(obj==null){
                throw new IllegalArgumentException("Null input in position "+i);
            }
        }
    }
}
