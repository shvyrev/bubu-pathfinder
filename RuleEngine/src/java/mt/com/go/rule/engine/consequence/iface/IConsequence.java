package mt.com.go.rule.engine.consequence.iface;

import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;

public interface IConsequence {

    public void doConsequence(RuleEngineRequest request, RuleEngineResponse response);

}
