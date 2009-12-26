package mt.com.go.decision.engine.consequence.iface;

import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;

public interface IConsequence {

    public void doConsequence(DecisionEngineRequest request, DecisionEngineResponse response);

}
