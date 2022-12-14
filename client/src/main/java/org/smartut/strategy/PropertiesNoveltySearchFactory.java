package org.smartut.strategy;

import org.smartut.Properties;
import org.smartut.ShutdownTestWriter;
import org.smartut.TestGenerationContext;
import org.smartut.coverage.branch.BranchPool;
import org.smartut.coverage.mutation.MutationTimeoutStoppingCondition;
import org.smartut.ga.archive.ArchiveTestChromosomeFactory;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.ga.metaheuristics.NoveltySearch;
import org.smartut.ga.operators.crossover.*;
import org.smartut.ga.operators.ranking.FastNonDominatedSorting;
import org.smartut.ga.operators.ranking.RankBasedPreferenceSorting;
import org.smartut.ga.operators.ranking.RankingFunction;
import org.smartut.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.smartut.ga.operators.selection.FitnessProportionateSelection;
import org.smartut.ga.operators.selection.RankSelection;
import org.smartut.ga.operators.selection.SelectionFunction;
import org.smartut.ga.operators.selection.TournamentSelection;
import org.smartut.ga.operators.selection.TournamentSelectionRankAndCrowdingDistanceComparator;
import org.smartut.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.RMIStoppingCondition;
import org.smartut.ga.stoppingconditions.SocketStoppingCondition;
import org.smartut.ga.stoppingconditions.StoppingCondition;
import org.smartut.ga.stoppingconditions.ZeroFitnessStoppingCondition;
import org.smartut.statistics.StatisticsListener;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.factories.AllMethodsTestChromosomeFactory;
import org.smartut.testcase.factories.JUnitTestCarvedChromosomeFactory;
import org.smartut.testcase.factories.RandomLengthTestFactory;
import org.smartut.testcase.secondaryobjectives.TestCaseSecondaryObjective;
import org.smartut.testsuite.RelativeSuiteLengthBloatControl;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.ResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

