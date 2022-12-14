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
package org.smartut.lm;

import org.smartut.testcase.ValueMinimizer;
import org.smartut.testcase.variable.ConstantValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by mat on 07/04/2014.
 */
public class LanguageModel11EA extends LanguageModelSearch{
    protected static Logger logger = LoggerFactory.getLogger(LanguageModel11EA.class);


    public  LanguageModel11EA(ConstantValue statement, ValueMinimizer.Minimization objective) {
        super(objective, statement);
    }




    @Override
    public String optimise() {
        if(startPoint == null || startPoint.isEmpty()){
            logger.info("Not trying to optimise null or empty string");
            return startPoint;
        }

        resetEvaluationCounter();

        Chromosome best = new Chromosome(startPoint);
        best.setFitness(evaluate(best));
        double originalStringScore = best.getFitness();

        for(int generation = 0; generation < GENERATIONS && !isBudgetExpended() ; generation++){
            double currentStringScore = best.getFitness();

            Chromosome mutant = mutate(best);
            if(!mutant.isEvaluated())
                mutant.setFitness(evaluate(mutant));

            if(mutant.compareTo(best) > 0){
                best = mutant;
            }
        }
        double thisStringScore = best.getFitness();



/*        logger.info(String.format("LanguageModel: Produced a new string '%s' with score %f, old string was '%s' with score %f",
                best.getValue(),
                thisStringScore,
                startPoint,
                originalStringScore));
*/
        return best.getValue();
    }

}
