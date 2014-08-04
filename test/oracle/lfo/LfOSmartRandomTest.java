package oracle.lfo;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.junit.Assert;

import oracle.Config;
import oracle.SandboxOracle;
import oracle.TraceGenerator;

import org.jLOAF.casebase.CaseBase;
import org.jLOAF.tools.LeaveOneOut;
import org.jLOAF.tools.TestingTrainingPair;
import org.jLOAF.util.CaseLogger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sandbox.Creature;
import sandbox.Direction;
import sandbox.creature.DirtBasedCreature;
import util.expert.ExpertStrategy;
import util.expert.lfo.SmartRandomExpertStrategy;
import agent.AbstractSandboxAgent;
import agent.SandboxAgent;
import agent.lfo.LfOPerception;
import agent.lfo.SmartRandomExpert;

public class LfOSmartRandomTest {

	private SandboxOracle oracle;
	
	private static List<TestingTrainingPair> loo;
	private static int testNo;
	
	@BeforeClass 
	public static void init() throws Exception{		
		Creature c = new DirtBasedCreature(7, 2, Direction.NORTH);
		ExpertStrategy expert = new SmartRandomExpertStrategy();
		TraceGenerator.generateTrace(Config.DEFAULT_ITER, Config.DEFAULT_GRID_SIZE, Config.DEFAULT_LENGTH, Config.DEFAULT_TEST_TRACE_NAME, true, c, expert);
		expert.parseFile(Config.DEFAULT_TEST_TRACE_NAME, Config.DEFAULT_TEST_CASEBASE_NAME);
		
		LeaveOneOut l = LeaveOneOut.loadTrainAndTest(Config.DEFAULT_TEST_CASEBASE_NAME + Config.CASEBASE_EXT, Config.DEFAULT_LENGTH, Config.DEFAULT_NUM_OF_SIMULATIONS);
		loo = l.getTestingAndTrainingSets();
		testNo = 0;
	}

	@AfterClass
	public static void cleanUp() throws Exception{
		File f = new File(Config.DEFAULT_TEST_TRACE_NAME + Config.TRACE_EXT);
		f.delete();
		
		f = new File(Config.DEFAULT_TEST_CASEBASE_NAME + Config.CASEBASE_EXT);
		f.delete();
	}
	
	@Before
	public void setUp() throws Exception {
		Creature creature = new DirtBasedCreature(7, 2, Direction.NORTH);
		AbstractSandboxAgent testAgent = new SmartRandomExpert(Config.DEFAULT_WORLD_SIZE, new DirtBasedCreature(creature));
		
		CaseBase cb = loo.get(testNo).getTraining();
		Assert.assertFalse(cb == null);
		SandboxAgent agent = new SandboxAgent(cb, true, Config.DEFAULT_K);
		
		oracle = new SandboxOracle(Config.DEFAULT_WORLD_SIZE, testAgent, agent, creature, new LfOPerception());
		oracle.setTestData(loo.get(testNo).getTesting());
	}
	
	@Test
	public void testExpert(){
		System.out.println("+++++++++++++++Test Smart Random Simulation+++++++++++++++");
		Random r = new Random();
		oracle.resetOracleStats();
		for (int i = 0; i < Config.DEFAULT_NUM_OF_SIMULATIONS - 1; i++){
			CaseLogger.createLogger(true, "LOG_Random_" + (i + 1) + ".txt");
			oracle.runSimulation(true, Config.DEBUG_PRINT_STATS);
			Creature creature = new DirtBasedCreature(r.nextInt(Config.DEFAULT_WORLD_SIZE - 2) + 1, r.nextInt(Config.DEFAULT_WORLD_SIZE - 2) + 1, Direction.values()[r.nextInt(Direction.values().length)]);
			oracle.setCreature(creature);
			
			CaseBase cb = loo.get(testNo).getTraining();
			SandboxAgent agent = new SandboxAgent(cb, true, Config.DEFAULT_K);
			oracle.setAgent(agent);
			testNo++;
			
			oracle.setTestData(loo.get(testNo).getTesting());
			if (Config.DEBUG_PRINT_STATS){
				System.out.println("-----------------------------------------------");
			}
		}
		oracle.runSimulation(true, Config.DEBUG_PRINT_STATS);
		System.out.println("Average Accuracy : " + oracle.getGlobalAccuracyAvg());
		System.out.println("+++++++++++++++End Test Smart Random Simulation+++++++++++++++\n\n");
	}
}
