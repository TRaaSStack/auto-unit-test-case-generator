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


import org.smartut.runtime.mock.MockFramework;
import org.smartut.runtime.mock.java.lang.MockThread;
import org.smartut.runtime.mock.java.util.MockLocale;
import org.smartut.runtime.mock.java.util.MockTimeZone;
import org.smartut.runtime.mock.java.util.prefs.MockPreferences;
import org.smartut.runtime.thread.ThreadCounter;
import org.smartut.runtime.vfs.VirtualFileSystem;
import org.smartut.runtime.vnet.VirtualNetwork;

import javax.swing.*;
import java.util.Locale;

/**
 * <p>
 * Runtime class.
 * </p>
 * 
 * @author Gordon Fraser
 * @author Daniel Muth
 */
public class Runtime {

	private static final Runtime singleton = new Runtime();


	protected Runtime(){		
	}

	public synchronized static Runtime getInstance(){
		return singleton;
	}

	public synchronized static void resetSingleton(){
		singleton.resetRuntime();
	}

	/**
	 * Resets all simulated classes to an initial default state (so that it
	 * seems they have never been used by previous test case executions)
	 * 
	 */
	public void resetRuntime() {

		MockFramework.enable();

		/*
		 * TODO: If the setting of mockJVMNonDeterminism changes
		 *       at runtime, then the MethodCallReplacementCache
		 *       would need to be reset.
		 */
		if (RuntimeSettings.mockJVMNonDeterminism) {			
			Random.reset();
			System.resetRuntime();
            MockThread.reset();
            ThreadCounter.getInstance().resetSingleton();
            MockTimeZone.reset();
            MockLocale.reset();
			MockPreferences.resetPreferences();
			JComponent.setDefaultLocale(Locale.getDefault());
		}

		if (RuntimeSettings.useVFS) {
			VirtualFileSystem.getInstance().resetSingleton();
			VirtualFileSystem.getInstance().init();
		}

        if(RuntimeSettings.useVNET){
            VirtualNetwork.getInstance().reset();
            VirtualNetwork.getInstance().init();
        }

        LoopCounter.getInstance().reset();
	}

}
