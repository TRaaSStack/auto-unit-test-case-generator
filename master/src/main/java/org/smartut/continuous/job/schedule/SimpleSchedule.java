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
package org.smartut.continuous.job.schedule;

import java.util.LinkedList;
import java.util.List;

import org.smartut.continuous.job.JobDefinition;
import org.smartut.continuous.job.JobScheduler;
import org.smartut.continuous.project.ProjectStaticData;
import org.smartut.continuous.project.ProjectStaticData.ClassInfo;

public class SimpleSchedule extends OneTimeSchedule{

	public SimpleSchedule(JobScheduler scheduler){
		super(scheduler);
	}

	@Override
	protected List<JobDefinition> createScheduleOnce() {

		assert enoughBudgetForAll(); 

		ProjectStaticData data = scheduler.getProjectData();
		int totalBudgetInSeconds = 60 * scheduler.getConfiguration().timeInMinutes * scheduler.getConfiguration().getNumberOfUsableCores(); 

		List<JobDefinition> jobs = new LinkedList<>();

		//simple case, distribute budget equally
		int cores = scheduler.getConfiguration().getNumberOfUsableCores();
		int cuts = data.getTotalNumberOfTestableCUTs();		
		int slots = (int)Math.round(cores * Math.ceil((double) cuts / (double) cores));
		int budgetInSecondsPerCUT = totalBudgetInSeconds / slots; 

		for(ClassInfo info : data.getClassInfos()){
			if(!info.isTestable()){
				continue;
			}
			JobDefinition job = new JobDefinition(
					budgetInSecondsPerCUT, scheduler.getConfiguration().getConstantMemoryPerJob(), info.getClassName(), 0, null, null);
			jobs.add(job);
		}
		return jobs;
	}

}
