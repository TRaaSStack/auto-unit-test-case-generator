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
package org.smartut.continuous.job;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.smartut.junit.writer.TestSuiteWriter;
import org.smartut.utils.FileIOUtils;
import org.junit.Assert;

import org.smartut.Properties;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.continuous.CtgConfiguration;
import org.smartut.Properties.AvailableSchedule;
import org.smartut.continuous.persistency.StorageManager;
import org.smartut.continuous.persistency.StorageManager.TestsOnDisk;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.continuous.Simple;
import com.examples.with.different.packagename.continuous.Trivial;
import com.examples.with.different.packagename.continuous.UsingSimpleAndTrivial;

import static org.junit.Assert.assertTrue;

public class JobExecutorIntTest {

	private StorageManager storage;

	@Before
	public void init(){
		Properties.CTG_DIR = ".tmp_for_testing_" + JobExecutorIntTest.class.getName();
		if(storage!=null){
			boolean deleted = storage.clean();
			assertTrue(deleted);
		} else {
			storage = new StorageManager();
			storage.clean();
		}
	}

	@Test(timeout = 90_000)
	public void testActualExecutionOfSchedule() throws IOException {

		Properties.TEST_SCAFFOLDING = true;
		
		boolean storageOK = storage.isStorageOk();
		assertTrue(storageOK);
		storageOK = storage.createNewTmpFolders();
		assertTrue(storageOK);
		
		List<TestsOnDisk> data = storage.gatherGeneratedTestsOnDisk();
		Assert.assertEquals(0, data.size());

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
		String classpath =  ClassPathHandler.getInstance().getTargetProjectClasspath();

		int cores = 1;
		int memory = 1000; 
		int minutes = 1;

		CtgConfiguration conf = new CtgConfiguration(memory, cores, minutes, 1, false, AvailableSchedule.SIMPLE);
		JobExecutor exe = new JobExecutor(storage, classpath, conf);

		JobDefinition simple = new JobDefinition(30, memory, 
				com.examples.with.different.packagename.continuous.Simple.class.getName(), 0, null, null);

		JobDefinition trivial = new JobDefinition(30, memory, 
				com.examples.with.different.packagename.continuous.Trivial.class.getName(), 0, null, null);

		assertTrue(simple.jobID < trivial.jobID);

		List<JobDefinition> jobs = Arrays.asList(simple,trivial);

		exe.executeJobs(jobs,cores);

		exe.waitForJobs();

		data = storage.gatherGeneratedTestsOnDisk();
		// if Properties.TEST_SCAFFOLDING is enabled, we should have
		// 4 java files (2 test cases and 2 scaffolding files), however
		// 'storage' just returns the 2 test cases

		boolean areThereFiles = (data.size() == 2);
		boolean areThereTests = true;

		//check if indeed they have tests
		for(TestsOnDisk tod : data){
			String content = FileUtils.readFileToString(tod.testSuite);
			areThereTests = areThereTests && content.contains("@Test") && !content.contains(TestSuiteWriter.NOT_GENERATED_TEST_NAME);
		}

		String msg = "Tmp folder: "+Properties.CTG_DIR+"\n";
		if(!areThereFiles || !areThereTests){
			//build a better error message by looking at the log files
			File logDir = storage.getTmpLogs();
			msg += "Log folder: "+logDir.getAbsolutePath()+"\n";
			List<File> files = FileIOUtils.getRecursivelyAllFilesInAllSubfolders(logDir, ".log");
			msg += "# log files: " + files.size()+"\n";

			for (File log : files) {
				String content = null;
				try {
					content = FileUtils.readFileToString(log);
				} catch (IOException e) {
					msg += "Failed to read file " + log.getName() + " due to: " + e.toString() + "\n";
				}
				if (content != null) {
					msg += "Content for file: " + log.getName() + "\n";
					msg += "--------------------------------------------------" + "\n";
					msg += content;
					msg += "\n" + "--------------------------------------------------" + "\n";
				}
			}
		}

		Assert.assertEquals(msg, 2, data.size());
		Assert.assertTrue(msg, areThereTests);

		boolean deleted = storage.clean();
		Assert.assertTrue(deleted);
	}

	@Test
	public void testEventSequenceWhenWrongSchedule() throws InterruptedException{

		boolean storageOK = storage.isStorageOk();
		assertTrue(storageOK);
		storageOK = storage.createNewTmpFolders();
		assertTrue(storageOK);

		List<TestsOnDisk> data = storage.gatherGeneratedTestsOnDisk();
		Assert.assertEquals(0, data.size());

		// no need to specify it, as com.examples are compiled with SmartUt
		String classpath = System.getProperty("java.class.path"); 

		int cores = 1;
		int memory = 1000; 
		int minutes = 10000;

		CtgConfiguration conf = new CtgConfiguration(memory, cores, minutes, 1, false, AvailableSchedule.SIMPLE);
		final JobExecutor exe = new JobExecutor(storage, classpath, conf);

		JobDefinition simple = new JobDefinition(30, memory, 
				Simple.class.getName(), 0, null, null);

		JobDefinition trivial = new JobDefinition(30, memory, 
				Trivial.class.getName(), 0, null, null);

		JobDefinition ust = new JobDefinition(30, memory, 
				UsingSimpleAndTrivial.class.getName(), 0, 
				new HashSet<>(Arrays.asList(new String[]{Simple.class.getName(),Trivial.class.getName()})),
				null);


		/*
		 * ust is in the middle, instead of last element.
		 * still, even if the schedule is wrong, the executor should be able
		 * to properly handle it 
		 */		
		final List<JobDefinition> jobs = Arrays.asList(simple,ust,trivial);

		exe.initExecution(jobs);
		
		Thread t = new Thread(){
			@Override
			public void run(){
				exe.execute(jobs);
			}
		};
		try{
			t.start();

			exe.doneWithJob(exe.pollJob());
			exe.doneWithJob(exe.pollJob());

			JobDefinition last = exe.pollJob();
			exe.doneWithJob(last);
			
			Assert.assertEquals(ust.cut,last.cut);
		}
		finally{
			t.interrupt();
		}

		storage.clean();
	}
}
