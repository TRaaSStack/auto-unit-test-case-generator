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
package org.smartut.coverage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.Chromosome;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.ga.metaheuristics.SearchListener;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>FitnessLogger class.</p>
 *
 * @author Gordon Fraser
 */
public class FitnessLogger<T extends Chromosome<T>> implements SearchListener<T> {

	private static final Logger logger = LoggerFactory.getLogger(FitnessLogger.class);

	private static final long serialVersionUID = 1914403470617343821L;

	private final List<Integer> evaluations_history;
	private final List<Long> statements_history;
	private final List<Double> fitness_history;
	private final List<Integer> size_history;
	private String name;
	private int evaluations;

	public FitnessLogger() {
		evaluations_history = new ArrayList<>();
		statements_history = new ArrayList<>();
		fitness_history = new ArrayList<>();
		size_history = new ArrayList<>();
		name = null;
		evaluations = 0;
	}

	public FitnessLogger(FitnessLogger<?> that) {
		this.evaluations_history = new ArrayList<>(that.evaluations_history);
		this.statements_history = new ArrayList<>(that.statements_history);
		this.fitness_history = new ArrayList<>(that.fitness_history);
		this.size_history = new ArrayList<>(that.size_history);
		this.name = that.name;
		this.evaluations = that.evaluations;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.SearchListener#searchStarted(org.smartut.ga.FitnessFunction)
	 */
	/** {@inheritDoc} */
	@Override
	public void searchStarted(GeneticAlgorithm<T> algorithm) {
		evaluations = 0;
		evaluations_history.clear();
		statements_history.clear();
		fitness_history.clear();
		size_history.clear();
		File dir = new File(Properties.REPORT_DIR + "/goals/");
		dir.mkdirs();
		name = Properties.REPORT_DIR
		        + "/goals/"
		        + algorithm.getFitnessFunction().toString().replace(" ", "_").replace(":",
		                                                                              "-").replace("(",
		                                                                                           "").replace(")",
		                                                                                                       "");
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.SearchListener#iteration(java.util.List)
	 */
	/** {@inheritDoc} */
	@Override
	public void iteration(GeneticAlgorithm<T> algorithm) {
		if (algorithm.getPopulation().isEmpty())
			return;

		evaluations_history.add(evaluations);
		statements_history.add(MaxStatementsStoppingCondition.getNumExecutedStatements());
		fitness_history.add(algorithm.getBestIndividual().getFitness());
		size_history.add(algorithm.getBestIndividual().size());
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.SearchListener#searchFinished(java.util.List)
	 */
	/** {@inheritDoc} */
	@Override
	public void searchFinished(GeneticAlgorithm<T> algorithm) {
		if (name == null)
			return;

		File f = new File(name);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write("Iteration,Evaluations,Statements,Fitness,Size\n");
			for (int i = 0; i < fitness_history.size(); i++) {
				out.write(i + ",");
				out.write(evaluations_history.get(i) + ",");
				out.write(statements_history.get(i) + ",");
				out.write(fitness_history.get(i) + ",");
				out.write(size_history.get(i) + "\n");
			}
			out.close();
		} catch (IOException e) {
			logger.error("Could not open csv file: " + e);
		}
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.SearchListener#fitnessEvaluation(org.smartut.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public void fitnessEvaluation(T individual) {
		evaluations++;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.SearchListener#modification(org.smartut.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public void modification(T individual) {
		// TODO Auto-generated method stub

	}

}
