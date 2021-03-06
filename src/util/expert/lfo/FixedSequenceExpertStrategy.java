package util.expert.lfo;

import util.expert.LfOExpertStrategy;
import sandbox.Direction;
import sandbox.creature.DirtBasedCreature;
import agent.AbstractSandboxAgent;
import agent.lfo.expert.FixedSequenceExpert;



public class FixedSequenceExpertStrategy extends LfOExpertStrategy{
    
    @Override
    public AbstractSandboxAgent getAgent(int size, int x, int y, Direction dir) {
        return new FixedSequenceExpert(new DirtBasedCreature(x, y, dir), null);
    }

    @Override
    public String getAgentName() {
        return FixedSequenceExpert.class.getSimpleName();
    }
    
}
