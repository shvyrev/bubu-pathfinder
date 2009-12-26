package mt.com.go.decision.engine.condition.iface;

import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;

public interface ICondition {

    public boolean doCondition(DecisionEngineRequest request, DecisionEngineResponse response);

}
