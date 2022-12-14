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
package org.smartut.runtime.mock.javax.swing;

import java.io.File;

import javax.swing.JFileChooser;


import org.smartut.runtime.Runtime;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.mock.java.io.MockFile;
import org.smartut.runtime.mock.javax.swing.MockJFileChooser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MockJFileChooserTest {

	private static final boolean VFS = RuntimeSettings.useVFS;

	@Before
	public void init(){		
		RuntimeSettings.useVFS = true;
		Runtime.getInstance().resetRuntime();		
	}
	
	@After
	public void restoreProperties(){
		RuntimeSettings.useVFS = VFS;
	}
	
	@Test
	public void testGetCurrentDirectory(){
		
		JFileChooser chooser = new MockJFileChooser();
		File dir = chooser.getCurrentDirectory();
		
		Assert.assertTrue(dir.exists());
		Assert.assertTrue(dir instanceof MockFile);
	}

}
