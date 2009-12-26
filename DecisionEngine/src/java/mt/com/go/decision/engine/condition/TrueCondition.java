package mt.com.go.decision.engine.condition;

import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;
import mt.com.go.decision.engine.condition.iface.ICondition;

public class TrueCondition implements ICondition {

    public boolean doCondition(DecisionEngineRequest request, DecisionEngineResponse response) {
        return true;
    }

}