public class PropertiesNoveltySearchFactory extends PropertiesSearchAlgorithmFactory<TestChromosome> {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesNoveltySearchFactory.class);

    private ChromosomeFactory<TestChromosome> getChromosomeFactory() {
        switch (Properties.TEST_FACTORY) {
            case ALLMETHODS:
                logger.info("Using all methods chromosome factory");
                return new AllMethodsTestChromosomeFactory();
            case RANDOM:
                logger.info("Using random chromosome factory");
                return new RandomLengthTestFactory();
            case ARCHIVE:
                logger.info("Using archive chromosome factory");
                return new ArchiveTestChromosomeFactory();
            case JUNIT:
                logger.info("Using seeding chromosome factory");
                return new JUnitTestCarvedChromosomeFactory(new RandomLengthTestFactory());
            case SERIALIZATION:
                logger.info("Using serialization seeding chromosome factory");
                return new RandomLengthTestFactory();
            default:
                throw new RuntimeException("Unsupported test factory: "
                        + Properties.TEST_FACTORY);
        }
    }

    protected SelectionFunction<TestChromosome> getSelectionFunction() {
        switch (Properties.SELECTION_FUNCTION) {
            case ROULETTEWHEEL:
                return new FitnessProportionateSelection<>();
            case TOURNAMENT:
                return new TournamentSelection<>();
            case BINARY_TOURNAMENT:
                return new BinaryTournamentSelectionCrowdedComparison<>();
            case RANK_CROWD_DISTANCE_TOURNAMENT:
                return new TournamentSelectionRankAndCrowdingDistanceComparator<>();
            default:
                return new RankSelection<>();
        }
    }

    protected CrossOverFunction<TestChromosome> getCrossoverFunction() {
        switch (Properties.CROSSOVER_FUNCTION) {
            case SINGLEPOINTFIXED:
                return new SinglePointFixedCrossOver<>();
            case SINGLEPOINTRELATIVE:
                return new SinglePointRelativeCrossOver<>();
            case SINGLEPOINT:
                return new SinglePointCrossOver<>();
            case COVERAGE:
                throw new RuntimeException(
                            "Coverage crossover not supported in test case mode");
            default:
                throw new RuntimeException("Unknown crossover function: "
                        + Properties.CROSSOVER_FUNCTION);
        }
    }

    private RankingFunction<TestChromosome> getRankingFunction() {
      switch (Properties.RANKING_TYPE) {
        case FAST_NON_DOMINATED_SORTING:
          return new FastNonDominatedSorting<>();
        case PREFERENCE_SORTING:
        default:
          return new RankBasedPreferenceSorting<>();
      }
    }

    @Override
    //public GeneticAlgorithm<TestChromosome> getSearchAlgorithm() {
    public NoveltySearch getSearchAlgorithm() {
        ChromosomeFactory<TestChromosome> factory = getChromosomeFactory();

        NoveltySearch ga = new NoveltySearch(factory);

        if (Properties.NEW_STATISTICS)
            ga.addListener(new StatisticsListener<>());

        // How to select candidates for reproduction
        SelectionFunction<TestChromosome> selectionFunction = getSelectionFunction();
        selectionFunction.setMaximize(false);
        ga.setSelectionFunction(selectionFunction);

        RankingFunction<TestChromosome> ranking_function = getRankingFunction();
        ga.setRankingFunction(ranking_function);

        // When to stop the search
        StoppingCondition<TestChromosome> stopping_condition = getStoppingCondition();
        ga.setStoppingCondition(stopping_condition);
        // ga.addListener(stopping_condition);
        if (Properties.STOP_ZERO) {
            ga.addStoppingCondition(new ZeroFitnessStoppingCondition<>());
        }

        if (!(stopping_condition instanceof MaxTimeStoppingCondition)) {
            ga.addStoppingCondition(new GlobalTimeStoppingCondition<>());
        }

        if (ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.MUTATION)
                || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.STRONGMUTATION)) {
            if (Properties.STRATEGY == Properties.Strategy.ONEBRANCH)
                ga.addStoppingCondition(new MutationTimeoutStoppingCondition<>());
        }
        ga.resetStoppingConditions();
        ga.setPopulationLimit(getPopulationLimit());

        // How to cross over
        CrossOverFunction<TestChromosome> crossover_function = getCrossoverFunction();
        ga.setCrossOverFunction(crossover_function);

        // What to do about bloat
        // MaxLengthBloatControl bloat_control = new MaxLengthBloatControl();
        // ga.setBloatControl(bloat_control);

        if (Properties.CHECK_BEST_LENGTH) {
            RelativeSuiteLengthBloatControl<TestChromosome> bloat_control =
                    new RelativeSuiteLengthBloatControl<>();
            ga.addBloatControl(bloat_control);
            ga.addListener(bloat_control);
        }
        // ga.addBloatControl(new MaxLengthBloatControl());

        TestCaseSecondaryObjective.setSecondaryObjectives();

        // Some statistics
        //if (Properties.STRATEGY == Strategy.SmartUt)
        //	ga.addListener(SearchStatistics.getInstance());
        // ga.addListener(new MemoryMonitor());
        // ga.addListener(MutationStatistics.getInstance());
        // ga.addListener(BestChromosomeTracker.getInstance());

        if (Properties.DYNAMIC_LIMIT) {
            // max_s = GAProperties.generations * getBranches().size();
            // TODO: might want to make this dependent on the selected coverage
            // criterion
            // TODO also, question: is branchMap.size() really intended here?
            // I think BranchPool.getBranchCount() was intended
            Properties.SEARCH_BUDGET = Properties.SEARCH_BUDGET
                    * (BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getNumBranchlessMethods(Properties.TARGET_CLASS) + BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranchCountForClass(Properties.TARGET_CLASS) * 2);
            stopping_condition.setLimit(Properties.SEARCH_BUDGET);
            logger.info("Setting dynamic length limit to " + Properties.SEARCH_BUDGET);
        }

        if (Properties.LOCAL_SEARCH_RESTORE_COVERAGE) {
            // Novelty search does not use local search yet
            // hence we don't need to add the BranchCoverageMap
            // SearchListener here
            // SearchListener<TestChromosome> map = BranchCoverageMap.getInstance();
            // ga.addListener(map);
        }

        if (Properties.SHUTDOWN_HOOK) {
            // ShutdownTestWriter writer = new
            // ShutdownTestWriter(Thread.currentThread());
            ShutdownTestWriter<TestChromosome> writer = new ShutdownTestWriter<>();
            ga.addStoppingCondition(writer);
            RMIStoppingCondition<TestChromosome> rmi = RMIStoppingCondition.getInstance();
            ga.addStoppingCondition(rmi);

            if (Properties.STOPPING_PORT != -1) {
                SocketStoppingCondition<TestChromosome> ss = SocketStoppingCondition.getInstance();
                ss.accept();
                ga.addStoppingCondition(ss);
            }

            // Runtime.getRuntime().addShutdownHook(writer);
            Signal.handle(new Signal("INT"), writer);
        }

        ga.addListener(new ResourceController<>());
        return ga;
    }


}
