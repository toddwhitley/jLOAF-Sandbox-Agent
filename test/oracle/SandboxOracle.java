package oracle;

import org.jLOAF.Agent;
import org.jLOAF.action.Action;
import org.jLOAF.casebase.Case;
import org.jLOAF.inputs.Input;
import org.jLOAF.performance.ClassificationStatisticsWrapper;
import org.jLOAF.performance.StatisticsBundle;
import org.jLOAF.performance.StatisticsWrapper;
import org.jLOAF.performance.actionestimation.LastActionEstimate;
import agent.SandboxAction;
import agent.SandboxPerception;
import agent.StateBasedAgent;
import sandbox.Creature;
import sandbox.MovementAction;
import sandbox.Sandbox;

public class SandboxOracle {

	private static final int DEFAULT_WORLD_SIZE = 10;
	
	private Sandbox sandbox;
	private int creatureId;
	
	private int iterations;
	
	private StateBasedAgent testAgent;
	
	private Agent agent;
	
	public SandboxOracle(int worldSize, StateBasedAgent testAgent, int iterations, Agent agent, Creature creature){
		if (worldSize == -1){
			worldSize = DEFAULT_WORLD_SIZE;
		}
		this.sandbox = new Sandbox(worldSize);
		this.creatureId = sandbox.addCreature(new Creature(creature));
		
		this.iterations = iterations;
		this.testAgent = testAgent;
		this.agent = agent;
		
		sandbox.init();
	}
	
	public void runSimulation(boolean toLearn, boolean printStats){
		SandboxPerception percept = new SandboxPerception();
		StatisticsWrapper stat = new ClassificationStatisticsWrapper(agent, new LastActionEstimate());
		
		for (int i = 0; i < this.iterations; i++){
			Input in = percept.sense(sandbox.getCreature().get(creatureId));
			MovementAction action = testAgent.testAction(sandbox.getCreature().get(creatureId));
			SandboxAction a = new SandboxAction(action);
			
			Case correctCase = new Case(in, a, null);
			
			Action act = stat.senseEnvironment(correctCase);
			SandboxAction sa = (SandboxAction)act;
			MovementAction move = MovementAction.values()[(int) sa.getFeatures().get(0).getValue()];
			
			//Creature c = sandbox.getCreature().get(creatureId);
			//String data = c.isHasTouched() + "|" + c.getSonar() + "|" + c.getSound();
			//String local = c.getX() + "|" + c.getY() + "|" + c.getDir();
			//System.out.println("Creature : " + data + " Actual Action : " + action + " Agent Action : " + move + " Local : " + local);
			if (toLearn){
				if (!action.equals(move)){
					agent.learn(correctCase);
					move = action;
				}
			}
			sandbox.takeAction(creatureId, move);
		}
		
		if (printStats){
			StatisticsBundle bundle = stat.getStatisticsBundle();
			String[] labels = bundle.getLabels();
			for (int i = 0; i < labels.length; i++){
				System.out.println(labels[i] + " : " + bundle.getAllStatistics()[i]);
				if (labels[i].contains("Recall") || labels[i].contains("Classification Accuracy")){
					System.out.println("");
				}
			}
		}
	}
}